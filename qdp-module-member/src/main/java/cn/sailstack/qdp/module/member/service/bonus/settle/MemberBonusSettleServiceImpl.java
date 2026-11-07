package cn.sailstack.qdp.module.member.service.bonus.settle;

import cn.sailstack.qdp.framework.common.enums.UserTypeEnum;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.util.json.JsonUtils;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle.MemberBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.*;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberBonusRecordMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberBonusSettleBatchMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberBonusSettleDetailMapper;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusPayStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusSettleStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusTypeEnum;
import cn.sailstack.qdp.module.member.service.bonus.calculate.DirectorGiftBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.calculate.HelpBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import cn.sailstack.qdp.module.member.service.bonus.director.MemberDirectorService;
import cn.sailstack.qdp.module.pay.api.wallet.PayWalletApi;
import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import cn.sailstack.qdp.module.pay.enums.wallet.PayWalletBizTypeEnum;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.*;

/**
 * 会员礼包奖金结算 Service 实现类
 *
 * 结算批次与规则快照：每次结算保存当时配置快照（JSON），已结算明细不覆盖；
 * 更正通过关联冲正/调整流水完成。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class MemberBonusSettleServiceImpl implements MemberBonusSettleService {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");
    /**
     * 礼包奖金体系标识（与商品奖金 PRODUCT 区分）
     */
    public static final String SCOPE_GIFT = "GIFT";

    @Resource
    private MemberBonusConfigService bonusConfigService;
    @Resource
    private MemberDirectorService directorService;
    @Resource
    private HelpBonusCalculator helpBonusCalculator;
    @Resource
    private DirectorGiftBonusCalculator directorGiftBonusCalculator;
    @Resource
    private MemberBonusRecordMapper bonusRecordMapper;
    @Resource
    private MemberBonusSettleBatchMapper settleBatchMapper;
    @Resource
    private MemberBonusSettleDetailMapper settleDetailMapper;
    @Resource
    private PayWalletApi payWalletApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberBonusSettleBatchDO settleMonth(String period, Integer dividendPoolFen) {
        // 1. 参数与批次查重（uk period+scope 兜底并发）
        Preconditions.checkArgument(period != null && PERIOD_PATTERN.matcher(period).matches(), "账期格式必须为 yyyy-MM");
        if (settleBatchMapper.selectByPeriodAndScope(period, SCOPE_GIFT) != null) {
            throw exception(BONUS_SETTLE_BATCH_EXISTS, period);
        }
        MemberBonusConfigDO config = bonusConfigService.getRequiredConfig();
        List<String> remarks = new ArrayList<>();
        // 2. 董事资格核验（月结前统一核验，未取得等级不参与本次分红；第一版不自动降级）
        directorService.verifyAllDirectors();
        // 3. 帮扶奖：先完成直推/增量月度汇总，再计算帮扶奖（禁止递归）
        helpBonusCalculator.calculate(config, period).forEach(this::insertRecordIfAbsent);
        // 3. 礼包董事分红：收入口径【待业务确认】——DISABLED 不结算；MANUAL 必须录入分红池
        Integer pool = null;
        if ("MANUAL".equals(config.getGiftIncomeSource())) {
            Preconditions.checkArgument(dividendPoolFen != null && dividendPoolFen >= 0,
                    "收入口径为 MANUAL 时必须录入董事分红池金额");
            pool = dividendPoolFen;
        } else {
            remarks.add("礼包董事分红池收入口径未配置(" + config.getGiftIncomeSource() + ")，本账期跳过董事分红");
        }
        DirectorGiftBonusCalculator.CalculateResultBO directorResult =
                directorGiftBonusCalculator.calculate(config, period, pool);
        directorResult.getRecords().forEach(this::insertRecordIfAbsent);
        remarks.addAll(directorResult.getEmptyLevelRemarks());
        // 4. 汇总该账期待结算明细 → 生成个人结算明细（负数合计保留待财务处理，不自动抵扣钱包）
        String batchNo = "GB" + period.replace("-", "");
        List<MemberBonusRecordDO> waitRecords = bonusRecordMapper.selectListByPeriodAndTypes(period,
                List.of(MemberBonusTypeEnum.DIRECT.getType(), MemberBonusTypeEnum.INCREMENT.getType(),
                        MemberBonusTypeEnum.HELP.getType(), MemberBonusTypeEnum.DIRECTOR.getType())).stream()
                .filter(r -> MemberBonusStatusEnum.WAIT_SETTLE.getStatus().equals(r.getStatus()))
                .toList();
        Map<Long, Integer> totalByUser = waitRecords.stream()
                .collect(Collectors.groupingBy(MemberBonusRecordDO::getUserId,
                        Collectors.summingInt(MemberBonusRecordDO::getAmount)));
        totalByUser.forEach((userId, total) -> settleDetailMapper.insert(MemberBonusSettleDetailDO.builder()
                .batchNo(batchNo).userId(userId).totalAmount(total)
                .payStatus(MemberBonusPayStatusEnum.WAIT_PAY.getStatus()).build()));
        // 5. 明细置已结算并挂批次号；批次落库（含规则快照）
        for (MemberBonusRecordDO record : waitRecords) {
            bonusRecordMapper.updateById(new MemberBonusRecordDO().setId(record.getId())
                    .setStatus(MemberBonusStatusEnum.SETTLED.getStatus()).setSettleBatchNo(batchNo));
        }
        int totalAmount = totalByUser.values().stream().filter(v -> v > 0).mapToInt(Integer::intValue).sum();
        MemberBonusSettleBatchDO batch = MemberBonusSettleBatchDO.builder()
                .batchNo(batchNo).period(period).scope(SCOPE_GIFT)
                .status(MemberBonusSettleStatusEnum.SETTLED.getStatus())
                .dividendPool(pool != null ? pool : 0)
                .totalAmount(totalAmount).userCount(totalByUser.size())
                .configSnapshot(JsonUtils.toJsonString(config))
                .remark(String.join("；", remarks))
                .build();
        settleBatchMapper.insert(batch);
        log.info("[settleMonth][账期({}) 礼包奖金结算完成：批次({}) 人数({}) 总额({}分) 备注({})]",
                period, batchNo, totalByUser.size(), totalAmount, batch.getRemark());
        return batch;
    }

    @Override
    public void payBatch(String batchNo) {
        MemberBonusSettleBatchDO batch = getBatchByBatchNo(batchNo);
        if (MemberBonusSettleStatusEnum.PAID.getStatus().equals(batch.getStatus())) {
            log.info("[payBatch][批次({}) 已发放，幂等返回]", batchNo);
            return;
        }
        if (!MemberBonusSettleStatusEnum.SETTLED.getStatus().equals(batch.getStatus())) {
            throw exception(BONUS_SETTLE_BATCH_STATUS_ERROR, batchNo);
        }
        List<MemberBonusSettleDetailDO> details = settleDetailMapper.selectListByBatchNo(batchNo);
        for (MemberBonusSettleDetailDO detail : details) {
            if (!MemberBonusPayStatusEnum.WAIT_PAY.getStatus().equals(detail.getPayStatus())) {
                continue; // 已发放/失败重试由人工介入
            }
            // 负数（退款冲抵差额）与零金额不发放、不扣减，保留财务线下处理
            if (detail.getTotalAmount() <= 0) {
                settleDetailMapper.updateById(new MemberBonusSettleDetailDO().setId(detail.getId())
                        .setPayStatus(MemberBonusPayStatusEnum.PAY_FAIL.getStatus())
                        .setWalletBizId(buildWalletBizId(batchNo, detail.getUserId()))
                        .setPayTime(LocalDateTime.now()));
                log.warn("[payBatch][批次({}) 会员({}) 合计({}分)非正数，跳过发放待财务处理]",
                        batchNo, detail.getUserId(), detail.getTotalAmount());
                continue;
            }
            try {
                String walletBizId = buildWalletBizId(batchNo, detail.getUserId());
                payWalletApi.addWalletBalance(new PayWalletAddBalanceReqDTO()
                        .setUserId(detail.getUserId())
                        .setUserType(UserTypeEnum.MEMBER.getValue())
                        .setBizType(PayWalletBizTypeEnum.BONUS.getType())
                        .setBizId(walletBizId)
                        .setPrice(detail.getTotalAmount()));
                settleDetailMapper.updateById(new MemberBonusSettleDetailDO().setId(detail.getId())
                        .setPayStatus(MemberBonusPayStatusEnum.PAID.getStatus())
                        .setWalletBizId(walletBizId).setPayTime(LocalDateTime.now()));
            } catch (Exception ex) {
                log.error("[payBatch][批次({}) 会员({}) 发放失败]", batchNo, detail.getUserId(), ex);
                settleDetailMapper.updateById(new MemberBonusSettleDetailDO().setId(detail.getId())
                        .setPayStatus(MemberBonusPayStatusEnum.PAY_FAIL.getStatus())
                        .setWalletBizId(buildWalletBizId(batchNo, detail.getUserId())));
            }
        }
        // 批次置已发放（个别失败明细由管理端重试/人工处理）
        settleBatchMapper.updateById(new MemberBonusSettleBatchDO().setId(batch.getId())
                .setStatus(MemberBonusSettleStatusEnum.PAID.getStatus()));
        log.info("[payBatch][批次({}) 发放流程执行完成]", batchNo);
    }

    @Override
    public PageResult<MemberBonusSettleBatchDO> getBatchPage(MemberBonusSettleBatchPageReqVO pageReqVO) {
        return settleBatchMapper.selectPage(pageReqVO);
    }

    @Override
    public MemberBonusSettleBatchDO getBatchByBatchNo(String batchNo) {
        MemberBonusSettleBatchDO batch = settleBatchMapper.selectByBatchNo(batchNo);
        if (batch == null) {
            throw exception(BONUS_SETTLE_BATCH_NOT_EXISTS);
        }
        return batch;
    }

    @Override
    public List<MemberBonusSettleDetailDO> getDetailListByBatchNo(String batchNo) {
        return settleDetailMapper.selectListByBatchNo(batchNo);
    }

    private void insertRecordIfAbsent(MemberBonusRecordDO record) {
        if (bonusRecordMapper.selectByDedupKey(record.getDedupKey()) != null) {
            return; // 补跑/重试幂等
        }
        bonusRecordMapper.insert(record);
    }

    private String buildWalletBizId(String batchNo, Long userId) {
        return batchNo + ":" + userId;
    }

}

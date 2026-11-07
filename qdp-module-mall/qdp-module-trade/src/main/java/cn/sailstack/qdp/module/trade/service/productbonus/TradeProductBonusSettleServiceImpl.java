package cn.sailstack.qdp.module.trade.service.productbonus;

import cn.sailstack.qdp.framework.common.enums.UserTypeEnum;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.util.json.JsonUtils;
import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberBonusConfigRespDTO;
import cn.sailstack.qdp.module.pay.api.wallet.PayWalletApi;
import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import cn.sailstack.qdp.module.pay.enums.wallet.PayWalletBizTypeEnum;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.*;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductBonusRecordMapper;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductBonusSettleBatchMapper;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductBonusSettleDetailMapper;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductGmvRecordMapper;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusSettleStatusEnum;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusStatusEnum;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusTypeEnum;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.trade.enums.ErrorCodeConstants.*;

/**
 * 商品奖金结算 Service 实现类
 *
 * 需求 9：商品奖金池 = 当月会员商品有效 GMV × 10%；董事加权分红合计占池 27%
 * （一星 10%、二星 7%、三星 5%、四星 3%、五星 2%，各等级预算独立、同级平均分配）；
 * 公司留存 = 池 × 73%。注意 27% 是池的比例，不是 GMV 的比例。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class TradeProductBonusSettleServiceImpl implements TradeProductBonusSettleService {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");
    /**
     * 商品奖金体系标识
     */
    public static final String SCOPE_PRODUCT = "PRODUCT";
    /**
     * 公司留存比例（需求 9.4：池 × 73%）
     */
    private static final int COMPANY_RETAIN_PERCENT = 73;

    @Resource
    private TradeProductBonusRecordMapper productBonusRecordMapper;
    @Resource
    private TradeProductGmvRecordMapper productGmvRecordMapper;
    @Resource
    private TradeProductBonusSettleBatchMapper settleBatchMapper;
    @Resource
    private TradeProductBonusSettleDetailMapper settleDetailMapper;
    @Resource
    private MemberBonusApi memberBonusApi;
    @Resource
    private PayWalletApi payWalletApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TradeProductBonusSettleBatchDO settleMonth(String period) {
        Preconditions.checkArgument(period != null && PERIOD_PATTERN.matcher(period).matches(), "账期格式必须为 yyyy-MM");
        if (settleBatchMapper.selectByPeriodAndScope(period, SCOPE_PRODUCT) != null) {
            throw exception(PRODUCT_BONUS_SETTLE_BATCH_EXISTS, period);
        }
        MemberBonusConfigRespDTO config = memberBonusApi.getBonusConfig();
        List<String> remarks = new ArrayList<>();
        // 1. 当月会员商品有效 GMV（gmv_record 已扣除退款）
        int gmvAmount = productGmvRecordMapper.selectListByPeriod(period).stream()
                .mapToInt(TradeProductGmvRecordDO::getGmvAmount).sum();
        // 2. 商品奖金池 = GMV × productPoolPercent（默认 10%）
        int bonusPool = calcPercent(gmvAmount, config.getProductPoolPercent(), config.getRoundingMode());
        // 3. 董事加权分红：各等级预算 = 池 × 等级比例，同级平均分配；空档等级预算留存（策略待确认）
        String batchNo = "PB" + period.replace("-", "");
        for (int level = 1; level <= 5; level++) {
            int rate = config.getDirectorRate(level);
            int budget = calcPercent(bonusPool, rate, config.getRoundingMode());
            List<Long> directorUserIds = memberBonusApi.getDirectorUserIds(level);
            if (directorUserIds == null || directorUserIds.isEmpty()) {
                remarks.add(String.format("%s：预算(%d分)无人符合条件，按策略(%s)留存",
                        levelName(level), budget, config.getEmptyLevelPolicy()));
                continue;
            }
            int avgAmount = BigDecimal.valueOf(budget)
                    .divide(BigDecimal.valueOf(directorUserIds.size()), 0, RoundingMode.HALF_UP)
                    .intValue();
            for (Long userId : directorUserIds) {
                insertRecordIfAbsent(TradeProductBonusRecordDO.builder()
                        .dedupKey(buildDirectorDedupKey(period, userId, level))
                        .userId(userId)
                        .bonusType(TradeProductBonusTypeEnum.DIRECTOR.getType())
                        .directorLevel(level)
                        .baseAmount(budget).rate(rate).amount(avgAmount)
                        .period(period).ruleVersion(config.getVersion())
                        .status(TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus())
                        .build());
            }
        }
        // 4. 汇总该账期待结算明细（差额奖 + 商品董事分红）→ 个人结算明细
        List<TradeProductBonusRecordDO> waitRecords = productBonusRecordMapper.selectListByPeriod(period).stream()
                .filter(r -> TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus().equals(r.getStatus()))
                .toList();
        Map<Long, Integer> totalByUser = waitRecords.stream()
                .collect(Collectors.groupingBy(TradeProductBonusRecordDO::getUserId,
                        Collectors.summingInt(TradeProductBonusRecordDO::getAmount)));
        totalByUser.forEach((userId, total) -> settleDetailMapper.insert(TradeProductBonusSettleDetailDO.builder()
                .batchNo(batchNo).userId(userId).totalAmount(total)
                .payStatus(10).build()));
        for (TradeProductBonusRecordDO record : waitRecords) {
            productBonusRecordMapper.updateById(new TradeProductBonusRecordDO().setId(record.getId())
                    .setStatus(TradeProductBonusStatusEnum.SETTLED.getStatus()).setSettleBatchNo(batchNo));
        }
        // 5. 批次落库（公司留存 = 池 × 73%；空档等级额外留存记入备注）
        int totalAmount = totalByUser.values().stream().filter(v -> v > 0).mapToInt(Integer::intValue).sum();
        TradeProductBonusSettleBatchDO batch = TradeProductBonusSettleBatchDO.builder()
                .batchNo(batchNo).period(period).scope(SCOPE_PRODUCT)
                .status(TradeProductBonusSettleStatusEnum.SETTLED.getStatus())
                .gmvAmount(gmvAmount).bonusPool(bonusPool)
                .companyAmount(calcPercent(bonusPool, COMPANY_RETAIN_PERCENT, config.getRoundingMode()))
                .totalAmount(totalAmount).userCount(totalByUser.size())
                .configSnapshot(JsonUtils.toJsonString(config))
                .remark(String.join("；", remarks))
                .build();
        settleBatchMapper.insert(batch);
        log.info("[settleMonth][账期({}) 商品奖金结算完成：GMV({}分) 池({}分) 分配({}分) 人数({})]",
                period, gmvAmount, bonusPool, totalAmount, totalByUser.size());
        return batch;
    }

    @Override
    public void payBatch(String batchNo) {
        TradeProductBonusSettleBatchDO batch = getBatchByBatchNo(batchNo);
        if (TradeProductBonusSettleStatusEnum.PAID.getStatus().equals(batch.getStatus())) {
            log.info("[payBatch][批次({}) 已发放，幂等返回]", batchNo);
            return;
        }
        if (!TradeProductBonusSettleStatusEnum.SETTLED.getStatus().equals(batch.getStatus())) {
            throw exception(PRODUCT_BONUS_SETTLE_BATCH_STATUS_ERROR, batchNo);
        }
        List<TradeProductBonusSettleDetailDO> details = settleDetailMapper.selectListByBatchNo(batchNo);
        for (TradeProductBonusSettleDetailDO detail : details) {
            if (!Integer.valueOf(10).equals(detail.getPayStatus())) {
                continue;
            }
            String walletBizId = batchNo + ":" + detail.getUserId();
            // 非正数合计（退款冲抵差额）不发放、不扣减，保留财务线下处理
            if (detail.getTotalAmount() <= 0) {
                settleDetailMapper.updateById(new TradeProductBonusSettleDetailDO().setId(detail.getId())
                        .setPayStatus(30).setWalletBizId(walletBizId).setPayTime(LocalDateTime.now()));
                log.warn("[payBatch][批次({}) 会员({}) 合计({}分)非正数，跳过发放待财务处理]",
                        batchNo, detail.getUserId(), detail.getTotalAmount());
                continue;
            }
            try {
                payWalletApi.addWalletBalance(new PayWalletAddBalanceReqDTO()
                        .setUserId(detail.getUserId())
                        .setUserType(UserTypeEnum.MEMBER.getValue())
                        .setBizType(PayWalletBizTypeEnum.BONUS.getType())
                        .setBizId(walletBizId)
                        .setPrice(detail.getTotalAmount()));
                settleDetailMapper.updateById(new TradeProductBonusSettleDetailDO().setId(detail.getId())
                        .setPayStatus(20).setWalletBizId(walletBizId).setPayTime(LocalDateTime.now()));
            } catch (Exception ex) {
                log.error("[payBatch][批次({}) 会员({}) 发放失败]", batchNo, detail.getUserId(), ex);
                settleDetailMapper.updateById(new TradeProductBonusSettleDetailDO().setId(detail.getId())
                        .setPayStatus(30).setWalletBizId(walletBizId));
            }
        }
        settleBatchMapper.updateById(new TradeProductBonusSettleBatchDO().setId(batch.getId())
                .setStatus(TradeProductBonusSettleStatusEnum.PAID.getStatus()));
        log.info("[payBatch][批次({}) 发放流程执行完成]", batchNo);
    }

    @Override
    public PageResult<TradeProductBonusSettleBatchDO> getBatchPage(TradeProductBonusSettleBatchPageReqVO pageReqVO) {
        return settleBatchMapper.selectPage(pageReqVO);
    }

    @Override
    public TradeProductBonusSettleBatchDO getBatchByBatchNo(String batchNo) {
        TradeProductBonusSettleBatchDO batch = settleBatchMapper.selectByBatchNo(batchNo);
        if (batch == null) {
            throw exception(PRODUCT_BONUS_SETTLE_BATCH_NOT_EXISTS);
        }
        return batch;
    }

    @Override
    public List<TradeProductBonusSettleDetailDO> getDetailListByBatchNo(String batchNo) {
        return settleDetailMapper.selectListByBatchNo(batchNo);
    }

    private void insertRecordIfAbsent(TradeProductBonusRecordDO record) {
        if (productBonusRecordMapper.selectByDedupKey(record.getDedupKey()) != null) {
            return;
        }
        productBonusRecordMapper.insert(record);
    }

    static String buildDirectorDedupKey(String period, Long userId, Integer level) {
        return "PRODUCT-DIRECTOR:" + period + ":" + userId + ":" + level;
    }

    static String levelName(int level) {
        return switch (level) {
            case 1 -> "一星董事";
            case 2 -> "二星董事";
            case 3 -> "三星董事";
            case 4 -> "四星董事";
            case 5 -> "五星董事";
            default -> "未知等级";
        };
    }

    private int calcPercent(int base, Integer percent, String roundingMode) {
        RoundingMode mode;
        try {
            mode = RoundingMode.valueOf(roundingMode);
        } catch (IllegalArgumentException | NullPointerException ex) {
            mode = RoundingMode.HALF_UP;
        }
        return BigDecimal.valueOf(base).multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 0, mode).intValue();
    }

}

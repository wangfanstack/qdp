package cn.sailstack.qdp.module.member.service.bonus.gift;

import cn.sailstack.qdp.framework.common.enums.CommonStatusEnum;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftEffectReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftPrepareRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit.MemberGiftBenefitPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.*;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.*;
import cn.sailstack.qdp.module.member.enums.bonus.*;
import cn.sailstack.qdp.module.member.service.bonus.calculate.BonusCalcUtils;
import cn.sailstack.qdp.module.member.service.bonus.calculate.DirectBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.calculate.IncrementBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import cn.sailstack.qdp.module.member.service.bonus.relation.MemberRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.*;

/**
 * 会员礼包权益 Service 实现类
 *
 * 事务与并发（需求 12.3）：
 * 1. 权益落库幂等：uk(order_id) + 先查后插；
 * 2. 有效直推顺序：事务内 SELECT FOR UPDATE 锁定推荐人关系行后计算顺序，同一推荐人并发购买串行化；
 * 3. 老板身份与市场切换：与直推顺序分配同事务，boss_time/market/history 一次成型；
 * 4. 增量奖唯一归属：沿推荐链向上找 bossTime 严格早于支付时间的最近老板（最近有效老板市场归属原则）。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class MemberGiftBenefitServiceImpl implements MemberGiftBenefitService {

    @Resource
    private MemberGiftConfigService giftConfigService;
    @Resource
    private MemberBonusConfigService bonusConfigService;
    @Resource
    private MemberRelationService relationService;
    @Resource
    private MemberGiftBenefitMapper giftBenefitMapper;
    @Resource
    private MemberRelationMapper relationMapper;
    @Resource
    private MemberMarketMapper marketMapper;
    @Resource
    private MemberMarketHistoryMapper marketHistoryMapper;
    @Resource
    private MemberBonusRecordMapper bonusRecordMapper;
    @Resource
    private DirectBonusCalculator directBonusCalculator;
    @Resource
    private IncrementBonusCalculator incrementBonusCalculator;

    @Override
    public MemberGiftPrepareRespDTO prepareGiftBuy(Long userId, Long giftId) {
        // 1. 礼包必须存在并开启
        MemberGiftConfigDO gift = giftConfigService.validateGiftConfigEnable(giftId);
        // 2. 仅可购买更高档位：需求 2.2 只定义补差升级，不支持同级或降级
        Integer paidAmount = getPaidAmount(userId);
        if (paidAmount >= gift.getPrice()) {
            throw exception(GIFT_BUY_LEVEL_NOT_HIGHER);
        }
        // 3. 差价计算：应补差价 = 目标礼包价格 - 当前有效礼包已购金额
        MemberRelationDO relation = relationService.getRelation(userId);
        return new MemberGiftPrepareRespDTO()
                .setGiftId(gift.getId())
                .setGiftName(gift.getName())
                .setGiftPrice(gift.getPrice())
                .setPaidAmount(paidAmount)
                .setPayableAmount(gift.getPrice() - paidAmount)
                .setUpgrade(paidAmount > 0)
                .setReferrerBound(relation != null && relation.getReferrerId() != null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void effectGiftOrder(MemberGiftEffectReqDTO reqDTO) {
        // 1. 幂等：订单维度唯一
        if (giftBenefitMapper.selectByOrderId(reqDTO.getOrderId()) != null) {
            log.info("[effectGiftOrder][订单({}) 权益已生效，幂等返回]", reqDTO.getOrderId());
            return;
        }
        // 2. 定位礼包配置（SPU 维度）
        MemberGiftConfigDO gift = giftConfigService.getGiftConfigBySpuId(reqDTO.getSpuId());
        if (gift == null) {
            log.warn("[effectGiftOrder][订单({}) 的 SPU({}) 未绑定礼包配置，忽略]", reqDTO.getOrderId(), reqDTO.getSpuId());
            return;
        }
        MemberBonusConfigDO config = bonusConfigService.getRequiredConfig();
        // 3. 计算新增 PV：升级只按有效补差价计算，不重复计算历史金额（payAmount 即本次有效支付金额）
        int pv = BonusCalcUtils.calcPv(reqDTO.getPayAmount(), gift.getPvPercent());
        int paidAmount = getPaidAmount(reqDTO.getUserId());
        // 4. 权益落库（uk order_id 兜底并发）
        MemberGiftBenefitDO benefit = MemberGiftBenefitDO.builder()
                .userId(reqDTO.getUserId()).giftId(gift.getId())
                .orderId(reqDTO.getOrderId()).orderNo(reqDTO.getOrderNo())
                .type(paidAmount == 0 ? MemberGiftBenefitTypeEnum.NEW.getType() : MemberGiftBenefitTypeEnum.UPGRADE.getType())
                .payAmount(reqDTO.getPayAmount()).paidAmount(paidAmount).addedAmount(reqDTO.getPayAmount())
                .pv(pv).reversalAmount(0).reversalPv(0)
                .status(MemberGiftBenefitStatusEnum.EFFECT.getStatus())
                .effectTime(reqDTO.getPayTime())
                .build();
        giftBenefitMapper.insert(benefit);
        // 5. 推荐关系处理：必须已绑定推荐人
        MemberRelationDO relation = relationService.getRelation(reqDTO.getUserId());
        if (relation == null || relation.getReferrerId() == null) {
            throw exception(GIFT_BUY_NEED_REFERRER);
        }
        Long referrerId = relation.getReferrerId();
        // 5.1 首次有效购买：分配有效直推顺序（锁推荐人行）
        int validSeq;
        if (relation.getValidBuyTime() == null) {
            validSeq = allocateValidSeq(referrerId, reqDTO.getUserId(), reqDTO.getPayTime());
        } else {
            // 升级购买：不重复计入直推顺序
            validSeq = relation.getValidSeq() != null ? relation.getValidSeq() : 0;
        }
        // 6. 直推奖（第 1、2 名 30%；第 3 名及以后 55%）
        insertBonusIfAbsent(directBonusCalculator.calculate(config, DirectBonusCalculator.DirectBonusCalculateReqBO.builder()
                .userId(referrerId).sourceUserId(reqDTO.getUserId()).sourceOrderId(reqDTO.getOrderId())
                .pv(pv).validSeq(validSeq).effectTime(reqDTO.getPayTime()).build()));
        // 7. 增量奖：仅当被推荐人是推荐人前两名直推时，其 PV 计入市场归属人的增量奖
        if (validSeq >= 1 && validSeq <= 2) {
            MemberRelationDO owner = relationService.findMarketOwner(referrerId, reqDTO.getPayTime());
            if (owner != null) {
                insertBonusIfAbsent(incrementBonusCalculator.calculate(config, IncrementBonusCalculator.IncrementBonusCalculateReqBO.builder()
                        .userId(owner.getUserId()).marketId(owner.getMarketId())
                        .sourceUserId(reqDTO.getUserId()).sourceOrderId(reqDTO.getOrderId())
                        .pv(pv).effectTime(reqDTO.getPayTime()).build()));
            }
        }
        log.info("[effectGiftOrder][订单({}) 会员({}) 礼包({}) PV({}) 生效完成]", reqDTO.getOrderId(), reqDTO.getUserId(), gift.getId(), pv);
    }

    /**
     * 分配有效直推顺序：锁定推荐人关系行 → 计算已有有效直推数 → 顺序 +1；
     * 第 2 名达成时同事务完成老板身份、市场创建与归属历史。
     */
    private int allocateValidSeq(Long referrerId, Long userId, LocalDateTime payTime) {
        // 锁定推荐人关系行（不存在则先创建占位行再锁），同一推荐人并发购买在此串行化
        if (relationMapper.selectByUserIdForUpdate(referrerId) == null) {
            relationService.getOrCreateRelation(referrerId);
        }
        MemberRelationDO referrerRelation = relationMapper.selectByUserIdForUpdate(referrerId);
        List<MemberRelationDO> validDirects = relationMapper.selectListByReferrerId(referrerId);
        int seq = validDirects.size() + 1;
        // 更新被推荐人首次有效购买时间与顺序
        MemberRelationDO selfRelation = relationMapper.selectByUserIdForUpdate(userId);
        relationMapper.updateById(new MemberRelationDO().setId(selfRelation.getId())
                .setValidBuyTime(payTime).setValidSeq(seq));
        // 第 2 名有效直推达成 → 推荐人取得老板身份，建立自己的市场
        if (seq == 2 && referrerRelation.getBossTime() == null) {
            MemberMarketDO market = MemberMarketDO.builder()
                    .ownerUserId(referrerId).effectTime(payTime)
                    .status(CommonStatusEnum.ENABLE.getStatus()).build();
            marketMapper.insert(market);
            relationMapper.updateById(new MemberRelationDO().setId(referrerRelation.getId())
                    .setBossTime(payTime).setMarketId(market.getId()));
            // 市场归属变更历史（只追加不覆盖）
            marketHistoryMapper.insert(MemberMarketHistoryDO.builder()
                    .userId(referrerId).fromMarketId(referrerRelation.getMarketId()).toMarketId(market.getId())
                    .effectTime(payTime).reason("第2名有效直推达成，取得老板身份").build());
            log.info("[allocateValidSeq][会员({}) 取得老板身份，市场({}) 生效]", referrerId, market.getId());
        }
        return seq;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundGiftOrder(MemberGiftRefundReqDTO reqDTO) {
        MemberGiftBenefitDO benefit = giftBenefitMapper.selectByOrderId(reqDTO.getOrderId());
        if (benefit == null) {
            log.info("[refundGiftOrder][订单({}) 未生效过权益，幂等返回]", reqDTO.getOrderId());
            return;
        }
        // 1. 累计冲减金额（不超过支付金额；重复回调幂等）
        int newReversalAmount = Math.min(benefit.getReversalAmount() + reqDTO.getRefundAmount(), benefit.getPayAmount());
        int currentReversalAmount = newReversalAmount - benefit.getReversalAmount();
        if (currentReversalAmount <= 0) {
            return;
        }
        MemberGiftConfigDO gift = giftConfigService.getGiftConfig(benefit.getGiftId());
        int pvPercent = gift != null ? gift.getPvPercent() : 60;
        // PV 按累计冲减金额重算，保证全额退款时累计冲减 PV 与原 PV 一致
        int newReversalPv = BonusCalcUtils.calcPv(newReversalAmount, pvPercent);
        boolean fullReversal = newReversalAmount >= benefit.getPayAmount();
        giftBenefitMapper.updateById(new MemberGiftBenefitDO()
                .setId(benefit.getId())
                .setReversalAmount(newReversalAmount)
                .setReversalPv(newReversalPv)
                .setStatus(fullReversal ? MemberGiftBenefitStatusEnum.REVERSED.getStatus()
                        : MemberGiftBenefitStatusEnum.EFFECT.getStatus()));
        // 2. 冲正对应奖金（直推 + 增量），按退款金额比例冲回
        int currentReversalPv = newReversalPv - benefit.getReversalPv();
        List<MemberBonusRecordDO> bonusRecords = bonusRecordMapper.selectListBySourceOrderId(reqDTO.getOrderId());
        for (MemberBonusRecordDO record : bonusRecords) {
            if (Objects.equals(record.getBonusType(), MemberBonusTypeEnum.DIRECT.getType())) {
                reverseBonus(record, benefit, currentReversalAmount, currentReversalPv,
                        DirectBonusCalculator.buildReversalDedupKey(reqDTO.getOrderId(), reqDTO.getRefundBizId()),
                        reqDTO.getReason(), fullReversal);
            } else if (Objects.equals(record.getBonusType(), MemberBonusTypeEnum.INCREMENT.getType())) {
                reverseBonus(record, benefit, currentReversalAmount, currentReversalPv,
                        IncrementBonusCalculator.buildReversalDedupKey(reqDTO.getOrderId(), reqDTO.getRefundBizId()),
                        reqDTO.getReason(), fullReversal);
            }
        }
        log.info("[refundGiftOrder][订单({}) 累计冲减金额({})分 / 支付({})分]", reqDTO.getOrderId(), newReversalAmount, benefit.getPayAmount());
    }

    /**
     * 冲正单条奖金：
     * 待结算原记录：部分退款保持结算、由负数冲正记录（WAIT_SETTLE）在结算时抵扣；全额退款置已冲正。
     * 已结算/已发放原记录：状态不动（历史不覆盖），负数冲正记录置已冲正（RECORD_ONLY，仅可追溯追偿，待财务确认处理策略）。
     */
    private void reverseBonus(MemberBonusRecordDO record, MemberGiftBenefitDO benefit,
                              int currentReversalAmount, int currentReversalPv,
                              String dedupKey, String reason, boolean fullReversal) {
        if (bonusRecordMapper.selectByDedupKey(dedupKey) != null) {
            return; // 该退款单已冲正，幂等
        }
        // 按本次冲减金额占支付金额比例冲回
        long ratio = currentReversalAmount * 10000L / benefit.getPayAmount();
        int reversalAmount = (int) (record.getAmount() * ratio / 10000L);
        int reversalBaseAmount = (int) (record.getBaseAmount() * ratio / 10000L);
        if (reversalAmount <= 0) {
            return;
        }
        boolean waitSettle = Objects.equals(record.getStatus(), MemberBonusStatusEnum.WAIT_SETTLE.getStatus());
        // 全额退款：原记录置已冲正（含已结算未发放场景按需求冲减未结算/待处理）
        if (fullReversal && waitSettle) {
            bonusRecordMapper.updateById(new MemberBonusRecordDO().setId(record.getId())
                    .setStatus(MemberBonusStatusEnum.REVERSED.getStatus())
                    .setReversalReason(reason));
        }
        MemberBonusRecordDO reversal = MemberBonusRecordDO.builder()
                .dedupKey(dedupKey)
                .userId(record.getUserId())
                .bonusType(record.getBonusType())
                .sourceUserId(record.getSourceUserId())
                .sourceOrderId(record.getSourceOrderId())
                .basePv(currentReversalPv)
                .baseAmount(reversalBaseAmount)
                .rate(record.getRate())
                .amount(-reversalAmount)
                .marketId(record.getMarketId())
                .period(record.getPeriod())
                .ruleVersion(record.getRuleVersion())
                // 部分退款且原记录待结算：负数冲正参与下期结算抵扣；
                // 全额退款（原记录已置冲正）或已结算/已发放：负数记录仅作可追溯冲正/追偿，不参与结算
                .status(waitSettle && !fullReversal ? MemberBonusStatusEnum.WAIT_SETTLE.getStatus()
                        : MemberBonusStatusEnum.REVERSED.getStatus())
                .reversalOfId(record.getId())
                .reversalReason(reason)
                .build();
        bonusRecordMapper.insert(reversal);
    }

    private void insertBonusIfAbsent(MemberBonusRecordDO record) {
        if (record == null) {
            return;
        }
        if (bonusRecordMapper.selectByDedupKey(record.getDedupKey()) != null) {
            log.info("[insertBonusIfAbsent][防重复键({}) 已存在，跳过]", record.getDedupKey());
            return;
        }
        bonusRecordMapper.insert(record);
    }

    @Override
    public Integer getPaidAmount(Long userId) {
        List<MemberGiftBenefitDO> benefits = giftBenefitMapper.selectListByUserId(userId);
        return benefits.stream()
                .filter(b -> Objects.equals(b.getStatus(), MemberGiftBenefitStatusEnum.EFFECT.getStatus()))
                .mapToInt(b -> b.getPayAmount() - b.getReversalAmount())
                .sum();
    }

    @Override
    public List<MemberGiftBenefitDO> getBenefitListByUserId(Long userId) {
        return giftBenefitMapper.selectListByUserId(userId);
    }

    @Override
    public PageResult<MemberGiftBenefitDO> getBenefitPage(MemberGiftBenefitPageReqVO pageReqVO) {
        return giftBenefitMapper.selectPage(pageReqVO);
    }

}

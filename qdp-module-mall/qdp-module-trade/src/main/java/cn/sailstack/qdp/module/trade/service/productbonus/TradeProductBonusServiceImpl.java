package cn.sailstack.qdp.module.trade.service.productbonus;

import cn.sailstack.qdp.framework.common.enums.UserTypeEnum;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.level.MemberLevelApi;
import cn.sailstack.qdp.module.member.api.level.dto.MemberLevelRespDTO;
import cn.sailstack.qdp.module.member.api.user.MemberUserApi;
import cn.sailstack.qdp.module.member.api.user.dto.MemberUserRespDTO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusRecordPageReqVO;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductGmvPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.aftersale.AfterSaleDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusRecordDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductGmvRecordDO;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductBonusRecordMapper;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductGmvRecordMapper;
import cn.sailstack.qdp.module.trade.enums.order.TradeOrderTypeEnum;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusStatusEnum;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusTypeEnum;
import cn.sailstack.qdp.module.trade.service.aftersale.AfterSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 商品奖金 Service 实现类（折扣差额奖 + 会员商品 GMV）
 *
 * 需求 8：直接上级奖金 =（购买会员折扣 - 直接上级折扣）× 商品折后价格；差额<=0 不产生正向奖金；
 * 仅购买会员的直接上级获得，不向间接上级发放。
 * 需求 9：GMV 仅统计会员商品已完成订单实际有效成交金额并扣除退款；礼包订单不计入。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class TradeProductBonusServiceImpl implements TradeProductBonusService {

    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Resource
    private TradeProductBonusRecordMapper productBonusRecordMapper;
    @Resource
    private TradeProductGmvRecordMapper productGmvRecordMapper;
    @Resource
    private MemberBonusApi memberBonusApi;
    @Resource
    private MemberUserApi memberUserApi;
    @Resource
    private MemberLevelApi memberLevelApi;
    @Resource
    private AfterSaleService afterSaleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBonusOnReceiveOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 礼包订单属于礼包奖金体系，不计入商品 GMV、不产生差额奖
        if (TradeOrderTypeEnum.isGift(order.getType())) {
            return;
        }
        String period = order.getReceiveTime() != null ? PERIOD_FORMATTER.format(order.getReceiveTime())
                : PERIOD_FORMATTER.format(order.getFinishTime() != null ? order.getFinishTime() : java.time.LocalDateTime.now());
        // 1. 记录 GMV（uk order_id 幂等）；口径默认不含运费（【待业务确认】配置项 gmvIncludeDelivery）
        if (productGmvRecordMapper.selectByOrderId(order.getId()) == null) {
            int gmvBase = calcGmvBase(order);
            if (gmvBase > 0) {
                productGmvRecordMapper.insert(TradeProductGmvRecordDO.builder()
                        .orderId(order.getId()).orderNo(order.getNo()).userId(order.getUserId())
                        .payAmount(gmvBase).refundAmount(0).gmvAmount(gmvBase)
                        .period(period)
                        .finishTime(order.getReceiveTime() != null ? order.getReceiveTime() : java.time.LocalDateTime.now())
                        .build());
            }
        }
        // 2. 折扣差额奖：仅发购买会员的直接上级
        Long superiorId = memberBonusApi.getDirectSuperiorId(order.getUserId());
        if (superiorId == null || superiorId.equals(order.getUserId())) {
            return; // 无推荐关系（直接上级），不产生差额奖
        }
        Integer buyerDiscount = getDiscountPercent(order.getUserId());
        Integer superiorDiscount = getDiscountPercent(superiorId);
        if (buyerDiscount == null || superiorDiscount == null) {
            return; // 任一方无等级折扣信息（无 member_level），不产生差额奖
        }
        int diffRate = buyerDiscount - superiorDiscount;
        if (diffRate <= 0) {
            return; // 折扣差额小于等于 0，不产生正向奖金
        }
        for (TradeOrderItemDO orderItem : orderItems) {
            // 已售后成功的订单项不计算
            if (!Objects.equals(orderItem.getAfterSaleStatus(), 0)) {
                continue;
            }
            String dedupKey = buildDifferenceDedupKey(orderItem.getId());
            if (productBonusRecordMapper.selectByDedupKey(dedupKey) != null) {
                continue;
            }
            int baseAmount = orderItem.getPayPrice() != null ? orderItem.getPayPrice() : 0;
            if (baseAmount <= 0) {
                continue;
            }
            // 奖金 = 折扣差（百分比）× 商品折后价格（订单项实付）
            int amount = BigDecimal.valueOf(baseAmount)
                    .multiply(BigDecimal.valueOf(diffRate))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                    .intValue();
            productBonusRecordMapper.insert(TradeProductBonusRecordDO.builder()
                    .dedupKey(dedupKey)
                    .userId(superiorId)
                    .bonusType(TradeProductBonusTypeEnum.DIFFERENCE.getType())
                    .buyerUserId(order.getUserId())
                    .orderId(order.getId()).orderItemId(orderItem.getId())
                    .buyerDiscount(buyerDiscount).superiorDiscount(superiorDiscount)
                    .baseAmount(baseAmount).rate(diffRate).amount(amount)
                    .period(period)
                    .ruleVersion(1)
                    .status(TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus())
                    .build());
        }
        log.info("[createBonusOnReceiveOrder][订单({}) GMV 记录完成，买家折扣({}) 上级({}) 差额奖比例({}%)]",
                order.getId(), buyerDiscount, superiorDiscount, diffRate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reverseBonusOnAfterSale(TradeOrderDO order, TradeOrderItemDO orderItem) {
        if (TradeOrderTypeEnum.isGift(order.getType())) {
            return; // 礼包订单退款由礼包权益流程冲正
        }
        // 1. 扣减 GMV：按售后退款金额累减；累计退款达到订单实付视为全额退款
        TradeProductGmvRecordDO gmvRecord = productGmvRecordMapper.selectByOrderId(order.getId());
        AfterSaleDO afterSale = orderItem.getAfterSaleId() != null ? afterSaleService.getAfterSale(orderItem.getAfterSaleId()) : null;
        int refundAmount = afterSale != null ? afterSale.getRefundPrice() : 0;
        boolean fullRefund = false;
        if (gmvRecord != null && refundAmount > 0) {
            int newRefund = Math.min(gmvRecord.getRefundAmount() + refundAmount, gmvRecord.getPayAmount());
            productGmvRecordMapper.updateById(new TradeProductGmvRecordDO()
                    .setId(gmvRecord.getId())
                    .setRefundAmount(newRefund)
                    .setGmvAmount(gmvRecord.getPayAmount() - newRefund));
            fullRefund = newRefund >= gmvRecord.getPayAmount();
        }
        // 2. 冲回差额奖：按对应商品退款金额占订单项实付比例冲回（幂等：售后单维度）
        TradeProductBonusRecordDO bonusRecord = productBonusRecordMapper.selectOriginalDifferenceByOrderItemId(orderItem.getId());
        if (bonusRecord == null || refundAmount <= 0 || bonusRecord.getAmount() <= 0) {
            return;
        }
        String reversalDedupKey = buildDifferenceReversalDedupKey(orderItem.getId(), orderItem.getAfterSaleId());
        if (productBonusRecordMapper.selectByDedupKey(reversalDedupKey) != null) {
            return;
        }
        int itemPayAmount = bonusRecord.getBaseAmount();
        long ratio = itemPayAmount > 0 ? refundAmount * 10000L / itemPayAmount : 0;
        int reversalAmount = (int) (bonusRecord.getAmount() * ratio / 10000L);
        if (reversalAmount <= 0) {
            return;
        }
        boolean waitSettle = TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus().equals(bonusRecord.getStatus());
        if (fullRefund && waitSettle) {
            productBonusRecordMapper.updateById(new TradeProductBonusRecordDO().setId(bonusRecord.getId())
                    .setStatus(TradeProductBonusStatusEnum.REVERSED.getStatus())
                    .setReversalReason("订单项全额退款"));
        }
        productBonusRecordMapper.insert(TradeProductBonusRecordDO.builder()
                .dedupKey(reversalDedupKey)
                .userId(bonusRecord.getUserId())
                .bonusType(bonusRecord.getBonusType())
                .buyerUserId(bonusRecord.getBuyerUserId())
                .orderId(bonusRecord.getOrderId()).orderItemId(bonusRecord.getOrderItemId())
                .buyerDiscount(bonusRecord.getBuyerDiscount()).superiorDiscount(bonusRecord.getSuperiorDiscount())
                .baseAmount(refundAmount).rate(bonusRecord.getRate()).amount(-reversalAmount)
                .period(bonusRecord.getPeriod())
                .ruleVersion(bonusRecord.getRuleVersion())
                // 部分退款且原记录待结算：负数冲正参与结算抵扣；全额退款或已结算/已发放：仅记录可追溯冲正
                .status(waitSettle && !fullRefund ? TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus()
                        : TradeProductBonusStatusEnum.REVERSED.getStatus())
                .reversalOfId(bonusRecord.getId())
                .reversalReason("订单项退款冲回：售后单(" + orderItem.getAfterSaleId() + ")")
                .build());
        log.info("[reverseBonusOnAfterSale][订单项({}) 冲回差额奖({}分)]", orderItem.getId(), reversalAmount);
    }

    @Override
    public PageResult<TradeProductBonusRecordDO> getBonusRecordPage(TradeProductBonusRecordPageReqVO pageReqVO) {
        return productBonusRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<TradeProductGmvRecordDO> getGmvPage(TradeProductGmvPageReqVO pageReqVO) {
        return productGmvRecordMapper.selectPage(pageReqVO);
    }

    // ========== 内部方法 ==========

    /**
     * GMV 基数：订单实付；默认不含运费（优惠券/运费口径为【待业务确认】项，
     * 配置在 member_bonus_config.gmv_include_delivery，trade 侧结算时按明细汇总口径一致）
     */
    private int calcGmvBase(TradeOrderDO order) {
        int payPrice = order.getPayPrice() != null ? order.getPayPrice() : 0;
        int deliveryPrice = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : 0;
        return Math.max(payPrice - deliveryPrice, 0);
    }

    /**
     * 获得会员折扣（member_level.discount_percent；无等级返回 null）
     */
    private Integer getDiscountPercent(Long userId) {
        MemberUserRespDTO user = memberUserApi.getUser(userId);
        if (user == null || user.getLevelId() == null) {
            return null;
        }
        MemberLevelRespDTO level = memberLevelApi.getMemberLevel(user.getLevelId());
        return level != null ? level.getDiscountPercent() : null;
    }

    static String buildDifferenceDedupKey(Long orderItemId) {
        return "DIFF:" + orderItemId;
    }

    static String buildDifferenceReversalDedupKey(Long orderItemId, Long afterSaleId) {
        return "DIFF-REV:" + orderItemId + ":" + afterSaleId;
    }

}

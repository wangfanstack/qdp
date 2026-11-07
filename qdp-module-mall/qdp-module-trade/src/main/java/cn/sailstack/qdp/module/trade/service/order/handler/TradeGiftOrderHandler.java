package cn.sailstack.qdp.module.trade.service.order.handler;

import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftConfigRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftEffectReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftPrepareRespDTO;
import cn.sailstack.qdp.module.trade.dal.dataobject.aftersale.AfterSaleDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.sailstack.qdp.module.trade.enums.order.TradeOrderTypeEnum;
import cn.sailstack.qdp.module.trade.service.aftersale.AfterSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.trade.enums.ErrorCodeConstants.PRODUCT_BONUS_GIFT_ORDER_ITEMS_ERROR;

/**
 * 会员礼包订单的 {@link TradeOrderHandler} 实现类
 *
 * 礼包 = 会员权益，SPU 只是支付载体：
 * 1. beforeOrderCreate：识别礼包 SPU → 校验档位/推荐关系 → 标记订单类型 GIFT → 升级自动补差价（adjustPrice）；
 * 2. afterPayOrder：支付成功即权益生效（虚拟权益交付即时完成，观察期配置化），通知 member 落权益并计奖；
 * 3. afterCancelOrderItem/afterCancelOrder：退款冲正（未支付取消无影响，幂等跳过）。
 *
 * @author sail
 */
@Component
@Slf4j
public class TradeGiftOrderHandler implements TradeOrderHandler {

    @Resource
    private MemberBonusApi memberBonusApi;
    @Resource
    private AfterSaleService afterSaleService;

    @Override
    public void beforeOrderCreate(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        // 1. 识别订单中的礼包 SPU
        MemberGiftConfigRespDTO giftConfig = null;
        int giftItemCount = 0;
        for (TradeOrderItemDO orderItem : orderItems) {
            MemberGiftConfigRespDTO config = memberBonusApi.getEnabledGiftConfigBySpuId(orderItem.getSpuId());
            if (config != null) {
                giftConfig = config;
                giftItemCount += orderItem.getCount();
            }
        }
        if (giftConfig == null) {
            return; // 普通商品订单
        }
        // 2. 礼包订单必须且只能包含一个礼包（不支持多礼包/混单）
        if (giftItemCount != 1 || orderItems.size() != 1) {
            throw exception(PRODUCT_BONUS_GIFT_ORDER_ITEMS_ERROR);
        }
        // 3. 校验档位（仅可购买更高档）与推荐关系，计算应付金额（全价或补差价）
        MemberGiftPrepareRespDTO prepare = memberBonusApi.prepareGiftBuy(order.getUserId(), giftConfig.getId());
        // 4. 标记为礼包订单；升级自动补差价：adjustPrice = -(当前累计已购金额)
        order.setType(TradeOrderTypeEnum.GIFT.getType());
        if (prepare.getPaidAmount() > 0) {
            order.setAdjustPrice(-prepare.getPaidAmount());
            // payPrice 与 adjustPrice 保持同口径（礼包入口不使用优惠券/积分，这里统一按差价重算）
            order.setPayPrice(Math.max(order.getTotalPrice() - prepare.getPaidAmount()
                    - (order.getCouponPrice() != null ? order.getCouponPrice() : 0)
                    - (order.getPointPrice() != null ? order.getPointPrice() : 0)
                    - (order.getDiscountPrice() != null ? order.getDiscountPrice() : 0)
                    - (order.getVipPrice() != null ? order.getVipPrice() : 0)
                    + (order.getDeliveryPrice() != null ? order.getDeliveryPrice() : 0), 0));
        }
        log.info("[beforeOrderCreate][会员({}) 礼包({}) 下单：应付({})分 升级({})]",
                order.getUserId(), giftConfig.getId(), prepare.getPayableAmount(), prepare.getUpgrade());
    }

    @Override
    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        if (!TradeOrderTypeEnum.isGift(order.getType())) {
            return;
        }
        TradeOrderItemDO giftItem = orderItems.get(0);
        MemberGiftEffectReqDTO effectReqDTO = new MemberGiftEffectReqDTO()
                .setOrderId(order.getId())
                .setOrderNo(order.getNo())
                .setUserId(order.getUserId())
                .setSpuId(giftItem.getSpuId())
                .setPayAmount(order.getPayPrice())
                .setPayTime(order.getPayTime());
        memberBonusApi.effectGiftOrder(effectReqDTO);
    }

    @Override
    public void afterCancelOrderItem(TradeOrderDO order, TradeOrderItemDO orderItem) {
        if (!TradeOrderTypeEnum.isGift(order.getType()) || !order.getPayStatus()) {
            return; // 未支付取消：权益未生效，无需冲正
        }
        // 售后退款金额按售后单获取
        AfterSaleDO afterSale = orderItem.getAfterSaleId() != null
                ? afterSaleService.getAfterSale(orderItem.getAfterSaleId()) : null;
        Integer refundAmount = afterSale != null ? afterSale.getRefundPrice() : order.getRefundPrice();
        memberBonusApi.refundGiftOrder(new cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO()
                .setOrderId(order.getId())
                .setUserId(order.getUserId())
                .setRefundAmount(refundAmount != null ? refundAmount : 0)
                .setRefundBizId(String.valueOf(orderItem.getAfterSaleId() != null ? orderItem.getAfterSaleId() : orderItem.getId()))
                .setReason("礼包订单售后退款"));
    }

    @Override
    public void afterCancelOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {
        if (!TradeOrderTypeEnum.isGift(order.getType()) || !order.getPayStatus()) {
            return;
        }
        // 已售后成功的订单项已在 afterCancelOrderItem 冲正，这里只处理无售后的订单项（如整单退款取消）
        List<TradeOrderItemDO> noneAfterSaleItems = filterOrderItemListByNoneAfterSale(orderItems);
        if (noneAfterSaleItems.isEmpty()) {
            return;
        }
        // 整单取消退款：按订单已退款金额冲正（refundBizId 用订单号保证幂等）
        int refundAmount = noneAfterSaleItems.stream()
                .mapToInt(item -> item.getPayPrice() != null ? item.getPayPrice() : 0).sum();
        memberBonusApi.refundGiftOrder(new cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO()
                .setOrderId(order.getId())
                .setUserId(order.getUserId())
                .setRefundAmount(Math.min(refundAmount, order.getPayPrice()))
                .setRefundBizId("ORDER-" + order.getId())
                .setReason("礼包订单整单退款取消"));
    }

}

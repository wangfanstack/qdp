package cn.sailstack.qdp.module.trade.service.order.handler;

import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.sailstack.qdp.module.trade.dal.mysql.order.TradeOrderItemMapper;
import cn.sailstack.qdp.module.trade.service.productbonus.TradeProductBonusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品奖金（折扣差额奖 + 会员商品 GMV）的 {@link TradeOrderHandler} 实现类
 *
 * 需求 8：订单完成后产生差额奖；部分退款/整单退款按对应商品退款金额冲回。
 * 支付成功不等于订单完成，不在支付回调计奖。
 *
 * @author sail
 */
@Component
@Slf4j
public class TradeProductBonusOrderHandler implements TradeOrderHandler {

    @Resource
    private TradeProductBonusService productBonusService;
    @Resource
    private TradeOrderItemMapper tradeOrderItemMapper;

    @Override
    public void afterReceiveOrder(TradeOrderDO order) {
        // 订单完成（确认收货/系统自动收货/自提核销）→ 记录 GMV + 计算差额奖
        List<TradeOrderItemDO> orderItems = tradeOrderItemMapper.selectListByOrderId(order.getId());
        productBonusService.createBonusOnReceiveOrder(order, orderItems);
    }

    @Override
    public void afterCancelOrderItem(TradeOrderDO order, TradeOrderItemDO orderItem) {
        // 订单项售后退款成功 → 冲回差额奖 + 扣减 GMV（礼包订单在 TradeGiftOrderHandler 处理）
        productBonusService.reverseBonusOnAfterSale(order, orderItem);
    }

}

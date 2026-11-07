package cn.sailstack.qdp.module.trade.service.productbonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusRecordPageReqVO;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductGmvPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusRecordDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductGmvRecordDO;

/**
 * 商品奖金 Service 接口（折扣差额奖 + 会员商品 GMV）
 *
 * @author sail
 */
public interface TradeProductBonusService {

    /**
     * 订单完成（确认收货）后：记录会员商品 GMV + 计算直接上级折扣差额奖
     *
     * 幂等：GMV uk(order_id)、差额奖 uk(dedup_key=订单项)；
     * 礼包订单不计入商品 GMV、不产生差额奖（两套体系独立）。
     *
     * @param order      订单
     * @param orderItems 订单项列表
     */
    void createBonusOnReceiveOrder(TradeOrderDO order, java.util.List<TradeOrderItemDO> orderItems);

    /**
     * 订单项售后退款成功后：按对应商品退款金额冲回差额奖并扣减 GMV
     *
     * @param order     订单
     * @param orderItem 订单项（afterSaleId 已回填）
     */
    void reverseBonusOnAfterSale(TradeOrderDO order, TradeOrderItemDO orderItem);

    /**
     * 获得商品奖金明细分页
     *
     * @param pageReqVO 分页查询
     * @return 商品奖金明细分页
     */
    PageResult<TradeProductBonusRecordDO> getBonusRecordPage(TradeProductBonusRecordPageReqVO pageReqVO);

    /**
     * 获得会员商品 GMV 分页
     *
     * @param pageReqVO 分页查询
     * @return GMV 分页
     */
    PageResult<TradeProductGmvRecordDO> getGmvPage(TradeProductGmvPageReqVO pageReqVO);

}

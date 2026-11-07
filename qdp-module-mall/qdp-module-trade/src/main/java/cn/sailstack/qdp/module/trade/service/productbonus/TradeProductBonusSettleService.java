package cn.sailstack.qdp.module.trade.service.productbonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusSettleBatchDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusSettleDetailDO;

import java.util.List;

/**
 * 商品奖金结算 Service 接口（与礼包奖金独立核算、独立批次）
 *
 * @author sail
 */
public interface TradeProductBonusSettleService {

    /**
     * 执行指定账期的商品奖金月度结算
     *
     * 流程：汇总当月会员商品有效 GMV → 商品奖金池（GMV × 10%）→
     * 各董事等级预算（池 × 10/7/5/3/2%，合计 27%）→ 同级平均分配 →
     * 生成个人结算明细与批次（公司留存 = 池 × 73%）。
     * uk(period, scope) 保证同账期不重复结算。
     *
     * @param period 账期（yyyy-MM）
     * @return 结算批次
     */
    TradeProductBonusSettleBatchDO settleMonth(String period);

    /**
     * 发放指定批次的商品奖金到会员钱包余额（幂等）
     *
     * @param batchNo 批次号
     */
    void payBatch(String batchNo);

    /**
     * 获得结算批次分页
     *
     * @param pageReqVO 分页查询
     * @return 结算批次分页
     */
    PageResult<TradeProductBonusSettleBatchDO> getBatchPage(TradeProductBonusSettleBatchPageReqVO pageReqVO);

    /**
     * 获得结算批次（不存在抛业务异常）
     *
     * @param batchNo 批次号
     * @return 结算批次
     */
    TradeProductBonusSettleBatchDO getBatchByBatchNo(String batchNo);

    /**
     * 获得批次的个人结算明细列表
     *
     * @param batchNo 批次号
     * @return 个人结算明细列表
     */
    List<TradeProductBonusSettleDetailDO> getDetailListByBatchNo(String batchNo);

}

package cn.sailstack.qdp.module.member.service.bonus.settle;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle.MemberBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusSettleBatchDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusSettleDetailDO;

import java.util.List;

/**
 * 会员礼包奖金结算 Service 接口
 *
 * 与商品奖金（trade 侧）独立核算、独立批次、独立明细、独立对账。
 *
 * @author sail
 */
public interface MemberBonusSettleService {

    /**
     * 执行指定账期的礼包奖金月度结算
     *
     * 流程：董事资格核验 → 帮扶奖计算 → 礼包董事分红计算（收入口径未配置则跳过并记录）
     * → 汇总待结算明细生成个人结算明细 → 批次落库（含规则快照）。
     * uk(period, scope) 保证同账期不重复结算；重复执行直接抛出业务异常。
     *
     * @param period          账期（yyyy-MM）
     * @param dividendPoolFen 礼包董事分红池（分）；收入口径 MANUAL 时必传，DISABLED 时忽略
     * @return 结算批次
     */
    MemberBonusSettleBatchDO settleMonth(String period, Integer dividendPoolFen);

    /**
     * 发放指定批次的奖金到会员钱包余额
     *
     * 幂等：结算个人明细 uk(batch_no, user_id) + 钱包流水 bizId(batchNo:userId) 双重保障；
     * 个人合计非正数（负数为退款冲抵差额）不发放、不扣减，保留财务处理。
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
    PageResult<MemberBonusSettleBatchDO> getBatchPage(MemberBonusSettleBatchPageReqVO pageReqVO);

    /**
     * 获得结算批次
     *
     * @param batchNo 批次号
     * @return 结算批次
     */
    MemberBonusSettleBatchDO getBatchByBatchNo(String batchNo);

    /**
     * 获得批次的个人结算明细列表
     *
     * @param batchNo 批次号
     * @return 个人结算明细列表
     */
    List<MemberBonusSettleDetailDO> getDetailListByBatchNo(String batchNo);

}

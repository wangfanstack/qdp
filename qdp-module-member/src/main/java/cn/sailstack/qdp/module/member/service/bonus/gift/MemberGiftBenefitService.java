package cn.sailstack.qdp.module.member.service.bonus.gift;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftEffectReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftPrepareRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit.MemberGiftBenefitPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftBenefitDO;

import java.util.List;

/**
 * 会员礼包权益 Service 接口
 *
 * @author sail
 */
public interface MemberGiftBenefitService {

    /**
     * 下单前准备：校验档位（仅可购买更高档）并计算应付金额（全价或补差价）
     *
     * @param userId  会员编号
     * @param giftId  礼包编号
     * @return 准备结果（含差价）
     */
    MemberGiftPrepareRespDTO prepareGiftBuy(Long userId, Long giftId);

    /**
     * 礼包订单支付成功 → 权益生效并计奖（直推奖 + 增量奖）
     *
     * 幂等：uk(order_id)；重复回调直接返回。
     * 并发：有效直推顺序通过锁定推荐人关系行（FOR UPDATE）串行分配；
     * 第 2 名有效直推达成时在同一事务内完成老板身份生效、市场创建与归属历史。
     *
     * @param reqDTO 生效请求
     */
    void effectGiftOrder(MemberGiftEffectReqDTO reqDTO);

    /**
     * 礼包订单退款成功 → 冲减 PV 与对应奖金
     *
     * 幂等：按退款业务单号（售后单）生成冲正防重复键。
     * 已发放奖金不扣减会员现金余额，仅生成可追溯的冲正/追偿记录（策略配置化）。
     *
     * @param reqDTO 退款请求
     */
    void refundGiftOrder(MemberGiftRefundReqDTO reqDTO);

    /**
     * 获得会员当前累计已购金额（分）= SUM(生效记录 pay_amount - reversal_amount)
     *
     * @param userId 会员编号
     * @return 累计已购金额（分）
     */
    Integer getPaidAmount(Long userId);

    /**
     * 获得会员权益记录列表
     *
     * @param userId 会员编号
     * @return 权益记录列表
     */
    List<MemberGiftBenefitDO> getBenefitListByUserId(Long userId);

    /**
     * 获得权益记录分页
     *
     * @param pageReqVO 分页查询
     * @return 权益记录分页
     */
    PageResult<MemberGiftBenefitDO> getBenefitPage(MemberGiftBenefitPageReqVO pageReqVO);

}

package cn.sailstack.qdp.module.member.api.bonus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 礼包订单退款冲正 Request DTO
 *
 * trade 模块在礼包订单售后退款成功后调用 member 冲减 PV 与对应奖金
 *
 * @author sail
 */
@Data
public class MemberGiftRefundReqDTO {

    /**
     * 来源订单编号
     */
    @NotNull(message = "订单编号不能为空")
    private Long orderId;
    /**
     * 购买会员编号
     */
    @NotNull(message = "会员编号不能为空")
    private Long userId;
    /**
     * 本次退款金额（分）
     */
    @NotNull(message = "退款金额不能为空")
    private Integer refundAmount;
    /**
     * 退款业务单号（售后单编号；冲正幂等键组成部分）
     */
    @NotNull(message = "退款业务单号不能为空")
    private String refundBizId;
    /**
     * 退款原因
     */
    private String reason;

}

package cn.sailstack.qdp.module.member.api.bonus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 礼包订单权益生效 Request DTO
 *
 * trade 模块在礼包订单支付成功后（afterPayOrder）调用 member 生效权益并计奖
 *
 * @author sail
 */
@Data
public class MemberGiftEffectReqDTO {

    /**
     * 来源订单编号
     */
    @NotNull(message = "订单编号不能为空")
    private Long orderId;
    /**
     * 来源订单流水号
     */
    private String orderNo;
    /**
     * 购买会员编号
     */
    @NotNull(message = "会员编号不能为空")
    private Long userId;
    /**
     * 订单关联的礼包 SPU 编号
     */
    @NotNull(message = "SPU 编号不能为空")
    private Long spuId;
    /**
     * 实际支付金额（分，新购为全价、升级为补差价）
     */
    @NotNull(message = "支付金额不能为空")
    private Integer payAmount;
    /**
     * 支付成功时间
     */
    @NotNull(message = "支付时间不能为空")
    private LocalDateTime payTime;

}

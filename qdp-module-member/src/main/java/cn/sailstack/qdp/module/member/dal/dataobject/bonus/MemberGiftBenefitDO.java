package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员礼包权益记录 DO
 *
 * 每次新购/补差升级/退款冲减各记一条；uk(order_id) 保证支付回调幂等。
 * 会员当前累计已购金额 = SUM(status=生效 的 pay_amount)。
 *
 * @author sail
 */
@TableName("member_gift_benefit")
@KeySequence("member_gift_benefit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGiftBenefitDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会员编号
     */
    private Long userId;
    /**
     * 礼包编号
     */
    private Long giftId;
    /**
     * 来源订单编号（唯一）
     */
    private Long orderId;
    /**
     * 来源订单流水号
     */
    private String orderNo;
    /**
     * 类型
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberGiftBenefitTypeEnum}
     */
    private Integer type;
    /**
     * 实际支付金额（分，冲减记录为负数）
     */
    private Integer payAmount;
    /**
     * 升级前累计已购金额（分，快照）
     */
    private Integer paidAmount;
    /**
     * 本次补差金额（分，新购等于支付金额）
     */
    private Integer addedAmount;
    /**
     * 新增 PV（元，四舍五入取整）
     */
    private Integer pv;
    /**
     * 累计冲减支付金额（分，退款冲正用）
     */
    private Integer reversalAmount;
    /**
     * 累计冲减 PV（元）
     */
    private Integer reversalPv;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberGiftBenefitStatusEnum}
     */
    private Integer status;
    /**
     * 生效时间（支付成功时间）
     */
    private LocalDateTime effectTime;

}

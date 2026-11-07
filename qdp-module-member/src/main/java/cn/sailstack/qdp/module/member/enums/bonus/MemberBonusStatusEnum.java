package cn.sailstack.qdp.module.member.enums.bonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员礼包奖金 - 奖金状态枚举
 *
 * 待计算状态由订单生命周期隐含（订单未生效不产生明细），明细落库即待结算。
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberBonusStatusEnum {

    WAIT_SETTLE(10, "待结算"),
    SETTLED(20, "已结算"),
    PAID(30, "已发放"),
    REVERSED(40, "已冲正"),
    ;

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 说明
     */
    private final String name;

}

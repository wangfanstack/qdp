package cn.sailstack.qdp.module.member.enums.bonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员礼包奖金 - 结算个人明细发放状态枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberBonusPayStatusEnum {

    WAIT_PAY(10, "待发放"),
    PAID(20, "已发放"),
    PAY_FAIL(30, "发放失败"),
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

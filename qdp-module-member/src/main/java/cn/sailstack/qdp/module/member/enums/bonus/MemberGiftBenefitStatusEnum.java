package cn.sailstack.qdp.module.member.enums.bonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员礼包权益 - 状态枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberGiftBenefitStatusEnum {

    EFFECT(1, "生效"),
    REVERSED(2, "已冲减"),
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

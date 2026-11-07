package cn.sailstack.qdp.module.member.enums.bonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员礼包奖金 - 结算批次/发放状态枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberBonusSettleStatusEnum {

    /** 批次已生成（明细已结算、待发放） */
    SETTLED(10, "已结算"),
    /** 批次奖金已发放入账 */
    PAID(20, "已发放"),
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

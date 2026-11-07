package cn.sailstack.qdp.module.member.enums.bonus;

import cn.sailstack.qdp.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 会员礼包权益 - 记录类型枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberGiftBenefitTypeEnum implements ArrayValuable<Integer> {

    NEW(1, "新购"),
    UPGRADE(2, "补差升级"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MemberGiftBenefitTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 说明
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

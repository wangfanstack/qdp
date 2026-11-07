package cn.sailstack.qdp.module.member.enums.bonus;

import cn.sailstack.qdp.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 会员礼包奖金 - 奖励类型枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberBonusTypeEnum implements ArrayValuable<Integer> {

    DIRECT(1, "直推奖"),
    INCREMENT(2, "市场增量奖"),
    HELP(3, "第一代帮扶奖"),
    DIRECTOR(4, "礼包董事分红"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MemberBonusTypeEnum::getType).toArray(Integer[]::new);

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

package cn.sailstack.qdp.module.member.enums.bonus;

import cn.sailstack.qdp.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 会员董事 - 董事等级枚举
 *
 * 各等级分红比例从 {@link cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO} 读取，
 * 此处仅定义等级值；比例不落枚举，保证历史奖金按规则版本追溯。
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberDirectorLevelEnum implements ArrayValuable<Integer> {

    ONE(1, "一星董事"),
    TWO(2, "二星董事"),
    THREE(3, "三星董事"),
    FOUR(4, "四星董事"),
    FIVE(5, "五星董事"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MemberDirectorLevelEnum::getLevel).toArray(Integer[]::new);

    /**
     * 等级
     */
    private final Integer level;
    /**
     * 说明
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

package cn.sailstack.qdp.module.trade.enums.productbonus;

import cn.sailstack.qdp.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品奖金 - 奖励类型枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum TradeProductBonusTypeEnum implements ArrayValuable<Integer> {

    DIFFERENCE(1, "折扣差额奖"),
    DIRECTOR(2, "商品董事加权分红"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(TradeProductBonusTypeEnum::getType).toArray(Integer[]::new);

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

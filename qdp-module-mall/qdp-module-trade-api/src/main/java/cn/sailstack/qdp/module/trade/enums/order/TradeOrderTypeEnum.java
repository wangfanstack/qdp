package cn.sailstack.qdp.module.trade.enums.order;

import cn.sailstack.qdp.framework.common.util.object.ObjectUtils;
import cn.sailstack.qdp.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 交易订单 - 类型
 *
 * @author Sin
 */
@RequiredArgsConstructor
@Getter
public enum TradeOrderTypeEnum implements ArrayValuable<Integer> {

    NORMAL(0, "普通订单"),
    SECKILL(1, "秒杀订单"),
    BARGAIN(2, "砍价订单"),
    COMBINATION(3, "拼团订单"),
    POINT(4, "积分商城"),
    GIFT(5, "会员礼包订单"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(TradeOrderTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isNormal(Integer type) {
        return ObjectUtils.equal(type, NORMAL.getType());
    }

    public static boolean isSeckill(Integer type) {
        return ObjectUtils.equal(type, SECKILL.getType());
    }

    public static boolean isBargain(Integer type) {
        return ObjectUtils.equal(type, BARGAIN.getType());
    }

    public static boolean isCombination(Integer type) {
        return ObjectUtils.equal(type, COMBINATION.getType());
    }

    public static boolean isPoint(Integer type) {
        return ObjectUtils.equal(type, POINT.getType());
    }

    public static boolean isGift(Integer type) {
        return ObjectUtils.equal(type, GIFT.getType());
    }

}

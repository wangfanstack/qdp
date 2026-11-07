package cn.sailstack.qdp.module.trade.enums.productbonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品奖金 - 状态枚举（与礼包奖金状态保持一致的数值语义）
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum TradeProductBonusStatusEnum {

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

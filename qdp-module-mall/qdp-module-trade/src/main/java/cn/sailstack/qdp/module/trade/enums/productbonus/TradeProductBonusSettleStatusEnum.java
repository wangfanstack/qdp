package cn.sailstack.qdp.module.trade.enums.productbonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品奖金 - 结算批次/个人明细发放状态枚举
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum TradeProductBonusSettleStatusEnum {

    SETTLED(10, "已结算"),
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

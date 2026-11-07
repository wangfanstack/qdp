package cn.sailstack.qdp.module.member.api.bonus.dto;

import lombok.Data;

/**
 * 礼包购买准备（下单前校验与差价计算）Response DTO
 *
 * @author sail
 */
@Data
public class MemberGiftPrepareRespDTO {

    /**
     * 礼包编号
     */
    private Long giftId;
    /**
     * 礼包名称
     */
    private String giftName;
    /**
     * 礼包销售价格（分）
     */
    private Integer giftPrice;
    /**
     * 当前累计已购金额（分）
     */
    private Integer paidAmount;
    /**
     * 应付金额（分，= 礼包价格 - 已购金额）
     */
    private Integer payableAmount;
    /**
     * 是否补差升级
     */
    private Boolean upgrade;
    /**
     * 是否已绑定推荐人
     */
    private Boolean referrerBound;

}

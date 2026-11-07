package cn.sailstack.qdp.module.member.api.bonus.dto;

import lombok.Data;

/**
 * 会员礼包配置 Response DTO（跨模块精简版）
 *
 * @author sail
 */
@Data
public class MemberGiftConfigRespDTO {

    /**
     * 礼包编号
     */
    private Long id;
    /**
     * 礼包名称
     */
    private String name;
    /**
     * 会员等级名称
     */
    private String levelName;
    /**
     * 销售价格（分）
     */
    private Integer price;
    /**
     * PV 比例（百分比）
     */
    private Integer pvPercent;
    /**
     * 关联商品 SPU 编号
     */
    private Long spuId;
    /**
     * 状态（0开启 1停用）
     */
    private Integer status;

}

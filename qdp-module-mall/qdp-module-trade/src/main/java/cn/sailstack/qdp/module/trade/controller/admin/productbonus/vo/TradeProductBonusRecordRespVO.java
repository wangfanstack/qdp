package cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 商品奖金明细 Response VO")
@Data
public class TradeProductBonusRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "获奖会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "奖励类型（1折扣差额奖 2商品董事加权分红）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer bonusType;

    @Schema(description = "购买会员编号（差额奖）", example = "1024")
    private Long buyerUserId;

    @Schema(description = "来源订单编号", example = "100")
    private Long orderId;

    @Schema(description = "来源订单项编号", example = "200")
    private Long orderItemId;

    @Schema(description = "购买会员折扣（百分比）", example = "60")
    private Integer buyerDiscount;

    @Schema(description = "直接上级折扣（百分比）", example = "40")
    private Integer superiorDiscount;

    @Schema(description = "计奖基数（分）", example = "10000")
    private Integer baseAmount;

    @Schema(description = "奖励比例（百分比，差额奖=折扣差）", example = "20")
    private Integer rate;

    @Schema(description = "奖金金额（分，冲正为负）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2000")
    private Integer amount;

    @Schema(description = "董事等级（商品董事加权分红）", example = "1")
    private Integer directorLevel;

    @Schema(description = "所属账期", example = "2026-11")
    private String period;

    @Schema(description = "规则版本", example = "1")
    private Integer ruleVersion;

    @Schema(description = "奖金状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "结算批次号", example = "PB202611")
    private String settleBatchNo;

    @Schema(description = "冲正关联的原奖金编号", example = "1")
    private Long reversalOfId;

    @Schema(description = "冲正原因", example = "订单项退款冲回")
    private String reversalReason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

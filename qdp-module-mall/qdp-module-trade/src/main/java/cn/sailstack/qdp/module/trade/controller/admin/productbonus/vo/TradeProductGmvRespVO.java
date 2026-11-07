package cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员商品 GMV 记录 Response VO")
@Data
public class TradeProductGmvRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long orderId;

    @Schema(description = "订单流水号", example = "20261108...")
    private String orderNo;

    @Schema(description = "购买会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "订单实付金额（分）", example = "10000")
    private Integer payAmount;

    @Schema(description = "累计退款金额（分）", example = "0")
    private Integer refundAmount;

    @Schema(description = "有效 GMV（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    private Integer gmvAmount;

    @Schema(description = "账期", example = "2026-11")
    private String period;

    @Schema(description = "订单完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

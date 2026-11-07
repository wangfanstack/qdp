package cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 商品奖金结算批次 Response VO（含个人明细）")
@Data
public class TradeProductBonusSettleBatchRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "批次号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PB202611")
    private String batchNo;

    @Schema(description = "账期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-11")
    private String period;

    @Schema(description = "奖金体系", example = "PRODUCT")
    private String scope;

    @Schema(description = "状态（10已结算 20已发放）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "当月有效 GMV（分）", example = "100000000")
    private Integer gmvAmount;

    @Schema(description = "商品奖金池（分）", example = "10000000")
    private Integer bonusPool;

    @Schema(description = "公司留存（分）", example = "7300000")
    private Integer companyAmount;

    @Schema(description = "实际分配总金额（分）", example = "2700000")
    private Integer totalAmount;

    @Schema(description = "获奖人数", example = "6")
    private Integer userCount;

    @Schema(description = "结算时规则配置快照（JSON）")
    private String configSnapshot;

    @Schema(description = "备注（空档等级说明等）")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "个人结算明细列表")
    private List<Detail> details;

    @Schema(description = "个人结算明细")
    @Data
    public static class Detail {

        @Schema(description = "会员编号", example = "2048")
        private Long userId;

        @Schema(description = "个人奖金合计（分）", example = "10000")
        private Integer totalAmount;

        @Schema(description = "发放状态（10待发放 20已发放 30失败）", example = "10")
        private Integer payStatus;

        @Schema(description = "钱包入账业务单号", example = "PB202611:2048")
        private String walletBizId;

        @Schema(description = "发放时间")
        private LocalDateTime payTime;
    }

}

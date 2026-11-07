package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 礼包奖金结算批次 Response VO（含个人明细）")
@Data
public class MemberBonusSettleBatchRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "批次号", requiredMode = Schema.RequiredMode.REQUIRED, example = "GB202611")
    private String batchNo;

    @Schema(description = "账期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-11")
    private String period;

    @Schema(description = "奖金体系", example = "GIFT")
    private String scope;

    @Schema(description = "状态（10已结算 20已发放）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "本次礼包董事分红池（分）", example = "10000000")
    private Integer dividendPool;

    @Schema(description = "批次奖金总金额（分）", example = "500000")
    private Integer totalAmount;

    @Schema(description = "获奖人数", example = "8")
    private Integer userCount;

    @Schema(description = "结算时规则配置快照（JSON）")
    private String configSnapshot;

    @Schema(description = "备注（跳过项说明）", example = "分红池收入口径未配置，跳过董事分红")
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

        @Schema(description = "个人奖金合计（分）", example = "50000")
        private Integer totalAmount;

        @Schema(description = "发放状态（10待发放 20已发放 30失败）", example = "10")
        private Integer payStatus;

        @Schema(description = "钱包入账业务单号", example = "GB202611:2048")
        private String walletBizId;

        @Schema(description = "发放时间")
        private LocalDateTime payTime;
    }

}

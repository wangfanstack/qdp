package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 礼包奖金明细 Response VO")
@Data
public class MemberBonusRecordRespVO {

    @Schema(description = "奖金编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "获奖会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "奖励类型（1直推奖 2市场增量奖 3第一代帮扶奖 4礼包董事分红）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer bonusType;

    @Schema(description = "来源会员编号", example = "1024")
    private Long sourceUserId;

    @Schema(description = "来源订单编号", example = "100")
    private Long sourceOrderId;

    @Schema(description = "计奖基数 PV（元）", example = "23880")
    private Integer basePv;

    @Schema(description = "计奖基数金额（分）", example = "2388000")
    private Integer baseAmount;

    @Schema(description = "奖励比例（百分比）", example = "55")
    private Integer rate;

    @Schema(description = "奖金金额（分，冲正为负）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1313400")
    private Integer amount;

    @Schema(description = "市场编号（增量奖）", example = "100")
    private Long marketId;

    @Schema(description = "董事等级（礼包董事分红）", example = "1")
    private Integer directorLevel;

    @Schema(description = "所属账期", example = "2026-11")
    private String period;

    @Schema(description = "规则版本", example = "1")
    private Integer ruleVersion;

    @Schema(description = "奖金状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "结算批次号", example = "GB202611")
    private String settleBatchNo;

    @Schema(description = "冲正关联的原奖金编号", example = "1")
    private Long reversalOfId;

    @Schema(description = "冲正/追偿原因", example = "订单退款")
    private String reversalReason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 奖金规则配置新增/修改 Request VO（单行）")
@Data
public class MemberBonusConfigSaveReqVO {

    @Schema(description = "PV 比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    @NotNull(message = "PV 比例不能为空")
    @Min(value = 1, message = "PV 比例必须在 (0, 100]")
    @Max(value = 100, message = "PV 比例必须在 (0, 100]")
    private Integer pvPercent;

    @Schema(description = "直推奖比例：第 1、2 名（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    @NotNull(message = "直推奖比例（前两名）不能为空")
    private Integer directFirstRate;

    @Schema(description = "直推奖比例：第 3 名及以后（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "55")
    @NotNull(message = "直推奖比例（第三名起）不能为空")
    private Integer directAfterRate;

    @Schema(description = "市场增量奖比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25")
    @NotNull(message = "市场增量奖比例不能为空")
    private Integer incrementRate;

    @Schema(description = "第一代帮扶奖比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "帮扶奖比例不能为空")
    private Integer helpRate;

    @Schema(description = "一星董事礼包分红比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "一星董事比例不能为空")
    private Integer directorOneRate;

    @Schema(description = "二星董事礼包分红比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "7")
    @NotNull(message = "二星董事比例不能为空")
    private Integer directorTwoRate;

    @Schema(description = "三星董事礼包分红比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "三星董事比例不能为空")
    private Integer directorThreeRate;

    @Schema(description = "四星董事礼包分红比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "四星董事比例不能为空")
    private Integer directorFourRate;

    @Schema(description = "五星董事礼包分红比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "五星董事比例不能为空")
    private Integer directorFiveRate;

    @Schema(description = "礼包董事分红池占公司当月收入比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    @NotNull(message = "礼包董事分红池比例不能为空")
    private Integer giftPoolPercent;

    @Schema(description = "礼包分红池公司收入口径（DISABLED 不结算 / MANUAL 手工录入），【待业务确认】", example = "DISABLED")
    private String giftIncomeSource;

    @Schema(description = "董事资格所需礼包编号，【待业务确认】", example = "4")
    private Long directorQualifyGiftId;

    @Schema(description = "一星董事晋升：推荐该礼包数量，【待业务确认】", example = "6")
    @NotNull(message = "一星晋升数量不能为空")
    private Integer directorOneStarCount;

    @Schema(description = "一星晋升关系范围（DIRECT/TEAM），【待业务确认】", example = "DIRECT")
    private String directorQualifyScope;

    @Schema(description = "培养一星董事关系范围（DIRECT/TEAM），【待业务确认】", example = "DIRECT")
    private String directorTraineeScope;

    @Schema(description = "二星：培养一星董事数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "二星培养数量不能为空")
    private Integer directorTwoStarTrainees;

    @Schema(description = "三星：培养一星董事数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "三星培养数量不能为空")
    private Integer directorThreeStarTrainees;

    @Schema(description = "四星：培养一星董事数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    @NotNull(message = "四星培养数量不能为空")
    private Integer directorFourStarTrainees;

    @Schema(description = "五星：培养一星董事数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "6")
    @NotNull(message = "五星培养数量不能为空")
    private Integer directorFiveStarTrainees;

    @Schema(description = "空档等级预算处理策略（KEEP 留存公司），【待业务确认】", example = "KEEP")
    private String emptyLevelPolicy;

    @Schema(description = "订单观察期（天，0 表示支付成功即生效）", example = "0")
    @NotNull(message = "观察期不能为空")
    private Integer observeDays;

    @Schema(description = "月度结算日（1-31，未配置不自动结算），【待业务确认】", example = "1")
    @Min(value = 1, message = "结算日必须在 1-31")
    @Max(value = 31, message = "结算日必须在 1-31")
    private Integer settleDay;

    @Schema(description = "奖金发放日（1-31），【待业务确认】", example = "10")
    @Min(value = 1, message = "发放日必须在 1-31")
    @Max(value = 31, message = "发放日必须在 1-31")
    private Integer payDay;

    @Schema(description = "金额舍入模式（HALF_UP/FLOOR），【待业务确认】", example = "HALF_UP")
    private String roundingMode;

    @Schema(description = "已发放奖金追偿策略（RECORD_ONLY），【待业务确认】", example = "RECORD_ONLY")
    private String reversalPolicy;

    @Schema(description = "商品奖金池占 GMV 比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "商品奖金池比例不能为空")
    private Integer productPoolPercent;

    @Schema(description = "商品 GMV 是否包含运费（0 不含 1 含），【待业务确认】", example = "0")
    private Integer gmvIncludeDelivery;

    @Schema(description = "备注", example = "比例取自需求规格说明书 V1.0")
    private String remark;

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 奖金规则配置 Response VO（单行）")
@Data
public class MemberBonusConfigRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "PV 比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    private Integer pvPercent;

    @Schema(description = "直推奖比例：第 1、2 名（百分比）", example = "30")
    private Integer directFirstRate;

    @Schema(description = "直推奖比例：第 3 名及以后（百分比）", example = "55")
    private Integer directAfterRate;

    @Schema(description = "市场增量奖比例（百分比）", example = "25")
    private Integer incrementRate;

    @Schema(description = "第一代帮扶奖比例（百分比）", example = "20")
    private Integer helpRate;

    @Schema(description = "一星董事礼包分红比例（百分比）", example = "10")
    private Integer directorOneRate;

    @Schema(description = "二星董事礼包分红比例（百分比）", example = "7")
    private Integer directorTwoRate;

    @Schema(description = "三星董事礼包分红比例（百分比）", example = "5")
    private Integer directorThreeRate;

    @Schema(description = "四星董事礼包分红比例（百分比）", example = "3")
    private Integer directorFourRate;

    @Schema(description = "五星董事礼包分红比例（百分比）", example = "2")
    private Integer directorFiveRate;

    @Schema(description = "礼包董事分红池占公司当月收入比例（百分比）", example = "60")
    private Integer giftPoolPercent;

    @Schema(description = "礼包分红池公司收入口径，【待业务确认】", example = "DISABLED")
    private String giftIncomeSource;

    @Schema(description = "董事资格所需礼包编号，【待业务确认】", example = "4")
    private Long directorQualifyGiftId;

    @Schema(description = "一星董事晋升：推荐该礼包数量，【待业务确认】", example = "6")
    private Integer directorOneStarCount;

    @Schema(description = "一星晋升关系范围，【待业务确认】", example = "DIRECT")
    private String directorQualifyScope;

    @Schema(description = "培养一星董事关系范围，【待业务确认】", example = "DIRECT")
    private String directorTraineeScope;

    @Schema(description = "二星：培养一星董事数量", example = "1")
    private Integer directorTwoStarTrainees;

    @Schema(description = "三星：培养一星董事数量", example = "2")
    private Integer directorThreeStarTrainees;

    @Schema(description = "四星：培养一星董事数量", example = "4")
    private Integer directorFourStarTrainees;

    @Schema(description = "五星：培养一星董事数量", example = "6")
    private Integer directorFiveStarTrainees;

    @Schema(description = "空档等级预算处理策略，【待业务确认】", example = "KEEP")
    private String emptyLevelPolicy;

    @Schema(description = "订单观察期（天）", example = "0")
    private Integer observeDays;

    @Schema(description = "月度结算日，【待业务确认】", example = "1")
    private Integer settleDay;

    @Schema(description = "奖金发放日，【待业务确认】", example = "10")
    private Integer payDay;

    @Schema(description = "金额舍入模式，【待业务确认】", example = "HALF_UP")
    private String roundingMode;

    @Schema(description = "已发放奖金追偿策略，【待业务确认】", example = "RECORD_ONLY")
    private String reversalPolicy;

    @Schema(description = "商品奖金池占 GMV 比例（百分比）", example = "10")
    private Integer productPoolPercent;

    @Schema(description = "商品 GMV 是否包含运费，【待业务确认】", example = "0")
    private Integer gmvIncludeDelivery;

    @Schema(description = "规则版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer version;

    @Schema(description = "备注", example = "初始版本")
    private String remark;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 礼包奖金明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberBonusRecordPageReqVO extends PageParam {

    @Schema(description = "获奖会员编号", example = "2048")
    private Long userId;

    @Schema(description = "奖励类型（1直推奖 2市场增量奖 3第一代帮扶奖 4礼包董事分红）", example = "1")
    private Integer bonusType;

    @Schema(description = "奖金状态（10待结算 20已结算 30已发放 40已冲正）", example = "10")
    private Integer status;

    @Schema(description = "所属账期（yyyy-MM）", example = "2026-11")
    private String period;

    @Schema(description = "来源会员编号", example = "1024")
    private Long sourceUserId;

    @Schema(description = "结算批次号", example = "GB202611")
    private String settleBatchNo;

}

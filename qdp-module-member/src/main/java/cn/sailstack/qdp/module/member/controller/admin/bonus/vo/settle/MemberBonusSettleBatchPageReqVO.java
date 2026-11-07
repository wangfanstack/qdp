package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 礼包奖金结算批次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberBonusSettleBatchPageReqVO extends PageParam {

    @Schema(description = "账期（yyyy-MM）", example = "2026-11")
    private String period;

    @Schema(description = "状态（10已结算 20已发放）", example = "10")
    private Integer status;

}

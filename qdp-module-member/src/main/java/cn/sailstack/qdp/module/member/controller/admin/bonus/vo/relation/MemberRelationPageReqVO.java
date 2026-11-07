package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 会员推荐关系分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberRelationPageReqVO extends PageParam {

    @Schema(description = "会员编号", example = "2048")
    private Long userId;

    @Schema(description = "推荐人编号", example = "1024")
    private Long referrerId;

    @Schema(description = "市场编号", example = "100")
    private Long marketId;

}

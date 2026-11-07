package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 会员董事资格分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberDirectorPageReqVO extends PageParam {

    @Schema(description = "会员编号", example = "2048")
    private Long userId;

    @Schema(description = "董事等级（1-5）", example = "1")
    private Integer level;

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员董事资格 Response VO")
@Data
public class MemberDirectorRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "董事等级（1一星 2二星 3三星 4四星 5五星）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer level;

    @Schema(description = "资格达成时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime qualifyTime;

    @Schema(description = "状态（1有效 2撤销）", example = "1")
    private Integer status;

    @Schema(description = "达成说明", example = "推荐6个董事资格礼包(DIRECT口径)")
    private String source;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

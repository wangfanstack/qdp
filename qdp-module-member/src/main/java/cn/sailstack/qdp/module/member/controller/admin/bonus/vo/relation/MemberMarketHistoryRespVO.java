package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员市场归属变更历史 Response VO")
@Data
public class MemberMarketHistoryRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "原市场编号", example = "100")
    private Long fromMarketId;

    @Schema(description = "新市场编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
    private Long toMarketId;

    @Schema(description = "生效时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime effectTime;

    @Schema(description = "变更原因", example = "第2名有效直推达成，取得老板身份")
    private String reason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

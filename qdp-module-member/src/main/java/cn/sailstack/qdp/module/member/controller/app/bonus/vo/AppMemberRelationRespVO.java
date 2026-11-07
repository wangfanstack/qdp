package cn.sailstack.qdp.module.member.controller.app.bonus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 我的推荐关系 Response VO")
@Data
public class AppMemberRelationRespVO {

    @Schema(description = "会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "推荐人编号", example = "1024")
    private Long referrerId;

    @Schema(description = "推荐关系建立时间")
    private LocalDateTime bindTime;

    @Schema(description = "首次有效购买时间")
    private LocalDateTime validBuyTime;

    @Schema(description = "有效直推顺序", example = "1")
    private Integer validSeq;

    @Schema(description = "老板身份生效时间")
    private LocalDateTime bossTime;

    @Schema(description = "当前市场编号", example = "100")
    private Long marketId;

}

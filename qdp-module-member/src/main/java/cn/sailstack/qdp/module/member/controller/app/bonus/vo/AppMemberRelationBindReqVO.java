package cn.sailstack.qdp.module.member.controller.app.bonus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 绑定推荐人 Request VO")
@Data
public class AppMemberRelationBindReqVO {

    @Schema(description = "推荐人会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "推荐人编号不能为空")
    private Long referrerId;

}

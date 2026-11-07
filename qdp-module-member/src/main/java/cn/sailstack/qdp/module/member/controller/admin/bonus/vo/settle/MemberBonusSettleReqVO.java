package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "管理后台 - 手动触发礼包奖金月度结算 Request VO")
@Data
public class MemberBonusSettleReqVO {

    @Schema(description = "账期（yyyy-MM）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-11")
    @NotBlank(message = "账期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "账期格式必须为 yyyy-MM")
    private String period;

    @Schema(description = "礼包董事分红池（分）；收入口径为 MANUAL 时必填", example = "10000000")
    private Integer dividendPool;

}

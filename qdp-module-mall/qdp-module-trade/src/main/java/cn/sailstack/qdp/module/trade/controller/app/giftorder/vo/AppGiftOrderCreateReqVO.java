package cn.sailstack.qdp.module.trade.controller.app.giftorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 会员礼包开通/升级下单 Request VO")
@Data
public class AppGiftOrderCreateReqVO {

    @Schema(description = "礼包编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "礼包编号不能为空")
    private Long giftId;

    @Schema(description = "推荐人编号（首次开通且未绑定推荐人时必填）", example = "1024")
    private Long referrerId;

    @Schema(description = "收件地址编号（快递配送；不传使用默认地址）", example = "1")
    private Long addressId;

}

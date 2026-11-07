package cn.sailstack.qdp.module.trade.controller.app.giftorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 会员礼包下单 Response VO")
@Data
public class AppGiftOrderCreateRespVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long orderId;

    @Schema(description = "支付单编号（payPrice 为 0 时无需支付）", example = "200")
    private Long payOrderId;

}

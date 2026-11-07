package cn.sailstack.qdp.module.trade.controller.app.giftorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 会员礼包档位 Response VO（含当前档位与升级差价）")
@Data
public class AppGiftRespVO {

    @Schema(description = "礼包编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long giftId;

    @Schema(description = "礼包名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "会员礼包")
    private String name;

    @Schema(description = "会员等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "体验官")
    private String levelName;

    @Schema(description = "销售价格（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "59800")
    private Integer price;

    @Schema(description = "PV 比例（百分比）", example = "60")
    private Integer pvPercent;

    @Schema(description = "对应 PV（元）", example = "359")
    private Integer pv;

    @Schema(description = "关联 SPU 编号", example = "1024")
    private Long spuId;

    @Schema(description = "图标", example = "http://xx/icon.png")
    private String icon;

    @Schema(description = "当前会员应付金额（分，新购=全价；升级=补差价）", requiredMode = Schema.RequiredMode.REQUIRED, example = "338200")
    private Integer payableAmount;

    @Schema(description = "是否补差升级", example = "false")
    private Boolean upgrade;

    @Schema(description = "是否可购买（低于等于当前档位时 false）", example = "true")
    private Boolean purchasable;

}

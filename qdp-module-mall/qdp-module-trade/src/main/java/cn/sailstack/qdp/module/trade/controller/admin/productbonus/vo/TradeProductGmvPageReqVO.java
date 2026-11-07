package cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 会员商品 GMV 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TradeProductGmvPageReqVO extends PageParam {

    @Schema(description = "购买会员编号", example = "2048")
    private Long userId;

    @Schema(description = "账期（yyyy-MM）", example = "2026-11")
    private String period;

}

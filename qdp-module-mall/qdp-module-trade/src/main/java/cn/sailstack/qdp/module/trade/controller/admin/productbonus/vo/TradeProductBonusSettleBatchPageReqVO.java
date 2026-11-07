package cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 商品奖金结算批次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TradeProductBonusSettleBatchPageReqVO extends PageParam {

    @Schema(description = "账期（yyyy-MM）", example = "2026-11")
    private String period;

    @Schema(description = "状态（10已结算 20已发放）", example = "10")
    private Integer status;

}

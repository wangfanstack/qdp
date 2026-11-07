package cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 商品奖金明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TradeProductBonusRecordPageReqVO extends PageParam {

    @Schema(description = "获奖会员编号", example = "2048")
    private Long userId;

    @Schema(description = "奖励类型（1折扣差额奖 2商品董事加权分红）", example = "1")
    private Integer bonusType;

    @Schema(description = "奖金状态（10待结算 20已结算 30已发放 40已冲正）", example = "10")
    private Integer status;

    @Schema(description = "所属账期（yyyy-MM）", example = "2026-11")
    private String period;

    @Schema(description = "购买会员编号（差额奖）", example = "1024")
    private Long buyerUserId;

}

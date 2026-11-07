package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员礼包权益记录 Response VO")
@Data
public class MemberGiftBenefitRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "礼包编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long giftId;

    @Schema(description = "来源订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long orderId;

    @Schema(description = "来源订单流水号", example = "20261108...")
    private String orderNo;

    @Schema(description = "类型（1新购 2补差升级）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "实际支付金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "398000")
    private Integer payAmount;

    @Schema(description = "升级前累计已购金额（分）", example = "59800")
    private Integer paidAmount;

    @Schema(description = "本次补差金额（分）", example = "338200")
    private Integer addedAmount;

    @Schema(description = "新增 PV（元）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2029")
    private Integer pv;

    @Schema(description = "累计冲减支付金额（分）", example = "0")
    private Integer reversalAmount;

    @Schema(description = "累计冲减 PV（元）", example = "0")
    private Integer reversalPv;

    @Schema(description = "状态（1生效 2已全额冲减）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "生效时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime effectTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(description = "管理后台 - 会员礼包配置新增/修改 Request VO")
@Data
public class MemberGiftConfigSaveReqVO {

    @Schema(description = "编号（修改时必填）", example = "1")
    private Long id;

    @Schema(description = "礼包名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "课程礼包")
    @NotBlank(message = "礼包名称不能为空")
    private String name;

    @Schema(description = "会员等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "初级代表")
    @NotBlank(message = "会员等级名称不能为空")
    private String levelName;

    @Schema(description = "销售价格（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "398000")
    @NotNull(message = "销售价格不能为空")
    @Positive(message = "销售价格必须大于 0")
    private Integer price;

    @Schema(description = "PV 比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    @NotNull(message = "PV 比例不能为空")
    private Integer pvPercent;

    @Schema(description = "关联商品 SPU 编号", example = "1024")
    private Long spuId;

    @Schema(description = "礼包图标", example = "http://xx/icon.png")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态（0开启 1停用）", example = "0")
    private Integer status;

    @Schema(description = "备注", example = "3980 课程礼包")
    private String remark;

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员礼包配置 Response VO")
@Data
public class MemberGiftConfigRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "礼包名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "课程礼包")
    private String name;

    @Schema(description = "会员等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "初级代表")
    private String levelName;

    @Schema(description = "销售价格（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "398000")
    private Integer price;

    @Schema(description = "PV 比例（百分比）", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    private Integer pvPercent;

    @Schema(description = "对应 PV（元）", example = "2388")
    private Integer pv;

    @Schema(description = "关联商品 SPU 编号", example = "1024")
    private Long spuId;

    @Schema(description = "礼包图标", example = "http://xx/icon.png")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "备注", example = "3980 课程礼包")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

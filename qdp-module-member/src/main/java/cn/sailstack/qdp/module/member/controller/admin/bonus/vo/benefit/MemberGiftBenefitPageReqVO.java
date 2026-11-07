package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 会员礼包权益记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberGiftBenefitPageReqVO extends PageParam {

    @Schema(description = "会员编号", example = "2048")
    private Long userId;

    @Schema(description = "礼包编号", example = "1")
    private Long giftId;

    @Schema(description = "类型（1新购 2补差升级）", example = "1")
    private Integer type;

    @Schema(description = "状态（1生效 2已全额冲减）", example = "1")
    private Integer status;

}

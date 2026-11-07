package cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 会员礼包配置分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberGiftConfigPageReqVO extends PageParam {

    @Schema(description = "礼包名称", example = "课程礼包")
    private String name;

    @Schema(description = "状态", example = "0")
    private Integer status;

}

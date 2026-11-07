package cn.sailstack.qdp.module.member.controller.admin.bonus.config;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.config.MemberBonusConfigRespVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.config.MemberBonusConfigSaveReqVO;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean;

@Tag(name = "管理后台 - 奖金规则配置")
@RestController
@RequestMapping("/member/bonus-config")
@Validated
public class MemberBonusConfigController {

    @Resource
    private MemberBonusConfigService bonusConfigService;

    @GetMapping("/get")
    @Operation(summary = "获得奖金规则配置（单行）")
    @PreAuthorize("@ss.hasPermission('member:bonus-config:query')")
    public CommonResult<MemberBonusConfigRespVO> getConfig() {
        return success(toBean(bonusConfigService.getRequiredConfig(), MemberBonusConfigRespVO.class));
    }

    @PutMapping("/update")
    @Operation(summary = "更新奖金规则配置（规则版本自增）")
    @PreAuthorize("@ss.hasPermission('member:bonus-config:update')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody MemberBonusConfigSaveReqVO updateReqVO) {
        bonusConfigService.updateConfig(updateReqVO);
        return success(true);
    }

}

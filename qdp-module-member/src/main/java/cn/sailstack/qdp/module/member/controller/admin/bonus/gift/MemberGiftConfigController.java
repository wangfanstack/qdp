package cn.sailstack.qdp.module.member.controller.admin.bonus.gift;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift.*;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftConfigDO;
import cn.sailstack.qdp.module.member.service.bonus.gift.MemberGiftConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean;

@Tag(name = "管理后台 - 会员礼包配置")
@RestController
@RequestMapping("/member/gift-config")
@Validated
public class MemberGiftConfigController {

    @Resource
    private MemberGiftConfigService giftConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建礼包配置")
    @PreAuthorize("@ss.hasPermission('member:gift-config:create')")
    public CommonResult<Long> createGiftConfig(@Valid @RequestBody MemberGiftConfigSaveReqVO createReqVO) {
        return success(giftConfigService.createGiftConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新礼包配置")
    @PreAuthorize("@ss.hasPermission('member:gift-config:update')")
    public CommonResult<Boolean> updateGiftConfig(@Valid @RequestBody MemberGiftConfigSaveReqVO updateReqVO) {
        giftConfigService.updateGiftConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除礼包配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('member:gift-config:delete')")
    public CommonResult<Boolean> deleteGiftConfig(@RequestParam("id") Long id) {
        giftConfigService.deleteGiftConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得礼包配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('member:gift-config:query')")
    public CommonResult<MemberGiftConfigRespVO> getGiftConfig(@RequestParam("id") Long id) {
        MemberGiftConfigDO config = giftConfigService.getGiftConfig(id);
        return success(toBean(config, MemberGiftConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得礼包配置分页")
    @PreAuthorize("@ss.hasPermission('member:gift-config:query')")
    public CommonResult<PageResult<MemberGiftConfigRespVO>> getGiftConfigPage(@Valid MemberGiftConfigPageReqVO pageReqVO) {
        PageResult<MemberGiftConfigDO> pageResult = giftConfigService.getGiftConfigPage(pageReqVO);
        return success(toBean(pageResult, MemberGiftConfigRespVO.class,
                config -> config.setPv(calcPv(config.getPrice(), config.getPvPercent()))));
    }

    @GetMapping("/list")
    @Operation(summary = "获得开启中的礼包列表")
    @PreAuthorize("@ss.hasPermission('member:gift-config:query')")
    public CommonResult<List<MemberGiftConfigRespVO>> getGiftConfigList() {
        List<MemberGiftConfigDO> list = giftConfigService.getGiftConfigListEnable();
        return success(toBean(list, MemberGiftConfigRespVO.class,
                config -> config.setPv(calcPv(config.getPrice(), config.getPvPercent()))));
    }

    /**
     * 对应 PV = 销售价格 × PV 比例，四舍五入取整数元
     */
    private Integer calcPv(Integer priceFen, Integer pvPercent) {
        if (priceFen == null || pvPercent == null) {
            return 0;
        }
        return java.math.BigDecimal.valueOf(priceFen)
                .divide(java.math.BigDecimal.valueOf(100), 10, java.math.RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(pvPercent))
                .divide(java.math.BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }

}

package cn.sailstack.qdp.module.member.controller.admin.bonus.director;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director.MemberDirectorPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director.MemberDirectorRespVO;
import cn.sailstack.qdp.module.member.service.bonus.director.MemberDirectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean;

@Tag(name = "管理后台 - 会员董事资格")
@RestController
@RequestMapping("/member/director")
@Validated
public class MemberDirectorController {

    @Resource
    private MemberDirectorService directorService;

    @GetMapping("/page")
    @Operation(summary = "获得董事资格分页")
    @PreAuthorize("@ss.hasPermission('member:director:query')")
    public CommonResult<PageResult<MemberDirectorRespVO>> getDirectorPage(@Valid MemberDirectorPageReqVO pageReqVO) {
        return success(toBean(directorService.getDirectorPage(pageReqVO), MemberDirectorRespVO.class));
    }

    @PostMapping("/verify")
    @Operation(summary = "手动触发全部董事资格核验（月结前自动执行；口径为【待业务确认】配置项）")
    @PreAuthorize("@ss.hasPermission('member:director:verify')")
    public CommonResult<Integer> verifyDirectors() {
        return success(directorService.verifyAllDirectors());
    }

    @GetMapping("/list-by-level")
    @Operation(summary = "获得指定等级的董事列表")
    @Parameter(name = "level", description = "董事等级（1-5）", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('member:director:query')")
    public CommonResult<java.util.List<MemberDirectorRespVO>> getDirectorListByLevel(@RequestParam("level") Integer level) {
        return success(toBean(directorService.getDirectorListByLevel(level), MemberDirectorRespVO.class));
    }

}

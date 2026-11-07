package cn.sailstack.qdp.module.member.controller.admin.bonus.relation;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation.MemberMarketHistoryRespVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation.MemberRelationPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation.MemberRelationRespVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberMarketHistoryDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import cn.sailstack.qdp.module.member.service.bonus.relation.MemberRelationService;
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

@Tag(name = "管理后台 - 会员推荐关系")
@RestController
@RequestMapping("/member/relation")
@Validated
public class MemberRelationController {

    @Resource
    private MemberRelationService relationService;

    @GetMapping("/page")
    @Operation(summary = "获得会员推荐关系分页")
    @PreAuthorize("@ss.hasPermission('member:relation:query')")
    public CommonResult<PageResult<MemberRelationRespVO>> getRelationPage(@Valid MemberRelationPageReqVO pageReqVO) {
        return success(toBean(relationService.getRelationPage(pageReqVO), MemberRelationRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员推荐关系")
    @Parameter(name = "userId", description = "会员编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('member:relation:query')")
    public CommonResult<MemberRelationRespVO> getRelation(@RequestParam("userId") Long userId) {
        return success(toBean(relationService.getRelation(userId), MemberRelationRespVO.class));
    }

    @GetMapping("/valid-direct-list")
    @Operation(summary = "获得推荐人的有效直推列表（按有效购买时间排序）")
    @Parameter(name = "referrerId", description = "推荐人编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:relation:query')")
    public CommonResult<List<MemberRelationRespVO>> getValidDirectList(@RequestParam("referrerId") Long referrerId) {
        return success(toBean(relationService.getValidDirectList(referrerId), MemberRelationRespVO.class));
    }

    @GetMapping("/market-history/list")
    @Operation(summary = "获得会员市场归属变更历史")
    @Parameter(name = "userId", description = "会员编号", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('member:relation:query')")
    public CommonResult<List<MemberMarketHistoryRespVO>> getMarketHistoryList(@RequestParam("userId") Long userId) {
        List<MemberMarketHistoryDO> list = relationService.getMarketHistoryList(userId);
        return success(toBean(list, MemberMarketHistoryRespVO.class));
    }

}

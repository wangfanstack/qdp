package cn.sailstack.qdp.module.member.controller.admin.bonus.record;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record.MemberBonusRecordPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record.MemberBonusRecordRespVO;
import cn.sailstack.qdp.module.member.service.bonus.record.MemberBonusRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean;

@Tag(name = "管理后台 - 礼包奖金明细")
@RestController
@RequestMapping("/member/bonus-record")
@Validated
public class MemberBonusRecordController {

    @Resource
    private MemberBonusRecordService bonusRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得礼包奖金明细分页")
    @PreAuthorize("@ss.hasPermission('member:bonus-record:query')")
    public CommonResult<PageResult<MemberBonusRecordRespVO>> getBonusRecordPage(@Valid MemberBonusRecordPageReqVO pageReqVO) {
        return success(toBean(bonusRecordService.getBonusRecordPage(pageReqVO), MemberBonusRecordRespVO.class));
    }

}

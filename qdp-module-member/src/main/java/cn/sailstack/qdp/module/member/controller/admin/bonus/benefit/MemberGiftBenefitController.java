package cn.sailstack.qdp.module.member.controller.admin.bonus.benefit;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit.MemberGiftBenefitPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit.MemberGiftBenefitRespVO;
import cn.sailstack.qdp.module.member.service.bonus.gift.MemberGiftBenefitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean;

@Tag(name = "管理后台 - 会员礼包权益记录")
@RestController
@RequestMapping("/member/gift-benefit")
@Validated
public class MemberGiftBenefitController {

    @Resource
    private MemberGiftBenefitService giftBenefitService;

    @GetMapping("/page")
    @Operation(summary = "获得会员礼包权益记录分页")
    @PreAuthorize("@ss.hasPermission('member:gift-benefit:query')")
    public CommonResult<PageResult<MemberGiftBenefitRespVO>> getBenefitPage(@Valid MemberGiftBenefitPageReqVO pageReqVO) {
        return success(toBean(giftBenefitService.getBenefitPage(pageReqVO), MemberGiftBenefitRespVO.class));
    }

}

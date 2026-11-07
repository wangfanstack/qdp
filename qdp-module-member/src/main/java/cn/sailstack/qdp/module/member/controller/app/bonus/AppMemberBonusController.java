package cn.sailstack.qdp.module.member.controller.app.bonus;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageParam;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit.MemberGiftBenefitRespVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record.MemberBonusRecordRespVO;
import cn.sailstack.qdp.module.member.controller.app.bonus.vo.AppMemberRelationBindReqVO;
import cn.sailstack.qdp.module.member.controller.app.bonus.vo.AppMemberRelationRespVO;
import cn.sailstack.qdp.module.member.service.bonus.gift.MemberGiftBenefitService;
import cn.sailstack.qdp.module.member.service.bonus.record.MemberBonusRecordService;
import cn.sailstack.qdp.module.member.service.bonus.relation.MemberRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean;

@Tag(name = "用户 App - 会员权益与奖金")
@RestController
@RequestMapping("/member/bonus")
@Validated
public class AppMemberBonusController {

    @Resource
    private MemberRelationService relationService;
    @Resource
    private MemberGiftBenefitService giftBenefitService;
    @Resource
    private MemberBonusRecordService bonusRecordService;

    @PostMapping("/relation/bind")
    @Operation(summary = "绑定推荐人（一次性；防自绑、防环）")
    public CommonResult<Boolean> bindRelation(@Valid @RequestBody AppMemberRelationBindReqVO reqVO) {
        relationService.bindRelation(getLoginUserId(), reqVO.getReferrerId());
        return success(true);
    }

    @GetMapping("/relation/get")
    @Operation(summary = "获得我的推荐关系（含老板身份/市场归属）")
    public CommonResult<AppMemberRelationRespVO> getRelation() {
        return success(toBean(relationService.getRelation(getLoginUserId()), AppMemberRelationRespVO.class));
    }

    @GetMapping("/gift-benefit/list")
    @Operation(summary = "获得我的礼包权益记录")
    public CommonResult<List<MemberGiftBenefitRespVO>> getBenefitList() {
        return success(toBean(giftBenefitService.getBenefitListByUserId(getLoginUserId()), MemberGiftBenefitRespVO.class));
    }

    @GetMapping("/bonus-record/page")
    @Operation(summary = "获得我的礼包奖金明细分页")
    public CommonResult<PageResult<MemberBonusRecordRespVO>> getBonusRecordPage(PageParam pageParam) {
        return success(toBean(bonusRecordService.getBonusRecordPage(getLoginUserId(), pageParam),
                MemberBonusRecordRespVO.class));
    }

}

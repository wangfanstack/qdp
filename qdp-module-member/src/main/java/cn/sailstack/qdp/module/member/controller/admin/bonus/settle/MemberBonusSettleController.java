package cn.sailstack.qdp.module.member.controller.admin.bonus.settle;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle.MemberBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle.MemberBonusSettleBatchRespVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle.MemberBonusSettleReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusSettleBatchDO;
import cn.sailstack.qdp.module.member.service.bonus.settle.MemberBonusSettleService;
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

@Tag(name = "管理后台 - 礼包奖金结算")
@RestController
@RequestMapping("/member/bonus-settle")
@Validated
public class MemberBonusSettleController {

    @Resource
    private MemberBonusSettleService settleService;

    @PostMapping("/settle")
    @Operation(summary = "手动触发指定账期月度结算（收入口径 MANUAL 时须录入分红池）")
    @PreAuthorize("@ss.hasPermission('member:bonus-settle:settle')")
    public CommonResult<String> settleMonth(@Valid @RequestBody MemberBonusSettleReqVO reqVO) {
        MemberBonusSettleBatchDO batch = settleService.settleMonth(reqVO.getPeriod(), reqVO.getDividendPool());
        return success(batch.getBatchNo());
    }

    @PostMapping("/pay")
    @Operation(summary = "手动触发批次发放（打入会员钱包余额）")
    @Parameter(name = "batchNo", description = "批次号", required = true, example = "GB202611")
    @PreAuthorize("@ss.hasPermission('member:bonus-settle:pay')")
    public CommonResult<Boolean> payBatch(@RequestParam("batchNo") String batchNo) {
        settleService.payBatch(batchNo);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得结算批次分页")
    @PreAuthorize("@ss.hasPermission('member:bonus-settle:query')")
    public CommonResult<PageResult<MemberBonusSettleBatchRespVO>> getBatchPage(@Valid MemberBonusSettleBatchPageReqVO pageReqVO) {
        return success(toBean(settleService.getBatchPage(pageReqVO), MemberBonusSettleBatchRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得结算批次详情（含个人结算明细）")
    @Parameter(name = "batchNo", description = "批次号", required = true, example = "GB202611")
    @PreAuthorize("@ss.hasPermission('member:bonus-settle:query')")
    public CommonResult<MemberBonusSettleBatchRespVO> getBatch(@RequestParam("batchNo") String batchNo) {
        MemberBonusSettleBatchRespVO respVO = toBean(settleService.getBatchByBatchNo(batchNo), MemberBonusSettleBatchRespVO.class);
        respVO.setDetails(toBean(settleService.getDetailListByBatchNo(batchNo), MemberBonusSettleBatchRespVO.Detail.class));
        return success(respVO);
    }

}

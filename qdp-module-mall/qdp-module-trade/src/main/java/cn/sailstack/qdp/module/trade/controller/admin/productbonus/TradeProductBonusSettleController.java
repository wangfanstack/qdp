package cn.sailstack.qdp.module.trade.controller.admin.productbonus;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusSettleBatchRespVO;
import cn.sailstack.qdp.module.trade.service.productbonus.TradeProductBonusSettleService;
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

@Tag(name = "管理后台 - 商品奖金结算")
@RestController
@RequestMapping("/trade/product-bonus-settle")
@Validated
public class TradeProductBonusSettleController {

    @Resource
    private TradeProductBonusSettleService settleService;

    @PostMapping("/settle")
    @Operation(summary = "手动触发指定账期商品奖金月度结算（GMV→奖金池→董事加权分红）")
    @Parameter(name = "period", description = "账期（yyyy-MM）", required = true, example = "2026-11")
    @PreAuthorize("@ss.hasPermission('trade:product-bonus-settle:settle')")
    public CommonResult<String> settleMonth(@RequestParam("period") String period) {
        return success(settleService.settleMonth(period).getBatchNo());
    }

    @PostMapping("/pay")
    @Operation(summary = "手动触发批次发放（打入会员钱包余额）")
    @Parameter(name = "batchNo", description = "批次号", required = true, example = "PB202611")
    @PreAuthorize("@ss.hasPermission('trade:product-bonus-settle:pay')")
    public CommonResult<Boolean> payBatch(@RequestParam("batchNo") String batchNo) {
        settleService.payBatch(batchNo);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品奖金结算批次分页")
    @PreAuthorize("@ss.hasPermission('trade:product-bonus-settle:query')")
    public CommonResult<PageResult<TradeProductBonusSettleBatchRespVO>> getBatchPage(@Valid TradeProductBonusSettleBatchPageReqVO pageReqVO) {
        return success(toBean(settleService.getBatchPage(pageReqVO), TradeProductBonusSettleBatchRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得商品奖金结算批次详情（含个人结算明细）")
    @Parameter(name = "batchNo", description = "批次号", required = true, example = "PB202611")
    @PreAuthorize("@ss.hasPermission('trade:product-bonus-settle:query')")
    public CommonResult<TradeProductBonusSettleBatchRespVO> getBatch(@RequestParam("batchNo") String batchNo) {
        TradeProductBonusSettleBatchRespVO respVO = toBean(settleService.getBatchByBatchNo(batchNo), TradeProductBonusSettleBatchRespVO.class);
        respVO.setDetails(toBean(settleService.getDetailListByBatchNo(batchNo), TradeProductBonusSettleBatchRespVO.Detail.class));
        return success(respVO);
    }

}

package cn.sailstack.qdp.module.trade.controller.admin.productbonus;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusRecordPageReqVO;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusRecordRespVO;
import cn.sailstack.qdp.module.trade.service.productbonus.TradeProductBonusService;
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

@Tag(name = "管理后台 - 商品奖金明细")
@RestController
@RequestMapping("/trade/product-bonus-record")
@Validated
public class TradeProductBonusRecordController {

    @Resource
    private TradeProductBonusService productBonusService;

    @GetMapping("/page")
    @Operation(summary = "获得商品奖金明细分页（差额奖 + 商品董事加权分红）")
    @PreAuthorize("@ss.hasPermission('trade:product-bonus-record:query')")
    public CommonResult<PageResult<TradeProductBonusRecordRespVO>> getBonusRecordPage(@Valid TradeProductBonusRecordPageReqVO pageReqVO) {
        return success(toBean(productBonusService.getBonusRecordPage(pageReqVO), TradeProductBonusRecordRespVO.class));
    }

}

package cn.sailstack.qdp.module.trade.controller.admin.productbonus;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductGmvPageReqVO;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductGmvRespVO;
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

@Tag(name = "管理后台 - 会员商品 GMV")
@RestController
@RequestMapping("/trade/product-gmv")
@Validated
public class TradeProductGmvController {

    @Resource
    private TradeProductBonusService productBonusService;

    @GetMapping("/page")
    @Operation(summary = "获得会员商品 GMV 分页（完成订单实付，已扣退款）")
    @PreAuthorize("@ss.hasPermission('trade:product-gmv:query')")
    public CommonResult<PageResult<TradeProductGmvRespVO>> getGmvPage(@Valid TradeProductGmvPageReqVO pageReqVO) {
        return success(toBean(productBonusService.getGmvPage(pageReqVO), TradeProductGmvRespVO.class));
    }

}

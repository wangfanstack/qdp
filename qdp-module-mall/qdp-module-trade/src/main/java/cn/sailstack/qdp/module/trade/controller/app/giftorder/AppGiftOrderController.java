package cn.sailstack.qdp.module.trade.controller.app.giftorder;

import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftOrderCreateReqVO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftOrderCreateRespVO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftRespVO;
import cn.sailstack.qdp.module.trade.service.giftorder.TradeGiftOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 会员礼包开通/升级")
@RestController
@RequestMapping("/trade/gift-order")
@Validated
public class AppGiftOrderController {

    @Resource
    private TradeGiftOrderService giftOrderService;

    @GetMapping("/gifts")
    @Operation(summary = "获得礼包档位列表（含当前应付金额与升级差价）")
    public CommonResult<List<AppGiftRespVO>> getGiftList() {
        return success(giftOrderService.getGiftList(getLoginUserId()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建礼包开通/升级订单（复用标准订单支付链路）")
    public CommonResult<AppGiftOrderCreateRespVO> createGiftOrder(@Valid @RequestBody AppGiftOrderCreateReqVO reqVO) {
        return success(giftOrderService.createGiftOrder(getLoginUserId(), reqVO));
    }

}

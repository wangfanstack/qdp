package cn.sailstack.qdp.module.pay.controller.app.order;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.pay.convert.order.PayOrderConvert;
import cn.sailstack.qdp.module.pay.convert.pay.PayOrderSubmitConvert;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.common.util.object.ObjectUtils;
import cn.sailstack.qdp.framework.common.pojo.CommonResult;

import cn.sailstack.qdp.module.pay.controller.admin.order.vo.PayOrderRespVO;
import cn.sailstack.qdp.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.sailstack.qdp.module.pay.controller.app.order.vo.AppPayOrderSubmitReqVO;
import cn.sailstack.qdp.module.pay.controller.app.order.vo.AppPayOrderSubmitRespVO;
import cn.sailstack.qdp.module.pay.dal.dataobject.order.PayOrderDO;
import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletDO;
import cn.sailstack.qdp.module.pay.enums.PayChannelEnum;
import cn.sailstack.qdp.module.pay.enums.order.PayOrderStatusEnum;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.impl.wallet.WalletPayClient;
import cn.sailstack.qdp.module.pay.service.order.PayOrderService;
import cn.sailstack.qdp.module.pay.service.wallet.PayWalletService;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.sailstack.qdp.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static cn.sailstack.qdp.framework.web.core.util.WebFrameworkUtils.getLoginUserType;
import static cn.sailstack.qdp.module.pay.enums.ErrorCodeConstants.PAY_ORDER_NOT_FOUND;

@Tag(name = "用户 APP - 支付订单")
@RestController
@RequestMapping("/pay/order")
@Validated
@Slf4j
public class AppPayOrderController {

    @Resource
    private PayOrderService payOrderService;
    @Resource
    private PayWalletService payWalletService;

    @GetMapping("/get")
    @Operation(summary = "获得支付订单")
    @Parameters({
            @Parameter(name = "id", description = "编号", example = "1024"),
            @Parameter(name = "no", description = "支付订单号", example = "Pxxx"),
            @Parameter(name = "sync", description = "是否同步", example = "true")
    })
    public CommonResult<PayOrderRespVO> getOrder(@RequestParam(value = "id", required = false) Long id,
                                                 @RequestParam(value = "no", required = false) String no,
                                                 @RequestParam(value = "sync", required = false) Boolean sync) {
        PayOrderDO order = null;
        if (StringUtils.isNotEmpty(no)) {
            order = payOrderService.getOrder(no);
        }
        if (ObjectUtils.isNull(order) && ObjectUtils.isNotNull(id)) {
            order = payOrderService.getOrder(id);
        }
        if (order == null) {
            return success(null);
        }
        // 重要：校验订单是否是当前用户，避免越权
        if (!isLoginUserOrder(order)) {
            return success(null);
        }

        // sync 仅在等待支付
        if (Boolean.TRUE.equals(sync) && PayOrderStatusEnum.isWaiting(order.getStatus())) {
            payOrderService.syncOrderQuietly(order.getId());
            // 重新查询，因为同步后，可能会有变化
            order = payOrderService.getOrder(order.getId());
        }
        return success(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(order, PayOrderRespVO.class));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交支付订单")
    public CommonResult<AppPayOrderSubmitRespVO> submitPayOrder(@RequestBody AppPayOrderSubmitReqVO reqVO) {
        // 1. 重要：校验订单是否是当前用户，避免越权
        PayOrderDO order = payOrderService.getOrder(reqVO.getId());
        if (order == null || !isLoginUserOrder(order)) {
            throw exception(PAY_ORDER_NOT_FOUND);
        }

        // 2. 钱包支付事，需要额外传 user_id 和 user_type
        if (Objects.equals(reqVO.getChannelCode(), PayChannelEnum.WALLET.getCode())) {
            if (reqVO.getChannelExtras() == null) {
                reqVO.setChannelExtras(Maps.newHashMapWithExpectedSize(1));
            }
            PayWalletDO wallet = payWalletService.getOrCreateWallet(getLoginUserId(), getLoginUserType());
            reqVO.getChannelExtras().put(WalletPayClient.WALLET_ID_KEY, String.valueOf(wallet.getId()));
        }

        // 3. 提交支付
        PayOrderSubmitRespVO respVO = payOrderService.submitOrder(reqVO, getClientIP());
        return success(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(respVO, AppPayOrderSubmitRespVO.class));
    }

    /**
     * 判断订单是否属于当前登录用户，需同时匹配用户编号和用户类型
     *
     * 相关 issue：https://github.com/YunaiV/ruoyi-vue-pro/issues/1303
     *
     * @param order 支付订单
     * @return 是否属于
     */
    private boolean isLoginUserOrder(PayOrderDO order) {
        // 特殊：早期订单未存储 userId、userType，所以忽略
        return (order.getUserId() == null || ObjectUtils.equal(order.getUserId(), getLoginUserId()))
                && (order.getUserType() == null || ObjectUtils.equal(order.getUserType(), getLoginUserType()));
    }

}

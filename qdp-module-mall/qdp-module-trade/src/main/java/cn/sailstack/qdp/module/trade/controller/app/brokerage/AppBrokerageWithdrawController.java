package cn.sailstack.qdp.module.trade.controller.app.brokerage;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.trade.convert.brokerage.BrokerageWithdrawConvert;
import cn.sailstack.qdp.framework.common.util.object.ObjectUtils;
import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;

import cn.sailstack.qdp.framework.dict.core.DictFrameworkUtils;
import cn.sailstack.qdp.module.pay.api.transfer.PayTransferApi;
import cn.sailstack.qdp.module.pay.api.transfer.dto.PayTransferRespDTO;
import cn.sailstack.qdp.module.trade.controller.admin.brokerage.vo.withdraw.BrokerageWithdrawPageReqVO;
import cn.sailstack.qdp.module.trade.controller.app.brokerage.vo.withdraw.AppBrokerageWithdrawCreateReqVO;
import cn.sailstack.qdp.module.trade.controller.app.brokerage.vo.withdraw.AppBrokerageWithdrawPageReqVO;
import cn.sailstack.qdp.module.trade.controller.app.brokerage.vo.withdraw.AppBrokerageWithdrawRespVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.brokerage.BrokerageWithdrawDO;
import cn.sailstack.qdp.module.trade.enums.DictTypeConstants;
import cn.sailstack.qdp.module.trade.enums.brokerage.BrokerageWithdrawStatusEnum;
import cn.sailstack.qdp.module.trade.enums.brokerage.BrokerageWithdrawTypeEnum;
import cn.sailstack.qdp.module.trade.service.brokerage.BrokerageWithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 分销提现")
@RestController
@RequestMapping("/trade/brokerage-withdraw")
@Validated
@Slf4j
public class AppBrokerageWithdrawController {

    @Resource
    private BrokerageWithdrawService brokerageWithdrawService;

    @Resource
    private PayTransferApi payTransferApi;

    @GetMapping("/page")
    @Operation(summary = "获得分销提现分页")
    public CommonResult<PageResult<AppBrokerageWithdrawRespVO>> getBrokerageWithdrawPage(AppBrokerageWithdrawPageReqVO pageReqVO) {
        PageResult<BrokerageWithdrawDO> pageResult = brokerageWithdrawService.getBrokerageWithdrawPage(
                BeanUtils.toBean(pageReqVO, BrokerageWithdrawPageReqVO.class).setUserId(getLoginUserId()));
        return success(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(pageResult, AppBrokerageWithdrawRespVO.class, withdrawVO ->
                withdrawVO.setTypeName(DictFrameworkUtils.parseDictDataLabel(DictTypeConstants.BROKERAGE_WITHDRAW_TYPE, withdrawVO.getType()))
                        .setStatusName(DictFrameworkUtils.parseDictDataLabel(DictTypeConstants.BROKERAGE_WITHDRAW_STATUS, withdrawVO.getStatus()))));
    }

    @GetMapping("/get")
    @Operation(summary = "获得佣金提现")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppBrokerageWithdrawRespVO> getBrokerageWithdraw(@RequestParam("id") Long id) {
        BrokerageWithdrawDO withdraw = brokerageWithdrawService.getBrokerageWithdraw(id);
        if (withdraw == null || ObjectUtils.notEqual(withdraw.getUserId(), getLoginUserId())) {
            return success(null);
        }
        // 审核中（转账中），并且是微信转账，需要返回 mchId 用于确认收款
        AppBrokerageWithdrawRespVO withdrawVO = BeanUtils.toBean(withdraw, AppBrokerageWithdrawRespVO.class);
        if (Objects.equals(withdraw.getStatus(), BrokerageWithdrawStatusEnum.AUDIT_SUCCESS.getStatus())
                && Objects.equals(withdraw.getType(), BrokerageWithdrawTypeEnum.WECHAT_API.getType())
                && withdraw.getPayTransferId() != null) {
            PayTransferRespDTO transfer = payTransferApi.getTransfer(withdraw.getPayTransferId());
            if (transfer != null) {
                withdrawVO.setTransferChannelPackageInfo(transfer.getChannelPackageInfo())
                        .setTransferChannelMchId(transfer.getChannelMchId());
            }
        }
        return success(withdrawVO);
    }

    @PostMapping("/create")
    @Operation(summary = "创建分销提现")
    public CommonResult<Long> createBrokerageWithdraw(@RequestBody @Valid AppBrokerageWithdrawCreateReqVO createReqVO) {
        return success(brokerageWithdrawService.createBrokerageWithdraw(getLoginUserId(), createReqVO));
    }

}

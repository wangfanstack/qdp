package cn.sailstack.qdp.module.pay.convert.wallet;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.pay.convert.wallet.PayWalletRechargeConvert;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import cn.sailstack.qdp.framework.common.util.collection.MapUtils;

import cn.sailstack.qdp.framework.dict.core.DictFrameworkUtils;
import cn.sailstack.qdp.module.pay.controller.app.wallet.vo.recharge.AppPayWalletRechargeCreateRespVO;
import cn.sailstack.qdp.module.pay.controller.app.wallet.vo.recharge.AppPayWalletRechargeRespVO;
import cn.sailstack.qdp.module.pay.dal.dataobject.order.PayOrderDO;
import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletRechargeDO;
import cn.sailstack.qdp.module.pay.enums.DictTypeConstants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface PayWalletRechargeConvert {

    PayWalletRechargeConvert INSTANCE = Mappers.getMapper(PayWalletRechargeConvert.class);

    @Mapping(target = "totalPrice", expression = "java( payPrice + bonusPrice)")
    PayWalletRechargeDO convert(Long walletId, Integer payPrice, Integer bonusPrice, Long packageId);

    AppPayWalletRechargeCreateRespVO convert(PayWalletRechargeDO bean);

    default PageResult<AppPayWalletRechargeRespVO> convertPage(PageResult<PayWalletRechargeDO> pageResult,
                                                               List<PayOrderDO> payOrderList) {
        PageResult<AppPayWalletRechargeRespVO> voPageResult = cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(pageResult, AppPayWalletRechargeRespVO.class);
        Map<Long, PayOrderDO> payOrderMap = CollectionUtils.convertMap(payOrderList, PayOrderDO::getId);
        voPageResult.getList().forEach(recharge -> {
            recharge.setPayChannelName(DictFrameworkUtils.parseDictDataLabel(
                    DictTypeConstants.CHANNEL_CODE, recharge.getPayChannelCode()));
            MapUtils.findAndThen(payOrderMap, recharge.getPayOrderId(),
                    order -> recharge.setPayOrderChannelOrderNo(order.getChannelOrderNo()));
        });
        return voPageResult;
    }


    AppPayWalletRechargeRespVO convert(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletRechargeDO> source);

    cn.sailstack.qdp.framework.common.pojo.PageResult<AppPayWalletRechargeRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletRechargeDO> page);
}

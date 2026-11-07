package cn.sailstack.qdp.module.trade.service.giftorder;

import cn.sailstack.qdp.module.member.api.address.MemberAddressApi;
import cn.sailstack.qdp.module.member.api.address.dto.MemberAddressRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftConfigRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftPrepareRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberRelationRespDTO;
import cn.sailstack.qdp.module.product.api.sku.ProductSkuApi;
import cn.sailstack.qdp.module.product.api.sku.dto.ProductSkuRespDTO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftOrderCreateReqVO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftOrderCreateRespVO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftRespVO;
import cn.sailstack.qdp.module.trade.controller.app.order.vo.AppTradeOrderCreateReqVO;
import cn.sailstack.qdp.module.trade.controller.app.order.vo.AppTradeOrderSettlementReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.sailstack.qdp.module.trade.enums.delivery.DeliveryTypeEnum;
import cn.sailstack.qdp.module.trade.service.order.TradeOrderUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.GIFT_BUY_NEED_REFERRER;
import static cn.sailstack.qdp.module.trade.enums.ErrorCodeConstants.PRODUCT_BONUS_GIFT_ORDER_ITEMS_ERROR;

/**
 * 会员礼包订单（开通/升级）Service 实现类
 *
 * 编排流程：绑定推荐人（首次）→ 差价校验（member）→ 组装标准下单请求复用
 * {@link TradeOrderUpdateService#createOrder}；礼包类型标记与补差价由
 * {@code TradeGiftOrderHandler#beforeOrderCreate} 统一处理（普通入口直购礼包 SPU 走同一规则）。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class TradeGiftOrderServiceImpl implements TradeGiftOrderService {

    @Resource
    private MemberBonusApi memberBonusApi;
    @Resource
    private MemberAddressApi memberAddressApi;
    @Resource
    private ProductSkuApi productSkuApi;
    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;

    @Override
    public List<AppGiftRespVO> getGiftList(Long userId) {
        List<MemberGiftConfigRespDTO> giftConfigs = memberBonusApi.getEnabledGiftConfigList();
        return giftConfigs.stream().map(config -> {
            AppGiftRespVO respVO = new AppGiftRespVO()
                    .setGiftId(config.getId())
                    .setName(config.getName())
                    .setLevelName(config.getLevelName())
                    .setPrice(config.getPrice())
                    .setPvPercent(config.getPvPercent())
                    .setSpuId(config.getSpuId())
                    .setPv(calcPv(config.getPrice(), config.getPvPercent()));
            // 差价预览：低于等于当前档位不可购买，但仍在列表展示
            try {
                MemberGiftPrepareRespDTO prepare = memberBonusApi.prepareGiftBuy(userId, config.getId());
                respVO.setPayableAmount(prepare.getPayableAmount());
                respVO.setUpgrade(prepare.getUpgrade());
                respVO.setPurchasable(true);
            } catch (Exception ex) {
                respVO.setPayableAmount(0).setUpgrade(false).setPurchasable(false);
            }
            return respVO;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppGiftOrderCreateRespVO createGiftOrder(Long userId, AppGiftOrderCreateReqVO reqVO) {
        // 1. 首次开通必须绑定推荐人（已绑定忽略传入的 referrerId）
        MemberRelationRespDTO relation = memberBonusApi.getRelation(userId);
        if (relation == null || relation.getReferrerId() == null) {
            if (reqVO.getReferrerId() == null) {
                throw exception(GIFT_BUY_NEED_REFERRER);
            }
            memberBonusApi.bindRelation(userId, reqVO.getReferrerId());
        }
        // 2. 礼包配置（开启中）与差价校验（member 侧：仅可购买更高档）
        MemberGiftConfigRespDTO giftConfig = memberBonusApi.getEnabledGiftConfig(reqVO.getGiftId());
        if (giftConfig == null) {
            throw exception(PRODUCT_BONUS_GIFT_ORDER_ITEMS_ERROR);
        }
        MemberGiftPrepareRespDTO prepare = memberBonusApi.prepareGiftBuy(userId, giftConfig.getId());
        // 3. 取礼包 SPU 的第一个 SKU 作为支付载体
        List<ProductSkuRespDTO> skus = productSkuApi.getSkuListBySpuId(List.of(giftConfig.getSpuId()));
        if (skus == null || skus.isEmpty()) {
            throw exception(PRODUCT_BONUS_GIFT_ORDER_ITEMS_ERROR);
        }
        // 4. 组装标准下单请求（不使用优惠券/积分；快递配送，地址缺省取默认地址）
        AppTradeOrderCreateReqVO createReqVO = new AppTradeOrderCreateReqVO();
        AppTradeOrderSettlementReqVO.Item item = new AppTradeOrderSettlementReqVO.Item();
        item.setSkuId(skus.get(0).getId());
        item.setCount(1);
        createReqVO.setItems(List.of(item));
        createReqVO.setPointStatus(false);
        createReqVO.setDeliveryType(DeliveryTypeEnum.EXPRESS.getType());
        Long addressId = reqVO.getAddressId();
        if (addressId == null) {
            MemberAddressRespDTO defaultAddress = memberAddressApi.getDefaultAddress(userId);
            addressId = defaultAddress != null ? defaultAddress.getId() : null;
        }
        createReqVO.setAddressId(addressId);
        createReqVO.setRemark("会员礼包开通/升级");
        // 5. 复用标准下单链路（TradeGiftOrderHandler 将标记 GIFT 类型并写入差价）
        TradeOrderDO order = tradeOrderUpdateService.createOrder(userId, createReqVO);
        log.info("[createGiftOrder][会员({}) 礼包({}) 应付({}分) 订单({})]",
                userId, giftConfig.getId(), prepare.getPayableAmount(), order.getId());
        return new AppGiftOrderCreateRespVO().setOrderId(order.getId()).setPayOrderId(order.getPayOrderId());
    }

    private Integer calcPv(Integer priceFen, Integer pvPercent) {
        if (priceFen == null || pvPercent == null) {
            return 0;
        }
        return BigDecimal.valueOf(priceFen)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(pvPercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .intValue();
    }

}

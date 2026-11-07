package cn.sailstack.qdp.module.trade.service.productbonus;

import cn.sailstack.qdp.framework.test.core.ut.BaseDbUnitTest;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.level.MemberLevelApi;
import cn.sailstack.qdp.module.member.api.level.dto.MemberLevelRespDTO;
import cn.sailstack.qdp.module.member.api.user.MemberUserApi;
import cn.sailstack.qdp.module.member.api.user.dto.MemberUserRespDTO;
import cn.sailstack.qdp.module.trade.dal.dataobject.aftersale.AfterSaleDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.order.TradeOrderItemDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusRecordDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductGmvRecordDO;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductBonusRecordMapper;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductGmvRecordMapper;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusStatusEnum;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusTypeEnum;
import cn.sailstack.qdp.module.trade.service.aftersale.AfterSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * {@link TradeProductBonusServiceImpl} 的单元测试
 *
 * 覆盖验收用例 7、9：折扣差额奖（含仅直接上级）、GMV 记录（不含运费）、退款冲回、幂等。
 *
 * @author sail
 */
@Import(TradeProductBonusServiceImpl.class)
public class TradeProductBonusServiceImplTest extends BaseDbUnitTest {

    @Resource
    private TradeProductBonusService productBonusService;
    @Resource
    private TradeProductBonusRecordMapper productBonusRecordMapper;
    @Resource
    private TradeProductGmvRecordMapper productGmvRecordMapper;

    @MockitoBean
    private MemberBonusApi memberBonusApi;
    @MockitoBean
    private MemberUserApi memberUserApi;
    @MockitoBean
    private MemberLevelApi memberLevelApi;
    @MockitoBean
    private AfterSaleService afterSaleService;

    private TradeOrderDO buildOrder(Long orderId, int payPrice, int deliveryPrice) {
        TradeOrderDO order = new TradeOrderDO();
        order.setId(orderId);
        order.setNo("NO" + orderId);
        order.setUserId(102L);         // 买家 B
        order.setType(0);              // 普通订单
        order.setPayPrice(payPrice);
        order.setDeliveryPrice(deliveryPrice);
        order.setReceiveTime(LocalDateTime.of(2026, 11, 6, 10, 0, 0));
        return order;
    }

    private TradeOrderItemDO buildItem(Long itemId, Long orderId, int payPrice) {
        TradeOrderItemDO item = new TradeOrderItemDO();
        item.setId(itemId);
        item.setOrderId(orderId);
        item.setUserId(102L);
        item.setSpuId(900L);
        item.setSkuId(901L);
        item.setCount(1);
        item.setPayPrice(payPrice);
        item.setAfterSaleStatus(0);
        return item;
    }

    private void mockDiscount(Long buyerId, Integer buyerDiscount, Long superiorId, Integer superiorDiscount) {
        MemberUserRespDTO buyer = new MemberUserRespDTO();
        buyer.setId(buyerId);
        buyer.setLevelId(11L);
        when(memberUserApi.getUser(buyerId)).thenReturn(buyer);
        MemberUserRespDTO superior = new MemberUserRespDTO();
        superior.setId(superiorId);
        superior.setLevelId(12L);
        when(memberUserApi.getUser(superiorId)).thenReturn(superior);
        MemberLevelRespDTO buyerLevel = new MemberLevelRespDTO();
        buyerLevel.setId(11L);
        buyerLevel.setDiscountPercent(buyerDiscount);
        when(memberLevelApi.getMemberLevel(11L)).thenReturn(buyerLevel);
        MemberLevelRespDTO superiorLevel = new MemberLevelRespDTO();
        superiorLevel.setId(12L);
        superiorLevel.setDiscountPercent(superiorDiscount);
        when(memberLevelApi.getMemberLevel(12L)).thenReturn(superiorLevel);
        when(memberBonusApi.getDirectSuperiorId(buyerId)).thenReturn(superiorId);
    }

    @Test
    public void testCreateBonus_differenceBonus20Yuan() {
        // 验收 7：上级 A 4 折（40）、买家 B 6 折（60），商品折后 100 元 → A 获得 20 元；仅直接上级
        mockDiscount(102L, 60, 101L, 40);
        TradeOrderDO order = buildOrder(1001L, 10800, 800);
        productBonusService.createBonusOnReceiveOrder(order, List.of(buildItem(2001L, 1001L, 10000)));

        // 差额奖：(60 - 40)% × 10000 分 = 2000 分
        TradeProductBonusRecordDO record = productBonusRecordMapper.selectByOrderItemId(2001L);
        assertNotNull(record);
        assertEquals(101L, record.getUserId());      // 仅直接上级
        assertEquals(102L, record.getBuyerUserId());
        assertEquals(20, record.getRate());
        assertEquals(2000, record.getAmount());
        assertEquals(TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus(), record.getStatus());
        // 仅一条（间接上级不获得）
        assertEquals(1, productBonusRecordMapper.selectList(
                new LambdaQueryWrapperX<TradeProductBonusRecordDO>()
                        .eq(TradeProductBonusRecordDO::getOrderId, 1001L)).size());
        // GMV：实付 10800 - 运费 800 = 10000（不含运费口径）
        TradeProductGmvRecordDO gmv = productGmvRecordMapper.selectByOrderId(1001L);
        assertEquals(10000, gmv.getGmvAmount());
        assertEquals("2026-11", gmv.getPeriod());
    }

    @Test
    public void testCreateBonus_noPositiveDiff() {
        // 折扣差额 ≤ 0（买家 4 折、上级 6 折）→ 不产生正向奖金；GMV 照记
        mockDiscount(102L, 40, 101L, 60);
        TradeOrderDO order = buildOrder(1002L, 10000, 0);
        productBonusService.createBonusOnReceiveOrder(order, List.of(buildItem(2002L, 1002L, 10000)));
        assertNull(productBonusRecordMapper.selectByOrderItemId(2002L));
        assertNotNull(productGmvRecordMapper.selectByOrderId(1002L));
    }

    @Test
    public void testCreateBonus_giftOrderExcluded() {
        // 礼包订单不计入 GMV、不产生差额奖（两套体系独立）
        mockDiscount(102L, 60, 101L, 40);
        TradeOrderDO order = buildOrder(1003L, 398000, 0);
        order.setType(5); // GIFT
        productBonusService.createBonusOnReceiveOrder(order, List.of(buildItem(2003L, 1003L, 398000)));
        assertNull(productBonusRecordMapper.selectByOrderItemId(2003L));
        assertNull(productGmvRecordMapper.selectByOrderId(1003L));
    }

    @Test
    public void testCreateBonus_idempotent() {
        // 验收 9：订单完成事件重复触发不重复计奖
        mockDiscount(102L, 60, 101L, 40);
        TradeOrderDO order = buildOrder(1004L, 10000, 0);
        TradeOrderItemDO item = buildItem(2004L, 1004L, 10000);
        productBonusService.createBonusOnReceiveOrder(order, List.of(item));
        productBonusService.createBonusOnReceiveOrder(order, List.of(item));
        assertEquals(1, productBonusRecordMapper.selectList(
                new LambdaQueryWrapperX<TradeProductBonusRecordDO>()
                        .eq(TradeProductBonusRecordDO::getOrderItemId, 2004L)).size());
        TradeProductGmvRecordDO gmv = productGmvRecordMapper.selectByOrderId(1004L);
        assertEquals(10000, gmv.getGmvAmount());
    }

    @Test
    public void testReverseOnAfterSale_partialAndFull() {
        // 部分退款 5000 分 → 冲回 1000 分（50%），GMV 减 5000；全额退款 → 原记录置已冲正
        mockDiscount(102L, 60, 101L, 40);
        TradeOrderDO order = buildOrder(1005L, 10000, 0);
        TradeOrderItemDO item = buildItem(2005L, 1005L, 10000);
        productBonusService.createBonusOnReceiveOrder(order, List.of(item));

        // 部分退款：售后单 AS1 退 5000
        AfterSaleDO afterSale = new AfterSaleDO();
        afterSale.setId(9001L);
        afterSale.setRefundPrice(5000);
        when(afterSaleService.getAfterSale(9001L)).thenReturn(afterSale);
        item.setAfterSaleId(9001L);
        productBonusService.reverseBonusOnAfterSale(order, item);

        TradeProductBonusRecordDO reversal = productBonusRecordMapper.selectByDedupKey(
                TradeProductBonusServiceImpl.buildDifferenceReversalDedupKey(2005L, 9001L));
        assertEquals(-1000, reversal.getAmount());
        assertEquals(TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus(), reversal.getStatus()); // 参与下期抵扣
        assertEquals(5000, productGmvRecordMapper.selectByOrderId(1005L).getGmvAmount());
        // 原记录保持待结算
        assertEquals(TradeProductBonusStatusEnum.WAIT_SETTLE.getStatus(),
                productBonusRecordMapper.selectOriginalDifferenceByOrderItemId(2005L).getStatus());
        // 重复回调幂等
        productBonusService.reverseBonusOnAfterSale(order, item);
        assertEquals(1, productBonusRecordMapper.selectList(
                new LambdaQueryWrapperX<TradeProductBonusRecordDO>()
                        .eq(TradeProductBonusRecordDO::getOrderItemId, 2005L)
                        .lt(TradeProductBonusRecordDO::getAmount, 0)).size());

        // 全额退款：售后单 AS2 退剩余 5000 → 累计全退，原记录置已冲正
        AfterSaleDO afterSale2 = new AfterSaleDO();
        afterSale2.setId(9002L);
        afterSale2.setRefundPrice(5000);
        when(afterSaleService.getAfterSale(9002L)).thenReturn(afterSale2);
        item.setAfterSaleId(9002L);
        productBonusService.reverseBonusOnAfterSale(order, item);
        assertEquals(TradeProductBonusStatusEnum.REVERSED.getStatus(),
                productBonusRecordMapper.selectOriginalDifferenceByOrderItemId(2005L).getStatus());
        assertEquals(0, productGmvRecordMapper.selectByOrderId(1005L).getGmvAmount());
    }

}

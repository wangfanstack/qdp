package cn.sailstack.qdp.module.member.service.bonus.gift;

import cn.sailstack.qdp.framework.test.core.ut.BaseDbUnitTest;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftEffectReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.*;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.*;
import cn.sailstack.qdp.module.member.enums.bonus.*;
import cn.sailstack.qdp.module.member.service.bonus.calculate.DirectBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.calculate.IncrementBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigServiceImpl;
import cn.sailstack.qdp.module.member.service.bonus.relation.MemberRelationService;
import cn.sailstack.qdp.module.member.service.bonus.relation.MemberRelationServiceImpl;
import cn.sailstack.qdp.module.member.service.user.MemberUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * {@link MemberGiftBenefitServiceImpl} 的单元测试
 *
 * 覆盖验收用例 2、3、4、9：升级差价与 PV、直推奖比例、增量奖归属与市场切换、幂等、退款冲正。
 *
 * @author sail
 */
@Import({MemberGiftBenefitServiceImpl.class, MemberGiftConfigServiceImpl.class,
        MemberBonusConfigServiceImpl.class, MemberRelationServiceImpl.class,
        DirectBonusCalculator.class, IncrementBonusCalculator.class})
public class MemberGiftBenefitServiceImplTest extends BaseDbUnitTest {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 1, 10, 0, 0);
    private static final LocalDateTime T1 = LocalDateTime.of(2026, 11, 2, 10, 0, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2026, 11, 3, 10, 0, 0);
    private static final LocalDateTime T3 = LocalDateTime.of(2026, 11, 4, 10, 0, 0);
    private static final LocalDateTime T4 = LocalDateTime.of(2026, 11, 5, 10, 0, 0);

    @Resource
    private MemberGiftBenefitService giftBenefitService;
    @Resource
    private MemberGiftConfigMapper giftConfigMapper;
    @Resource
    private MemberGiftBenefitMapper giftBenefitMapper;
    @Resource
    private MemberRelationMapper relationMapper;
    @Resource
    private MemberMarketMapper marketMapper;
    @Resource
    private MemberBonusRecordMapper bonusRecordMapper;
    @Resource
    private MemberBonusConfigMapper bonusConfigMapper;

    @MockitoBean
    private MemberUserService memberUserService;

    private Long gift598;
    private Long gift3980;

    @BeforeEach
    public void setUp() {
        // 默认规则配置（单行）
        bonusConfigMapper.insert(MemberBonusConfigDO.builder().id(1L).build());
        // 两档礼包
        MemberGiftConfigDO gift1 = MemberGiftConfigDO.builder()
                .name("会员礼包").levelName("体验官").price(59800).pvPercent(60)
                .spuId(1001L).sort(1).status(0).build();
        giftConfigMapper.insert(gift1);
        MemberGiftConfigDO gift2 = MemberGiftConfigDO.builder()
                .name("课程礼包").levelName("初级代表").price(398000).pvPercent(60)
                .spuId(1002L).sort(2).status(0).build();
        giftConfigMapper.insert(gift2);
        MemberGiftConfigDO gift3 = MemberGiftConfigDO.builder()
                .name("加盟店礼包").levelName("城市合伙人").price(3980000).pvPercent(60)
                .spuId(1003L).sort(3).status(0).build();
        giftConfigMapper.insert(gift3);
        gift598 = gift1.getId();
        gift3980 = gift2.getId();
        // 会员校验 mock
        when(memberUserService.getUser(anyLong())).thenReturn(new cn.sailstack.qdp.module.member.dal.dataobject.user.MemberUserDO());
    }

    private void bindRelation(Long userId, Long referrerId) {
        relationMapper.insert(MemberRelationDO.builder()
                .userId(userId).referrerId(referrerId).bindTime(T0.minusDays(10)).build());
        // 确保推荐人占位行存在（老板身份判定沿链向上需要）
        if (relationMapper.selectByUserId(referrerId) == null) {
            relationMapper.insert(MemberRelationDO.builder().userId(referrerId).build());
        }
    }

    private MemberGiftEffectReqDTO buildEffect(Long orderId, Long userId, Long spuId, int payAmountFen, LocalDateTime payTime) {
        return new MemberGiftEffectReqDTO()
                .setOrderId(orderId).setOrderNo("NO" + orderId).setUserId(userId)
                .setSpuId(spuId).setPayAmount(payAmountFen).setPayTime(payTime);
    }

    @Test
    public void testEffectGiftOrder_firstBuy_directBonus30() {
        // A(101) 推荐 B(102)；B 首次购买 598 → PV 359，A 直推奖 30% = 107.7 元
        bindRelation(102L, 101L);
        giftBenefitService.effectGiftOrder(buildEffect(1001L, 102L, 1001L, 59800, T1));

        MemberGiftBenefitDO benefit = giftBenefitMapper.selectByOrderId(1001L);
        assertEquals(359, benefit.getPv());
        assertEquals(MemberGiftBenefitTypeEnum.NEW.getType(), benefit.getType());
        assertEquals(102L, benefit.getUserId());
        // 直推奖：第 1 名 30%
        List<MemberBonusRecordDO> directs = bonusRecordMapper.selectListBySourceOrderId(1001L).stream()
                .filter(r -> MemberBonusTypeEnum.DIRECT.getType().equals(r.getBonusType())).toList();
        assertEquals(1, directs.size());
        assertEquals(101L, directs.get(0).getUserId());
        assertEquals(30, directs.get(0).getRate());
        assertEquals(10770, directs.get(0).getAmount()); // 359 元 × 30% = 107.70 元
        // A 非老板且链上无老板 → 无增量奖
        assertEquals(0, bonusRecordMapper.selectListBySourceOrderId(1001L).stream()
                .filter(r -> MemberBonusTypeEnum.INCREMENT.getType().equals(r.getBonusType())).count());
    }

    @Test
    public void testEffectGiftOrder_thirdDirect_directBonus55() {
        // 验收 3：A 推荐 B、C、D → B、C 30%；D（第 3 名）55%
        bindRelation(102L, 101L);
        bindRelation(103L, 101L);
        bindRelation(104L, 101L);
        giftBenefitService.effectGiftOrder(buildEffect(2001L, 102L, 1001L, 59800, T1));
        giftBenefitService.effectGiftOrder(buildEffect(2002L, 103L, 1001L, 59800, T2));
        // C 是第 2 名 → A 取得老板身份
        MemberRelationDO relationA = relationMapper.selectByUserId(101L);
        assertNotNull(relationA.getBossTime());
        assertNotNull(relationA.getMarketId());
        assertEquals(1, marketMapper.selectByOwnerUserId(101L) != null ? 1 : 0);
        // D 第 3 名 55%（验收 3：D 购买 39800 加盟店礼包，PV=23880，55% → 13134 元）
        giftBenefitService.effectGiftOrder(buildEffect(2003L, 104L, 1003L, 3980000, T3));
        MemberBonusRecordDO dDirect = bonusRecordMapper.selectByDedupKey(DirectBonusCalculator.buildDedupKey(2003L));
        assertEquals(55, dDirect.getRate());
        assertEquals(1313400, dDirect.getAmount()); // 23880 元 × 55% = 13134 元
        // D 是 A 的第 3 名直推 → 不产生增量奖（即使 A 已是老板）
        assertNull(bonusRecordMapper.selectByDedupKey(IncrementBonusCalculator.buildDedupKey(2003L)));
    }

    @Test
    public void testEffectGiftOrder_incrementBonus_and_marketSwitch() {
        // 验收 4：A 是老板（市场 M_A），B 属于 A 市场
        bindRelation(102L, 101L);
        // 造 A 的老板身份（早于全部业务时间）
        MemberRelationDO relationA = relationMapper.selectByUserId(101L);
        relationMapper.updateById(new MemberRelationDO().setId(relationA.getId())
                .setBossTime(T0.minusDays(1)).setMarketId(999L));
        // ① X(103) 是 B 的第 1 名直推 → 增量奖归 A（25%）
        bindRelation(103L, 102L);
        giftBenefitService.effectGiftOrder(buildEffect(3001L, 103L, 1001L, 59800, T1));
        MemberBonusRecordDO inc1 = bonusRecordMapper.selectByDedupKey(IncrementBonusCalculator.buildDedupKey(3001L));
        assertNotNull(inc1);
        assertEquals(101L, inc1.getUserId());   // 归属 A
        assertEquals(999L, inc1.getMarketId());
        assertEquals(25, inc1.getRate());
        assertEquals(359 * 25, inc1.getAmount()); // 359 元 × 25%
        // ② Y(104) 是 B 的第 2 名直推 → B 成为老板；该笔业绩仍归 A（需求 5.4）
        bindRelation(104L, 102L);
        giftBenefitService.effectGiftOrder(buildEffect(3002L, 104L, 1001L, 59800, T2));
        MemberBonusRecordDO inc2 = bonusRecordMapper.selectByDedupKey(IncrementBonusCalculator.buildDedupKey(3002L));
        assertNotNull(inc2);
        assertEquals(101L, inc2.getUserId());   // 仍归 A，不归刚成为老板的 B
        // B 取得老板身份，建立自己的市场
        MemberRelationDO relationB = relationMapper.selectByUserId(102L);
        assertEquals(T2, relationB.getBossTime());
        assertNotNull(relationB.getMarketId());
        // ③ W(105) 由 X(103) 推荐，是 X 的第 1 名直推 → 增量奖归 B（B 新市场），不再归 A
        bindRelation(105L, 103L);
        giftBenefitService.effectGiftOrder(buildEffect(3003L, 105L, 1001L, 59800, T3));
        MemberBonusRecordDO inc3 = bonusRecordMapper.selectByDedupKey(IncrementBonusCalculator.buildDedupKey(3003L));
        assertNotNull(inc3);
        assertEquals(102L, inc3.getUserId());   // 归 B（最近有效老板）
        assertEquals(relationB.getMarketId(), inc3.getMarketId());
        // ④ Z(106) 是 B 的第 3 名直推 → 不纳入增量奖
        bindRelation(106L, 102L);
        giftBenefitService.effectGiftOrder(buildEffect(3004L, 106L, 1001L, 59800, T4));
        assertNull(bonusRecordMapper.selectByDedupKey(IncrementBonusCalculator.buildDedupKey(3004L)));
    }

    @Test
    public void testEffectGiftOrder_idempotent() {
        // 验收 9：重复回调不重复生效、不重复计奖
        bindRelation(102L, 101L);
        MemberGiftEffectReqDTO reqDTO = buildEffect(4001L, 102L, 1001L, 59800, T1);
        giftBenefitService.effectGiftOrder(reqDTO);
        giftBenefitService.effectGiftOrder(reqDTO);
        giftBenefitService.effectGiftOrder(reqDTO);
        assertEquals(1, giftBenefitMapper.selectListByUserId(102L).size());
        assertEquals(1, bonusRecordMapper.selectListBySourceOrderId(4001L).size());
    }

    @Test
    public void testEffectGiftOrder_upgrade_diffPrice() {
        // 验收 2：598 升级 3980，补差 3382 → 新增 PV 2029；原 598 的 PV 不重复计入
        bindRelation(102L, 101L);
        giftBenefitService.effectGiftOrder(buildEffect(5001L, 102L, 1001L, 59800, T1));
        giftBenefitService.effectGiftOrder(buildEffect(5002L, 102L, 1002L, 338200, T2));

        List<MemberGiftBenefitDO> benefits = giftBenefitMapper.selectListByUserId(102L);
        assertEquals(2, benefits.size());
        MemberGiftBenefitDO upgrade = giftBenefitMapper.selectByOrderId(5002L);
        assertEquals(MemberGiftBenefitTypeEnum.UPGRADE.getType(), upgrade.getType());
        assertEquals(2029, upgrade.getPv());          // 3382 × 60% = 2029.2 → 2029
        assertEquals(338200, upgrade.getAddedAmount());
        assertEquals(59800, upgrade.getPaidAmount()); // 升级前累计已购
        // 升级订单仍是有效订单：直推奖按 B 的既有顺序（第 1 名 30%）
        MemberBonusRecordDO upgradeDirect = bonusRecordMapper.selectByDedupKey(DirectBonusCalculator.buildDedupKey(5002L));
        assertEquals(30, upgradeDirect.getRate());
        assertEquals(2029 * 30, upgradeDirect.getAmount());
        // 不重复计入直推顺序
        assertEquals(1, relationMapper.selectByUserId(102L).getValidSeq());
    }

    @Test
    public void testRefundGiftOrder_fullReversal() {
        // 全额退款：PV 冲减、直推奖冲正、权益置已冲减
        bindRelation(102L, 101L);
        giftBenefitService.effectGiftOrder(buildEffect(6001L, 102L, 1001L, 59800, T1));
        giftBenefitService.refundGiftOrder(new MemberGiftRefundReqDTO()
                .setOrderId(6001L).setUserId(102L).setRefundAmount(59800)
                .setRefundBizId("AS6001").setReason("全额退款"));

        MemberGiftBenefitDO benefit = giftBenefitMapper.selectByOrderId(6001L);
        assertEquals(59800, benefit.getReversalAmount());
        assertEquals(359, benefit.getReversalPv());
        assertEquals(MemberGiftBenefitStatusEnum.REVERSED.getStatus(), benefit.getStatus());
        // 原直推奖置已冲正；生成负数冲正记录（全额退款：负记录仅追溯，不参与结算）
        MemberBonusRecordDO original = bonusRecordMapper.selectByDedupKey(DirectBonusCalculator.buildDedupKey(6001L));
        assertEquals(MemberBonusStatusEnum.REVERSED.getStatus(), original.getStatus());
        MemberBonusRecordDO reversal = bonusRecordMapper.selectByDedupKey(
                DirectBonusCalculator.buildReversalDedupKey(6001L, "AS6001"));
        assertNotNull(reversal);
        assertEquals(-10770, reversal.getAmount());
        assertEquals(MemberBonusStatusEnum.REVERSED.getStatus(), reversal.getStatus());
        assertEquals(original.getId(), reversal.getReversalOfId());
        // 重复退款回调幂等
        giftBenefitService.refundGiftOrder(new MemberGiftRefundReqDTO()
                .setOrderId(6001L).setUserId(102L).setRefundAmount(59800)
                .setRefundBizId("AS6001").setReason("全额退款"));
        assertEquals(1, bonusRecordMapper.selectListBySourceOrderId(6001L).stream()
                .filter(r -> r.getAmount() < 0).count());
    }

    @Test
    public void testRefundGiftOrder_partialReversal() {
        // 部分退款：按比例冲减，负数冲正参与下期结算抵扣
        bindRelation(102L, 101L);
        giftBenefitService.effectGiftOrder(buildEffect(7001L, 102L, 1001L, 59800, T1));
        giftBenefitService.refundGiftOrder(new MemberGiftRefundReqDTO()
                .setOrderId(7001L).setUserId(102L).setRefundAmount(29900)  // 退一半
                .setRefundBizId("AS7001").setReason("部分退款"));

        MemberGiftBenefitDO benefit = giftBenefitMapper.selectByOrderId(7001L);
        assertEquals(MemberGiftBenefitStatusEnum.EFFECT.getStatus(), benefit.getStatus());
        assertEquals(29900, benefit.getReversalAmount());
        // 冲减 PV 按累计冲减金额重算：299 元 × 60% = 179.4 → 179
        assertEquals(179, benefit.getReversalPv());
        MemberBonusRecordDO reversal = bonusRecordMapper.selectByDedupKey(
                DirectBonusCalculator.buildReversalDedupKey(7001L, "AS7001"));
        assertEquals(-5385, reversal.getAmount()); // 10770 × 50%
        assertEquals(MemberBonusStatusEnum.WAIT_SETTLE.getStatus(), reversal.getStatus()); // 参与抵扣
        // 原记录保持待结算
        assertEquals(MemberBonusStatusEnum.WAIT_SETTLE.getStatus(),
                bonusRecordMapper.selectByDedupKey(DirectBonusCalculator.buildDedupKey(7001L)).getStatus());
    }

    @Test
    public void testPrepareGiftBuy_notHigherLevel() {
        // 只可购买更高档位：已购 3980 后买 598 被拒绝
        bindRelation(102L, 101L);
        giftBenefitService.effectGiftOrder(buildEffect(8001L, 102L, 1002L, 398000, T1));
        assertThrows(cn.sailstack.qdp.framework.common.exception.ServiceException.class,
                () -> giftBenefitService.prepareGiftBuy(102L, gift598));
    }

}

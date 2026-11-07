package cn.sailstack.qdp.module.trade.service.productbonus;

import cn.sailstack.qdp.framework.test.core.ut.BaseDbUnitTest;
import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberBonusConfigRespDTO;
import cn.sailstack.qdp.module.pay.api.wallet.PayWalletApi;
import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusRecordDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusSettleBatchDO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductGmvRecordDO;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductBonusRecordMapper;
import cn.sailstack.qdp.module.trade.dal.mysql.productbonus.TradeProductGmvRecordMapper;
import cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * {@link TradeProductBonusSettleServiceImpl} 的单元测试
 *
 * 覆盖验收用例 8：GMV 100 万 → 奖金池 10 万 → 五级预算 1万/7千/5千/3千/2千元，
 * 公司留存 7.3 万；同级平均分配。
 *
 * @author sail
 */
@Import(TradeProductBonusSettleServiceImpl.class)
public class TradeProductBonusSettleServiceImplTest extends BaseDbUnitTest {

    private static final String PERIOD = "2026-11";

    @Resource
    private TradeProductBonusSettleService settleService;
    @Resource
    private TradeProductBonusRecordMapper productBonusRecordMapper;
    @Resource
    private TradeProductGmvRecordMapper productGmvRecordMapper;

    @MockitoBean
    private MemberBonusApi memberBonusApi;
    @MockitoBean
    private PayWalletApi payWalletApi;

    @BeforeEach
    public void setUp() {
        MemberBonusConfigRespDTO config = new MemberBonusConfigRespDTO();
        config.setDirectorOneRate(10);
        config.setDirectorTwoRate(7);
        config.setDirectorThreeRate(5);
        config.setDirectorFourRate(3);
        config.setDirectorFiveRate(2);
        config.setProductPoolPercent(10);
        config.setEmptyLevelPolicy("KEEP");
        config.setRoundingMode("HALF_UP");
        config.setVersion(1);
        when(memberBonusApi.getBonusConfig()).thenReturn(config);
    }

    private void insertGmv(Long orderId, int gmvFen) {
        productGmvRecordMapper.insert(TradeProductGmvRecordDO.builder()
                .orderId(orderId).orderNo("NO" + orderId).userId(1000L + orderId)
                .payAmount(gmvFen).refundAmount(0).gmvAmount(gmvFen)
                .period(PERIOD).finishTime(LocalDateTime.now()).build());
    }

    @Test
    public void testSettleMonth_gmvOneMillion() {
        // 验收 8：GMV 100 万元 → 奖金池 10 万；一星 3 人各 3333.33 元；二星~五星各 1 人拿满预算
        insertGmv(1L, 50000000);
        insertGmv(2L, 50000000);
        when(memberBonusApi.getDirectorUserIds(1)).thenReturn(List.of(201L, 202L, 203L));
        when(memberBonusApi.getDirectorUserIds(2)).thenReturn(List.of(301L));
        when(memberBonusApi.getDirectorUserIds(3)).thenReturn(List.of(401L));
        when(memberBonusApi.getDirectorUserIds(4)).thenReturn(List.of(501L));
        when(memberBonusApi.getDirectorUserIds(5)).thenReturn(List.of(601L));

        TradeProductBonusSettleBatchDO batch = settleService.settleMonth(PERIOD);

        assertEquals(100000000, batch.getGmvAmount());   // GMV 100 万元
        assertEquals(10000000, batch.getBonusPool());    // 奖金池 10 万（GMV × 10%）
        assertEquals(7300000, batch.getCompanyAmount()); // 公司留存 7.3 万（池 × 73%）
        // 各等级预算：一星 1000000 分（1 万元）、二星 700000、三星 500000、四星 300000、五星 200000
        List<TradeProductBonusRecordDO> records = productBonusRecordMapper.selectListByPeriod(PERIOD).stream()
                .filter(r -> TradeProductBonusTypeEnum.DIRECTOR.getType().equals(r.getBonusType())).toList();
        assertEquals(7, records.size());
        // 一星 3 人平均：10000 元 / 3 = 3333.33 → 333333 分
        records.stream().filter(r -> r.getDirectorLevel().equals(1))
                .forEach(r -> assertEquals(333333, r.getAmount()));
        // 二星~五星各 1 人拿满等级预算
        assertEquals(700000, records.stream().filter(r -> r.getDirectorLevel().equals(2))
                .findFirst().orElseThrow().getAmount());
        assertEquals(500000, records.stream().filter(r -> r.getDirectorLevel().equals(3))
                .findFirst().orElseThrow().getAmount());
        assertEquals(300000, records.stream().filter(r -> r.getDirectorLevel().equals(4))
                .findFirst().orElseThrow().getAmount());
        assertEquals(200000, records.stream().filter(r -> r.getDirectorLevel().equals(5))
                .findFirst().orElseThrow().getAmount());
        // 分配合计 = 27% × 池（HALF_UP 误差允许 ±1 分）
        int total = records.stream().mapToInt(TradeProductBonusRecordDO::getAmount).sum();
        assertTrue(Math.abs(total - 2700000) <= 1);
    }

    @Test
    public void testSettleMonth_emptyLevelKept() {
        // 空档等级预算留存公司（策略 KEEP），备注说明
        insertGmv(3L, 10000000);
        when(memberBonusApi.getDirectorUserIds(1)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(2)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(3)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(4)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(5)).thenReturn(List.of());

        TradeProductBonusSettleBatchDO batch = settleService.settleMonth(PERIOD);
        assertEquals(1000000, batch.getBonusPool());
        assertEquals(0, batch.getTotalAmount());
        assertTrue(batch.getRemark().contains("一星董事"));
        assertTrue(batch.getRemark().contains("KEEP"));
    }

    @Test
    public void testPayBatch_idempotent() {
        insertGmv(4L, 1000000);
        when(memberBonusApi.getDirectorUserIds(1)).thenReturn(List.of(201L));
        when(memberBonusApi.getDirectorUserIds(2)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(3)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(4)).thenReturn(List.of());
        when(memberBonusApi.getDirectorUserIds(5)).thenReturn(List.of());
        TradeProductBonusSettleBatchDO batch = settleService.settleMonth(PERIOD);

        settleService.payBatch(batch.getBatchNo());
        settleService.payBatch(batch.getBatchNo()); // 幂等
        org.mockito.Mockito.verify(payWalletApi, org.mockito.Mockito.times(1))
                .addWalletBalance(org.mockito.ArgumentMatchers.argThat(
                        (PayWalletAddBalanceReqDTO req) -> req.getBizId().equals(batch.getBatchNo() + ":201")
                                && req.getPrice().equals(10000))); // 池 10 万 × 10% = 1 万元
    }

}

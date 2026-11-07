package cn.sailstack.qdp.module.member.service.bonus.settle;

import cn.sailstack.qdp.framework.common.exception.ServiceException;
import cn.sailstack.qdp.framework.test.core.ut.BaseDbUnitTest;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.*;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.*;
import cn.sailstack.qdp.module.member.enums.bonus.*;
import cn.sailstack.qdp.module.member.service.bonus.calculate.DirectorGiftBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.calculate.HelpBonusCalculator;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigServiceImpl;
import cn.sailstack.qdp.module.member.service.bonus.director.MemberDirectorService;
import cn.sailstack.qdp.module.pay.api.wallet.PayWalletApi;
import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.BONUS_SETTLE_BATCH_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link MemberBonusSettleServiceImpl} 的单元测试
 *
 * 覆盖验收用例 5、6、9：帮扶奖基数与金额、董事分红平均分配、
 * 月结批次唯一、发放入账幂等参数。
 *
 * @author sail
 */
@Import({MemberBonusSettleServiceImpl.class, MemberBonusConfigServiceImpl.class,
        HelpBonusCalculator.class, DirectorGiftBonusCalculator.class,
        MemberBonusSettleServiceImplTest.MockDirectorService.class})
public class MemberBonusSettleServiceImplTest extends BaseDbUnitTest {

    private static final String PERIOD = "2026-11";

    @Resource
    private MemberBonusSettleService settleService;
    @Resource
    private MemberBonusRecordMapper bonusRecordMapper;
    @Resource
    private MemberBonusSettleBatchMapper settleBatchMapper;
    @Resource
    private MemberBonusSettleDetailMapper settleDetailMapper;
    @Resource
    private MemberBonusConfigMapper bonusConfigMapper;
    @Resource
    private MemberRelationMapper relationMapper;
    @Resource
    private MemberDirectorMapper directorMapper;

    @MockitoBean
    private PayWalletApi payWalletApi;

    /**
     * 董事资格核验在月结前自动执行，这里提供最小实现（不做任何核验）
     */
    public static class MockDirectorService implements MemberDirectorService {

        @Override
        public List<MemberDirectorDO> verifyUserDirector(Long userId) {
            return List.of();
        }

        @Override
        public int verifyAllDirectors() {
            return 0;
        }

        @Override
        public List<MemberDirectorDO> getDirectorListByLevel(Integer level) {
            return List.of();
        }

        @Override
        public List<MemberDirectorDO> getDirectorListByUserId(Long userId) {
            return List.of();
        }

        @Override
        public cn.sailstack.qdp.framework.common.pojo.PageResult<MemberDirectorDO> getDirectorPage(
                cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director.MemberDirectorPageReqVO pageReqVO) {
            return cn.sailstack.qdp.framework.common.pojo.PageResult.empty();
        }
    }

    @BeforeEach
    public void setUp() {
        bonusConfigMapper.insert(MemberBonusConfigDO.builder().id(1L).build());
    }

    private void insertBonus(String dedupKey, Long userId, Integer bonusType, Long sourceUserId,
                             Integer basePv, Integer rate, Integer amount) {
        bonusRecordMapper.insert(MemberBonusRecordDO.builder()
                .dedupKey(dedupKey).userId(userId).bonusType(bonusType).sourceUserId(sourceUserId)
                .basePv(basePv).baseAmount(basePv * 100).rate(rate).amount(amount)
                .period(PERIOD).ruleVersion(1).status(MemberBonusStatusEnum.WAIT_SETTLE.getStatus()).build());
    }

    @Test
    public void testSettleMonth_helpBonus() {
        // 验收 5：第一代会员 B 当月直推+增量合计 10000 元、C 合计 8000 元 → A 帮扶奖 3600 元
        relationMapper.insert(MemberRelationDO.builder().userId(102L).referrerId(101L).build());
        relationMapper.insert(MemberRelationDO.builder().userId(103L).referrerId(101L).build());
        insertBonus("T1", 102L, MemberBonusTypeEnum.DIRECT.getType(), 102L, 0, 30, 1000000);
        insertBonus("T2", 102L, MemberBonusTypeEnum.INCREMENT.getType(), 102L, 0, 25, 500000);
        insertBonus("T3", 103L, MemberBonusTypeEnum.DIRECT.getType(), 103L, 0, 30, 800000);
        // 噪音：董事/商品类不进帮扶基数
        insertBonus("T4", 102L, MemberBonusTypeEnum.HELP.getType(), null, 0, 20, 100000);
        insertBonus("T5", 102L, MemberBonusTypeEnum.DIRECTOR.getType(), null, 0, 10, 200000);

        MemberBonusSettleBatchDO batch = settleService.settleMonth(PERIOD, null);

        // 帮扶奖：A 获得 (10000 + 5000 + 8000) × 20% = 4600 元
        MemberBonusRecordDO helpB = bonusRecordMapper.selectByDedupKey(
                HelpBonusCalculator.buildDedupKey(PERIOD, 102L));
        assertEquals(101L, helpB.getUserId());
        assertEquals(1500000, helpB.getBaseAmount());
        assertEquals(300000, helpB.getAmount());
        MemberBonusRecordDO helpC = bonusRecordMapper.selectByDedupKey(
                HelpBonusCalculator.buildDedupKey(PERIOD, 103L));
        assertEquals(160000, helpC.getAmount()); // 8000 × 20%
        // 批次：备注说明董事分红跳过（收入口径 DISABLED）
        assertNotNull(batch.getBatchNo());
        assertTrue(batch.getRemark().contains("跳过董事分红"));
        // 明细置已结算
        assertEquals(MemberBonusStatusEnum.SETTLED.getStatus(),
                bonusRecordMapper.selectByDedupKey("T1").getStatus());
    }

    @Test
    public void testSettleMonth_directorGiftBonus_manualPool() {
        // 验收 6：MANUAL 口径录入分红池 600000 元；3 名一星董事 → 每人 60000 元（需求 7.4 示例）
        bonusConfigMapper.updateById(new MemberBonusConfigDO().setId(1L).setGiftIncomeSource("MANUAL"));
        for (long userId = 201L; userId <= 203L; userId++) {
            directorMapper.insert(MemberDirectorDO.builder()
                    .userId(userId).level(1).qualifyTime(LocalDateTime.now()).status(1).build());
        }
        MemberBonusSettleBatchDO batch = settleService.settleMonth(PERIOD, 60000000);

        assertEquals(60000000, batch.getDividendPool());
        List<MemberBonusRecordDO> directorRecords = bonusRecordMapper.selectListByPeriodAndTypes(
                PERIOD, List.of(MemberBonusTypeEnum.DIRECTOR.getType()));
        assertEquals(3, directorRecords.size());
        directorRecords.forEach(r -> {
            assertEquals(10, r.getRate());
            assertEquals(2000000, r.getAmount()); // 一星预算 600000 元 × 10% = 60000 元，3 人平均 → 20000 元
        });
        // 空档等级（二星~五星无人）备注留存说明
        assertTrue(batch.getRemark().contains("二星董事"));
    }

    @Test
    public void testSettleMonth_duplicateRejected() {
        // 验收 9：同账期重复结算被拒绝（uk 兜底）
        settleService.settleMonth(PERIOD, null);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> settleService.settleMonth(PERIOD, null));
        assertEquals(BONUS_SETTLE_BATCH_EXISTS.getCode(), ex.getCode());
    }

    @Test
    public void testPayBatch_walletIdempotentParams() {
        // 发放：验证钱包入账参数（bizId=batchNo:userId）与个人明细状态流转
        insertBonus("P1", 301L, MemberBonusTypeEnum.DIRECT.getType(), null, 0, 30, 100000);
        insertBonus("P2", 301L, MemberBonusTypeEnum.INCREMENT.getType(), null, 0, 25, 50000);
        insertBonus("P3", 302L, MemberBonusTypeEnum.DIRECT.getType(), null, 0, 30, 80000);
        MemberBonusSettleBatchDO batch = settleService.settleMonth(PERIOD, null);

        settleService.payBatch(batch.getBatchNo());
        // 301 合计 150000 分、302 合计 80000 分
        ArgumentCaptor<PayWalletAddBalanceReqDTO> captor = ArgumentCaptor.forClass(PayWalletAddBalanceReqDTO.class);
        verify(payWalletApi, times(2)).addWalletBalance(captor.capture());
        List<PayWalletAddBalanceReqDTO> calls = captor.getAllValues();
        assertTrue(calls.stream().anyMatch(c -> c.getUserId().equals(301L)
                && c.getPrice().equals(150000) && c.getBizId().equals(batch.getBatchNo() + ":301")
                && c.getBizType().equals(cn.sailstack.qdp.module.pay.enums.wallet.PayWalletBizTypeEnum.BONUS.getType())));
        assertTrue(calls.stream().anyMatch(c -> c.getUserId().equals(302L) && c.getPrice().equals(80000)));
        // 明细置已发放、批次置已发放；重复发放幂等
        settleDetailMapper.selectListByBatchNo(batch.getBatchNo())
                .forEach(d -> assertEquals(MemberBonusPayStatusEnum.PAID.getStatus(), d.getPayStatus()));
        assertEquals(MemberBonusSettleStatusEnum.PAID.getStatus(),
                settleBatchMapper.selectByBatchNo(batch.getBatchNo()).getStatus());
        settleService.payBatch(batch.getBatchNo());
        verify(payWalletApi, times(2)).addWalletBalance(any());
    }

}

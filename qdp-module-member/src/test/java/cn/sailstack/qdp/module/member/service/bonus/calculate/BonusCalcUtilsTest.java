package cn.sailstack.qdp.module.member.service.bonus.calculate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BonusCalcUtils} 的单元测试
 *
 * 对应验收用例 1、2、5：PV 四档基准、升级补差 PV、帮扶奖金额
 *
 * @author sail
 */
public class BonusCalcUtilsTest {

    @Test
    public void testCalcPv_fourGiftLevels() {
        // 验收基准：598→359；3,980→2,388；12,800→7,680；39,800→23,880
        assertEquals(359, BonusCalcUtils.calcPv(59800, 60));
        assertEquals(2388, BonusCalcUtils.calcPv(398000, 60));
        assertEquals(7680, BonusCalcUtils.calcPv(1280000, 60));
        assertEquals(23880, BonusCalcUtils.calcPv(3980000, 60));
    }

    @Test
    public void testCalcPv_upgradeDiff() {
        // 验收基准：598 升级 3,980，补差 3,382 → 新增 PV = 3382 × 60% = 2029.2 → 四舍五入 2029
        assertEquals(2029, BonusCalcUtils.calcPv(338200, 60));
    }

    @Test
    public void testCalcAmount_directBonus() {
        // 验收基准：A 推荐 D（第 3 名及以后 55%），D 购买 39800 礼包 PV=23880 → 直推奖 13134 元
        assertEquals(1313400, BonusCalcUtils.calcAmount(23880 * 100, 55));
        // 第 1、2 名 30%
        assertEquals(10770, BonusCalcUtils.calcAmount(359 * 100, 30));
    }

    @Test
    public void testCalcAmount_incrementBonus() {
        // 增量奖 25%：23880 PV → 5970 元
        assertEquals(597000, BonusCalcUtils.calcAmount(23880 * 100, 25));
    }

    @Test
    public void testCalcAmount_helpBonus() {
        // 验收基准：第一代会员当月直推+增量合计 10000 与 8000 元 → 上级 18000 × 20% = 3600 元
        assertEquals(360000, BonusCalcUtils.calcAmount(1800000, 20));
    }

    @Test
    public void testCalcAvgAmount() {
        // 平均分配：600000 元预算 10 人 → 每人 60000 元（需求 7.4 示例）
        assertEquals(6000000, BonusCalcUtils.calcAvgAmount(60000000, 10));
        // 除不尽时 HALF_UP 到分
        assertEquals(333333, BonusCalcUtils.calcAvgAmount(1000000, 3));
    }

    @Test
    public void testPvToFen() {
        assertEquals(2388000, BonusCalcUtils.pvToFen(23880));
    }

}

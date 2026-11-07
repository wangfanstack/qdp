package cn.sailstack.qdp.module.member.service.bonus.calculate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 奖金纯计算工具
 *
 * 输入显式、输出可解释；PV 四舍五入取整数元（需求 2.1），
 * 其余金额按配置的舍入模式（默认 HALF_UP）精确到分（待业务确认项）。
 *
 * @author sail
 */
public class BonusCalcUtils {

    /**
     * 计算 PV：有效金额（分）× PV 比例，四舍五入取整数元
     *
     * 验收基准：598 元→359；3,980→2,388；12,800→7,680；39,800→23,880；补差 3,382→2,029
     *
     * @param payAmountFen 有效金额（分）
     * @param pvPercent    PV 比例（百分比，例如 60）
     * @return PV（元，整数）
     */
    public static int calcPv(int payAmountFen, int pvPercent) {
        // 分 → 元 → × 比例 → 四舍五入取整
        return BigDecimal.valueOf(payAmountFen)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(pvPercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    /**
     * 按基数（分）与比例（百分比）计算金额，HALF_UP 精确到分
     *
     * @param baseAmountFen 基数（分）
     * @param ratePercent   比例（百分比，例如 55）
     * @return 金额（分）
     */
    public static int calcAmount(int baseAmountFen, int ratePercent) {
        return calcAmount(baseAmountFen, ratePercent, RoundingMode.HALF_UP.name());
    }

    /**
     * 按基数（分）与比例（百分比）计算金额，按配置舍入模式精确到分
     *
     * @param baseAmountFen 基数（分）
     * @param ratePercent   比例（百分比）
     * @param roundingMode  舍入模式名（HALF_UP / FLOOR 等，待业务确认）
     * @return 金额（分）
     */
    public static int calcAmount(int baseAmountFen, int ratePercent, String roundingMode) {
        RoundingMode mode;
        try {
            mode = RoundingMode.valueOf(roundingMode);
        } catch (IllegalArgumentException ex) {
            mode = RoundingMode.HALF_UP;
        }
        return BigDecimal.valueOf(baseAmountFen)
                .multiply(BigDecimal.valueOf(ratePercent))
                .divide(BigDecimal.valueOf(100), 0, mode)
                .intValue();
    }

    /**
     * 平均分配预算：预算（分）÷ 人数，HALF_UP 精确到分
     *
     * @param budgetFen 预算（分）
     * @param count     人数
     * @return 每人金额（分）
     */
    public static int calcAvgAmount(int budgetFen, int count) {
        return BigDecimal.valueOf(budgetFen)
                .divide(BigDecimal.valueOf(count), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    /**
     * PV（元）转分，用于奖金基数换算（1 PV = 1 元人民币）
     *
     * @param pv PV（元）
     * @return 基数（分）
     */
    public static int pvToFen(int pv) {
        return pv * 100;
    }

}

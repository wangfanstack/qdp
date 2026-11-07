package cn.sailstack.qdp.module.member.api.bonus.dto;

import lombok.Data;

/**
 * 奖金规则配置 Response DTO（跨模块精简版，供 trade 商品奖金结算读取）
 *
 * @author sail
 */
@Data
public class MemberBonusConfigRespDTO {

    /**
     * 一星董事分红比例（百分比）
     */
    private Integer directorOneRate;
    /**
     * 二星董事分红比例（百分比）
     */
    private Integer directorTwoRate;
    /**
     * 三星董事分红比例（百分比）
     */
    private Integer directorThreeRate;
    /**
     * 四星董事分红比例（百分比）
     */
    private Integer directorFourRate;
    /**
     * 五星董事分红比例（百分比）
     */
    private Integer directorFiveRate;
    /**
     * 商品奖金池占 GMV 比例（百分比）
     */
    private Integer productPoolPercent;
    /**
     * 空档等级预算处理策略（KEEP 留存公司），【待业务确认】
     */
    private String emptyLevelPolicy;
    /**
     * 金额舍入模式，【待业务确认】
     */
    private String roundingMode;
    /**
     * 月度结算日，【待业务确认】
     */
    private Integer settleDay;
    /**
     * 规则版本
     */
    private Integer version;

    /**
     * 按董事等级取分红比例
     */
    public Integer getDirectorRate(Integer level) {
        return switch (level) {
            case 1 -> directorOneRate;
            case 2 -> directorTwoRate;
            case 3 -> directorThreeRate;
            case 4 -> directorFourRate;
            case 5 -> directorFiveRate;
            default -> throw new IllegalArgumentException("director level invalid: " + level);
        };
    }

}

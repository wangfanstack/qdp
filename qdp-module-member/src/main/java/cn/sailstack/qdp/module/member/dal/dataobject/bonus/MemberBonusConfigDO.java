package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 奖金规则配置 DO（单行，id 固定 1）
 *
 * 各比例来自需求规格说明书；标注【待业务确认】的口径项在上线前不得擅自调整默认值，
 * 未确认前相关自动结算不执行。每次修改 version 自增，历史奖金按产生时版本追溯。
 *
 * @author sail
 */
@TableName("member_bonus_config")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBonusConfigDO extends TenantBaseDO {

    /**
     * 单行配置固定编号
     */
    public static final Long ID = 1L;

    /**
     * 编号（固定 1）
     */
    @TableId
    private Long id;
    /**
     * PV 比例（百分比，需求 60）
     */
    private Integer pvPercent;
    /**
     * 直推奖比例：第 1、2 名有效直推（百分比）
     */
    private Integer directFirstRate;
    /**
     * 直推奖比例：第 3 名及以后（百分比）
     */
    private Integer directAfterRate;
    /**
     * 市场增量奖比例（百分比）
     */
    private Integer incrementRate;
    /**
     * 第一代帮扶奖比例（百分比）
     */
    private Integer helpRate;
    /**
     * 一星董事礼包分红比例（百分比）
     */
    private Integer directorOneRate;
    /**
     * 二星董事礼包分红比例（百分比）
     */
    private Integer directorTwoRate;
    /**
     * 三星董事礼包分红比例（百分比）
     */
    private Integer directorThreeRate;
    /**
     * 四星董事礼包分红比例（百分比）
     */
    private Integer directorFourRate;
    /**
     * 五星董事礼包分红比例（百分比）
     */
    private Integer directorFiveRate;
    /**
     * 礼包董事分红池占公司当月收入比例（百分比）
     */
    private Integer giftPoolPercent;
    /**
     * 礼包分红池公司收入口径（DISABLED 不结算 / MANUAL 月结时手工录入），【待业务确认】
     */
    private String giftIncomeSource;
    /**
     * 董事资格所需礼包编号（默认指向最高档礼包，【待业务确认】）
     */
    private Long directorQualifyGiftId;
    /**
     * 一星董事晋升：推荐该礼包数量（【待业务确认】关系范围）
     */
    private Integer directorOneStarCount;
    /**
     * 一星晋升"推荐"关系范围（DIRECT 仅直推 / TEAM 团队），【待业务确认】
     * <p>
     * 参考 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberDirectorScopeEnum}
     */
    private String directorQualifyScope;
    /**
     * 二星及以上"培养一星董事"关系范围（DIRECT 仅直推 / TEAM 团队），【待业务确认】
     */
    private String directorTraineeScope;
    /**
     * 二星：培养一星董事数量
     */
    private Integer directorTwoStarTrainees;
    /**
     * 三星：培养一星董事数量
     */
    private Integer directorThreeStarTrainees;
    /**
     * 四星：培养一星董事数量
     */
    private Integer directorFourStarTrainees;
    /**
     * 五星：培养一星董事数量
     */
    private Integer directorFiveStarTrainees;
    /**
     * 董事等级无人符合时预算处理（KEEP 留存公司），【待业务确认】
     */
    private String emptyLevelPolicy;
    /**
     * 订单观察期（天，0 表示支付成功即生效）
     */
    private Integer observeDays;
    /**
     * 月度结算日（1-31，NULL 表示未配置，未配置不自动结算），【待业务确认】
     */
    private Integer settleDay;
    /**
     * 奖金发放日（1-31，NULL 表示未配置），【待业务确认】
     */
    private Integer payDay;
    /**
     * 奖金金额舍入模式（HALF_UP / FLOOR 等，精度到分），【待业务确认】
     */
    private String roundingMode;
    /**
     * 已发放奖金退款追偿策略（RECORD_ONLY 仅生成追偿记录），【待业务确认】
     */
    private String reversalPolicy;
    /**
     * 商品奖金池占会员商品有效 GMV 比例（百分比）
     */
    private Integer productPoolPercent;
    /**
     * 商品 GMV 是否包含运费（0 不含），【待业务确认】
     */
    private Integer gmvIncludeDelivery;
    /**
     * 规则版本号（每次修改自增）
     */
    private Integer version;
    /**
     * 备注
     */
    private String remark;

    /**
     * 按董事等级取礼包分红比例
     *
     * @param level 董事等级（1-5）
     * @return 分红比例（百分比）
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

    /**
     * 按董事等级取"培养一星董事"晋升数量
     *
     * @param level 董事等级（2-5）
     * @return 培养数量
     */
    public Integer getDirectorTrainees(Integer level) {
        return switch (level) {
            case 2 -> directorTwoStarTrainees;
            case 3 -> directorThreeStarTrainees;
            case 4 -> directorFourStarTrainees;
            case 5 -> directorFiveStarTrainees;
            default -> throw new IllegalArgumentException("director level invalid: " + level);
        };
    }

}

package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员礼包奖金明细 DO
 *
 * uk(dedup_key) 保证同一订单同一奖励类型不重复计奖；
 * 每条明细可追溯：订单 → 会员关系 → 计奖资格 → 基数 → 比例 → 金额 → 结算批次。
 *
 * @author sail
 */
@TableName("member_bonus_record")
@KeySequence("member_bonus_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBonusRecordDO extends TenantBaseDO {

    /**
     * 奖金编号
     */
    @TableId
    private Long id;
    /**
     * 防重复键（直推/增量=订单:类型；帮扶=月:上级:下级；董事=月:会员:等级）
     */
    private String dedupKey;
    /**
     * 获奖会员编号
     */
    private Long userId;
    /**
     * 奖励类型
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberBonusTypeEnum}
     */
    private Integer bonusType;
    /**
     * 来源会员编号（帮扶奖为第一代下级，董事分红为空）
     */
    private Long sourceUserId;
    /**
     * 来源订单编号（直推/增量奖，帮扶/董事为空）
     */
    private Long sourceOrderId;
    /**
     * 计奖基数 PV（元，董事分红为 0）
     */
    private Integer basePv;
    /**
     * 计奖基数金额（分，帮扶奖=下级当月直推+增量合计，董事=等级预算）
     */
    private Integer baseAmount;
    /**
     * 奖励比例（百分比）
     */
    private Integer rate;
    /**
     * 奖金金额（分）
     */
    private Integer amount;
    /**
     * 市场编号（增量奖归属市场）
     */
    private Long marketId;
    /**
     * 董事等级（礼包董事分红）
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberDirectorLevelEnum}
     */
    private Integer directorLevel;
    /**
     * 所属账期（yyyy-MM，即时奖金为空，月度奖金必填）
     */
    private String period;
    /**
     * 计算时规则版本
     */
    private Integer ruleVersion;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberBonusStatusEnum}
     */
    private Integer status;
    /**
     * 结算批次号
     */
    private String settleBatchNo;
    /**
     * 冲正关联的原奖金编号
     */
    private Long reversalOfId;
    /**
     * 冲正/追偿原因
     */
    private String reversalReason;

}

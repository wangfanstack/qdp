package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员礼包奖金结算批次 DO
 *
 * uk(period, scope)；结算时保存规则配置快照，已结算数据不覆盖。
 *
 * @author sail
 */
@TableName("member_bonus_settle_batch")
@KeySequence("member_bonus_settle_batch_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBonusSettleBatchDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 批次号（GB+period）
     */
    private String batchNo;
    /**
     * 账期（yyyy-MM）
     */
    private String period;
    /**
     * 奖金体系（GIFT 礼包奖金）
     */
    private String scope;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberBonusSettleStatusEnum}
     */
    private Integer status;
    /**
     * 本次礼包董事分红池（分，MANUAL 录入）
     */
    private Integer dividendPool;
    /**
     * 批次奖金总金额（分）
     */
    private Integer totalAmount;
    /**
     * 获奖人数
     */
    private Integer userCount;
    /**
     * 结算时规则配置快照（JSON）
     */
    private String configSnapshot;
    /**
     * 备注（例如跳过项说明）
     */
    private String remark;

}

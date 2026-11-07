package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员礼包奖金结算个人明细 DO
 *
 * uk(batch_no, user_id) 保证发放入账幂等。
 *
 * @author sail
 */
@TableName("member_bonus_settle_detail")
@KeySequence("member_bonus_settle_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBonusSettleDetailDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 会员编号
     */
    private Long userId;
    /**
     * 个人奖金合计（分）
     */
    private Integer totalAmount;
    /**
     * 发放状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberBonusPayStatusEnum}
     */
    private Integer payStatus;
    /**
     * 钱包入账业务单号（batchNo:userId）
     */
    private String walletBizId;
    /**
     * 发放时间
     */
    private LocalDateTime payTime;

}

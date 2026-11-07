package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员市场 DO
 *
 * 一个老板一个市场，uk(owner_user_id)；一个市场只有一个增量奖归属人。
 *
 * @author sail
 */
@TableName("member_market")
@KeySequence("member_market_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMarketDO extends TenantBaseDO {

    /**
     * 市场编号
     */
    @TableId
    private Long id;
    /**
     * 市场归属人（老板）会员编号（唯一）
     */
    private Long ownerUserId;
    /**
     * 市场生效时间（老板身份生效时间）
     */
    private LocalDateTime effectTime;
    /**
     * 状态（1 生效 2 停用，预留）
     */
    private Integer status;

}

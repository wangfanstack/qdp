package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员董事资格 DO
 *
 * 一个会员可同时满足多个等级，uk(user_id, level)；第一版不做自动降级。
 *
 * @author sail
 */
@TableName("member_director")
@KeySequence("member_director_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDirectorDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会员编号
     */
    private Long userId;
    /**
     * 董事等级
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.member.enums.bonus.MemberDirectorLevelEnum}
     */
    private Integer level;
    /**
     * 资格达成时间
     */
    private LocalDateTime qualifyTime;
    /**
     * 状态（1 有效 2 撤销，预留）
     */
    private Integer status;
    /**
     * 达成说明（核验依据快照）
     */
    private String source;

}

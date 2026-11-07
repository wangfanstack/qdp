package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员推荐关系 DO
 *
 * 一会员一条，uk(user_id)；推荐人名下有效直推顺序（valid_seq）的分配
 * 在事务内通过锁定推荐人行（SELECT FOR UPDATE）保证并发唯一。
 *
 * @author sail
 */
@TableName("member_relation")
@KeySequence("member_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRelationDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会员编号（唯一）
     */
    private Long userId;
    /**
     * 推荐人编号（根会员为空）
     */
    private Long referrerId;
    /**
     * 推荐关系建立时间
     */
    private LocalDateTime bindTime;
    /**
     * 首次有效购买时间（成为有效直推的时间）
     */
    private LocalDateTime validBuyTime;
    /**
     * 在推荐人名下的有效直推顺序（从 1 开始）
     */
    private Integer validSeq;
    /**
     * 老板身份生效时间（第 2 名有效直推达成时间）
     */
    private LocalDateTime bossTime;
    /**
     * 当前市场编号（取得老板身份后指向自己的市场）
     */
    private Long marketId;

}

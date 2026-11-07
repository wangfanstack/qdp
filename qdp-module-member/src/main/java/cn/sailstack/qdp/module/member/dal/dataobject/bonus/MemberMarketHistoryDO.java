package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员市场归属变更历史 DO
 *
 * 市场归属变更只追加不覆盖，带生效时间；历史业绩仍归原市场。
 *
 * @author sail
 */
@TableName("member_market_history")
@KeySequence("member_market_history_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMarketHistoryDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 发生归属变更的会员编号
     */
    private Long userId;
    /**
     * 原市场编号（首次归属为空）
     */
    private Long fromMarketId;
    /**
     * 新市场编号
     */
    private Long toMarketId;
    /**
     * 生效时间
     */
    private LocalDateTime effectTime;
    /**
     * 变更原因（例如：绑定推荐人/取得老板身份）
     */
    private String reason;

}

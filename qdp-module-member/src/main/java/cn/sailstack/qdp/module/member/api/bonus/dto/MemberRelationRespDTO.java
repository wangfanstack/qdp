package cn.sailstack.qdp.module.member.api.bonus.dto;

import lombok.Data;

/**
 * 会员推荐关系 Response DTO
 *
 * @author sail
 */
@Data
public class MemberRelationRespDTO {

    /**
     * 会员编号
     */
    private Long userId;
    /**
     * 推荐人编号
     */
    private Long referrerId;
    /**
     * 推荐关系建立时间
     */
    private java.time.LocalDateTime bindTime;
    /**
     * 首次有效购买时间
     */
    private java.time.LocalDateTime validBuyTime;
    /**
     * 有效直推顺序
     */
    private Integer validSeq;
    /**
     * 老板身份生效时间
     */
    private java.time.LocalDateTime bossTime;
    /**
     * 当前市场编号
     */
    private Long marketId;

}

package cn.sailstack.qdp.module.member.service.bonus.relation;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation.MemberRelationPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberMarketHistoryDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员推荐关系 Service 接口
 *
 * @author sail
 */
public interface MemberRelationService {

    /**
     * 绑定推荐关系（一次性；防自绑、防环；推荐人必须是平台会员）
     *
     * @param userId     会员编号
     * @param referrerId 推荐人编号
     */
    void bindRelation(Long userId, Long referrerId);

    /**
     * 获得会员推荐关系
     *
     * @param userId 会员编号
     * @return 推荐关系；未绑定返回 null
     */
    MemberRelationDO getRelation(Long userId);

    /**
     * 获得或创建推荐关系占位行（referrerId 为空；用于保证任意会员都有行可加锁）
     *
     * @param userId 会员编号
     * @return 推荐关系
     */
    MemberRelationDO getOrCreateRelation(Long userId);

    /**
     * 查询会员的推荐关系分页
     *
     * @param pageReqVO 分页查询
     * @return 推荐关系分页
     */
    PageResult<MemberRelationDO> getRelationPage(MemberRelationPageReqVO pageReqVO);

    /**
     * 获得推荐人的有效直推列表（按有效购买时间排序）
     *
     * @param referrerId 推荐人编号
     * @return 有效直推关系列表
     */
    List<MemberRelationDO> getValidDirectList(Long referrerId);

    /**
     * 按最近有效老板市场归属原则，查找指定时刻的市场归属人：
     * 从 fromUserId（含）沿推荐链向上找第一个 bossTime 早于 bizTime 的会员。
     *
     * 需求 5.3/5.4：会员取得老板身份（第 2 名有效直推支付时点）之前的业绩仍归原市场，
     * 因此使用严格早于（isBefore）排除"本次购买刚使其成为老板"的情况。
     *
     * @param fromUserId 起始会员编号（通常为被推荐人的推荐人）
     * @param bizTime    业务时间（订单支付时间）
     * @return 市场归属人关系；向上找不到老板返回 null（根链场景）
     */
    MemberRelationDO findMarketOwner(Long fromUserId, LocalDateTime bizTime);

    /**
     * 获得会员的市场归属变更历史
     *
     * @param userId 会员编号
     * @return 变更历史列表
     */
    List<MemberMarketHistoryDO> getMarketHistoryList(Long userId);

}

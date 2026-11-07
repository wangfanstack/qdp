package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation.MemberRelationPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员推荐关系 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberRelationMapper extends BaseMapperX<MemberRelationDO> {

    default MemberRelationDO selectByUserId(Long userId) {
        return selectOne(MemberRelationDO::getUserId, userId);
    }

    /**
     * 按会员编号锁定推荐关系行（FOR UPDATE），用于有效直推顺序的并发分配
     *
     * @param userId 会员编号
     * @return 推荐关系
     */
    default MemberRelationDO selectByUserIdForUpdate(Long userId) {
        return selectOneForUpdate(MemberRelationDO::getUserId, userId);
    }

    default List<MemberRelationDO> selectListByReferrerId(Long referrerId) {
        return selectList(new LambdaQueryWrapperX<MemberRelationDO>()
                .eq(MemberRelationDO::getReferrerId, referrerId)
                .isNotNull(MemberRelationDO::getValidBuyTime)
                .orderByAsc(MemberRelationDO::getValidBuyTime)
                .orderByAsc(MemberRelationDO::getId));
    }

    default Long selectCountByReferrerId(Long referrerId) {
        return selectCount(MemberRelationDO::getReferrerId, referrerId);
    }

    default PageResult<MemberRelationDO> selectPage(MemberRelationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberRelationDO>()
                .eqIfPresent(MemberRelationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MemberRelationDO::getReferrerId, reqVO.getReferrerId())
                .eqIfPresent(MemberRelationDO::getMarketId, reqVO.getMarketId())
                .orderByDesc(MemberRelationDO::getId));
    }

}

package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director.MemberDirectorPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberDirectorDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 会员董事资格 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberDirectorMapper extends BaseMapperX<MemberDirectorDO> {

    default List<MemberDirectorDO> selectListByUserId(Long userId) {
        return selectList(MemberDirectorDO::getUserId, userId);
    }

    default MemberDirectorDO selectByUserIdAndLevel(Long userId, Integer level) {
        return selectOne(new LambdaQueryWrapperX<MemberDirectorDO>()
                .eq(MemberDirectorDO::getUserId, userId)
                .eq(MemberDirectorDO::getLevel, level));
    }

    default List<MemberDirectorDO> selectListByLevel(Integer level) {
        return selectList(MemberDirectorDO::getLevel, level);
    }

    default List<MemberDirectorDO> selectListByUserIds(Collection<Long> userIds) {
        return selectList(new LambdaQueryWrapperX<MemberDirectorDO>()
                .in(MemberDirectorDO::getUserId, userIds));
    }

    default PageResult<MemberDirectorDO> selectPage(MemberDirectorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberDirectorDO>()
                .eqIfPresent(MemberDirectorDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MemberDirectorDO::getLevel, reqVO.getLevel())
                .orderByDesc(MemberDirectorDO::getQualifyTime));
    }

}

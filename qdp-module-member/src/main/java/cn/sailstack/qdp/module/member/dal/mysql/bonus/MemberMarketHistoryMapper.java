package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberMarketHistoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员市场归属变更历史 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberMarketHistoryMapper extends BaseMapperX<MemberMarketHistoryDO> {

    default List<MemberMarketHistoryDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberMarketHistoryDO>()
                .eq(MemberMarketHistoryDO::getUserId, userId)
                .orderByAsc(MemberMarketHistoryDO::getEffectTime));
    }

}

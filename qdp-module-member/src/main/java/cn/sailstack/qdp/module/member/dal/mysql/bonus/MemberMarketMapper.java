package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberMarketDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员市场 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberMarketMapper extends BaseMapperX<MemberMarketDO> {

    default MemberMarketDO selectByOwnerUserId(Long ownerUserId) {
        return selectOne(MemberMarketDO::getOwnerUserId, ownerUserId);
    }

}

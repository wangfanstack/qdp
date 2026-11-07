package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 奖金规则配置 Mapper（单行，id 固定 1）
 *
 * @author sail
 */
@Mapper
public interface MemberBonusConfigMapper extends BaseMapperX<MemberBonusConfigDO> {

    default MemberBonusConfigDO selectConfig() {
        return selectById(MemberBonusConfigDO.ID);
    }

}

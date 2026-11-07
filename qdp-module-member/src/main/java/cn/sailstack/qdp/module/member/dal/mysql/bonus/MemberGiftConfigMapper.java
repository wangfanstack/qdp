package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.common.enums.CommonStatusEnum;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift.MemberGiftConfigPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员礼包配置 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberGiftConfigMapper extends BaseMapperX<MemberGiftConfigDO> {

    default PageResult<MemberGiftConfigDO> selectPage(MemberGiftConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberGiftConfigDO>()
                .likeIfPresent(MemberGiftConfigDO::getName, reqVO.getName())
                .eqIfPresent(MemberGiftConfigDO::getStatus, reqVO.getStatus())
                .orderByAsc(MemberGiftConfigDO::getSort));
    }

    default MemberGiftConfigDO selectBySpuId(Long spuId) {
        return selectOne(MemberGiftConfigDO::getSpuId, spuId);
    }

    default List<MemberGiftConfigDO> selectListByStatusEnable() {
        return selectList(new LambdaQueryWrapperX<MemberGiftConfigDO>()
                .eq(MemberGiftConfigDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByAsc(MemberGiftConfigDO::getSort));
    }

    default MemberGiftConfigDO selectByName(String name) {
        return selectOne(MemberGiftConfigDO::getName, name);
    }

}

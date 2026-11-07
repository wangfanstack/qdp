package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.benefit.MemberGiftBenefitPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftBenefitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员礼包权益记录 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberGiftBenefitMapper extends BaseMapperX<MemberGiftBenefitDO> {

    default MemberGiftBenefitDO selectByOrderId(Long orderId) {
        return selectOne(MemberGiftBenefitDO::getOrderId, orderId);
    }

    default List<MemberGiftBenefitDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberGiftBenefitDO>()
                .eq(MemberGiftBenefitDO::getUserId, userId)
                .orderByAsc(MemberGiftBenefitDO::getId));
    }

    default PageResult<MemberGiftBenefitDO> selectPage(MemberGiftBenefitPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberGiftBenefitDO>()
                .eqIfPresent(MemberGiftBenefitDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MemberGiftBenefitDO::getGiftId, reqVO.getGiftId())
                .eqIfPresent(MemberGiftBenefitDO::getType, reqVO.getType())
                .eqIfPresent(MemberGiftBenefitDO::getStatus, reqVO.getStatus())
                .orderByDesc(MemberGiftBenefitDO::getId));
    }

}

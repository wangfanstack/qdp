package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.settle.MemberBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusSettleBatchDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员礼包奖金结算批次 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberBonusSettleBatchMapper extends BaseMapperX<MemberBonusSettleBatchDO> {

    default MemberBonusSettleBatchDO selectByPeriodAndScope(String period, String scope) {
        return selectOne(new LambdaQueryWrapperX<MemberBonusSettleBatchDO>()
                .eq(MemberBonusSettleBatchDO::getPeriod, period)
                .eq(MemberBonusSettleBatchDO::getScope, scope));
    }

    default MemberBonusSettleBatchDO selectByBatchNo(String batchNo) {
        return selectOne(MemberBonusSettleBatchDO::getBatchNo, batchNo);
    }

    default PageResult<MemberBonusSettleBatchDO> selectPage(MemberBonusSettleBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberBonusSettleBatchDO>()
                .eqIfPresent(MemberBonusSettleBatchDO::getPeriod, reqVO.getPeriod())
                .eqIfPresent(MemberBonusSettleBatchDO::getStatus, reqVO.getStatus())
                .orderByDesc(MemberBonusSettleBatchDO::getId));
    }

}

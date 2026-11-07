package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record.MemberBonusRecordPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 会员礼包奖金明细 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberBonusRecordMapper extends BaseMapperX<MemberBonusRecordDO> {

    default MemberBonusRecordDO selectByDedupKey(String dedupKey) {
        return selectOne(MemberBonusRecordDO::getDedupKey, dedupKey);
    }

    default List<MemberBonusRecordDO> selectListBySourceOrderId(Long sourceOrderId) {
        return selectList(MemberBonusRecordDO::getSourceOrderId, sourceOrderId);
    }

    default List<MemberBonusRecordDO> selectListByPeriodAndTypes(String period, Collection<Integer> bonusTypes) {
        return selectList(new LambdaQueryWrapperX<MemberBonusRecordDO>()
                .eq(MemberBonusRecordDO::getPeriod, period)
                .in(MemberBonusRecordDO::getBonusType, bonusTypes)
                .orderByAsc(MemberBonusRecordDO::getId));
    }

    default List<MemberBonusRecordDO> selectListByStatusAndBeforeTime(Integer status,
                                                                      Collection<Integer> bonusTypes,
                                                                      String periodPrefix) {
        // 查询某账期（含）之前、指定类型、指定状态的明细，用于月结汇总
        return selectList(new LambdaQueryWrapperX<MemberBonusRecordDO>()
                .le(MemberBonusRecordDO::getPeriod, periodPrefix)
                .eq(MemberBonusRecordDO::getStatus, status)
                .in(MemberBonusRecordDO::getBonusType, bonusTypes)
                .orderByAsc(MemberBonusRecordDO::getId));
    }

    default List<MemberBonusRecordDO> selectListByBatchNo(String batchNo) {
        return selectList(MemberBonusRecordDO::getSettleBatchNo, batchNo);
    }

    default PageResult<MemberBonusRecordDO> selectPage(MemberBonusRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberBonusRecordDO>()
                .eqIfPresent(MemberBonusRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MemberBonusRecordDO::getBonusType, reqVO.getBonusType())
                .eqIfPresent(MemberBonusRecordDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MemberBonusRecordDO::getPeriod, reqVO.getPeriod())
                .eqIfPresent(MemberBonusRecordDO::getSourceUserId, reqVO.getSourceUserId())
                .likeIfPresent(MemberBonusRecordDO::getSettleBatchNo, reqVO.getSettleBatchNo())
                .orderByDesc(MemberBonusRecordDO::getId));
    }

}

package cn.sailstack.qdp.module.member.dal.mysql.bonus;

import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusSettleDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员礼包奖金结算个人明细 Mapper
 *
 * @author sail
 */
@Mapper
public interface MemberBonusSettleDetailMapper extends BaseMapperX<MemberBonusSettleDetailDO> {

    default MemberBonusSettleDetailDO selectByBatchNoAndUserId(String batchNo, Long userId) {
        return selectOne(new LambdaQueryWrapperX<MemberBonusSettleDetailDO>()
                .eq(MemberBonusSettleDetailDO::getBatchNo, batchNo)
                .eq(MemberBonusSettleDetailDO::getUserId, userId));
    }

    default List<MemberBonusSettleDetailDO> selectListByBatchNo(String batchNo) {
        return selectList(MemberBonusSettleDetailDO::getBatchNo, batchNo);
    }

}

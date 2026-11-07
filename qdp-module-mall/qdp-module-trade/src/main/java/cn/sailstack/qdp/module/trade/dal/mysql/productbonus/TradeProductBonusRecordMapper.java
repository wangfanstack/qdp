package cn.sailstack.qdp.module.trade.dal.mysql.productbonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusRecordPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品奖金明细 Mapper
 *
 * @author sail
 */
@Mapper
public interface TradeProductBonusRecordMapper extends BaseMapperX<TradeProductBonusRecordDO> {

    default TradeProductBonusRecordDO selectByDedupKey(String dedupKey) {
        return selectOne(TradeProductBonusRecordDO::getDedupKey, dedupKey);
    }

    default TradeProductBonusRecordDO selectByOrderItemId(Long orderItemId) {
        return selectOne(TradeProductBonusRecordDO::getOrderItemId, orderItemId);
    }

    /**
     * 查询订单项的原始差额奖记录（排除冲正记录，冲正记录 reversal_of_id 非空）
     */
    default TradeProductBonusRecordDO selectOriginalDifferenceByOrderItemId(Long orderItemId) {
        return selectOne(new LambdaQueryWrapperX<TradeProductBonusRecordDO>()
                .eq(TradeProductBonusRecordDO::getOrderItemId, orderItemId)
                .eq(TradeProductBonusRecordDO::getBonusType,
                        cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusTypeEnum.DIFFERENCE.getType())
                .isNull(TradeProductBonusRecordDO::getReversalOfId));
    }

    default List<TradeProductBonusRecordDO> selectListByPeriod(String period) {
        return selectList(new LambdaQueryWrapperX<TradeProductBonusRecordDO>()
                .eq(TradeProductBonusRecordDO::getPeriod, period)
                .orderByAsc(TradeProductBonusRecordDO::getId));
    }

    default List<TradeProductBonusRecordDO> selectListByBatchNo(String batchNo) {
        return selectList(TradeProductBonusRecordDO::getSettleBatchNo, batchNo);
    }

    default PageResult<TradeProductBonusRecordDO> selectPage(TradeProductBonusRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TradeProductBonusRecordDO>()
                .eqIfPresent(TradeProductBonusRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TradeProductBonusRecordDO::getBonusType, reqVO.getBonusType())
                .eqIfPresent(TradeProductBonusRecordDO::getStatus, reqVO.getStatus())
                .eqIfPresent(TradeProductBonusRecordDO::getPeriod, reqVO.getPeriod())
                .eqIfPresent(TradeProductBonusRecordDO::getBuyerUserId, reqVO.getBuyerUserId())
                .orderByDesc(TradeProductBonusRecordDO::getId));
    }

}

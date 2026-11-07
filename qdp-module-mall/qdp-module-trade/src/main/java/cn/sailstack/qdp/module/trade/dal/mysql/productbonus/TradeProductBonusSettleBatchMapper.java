package cn.sailstack.qdp.module.trade.dal.mysql.productbonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductBonusSettleBatchPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusSettleBatchDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品奖金结算批次 Mapper
 *
 * @author sail
 */
@Mapper
public interface TradeProductBonusSettleBatchMapper extends BaseMapperX<TradeProductBonusSettleBatchDO> {

    default TradeProductBonusSettleBatchDO selectByPeriodAndScope(String period, String scope) {
        return selectOne(new LambdaQueryWrapperX<TradeProductBonusSettleBatchDO>()
                .eq(TradeProductBonusSettleBatchDO::getPeriod, period)
                .eq(TradeProductBonusSettleBatchDO::getScope, scope));
    }

    default TradeProductBonusSettleBatchDO selectByBatchNo(String batchNo) {
        return selectOne(TradeProductBonusSettleBatchDO::getBatchNo, batchNo);
    }

    default PageResult<TradeProductBonusSettleBatchDO> selectPage(TradeProductBonusSettleBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TradeProductBonusSettleBatchDO>()
                .eqIfPresent(TradeProductBonusSettleBatchDO::getPeriod, reqVO.getPeriod())
                .eqIfPresent(TradeProductBonusSettleBatchDO::getStatus, reqVO.getStatus())
                .orderByDesc(TradeProductBonusSettleBatchDO::getId));
    }

}

package cn.sailstack.qdp.module.trade.dal.mysql.productbonus;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.trade.controller.admin.productbonus.vo.TradeProductGmvPageReqVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductGmvRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员商品 GMV 记录 Mapper
 *
 * @author sail
 */
@Mapper
public interface TradeProductGmvRecordMapper extends BaseMapperX<TradeProductGmvRecordDO> {

    default TradeProductGmvRecordDO selectByOrderId(Long orderId) {
        return selectOne(TradeProductGmvRecordDO::getOrderId, orderId);
    }

    default List<TradeProductGmvRecordDO> selectListByPeriod(String period) {
        return selectList(new LambdaQueryWrapperX<TradeProductGmvRecordDO>()
                .eq(TradeProductGmvRecordDO::getPeriod, period));
    }

    default PageResult<TradeProductGmvRecordDO> selectPage(TradeProductGmvPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TradeProductGmvRecordDO>()
                .eqIfPresent(TradeProductGmvRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TradeProductGmvRecordDO::getPeriod, reqVO.getPeriod())
                .orderByDesc(TradeProductGmvRecordDO::getId));
    }

}

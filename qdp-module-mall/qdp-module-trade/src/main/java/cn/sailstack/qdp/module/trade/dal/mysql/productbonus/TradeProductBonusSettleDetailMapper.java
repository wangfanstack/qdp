package cn.sailstack.qdp.module.trade.dal.mysql.productbonus;

import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.module.trade.dal.dataobject.productbonus.TradeProductBonusSettleDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品奖金结算个人明细 Mapper
 *
 * @author sail
 */
@Mapper
public interface TradeProductBonusSettleDetailMapper extends BaseMapperX<TradeProductBonusSettleDetailDO> {

    default List<TradeProductBonusSettleDetailDO> selectListByBatchNo(String batchNo) {
        return selectList(TradeProductBonusSettleDetailDO::getBatchNo, batchNo);
    }

}

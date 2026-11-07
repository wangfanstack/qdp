package cn.sailstack.qdp.module.trade.convert.brokerage;

import cn.sailstack.qdp.module.trade.controller.app.brokerage.vo.withdraw.AppBrokerageWithdrawRespVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.brokerage.BrokerageWithdrawDO;
import cn.sailstack.qdp.module.trade.service.brokerage.bo.BrokerageWithdrawSummaryRespBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 佣金提现概括 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface BrokerageWithdrawSummaryRespConvert {

    BrokerageWithdrawSummaryRespConvert INSTANCE = Mappers.getMapper(BrokerageWithdrawSummaryRespConvert.class);

    BrokerageWithdrawSummaryRespBO convert(Long withdrawSum, Long brokerageCount, Integer withdrawTotalPrice);

}

package cn.sailstack.qdp.module.trade.convert.config;

import cn.sailstack.qdp.module.trade.controller.admin.config.vo.TradeConfigRespVO;
import cn.sailstack.qdp.module.trade.controller.admin.config.vo.TradeConfigSaveReqVO;
import cn.sailstack.qdp.module.trade.controller.app.config.vo.AppTradeConfigRespVO;
import cn.sailstack.qdp.module.trade.dal.dataobject.config.TradeConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 交易中心配置 Convert
 *
 * @author owen
 */
@Mapper
public interface TradeConfigConvert {

    TradeConfigConvert INSTANCE = Mappers.getMapper(TradeConfigConvert.class);

    TradeConfigDO convert(TradeConfigSaveReqVO bean);

    TradeConfigRespVO convert(TradeConfigDO bean);

    AppTradeConfigRespVO convert02(TradeConfigDO tradeConfig);
}

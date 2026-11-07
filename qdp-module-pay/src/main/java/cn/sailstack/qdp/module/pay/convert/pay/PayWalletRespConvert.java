package cn.sailstack.qdp.module.pay.convert.pay;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletDO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayWalletRespConvert {

    PayWalletRespConvert INSTANCE = Mappers.getMapper(PayWalletRespConvert.class);

    PayWalletRespDTO convert(cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletDO source);
}

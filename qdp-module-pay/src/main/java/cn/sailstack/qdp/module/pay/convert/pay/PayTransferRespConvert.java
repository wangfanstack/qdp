package cn.sailstack.qdp.module.pay.convert.pay;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.api.transfer.dto.PayTransferRespDTO;
import cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayTransferRespConvert {

    PayTransferRespConvert INSTANCE = Mappers.getMapper(PayTransferRespConvert.class);

    PayTransferRespDTO convert(cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO source);
}

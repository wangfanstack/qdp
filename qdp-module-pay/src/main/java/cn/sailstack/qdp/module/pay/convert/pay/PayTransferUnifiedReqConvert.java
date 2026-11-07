package cn.sailstack.qdp.module.pay.convert.pay;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.dto.transfer.PayTransferUnifiedReqDTO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayTransferUnifiedReqConvert {

    PayTransferUnifiedReqConvert INSTANCE = Mappers.getMapper(PayTransferUnifiedReqConvert.class);

    PayTransferUnifiedReqDTO convert(cn.sailstack.qdp.module.pay.api.transfer.dto.PayTransferCreateReqDTO source);
}

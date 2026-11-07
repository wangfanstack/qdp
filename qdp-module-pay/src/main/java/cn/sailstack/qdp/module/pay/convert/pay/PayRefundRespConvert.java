package cn.sailstack.qdp.module.pay.convert.pay;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.api.refund.dto.PayRefundRespDTO;
import cn.sailstack.qdp.module.pay.dal.dataobject.refund.PayRefundDO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayRefundRespConvert {

    PayRefundRespConvert INSTANCE = Mappers.getMapper(PayRefundRespConvert.class);

    PayRefundRespDTO convert(cn.sailstack.qdp.module.pay.dal.dataobject.refund.PayRefundDO source);
}

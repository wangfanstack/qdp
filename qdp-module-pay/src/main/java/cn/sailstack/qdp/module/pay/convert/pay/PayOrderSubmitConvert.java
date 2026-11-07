package cn.sailstack.qdp.module.pay.convert.pay;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import cn.sailstack.qdp.module.pay.controller.app.order.vo.AppPayOrderSubmitRespVO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayOrderSubmitConvert {

    PayOrderSubmitConvert INSTANCE = Mappers.getMapper(PayOrderSubmitConvert.class);

    AppPayOrderSubmitRespVO convert(cn.sailstack.qdp.module.pay.controller.admin.order.vo.PayOrderSubmitRespVO source);
}

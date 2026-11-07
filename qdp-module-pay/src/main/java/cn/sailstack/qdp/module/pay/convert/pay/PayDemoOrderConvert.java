package cn.sailstack.qdp.module.pay.convert.pay;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.controller.admin.demo.vo.order.PayDemoOrderRespVO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayDemoOrderConvert {

    PayDemoOrderConvert INSTANCE = Mappers.getMapper(PayDemoOrderConvert.class);

    cn.sailstack.qdp.framework.common.pojo.PageResult<PayDemoOrderRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.demo.PayDemoOrderDO> page);

    default cn.sailstack.qdp.framework.common.pojo.PageResult<PayDemoOrderRespVO> convertPageWithPeek(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.demo.PayDemoOrderDO> page, java.util.function.Consumer<PayDemoOrderRespVO> peek) {
        cn.sailstack.qdp.framework.common.pojo.PageResult<PayDemoOrderRespVO> result = convertPage(page);
        if (result != null) {
            result.getList().forEach(peek);
        }
        return result;
    }
}

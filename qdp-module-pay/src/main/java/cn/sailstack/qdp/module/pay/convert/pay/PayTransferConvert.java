package cn.sailstack.qdp.module.pay.convert.pay;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import cn.sailstack.qdp.module.pay.controller.admin.transfer.vo.PayTransferRespVO;
import cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO;
import java.util.function.Consumer;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayTransferConvert {

    PayTransferConvert INSTANCE = Mappers.getMapper(PayTransferConvert.class);

    PayTransferRespVO convert(cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO source);
    cn.sailstack.qdp.framework.common.pojo.PageResult<PayTransferRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO> page);
    PayTransferDO convert(cn.sailstack.qdp.module.pay.api.transfer.dto.PayTransferCreateReqDTO source);
    default PayTransferRespVO convert(cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO source, java.util.function.Consumer<PayTransferRespVO> peek) {
        PayTransferRespVO target = convert(source);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }
    default cn.sailstack.qdp.framework.common.pojo.PageResult<PayTransferRespVO> convertPageWithPeek(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.transfer.PayTransferDO> page, java.util.function.Consumer<PayTransferRespVO> peek) {
        cn.sailstack.qdp.framework.common.pojo.PageResult<PayTransferRespVO> result = convertPage(page);
        if (result != null) {
            result.getList().forEach(peek);
        }
        return result;
    }
}

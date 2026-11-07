package cn.sailstack.qdp.module.pay.convert.pay;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.controller.admin.notify.vo.PayNotifyTaskRespVO;
import java.util.function.Consumer;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayNotifyTaskConvert {

    PayNotifyTaskConvert INSTANCE = Mappers.getMapper(PayNotifyTaskConvert.class);

    cn.sailstack.qdp.framework.common.pojo.PageResult<PayNotifyTaskRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.notify.PayNotifyTaskDO> page);
    default cn.sailstack.qdp.framework.common.pojo.PageResult<PayNotifyTaskRespVO> convertPageWithPeek(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.notify.PayNotifyTaskDO> page, java.util.function.Consumer<PayNotifyTaskRespVO> peek) {
        cn.sailstack.qdp.framework.common.pojo.PageResult<PayNotifyTaskRespVO> result = convertPage(page);
        if (result != null) {
            result.getList().forEach(peek);
        }
        return result;
    }
}

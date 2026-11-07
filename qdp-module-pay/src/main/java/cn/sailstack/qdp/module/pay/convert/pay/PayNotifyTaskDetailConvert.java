package cn.sailstack.qdp.module.pay.convert.pay;

import cn.sailstack.qdp.module.pay.controller.admin.notify.vo.PayNotifyTaskDetailRespVO;
import cn.sailstack.qdp.module.pay.dal.dataobject.notify.PayNotifyLogDO;
import cn.sailstack.qdp.module.pay.dal.dataobject.notify.PayNotifyTaskDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.function.Consumer;

/**
 * 支付通知任务明细 Convert（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayNotifyTaskDetailConvert {

    PayNotifyTaskDetailConvert INSTANCE = Mappers.getMapper(PayNotifyTaskDetailConvert.class);

    PayNotifyTaskDetailRespVO convert(PayNotifyTaskDO source);

    default PayNotifyTaskDetailRespVO convert(PayNotifyTaskDO source, Consumer<PayNotifyTaskDetailRespVO> peek) {
        PayNotifyTaskDetailRespVO target = convert(source);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    List<PayNotifyTaskDetailRespVO.Log> convertLogList(List<PayNotifyLogDO> list);

}

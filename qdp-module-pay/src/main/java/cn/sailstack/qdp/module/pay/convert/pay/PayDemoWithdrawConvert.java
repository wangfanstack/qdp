package cn.sailstack.qdp.module.pay.convert.pay;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.controller.admin.demo.vo.withdraw.PayDemoWithdrawCreateReqVO;
import cn.sailstack.qdp.module.pay.controller.admin.demo.vo.withdraw.PayDemoWithdrawRespVO;
import cn.sailstack.qdp.module.pay.dal.dataobject.demo.PayDemoWithdrawDO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayDemoWithdrawConvert {

    PayDemoWithdrawConvert INSTANCE = Mappers.getMapper(PayDemoWithdrawConvert.class);

    cn.sailstack.qdp.framework.common.pojo.PageResult<PayDemoWithdrawRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.demo.PayDemoWithdrawDO> page);
    PayDemoWithdrawDO convert(cn.sailstack.qdp.module.pay.controller.admin.demo.vo.withdraw.PayDemoWithdrawCreateReqVO source);

    default cn.sailstack.qdp.framework.common.pojo.PageResult<PayDemoWithdrawRespVO> convertPageWithPeek(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.demo.PayDemoWithdrawDO> page, java.util.function.Consumer<PayDemoWithdrawRespVO> peek) {
        cn.sailstack.qdp.framework.common.pojo.PageResult<PayDemoWithdrawRespVO> result = convertPage(page);
        if (result != null) {
            result.getList().forEach(peek);
        }
        return result;
    }
}

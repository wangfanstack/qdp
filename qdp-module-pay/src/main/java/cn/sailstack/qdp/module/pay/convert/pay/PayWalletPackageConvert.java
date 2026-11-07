package cn.sailstack.qdp.module.pay.convert.pay;

import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletRechargePackageDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.pay.controller.app.wallet.vo.recharge.AppPayWalletPackageRespVO;
import java.util.List;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface PayWalletPackageConvert {

    PayWalletPackageConvert INSTANCE = Mappers.getMapper(PayWalletPackageConvert.class);

    java.util.List<AppPayWalletPackageRespVO> convertList(java.util.List<cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletRechargePackageDO> list);
}

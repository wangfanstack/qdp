package cn.sailstack.qdp.module.pay.convert.wallet;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.pay.controller.admin.wallet.vo.transaction.PayWalletTransactionRespVO;
import cn.sailstack.qdp.module.pay.controller.app.wallet.vo.transaction.AppPayWalletTransactionRespVO;
import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import cn.sailstack.qdp.module.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletTransactionConvert {

    PayWalletTransactionConvert INSTANCE = Mappers.getMapper(PayWalletTransactionConvert.class);

    PageResult<PayWalletTransactionRespVO> convertPage2(PageResult<PayWalletTransactionDO> page);

    PayWalletTransactionDO convert(WalletTransactionCreateReqBO bean);


    AppPayWalletTransactionRespVO convert(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletTransactionDO> source);

    cn.sailstack.qdp.framework.common.pojo.PageResult<AppPayWalletTransactionRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletTransactionDO> page);
}

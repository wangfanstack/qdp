package cn.sailstack.qdp.module.pay.api.wallet;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.pay.convert.pay.PayWalletRespConvert;
import com.google.common.base.Preconditions;

import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import cn.sailstack.qdp.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletDO;
import cn.sailstack.qdp.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import cn.sailstack.qdp.module.pay.enums.wallet.PayWalletBizTypeEnum;
import cn.sailstack.qdp.module.pay.service.wallet.PayWalletService;
import cn.sailstack.qdp.module.pay.service.wallet.PayWalletTransactionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 钱包 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class PayWalletApiImpl implements PayWalletApi {

    @Resource
    private PayWalletService payWalletService;
    @Resource
    private PayWalletTransactionService payWalletTransactionService;

    @Override
    public void addWalletBalance(PayWalletAddBalanceReqDTO reqDTO) {
        PayWalletBizTypeEnum bizType = PayWalletBizTypeEnum.valueOf(reqDTO.getBizType());
        // 幂等：同 bizId + bizType 的流水已存在时直接返回，防止业务方重试导致重复入账
        PayWalletTransactionDO existTransaction =
                payWalletTransactionService.getWalletTransaction(reqDTO.getBizId(), bizType);
        if (existTransaction != null) {
            return;
        }
        // 创建或获取钱包
        PayWalletDO wallet = payWalletService.getOrCreateWallet(reqDTO.getUserId(), reqDTO.getUserType());
        Preconditions.checkNotNull(wallet, "钱包({}/{})不存在", reqDTO.getUserId(), reqDTO.getUserType());

        // 增加余额
        payWalletService.addWalletBalance(wallet.getId(), reqDTO.getBizId(), bizType, reqDTO.getPrice());
    }

    @Override
    public PayWalletRespDTO getOrCreateWallet(Long userId, Integer userType) {
        PayWalletDO wallet = payWalletService.getOrCreateWallet(userId, userType);
        return cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(wallet, PayWalletRespDTO.class);
    }

}

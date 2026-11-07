package cn.sailstack.qdp.module.trade.framework.delivery.core.client.impl;

import com.google.common.base.Preconditions;
import cn.sailstack.qdp.module.trade.framework.delivery.config.TradeExpressProperties;
import cn.sailstack.qdp.module.trade.framework.delivery.core.client.ExpressClient;
import cn.sailstack.qdp.module.trade.framework.delivery.core.client.impl.kd100.Kd100ExpressClient;
import cn.sailstack.qdp.module.trade.framework.delivery.core.client.impl.kdniao.KdNiaoExpressClient;
import cn.sailstack.qdp.module.trade.framework.delivery.core.enums.ExpressClientEnum;
import cn.sailstack.qdp.module.trade.framework.delivery.core.client.ExpressClientFactory;
import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 快递客户端工厂实现类
 *
 * @author jason
 */
@AllArgsConstructor
public class ExpressClientFactoryImpl implements ExpressClientFactory {

    private final Map<ExpressClientEnum, ExpressClient> clientMap = new ConcurrentHashMap<>(8);

    private final TradeExpressProperties tradeExpressProperties;
    private final RestTemplate restTemplate;

    @Override
    public ExpressClient getDefaultExpressClient() {
        ExpressClient defaultClient = getOrCreateExpressClient(tradeExpressProperties.getClient());
        Preconditions.checkNotNull("默认的快递客户端不能为空");
        return defaultClient;
    }

    @Override
    public ExpressClient getOrCreateExpressClient(ExpressClientEnum clientEnum) {
        return clientMap.computeIfAbsent(clientEnum,
                client -> createExpressClient(client, tradeExpressProperties));
    }

    private ExpressClient createExpressClient(ExpressClientEnum queryProviderEnum,
                                                TradeExpressProperties tradeExpressProperties) {
        switch (queryProviderEnum) {
            case NOT_PROVIDE:
                return new NoProvideExpressClient();
            case KD_NIAO:
                return new KdNiaoExpressClient(restTemplate, tradeExpressProperties.getKdNiao());
            case KD_100:
                return new Kd100ExpressClient(restTemplate, tradeExpressProperties.getKd100());
        }
        return null;
    }
}

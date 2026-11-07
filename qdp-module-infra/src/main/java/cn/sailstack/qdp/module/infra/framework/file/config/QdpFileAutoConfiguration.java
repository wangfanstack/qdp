package cn.sailstack.qdp.module.infra.framework.file.config;

import cn.sailstack.qdp.module.infra.framework.file.core.client.FileClientFactory;
import cn.sailstack.qdp.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author sailstack源码
 */
@Configuration(proxyBeanMethods = false)
public class QdpFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}

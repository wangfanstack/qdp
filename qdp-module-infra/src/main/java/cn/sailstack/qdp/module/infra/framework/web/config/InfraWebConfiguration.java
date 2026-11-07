package cn.sailstack.qdp.module.infra.framework.web.config;

import cn.sailstack.qdp.framework.swagger.config.QdpSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * infra 模块的 web 组件的 Configuration
 *
 * @author sailstack源码
 */
@Configuration(proxyBeanMethods = false)
public class InfraWebConfiguration {

    /**
     * infra 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi infraGroupedOpenApi() {
        return QdpSwaggerAutoConfiguration.buildGroupedOpenApi("infra");
    }

}

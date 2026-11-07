package cn.sailstack.qdp.module.product.framework.web.config;

import cn.sailstack.qdp.framework.swagger.config.QdpSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * product 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class ProductWebConfiguration {

    /**
     * product 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi productGroupedOpenApi() {
        return QdpSwaggerAutoConfiguration.buildGroupedOpenApi("product");
    }

}

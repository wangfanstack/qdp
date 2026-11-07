package cn.sailstack.qdp.framework.common.config;

import cn.sailstack.qdp.framework.common.util.spring.SpringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * common 模块的自动配置类
 *
 * @author sailstack源码
 */
@AutoConfiguration
public class QdpCommonAutoConfiguration {

    /**
     * 注册 SpringUtils，使其以静态方式持有容器上下文
     */
    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

}

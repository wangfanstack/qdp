package cn.sailstack.qdp.framework.common.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Spring 工具类：以静态方式持有容器上下文
 *
 * 实现 {@link BeanFactoryPostProcessor}：保证自身不受全局懒加载（spring.main.lazy-initialization）影响，
 * 在容器刷新早期完成实例化，使 {@link #setApplicationContext(ApplicationContext)} 的静态赋值必定生效
 * （上游 Hutool SpringUtil 亦采用此机制）
 *
 * @author 芋道源码
 */
@Component
public class SpringUtils implements ApplicationContextAware, BeanFactoryPostProcessor {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.context = applicationContext;
    }

    /**
     * 空实现：仅利用 BeanFactoryPostProcessor 的强制早期实例化语义，参见类注释
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 无需处理
    }

    /**
     * 获得容器上下文
     *
     * @return 容器上下文
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * {@link #getContext()} 的别名，兼容既有调用习惯
     *
     * @return 容器上下文
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 获得可配置的 Bean 工厂
     *
     * @return Bean 工厂
     */
    public static ConfigurableBeanFactory getConfigurableBeanFactory() {
        return ((ConfigurableApplicationContext) context).getBeanFactory();
    }

    /**
     * 获得环境配置属性值
     *
     * @param key 属性名
     * @return 属性值；不存在时返回 null
     */
    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    /**
     * 获得环境配置属性值
     *
     * @param key          属性名
     * @param targetType   目标类型
     * @param defaultValue 默认值
     * @param <T>          目标类型
     * @return 属性值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return getEnvironment().getProperty(key, targetType, defaultValue);
    }

    /**
     * 获得当前激活的 profile
     *
     * @return 激活的 profile；未激活时返回 null
     */
    public static String getActiveProfile() {
        Environment environment = getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : null;
    }

    /**
     * 是否为生产环境
     *
     * @return 是否生产环境
     */
    public static boolean isProd() {
        String activeProfile = getActiveProfile();
        return Objects.equals("prod", activeProfile);
    }

    private static Environment getEnvironment() {
        return context != null ? context.getEnvironment()
                : new org.springframework.core.env.StandardEnvironment();
    }

}

package me.ziyframework.web.log.requestid;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * 日志相关自动配置类.<br/>
 * created on 2025-01
 *
 * @author ziy
 */
@AutoConfiguration
@EnableConfigurationProperties(TranceIdProperties.class)
public class TranceIdAutoConfiguration {

    /**
     * requestId Resolver.
     */
    @Bean
    @ConditionalOnMissingBean
    public TranceIdResolver requestIdResolver() {
        return TranceIdResolver.UUID_V4_RESOLVER;
    }

    /**
     * requestIdFilter.
     */
    @Bean
    public FilterRegistrationBean<TranceIdServletFilter> requestIdFilter(
            TranceIdProperties tranceIdProperties, TranceIdResolver tranceIdResolver) {
        FilterRegistrationBean<TranceIdServletFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(new TranceIdServletFilter(tranceIdResolver, tranceIdProperties));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}

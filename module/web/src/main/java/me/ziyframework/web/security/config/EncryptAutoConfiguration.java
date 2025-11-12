package me.ziyframework.web.security.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.Filter;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import me.ziyframework.web.security.cipher.HttpCipherFactory;
import me.ziyframework.web.security.cipher.Sm4CipherFactory;
import me.ziyframework.web.security.exchange.SecretExchange;
import me.ziyframework.web.security.exchange.Sm2SecretExchange;
import me.ziyframework.web.security.filter.HttpCipherServletFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 加解密配置类.<br/>
 * created on 2025-03
 *
 * @author ziy
 */
@AutoConfiguration
@RequiredArgsConstructor
public class EncryptAutoConfiguration {

    /**
     * 默认使用Sm2作为密钥协商对象.
     */
    @ConditionalOnMissingBean
    @Bean
    public SecretExchange secretExchange() {
        // 创建一个用于缓存相同公钥的共享密钥的缓存对象
        Cache<String, byte[]> cache = Caffeine.newBuilder()
                .maximumSize(1 << 20)
                .expireAfterAccess(Duration.ofSeconds(30))
                .build();
        return new Sm2SecretExchange(cache, Duration.ofHours(5).toSeconds());
    }

    /**
     * 对称解密的Cipher工厂.
     */
    @ConditionalOnMissingBean(HttpCipherFactory.class)
    @Bean
    public HttpCipherFactory httpDecryptResolver() {
        return new Sm4CipherFactory();
    }

    /**
     * 接口加解密过滤器.
     */
    @Bean
    public FilterRegistrationBean<Filter> decryptServletFilter(
            SecretExchange secretExchange, HttpCipherFactory httpCipherFactory) {
        FilterRegistrationBean<Filter> registrationBean =
                new FilterRegistrationBean<>(new HttpCipherServletFilter(secretExchange, httpCipherFactory));
        registrationBean.setOrder(10);
        return registrationBean;
    }
}

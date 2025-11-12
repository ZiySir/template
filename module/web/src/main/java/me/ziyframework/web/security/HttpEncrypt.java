package me.ziyframework.web.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http接口加解密注解.<br/>
 * created on 2025-03
 * @author ziy
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpEncrypt {

    /**
     * 是否加密响应体.
     */
    boolean encrypt() default true;

    /**
     * 是否解密请求体.
     */
    boolean decrypt() default true;
}

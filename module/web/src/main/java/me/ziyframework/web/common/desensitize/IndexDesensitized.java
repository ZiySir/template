package me.ziyframework.web.common.desensitize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于下标索引脱敏注解.<br/>
 * created on 2025-02
 * @author ziy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface IndexDesensitized {

    /**
     * 下标索引集.
     */
    String[] indexes();

    /**
     * 替换字符.
     */
    char mask() default '*';

    /**
     * 是否反转.<br/>
     * 反转后会将原脱敏数据部分保留，将原保留数据脱敏.
     */
    boolean reverse() default false;
}

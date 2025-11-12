package me.ziyframework.web.common.desensitize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于正则的数据脱敏.<br />
 * created on 2025-02
 * @author ziy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface RegexDesensitized {

    /**
     * 正则表达式.
     */
    String regex();

    /**
     * 替换字符串.
     */
    String replace();
}

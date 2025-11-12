package me.ziyframework.web.common.desensitize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * 通用数据脱敏注解.<br />
 * created on 2025-02
 * @author ziy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Desensitized {

    /**
     * 策略的bean名.
     */
    @AliasFor("bean")
    String value() default "";

    /**
     * .
     * @see Desensitized#value()
     */
    @AliasFor("value")
    String bean() default "";

    /**
     * 默认采用全脱策略.
     */
    DesensitizationEnum strategy() default DesensitizationEnum.SIX_MASK;
}

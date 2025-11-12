package me.ziyframework.web.jackson.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jackson扩展参数.<br/>
 * created on 2025-01
 *
 * @author ziy
 */
@ConfigurationProperties(prefix = "ziy.web.jackson")
@Data
public class JacksonProperties {

    /**
     * Json序列化时是否将Long转为String.
     */
    private boolean longToString = true;

    /**
     * 是否将null数组/集合转为空数组/集合.
     */
    private boolean nullToEmpty = true;
}

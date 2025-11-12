package me.ziyframework.web.common.config;

import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * .<br/>
 * created on 2025-04
 * @author ziy
 */
@Data
@ConfigurationProperties(prefix = "ziy.web.common")
public class WebCommonProperties {

    /**
     * 上下文路径.
     */
    private @Nullable String context;
}

package me.ziyframework.web.log.requestid;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 请求唯一id功能配置属性类.<br/>
 * created on 2025-01
 *
 * @author ziy
 */
@ConfigurationProperties(prefix = "ziy.web.trance")
@Data
public class TranceIdProperties {

    /**
     * 是否将tranceId返回调用者.
     */
    private boolean isReturn = true;

    /**
     * 请求唯一id的key名称.
     */
    private String name = "X-TranceId";
}

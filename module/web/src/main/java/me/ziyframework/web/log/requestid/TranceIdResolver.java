package me.ziyframework.web.log.requestid;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.util.UUID;

/**
 * TranceID解析者.<br/>
 * 可以重写自定义实现RequestID的解析策略.
 * created on 2025-01
 * @author ziy
 */
@FunctionalInterface
public interface TranceIdResolver {

    TranceIdResolver UUID_V4_RESOLVER = (request, response) -> UUID.randomUUID().toString();

    /**
     * 获取TranceID.<br />
     * 可自定义实现，默认使用Request的TranceID.
     * @param request 请求
     * @param response 响应
     * @return TranceID
     */
    String resolve(ServletRequest request, ServletResponse response);
}

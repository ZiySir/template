package me.ziyframework.web.log.requestid;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;

/**
 * trance id过滤器.<br />
 * created on 2025-04
 *
 * @author ziy
 */
public class TranceIdServletFilter implements Filter {

    private final TranceIdResolver tranceIdResolver;

    private final TranceIdProperties tranceIdProperties;

    public TranceIdServletFilter(TranceIdResolver tranceIdResolver, TranceIdProperties tranceIdProperties) {
        this.tranceIdResolver = tranceIdResolver;
        this.tranceIdProperties = tranceIdProperties;
    }

    /**
     * 对RequestID的处理.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final String requestId = tranceIdResolver.resolve(request, response);
        MDCCloseable mdcCloseable = MDC.putCloseable(tranceIdProperties.getName(), requestId);
        try (mdcCloseable) {
            request.setAttribute("tranceId", requestId);
            chain.doFilter(request, response);
        } finally {
            if (tranceIdProperties.isReturn()) {
                ((HttpServletResponse) response).setHeader(tranceIdProperties.getName(), requestId);
            }
        }
    }
}

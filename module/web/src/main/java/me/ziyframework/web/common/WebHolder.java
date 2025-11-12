package me.ziyframework.web.common;

import com.google.common.collect.ImmutableMap;
import com.palantir.logsafe.Preconditions;
import com.palantir.logsafe.exceptions.SafeNullPointerException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.ziyframework.core.Lazy;
import me.ziyframework.core.SpringHolder;
import me.ziyframework.web.common.exception.GlobalException;
import me.ziyframework.web.common.result.Result;
import me.ziyframework.web.utils.JsonUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * web holder类.
 *
 * @author ziy
 */
public final class WebHolder {

    private static final Lazy<List<HandlerMapping>> HANDLER_MAPPING_LAZY = Lazy.of(() -> {
        DispatcherServlet dispatcherServlet = getDispatcherServlet();
        return dispatcherServlet.getHandlerMappings();
    });

    private WebHolder() {}

    /**
     * 获取Request，注意调用线程是否web线程.
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest =
                (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (httpServletRequest == null) {
            throw new SafeNullPointerException("无法获取Request,请检查是否在web主线程上调用");
        }
        return httpServletRequest;
    }

    /**
     * 获取Servlet环境Response.
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Preconditions.checkNotNull(servletRequestAttributes, "ServletRequestAttributes must not be null");
        HttpServletResponse response = servletRequestAttributes.getResponse();
        if (response == null) {
            throw new SafeNullPointerException("无法获取Request,请检查是否在web主线程上调用");
        }
        return response;
    }

    /**
     * 获取Servlet环境OutputStream.
     */
    public static ServletOutputStream getOutputStream() {
        HttpServletResponse response = getResponse();
        try {
            return response.getOutputStream();
        } catch (IOException e) {
            throw GlobalException.wrap(e);
        }
    }

    /**
     * 写入json数据.
     */
    public static void write(Object object) {
        try {
            getOutputStream().write(JsonUtil.toJsonSpring(object).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw GlobalException.wrap(e);
        }
    }

    /**
     * 是否web环境.
     */
    public static boolean isWeb() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    /**
     * 获取当前请求的请求方法.
     */
    public static HttpMethod getMethod() {
        return HttpMethod.valueOf(getRequest().getMethod());
    }

    /**
     * 获取当前请求的请求路径.<br/>
     * 例如: /user/profile
     */
    public static String getPath() {
        return getRequest().getRequestURI();
    }

    /**
     * 重置response内容，并返回指定的result的json字符串.
     *
     * @param result result
     */
    public static void resetResponse(Result<?> result) {
        resetResponse(HttpStatus.OK, result);
    }

    /**
     * 重置response内容，并返回指定的result的json字符串.
     */
    public static void resetResponse(HttpStatus status, Result<?> result) {
        HttpServletResponse response = getResponse();
        response.reset();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JsonUtil.toJsonSpring(result));
        } catch (IOException e) {
            throw GlobalException.wrap(e);
        }
    }

    /**
     * 获取请求头.
     */
    public static @Nullable String getRequestHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }

    public static Map<String, String> getRequestHeader(String... headers) {
        if (headers.length == 0) {
            return Collections.emptyMap();
        }
        HttpServletRequest request = getRequest();
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builderWithExpectedSize(headers.length);
        for (String header : headers) {
            builder.put(header, request.getHeader(header));
        }
        return builder.buildKeepingLast();
    }

    /**
     * {@link WebHolder#getFiles(String)}.
     */
    public static @Nullable MultipartFile getFile(String name) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartHttpServletRequest multipartHttpServletRequest) {
            return multipartHttpServletRequest.getFile(name);
        }
        throw new GlobalException("type not MultipartHttpServletRequest");
    }

    /**
     * 获取MultipartFile.<br />
     * {@code spring.servlet.multipart.resolve-lazily: true}时，可以通过该方法配合实现延迟获取文件对象.
     */
    public static List<MultipartFile> getFiles(String name) {
        HttpServletRequest request = getRequest();
        if (request instanceof MultipartHttpServletRequest multipartHttpServletRequest) {
            return multipartHttpServletRequest.getFiles(name);
        }
        throw new GlobalException("type not MultipartHttpServletRequest");
    }

    /**
     * 返回已配置的{@link HandlerMapping}.
     */
    public static List<HandlerMapping> getHandlerMapping() {
        return HANDLER_MAPPING_LAZY.getOrThrow();
    }

    /**
     * 返回当前请求的HandlerMethod.
     */
    public static @Nullable HandlerExecutionChain getHandler(HttpServletRequest request) {
        List<HandlerMapping> handlerMappings = HANDLER_MAPPING_LAZY.getOrThrow();
        for (HandlerMapping mapping : handlerMappings) {
            HandlerExecutionChain handler;
            try {
                handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            } catch (Exception e) {
                throw GlobalException.wrap(e);
            }
        }
        return null;
    }

    public static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        HandlerExecutionChain chain = getHandler(request);
        if (chain == null) {
            throw new GlobalException("无法获取HandlerMethod");
        }
        Object handler = chain.getHandler();
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod;
        }
        throw new GlobalException("无法获取HandlerMethod");
    }

    /**
     * 获取DispatcherServlet.
     */
    private static DispatcherServlet getDispatcherServlet() {
        return SpringHolder.getBean(DispatcherServlet.class);
    }
}

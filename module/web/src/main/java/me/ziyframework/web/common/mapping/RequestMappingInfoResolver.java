package me.ziyframework.web.common.mapping;

import java.lang.reflect.Method;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * 接口映射信息解析器.
 * @author ziy
 */
@FunctionalInterface
public interface RequestMappingInfoResolver {

    /**
     * RequestMapping自定义处理.
     * <pre>
     * {@code
     * // example: 设置统一前缀(上下文)
     * String[] paths = mappingInfo.getPatternValues().stream()
     *                   .map(path -> "/chrray/admin" + path)
     *                   .toArray(String[]::new);
     * return mappingInfo.mutate().paths(paths).build();
     * }
     * </pre>
     * @param mappingInfo 接口映射信息
     * @param method method
     * @param handlerType handlerType
     * @return 将应用的RequestMappingInfo,如果不改变则应该返回原mappingInfo
     */
    RequestMappingInfo resolve(RequestMappingInfo mappingInfo, Method method, Class<?> handlerType);
}

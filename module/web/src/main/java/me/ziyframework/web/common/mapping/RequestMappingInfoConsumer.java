package me.ziyframework.web.common.mapping;

import java.lang.reflect.Method;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * RequestMapping的消费者，不会返回RequestMapping.<br/>
 * created on 2025-03
 * @author ziy
 */
@FunctionalInterface
public interface RequestMappingInfoConsumer {

    /**
     * RequestMapping自定义处理.
     * @param mappingInfo 接口映射信息
     * @param method method
     * @param handlerType handlerType
     */
    void accept(RequestMappingInfo mappingInfo, Method method, Class<?> handlerType);

    /**
     * 在所有接口都调用{@link RequestMappingInfoConsumer#accept}完成后，就会调用该方法.
     */
    default void after() {}
}

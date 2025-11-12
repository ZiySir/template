package me.ziyframework.web.common.mapping;

import com.palantir.logsafe.exceptions.SafeNullPointerException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 接口path自定义扩展.
 * @author ziy
 */
@RequiredArgsConstructor
public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final List<RequestMappingInfoResolver> resolvers;

    private final List<RequestMappingInfoConsumer> consumers;

    /**
     * 自定义接口path.
     */
    @Override
    protected @Nullable RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo == null) {
            return null;
        }
        RequestMappingInfo newMappingInfo = mappingInfo;
        if (!CollectionUtils.isEmpty(resolvers)) {
            newMappingInfo = resolvers.stream()
                    .map(resolver -> resolver.resolve(mappingInfo, method, handlerType))
                    .reduce(RequestMappingInfo::combine)
                    .orElseThrow(() -> new SafeNullPointerException("RequestMappingInfoResolver return null"));
        }

        for (RequestMappingInfoConsumer consumer : consumers) {
            consumer.accept(newMappingInfo, method, handlerType);
        }
        for (RequestMappingInfoConsumer consumer : consumers) {
            consumer.after();
        }

        return newMappingInfo;
    }
}

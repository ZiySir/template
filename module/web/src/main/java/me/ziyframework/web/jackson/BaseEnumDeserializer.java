package me.ziyframework.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.palantir.logsafe.Preconditions;
import com.palantir.logsafe.SafeArg;
import com.palantir.logsafe.exceptions.SafeIllegalArgumentException;
import java.io.IOException;
import me.ziyframework.core.enumeration.BaseEnum;
import org.jspecify.annotations.Nullable;

/**
 * BaseEnum反序列化.<br />
 * created on 2025-03
 *
 * @author ziy
 */
public final class BaseEnumDeserializer extends JsonDeserializer<BaseEnum> implements ContextualDeserializer {

    public static final BaseEnumDeserializer INSTANCE = new BaseEnumDeserializer();

    private @Nullable Class<? extends BaseEnum> baseEnumCls;

    private BaseEnumDeserializer() {}

    /**
     * BaseEnum反序列化.
     */
    @Override
    public BaseEnum deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        // 获取JSON输入值（支持数字或字符串类型的code）
        Integer codeValue = parser.readValueAs(Integer.class);
        // 调用BaseEnum的fromCode方法获取枚举实例
        BaseEnum baseEnum =
                BaseEnum.fromCode(codeValue, Preconditions.checkNotNull(baseEnumCls, "BaseEnum class not set"));
        if (baseEnum == null) {
            throw new SafeIllegalArgumentException("无效值: ", SafeArg.of("code", codeValue));
        }
        return baseEnum;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonDeserializer<?> createContextual(
            DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        BaseEnumDeserializer deserializer = new BaseEnumDeserializer();
        JavaType type = beanProperty.getType();
        Class<?> rawClass = type.getRawClass();
        deserializer.baseEnumCls = (Class<? extends BaseEnum>) rawClass;
        return deserializer;
    }
}

package me.ziyframework.web.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import me.ziyframework.core.enumeration.BaseEnum;

/**
 * .<br/>
 * created on 2025-03
 *
 * @author ziy
 */
public class BaseEnumJacksonDeserializerModifier extends BeanDeserializerModifier {

    /**
     * 将BaseEnum的子类反序列化调整为BaseEnumDeserializer.
     */
    @Override
    public JsonDeserializer<?> modifyDeserializer(
            DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (BaseEnum.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return BaseEnumDeserializer.INSTANCE;
        }
        return deserializer;
    }

    /**
     * 将BaseEnum的子类反序列化调整为BaseEnumDeserializer.
     */
    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(
            DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (BaseEnum.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return BaseEnumDeserializer.INSTANCE;
        }
        return deserializer;
    }
}

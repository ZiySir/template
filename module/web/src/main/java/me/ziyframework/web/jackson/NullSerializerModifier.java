package me.ziyframework.web.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 处理Null的反序列化.<br />
 * created on 2025-01
 * @author ziy
 */
public class NullSerializerModifier extends BeanSerializerModifier {

    @Override
    public final List<BeanPropertyWriter> changeProperties(
            SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            JavaType type = beanProperty.getType();
            if (isArrayLikeType(type)) {
                beanProperty.assignNullSerializer(NullArrayLikeSerializer.INSTANCE);
            } else if (isMapType(type)) {
                beanProperty.assignNullSerializer(NullMapSerializer.INSTANCE);
            }
        }
        return beanProperties;
    }

    private boolean isMapType(JavaType type) {
        return type.isMapLikeType();
    }

    private boolean isArrayLikeType(JavaType type) {
        Class<?> rawClass = type.getRawClass();
        return rawClass.isArray() || Collection.class.isAssignableFrom(rawClass);
    }

    /**
     * 处理空数组/集合等类似结构的序列化.
     */
    public static final class NullArrayLikeSerializer extends JsonSerializer<Object> {

        public static final NullArrayLikeSerializer INSTANCE = new NullArrayLikeSerializer();

        private NullArrayLikeSerializer() {}

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            gen.writeEndArray();
        }
    }

    // null Map序列化.
    public static final class NullMapSerializer extends JsonSerializer<Object> {

        public static final NullMapSerializer INSTANCE = new NullMapSerializer();

        private NullMapSerializer() {}

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeEndObject();
        }
    }
}

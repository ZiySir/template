package me.ziyframework.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.palantir.logsafe.SafeArg;
import com.palantir.logsafe.exceptions.SafeIllegalArgumentException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import org.jspecify.annotations.Nullable;

/**
 * .<br />
 * created on 2025-03
 *
 * @author ziy
 */
public class DateDeserializer<T> extends JsonDeserializer<T> implements ContextualDeserializer {

    private @Nullable JavaType contextualType; // 存储上下文类型

    /**
     * 创建上下文.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        DateDeserializer<?> deserializer = new DateDeserializer<>();
        deserializer.contextualType = ctxt.getContextualType();
        return deserializer;
    }

    /**
     * 反序列化时间戳.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        final long timestamp = parser.getValueAsLong();
        if (timestamp < 0) {
            throw new IllegalArgumentException("Invalid Unix timestamp: " + timestamp);
        }
        if (contextualType == null) {
            throw new JsonMappingException(parser, "Contextual type not set");
        }
        Class<?> cls = contextualType.getRawClass();
        if (LocalDateTime.class.isAssignableFrom(cls)) {
            return (T) LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ctxt.getTimeZone().toZoneId());
        }
        if (LocalDate.class.isAssignableFrom(cls)) {
            return (T) LocalDate.ofInstant(
                    Instant.ofEpochMilli(timestamp), ctxt.getTimeZone().toZoneId());
        }
        if (LocalTime.class.isAssignableFrom(cls)) {
            return (T) LocalTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ctxt.getTimeZone().toZoneId());
        }
        if (OffsetDateTime.class.isAssignableFrom(cls)) {
            return (T) OffsetDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ctxt.getTimeZone().toZoneId());
        }
        if (OffsetTime.class.isAssignableFrom(cls)) {
            return (T) OffsetTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ctxt.getTimeZone().toZoneId());
        }
        if (Instant.class.isAssignableFrom(cls)) {
            return (T) Instant.ofEpochMilli(timestamp);
        }
        throw new SafeIllegalArgumentException("Unsupported type: ", SafeArg.of("type", cls.getName()));
    }
}

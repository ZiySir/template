package me.ziyframework.web.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.palantir.logsafe.SafeArg;
import com.palantir.logsafe.exceptions.SafeIllegalArgumentException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.concurrent.TimeUnit;
import me.ziyframework.core.utils.DateUtil;
import me.ziyframework.web.jackson.config.JacksonProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.util.Assert;

/**
 * LocalDate、LocalDateTime、LocalTime的序列化.<br />
 * created on 2025-03
 *
 * @author ziy
 */
public class DateSerializer<T> extends JsonSerializer<T> implements ContextualSerializer {

    private final JacksonProperties properties;

    private @Nullable DateTimeFormatter formatter;

    private ZoneId timeZone = DateUtil.DEFAULT_ZONE;

    public DateSerializer(JacksonProperties properties) {
        this.properties = properties;
    }

    /**
     * 实现将LocalDate序列化为Unix时间戳.
     */
    @Override
    public void serialize(T value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (value instanceof LocalDateTime
                || value instanceof LocalTime
                || value instanceof LocalDate
                || value instanceof OffsetDateTime
                || value instanceof Instant) {
            write((Temporal) value, jsonGenerator);
            return;
        }
        throw new SafeIllegalArgumentException(
                "Unsupported type: ", SafeArg.of("type", value.getClass().getName()));
    }

    /**
     * .
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) {
        AnnotationIntrospector introspector = serializerProvider.getAnnotationIntrospector();
        AnnotatedMember member = beanProperty.getMember();

        DateSerializer<Object> serializer = new DateSerializer<>(this.properties);
        JsonFormat.Value format = introspector.findFormat(member);
        if (format != null
                && format.getPattern() != null
                && !format.getPattern().isEmpty()) {
            serializer.formatter = DateTimeFormatter.ofPattern(format.getPattern());
            serializer.timeZone = format.hasTimeZone()
                    ? format.getTimeZone().toZoneId()
                    : serializerProvider.getTimeZone().toZoneId();
        } else {
            serializer.timeZone = serializerProvider.getTimeZone().toZoneId();
        }
        return serializer;
    }

    private void write(Temporal temporal, JsonGenerator jsonGenerator) throws IOException {
        Assert.notNull(temporal, "temporal must not be null");
        if (formatter != null) {
            // 格式化日期
            jsonGenerator.writeString(formatter.withZone(timeZone).format(temporal));
        } else {
            // 转换为毫秒时间戳
            Long millis = DateUtil.to(temporal, timeZone, TimeUnit.MILLISECONDS);
            if (properties.isLongToString()) {
                jsonGenerator.writeString(String.valueOf(millis));
            } else {
                jsonGenerator.writeNumber(millis);
            }
        }
    }
}

package me.ziyframework.web.jackson.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import lombok.RequiredArgsConstructor;
import me.ziyframework.core.enumeration.BaseEnum;
import me.ziyframework.web.jackson.BaseEnumDeserializer;
import me.ziyframework.web.jackson.BaseEnumJacksonDeserializerModifier;
import me.ziyframework.web.jackson.BaseEnumSerializer;
import me.ziyframework.web.jackson.DateDeserializer;
import me.ziyframework.web.jackson.DateSerializer;
import me.ziyframework.web.jackson.DesensitizationJacksonSerializerModifier;
import me.ziyframework.web.jackson.NullSerializerModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * .<br/>
 * created on 2025-01
 *
 * @author ziy
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(JacksonProperties.class)
@ComponentScan(basePackages = "me.ziyframework.web.jackson")
public class JacksonAutoConfiguration {

    private final JacksonProperties jacksonProperties;

    /**
     * 自定义ObjectMapper.
     */
    @Autowired
    public void customObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(localModule());
        objectMapper.registerModule(baseEnumModule());

        if (jacksonProperties.isLongToString()) {
            objectMapper.registerModule(toStringModule());
        }

        SerializerFactory factory = objectMapper
                .getSerializerFactory()
                .withSerializerModifier(new DesensitizationJacksonSerializerModifier());
        if (jacksonProperties.isNullToEmpty()) {
            factory.withSerializerModifier(new NullSerializerModifier());
        }
        objectMapper.setSerializerFactory(factory);
    }

    private Module toStringModule() {
        SimpleModule module = new SimpleModule("custom");
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return module;
    }

    private Module localModule() {
        SimpleModule module = new SimpleModule("custom-LocalXxx");
        module.addSerializer(LocalDateTime.class, new DateSerializer<>(jacksonProperties));
        module.addSerializer(LocalDate.class, new DateSerializer<>(jacksonProperties));
        module.addSerializer(LocalTime.class, new DateSerializer<>(jacksonProperties));
        module.addSerializer(OffsetDateTime.class, new DateSerializer<>(jacksonProperties));
        module.addSerializer(OffsetTime.class, new DateSerializer<>(jacksonProperties));

        module.addDeserializer(LocalDateTime.class, new DateDeserializer<>());
        module.addDeserializer(LocalDate.class, new DateDeserializer<>());
        module.addDeserializer(LocalTime.class, new DateDeserializer<>());
        module.addDeserializer(OffsetDateTime.class, new DateDeserializer<>());
        module.addDeserializer(OffsetTime.class, new DateDeserializer<>());
        return module;
    }

    private Module baseEnumModule() {
        SimpleModule module = new SimpleModule("custom-BaseEnum");
        module.addSerializer(BaseEnum.class, BaseEnumSerializer.INSTANCE);
        module.addDeserializer(BaseEnum.class, BaseEnumDeserializer.INSTANCE);
        module.setDeserializerModifier(new BaseEnumJacksonDeserializerModifier());
        return module;
    }
}

package me.ziyframework.web.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.io.IOException;
import java.util.List;
import me.ziyframework.core.SpringHolder;
import me.ziyframework.web.common.desensitize.DesensitizationStrategy;
import me.ziyframework.web.common.desensitize.DesensitizationUtil;
import me.ziyframework.web.common.desensitize.Desensitized;
import me.ziyframework.web.common.desensitize.IndexDesensitized;
import me.ziyframework.web.common.desensitize.RegexDesensitized;
import me.ziyframework.web.common.desensitize.SlideDesensitized;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 脱敏Jackson序列化器.<br />
 * created on 2025-02
 *
 * @author ziy
 */
public class DesensitizationJacksonSerializerModifier extends BeanSerializerModifier {

    @Override
    public final List<BeanPropertyWriter> changeProperties(
            SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            Desensitized desensitized;
            SlideDesensitized slideDesensitized;
            RegexDesensitized regexDesensitized;
            IndexDesensitized indexDesensitized;
            if ((desensitized = beanProperty.getAnnotation(Desensitized.class)) != null) {
                beanProperty.assignSerializer(new DesensitizedJsonSerializer(desensitized));
                continue;
            }
            if (beanProperty.getType().getRawClass() == String.class) {
                if ((slideDesensitized = beanProperty.getAnnotation(SlideDesensitized.class)) != null) {
                    beanProperty.assignSerializer(new SlideDesensitizedJsonSerializer(slideDesensitized));
                } else if ((regexDesensitized = beanProperty.getAnnotation(RegexDesensitized.class)) != null) {
                    beanProperty.assignSerializer(new RegexDesensitizedJsonSerializer(regexDesensitized));
                } else if ((indexDesensitized = beanProperty.getAnnotation(IndexDesensitized.class)) != null) {
                    beanProperty.assignSerializer(new IndexDesensitizedJsonSerializer(indexDesensitized));
                }
            }
        }
        return beanProperties;
    }

    private static final class DesensitizedJsonSerializer extends JsonSerializer<Object> {

        private final Desensitized desensitized;

        private DesensitizedJsonSerializer(Desensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            String bean = (String) AnnotationUtils.getValue(desensitized);
            DesensitizationStrategy strategy;
            if (bean == null || bean.isEmpty()) {
                strategy = desensitized.strategy();
            } else {
                strategy = SpringHolder.getBean(bean, DesensitizationStrategy.class);
            }
            gen.writeObject(strategy.desensitize(value));
        }
    }

    private static final class SlideDesensitizedJsonSerializer extends JsonSerializer<Object> {

        private final SlideDesensitized desensitized;

        private SlideDesensitizedJsonSerializer(SlideDesensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value instanceof String str) {
                gen.writeString(DesensitizationUtil.slide(
                        str, desensitized.left(), desensitized.right(), desensitized.mask(), desensitized.reverse()));
            }
        }
    }

    private static final class RegexDesensitizedJsonSerializer extends JsonSerializer<Object> {

        private final RegexDesensitized desensitized;

        private RegexDesensitizedJsonSerializer(RegexDesensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value instanceof String str) {
                gen.writeString(DesensitizationUtil.regex(str, desensitized.regex(), desensitized.replace()));
            }
        }
    }

    private static final class IndexDesensitizedJsonSerializer extends JsonSerializer<Object> {

        private final IndexDesensitized desensitized;

        private IndexDesensitizedJsonSerializer(IndexDesensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value instanceof String str) {
                gen.writeString(DesensitizationUtil.index(
                        str, desensitized.mask(), desensitized.reverse(), desensitized.indexes()));
            }
        }
    }
}

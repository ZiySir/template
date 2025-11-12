package me.ziyframework.web.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import me.ziyframework.core.enumeration.BaseEnum;

/**
 * 枚举反序列化为整数类型.<br />
 * created on 2025-02
 *
 * @author ziy
 */
public final class BaseEnumSerializer extends JsonSerializer<BaseEnum> {

    public static final BaseEnumSerializer INSTANCE = new BaseEnumSerializer();

    private BaseEnumSerializer() {}

    @Override
    public void serialize(BaseEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getCode());
    }
}

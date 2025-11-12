package me.ziyframework.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.Getter;
import me.ziyframework.core.Lazy;
import me.ziyframework.core.SpringHolder;
import me.ziyframework.web.common.exception.GlobalException;

/**
 * Json工具类.<br/>
 * created on 2025-01
 *
 * @author ziy
 */
@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class JsonUtil {

    /**
     * 默认的ObjectMapper.
     */
    @Getter
    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();

    /**
     * springboot框架中注入的ObjectMapper.
     */
    private static final Lazy<ObjectMapper> springObjectMapperLazy =
            Lazy.of(() -> SpringHolder.getBean(ObjectMapper.class));

    private JsonUtil() {}

    /**
     * 将对象转换为Json字符串.
     *
     * @param obj 源对象
     * @return Json字符串
     */
    public static String toJson(Object obj) {
        return toJson(defaultObjectMapper, obj);
    }

    /**
     * 将对象转换为Json字节数组.
     *
     * @param obj 源对象
     * @return json字节数组
     */
    public static byte[] toJsonBytes(Object obj) {
        return toJsonBytes(defaultObjectMapper, obj);
    }

    /**
     * 将Json字符串转换为对象.
     *
     * @param json json
     * @param type 目标类型
     * @return 目标对象
     */
    public static <T> T parse(String json, Class<T> type) {
        return parse(defaultObjectMapper, json, type);
    }

    /**
     * 将Json字符串转换为对象.
     *
     * @param json json
     * @param type 源类型
     * @return 目标对象
     */
    public static <T> T parse(String json, TypeReference<T> type) {
        return parse(defaultObjectMapper, json, type);
    }

    /**
     * 将Json字节数组转换为对象.
     *
     * @param json json字节数组
     * @param type 源类型
     * @return 目标对象
     */
    public static <T> T parse(byte[] json, TypeReference<T> type) {
        return parse(defaultObjectMapper, json, type);
    }

    /**
     * 将Json字节数组转换为对象.
     *
     * @param json json字节数组
     * @param type 源类型
     * @return 源对象
     */
    public static <T> T parse(byte[] json, Class<T> type) {
        return parse(defaultObjectMapper, json, type);
    }

    /**
     * 将对象使用SpringBoot的ObjectMapper转换为Json字符串.
     *
     * @param obj 对象
     * @return json字符串
     */
    public static String toJsonSpring(Object obj) {
        return toJson(getSpringObjectMapper(), obj);
    }

    /**
     * 将对象使用SpringBoot的ObjectMapper转换为Json字节数组.
     *
     * @param obj 源对象
     * @return json字节数组
     */
    public static byte[] toJsonBytesSpring(Object obj) {
        return toJsonBytes(getSpringObjectMapper(), obj);
    }

    /**
     * 将json反序列化为对象.
     *
     * @param json json
     * @param type 目标类型
     * @return 目标对象
     */
    public static <T> T parseJsonSpring(String json, Class<T> type) {
        return parse(getSpringObjectMapper(), json, type);
    }

    /**
     * 将json反序列化为对象.
     *
     * @param json json
     * @return 对象
     */
    public static <T> T parseJsonSpring(String json, TypeReference<T> type) {
        return parse(getSpringObjectMapper(), json, type);
    }

    /**
     * 将json反序列化为对象.
     *
     * @param json json
     * @return 对象
     */
    public static <T> T parseJsonSpring(byte[] json, Class<T> type) {
        return parse(getSpringObjectMapper(), json, type);
    }

    /**
     * 将json反序列化为对象.
     *
     * @param json json
     * @return 对象
     */
    public static <T> T parseJsonSpring(byte[] json, TypeReference<T> type) {
        return parse(getSpringObjectMapper(), json, type);
    }

    /**
     * 将对象转换为Json字符串.
     *
     * @param objectMapper 对象映射器
     * @param obj 源对象
     * @return json字符串
     */
    public static String toJson(ObjectMapper objectMapper, Object obj) {
        try {
            return objectMapper.writer().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw GlobalException.wrap(e);
        }
    }

    /**
     * 将对象转换为Json字节数组.
     *
     * @param objectMapper 映射器
     * @param obj 源对象
     * @return json字节数组
     */
    public static byte[] toJsonBytes(ObjectMapper objectMapper, Object obj) {
        try {
            return objectMapper.writer().writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw GlobalException.wrap(e);
        }
    }

    public static <T> T parse(ObjectMapper objectMapper, String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw GlobalException.wrap(e);
        }
    }

    public static <T> T parse(ObjectMapper objectMapper, String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw GlobalException.wrap(e);
        }
    }

    public static <T> T parse(ObjectMapper objectMapper, byte[] json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw GlobalException.wrap(e);
        }
    }

    public static <T> T parse(ObjectMapper objectMapper, byte[] json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw GlobalException.wrap(e);
        }
    }

    /**
     * 获取SpringBoot中用于序列化的ObjectMapper.
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getSpringObjectMapper() {
        return springObjectMapperLazy.getOrThrow();
    }
}

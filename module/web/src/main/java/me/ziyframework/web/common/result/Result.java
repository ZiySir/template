package me.ziyframework.web.common.result;

import org.jspecify.annotations.Nullable;

/**
 * 统一封装结果.
 *
 * @param code 状态码.
 * @param msg 返回信息.
 * @param payload 返回数据.
 * @author ziy
 */
public record Result<T>(int code, @Nullable String msg, @Nullable T payload) {

    public static <T> Result<T> ok(T payload) {
        return new Result<>(ResultCode.OK.code(), ResultCode.OK.msg(), payload);
    }

    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.OK.code(), ResultCode.OK.msg(), null);
    }

    public static <T> Result<T> fail(T payload) {
        return new Result<>(ResultCode.FAIL.code(), ResultCode.FAIL.msg(), payload);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAIL.code(), ResultCode.FAIL.msg(), null);
    }

    public static <T> Result<T> of(int code, @Nullable String msg, @Nullable T payload) {
        return new Result<>(code, msg, payload);
    }

    public static <T> Result<T> of(ResultCode resultCode, @Nullable T payload) {
        return new Result<>(resultCode.code(), resultCode.msg(), payload);
    }

    public static <T> Result<T> of(ResultCode resultCode) {
        return new Result<>(resultCode.code(), resultCode.msg(), null);
    }
}

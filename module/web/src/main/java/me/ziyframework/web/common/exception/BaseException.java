package me.ziyframework.web.common.exception;

import lombok.Getter;
import me.ziyframework.core.i18n.MessageSourceHolder;
import me.ziyframework.web.common.result.ResultCode;
import org.slf4j.helpers.MessageFormatter;

/**
 * 基础异常.
 * @author ziy
 */
public class BaseException extends RuntimeException {

    @Getter
    private final ResultCode code;

    private final Object[] args;

    private final String message;

    /**
     * 创建基础异常.
     *
     * @param code 错误码
     * @param cause 异常栈
     * @param message 错误信息
     * @param args 参数
     */
    public BaseException(ResultCode code, Throwable cause, String message, Object... args) {
        super(message, cause);
        this.code = code;
        this.args = args;
        this.message = message;
    }

    /**
     * 创建基础异常.
     *
     * @param code 错误码
     * @param message 错误信息
     * @param args 参数
     */
    public BaseException(ResultCode code, String message, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
        this.message = message;
    }

    public BaseException(String message, Object... args) {
        this(ResultCode.FAIL, message, args);
    }

    /**
     * 支持国际化处理.
     */
    @Override
    public String getMessage() {
        if (message.startsWith("{") && message.endsWith("}")) {
            return MessageSourceHolder.i18n(message.substring(1, message.length() - 1), args);
        }
        return MessageFormatter.arrayFormat(message, args).getMessage();
    }
}

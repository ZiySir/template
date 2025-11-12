package me.ziyframework.web.common.exception;

import me.ziyframework.web.common.result.ResultCode;

/**
 * 客户端异常.<br/>
 * 主要用于调用方问题导致的异常.<br/>
 *
 * @author ziy
 */
public class ClientException extends BaseException {

    public ClientException(Throwable cause, String message, Object... args) {
        super(ResultCode.BAD_REQUEST, cause, message, args);
    }

    public ClientException(String message, Object... args) {
        super(ResultCode.FAIL, message, args);
    }
}

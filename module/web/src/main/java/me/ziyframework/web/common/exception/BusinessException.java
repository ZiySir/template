package me.ziyframework.web.common.exception;

import me.ziyframework.web.common.result.ResultCode;

/**
 * 业务异常.<br/>
 * 用于表示某个执行过程不符合业务流程导致的错误.
 *
 * @author ziy
 */
public class BusinessException extends BaseException {

    public BusinessException(ResultCode resultCode, Throwable cause, String message, Object... args) {
        super(resultCode, cause, message, args);
    }

    public BusinessException(ResultCode resultCode, String message, Object... args) {
        super(resultCode, message, args);
    }

    public BusinessException(String message, Object... args) {
        super(ResultCode.FAIL, message, args);
    }
}

package me.ziyframework.web.common.exception;

import com.palantir.logsafe.SafeArg;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.ziyframework.web.common.result.Result;
import me.ziyframework.web.common.result.ResultCode;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 统一的异常处理.
 *
 * @author ziy
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 处理任何异常.
     */
    @ExceptionHandler(Throwable.class)
    public Result<Void> otherException(Throwable th) {
        log.warn("Unknown Exception", th);
        return Result.fail();
    }

    /**
     * 拦截基础异常.
     */
    @ExceptionHandler(BaseException.class)
    public Result<String> globalExceptionHandle(BaseException ex) {
        log.warn("Global Exception", ex);
        return Result.of(ex.getCode(), null);
    }

    //
    // 拦截一些必要的框架异常.
    //

    /**
     * 缺少请求参数异常.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("Request Parameter Missing", ex);
        return Result.of(
                ResultCode.BAD_REQUEST.code(),
                MessageFormatter.format("Missing Parameter <{}>", ex.getParameterName()),
                null);
    }

    /**
     * 绑定异常.<br />
     * 一般出现于 <b>参数校验异常</b>或<b>未绑定到控制器入参对象</b>时发生的异常.
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException bindException) {
        log.warn("Bind Exception", bindException);
        List<ObjectError> errors = bindException.getBindingResult().getAllErrors();
        String message = ResultCode.BAD_REQUEST.msg();
        if (!errors.isEmpty()) {
            // 永远只返回前端第一个错误信息.
            message = errors.get(0).getDefaultMessage();
        }
        return Result.of(ResultCode.BAD_REQUEST.code(), message, null);
    }

    /**
     * 主要发生于方法参数上进行参数校验失败发生的异常.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result<?> handlerMethodValidationException(HandlerMethodValidationException ex) {
        log.warn("Method Validation Exception", ex);
        return Result.of(ResultCode.BAD_REQUEST.code(), ex.getMessage(), null);
    }

    /**
     * Http消息不可读异常.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Http Message Not Readable", ex);
        return Result.of(ResultCode.BAD_REQUEST);
    }

    /**
     * 错误的http请求的异常.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleRequestMethodNotSupportException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Http Request Not Support", ex);
        return Result.of(ResultCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 处理404异常.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn(
                "Resource Not Found method<{}> path<{}>",
                SafeArg.of("method", ex.getHttpMethod()),
                SafeArg.of("path", ex.getResourcePath()),
                ex);
        return Result.of(ResultCode.NOT_FOUND);
    }
}

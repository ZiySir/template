package me.ziyframework.web.common.result;

import me.ziyframework.core.i18n.MessageSourceHolder;

/**
 * 结果码枚举.
 *
 * @author ziy
 */
public record ResultCode(int code, String msg) {

    public static final ResultCode OK = new ResultCode(200_0, "ok");

    /**
     * 通用错误.
     */
    public static final ResultCode FAIL = new ResultCode(500_0, "fail");

    /**
     * 请求参数错误.
     */
    public static final ResultCode BAD_REQUEST = new ResultCode(400_00, "bad request");

    /**
     * 请勿重复提交.
     */
    public static final ResultCode IDEMPOTENT = new ResultCode(400_01, "duplicate request");

    /**
     * 请求方法不允许.
     */
    public static final ResultCode METHOD_NOT_ALLOWED = new ResultCode(405_00, "request method is not allowed");

    /**
     * 请求资源不存在.
     */
    public static final ResultCode NOT_FOUND = new ResultCode(404_00, "not found");

    /**
     * 未授权访问.
     */
    public static final ResultCode UNAUTHORIZED = new ResultCode(401_00, "unauthorized");

    /**
     * 禁止访问.
     */
    public static final ResultCode FORBIDDEN = new ResultCode(403_00, "forbidden");

    public ResultCode(int code, String msg) {
        this.code = code;
        this.msg = MessageSourceHolder.i18n(msg);
    }
}

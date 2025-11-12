package me.ziyframework.web.security;

/**
 * 接口加解密相关的http请求/响应头常量.<br />
 * created on 2025-04
 *
 * @author ziy
 */
public final class SecurityHttpHeaders {

    /**
     * 要求服务端使用的密钥对的id.
     */
    public static final String X_KEY_ID = "X-Server-Key-ID";

    /**
     * 客户端自己的公钥请求头(base64编码).
     */
    public static final String X_KEY = "X-Key";

    /**
     * 客户端传输共享密钥的iv请求头名称(base64编码).
     */
    public static final String X_NONCE = "X-Nonce";

    private SecurityHttpHeaders() {}
}

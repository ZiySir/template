package me.ziyframework.web.security.filter;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.crypto.Cipher;
import lombok.Getter;
import me.ziyframework.web.security.cipher.HttpCipherFactory;
import org.jspecify.annotations.Nullable;

/**
 * 支持请求体解密的ServletRequest.<br />
 * created on 2025-04
 *
 * @author ziy
 */
public final class HttpDecryptServletRequest extends HttpServletRequestWrapper {

    /**
     * -- GETTER --
     * 获取此次请求中的共享密钥.
     */
    @Getter
    private final byte[] secretBytes;

    private final byte[] iv;

    private final HttpCipherFactory httpCipherFactory;

    private @Nullable CipherServletInputStream servletInputStream;

    HttpDecryptServletRequest(
            HttpServletRequest request, HttpCipherFactory httpCipherFactory, byte[] secretBytes, byte[] iv) {
        super(request);
        this.httpCipherFactory = httpCipherFactory;
        this.secretBytes = secretBytes;
        this.iv = iv;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (servletInputStream == null) {
            Cipher cipher = httpCipherFactory.createCipher(Cipher.DECRYPT_MODE, secretBytes, iv);
            servletInputStream = new CipherServletInputStream(super.getInputStream(), cipher);
        }
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ServletInputStream inputStream = this.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
    }
}

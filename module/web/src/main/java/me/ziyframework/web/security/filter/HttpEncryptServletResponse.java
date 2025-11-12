package me.ziyframework.web.security.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.crypto.Cipher;
import me.ziyframework.web.security.cipher.HttpCipherFactory;
import org.jspecify.annotations.Nullable;

/**
 * 支持加密的响应体的ServletRequest.<br />
 * created on 2025-04
 * @author ziy
 */
public final class HttpEncryptServletResponse extends HttpServletResponseWrapper {

    private final byte[] secretBytes;

    private final byte[] iv;

    private final HttpCipherFactory httpCipherFactory;

    private @Nullable ServletOutputStream servletOutputStream;

    private @Nullable PrintWriter writer;

    public HttpEncryptServletResponse(
            HttpServletResponse response, HttpCipherFactory httpCipherFactory, byte[] secretBytes, byte[] iv) {
        super(response);
        this.httpCipherFactory = httpCipherFactory;
        this.secretBytes = secretBytes;
        this.iv = iv;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletOutputStream == null) {
            Cipher cipher = httpCipherFactory.createCipher(Cipher.ENCRYPT_MODE, secretBytes, iv);
            servletOutputStream = new CipherServletOutputStream(super.getOutputStream(), cipher);
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
        }
        return writer;
    }
}

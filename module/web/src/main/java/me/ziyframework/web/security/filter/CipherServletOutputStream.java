package me.ziyframework.web.security.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * 支持响应体加密的Servlet输出流.<br />
 * created on 2025-04
 * @author ziy
 */
@SuppressWarnings("checkstyle:ParameterName")
public final class CipherServletOutputStream extends ServletOutputStream {

    private final CipherOutputStream cipherOutputStream;

    private final ServletOutputStream originalOutputStream;

    CipherServletOutputStream(ServletOutputStream originalOutputStream, Cipher cipher) {
        this.originalOutputStream = originalOutputStream;
        this.cipherOutputStream = new CipherOutputStream(originalOutputStream, cipher);
    }

    @Override
    public void write(int b) throws IOException {
        cipherOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        cipherOutputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        cipherOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        cipherOutputStream.close();
    }

    @Override
    public boolean isReady() {
        return originalOutputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        originalOutputStream.setWriteListener(listener);
    }
}

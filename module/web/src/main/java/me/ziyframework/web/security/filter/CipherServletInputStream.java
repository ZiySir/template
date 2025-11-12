package me.ziyframework.web.security.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import org.jetbrains.annotations.NotNull;

/**
 * 支持接口解密的Servlet输入流.<br />
 * created on 2025-04
 * @author ziy
 */
@SuppressWarnings("checkstyle:ParameterName")
public final class CipherServletInputStream extends ServletInputStream {

    private final CipherInputStream cipherInputStream;

    private final ServletInputStream originalInputStream;

    CipherServletInputStream(ServletInputStream originalInputStream, Cipher cipher) {
        this.originalInputStream = originalInputStream;
        this.cipherInputStream = new CipherInputStream(originalInputStream, cipher);
    }

    @Override
    public boolean isFinished() {
        return originalInputStream.isFinished();
    }

    @Override
    public boolean isReady() {
        return originalInputStream.isReady();
    }

    @Override
    public void setReadListener(ReadListener listener) {
        originalInputStream.setReadListener(listener);
    }

    @Override
    public int read() throws IOException {
        return cipherInputStream.read();
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return cipherInputStream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        this.cipherInputStream.close();
    }
}

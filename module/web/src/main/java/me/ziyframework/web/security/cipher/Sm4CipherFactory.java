package me.ziyframework.web.security.cipher;

import javax.crypto.Cipher;
import me.ziyframework.core.utils.sm.Sm4;

/**
 * sm4工厂类.<br />
 * created on 2025-04
 * @author ziy
 */
public class Sm4CipherFactory implements HttpCipherFactory {

    /**
     * 创建支持Sm4的Cipher.<br/>
     * 需要处理key到128bit、iv到128bit
     */
    @Override
    public Cipher createCipher(int mode, byte[] key, byte[] iv) {
        return Sm4.createCipher(mode, Sm4.TRANSFORMATION, normalize(key), normalize(iv));
    }

    private byte[] normalize(byte[] bytes) {
        if (bytes.length == 16) {
            return bytes;
        }
        byte[] newBytes = new byte[16];
        System.arraycopy(bytes, 0, newBytes, 0, Math.min(bytes.length, 16));
        return newBytes;
    }
}

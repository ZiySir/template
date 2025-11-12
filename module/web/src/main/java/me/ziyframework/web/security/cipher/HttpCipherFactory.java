package me.ziyframework.web.security.cipher;

import javax.crypto.Cipher;

/**
 * 用于实现http接口加解密.<br />
 * created on 2025-04
 * @author ziy
 */
public interface HttpCipherFactory {

    /**
     * 创建加解密对象.
     * @param mode 模式 {@link Cipher#ENCRYPT_MODE}等
     * @param key 密钥 由{@link me.ziyframework.web.security.exchange.SecretExchange}协商出来,注意密钥可能不符合当前选择的加解密算法的密钥要求，需要自行处理
     * @param iv 初始化向量,注意初始化向量可能不符合当前选择的加解密算法的初始化向量要求，需要自行处理
     * @return 加解密对象
     */
    Cipher createCipher(int mode, byte[] key, byte[] iv);
}

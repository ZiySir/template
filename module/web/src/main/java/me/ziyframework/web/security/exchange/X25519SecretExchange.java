package me.ziyframework.web.security.exchange;

import com.github.benmanes.caffeine.cache.Cache;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.spec.NamedParameterSpec;
import javax.crypto.KeyAgreement;

/**
 * x25519密钥交换实现.
 * created in 2025-05
 *
 * @author ziy
 */
public class X25519SecretExchange extends SecretExchange {

    public X25519SecretExchange(Cache<String, byte[]> cache, long seconds) {
        super(cache, seconds);
    }

    /**
     * 生成密钥对生成器.
     */
    @Override
    protected KeyPairGenerator getKeyPairGenerator() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("X25519", "BC");
        keyPairGenerator.initialize(NamedParameterSpec.X25519);
        return keyPairGenerator;
    }

    /**
     * 生成密钥工厂.
     */
    @Override
    protected KeyFactory getKeyFactory() throws Exception {
        return KeyFactory.getInstance("X25519", "BC");
    }

    /**
     * 生成密钥交换算法.
     */
    @Override
    protected KeyAgreement getKeyAgreement() throws Exception {
        return KeyAgreement.getInstance("X25519", "BC");
    }
}

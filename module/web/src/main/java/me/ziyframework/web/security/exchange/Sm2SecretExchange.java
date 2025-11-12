package me.ziyframework.web.security.exchange;

import com.github.benmanes.caffeine.cache.Cache;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import javax.crypto.KeyAgreement;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

/**
 * .
 * created in 2025-05
 *
 * @author ziy
 */
public class Sm2SecretExchange extends SecretExchange {

    public Sm2SecretExchange(Cache<String, byte[]> cache, long seconds) {
        super(cache, seconds);
    }

    /**
     * 生成sm2p256v1曲线的密钥对生成器.
     */
    @Override
    protected KeyPairGenerator getKeyPairGenerator() throws Exception {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
        keyPairGenerator.initialize(ecSpec);
        return keyPairGenerator;
    }

    /**
     * 生成ECDH的密钥工厂.
     */
    @Override
    protected KeyFactory getKeyFactory() throws Exception {
        return KeyFactory.getInstance("ECDH", "BC");
    }

    /**
     * 生成ECDH密钥交换算法.
     */
    @Override
    protected KeyAgreement getKeyAgreement() throws Exception {
        return KeyAgreement.getInstance("ECDH", "BC");
    }
}

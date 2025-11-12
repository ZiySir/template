package me.ziyframework.web.security.exchange;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.io.BaseEncoding;
import com.palantir.logsafe.Preconditions;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.KeyAgreement;
import me.ziyframework.core.tuple.Tuple2;
import me.ziyframework.core.tuple.Tuples;
import me.ziyframework.web.common.exception.GlobalException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

/**
 * 密钥交换.<br />
 * created on 2025-03
 *
 * @author ziy
 */
public abstract class SecretExchange implements DisposableBean {

    private final KeyPairGenerator keyPairGenerator;

    private final KeyFactory keyFactory;

    /**
     * 缓存客户端共享密钥协商结果(提高高频加解密接口性能).
     */
    private final Cache<String, byte[]> cache;

    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "schedule-update-server-keyPair-thread");
                thread.setDaemon(true);
                return thread;
            });

    /**
     * 用于临时缓存切换服务端密钥对,从而允许密钥对更新后对旧密钥对的支持.
     * 在下一次密钥对更新时会清理旧密钥对
     */
    private final Cache<String, KeyPair> keyIdCache =
            Caffeine.newBuilder().maximumSize(2).build();

    private final AtomicReference<@Nullable String> newKeyIdRef = new AtomicReference<>();

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 创建一个服务端密钥对管理对象.
     *
     * @param cache 缓存共享密钥
     * @param seconds 定时更换服务端密钥对的时间（秒），每次更换都将清理共享密钥的缓存
     */
    public SecretExchange(Cache<String, byte[]> cache, long seconds) {
        try {
            this.keyPairGenerator = getKeyPairGenerator();
            this.keyFactory = getKeyFactory();
        } catch (Exception e) {
            throw GlobalException.wrap(e);
        }
        this.cache = cache;
        updateKeyPair();
        // 启动定时线程.
        Future<?> unusedFuture = scheduledExecutorService.scheduleWithFixedDelay(
                this::updateKeyPair, seconds, seconds, TimeUnit.SECONDS);
    }

    /**
     * 清理资源.
     */
    @Override
    public void destroy() {
        scheduledExecutorService.shutdownNow();
    }

    /**
     * 获取公钥base64编码.
     *
     * @return _1 keyId  _2 公钥base64编码
     */
    public Tuple2<String, String> getPublicBase64() {
        String keyId = newKeyIdRef.get();
        if (!StringUtils.hasText(keyId)) {
            throw new GlobalException("server key is null");
        }
        KeyPair keyPair = keyIdCache.getIfPresent(keyId);
        if (keyPair == null) {
            throw new GlobalException("server key is null");
        }
        String publicKeyBase64 =
                BaseEncoding.base64Url().encode(keyPair.getPublic().getEncoded());
        return Tuples.of(keyId, publicKeyBase64);
    }

    /**
     * 获取共享密钥,优先返回缓存的共享密钥.
     *
     * @param remotePublicKeyBase64 远程公钥base64编码
     * @param serverKeyId 服务端密钥id（主要用于支持密钥对更新）
     * @return 共享密钥
     */
    public byte[] getSharedSecret(String remotePublicKeyBase64, String serverKeyId) {
        if (StringUtil.isBlank(remotePublicKeyBase64)) {
            throw new GlobalException("publicKey is blank");
        }
        byte[] serverKeyBytes = cache.get(serverKeyId + ":" + remotePublicKeyBase64, key -> {
            KeyPair serverKeyPair = keyIdCache.getIfPresent(serverKeyId);
            if (serverKeyPair == null) {
                throw new GlobalException("server key is null");
            }
            return generateSharedSecret(remotePublicKeyBase64, serverKeyPair.getPrivate());
        });
        return Preconditions.checkNotNull(serverKeyBytes, "server key is null");
    }

    /**
     * 通过客户端的公钥计算出共享密钥(密钥协商).
     *
     * @param remotePublicKeyBase64 远程公钥base64编码
     */
    public byte[] generateSharedSecret(String remotePublicKeyBase64, PrivateKey serverPrivetKey) {
        // 解析base64编码的公钥
        byte[] remotePublicKeyBytes = BaseEncoding.base64Url().decode(remotePublicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(remotePublicKeyBytes);
        KeyAgreement agree;
        try {
            agree = getKeyAgreement();
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            // 计算共享公钥
            agree.init(serverPrivetKey);
            agree.doPhase(publicKey, true);
        } catch (Exception e) {
            throw GlobalException.wrap(e);
        }
        return agree.generateSecret();
    }

    /**
     * 创建服务端密钥对的生成器.
     */
    protected abstract KeyPairGenerator getKeyPairGenerator() throws Exception;

    /**
     * 获取一个密钥工厂.
     */
    protected abstract KeyFactory getKeyFactory() throws Exception;

    /**
     * 获取一个完成密钥协商的对象.
     */
    protected abstract KeyAgreement getKeyAgreement() throws Exception;

    /**
     * 更新密钥对.
     */
    private void updateKeyPair() {
        KeyPair newKeyPair = keyPairGenerator.generateKeyPair();
        cache.invalidateAll();
        final String keyId = String.valueOf(System.currentTimeMillis());
        keyIdCache.put(keyId, newKeyPair);
        // 记录当前最新的密钥对版本
        newKeyIdRef.set(keyId);
    }
}

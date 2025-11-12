package me.ziyframework.web.sse;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.palantir.logsafe.SafeArg;
import java.util.Collection;
import java.util.Optional;
import lombok.CustomLog;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * SSE会话管理器.
 * created in 2025-07
 *
 * @author ziy
 */
@CustomLog
public class SseManager {

    /**
     * SSE会话池.
     */
    private final Cache<String, SseEmitterWrapper> ssePool;

    public SseManager() {
        this.ssePool = Caffeine.newBuilder().build();
    }

    public SseManager(int maxSession) {
        this.ssePool = Caffeine.newBuilder().maximumSize(maxSession).build();
    }

    /**
     * 发送数据给指定客户端.
     *
     * @param clientIds 客户端ID
     * @param data 数据
     */
    public void send(Collection<String> clientIds, Object data) {
        clientIds.stream().distinct().forEach(clientId -> {
            getIfPresent(clientId).ifPresent(sseEmitter -> sseEmitter.send(data));
        });
    }

    /**
     * 发送数据给指定客户端.
     *
     * @param clientIds 客户端ID
     * @param eventBuilder 数据
     */
    public void send(Collection<String> clientIds, SseEventBuilder eventBuilder) {
        clientIds.stream().distinct().forEach(clientId -> {
            getIfPresent(clientId).ifPresent(sseEmitter -> sseEmitter.send(eventBuilder));
        });
    }

    /**
     * 发送数据给所有客户端.
     *
     * @param data 数据
     */
    public void sendAll(Object data) {
        ssePool.asMap().values().forEach(sseEmitter -> sseEmitter.send(data));
    }

    /**
     * 批量发送数据给所有客户端.
     *
     * @param eventBuilder 数据
     */
    public void sendAll(SseEventBuilder eventBuilder) {
        ssePool.asMap().values().forEach(sseEmitter -> sseEmitter.send(eventBuilder));
    }

    /**
     * 获取指定客户端的SSE会话.
     *
     * @param clientId 客户端id
     * @return SSE会话
     */
    public Optional<SseEmitterWrapper> getIfPresent(String clientId) {
        return Optional.ofNullable(ssePool.getIfPresent(clientId));
    }

    /**
     * 删除SSE会话.
     *
     * @param clientId 客户端ID
     */
    public void unRegister(String clientId) {
        Assert.hasText(clientId, "clientId can not be null");
        SseEmitterWrapper sseEmitter = ssePool.getIfPresent(clientId);
        if (sseEmitter != null) {
            try {
                sseEmitter.complete();
            } finally {
                ssePool.invalidate(clientId);
            }
        }
    }

    /**
     * 获取SSE会话,采用默认超时时间.
     *
     * @param clientId 客户端ID
     * @return SSE会话
     */
    public SseEmitterWrapper register(String clientId) {
        Assert.hasText(clientId, "clientId can not be null");
        return ssePool.get(clientId, cid -> wrap(new SseEmitter(), clientId));
    }

    /**
     * 获取SSE会话.
     *
     * @param clientId 客户端ID
     * @param timeout sse超时时间(单位：毫秒). 默认采用服务端配置.
     * @return SSE会话
     */
    public SseEmitterWrapper register(String clientId, long timeout) {
        Assert.hasText(clientId, "clientId can not be null");
        return ssePool.get(clientId, cid -> wrap(new SseEmitter(timeout), clientId));
    }

    private SseEmitterWrapper wrap(SseEmitter sseEmitter, String clientId) {
        sseEmitter.onError(throwable -> {
            log.error("sse session error, clientId:<{}>", SafeArg.of("clientId", clientId));
        });
        sseEmitter.onCompletion(() -> {
            log.debug("sse session completion, clientId:<{}>", SafeArg.of("clientId", clientId));
            // sse不再可用
            ssePool.invalidate(clientId);
        });
        sseEmitter.onTimeout(() -> {
            log.debug("sse session timeout, clientId:<{}>", SafeArg.of("clientId", clientId));
            sseEmitter.complete();
        });
        return new SseEmitterWrapper(clientId, sseEmitter);
    }
}

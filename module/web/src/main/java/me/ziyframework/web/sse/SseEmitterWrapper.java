package me.ziyframework.web.sse;

import com.palantir.logsafe.SafeArg;
import java.io.IOException;
import java.util.Set;
import lombok.CustomLog;
import me.ziyframework.web.common.exception.GlobalException;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

/**
 * 对SseEmitter包装类.
 * created in 2025-07
 *
 * @author ziy
 */
@CustomLog
public class SseEmitterWrapper {

    private static final Set<DataWithMediaType> PING_EVENT =
            SseEmitter.event().name("ping").build();

    private final String clientId;

    private final SseEmitter sseEmitter;

    public SseEmitterWrapper(String clientId, SseEmitter sseEmitter) {
        Assert.hasText(clientId, "clientId can not be null");
        Assert.notNull(sseEmitter, "sseEmitter can not be null");
        this.clientId = clientId;
        this.sseEmitter = sseEmitter;
    }

    /**
     * 获取SseEmitter实例.
     *
     * @return SseEmitter实例
     */
    public SseEmitter getNativeSseEmitter() {
        return sseEmitter;
    }

    /**
     * 发送数据.
     */
    public void send(Object data) {
        send(SseEmitter.event().data(data));
    }

    /**
     * 发送事件.
     */
    public void send(SseEventBuilder eventBuilder) {
        try {
            sseEmitter.send(eventBuilder);
        } catch (IOException ex) {
            log.error("send message failed, clientId:<{}>", SafeArg.of("clientId", clientId), ex);
            throw GlobalException.wrap(ex);
        }
    }

    /**
     * 发送ping事件.
     */
    public void ping() {
        try {
            sseEmitter.send(PING_EVENT);
        } catch (IOException e) {
            log.error("send ping failed, clientId:<{}>", SafeArg.of("clientId", clientId), e);
            throw GlobalException.wrap(e);
        }
    }

    /**
     * 完成.
     */
    public void complete() {
        sseEmitter.complete();
    }
}

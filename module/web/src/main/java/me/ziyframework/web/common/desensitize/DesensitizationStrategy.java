package me.ziyframework.web.common.desensitize;

/**
 * 脱敏策略.<br />
 * created on 2025-02
 * @author ziy
 */
@FunctionalInterface
public interface DesensitizationStrategy<T, R> {

    /**
     * 脱敏.
     */
    R desensitize(T value);
}

package me.ziyframework.web.common.desensitize;

import lombok.Getter;

/**
 * 内置.<br />
 * created on 2025-02
 * @author ziy
 */
@Getter
public enum DesensitizationEnum implements DesensitizationStrategy<Object, Object> {
    /**
     * 中国大陆11位手机号码的脱敏.
     */
    CHINESE_PHONE {
        @Override
        public Object desensitize(Object value) {
            if (value instanceof String chinesePhone) {
                if (chinesePhone.length() != 11) {
                    return chinesePhone;
                }
                return chinesePhone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
            return value;
        }
    },

    /**
     * 邮箱脱敏.
     */
    EMAIL {
        @Override
        public Object desensitize(Object value) {
            if (value instanceof String email) {
                if (email.isBlank()) {
                    return email;
                }
                int index = email.indexOf('@');
                if (index <= 1) {
                    return email;
                }
                return email.charAt(0) + email.substring(index);
            }
            return value;
        }
    },

    /**
     * 固定返回6个*.
     */
    SIX_MASK {
        @Override
        public Object desensitize(Object value) {
            return "******";
        }
    };

    DesensitizationEnum() {}

    @Override
    public Object desensitize(Object value) {
        throw new UnsupportedOperationException();
    }
}

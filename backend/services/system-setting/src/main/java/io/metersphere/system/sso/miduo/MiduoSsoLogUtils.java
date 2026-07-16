package io.metersphere.system.sso.miduo;

import org.apache.commons.lang3.StringUtils;

/**
 * SSO 日志脱敏工具。
 */
public final class MiduoSsoLogUtils {

    private MiduoSsoLogUtils() {
    }

    public static String maskToken(String token) {
        if (StringUtils.isBlank(token)) {
            return "";
        }
        if (token.length() <= 6) {
            return "***";
        }
        return token.substring(0, 6) + "***";
    }

    public static String maskTail(String value, int keep) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        if (value.length() <= keep) {
            return "***";
        }
        return "***" + value.substring(value.length() - keep);
    }
}

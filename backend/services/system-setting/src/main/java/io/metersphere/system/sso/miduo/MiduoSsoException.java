package io.metersphere.system.sso.miduo;

import io.metersphere.sdk.exception.MSException;

/**
 * 米多 SSO 业务异常（message 不得包含 token / secret 全量）。
 */
public class MiduoSsoException extends MSException {
    public MiduoSsoException(String message) {
        super(message);
    }
}

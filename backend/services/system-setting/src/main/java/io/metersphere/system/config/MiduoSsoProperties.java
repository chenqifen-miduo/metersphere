package io.metersphere.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 米多星球第三方 SSO 配置（环境变量注入，Secret 禁止入库）。
 */
@ConfigurationProperties(prefix = MiduoSsoProperties.PREFIX)
@Getter
@Setter
public class MiduoSsoProperties {
    public static final String PREFIX = "miduo.sso";

    /** 是否启用米多 SSO */
    private boolean enabled = false;
    /** 米多 API 根地址，如 https://xxx */
    private String baseUrl = "";
    private String appCode = "";
    private String appSecret = "";
    /** 与米多白名单字符级一致的 redirectUri */
    private String redirectUri = "";
    /** 工作台快捷入口 ID（可选） */
    private String shortcutId = "";
    /** 状态检查所用组织 ID（企微同步就绪判定） */
    private String organizationId = "";
    /** state TTL（秒） */
    private long stateTtlSeconds = 600;
    /** sessionToken Redis TTL（秒） */
    private long sessionTtlSeconds = 86400;
    /** 时钟偏差容忍（毫秒） */
    private long clockSkewMs = 300_000;
    /** refresh 提前量（秒）：距过期少于此值则 refresh */
    private long refreshAheadSeconds = 1800;

    public boolean isConfigured() {
        return enabled
                && baseUrl != null && !baseUrl.isBlank()
                && appCode != null && !appCode.isBlank()
                && appSecret != null && !appSecret.isBlank()
                && redirectUri != null && !redirectUri.isBlank();
    }
}

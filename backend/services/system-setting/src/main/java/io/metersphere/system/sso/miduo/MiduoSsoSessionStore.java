package io.metersphere.system.sso.miduo;

import io.metersphere.sdk.util.JSON;
import io.metersphere.system.config.MiduoSsoProperties;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 米多 sessionToken 服务端存储（仅 Redis）。
 */
@Service
public class MiduoSsoSessionStore {

    private static final String KEY_PREFIX = "miduo:sso:session:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MiduoSsoProperties properties;

    public void save(String userId, String sessionToken, Long expiresAt) {
        if (StringUtils.isAnyBlank(userId, sessionToken)) {
            return;
        }
        Map<String, Object> value = new HashMap<>();
        value.put("sessionToken", sessionToken);
        value.put("expiresAt", expiresAt);
        value.put("needReauth", false);
        long ttl = resolveTtlSeconds(expiresAt);
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + userId, JSON.toJSONString(value), Duration.ofSeconds(ttl));
    }

    public String getSessionToken(String userId) {
        Map<String, Object> data = load(userId);
        if (data == null) {
            return null;
        }
        Object token = data.get("sessionToken");
        return token == null ? null : String.valueOf(token);
    }

    public Long getExpiresAt(String userId) {
        Map<String, Object> data = load(userId);
        if (data == null || data.get("expiresAt") == null) {
            return null;
        }
        Object v = data.get("expiresAt");
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isNeedReauth(String userId) {
        Map<String, Object> data = load(userId);
        if (data == null) {
            return false;
        }
        Object flag = data.get("needReauth");
        return Boolean.TRUE.equals(flag) || "true".equalsIgnoreCase(String.valueOf(flag));
    }

    public void markNeedReauth(String userId) {
        Map<String, Object> data = load(userId);
        if (data == null) {
            data = new HashMap<>();
        }
        data.put("needReauth", true);
        long ttl = Math.max(60, properties.getSessionTtlSeconds());
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + userId, JSON.toJSONString(data), Duration.ofSeconds(ttl));
    }

    public void delete(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        stringRedisTemplate.delete(KEY_PREFIX + userId);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> load(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + userId);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, Map.class);
    }

    private long resolveTtlSeconds(Long expiresAt) {
        long defaultTtl = Math.max(60, properties.getSessionTtlSeconds());
        if (expiresAt == null || expiresAt <= 0) {
            return defaultTtl;
        }
        long remain = (expiresAt - System.currentTimeMillis()) / 1000;
        if (remain <= 0) {
            return 60;
        }
        return Math.min(defaultTtl, remain);
    }
}

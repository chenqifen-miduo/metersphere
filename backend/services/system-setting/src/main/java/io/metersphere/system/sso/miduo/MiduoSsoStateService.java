package io.metersphere.system.sso.miduo;

import io.metersphere.system.config.MiduoSsoProperties;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

/**
 * state 生成与一次性消费（防 CSRF）。
 */
@Service
public class MiduoSsoStateService {

    private static final String KEY_PREFIX = "miduo:sso:state:";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MiduoSsoProperties properties;

    public String generateState() {
        byte[] buf = new byte[32];
        RANDOM.nextBytes(buf);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
        long ttl = Math.max(60, properties.getStateTtlSeconds());
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + state, "1", Duration.ofSeconds(ttl));
        return state;
    }

    public void consumeState(String state) {
        if (StringUtils.isBlank(state)) {
            throw new MiduoSsoException("state 不能为空");
        }
        String key = KEY_PREFIX + state;
        Boolean deleted = stringRedisTemplate.delete(key);
        if (!Boolean.TRUE.equals(deleted)) {
            throw new MiduoSsoException("state 无效或已过期，请从米多重新进入");
        }
    }
}

package io.metersphere.system.sso.miduo;

import io.metersphere.sdk.util.JSON;
import io.metersphere.system.config.MiduoSsoProperties;
import io.metersphere.system.dto.sso.miduo.MiduoRefreshResult;
import io.metersphere.system.dto.sso.miduo.MiduoValidateResult;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 米多开放 API 签名客户端（validate / refresh / revoke / bridge）。
 */
@Service
public class MiduoSsoClient {

    private static final Logger log = LoggerFactory.getLogger(MiduoSsoClient.class);
    private static final String HMAC_ALG = "HmacSHA256";
    private static final String NONCE_KEY_PREFIX = "miduo:sso:nonce:";

    @Resource
    private MiduoSsoProperties properties;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private RestTemplate restTemplate;

    @PostConstruct
    void init() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setConnectionRequestTimeout(5000);
        factory.setReadTimeout(15000);
        restTemplate = new RestTemplate(factory);
    }

    public MiduoValidateResult validateLoginToken(String exchangeToken) {
        ensureEnabled();
        if (StringUtils.isBlank(exchangeToken)) {
            throw new MiduoSsoException("exchange token 不能为空");
        }
        Map<String, Object> body = Map.of("token", exchangeToken);
        Map<String, Object> resp = postSigned("/api/open/sso/validate-login-token", exchangeToken, body);
        Map<String, Object> data = asMap(resp.get("return_data"));
        MiduoValidateResult result = new MiduoValidateResult();
        result.setValid(Boolean.TRUE.equals(asBoolean(data.get("valid"))));
        if (!result.isValid()) {
            log.warn("miduo validate failed, token={}", MiduoSsoLogUtils.maskToken(exchangeToken));
            throw new MiduoSsoException("米多登录校验失败");
        }
        result.setSessionToken(asString(firstNonBlank(data, "sessionToken", "session_token")));
        result.setWeworkUserid(asString(firstNonBlank(data, "wework_userid", "weworkUserid", "userid")));
        result.setExpiresAt(asLong(firstNonBlank(data, "expiresAt", "expires_at", "expireAt")));
        if (StringUtils.isBlank(result.getSessionToken()) || StringUtils.isBlank(result.getWeworkUserid())) {
            throw new MiduoSsoException("米多校验响应缺少 sessionToken 或 wework_userid");
        }
        return result;
    }

    public MiduoRefreshResult refreshSessionToken(String sessionToken) {
        ensureEnabled();
        if (StringUtils.isBlank(sessionToken)) {
            throw new MiduoSsoException("sessionToken 不能为空");
        }
        Map<String, Object> body = Map.of("sessionToken", sessionToken);
        Map<String, Object> resp = postSigned("/api/open/sso/refresh-session-token", sessionToken, body);
        Map<String, Object> data = asMap(resp.get("return_data"));
        MiduoRefreshResult result = new MiduoRefreshResult();
        result.setSuccess(Boolean.TRUE.equals(asBoolean(data.get("success"))));
        if (!result.isSuccess()) {
            throw new MiduoSsoException("米多 session 刷新失败");
        }
        result.setSessionToken(StringUtils.defaultIfBlank(
                asString(firstNonBlank(data, "sessionToken", "session_token")), sessionToken));
        result.setExpiresAt(asLong(firstNonBlank(data, "expiresAt", "expires_at", "expireAt")));
        return result;
    }

    public void revokeSessionToken(String sessionToken) {
        ensureEnabled();
        if (StringUtils.isBlank(sessionToken)) {
            return;
        }
        Map<String, Object> body = Map.of("sessionToken", sessionToken);
        Map<String, Object> resp = postSigned("/api/open/sso/revoke-session-token", sessionToken, body);
        Map<String, Object> data = asMap(resp.get("return_data"));
        if (!Boolean.TRUE.equals(asBoolean(data.get("success")))) {
            log.warn("miduo revoke not success, token={}", MiduoSsoLogUtils.maskToken(sessionToken));
        }
    }

    public String buildBridgeUrl() {
        return buildBridgeUrl(null);
    }

    public String buildBridgeUrl(String state) {
        ensureEnabled();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(trimSlash(properties.getBaseUrl()) + "/api/sso/bridge/redirect-url")
                .queryParam("redirectUri", properties.getRedirectUri())
                .queryParam("appCode", properties.getAppCode());
        if (StringUtils.isNotBlank(properties.getShortcutId())) {
            builder.queryParam("shortcutId", properties.getShortcutId());
        }
        if (StringUtils.isNotBlank(state)) {
            builder.queryParam("state", state);
        }
        return builder.build(true).toUriString();
    }

    String sign(String signedValue, String timestamp, String nonce) {
        try {
            String canonical = properties.getAppCode() + "\n" + timestamp + "\n" + nonce + "\n" + signedValue;
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(properties.getAppSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALG));
            byte[] raw = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new MiduoSsoException("HMAC 签名失败");
        }
    }

    private Map<String, Object> postSigned(String path, String signedValue, Map<String, Object> body) {
        String nonce = UUID.randomUUID().toString().replace("-", "");
        rememberNonce(nonce);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = sign(signedValue, timestamp, nonce);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-App-Code", properties.getAppCode());
        headers.set("X-App-Timestamp", timestamp);
        headers.set("X-App-Nonce", nonce);
        headers.set("X-App-Signature", signature);

        String url = trimSlash(properties.getBaseUrl()) + path;
        try {
            ResponseEntity<String> entity = restTemplate.postForEntity(
                    url, new HttpEntity<>(JSON.toJSONString(body), headers), String.class);
            if (!entity.getStatusCode().is2xxSuccessful() || StringUtils.isBlank(entity.getBody())) {
                throw new MiduoSsoException("米多开放 API 调用失败");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = JSON.parseObject(entity.getBody(), Map.class);
            return resp != null ? resp : Map.of();
        } catch (MiduoSsoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("miduo open api error path={} msg={}", path, e.getMessage());
            throw new MiduoSsoException("米多开放 API 网络异常");
        }
    }

    private void rememberNonce(String nonce) {
        Boolean ok = stringRedisTemplate.opsForValue()
                .setIfAbsent(NONCE_KEY_PREFIX + nonce, "1", Duration.ofSeconds(300));
        if (Boolean.FALSE.equals(ok)) {
            throw new MiduoSsoException("nonce 重复，拒绝请求");
        }
    }

    private void ensureEnabled() {
        if (!properties.isConfigured()) {
            throw new MiduoSsoException("米多 SSO 未启用或配置不完整");
        }
    }

    private static String trimSlash(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new HashMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        }
        return Map.of();
    }

    private static Object firstNonBlank(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object v = data.get(key);
            if (v != null && StringUtils.isNotBlank(String.valueOf(v))) {
                return v;
            }
        }
        return null;
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Boolean asBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

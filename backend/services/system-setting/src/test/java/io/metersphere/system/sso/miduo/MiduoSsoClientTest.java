package io.metersphere.system.sso.miduo;

import io.metersphere.system.config.MiduoSsoProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 【AI辅助生成】签名单测；边界与联调用例需人工补充。
 */
class MiduoSsoClientTest {

    private MiduoSsoClient client;
    private MiduoSsoProperties properties;

    @BeforeEach
    void setUp() {
        client = new MiduoSsoClient();
        properties = new MiduoSsoProperties();
        properties.setEnabled(true);
        properties.setBaseUrl("https://miduo.example.com");
        properties.setAppCode("ms-app");
        properties.setAppSecret("test-secret");
        properties.setRedirectUri("https://ms.example.com/#/sso/miduo/callback");
        ReflectionTestUtils.setField(client, "properties", properties);
    }

    @Test
    void signMatchesHmacSha256Base64() throws Exception {
        String timestamp = "1710000000000";
        String nonce = "abc123";
        String signedValue = "exch_token";
        String expected = hmac(properties.getAppCode(), timestamp, nonce, signedValue, properties.getAppSecret());
        String actual = client.sign(signedValue, timestamp, nonce);
        Assertions.assertEquals(expected, actual);
    }

    private static String hmac(String appCode, String timestamp, String nonce, String signedValue, String secret)
            throws Exception {
        String canonical = appCode + "\n" + timestamp + "\n" + nonce + "\n" + signedValue;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8)));
    }
}

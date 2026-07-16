package io.metersphere.system.config;

import io.metersphere.sdk.util.ShiroFilterChainExtender;
import jakarta.servlet.Filter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 注册米多 SSO 匿名访问链（参考 AgentShiroFilterChainExtender）。
 */
@Component
public class MiduoSsoShiroFilterChainExtender implements ShiroFilterChainExtender {

    @Override
    public void extend(Map<String, Filter> filters, Map<String, String> chain) {
        chain.put("/auth/miduo/**", "anon");
    }
}

package io.metersphere.agent.config;

import io.metersphere.agent.security.AgentTokenFilter;
import io.metersphere.sdk.util.ShiroFilterChainExtender;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AgentShiroFilterChainExtender implements ShiroFilterChainExtender {
    @Resource
    private AgentTokenFilter agentTokenFilter;

    @Override
    public void extend(Map<String, Filter> filters, Map<String, String> chain) {
        filters.put("agentToken", agentTokenFilter);
        chain.put("/api/agent/v1/functional/health", "anon");
        chain.put("/api/agent/v1/**", "agentToken, authc");
        chain.put("/api/agent/token/**", "authc");
    }
}

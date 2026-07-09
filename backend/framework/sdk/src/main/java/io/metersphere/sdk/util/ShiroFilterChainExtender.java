package io.metersphere.sdk.util;

import jakarta.servlet.Filter;
import java.util.Map;

/**
 * Allows feature modules to register custom Shiro filters and high-priority routes.
 */
public interface ShiroFilterChainExtender {
    /**
     * Register custom filters and prepend route definitions (matched before base routes).
     */
    void extend(Map<String, Filter> filters, Map<String, String> chain);
}

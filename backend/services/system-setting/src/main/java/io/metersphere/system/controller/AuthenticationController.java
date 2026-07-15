package io.metersphere.system.controller;

import io.metersphere.system.dto.AuthSourceDTO;
import io.metersphere.system.service.AuthSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authentication")
@Tag(name = "登录-认证方式")
public class AuthenticationController {

    @Resource
    private AuthSourceService authSourceService;

    @GetMapping("/get-list")
    @Operation(summary = "获取已启用的登录认证方式")
    public List<String> getList() {
        return authSourceService.listEnabledAuthTypes();
    }

    @GetMapping("/get/by/type/{type}")
    @Operation(summary = "按类型获取认证源详情")
    public AuthSourceDTO getByType(@PathVariable String type) {
        return authSourceService.getByType(type);
    }
}

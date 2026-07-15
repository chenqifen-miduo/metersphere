package io.metersphere.system.controller;

import io.metersphere.system.dto.DingTalkInfoDTO;
import io.metersphere.system.dto.LarkInfoDTO;
import io.metersphere.system.dto.WeComInfoDTO;
import io.metersphere.system.service.PlatformSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录页扫码配置与 SSO 回调入口（匿名可访问）
 */
@RestController
@Tag(name = "登录-扫码SSO")
public class SsoLoginController {

    @Resource
    private PlatformSourceService platformSourceService;

    @GetMapping("/we_com/info")
    @Operation(summary = "登录页-企业微信扫码参数")
    public WeComInfoDTO weComInfo() {
        return platformSourceService.getWeComLoginInfo();
    }

    @GetMapping("/ding_talk/info")
    @Operation(summary = "登录页-钉钉扫码参数")
    public DingTalkInfoDTO dingTalkInfo() {
        return platformSourceService.getDingTalkLoginInfo();
    }

    @GetMapping("/lark/info")
    @Operation(summary = "登录页-飞书扫码参数")
    public LarkInfoDTO larkInfo() {
        return platformSourceService.getLarkLoginInfo(PlatformSourceService.LARK);
    }

    @GetMapping("/lark_suite/info")
    @Operation(summary = "登录页-国际飞书扫码参数")
    public LarkInfoDTO larkSuiteInfo() {
        return platformSourceService.getLarkLoginInfo(PlatformSourceService.LARK_SUITE);
    }

    @GetMapping("/sso/callback/we_com")
    @Operation(summary = "企业微信 SSO 回调")
    public void weComCallback(@RequestParam(required = false) String code) {
        platformSourceService.rejectSsoCallback(PlatformSourceService.WE_COM);
    }

    @GetMapping("/sso/callback/ding_talk")
    @Operation(summary = "钉钉 SSO 回调")
    public void dingTalkCallback(@RequestParam(required = false) String code) {
        platformSourceService.rejectSsoCallback(PlatformSourceService.DING_TALK);
    }

    @GetMapping("/sso/callback/lark")
    @Operation(summary = "飞书 SSO 回调")
    public void larkCallback(@RequestParam(required = false) String code) {
        platformSourceService.rejectSsoCallback(PlatformSourceService.LARK);
    }

    @GetMapping("/sso/callback/lark_suite")
    @Operation(summary = "国际飞书 SSO 回调")
    public void larkSuiteCallback(@RequestParam(required = false) String code) {
        platformSourceService.rejectSsoCallback(PlatformSourceService.LARK_SUITE);
    }
}

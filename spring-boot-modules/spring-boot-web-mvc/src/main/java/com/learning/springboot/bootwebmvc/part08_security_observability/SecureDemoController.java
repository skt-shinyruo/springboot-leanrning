package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本类用于演示安全分支：401（未登录）、403（权限不足/CSRF）、以及认证信息如何进入 handler。

import java.security.Principal;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/secure")
public class SecureDemoController {

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping(Principal principal) {
        return Map.of(
                "message", "pong",
                "user", principal == null ? "anonymous" : principal.getName()
        );
    }

    @GetMapping(value = "/admin/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> adminPing(Principal principal) {
        return Map.of(
                "message", "pong-admin",
                "user", principal == null ? "anonymous" : principal.getName()
        );
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateSomething(Principal principal) {
        return Map.of(
                "message", "updated",
                "user", principal == null ? "anonymous" : principal.getName()
        );
    }
}


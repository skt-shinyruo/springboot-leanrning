package com.learning.springboot.bootsecurity.part01_security;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure")
class SecureController {

    @GetMapping("/ping")
    Map<String, Object> ping(Authentication authentication) {
        return Map.of(
                "message", "pong",
                "user", authentication.getName()
        );
    }

    @GetMapping("/me")
    Map<String, Object> me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        return Map.of(
                "username", authentication.getName(),
                "roles", roles
        );
    }

    @PostMapping("/change-email")
    MessageResponse changeEmail(@RequestBody ChangeEmailRequest request) {
        return new MessageResponse("email_changed:" + request.email());
    }
}

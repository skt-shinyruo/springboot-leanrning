package com.learning.springboot.bootsecurity.part01_security;

import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jwt/secure")
class JwtSecureController {

    @GetMapping("/ping")
    Map<String, Object> ping(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "pong_jwt_secure",
                "subject", jwt.getSubject()
        );
    }

    @PostMapping("/change-email")
    MessageResponse changeEmail(@RequestBody ChangeEmailRequest request) {
        return new MessageResponse("email_changed_jwt:" + request.email());
    }
}


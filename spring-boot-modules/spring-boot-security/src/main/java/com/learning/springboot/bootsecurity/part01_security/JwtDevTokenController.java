package com.learning.springboot.bootsecurity.part01_security;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/jwt/dev")
class JwtDevTokenController {

    private final JwtTokenService tokenService;

    JwtDevTokenController(JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token")
    Map<String, Object> token(
            @RequestParam(defaultValue = "alice") String subject,
            @RequestParam(defaultValue = "read") String scope
    ) {
        List<String> scopes = List.of(scope.split("\\s+"));
        return Map.of(
                "tokenType", "Bearer",
                "token", tokenService.issueToken(subject, scopes)
        );
    }
}


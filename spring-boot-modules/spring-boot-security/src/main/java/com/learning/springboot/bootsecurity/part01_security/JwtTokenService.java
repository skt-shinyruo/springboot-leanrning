package com.learning.springboot.bootsecurity.part01_security;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
class JwtTokenService {

    private final JwtEncoder jwtEncoder;

    JwtTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    String issueToken(String subject, Collection<String> scopes) {
        Instant now = Instant.now();
        String scopeValue = String.join(" ", scopes);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("springboot-security")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(subject)
                .claim("scope", scopeValue)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    String issueToken(String subject, String... scopes) {
        return issueToken(subject, List.of(scopes));
    }
}

package com.learning.springboot.bootsecurity.part01_security;

/**
 * 默认 Lab：用可断言证据链证明多条 SecurityFilterChain 的 matcher 与 @Order 分流结果（避免靠“猜行为”排障）。
 */

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.Filter;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@SpringBootTest
class BootSecurityMultiFilterChainOrderLabTest {

    @Autowired
    private FilterChainProxy filterChainProxy;

    @Test
    void jwtPathMatchesJwtChain_andApiPathMatchesBasicChain() {
        List<Filter> jwtFilters = filtersFor(buildGet("/api/jwt/secure/ping"));
        assertThat(jwtFilters)
                .as("JWT chain should include BearerTokenAuthenticationFilter")
                .anyMatch(filter -> filter instanceof BearerTokenAuthenticationFilter);
        assertThat(jwtFilters)
                .as("JWT chain should not include BasicAuthenticationFilter")
                .noneMatch(filter -> filter instanceof BasicAuthenticationFilter);

        List<Filter> apiFilters = filtersFor(buildGet("/api/secure/ping"));
        assertThat(apiFilters)
                .as("Basic chain should include BasicAuthenticationFilter")
                .anyMatch(filter -> filter instanceof BasicAuthenticationFilter);
        assertThat(apiFilters)
                .as("Basic chain should not include BearerTokenAuthenticationFilter")
                .noneMatch(filter -> filter instanceof BearerTokenAuthenticationFilter);
    }

    @Test
    void traceIdFilterIsPresentInBothChains_asCrossCuttingConcern() {
        List<Filter> jwtFilters = filtersFor(buildGet("/api/jwt/secure/ping"));
        assertThat(jwtFilters)
                .as("TraceIdFilter should be installed in JWT chain")
                .anyMatch(filter -> filter instanceof TraceIdFilter);

        List<Filter> apiFilters = filtersFor(buildGet("/api/secure/ping"));
        assertThat(apiFilters)
                .as("TraceIdFilter should be installed in Basic chain")
                .anyMatch(filter -> filter instanceof TraceIdFilter);
    }

    private List<Filter> filtersFor(MockHttpServletRequest request) {
        SecurityFilterChain chain = filterChainProxy.getFilterChains().stream()
                .filter(c -> c.matches(request))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No SecurityFilterChain matched request: " + request.getRequestURI()));
        return chain.getFilters();
    }

    private static MockHttpServletRequest buildGet(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setRequestURI(path);
        request.setServletPath(path);
        return request;
    }
}

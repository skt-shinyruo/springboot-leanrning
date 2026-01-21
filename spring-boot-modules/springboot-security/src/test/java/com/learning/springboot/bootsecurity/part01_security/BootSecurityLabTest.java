package com.learning.springboot.bootsecurity.part01_security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BootSecurityLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AdminOnlyService adminOnlyService;

    @Autowired
    private SelfInvocationPitfallService selfInvocationPitfallService;

    @Test
    void publicEndpointIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/public/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"));
    }

    @Test
    void secureEndpointReturns401WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/secure/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("unauthorized"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/secure/ping"));
    }

    @Test
    void secureEndpointIsAccessibleWithBasicAuth() throws Exception {
        mockMvc.perform(get("/api/secure/ping").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.user").value("user"));
    }

    @Test
    void adminEndpointReturns403ForNonAdminUser() throws Exception {
        mockMvc.perform(get("/api/admin/ping").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.path").value("/api/admin/ping"));
    }

    @Test
    void adminEndpointIsAccessibleForAdminUser() throws Exception {
        mockMvc.perform(get("/api/admin/ping").with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong_admin"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminEndpointReturns403WhenAuthorityAdminButMissingRolePrefix_asPitfall() throws Exception {
        mockMvc.perform(get("/api/admin/ping"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("forbidden"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.path").value("/api/admin/ping"));
    }

    @Test
    void csrfBlocksPostEvenWhenAuthenticated() throws Exception {
        mockMvc.perform(post("/api/secure/change-email")
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@example.com\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("csrf_failed"))
                .andExpect(jsonPath("$.path").value("/api/secure/change-email"));
    }

    @Test
    void csrfTokenAllowsPostWhenAuthenticated() throws Exception {
        mockMvc.perform(post("/api/secure/change-email")
                        .with(httpBasic("user", "password"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("email_changed:alice@example.com"));
    }

    @Test
    void traceIdHeaderIsAddedEvenOnUnauthorizedResponses() throws Exception {
        mockMvc.perform(get("/api/secure/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(header().string("X-Trace-Id", org.hamcrest.Matchers.startsWith("trace-")));
    }

    @Test
    void jwtPublicEndpointIsAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/jwt/public/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong_jwt_public"));
    }

    @Test
    void jwtSecureEndpointReturns401WhenMissingBearerToken() throws Exception {
        mockMvc.perform(get("/api/jwt/secure/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/jwt/secure/ping"));
    }

    @Test
    void jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall() throws Exception {
        String token = jwtTokenService.issueToken("alice", "read");

        mockMvc.perform(get("/api/jwt/secure/ping")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/jwt/secure/ping"));
    }

    @Test
    void jwtSecureEndpointIsAccessibleWithBearerToken() throws Exception {
        String token = jwtTokenService.issueToken("alice", "read");

        mockMvc.perform(get("/api/jwt/secure/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong_jwt_secure"))
                .andExpect(jsonPath("$.subject").value("alice"));
    }

    @Test
    void jwtAdminEndpointReturns403WhenScopeMissing() throws Exception {
        String token = jwtTokenService.issueToken("alice", "read");

        mockMvc.perform(get("/api/jwt/admin/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("forbidden"))
                .andExpect(jsonPath("$.path").value("/api/jwt/admin/ping"));
    }

    @Test
    void jwtAdminEndpointIsAccessibleWhenAdminScopePresent() throws Exception {
        String token = jwtTokenService.issueToken("alice", "admin");

        mockMvc.perform(get("/api/jwt/admin/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong_jwt_admin"));
    }

    @Test
    void jwtPostDoesNotRequireCsrf() throws Exception {
        String token = jwtTokenService.issueToken("alice", "read");

        mockMvc.perform(post("/api/jwt/secure/change-email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("email_changed_jwt:alice@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void methodSecurityDeniesAdminOnlyMethodForNonAdmin() {
        assertThatThrownBy(() -> adminOnlyService.adminOnlyAction())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void methodSecurityDeniesAdminOnlyMethodWhenRolePrefixMissing_asPitfall() {
        assertThatThrownBy(() -> adminOnlyService.adminOnlyAction())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void methodSecurityAllowsAdminOnlyMethodForAdmin() {
        assertThat(adminOnlyService.adminOnlyAction()).isEqualTo("admin_action_done");
    }

    @Test
    @WithMockUser(roles = "USER")
    void selfInvocationBypassesMethodSecurityAsAPitfall() {
        assertThatThrownBy(() -> selfInvocationPitfallService.adminOnly())
                .isInstanceOf(AccessDeniedException.class);

        assertThat(selfInvocationPitfallService.outerCallsAdminOnly())
                .isEqualTo("outer:admin_only");
    }
}

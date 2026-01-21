package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本测试用于固定 Security 分支：401（未认证）、403（权限不足/CSRF），并提供可回归的证据链。

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.BootWebMvcApplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = BootWebMvcApplication.class)
@AutoConfigureMockMvc
class BootWebMvcSecurityLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/advanced/secure/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedRequestReturns200() throws Exception {
        mockMvc.perform(get("/api/advanced/secure/ping")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.user").value("user"));
    }

    @Test
    void insufficientRoleReturns403() throws Exception {
        mockMvc.perform(get("/api/advanced/secure/admin/ping")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfMissingReturns403EvenWhenAuthenticated() throws Exception {
        mockMvc.perform(post("/api/advanced/secure/update")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfProvidedReturns200() throws Exception {
        mockMvc.perform(post("/api/advanced/secure/update")
                        .with(httpBasic("user", "password"))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("updated"))
                .andExpect(jsonPath("$.user").value("user"));
    }

    @Test
    void adminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/advanced/secure/admin/ping")
                        .with(httpBasic("admin", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong-admin"))
                .andExpect(jsonPath("$.user").value("admin"));
    }
}


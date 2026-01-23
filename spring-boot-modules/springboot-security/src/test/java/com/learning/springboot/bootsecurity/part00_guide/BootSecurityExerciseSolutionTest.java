package com.learning.springboot.bootsecurity.part00_guide;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 参考实现：对齐 BootSecurityExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BootSecurityExerciseSolutionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void solution_secureMeEndpoint_returnsUsernameAndRoles() throws Exception {
        mockMvc.perform(get("/api/secure/me").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void solution_endToEnd_randomPort_distinguishes401And403() {
        ResponseEntity<Map> anonymous = restTemplate.getForEntity("/api/secure/ping", Map.class);
        assertThat(anonymous.getStatusCode().value()).isEqualTo(401);

        ResponseEntity<Map> forbidden = restTemplate.withBasicAuth("user", "password")
                .getForEntity("/api/admin/ping", Map.class);
        assertThat(forbidden.getStatusCode().value()).isEqualTo(403);
    }
}


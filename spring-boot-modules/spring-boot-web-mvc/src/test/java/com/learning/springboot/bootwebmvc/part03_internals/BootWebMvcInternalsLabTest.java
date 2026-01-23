package com.learning.springboot.bootwebmvc.part03_internals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WebMvcInternalsController.class)
@Import({WebMvcInternalsConfig.class, SecurityConfig.class})
class BootWebMvcInternalsLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void resolvesClientIpFromXForwardedForFirstValue() throws Exception {
        mockMvc.perform(get("/api/advanced/internals/whoami")
                        .header(HttpHeaders.USER_AGENT, "JUnit")
                        .header("X-Forwarded-For", "1.2.3.4, 5.6.7.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientIp").value("1.2.3.4"))
                .andExpect(jsonPath("$.userAgent").value("JUnit"));
    }

    @Test
    void fallsBackToRemoteAddrWhenXForwardedForIsMissing() throws Exception {
        mockMvc.perform(get("/api/advanced/internals/whoami")
                        .header(HttpHeaders.USER_AGENT, "JUnit")
                        .with(request -> {
                            request.setRemoteAddr("8.8.8.8");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientIp").value("8.8.8.8"))
                .andExpect(jsonPath("$.userAgent").value("JUnit"));
    }
}

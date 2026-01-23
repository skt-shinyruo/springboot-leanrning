package com.learning.springboot.bootwebmvc.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootwebmvc.BootWebMvcApplication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

/**
 * 参考实现：对齐 BootWebMvcExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest(
        classes = { BootWebMvcApplication.class, BootWebMvcExerciseSolutionTest.SolutionWebConfig.class },
        webEnvironment = WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class BootWebMvcExerciseSolutionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void solution_pathVariables_postThenGetById() throws Exception {
        String createBody = "{\"name\":\"Alice\",\"email\":\"alice@example.com\"}";

        String createdJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = objectMapper.readTree(createdJson);
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void solution_pathVariables_getReturns404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void solution_interceptor_onlyAppliesToApiPaths() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Api-Interceptor", "on"));

        mockMvc.perform(get("/css/app.css").accept(MediaType.valueOf("text/css")))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-Api-Interceptor"));
    }

    @Test
    void solution_converterFormatter_bindsPathVariableToCustomType() throws Exception {
        mockMvc.perform(get("/api/advanced/convert/users/{id}", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void solution_integrationTest_randomPortPingIsReachable() {
        ResponseEntity<Map> resp = restTemplate.getForEntity("/api/ping", Map.class);
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).containsEntry("message", "pong");
    }

    @Test
    void solution_strictJson_unknownFieldAppearsInFieldErrors() throws Exception {
        String body = """
                {
                  "message": "hello",
                  "createdAt": "2026-01-07T16:35:00Z",
                  "extra": "should-fail"
                }
                """;

        mockMvc.perform(post("/api/advanced/contract/strict-echo")
                        .contentType(MediaType.valueOf("application/vnd.learning.strict+json"))
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("malformed_json"))
                .andExpect(jsonPath("$.fieldErrors.extra").exists());
    }

    @Test
    void solution_fileNotFound_returns404AndApiError() throws Exception {
        mockMvc.perform(get("/api/advanced/files/{id}", 9999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("file_not_found"));
    }

    @TestConfiguration
    static class SolutionWebConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(apiOnlyInterceptor()).addPathPatterns("/api/**");
        }

        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(userIdConverter());
        }

        @Bean
        HandlerInterceptor apiOnlyInterceptor() {
            return new HandlerInterceptor() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                    response.setHeader("X-Api-Interceptor", "on");
                    return true;
                }
            };
        }

        @Bean
        Converter<String, UserId> userIdConverter() {
            return new Converter<>() {
                @Override
                public UserId convert(String source) {
                    return new UserId(Long.parseLong(source));
                }
            };
        }

        @Bean
        UserIdController userIdController() {
            return new UserIdController();
        }
    }

    record UserId(long value) {}

    @RestController
    @RequestMapping("/api/advanced/convert/users")
    static class UserIdController {

        @GetMapping("/{id}")
        Map<String, Object> get(@PathVariable("id") UserId id) {
            return Map.of("id", id.value());
        }
    }
}

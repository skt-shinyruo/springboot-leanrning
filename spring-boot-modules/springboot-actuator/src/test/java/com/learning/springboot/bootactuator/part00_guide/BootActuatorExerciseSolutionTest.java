package com.learning.springboot.bootactuator.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootactuator.BootActuatorApplication;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;

/**
 * 参考实现：对齐 BootActuatorExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class BootActuatorExerciseSolutionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void solution_toggleableHealthIndicator_changesUpDownWithProperty() throws Exception {
        try (ConfigurableApplicationContext enabled = startApp(
                "server.port=0",
                "learning.health.enabled=true")) {
            JsonNode health = getJson(enabled, "/actuator/health", 200);
            assertThat(health.path("status").asText()).isEqualTo("UP");
            assertThat(health.at("/components/learning/status").asText()).isEqualTo("UP");
        }

        try (ConfigurableApplicationContext disabled = startApp(
                "server.port=0",
                "learning.health.enabled=false")) {
            JsonNode health = getJson(disabled, "/actuator/health", 503);
            assertThat(health.path("status").asText()).isEqualTo("DOWN");
            assertThat(health.at("/components/learning/status").asText()).isEqualTo("DOWN");
        }
    }

    @Test
    void solution_infoContributor_appearsInActuatorInfo() throws Exception {
        try (ConfigurableApplicationContext context = startApp("server.port=0")) {
            JsonNode info = getJson(context, "/actuator/info", 200);
            assertThat(info.at("/learning/module").asText()).isEqualTo("springboot-actuator");
        }
    }

    @Test
    void solution_endpointExposure_customEndpoint404ByDefault_thenReachableWhenIncluded() throws Exception {
        try (ConfigurableApplicationContext context = startApp("server.port=0")) {
            ResponseEntity<String> resp = getString(context, "/actuator/learning");
            assertThat(resp.getStatusCode().value()).isEqualTo(404);
        }

        try (ConfigurableApplicationContext context = startApp(
                "server.port=0",
                "management.endpoints.web.exposure.include=health,info,learning")) {
            ResponseEntity<String> resp = getString(context, "/actuator/learning");
            assertThat(resp.getStatusCode().value()).isEqualTo(200);
            JsonNode json = readJson(resp.getBody());
            assertThat(json.get("module").asText()).isEqualTo("springboot-actuator");
            assertThat(json.get("status").asText()).isEqualTo("ok");
        }
    }

    private ConfigurableApplicationContext startApp(String... properties) {
        String[] args = Arrays.stream(properties)
                .map(p -> p.startsWith("--") ? p : "--" + p)
                .toArray(String[]::new);

        return new SpringApplicationBuilder(BootActuatorApplication.class)
                .run(args);
    }

    private JsonNode getJson(ConfigurableApplicationContext context, String path, int expectedStatus) throws IOException {
        ResponseEntity<String> resp = getString(context, path);
        assertThat(resp.getStatusCode().value()).isEqualTo(expectedStatus);
        return readJson(resp.getBody());
    }

    private JsonNode readJson(String body) throws IOException {
        assertThat(body).isNotNull();
        return objectMapper.readTree(body);
    }

    private ResponseEntity<String> getString(ConfigurableApplicationContext context, String path) {
        Integer port = context.getEnvironment().getProperty("local.server.port", Integer.class);
        assertThat(port).isNotNull();
        String url = "http://localhost:" + port + path;
        return new TestRestTemplate().getForEntity(url, String.class);
    }
}

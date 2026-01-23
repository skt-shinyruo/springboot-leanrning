package com.learning.springboot.bootactuator.part01_actuator;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootActuatorLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthIncludesCustomIndicator() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.path("status").asText()).isEqualTo("UP");
        assertThat(root.at("/components/learning/status").asText()).isEqualTo("UP");
        assertThat(root.at("/components/learning/details/module").asText()).isEqualTo("springboot-actuator");
    }

    @Test
    void infoEndpointContainsConfiguredInfoProperties() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.at("/app/name").asText()).isEqualTo("springboot-actuator");
    }

    @Test
    void actuatorRootListsExposedEndpoints() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.at("/_links/health/href").asText()).contains("/actuator/health");
        assertThat(root.at("/_links/info/href").asText()).contains("/actuator/info");
    }

    @Test
    void envEndpointIsNotExposedByDefault() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/env", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void healthReturnsHttp200WhenUp() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void healthResponseIsJson() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).contains("application/json");
    }

    @Test
    void learningIndicatorHasExpectedDetailsHint() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.at("/components/learning/details/hint").asText()).contains("learn Actuator");
    }
}

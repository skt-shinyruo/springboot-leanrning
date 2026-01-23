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

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "management.endpoints.web.exposure.include=health,info,env"
)
class BootActuatorExposureOverrideLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void envEndpointCanBeExposedViaProperties() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/env", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void actuatorRootIncludesEnvLinkWhenExposed() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator", String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.at("/_links/env/href").asText()).contains("/actuator/env");
    }

    @Test
    void envResponseContainsPropertySources() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/env", String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        assertThat(root.has("propertySources")).isTrue();
    }
}


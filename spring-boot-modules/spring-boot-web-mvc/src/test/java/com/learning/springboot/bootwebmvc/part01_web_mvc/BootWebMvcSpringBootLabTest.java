package com.learning.springboot.bootwebmvc.part01_web_mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootWebMvcSpringBootLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @SuppressWarnings("unchecked")
    void pingWorksInFullSpringBootContext() {
        Map<String, Object> response = restTemplate.getForObject("/api/ping", Map.class);
        assertThat(response.get("message")).isEqualTo("pong");
    }

    @Test
    @SuppressWarnings("unchecked")
    void createUserWorksInFullSpringBootContext() {
        Map<String, Object> response = restTemplate.postForObject(
                "/api/users",
                Map.of("name", "Alice", "email", "alice@example.com"),
                Map.class
        );

        assertThat(response.get("id")).isInstanceOf(Number.class);
        assertThat(((Number) response.get("id")).longValue()).isGreaterThan(0);
        assertThat(response.get("name")).isEqualTo("Alice");
        assertThat(response.get("email")).isEqualTo("alice@example.com");
    }

    @Test
    @SuppressWarnings("unchecked")
    void unknownRouteFallsBackToSpringBootErrorEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/does-not-exist", Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("path")).isEqualTo("/api/does-not-exist");
    }
}

package com.learning.springboot.boottesting.part01_testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerSpringBootLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @SuppressWarnings("unchecked")
    void returnsGreetingFromRealService() {
        Map<String, Object> response = restTemplate.getForObject("/api/greeting?name=Bob", Map.class);
        assertThat(response.get("message")).isEqualTo("Hello, Bob");
    }

    @Test
    @SuppressWarnings("unchecked")
    void usesDefaultNameWhenRequestParamIsMissing() {
        Map<String, Object> response = restTemplate.getForObject("/api/greeting", Map.class);
        assertThat(response.get("message")).isEqualTo("Hello, World");
    }

    @Test
    @SuppressWarnings("unchecked")
    void responseContainsMessageKey() {
        Map<String, Object> response = restTemplate.getForObject("/api/greeting?name=Alice", Map.class);
        assertThat(response).containsKey("message");
    }
}

package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本测试用于验证最小观测闭环：Interceptor 计时 + Actuator metrics 可读取。

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootwebmvc.BootWebMvcApplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = BootWebMvcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootWebMvcObservabilityLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void interceptorAddsTimingHeaderOnAdvancedEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/advanced/contract/ping", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("X-Lab-Elapsed-Ms")).isNotBlank();
    }

    @Test
    void actuatorMetricsEndpointIsReadable() throws Exception {
        // 先触发一次请求，确保 metrics 中至少有一次 http.server.requests 的记录。
        restTemplate.getForEntity("/api/advanced/contract/ping", String.class);

        ResponseEntity<String> metrics = restTemplate.getForEntity("/actuator/metrics/http.server.requests", String.class);
        assertThat(metrics.getStatusCode()).isEqualTo(HttpStatus.OK);

        String contentType = metrics.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        assertThat(contentType).isNotBlank();

        JsonNode json = objectMapper.readTree(metrics.getBody());
        assertThat(json.get("name").asText()).isEqualTo("http.server.requests");
    }
}


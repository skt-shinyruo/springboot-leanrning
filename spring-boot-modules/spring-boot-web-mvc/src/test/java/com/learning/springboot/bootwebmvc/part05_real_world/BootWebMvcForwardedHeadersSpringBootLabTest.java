package com.learning.springboot.bootwebmvc.part05_real_world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.forward-headers-strategy=framework"
)
class BootWebMvcForwardedHeadersSpringBootLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @SuppressWarnings("unchecked")
    void forwardedHeadersAreReflectedInServletRequestSemantics() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("X-Forwarded-Proto", "https");
        headers.add("X-Forwarded-Host", "example.com");
        headers.add("X-Forwarded-Port", "443");
        headers.add("X-Forwarded-Prefix", "/app");
        headers.add("X-Forwarded-For", "1.2.3.4, 5.6.7.8");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/advanced/internals/request-info",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("scheme")).isEqualTo("https");
        assertThat(body.get("secure")).isEqualTo(true);
        assertThat(body.get("serverName")).isEqualTo("example.com");
        assertThat(((Number) body.get("serverPort")).intValue()).isEqualTo(443);
        assertThat(body.get("contextPath")).isEqualTo("/app");
        assertThat(body.get("requestUri")).isEqualTo("/app/api/advanced/internals/request-info");
        assertThat(body.get("requestUrl")).isEqualTo("https://example.com/app/api/advanced/internals/request-info");
        assertThat(body.get("remoteAddr")).isEqualTo("1.2.3.4");
    }
}


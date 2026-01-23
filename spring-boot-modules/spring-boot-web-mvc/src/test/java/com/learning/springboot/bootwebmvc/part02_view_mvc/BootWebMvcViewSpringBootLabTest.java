package com.learning.springboot.bootwebmvc.part02_view_mvc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootWebMvcViewSpringBootLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void pingPageWorksInFullSpringBootContext() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/pages/ping",
                HttpMethod.GET,
                new HttpEntity<>(accept(MediaType.TEXT_HTML)),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getBody()).contains("data-testid=\"ping-message\"");
    }

    @Test
    void unknownRouteReturnsCustom404HtmlPage() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/pages/does-not-exist",
                HttpMethod.GET,
                new HttpEntity<>(accept(MediaType.TEXT_HTML)),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).contains("data-testid=\"error-404\"");
    }

    @Test
    void errorDemoRenders5xxHtmlPage() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/pages/error-demo",
                HttpMethod.GET,
                new HttpEntity<>(accept(MediaType.TEXT_HTML)),
                String.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).contains("data-testid=\"error-5xx\"");
    }

    private static HttpHeaders accept(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(mediaType));
        return headers;
    }
}

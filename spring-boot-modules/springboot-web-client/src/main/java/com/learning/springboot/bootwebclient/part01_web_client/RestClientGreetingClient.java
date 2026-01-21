package com.learning.springboot.bootwebclient.part01_web_client;

import java.time.Duration;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

class RestClientGreetingClient {

    private final RestClient restClient;

    RestClientGreetingClient(String baseUrl, Duration readTimeout, String correlationId) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("X-Correlation-Id", correlationId)
                .build();
    }

    GreetingResponse getGreeting(String name) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/greeting").queryParam("name", name).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new DownstreamServiceException(response.getStatusCode().value(), "downstream_error");
                })
                .body(GreetingResponse.class);
    }

    GreetingResponse createGreeting(String name) {
        return restClient.post()
                .uri("/api/greeting")
                .body(new GreetingRequest(name))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new DownstreamServiceException(response.getStatusCode().value(), "downstream_error");
                })
                .body(GreetingResponse.class);
    }

    GreetingResponse getGreetingWithRetryOn5xx(String name, int maxAttempts) {
        int attempts = 0;
        while (true) {
            attempts += 1;
            try {
                return getGreeting(name);
            } catch (DownstreamServiceException e) {
                if (attempts >= maxAttempts) {
                    throw e;
                }
                if (e.getStatus() >= 500 && e.getStatus() < 600) {
                    continue;
                }
                throw e;
            }
        }
    }

    private record GreetingRequest(String name) {}
}


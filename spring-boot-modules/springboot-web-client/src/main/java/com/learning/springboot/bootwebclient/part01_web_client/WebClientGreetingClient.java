package com.learning.springboot.bootwebclient.part01_web_client;

import java.time.Duration;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

class WebClientGreetingClient {

    private final WebClient webClient;

    WebClientGreetingClient(String baseUrl, Duration responseTimeout, String correlationId) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(responseTimeout);

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter((request, next) -> next.exchange(
                        ClientRequestWithHeader.addHeader(request, "X-Correlation-Id", correlationId)
                ))
                .build();
    }

    Mono<GreetingResponse> getGreeting(String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/greeting").queryParam("name", name).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(
                        new DownstreamServiceException(response.statusCode().value(), "downstream_error")
                ))
                .bodyToMono(GreetingResponse.class);
    }

    Mono<GreetingResponse> createGreeting(String name) {
        return webClient.post()
                .uri("/api/greeting")
                .bodyValue(new GreetingRequest(name))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(
                        new DownstreamServiceException(response.statusCode().value(), "downstream_error")
                ))
                .bodyToMono(GreetingResponse.class);
    }

    Mono<GreetingResponse> getGreetingWithRetryOn5xx(String name, int maxAttempts) {
        return getGreeting(name)
                .retryWhen(Retry.max(maxAttempts - 1)
                        .filter(throwable -> throwable instanceof DownstreamServiceException e
                                && e.getStatus() >= 500
                                && e.getStatus() < 600));
    }

    private record GreetingRequest(String name) {}
}


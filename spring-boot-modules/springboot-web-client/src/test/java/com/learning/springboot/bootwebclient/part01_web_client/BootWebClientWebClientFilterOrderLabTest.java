package com.learning.springboot.bootwebclient.part01_web_client;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class BootWebClientWebClientFilterOrderLabTest {

    @Test
    void webClientFilters_requestOrderAndResponseOrder_areDifferent() {
        List<String> trace = new ArrayList<>();

        ExchangeFunction exchangeFunction = request -> {
            trace.add("exchange");
            return Mono.just(ClientResponse.create(HttpStatus.OK).build());
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .filter(tracingFilter("f1", trace))
                .filter(tracingFilter("f2", trace))
                .build();

        webClient.get()
                .uri("http://example.test")
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(2));

        assertThat(trace).containsExactly(
                "f1:request",
                "f2:request",
                "exchange",
                "f2:response",
                "f1:response"
        );
    }

    private static ExchangeFilterFunction tracingFilter(String name, List<String> trace) {
        return (request, next) -> {
            trace.add(name + ":request");
            return next.exchange(request).doOnNext(response -> trace.add(name + ":response"));
        };
    }
}


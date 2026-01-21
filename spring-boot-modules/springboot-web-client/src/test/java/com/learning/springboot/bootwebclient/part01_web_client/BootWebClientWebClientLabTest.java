package com.learning.springboot.bootwebclient.part01_web_client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BootWebClientWebClientLabTest {

    private MockWebServer server;
    private WebClientGreetingClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        client = new WebClientGreetingClient(
                server.url("/").toString(),
                Duration.ofMillis(200),
                "cid-webflux-123"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void webClientGetsGreeting() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        StepVerifier.create(client.getGreeting("Alice"))
                .assertNext(response -> assertThat(response.message()).isEqualTo("Hello, Alice"))
                .verifyComplete();
    }

    @Test
    void webClientSendsExpectedPathAndHeaders() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        client.getGreeting("Alice").block(Duration.ofSeconds(1));

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/api/greeting?name=Alice");
        assertThat(request.getHeader("X-Correlation-Id")).isEqualTo("cid-webflux-123");
    }

    @Test
    void webClientIgnoresUnknownJsonFieldsByDefault() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\",\"extra\":\"ignored\"}"));

        StepVerifier.create(client.getGreeting("Alice"))
                .assertNext(response -> assertThat(response.message()).isEqualTo("Hello, Alice"))
                .verifyComplete();
    }

    @Test
    void webClientMaps400ToDomainException() {
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"bad_request\"}"));

        StepVerifier.create(client.getGreeting("Alice"))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(DownstreamServiceException.class);
                    assertThat(((DownstreamServiceException) ex).getStatus()).isEqualTo(400);
                })
                .verify();
    }

    @Test
    void webClientMaps500ToDomainException() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"server_error\"}"));

        StepVerifier.create(client.getGreeting("Alice"))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(DownstreamServiceException.class);
                    assertThat(((DownstreamServiceException) ex).getStatus()).isEqualTo(500);
                })
                .verify();
    }

    @Test
    void webClientCanPostJsonBody() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        GreetingResponse response = client.createGreeting("Alice").block(Duration.ofSeconds(1));
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Hello, Alice");

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/api/greeting");
        assertThat(request.getHeader("Content-Type")).contains("application/json");
        assertThat(request.getBody().readUtf8()).contains("\"name\":\"Alice\"");
    }

    @Test
    void webClientResponseTimeoutFailsFast() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}")
                .setBodyDelay(1, TimeUnit.SECONDS));

        Mono<GreetingResponse> mono = client.getGreeting("Alice");
        assertThatThrownBy(() -> mono.block(Duration.ofSeconds(2)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void webClientRetriesOn5xxAndEventuallySucceeds() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{\"message\":\"server_error\"}"));
        server.enqueue(new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        StepVerifier.create(client.getGreetingWithRetryOn5xx("Alice", 2))
                .assertNext(response -> assertThat(response.message()).isEqualTo("Hello, Alice"))
                .verifyComplete();

        assertThat(server.getRequestCount()).isEqualTo(2);
    }
}


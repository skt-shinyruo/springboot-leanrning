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
import org.springframework.web.client.RestClientException;

class BootWebClientRestClientLabTest {

    private MockWebServer server;
    private RestClientGreetingClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        client = new RestClientGreetingClient(
                server.url("/").toString(),
                Duration.ofMillis(200),
                "cid-rest-123"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void restClientGetsGreeting() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        GreetingResponse response = client.getGreeting("Alice");
        assertThat(response.message()).isEqualTo("Hello, Alice");
    }

    @Test
    void restClientSendsExpectedPathAndHeaders() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        client.getGreeting("Alice");

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/api/greeting?name=Alice");
        assertThat(request.getHeader("X-Correlation-Id")).isEqualTo("cid-rest-123");
    }

    @Test
    void restClientIgnoresUnknownJsonFieldsByDefault() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\",\"extra\":\"ignored\"}"));

        GreetingResponse response = client.getGreeting("Alice");
        assertThat(response.message()).isEqualTo("Hello, Alice");
    }

    @Test
    void restClientMaps400ToDomainException() {
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"bad_request\"}"));

        assertThatThrownBy(() -> client.getGreeting("Alice"))
                .isInstanceOf(DownstreamServiceException.class)
                .satisfies(ex -> assertThat(((DownstreamServiceException) ex).getStatus()).isEqualTo(400));
    }

    @Test
    void restClientMaps500ToDomainException() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"server_error\"}"));

        assertThatThrownBy(() -> client.getGreeting("Alice"))
                .isInstanceOf(DownstreamServiceException.class)
                .satisfies(ex -> assertThat(((DownstreamServiceException) ex).getStatus()).isEqualTo(500));
    }

    @Test
    void restClientCanPostJsonBody() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        GreetingResponse response = client.createGreeting("Alice");
        assertThat(response.message()).isEqualTo("Hello, Alice");

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/api/greeting");
        assertThat(request.getHeader("Content-Type")).contains("application/json");
        assertThat(request.getBody().readUtf8()).contains("\"name\":\"Alice\"");
    }

    @Test
    void restClientReadTimeoutFailsFast() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}")
                .setBodyDelay(1, TimeUnit.SECONDS));

        assertThatThrownBy(() -> client.getGreeting("Alice"))
                .isInstanceOf(RestClientException.class);
    }

    @Test
    void restClientRetriesOn5xxAndEventuallySucceeds() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{\"message\":\"server_error\"}"));
        server.enqueue(new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello, Alice\"}"));

        GreetingResponse response = client.getGreetingWithRetryOn5xx("Alice", 2);
        assertThat(response.message()).isEqualTo("Hello, Alice");

        assertThat(server.getRequestCount()).isEqualTo(2);
    }
}

package com.learning.springboot.bootwebclient.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.springboot.bootwebclient.part01_web_client.DownstreamServiceException;
import com.learning.springboot.bootwebclient.part01_web_client.GreetingResponse;
import com.learning.springboot.bootwebclient.part01_web_client.RestClientGreetingClient;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 参考实现：对齐 BootWebClientExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class BootWebClientExerciseSolutionTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void solution_restClientParsesErrorBodyMessage_intoDomainException() {
        RestClientGreetingClient client = new RestClientGreetingClient(
                server.url("/").toString(),
                Duration.ofMillis(200),
                "cid-rest-123"
        );

        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"bad_request\"}"));

        assertThatThrownBy(() -> client.getGreeting("Alice"))
                .isInstanceOf(DownstreamServiceException.class)
                .satisfies(ex -> {
                    DownstreamServiceException e = (DownstreamServiceException) ex;
                    assertThat(e.getStatus()).isEqualTo(400);
                    assertThat(e.getMessage()).isEqualTo("bad_request");
                    assertThat(e.getErrorBody()).contains("bad_request");
                });
    }

    @Test
    void solution_dynamicCorrelationId_webClientFilterGeneratesDifferentHeaderEachRequest() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"ok\"}"));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"ok\"}"));

        WebClient client = WebClient.builder()
                .baseUrl(server.url("/").toString())
                .filter((req, next) -> {
                    String cid = "cid-" + UUID.randomUUID();
                    return next.exchange(
                            org.springframework.web.reactive.function.client.ClientRequest
                                    .from(req)
                                    .header("X-Correlation-Id", cid)
                                    .build()
                    );
                })
                .build();

        Mono<String> call = client.get()
                .uri("/api/greeting?name=Alice")
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> Mono.error(new IllegalStateException("unexpected_status")))
                .bodyToMono(String.class);

        call.block(Duration.ofSeconds(1));
        call.block(Duration.ofSeconds(1));

        RecordedRequest first = server.takeRequest(1, TimeUnit.SECONDS);
        RecordedRequest second = server.takeRequest(1, TimeUnit.SECONDS);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.getHeader("X-Correlation-Id")).isNotNull();
        assertThat(second.getHeader("X-Correlation-Id")).isNotNull();
        assertThat(first.getHeader("X-Correlation-Id")).isNotEqualTo(second.getHeader("X-Correlation-Id"));
    }

    @Test
    void solution_pureReactiveTest_stepVerifierWithoutBlocking() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"Hello\"}"));

        WebClient client = WebClient.builder()
                .baseUrl(server.url("/").toString())
                .build();

        Mono<GreetingResponse> mono = client.get()
                .uri("/api/greeting?name=Alice")
                .retrieve()
                .bodyToMono(GreetingResponse.class);

        StepVerifier.create(mono)
                .assertNext(resp -> assertThat(resp.message()).isEqualTo("Hello"))
                .verifyComplete();
    }
}


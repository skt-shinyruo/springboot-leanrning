package com.learning.springboot.bootwebclient.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootwebclient.part01_web_client.GreetingResponse;
import com.learning.springboot.bootwebclient.part01_web_client.RestClientGreetingClient;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 并发/性能实验：单个 RestClient 实例可被并发复用（请求隔离 + 线程安全）。
 */
class BootWebClientRestClientConcurrencyLabTest {

    private MockWebServer server;
    private RestClientGreetingClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.setDispatcher(new GreetingDispatcher());
        server.start();

        client = new RestClientGreetingClient(
                server.url("/").toString(),
                Duration.ofSeconds(1),
                "cid-concurrency"
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void restClient_isReusableUnderConcurrentCalls_requestDoesNotMix() throws Exception {
        int tasks = 24;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("webclient-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                String name = "N" + i;
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        GreetingResponse response = client.getGreeting(name);
                        assertThat(response.message()).isEqualTo("Hello, " + name);
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(10, TimeUnit.SECONDS)).isTrue();
            assertThat(errors).isEmpty();
            assertThat(server.getRequestCount()).isEqualTo(tasks);
        } finally {
            pool.shutdownNow();
        }
    }

    private static ThreadFactory namedThreadFactory(String prefix) {
        AtomicInteger seq = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r);
            t.setName(prefix + seq.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
    }

    static class GreetingDispatcher extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) {
            String path = request.getPath();
            if (path == null || !path.startsWith("/api/greeting")) {
                return new MockResponse().setResponseCode(404);
            }

            String name = "World";
            int q = path.indexOf('?');
            if (q >= 0 && q + 1 < path.length()) {
                String query = path.substring(q + 1);
                for (String pair : query.split("&")) {
                    int eq = pair.indexOf('=');
                    if (eq > 0 && "name".equals(pair.substring(0, eq))) {
                        name = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                    }
                }
            }

            return new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody("{\"message\":\"Hello, " + name + "\"}");
        }
    }
}


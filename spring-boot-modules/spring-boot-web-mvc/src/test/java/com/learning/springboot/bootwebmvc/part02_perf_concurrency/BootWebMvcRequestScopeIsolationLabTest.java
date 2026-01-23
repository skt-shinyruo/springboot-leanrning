package com.learning.springboot.bootwebmvc.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootwebmvc.BootWebMvcApplication;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

/**
 * 并发/性能实验：用并发请求证明 request scope（以及请求上下文）不会串线。
 *
 * <p>断言策略：只断言“可观测事实”（返回值/唯一性），不使用耗时阈值。
 */
@SpringBootTest(
        classes = { BootWebMvcApplication.class, BootWebMvcRequestScopeIsolationLabTest.RequestScopeConfig.class },
        webEnvironment = WebEnvironment.RANDOM_PORT
)
class BootWebMvcRequestScopeIsolationLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void requestScopedBean_isIsolatedAcrossConcurrentRequests() throws Exception {
        int tasks = 16;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("bootwebmvc-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);

        ConcurrentLinkedQueue<String> requestIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                int idx = i;
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);

                        String expected = "cid-" + idx;
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("X-Expected", expected);

                        ResponseEntity<Map> resp = restTemplate.exchange(
                                "/api/perf/request-scope",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                Map.class
                        );

                        assertThat(resp.getStatusCode().value()).isEqualTo(200);
                        assertThat(resp.getBody()).containsEntry("echo", expected);

                        Object requestId = resp.getBody().get("requestId");
                        assertThat(requestId).isInstanceOf(String.class);
                        requestIds.add((String) requestId);
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(3, TimeUnit.SECONDS)).isTrue();

            assertThat(errors).isEmpty();
            assertThat(requestIds).hasSize(tasks);
            assertThat(requestIds).doesNotHaveDuplicates();
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

    @TestConfiguration
    static class RequestScopeConfig {

        @Bean
        RequestScopeController requestScopeController(RequestIdHolder requestIdHolder) {
            return new RequestScopeController(requestIdHolder);
        }

        @Bean
        @RequestScope
        RequestIdHolder requestIdHolder() {
            return new RequestIdHolder(UUID.randomUUID().toString());
        }
    }

    static class RequestIdHolder {
        private final String id;

        RequestIdHolder(String id) {
            this.id = id;
        }

        String id() {
            return id;
        }
    }

    @RestController
    @RequestMapping("/api/perf")
    static class RequestScopeController {

        private final RequestIdHolder requestIdHolder;

        RequestScopeController(RequestIdHolder requestIdHolder) {
            this.requestIdHolder = requestIdHolder;
        }

        @GetMapping("/request-scope")
        Map<String, Object> requestScope(@RequestHeader("X-Expected") String expected) {
            return Map.of(
                    "echo", expected,
                    "requestId", requestIdHolder.id()
            );
        }
    }
}

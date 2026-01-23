package com.learning.springboot.bootsecurity.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 并发/性能实验：并发请求下，SecurityContext 线程隔离（principal 不串线）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class BootSecuritySecurityContextIsolationLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void securityContext_isIsolatedAcrossConcurrentRequests() throws Exception {
        int tasks = 20;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("bootsecurity-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                String username = (i % 2 == 0) ? "user" : "admin";
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);

                        String body = mockMvc.perform(get("/api/secure/ping").with(httpBasic(username, "password")))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                        JsonNode json = objectMapper.readTree(body);
                        assertThat(json.path("user").asText()).isEqualTo(username);
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(errors).isEmpty();
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
}


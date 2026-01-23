package com.learning.springboot.springcoreaopweaving.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwPlainCalculator;
import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import com.learning.springboot.springcoreaopweaving.support.JoinPointEvent;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 并发/性能实验（LTW）：并发调用被 weaving 的非 Spring 对象，切面记录的线程名/事件条目保持一致。
 *
 * <p>断言策略：只断言“事件条目数量 + threadName 前缀”，不使用耗时阈值。
 */
public class AspectjLtwConcurrencyLabTest {

    @BeforeAll
    static void requireJavaAgent() {
        boolean hasJavaAgent = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("-javaagent:"));

        Assumptions.assumeTrue(
                hasJavaAgent,
                "LTW 测试需要 -javaagent（AspectJ Weaver）。请通过 Maven Surefire 的 ltw-tests-with-javaagent 执行，或直接运行 mvn test。"
        );
    }

    private final InvocationLog invocationLog = InvocationLog.getInstance();

    @BeforeEach
    void setUp() {
        invocationLog.reset();
    }

    @Test
    void ltw_concurrentInvocation_recordsEventsWithCorrectThreadNames() throws Exception {
        int tasks = 24;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("ltw-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        int r = new LtwPlainCalculator().add(1, 2);
                        assertThat(r).isEqualTo(3);
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

            List<JoinPointEvent> events = invocationLog.events().stream()
                    .filter(e -> "method-execution:plain-add".equals(e.advice()))
                    .toList();

            assertThat(events).hasSize(tasks);
            assertThat(events).allSatisfy(e -> assertThat(e.threadName()).startsWith("ltw-perf-"));
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


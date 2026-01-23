package com.learning.springboot.springcoretx.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoretx.SpringCoreTxApplication;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 并发/性能实验：事务上下文是 ThreadLocal 绑定的 —— 不会自动跨线程传播。
 */
@SpringBootTest(classes = { SpringCoreTxApplication.class, SpringCoreTxThreadLocalBoundaryLabTest.ProbeConfig.class })
class SpringCoreTxThreadLocalBoundaryLabTest {

    @Autowired
    private TransactionThreadBoundaryProbe probe;

    @Test
    void transactionContext_doesNotCrossThreadBoundary() throws Exception {
        Observation obs = probe.observeTransactionContextAcrossThreads();
        assertThat(obs.outerThreadTxActive()).isTrue();
        assertThat(obs.childThreadTxActive()).isFalse();
    }

    @TestConfiguration
    static class ProbeConfig {
        @Bean
        TransactionThreadBoundaryProbe transactionThreadBoundaryProbe() {
            return new TransactionThreadBoundaryProbe();
        }
    }

    record Observation(boolean outerThreadTxActive, boolean childThreadTxActive) {
    }

    static class TransactionThreadBoundaryProbe {

        @Transactional
        Observation observeTransactionContextAcrossThreads() throws Exception {
            boolean outerActive = TransactionSynchronizationManager.isActualTransactionActive();

            ExecutorService pool = Executors.newSingleThreadExecutor(namedThreadFactory("tx-child-"));
            CountDownLatch checked = new CountDownLatch(1);
            AtomicBoolean childActive = new AtomicBoolean(true);

            try {
                Future<?> f = pool.submit(() -> {
                    try {
                        childActive.set(TransactionSynchronizationManager.isActualTransactionActive());
                    } finally {
                        checked.countDown();
                    }
                });

                assertThat(checked.await(2, TimeUnit.SECONDS)).isTrue();
                f.get(2, TimeUnit.SECONDS);
            } finally {
                pool.shutdownNow();
            }

            return new Observation(outerActive, childActive.get());
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


package com.learning.springboot.bootdatajpa.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootdatajpa.BootDataJpaApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 并发/性能实验：并发事务中，每个线程都有独立的 EntityManager（线程绑定，不跨线程共享）。
 */
@SpringBootTest(classes = { BootDataJpaApplication.class, BootDataJpaEntityManagerConcurrencyLabTest.ProbeConfig.class })
class BootDataJpaEntityManagerConcurrencyLabTest {

    @Autowired
    private EntityManagerConcurrencyProbe probe;

    @Test
    void eachThreadGetsItsOwnEntityManager_inTransactionalBoundary() throws Exception {
        int tasks = 12;
        ExecutorService pool = Executors.newFixedThreadPool(6, namedThreadFactory("bootdatajpa-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);

        ConcurrentLinkedQueue<Integer> entityManagerIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        entityManagerIds.add(probe.entityManagerIdentityInTransaction());
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
            assertThat(entityManagerIds).hasSize(tasks);
            assertThat(entityManagerIds).doesNotHaveDuplicates();
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
    static class ProbeConfig {
        @Bean
        EntityManagerConcurrencyProbe entityManagerConcurrencyProbe(EntityManagerFactory entityManagerFactory) {
            return new EntityManagerConcurrencyProbe(entityManagerFactory);
        }
    }

    static class EntityManagerConcurrencyProbe {
        private final EntityManagerFactory entityManagerFactory;

        EntityManagerConcurrencyProbe(EntityManagerFactory entityManagerFactory) {
            this.entityManagerFactory = entityManagerFactory;
        }

        @Transactional
        int entityManagerIdentityInTransaction() {
            assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();

            EntityManager em1 = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
            EntityManager em2 = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);

            assertThat(em1).isNotNull();
            assertThat(em2).isSameAs(em1);

            return System.identityHashCode(em1);
        }
    }
}


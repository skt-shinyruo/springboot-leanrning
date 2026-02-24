package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

class BootAsyncSchedulingTransactionBoundaryLabTest {

    @Test
    void transactionContextDoesNotPropagateAcrossAsyncThreadBoundaryByDefault() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncTxConfig.class);

        runner.run(context -> {
            TransactionTemplate transactionTemplate = context.getBean(TransactionTemplate.class);
            TxObservingAsyncService asyncService = context.getBean(TxObservingAsyncService.class);

            TxObservation observation = transactionTemplate.execute(status -> {
                boolean callerTxActive = TransactionSynchronizationManager.isActualTransactionActive();
                String callerThreadName = Thread.currentThread().getName();

                try {
                    TxSnapshot asyncSnapshot = asyncService.observeTxActive().get(1, TimeUnit.SECONDS);
                    return new TxObservation(callerThreadName, callerTxActive, asyncSnapshot);
                } catch (Exception ex) {
                    throw new IllegalStateException("async observation failed", ex);
                }
            });

            assertThat(observation).isNotNull();
            assertThat(observation.callerThreadName()).doesNotStartWith("async-");
            assertThat(observation.callerTxActive()).isTrue();

            assertThat(observation.asyncSnapshot().threadName()).startsWith("async-");
            assertThat(observation.asyncSnapshot().txActive()).isFalse();
        });
    }

    @Test
    void asyncAndTransactionalOnSameMethod_runsTransactionInsideAsyncThread_notCallerThread() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncTxConfig.class);

        runner.run(context -> {
            TxObservingAsyncService asyncService = context.getBean(TxObservingAsyncService.class);

            boolean callerTxActive = TransactionSynchronizationManager.isActualTransactionActive();
            assertThat(callerTxActive).isFalse();

            TxSnapshot snapshot = asyncService.observeTxActiveInAsyncTransactionalMethod().get(1, TimeUnit.SECONDS);
            assertThat(snapshot.threadName()).startsWith("async-");
            assertThat(snapshot.txActive()).isTrue();
        });
    }

    record TxSnapshot(String threadName, boolean txActive) {}

    record TxObservation(String callerThreadName, boolean callerTxActive, TxSnapshot asyncSnapshot) {}

    static class TxObservingAsyncService {

        @Async
        CompletableFuture<TxSnapshot> observeTxActive() {
            return CompletableFuture.completedFuture(new TxSnapshot(
                    Thread.currentThread().getName(),
                    TransactionSynchronizationManager.isActualTransactionActive()));
        }

        @Async
        @Transactional
        CompletableFuture<TxSnapshot> observeTxActiveInAsyncTransactionalMethod() {
            return CompletableFuture.completedFuture(new TxSnapshot(
                    Thread.currentThread().getName(),
                    TransactionSynchronizationManager.isActualTransactionActive()));
        }
    }

    @EnableAsync
    @EnableTransactionManagement
    @Configuration
    static class AsyncTxConfig {

        @Bean
        TxObservingAsyncService txObservingAsyncService() {
            return new TxObservingAsyncService();
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new ThreadLocalTransactionManager();
        }

        @Bean
        TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
            return new TransactionTemplate(transactionManager);
        }

        @Bean(name = "taskExecutor")
        ThreadPoolTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("async-");
            executor.initialize();
            return executor;
        }
    }
}

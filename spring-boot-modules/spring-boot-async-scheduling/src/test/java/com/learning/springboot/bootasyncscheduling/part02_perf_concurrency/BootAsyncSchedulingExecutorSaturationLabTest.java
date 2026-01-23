package com.learning.springboot.bootasyncscheduling.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class BootAsyncSchedulingExecutorSaturationLabTest {

    @Test
    void executorSaturationRejectsSecondTaskDeterministically() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SaturatedAsyncConfig.class);

        runner.run(context -> {
            BlockingAsyncService service = context.getBean(BlockingAsyncService.class);

            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);

            CompletableFuture<String> first = service.blocking(started, release);
            assertThat(started.await(1, TimeUnit.SECONDS)).isTrue();

            CountDownLatch secondStarted = new CountDownLatch(1);
            Throwable rejectedAtSubmission = catchThrowable(() -> service.blocking(secondStarted, release));

            if (rejectedAtSubmission != null) {
                assertThat(rejectedAtSubmission).isInstanceOf(TaskRejectedException.class);
            } else {
                assertThat(secondStarted.await(200, TimeUnit.MILLISECONDS)).isFalse();
            }

            release.countDown();
            assertThat(first.get(1, TimeUnit.SECONDS)).startsWith("async-sat-");
        });
    }

    static class BlockingAsyncService {

        @Async
        CompletableFuture<String> blocking(CountDownLatch started, CountDownLatch release) {
            started.countDown();

            try {
                release.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CompletableFuture.failedFuture(e);
            }

            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }
    }

    @EnableAsync
    @Configuration
    static class SaturatedAsyncConfig {

        @Bean
        BlockingAsyncService blockingAsyncService() {
            return new BlockingAsyncService();
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(0);
            executor.setThreadNamePrefix("async-sat-");
            executor.initialize();
            return executor;
        }
    }
}


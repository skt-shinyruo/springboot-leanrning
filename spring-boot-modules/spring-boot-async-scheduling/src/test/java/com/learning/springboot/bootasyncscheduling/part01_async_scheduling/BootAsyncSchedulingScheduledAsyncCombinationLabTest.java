package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

class BootAsyncSchedulingScheduledAsyncCombinationLabTest {

    @Test
    void scheduledRunsOnSchedulerThread_butScheduledPlusAsyncRunsOnExecutorThread() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ScheduledPlusAsyncConfig.class);

        runner.run(context -> {
            SchedulingThreadComparisonProbe probe = context.getBean(SchedulingThreadComparisonProbe.class);

            assertThat(probe.awaitScheduled(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.awaitScheduledPlusAsync(1, TimeUnit.SECONDS)).isTrue();

            assertThat(probe.scheduledThreadName()).startsWith("sched-");
            assertThat(probe.scheduledPlusAsyncThreadName()).startsWith("async-");
        });
    }

    static class SchedulingThreadComparisonProbe {

        private final CountDownLatch scheduledLatch = new CountDownLatch(1);
        private final CountDownLatch scheduledPlusAsyncLatch = new CountDownLatch(1);

        private final AtomicReference<String> scheduledThreadNameRef = new AtomicReference<>();
        private final AtomicReference<String> scheduledPlusAsyncThreadNameRef = new AtomicReference<>();

        @Scheduled(fixedDelay = 10)
        void scheduledRunsOnSchedulerThread() {
            scheduledThreadNameRef.set(Thread.currentThread().getName());
            scheduledLatch.countDown();
        }

        @Async
        @Scheduled(fixedDelay = 10)
        void scheduledPlusAsyncRunsOnExecutorThread() {
            scheduledPlusAsyncThreadNameRef.set(Thread.currentThread().getName());
            scheduledPlusAsyncLatch.countDown();
        }

        boolean awaitScheduled(long timeout, TimeUnit unit) throws InterruptedException {
            return scheduledLatch.await(timeout, unit);
        }

        boolean awaitScheduledPlusAsync(long timeout, TimeUnit unit) throws InterruptedException {
            return scheduledPlusAsyncLatch.await(timeout, unit);
        }

        String scheduledThreadName() {
            return scheduledThreadNameRef.get();
        }

        String scheduledPlusAsyncThreadName() {
            return scheduledPlusAsyncThreadNameRef.get();
        }
    }

    @EnableScheduling
    @EnableAsync(proxyTargetClass = true)
    @Configuration
    static class ScheduledPlusAsyncConfig {

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("async-");
            executor.initialize();
            return executor;
        }

        @Bean
        ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadNamePrefix("sched-");
            scheduler.initialize();
            return scheduler;
        }

        @Bean
        SchedulingThreadComparisonProbe schedulingThreadComparisonProbe() {
            return new SchedulingThreadComparisonProbe();
        }
    }
}


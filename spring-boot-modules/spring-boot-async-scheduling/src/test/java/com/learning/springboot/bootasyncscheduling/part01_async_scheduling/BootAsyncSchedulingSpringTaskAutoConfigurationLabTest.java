package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

class BootAsyncSchedulingSpringTaskAutoConfigurationLabTest {

    @Test
    void springTaskExecutionPropertiesConfigureDefaultExecutor_andAsyncUsesIt() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class))
                .withUserConfiguration(EnableAsyncConfig.class)
                .withPropertyValues("spring.task.execution.thread-name-prefix=boot-async-");

        runner.run(context -> {
            BootProvidedAsyncService service = context.getBean(BootProvidedAsyncService.class);
            String threadName = service.threadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("boot-async-");
        });
    }

    @Test
    void springTaskSchedulingPropertiesConfigureTaskScheduler_andScheduledUsesIt() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(TaskSchedulingAutoConfiguration.class))
                .withUserConfiguration(EnableSchedulingConfig.class)
                .withPropertyValues(
                        "spring.task.scheduling.pool.size=1",
                        "spring.task.scheduling.thread-name-prefix=boot-sched-"
                );

        runner.run(context -> {
            ScheduledProbe probe = context.getBean(ScheduledProbe.class);
            assertThat(probe.latch.await(2, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.threadName).startsWith("boot-sched-");
        });
    }

    static class BootProvidedAsyncService {

        @Async
        CompletableFuture<String> threadName() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }
    }

    static class ScheduledProbe {
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile String threadName;

        @Scheduled(fixedDelay = 10)
        void tick() {
            if (latch.getCount() == 0) {
                return;
            }

            threadName = Thread.currentThread().getName();
            latch.countDown();
        }
    }

    @EnableAsync
    @Configuration
    static class EnableAsyncConfig {

        @Bean
        BootProvidedAsyncService bootProvidedAsyncService() {
            return new BootProvidedAsyncService();
        }
    }

    @EnableScheduling
    @Configuration
    static class EnableSchedulingConfig {

        @Bean
        ScheduledProbe scheduledProbe() {
            return new ScheduledProbe();
        }
    }
}


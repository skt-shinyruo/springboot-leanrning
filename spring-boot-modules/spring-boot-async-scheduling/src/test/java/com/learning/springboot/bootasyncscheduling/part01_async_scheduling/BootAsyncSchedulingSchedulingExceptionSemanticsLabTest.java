package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootasyncscheduling.testsupport.Waiter;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

class BootAsyncSchedulingSchedulingExceptionSemanticsLabTest {

    @Test
    void scheduledExceptionsAreHandledByErrorHandler_andTaskContinues() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SchedulingWithErrorHandlerConfig.class);

        runner.run(context -> {
            ThrowingScheduledProbe probe = context.getBean(ThrowingScheduledProbe.class);
            CollectingErrorHandler handler = context.getBean(CollectingErrorHandler.class);

            Waiter.await("scheduled task 至少触发 2 次", Duration.ofSeconds(1), Duration.ofMillis(10),
                    () -> probe.invocationCount() >= 2);

            Waiter.await("error handler 至少收到 2 次异常", Duration.ofSeconds(1), Duration.ofMillis(10),
                    () -> handler.size() >= 2);

            assertThat(handler.exceptions())
                    .allSatisfy(ex -> assertThat(ex).hasMessage("boom_scheduled"));
        });
    }

    static class CollectingErrorHandler implements ErrorHandler {

        private final CopyOnWriteArrayList<Throwable> exceptions = new CopyOnWriteArrayList<>();

        @Override
        public void handleError(Throwable t) {
            exceptions.add(t);
        }

        int size() {
            return exceptions.size();
        }

        CopyOnWriteArrayList<Throwable> exceptions() {
            return exceptions;
        }
    }

    static class ThrowingScheduledProbe {

        private final AtomicInteger count = new AtomicInteger();

        @Scheduled(fixedDelay = 10)
        void tick() {
            count.incrementAndGet();
            throw new IllegalStateException("boom_scheduled");
        }

        int invocationCount() {
            return count.get();
        }
    }

    @EnableScheduling
    @Configuration
    static class SchedulingWithErrorHandlerConfig {

        @Bean
        CollectingErrorHandler collectingErrorHandler() {
            return new CollectingErrorHandler();
        }

        @Bean
        ThreadPoolTaskScheduler taskScheduler(CollectingErrorHandler handler) {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadNamePrefix("sched-");
            scheduler.setErrorHandler(handler);
            scheduler.initialize();
            return scheduler;
        }

        @Bean
        ThrowingScheduledProbe throwingScheduledProbe() {
            return new ThrowingScheduledProbe();
        }
    }
}


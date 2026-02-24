package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class BootAsyncSchedulingContextPropagationLabTest {

    @Test
    void threadLocalContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(NoDecoratorConfig.class);

        runner.run(context -> {
            ContextReadingAsyncService service = context.getBean(ContextReadingAsyncService.class);

            ThreadLocalContext.set("trace-A");
            try {
                Observation observation = service.observe().get(1, TimeUnit.SECONDS);
                assertThat(observation.threadName()).startsWith("async-");
                assertThat(observation.contextValue()).isNull();
            } finally {
                ThreadLocalContext.clear();
            }
        });
    }

    @Test
    void taskDecoratorCanPropagateThreadLocalContext_andRestoreToAvoidLeaks() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CopyingDecoratorConfig.class);

        runner.run(context -> {
            ContextReadingAsyncService service = context.getBean(ContextReadingAsyncService.class);

            ThreadLocalContext.set("trace-A");
            Observation first = service.observe().get(1, TimeUnit.SECONDS);
            assertThat(first.threadName()).startsWith("async-");
            assertThat(first.contextValue()).isEqualTo("trace-A");

            ThreadLocalContext.clear();
            Observation second = service.observe().get(1, TimeUnit.SECONDS);
            assertThat(second.threadName()).startsWith("async-");
            assertThat(second.contextValue()).isNull();
        });
    }

    @Test
    void buggyTaskDecoratorThatSkipsNullCanLeakPreviousThreadLocalValueAcrossTasks() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(BuggyDecoratorConfig.class);

        runner.run(context -> {
            ContextReadingAsyncService service = context.getBean(ContextReadingAsyncService.class);

            ThreadLocalContext.set("trace-A");
            try {
                Observation first = service.observe().get(1, TimeUnit.SECONDS);
                assertThat(first.threadName()).startsWith("async-");
                assertThat(first.contextValue()).isEqualTo("trace-A");
            } finally {
                ThreadLocalContext.clear();
            }

            Observation second = service.observe().get(1, TimeUnit.SECONDS);
            assertThat(second.threadName()).startsWith("async-");
            assertThat(second.contextValue()).isEqualTo("trace-A");
        });
    }

    record Observation(String threadName, String contextValue) {
    }

    static class ThreadLocalContext {
        private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

        static void set(String value) {
            TRACE_ID.set(value);
        }

        static String get() {
            return TRACE_ID.get();
        }

        static void clear() {
            TRACE_ID.remove();
        }
    }

    static class ContextReadingAsyncService {

        @Async
        CompletableFuture<Observation> observe() {
            return CompletableFuture.completedFuture(
                    new Observation(Thread.currentThread().getName(), ThreadLocalContext.get()));
        }
    }

    @EnableAsync
    @Configuration
    static class NoDecoratorConfig {

        @Bean
        ContextReadingAsyncService contextReadingAsyncService() {
            return new ContextReadingAsyncService();
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            return executor("async-", null);
        }
    }

    @EnableAsync
    @Configuration
    static class CopyingDecoratorConfig {

        @Bean
        ContextReadingAsyncService contextReadingAsyncService() {
            return new ContextReadingAsyncService();
        }

        @Bean
        TaskDecorator copyingTaskDecorator() {
            return runnable -> {
                String captured = ThreadLocalContext.get();
                return () -> {
                    String previous = ThreadLocalContext.get();
                    ThreadLocalContext.set(captured);
                    try {
                        runnable.run();
                    } finally {
                        if (previous == null) {
                            ThreadLocalContext.clear();
                        } else {
                            ThreadLocalContext.set(previous);
                        }
                    }
                };
            };
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor(TaskDecorator copyingTaskDecorator) {
            return executor("async-", copyingTaskDecorator);
        }
    }

    @EnableAsync
    @Configuration
    static class BuggyDecoratorConfig {

        @Bean
        ContextReadingAsyncService contextReadingAsyncService() {
            return new ContextReadingAsyncService();
        }

        @Bean
        TaskDecorator buggyDecorator() {
            return runnable -> {
                String captured = ThreadLocalContext.get();
                if (captured == null) {
                    return runnable;
                }

                return () -> {
                    ThreadLocalContext.set(captured);
                    runnable.run();
                };
            };
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor(TaskDecorator buggyDecorator) {
            return executor("async-", buggyDecorator);
        }
    }

    private static TaskExecutor executor(String threadNamePrefix, TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setTaskDecorator(taskDecorator);
        executor.initialize();
        return executor;
    }
}


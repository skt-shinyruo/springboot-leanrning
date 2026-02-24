package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class BootAsyncSchedulingUncaughtExceptionHandlerLabTest {

    @Test
    void voidAsyncExceptions_areDeliveredToUncaughtExceptionHandlerWithMethodAndArgs() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(UncaughtHandlerConfig.class);

        runner.run(context -> {
            VoidFailureAsyncService service = context.getBean(VoidFailureAsyncService.class);
            CollectingUncaughtExceptionHandler handler = context.getBean(CollectingUncaughtExceptionHandler.class);

            service.failsAsVoid("p1");

            assertThat(handler.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(handler.method().getName()).isEqualTo("failsAsVoid");
            assertThat(handler.args()).containsExactly("p1");
            assertThat(handler.exception()).hasMessage("boom_void:p1");
        });
    }

    static class CollectingUncaughtExceptionHandler implements org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        private final AtomicReference<Method> methodRef = new AtomicReference<>();
        private final AtomicReference<Object[]> argsRef = new AtomicReference<>();

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            exceptionRef.set(ex);
            methodRef.set(method);
            argsRef.set(params);
            latch.countDown();
        }

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        Throwable exception() {
            return exceptionRef.get();
        }

        Method method() {
            return methodRef.get();
        }

        Object[] args() {
            return argsRef.get();
        }
    }

    @EnableAsync
    @Configuration
    static class UncaughtHandlerConfig implements AsyncConfigurer {

        @Bean
        VoidFailureAsyncService voidFailureAsyncService() {
            return new VoidFailureAsyncService();
        }

        @Bean
        CollectingUncaughtExceptionHandler collectingUncaughtExceptionHandler() {
            return new CollectingUncaughtExceptionHandler();
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            return threadPoolExecutor("uncaught-async-");
        }

        @Override
        public Executor getAsyncExecutor() {
            return taskExecutor();
        }

        @Override
        public org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return collectingUncaughtExceptionHandler();
        }
    }

    private static TaskExecutor threadPoolExecutor(String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}


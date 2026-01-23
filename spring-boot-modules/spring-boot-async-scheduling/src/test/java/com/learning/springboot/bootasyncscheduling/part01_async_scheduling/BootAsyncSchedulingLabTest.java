package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class BootAsyncSchedulingLabTest {

    @Test
    void asyncAnnotationDoesNothingWithoutEnableAsync() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(NoAsyncConfig.class);

        runner.run(context -> {
            AsyncDemoService service = context.getBean(AsyncDemoService.class);
            assertThat(AopUtils.isAopProxy(service)).isFalse();

            String threadName = service.currentThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).isEqualTo(Thread.currentThread().getName());
        });
    }

    @Test
    void asyncRunsOnExecutorThreadWhenEnableAsyncPresent() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledConfig.class);

        runner.run(context -> {
            AsyncDemoService service = context.getBean(AsyncDemoService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();

            String threadName = service.currentThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("async-");
        });
    }

    @Test
    void asyncVoidMethodExecutesOnExecutorThread() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledConfig.class);

        runner.run(context -> {
            AsyncDemoService service = context.getBean(AsyncDemoService.class);

            CountDownLatch latch = new CountDownLatch(1);
            List<String> threadNames = new CopyOnWriteArrayList<>();

            service.runAsync(() -> {
                threadNames.add(Thread.currentThread().getName());
                latch.countDown();
            });

            assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(threadNames).singleElement().satisfies(name -> assertThat(name).startsWith("async-"));
        });
    }

    @Test
    void asyncExceptionsPropagateThroughFuture() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledConfig.class);

        runner.run(context -> {
            AsyncDemoService service = context.getBean(AsyncDemoService.class);
            CompletableFuture<String> future = service.failsAsFuture();

            assertThatThrownBy(() -> future.get(1, TimeUnit.SECONDS))
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("boom_future");
        });
    }

    @Test
    void asyncExceptionsFromVoidAreHandledByAsyncUncaughtExceptionHandler() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledWithExceptionHandlerConfig.class);

        runner.run(context -> {
            AsyncDemoService service = context.getBean(AsyncDemoService.class);
            CollectingAsyncExceptionHandler handler = context.getBean(CollectingAsyncExceptionHandler.class);

            service.failsAsVoid();

            assertThat(handler.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(handler.exceptions())
                    .anySatisfy(ex -> assertThat(ex).hasMessage("boom_void"));
        });
    }

    @Test
    void selfInvocationBypassesAsyncAsAPitfall() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledConfig.class);

        runner.run(context -> {
            SelfInvocationAsyncService service = context.getBean(SelfInvocationAsyncService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();

            String outerThread = service.outerCallsAsyncMethod().get(1, TimeUnit.SECONDS);
            assertThat(outerThread).isEqualTo(Thread.currentThread().getName());
        });
    }

    @Test
    void callingAsyncThroughAnotherBeanGoesThroughProxy() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledConfig.class);

        runner.run(context -> {
            AsyncCallerService caller = context.getBean(AsyncCallerService.class);
            String threadName = caller.callsAsyncThroughProxy().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("async-");
        });
    }

    @Test
    void schedulingRequiresEnableScheduling() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ScheduledProbeConfigWithoutEnable.class);

        runner.run(context -> {
            ScheduledProbe probe = context.getBean(ScheduledProbe.class);
            assertThat(probe.await(200, TimeUnit.MILLISECONDS)).isFalse();
        });
    }

    @Test
    void schedulingTriggersTaskWhenEnableSchedulingPresent() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ScheduledProbeConfig.class);

        runner.run(context -> {
            ScheduledProbe probe = context.getBean(ScheduledProbe.class);
            assertThat(probe.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.invocationCount()).isGreaterThanOrEqualTo(1);
        });
    }

    @Test
    void executorThreadNamePrefixIsAStableObservationPoint() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncEnabledConfig.class);

        runner.run(context -> {
            AsyncDemoService service = context.getBean(AsyncDemoService.class);
            String threadName = service.currentThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("async-");
        });
    }

    @Configuration
    static class NoAsyncConfig {

        @Bean
        AsyncDemoService asyncDemoService() {
            return new AsyncDemoService();
        }
    }

    @EnableAsync
    @Configuration
    static class AsyncEnabledConfig {

        @Bean
        AsyncDemoService asyncDemoService() {
            return new AsyncDemoService();
        }

        @Bean
        AsyncCallerService asyncCallerService(AsyncDemoService asyncDemoService) {
            return new AsyncCallerService(asyncDemoService);
        }

        @Bean
        SelfInvocationAsyncService selfInvocationAsyncService() {
            return new SelfInvocationAsyncService();
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(2);
            executor.setMaxPoolSize(2);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("async-");
            executor.initialize();
            return executor;
        }
    }

    @EnableAsync
    @Configuration
    static class AsyncEnabledWithExceptionHandlerConfig implements AsyncConfigurer {

        @Bean
        AsyncDemoService asyncDemoService() {
            return new AsyncDemoService();
        }

        @Bean
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
        CollectingAsyncExceptionHandler collectingAsyncExceptionHandler() {
            return new CollectingAsyncExceptionHandler();
        }

        @Override
        public Executor getAsyncExecutor() {
            return taskExecutor();
        }

        @Override
        public org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return collectingAsyncExceptionHandler();
        }
    }

    static class CollectingAsyncExceptionHandler implements org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final List<Throwable> exceptions = new CopyOnWriteArrayList<>();

        @Override
        public void handleUncaughtException(Throwable ex, java.lang.reflect.Method method, Object... params) {
            exceptions.add(ex);
            latch.countDown();
        }

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        List<Throwable> exceptions() {
            return exceptions;
        }
    }

    @Configuration
    static class ScheduledProbeConfigWithoutEnable {

        @Bean
        ScheduledProbe scheduledProbe() {
            return new ScheduledProbe();
        }
    }

    @EnableScheduling
    @Configuration
    static class ScheduledProbeConfig {

        @Bean
        ScheduledProbe scheduledProbe() {
            return new ScheduledProbe();
        }
    }

    static class ScheduledProbe {

        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile int count;

        @Scheduled(fixedDelay = 10)
        void tick() {
            count += 1;
            latch.countDown();
        }

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        int invocationCount() {
            return count;
        }
    }
}

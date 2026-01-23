package com.learning.springboot.bootasyncscheduling.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.springboot.bootasyncscheduling.testsupport.Waiter;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 参考实现：对齐 BootAsyncSchedulingExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class BootAsyncSchedulingExerciseSolutionTest {

    @Test
    void solution_customExecutor() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CustomExecutorConfig.class);

        runner.run(context -> {
            ThreadNameService service = context.getBean(ThreadNameService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();

            String threadName = service.currentThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("custom-async-");
        });
    }

    @Test
    void solution_futureTypes_futureAndCompletableFutureBothWork() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CustomExecutorConfig.class);

        runner.run(context -> {
            ThreadNameService service = context.getBean(ThreadNameService.class);

            String futureThread = service.currentThreadNameAsFuture().get(1, TimeUnit.SECONDS);
            assertThat(futureThread).startsWith("custom-async-");

            String completableThread = service.currentThreadName().get(1, TimeUnit.SECONDS);
            assertThat(completableThread).startsWith("custom-async-");
        });
    }

    @Test
    void solution_executorSaturation_rejectedExecutionIsDeterministic() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SaturatedExecutorConfig.class);

        runner.run(context -> {
            BlockingService service = context.getBean(BlockingService.class);

            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);

            CompletableFuture<String> first = service.blocking(started, release);
            assertThat(started.await(1, TimeUnit.SECONDS)).isTrue();

            CountDownLatch secondStarted = new CountDownLatch(1);
            assertThatThrownBy(() -> service.blocking(secondStarted, release))
                    .isInstanceOf(TaskRejectedException.class);

            release.countDown();
            assertThat(first.get(1, TimeUnit.SECONDS)).startsWith("sat-async-");
        });
    }

    @Test
    void solution_selfInvocationInChain_bypassesAsync() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SelfInvocationConfig.class);

        runner.run(context -> {
            SelfInvocationService service = context.getBean(SelfInvocationService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();

            String threadName = service.outerCallsInnerAsync().get(1, TimeUnit.SECONDS);
            assertThat(threadName).isEqualTo(Thread.currentThread().getName());
        });
    }

    @Test
    void solution_fixedRateVsFixedDelayAndCron_areRegisteredAsDifferentTaskTypes() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SchedulingConfig.class);

        runner.run(context -> {
            ScheduledTaskHolder holder = context.getBean(ScheduledTaskHolder.class);
            Set<ScheduledTask> tasks = holder.getScheduledTasks();

            assertThat(tasks).isNotEmpty();

            assertThat(tasks).anySatisfy(task -> assertThat(task.getTask()).isInstanceOf(FixedRateTask.class));
            assertThat(tasks).anySatisfy(task -> assertThat(task.getTask()).isInstanceOf(FixedDelayTask.class));
            assertThat(tasks).anySatisfy(task -> assertThat(task.getTask()).isInstanceOf(CronTask.class));
        });
    }

    @Test
    void solution_uncaughtHandlerDetails_recordsMethodAndArgsForVoidAsyncExceptions() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(UncaughtHandlerConfig.class);

        runner.run(context -> {
            UncaughtDetailsProbe probe = context.getBean(UncaughtDetailsProbe.class);
            FailingVoidService service = context.getBean(FailingVoidService.class);

            service.failsAsVoid("p1");

            assertThat(probe.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.methodName()).isEqualTo("failsAsVoid");
            assertThat(probe.args()).containsExactly("p1");
            assertThat(probe.exception()).hasMessage("boom_void");
        });
    }

    @Test
    void solution_waitingUtility_canReplaceThreadSleepInAsyncAssertions() {
        AtomicInteger counter = new AtomicInteger();

        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter.incrementAndGet();
        }, "waiter-demo").start();

        Waiter.await("counter 变为 1", java.time.Duration.ofSeconds(1), java.time.Duration.ofMillis(10), () -> counter.get() == 1);
        assertThat(counter.get()).isEqualTo(1);
    }

    @EnableAsync
    @Configuration
    static class CustomExecutorConfig {

        @Bean
        ThreadNameService threadNameService() {
            return new ThreadNameService();
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(2);
            executor.setMaxPoolSize(2);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("custom-async-");
            executor.initialize();
            return executor;
        }
    }

    static class ThreadNameService {

        @Async
        CompletableFuture<String> currentThreadName() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }

        @Async
        Future<String> currentThreadNameAsFuture() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }
    }

    @EnableAsync
    @Configuration
    static class SaturatedExecutorConfig {

        @Bean
        BlockingService blockingService() {
            return new BlockingService();
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(0);
            executor.setThreadNamePrefix("sat-async-");
            executor.initialize();
            return executor;
        }
    }

    static class BlockingService {

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
    static class SelfInvocationConfig {

        @Bean
        SelfInvocationService selfInvocationService() {
            return new SelfInvocationService();
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("selfinv-async-");
            executor.initialize();
            return executor;
        }
    }

    static class SelfInvocationService {

        CompletableFuture<String> outerCallsInnerAsync() {
            return innerAsync();
        }

        @Async
        CompletableFuture<String> innerAsync() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }
    }

    @EnableScheduling
    @Configuration
    static class SchedulingConfig {

        @Bean
        ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadNamePrefix("sched-");
            scheduler.initialize();
            return scheduler;
        }

        @Bean
        SchedulingProbe schedulingProbe() {
            return new SchedulingProbe();
        }
    }

    static class SchedulingProbe {

        @Scheduled(fixedRate = 1000)
        void fixedRateTask() {
        }

        @Scheduled(fixedDelay = 1000)
        void fixedDelayTask() {
        }

        @Scheduled(cron = "*/5 * * * * *")
        void cronTask() {
        }
    }

    @EnableAsync
    @Configuration
    static class UncaughtHandlerConfig implements AsyncConfigurer {

        @Bean
        UncaughtDetailsProbe uncaughtDetailsProbe() {
            return new UncaughtDetailsProbe();
        }

        @Bean
        FailingVoidService failingVoidService() {
            return new FailingVoidService();
        }

        @Bean
        TaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("uncaught-async-");
            executor.initialize();
            return executor;
        }

        @Override
        public java.util.concurrent.Executor getAsyncExecutor() {
            return taskExecutor();
        }

        @Override
        public org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (ex, method, params) -> {
                UncaughtDetailsProbe probe = uncaughtDetailsProbe();
                probe.record(ex, method.getName(), params);
            };
        }
    }

    static class UncaughtDetailsProbe {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        private final AtomicReference<String> methodNameRef = new AtomicReference<>();
        private final AtomicReference<Object[]> argsRef = new AtomicReference<>();

        void record(Throwable ex, String methodName, Object[] args) {
            exceptionRef.set(ex);
            methodNameRef.set(methodName);
            argsRef.set(args);
            latch.countDown();
        }

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        Throwable exception() {
            return exceptionRef.get();
        }

        String methodName() {
            return methodNameRef.get();
        }

        Object[] args() {
            return argsRef.get();
        }
    }

    static class FailingVoidService {

        @Async
        void failsAsVoid(String arg) {
            throw new IllegalStateException("boom_void");
        }
    }
}


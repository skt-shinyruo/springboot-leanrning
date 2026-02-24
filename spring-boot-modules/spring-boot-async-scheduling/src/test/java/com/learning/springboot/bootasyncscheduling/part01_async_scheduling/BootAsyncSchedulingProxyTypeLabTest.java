package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class BootAsyncSchedulingProxyTypeLabTest {

    @Test
    void jdkProxyIsUsedWhenProxyTargetClassFalseAndInterfacePresent() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(JdkProxyConfig.class);

        runner.run(context -> {
            ThreadNamePort port = context.getBean(ThreadNamePort.class);
            assertThat(AopUtils.isAopProxy(port)).isTrue();
            assertThat(AopUtils.isJdkDynamicProxy(port)).isTrue();

            String threadName = port.threadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("async-");
        });
    }

    @Test
    void cglibProxyIsUsedWhenProxyTargetClassTrue() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CglibProxyConfig.class);

        runner.run(context -> {
            InterfaceBasedAsyncService service = context.getBean(InterfaceBasedAsyncService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();
            assertThat(AopUtils.isCglibProxy(service)).isTrue();

            String threadName = service.threadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("async-");
        });
    }

    @Test
    void cglibCannotInterceptFinalMethods_asyncIsBypassed() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CglibFinalMethodConfig.class);

        runner.run(context -> {
            FinalMethodAsyncService service = context.getBean(FinalMethodAsyncService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();
            assertThat(AopUtils.isCglibProxy(service)).isTrue();

            String threadName = service.finalThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).isEqualTo(Thread.currentThread().getName());
        });
    }

    interface ThreadNamePort {
        CompletableFuture<String> threadName();
    }

    static class InterfaceBasedAsyncService implements ThreadNamePort {

        @Override
        @Async
        public CompletableFuture<String> threadName() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }
    }

    static class FinalMethodAsyncService {

        @Async
        final CompletableFuture<String> finalThreadName() {
            return CompletableFuture.completedFuture(Thread.currentThread().getName());
        }
    }

    @EnableAsync(proxyTargetClass = false)
    @Configuration
    static class JdkProxyConfig {

        @Bean
        ThreadNamePort threadNamePort() {
            return new InterfaceBasedAsyncService();
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            return threadPoolExecutor("async-");
        }
    }

    @EnableAsync(proxyTargetClass = true)
    @Configuration
    static class CglibProxyConfig {

        @Bean
        InterfaceBasedAsyncService interfaceBasedAsyncService() {
            return new InterfaceBasedAsyncService();
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            return threadPoolExecutor("async-");
        }
    }

    @EnableAsync(proxyTargetClass = true)
    @Configuration
    static class CglibFinalMethodConfig {

        @Bean
        FinalMethodAsyncService finalMethodAsyncService() {
            return new FinalMethodAsyncService();
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            return threadPoolExecutor("async-");
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


package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

class BootAsyncSchedulingSecurityContextPropagationLabTest {

    @Test
    void securityContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(NoDelegationConfig.class);

        runner.run(context -> {
            SecurityContextReadingAsyncService service = context.getBean(SecurityContextReadingAsyncService.class);

            setAuthentication("alice");
            try {
                AuthObservation observation = service.observeAuthentication().get(1, TimeUnit.SECONDS);
                assertThat(observation.threadName()).startsWith("async-");
                assertThat(observation.authenticationName()).isNull();
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
    }

    @Test
    void delegatingSecurityContextExecutorCanPropagate_andCleansUpToAvoidThreadReuseLeaks() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(DelegatingConfig.class);

        runner.run(context -> {
            SecurityContextReadingAsyncService service = context.getBean(SecurityContextReadingAsyncService.class);

            setAuthentication("alice");
            AuthObservation first = service.observeAuthentication().get(1, TimeUnit.SECONDS);
            assertThat(first.threadName()).startsWith("async-");
            assertThat(first.authenticationName()).isEqualTo("alice");

            SecurityContextHolder.clearContext();
            AuthObservation second = service.observeAuthentication().get(1, TimeUnit.SECONDS);
            assertThat(second.threadName()).startsWith("async-");
            assertThat(second.authenticationName()).isNull();
        });
    }

    record AuthObservation(String threadName, String authenticationName) {}

    static class SecurityContextReadingAsyncService {

        @Async
        CompletableFuture<AuthObservation> observeAuthentication() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication == null ? null : authentication.getName();
            return CompletableFuture.completedFuture(new AuthObservation(Thread.currentThread().getName(), name));
        }
    }

    @EnableAsync
    @Configuration
    static class NoDelegationConfig {

        @Bean
        SecurityContextReadingAsyncService securityContextReadingAsyncService() {
            return new SecurityContextReadingAsyncService();
        }

        @Bean(name = "taskExecutor")
        AsyncTaskExecutor taskExecutor() {
            return singleThreadExecutor("async-", null);
        }
    }

    @EnableAsync
    @Configuration
    static class DelegatingConfig {

        @Bean
        SecurityContextReadingAsyncService securityContextReadingAsyncService() {
            return new SecurityContextReadingAsyncService();
        }

        @Bean(name = "taskExecutor")
        AsyncTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor delegate = singleThreadExecutor("async-", null);
            return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
        }
    }

    private static ThreadPoolTaskExecutor singleThreadExecutor(String threadNamePrefix, String executorBeanName) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setBeanName(executorBeanName);
        executor.initialize();
        return executor;
    }

    private static void setAuthentication(String username) {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(username, "N/A"));
    }
}

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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class BootAsyncSchedulingRequestContextPropagationLabTest {

    @Test
    void requestContextIsNotPropagatedByDefaultAcrossAsyncThreadBoundary() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(NoDecoratorConfig.class);

        runner.run(context -> {
            RequestContextReadingAsyncService service = context.getBean(RequestContextReadingAsyncService.class);

            setRequestId("R-1");
            try {
                RequestObservation observation = service.observeRequestId().get(1, TimeUnit.SECONDS);
                assertThat(observation.threadName()).startsWith("async-");
                assertThat(observation.requestId()).isNull();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    @Test
    void taskDecoratorCanPropagateRequestContext_andRestoreToAvoidLeaks() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CopyingDecoratorConfig.class);

        runner.run(context -> {
            RequestContextReadingAsyncService service = context.getBean(RequestContextReadingAsyncService.class);

            setRequestId("R-1");
            RequestObservation first = service.observeRequestId().get(1, TimeUnit.SECONDS);
            assertThat(first.threadName()).startsWith("async-");
            assertThat(first.requestId()).isEqualTo("R-1");

            RequestContextHolder.resetRequestAttributes();
            RequestObservation second = service.observeRequestId().get(1, TimeUnit.SECONDS);
            assertThat(second.threadName()).startsWith("async-");
            assertThat(second.requestId()).isNull();
        });
    }

    @Test
    void buggyTaskDecoratorThatSkipsNullCanLeakPreviousRequestAttributesAcrossTasks() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(BuggyDecoratorConfig.class);

        runner.run(context -> {
            RequestContextReadingAsyncService service = context.getBean(RequestContextReadingAsyncService.class);

            setRequestId("R-1");
            try {
                RequestObservation first = service.observeRequestId().get(1, TimeUnit.SECONDS);
                assertThat(first.threadName()).startsWith("async-");
                assertThat(first.requestId()).isEqualTo("R-1");
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }

            RequestObservation second = service.observeRequestId().get(1, TimeUnit.SECONDS);
            assertThat(second.threadName()).startsWith("async-");
            assertThat(second.requestId()).isEqualTo("R-1");
        });
    }

    record RequestObservation(String threadName, String requestId) {}

    static class RequestContextReadingAsyncService {

        @Async
        CompletableFuture<RequestObservation> observeRequestId() {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            String requestId = attributes == null ? null : (String) attributes.getAttribute("rid", RequestAttributes.SCOPE_REQUEST);
            return CompletableFuture.completedFuture(new RequestObservation(Thread.currentThread().getName(), requestId));
        }
    }

    @EnableAsync
    @Configuration
    static class NoDecoratorConfig {

        @Bean
        RequestContextReadingAsyncService requestContextReadingAsyncService() {
            return new RequestContextReadingAsyncService();
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
        RequestContextReadingAsyncService requestContextReadingAsyncService() {
            return new RequestContextReadingAsyncService();
        }

        @Bean
        TaskDecorator requestAttributesCopyingDecorator() {
            return runnable -> {
                RequestAttributes captured = RequestContextHolder.getRequestAttributes();
                return () -> {
                    RequestAttributes previous = RequestContextHolder.getRequestAttributes();
                    if (captured != null) {
                        RequestContextHolder.setRequestAttributes(captured);
                    }

                    try {
                        runnable.run();
                    } finally {
                        if (previous != null) {
                            RequestContextHolder.setRequestAttributes(previous);
                        } else {
                            RequestContextHolder.resetRequestAttributes();
                        }
                    }
                };
            };
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor(TaskDecorator requestAttributesCopyingDecorator) {
            return executor("async-", requestAttributesCopyingDecorator);
        }
    }

    @EnableAsync
    @Configuration
    static class BuggyDecoratorConfig {

        @Bean
        RequestContextReadingAsyncService requestContextReadingAsyncService() {
            return new RequestContextReadingAsyncService();
        }

        @Bean
        TaskDecorator buggyDecorator() {
            return runnable -> {
                RequestAttributes captured = RequestContextHolder.getRequestAttributes();
                if (captured == null) {
                    return runnable;
                }
                return () -> {
                    RequestContextHolder.setRequestAttributes(captured);
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

    private static void setRequestId(String requestId) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        attributes.setAttribute("rid", requestId, RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.setRequestAttributes(attributes);
    }
}


package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class BootAsyncSchedulingExecutorSelectionLabTest {

    @Test
    void whenSingleTaskExecutorBeanExists_itIsUsedAsDefaultAsyncExecutor() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SingleExecutorConfig.class);

        runner.run(context -> {
            ExecutorSelectionAsyncService service = context.getBean(ExecutorSelectionAsyncService.class);

            String threadName = service.defaultExecutorThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("only-");
        });
    }

    @Test
    void whenMultipleExecutorsExist_namedTaskExecutorWinsAsDefault() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(MultipleExecutorsConfig.class);

        runner.run(context -> {
            ExecutorSelectionAsyncService service = context.getBean(ExecutorSelectionAsyncService.class);

            String threadName = service.defaultExecutorThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("default-");
        });
    }

    @Test
    void asyncValueSelectsQualifiedExecutorByName() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(MultipleExecutorsConfig.class);

        runner.run(context -> {
            ExecutorSelectionAsyncService service = context.getBean(ExecutorSelectionAsyncService.class);

            String threadName = service.specialExecutorThreadName().get(1, TimeUnit.SECONDS);
            assertThat(threadName).startsWith("special-");
        });
    }

    @Test
    void asyncConfigurerOverridesDefaultExecutorSelection_butQualifiedExecutorStillWorks() throws Exception {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(AsyncConfigurerOverridesDefaultConfig.class);

        runner.run(context -> {
            ExecutorSelectionAsyncService service = context.getBean(ExecutorSelectionAsyncService.class);

            String defaultThreadName = service.defaultExecutorThreadName().get(1, TimeUnit.SECONDS);
            assertThat(defaultThreadName).startsWith("configurer-");

            String qualifiedThreadName = service.specialExecutorThreadName().get(1, TimeUnit.SECONDS);
            assertThat(qualifiedThreadName).startsWith("special-");
        });
    }

    @EnableAsync
    @Configuration
    static class SingleExecutorConfig {

        @Bean
        ExecutorSelectionAsyncService executorSelectionAsyncService() {
            return new ExecutorSelectionAsyncService();
        }

        @Bean
        TaskExecutor onlyExecutor() {
            return threadPoolExecutor("only-");
        }
    }

    @EnableAsync
    @Configuration
    static class MultipleExecutorsConfig {

        @Bean
        ExecutorSelectionAsyncService executorSelectionAsyncService() {
            return new ExecutorSelectionAsyncService();
        }

        @Bean(name = "taskExecutor")
        TaskExecutor taskExecutor() {
            return threadPoolExecutor("default-");
        }

        @Bean
        TaskExecutor otherExecutor() {
            return threadPoolExecutor("other-");
        }

        @Bean(name = "specialExecutor")
        TaskExecutor specialExecutor() {
            return threadPoolExecutor("special-");
        }
    }

    @EnableAsync
    @Configuration
    static class AsyncConfigurerOverridesDefaultConfig implements AsyncConfigurer {

        @Bean
        ExecutorSelectionAsyncService executorSelectionAsyncService() {
            return new ExecutorSelectionAsyncService();
        }

        @Bean(name = "specialExecutor")
        TaskExecutor specialExecutor() {
            return threadPoolExecutor("special-");
        }

        @Bean
        TaskExecutor configuredDefaultExecutor() {
            return threadPoolExecutor("configurer-");
        }

        @Override
        public Executor getAsyncExecutor() {
            return configuredDefaultExecutor();
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

package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

/*
 * 本实验对照 JSR-330（jakarta.inject）与 Spring 注入体系：@Inject/@Named/Provider<T> vs @Autowired/@Qualifier/ObjectProvider<T>。
 * 目标：用最小可断言用例展示 provider 的延迟解析、按名限定，以及“直接注入 vs provider 注入”的时机差异。
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

class SpringCoreBeansJsr330InjectionLabTest {

    @Test
    void providerInjection_isLazyUntilGet_forJsr330AndSpring() {
        AtomicInteger fastCreated = new AtomicInteger();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ProviderInjectionConfig.class);
            ProviderInjectionConfig.fastCreated = fastCreated;
            context.refresh();

            assertThat(fastCreated.get()).as("provider 注入不应触发目标 bean 的提前创建").isZero();

            Jsr330ProviderConsumer jsrConsumer = context.getBean(Jsr330ProviderConsumer.class);
            SpringObjectProviderConsumer springConsumer = context.getBean(SpringObjectProviderConsumer.class);

            assertThat(fastCreated.get()).as("仅获取 consumer bean 不应触发 lazy 目标 bean 创建").isZero();

            assertThat(jsrConsumer.getWorkerId()).isEqualTo("fast");
            assertThat(fastCreated.get()).as("第一次 get() 才触发 lazy 目标 bean 创建").isEqualTo(1);

            assertThat(springConsumer.getWorkerId()).isEqualTo("fast");
            assertThat(fastCreated.get()).as("同一个 singleton 只创建一次").isEqualTo(1);
        }
    }

    @Test
    void directInjection_isEagerDuringRefresh_forJsr330Inject() {
        AtomicInteger fastCreated = new AtomicInteger();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(DirectInjectionConfig.class);
            DirectInjectionConfig.fastCreated = fastCreated;
            context.refresh();

            assertThat(fastCreated.get())
                    .as("直接注入需要立即解析依赖，因此会在 refresh/创建 consumer 期间触发目标 bean 创建")
                    .isEqualTo(1);

            DirectInjectConsumer consumer = context.getBean(DirectInjectConsumer.class);
            assertThat(consumer.workerId()).isEqualTo("fast");
        }
    }

    interface Worker {
        String id();
    }

    @Configuration
    static class ProviderInjectionConfig {
        static AtomicInteger fastCreated = new AtomicInteger();

        @Bean
        @Lazy
        Worker fastWorker() {
            fastCreated.incrementAndGet();
            return () -> "fast";
        }

        @Bean
        @Lazy
        Worker slowWorker() {
            return () -> "slow";
        }

        @Bean
        Jsr330ProviderConsumer jsr330ProviderConsumer() {
            return new Jsr330ProviderConsumer();
        }

        @Bean
        SpringObjectProviderConsumer springObjectProviderConsumer() {
            return new SpringObjectProviderConsumer();
        }
    }

    @Configuration
    static class DirectInjectionConfig {
        static AtomicInteger fastCreated = new AtomicInteger();

        @Bean
        @Lazy
        Worker fastWorker() {
            fastCreated.incrementAndGet();
            return () -> "fast";
        }

        @Bean
        @Lazy
        Worker slowWorker() {
            return () -> "slow";
        }

        @Bean
        DirectInjectConsumer directInjectConsumer() {
            return new DirectInjectConsumer();
        }
    }

    static class Jsr330ProviderConsumer {
        @Inject
        @Named("fastWorker")
        private Provider<Worker> workerProvider;

        String getWorkerId() {
            return workerProvider.get().id();
        }
    }

    static class SpringObjectProviderConsumer {
        @Autowired
        @Qualifier("fastWorker")
        private ObjectProvider<Worker> workerProvider;

        String getWorkerId() {
            return workerProvider.getObject().id();
        }
    }

    static class DirectInjectConsumer {
        @Inject
        @Named("fastWorker")
        private Worker worker;

        String workerId() {
            return worker.id();
        }
    }
}


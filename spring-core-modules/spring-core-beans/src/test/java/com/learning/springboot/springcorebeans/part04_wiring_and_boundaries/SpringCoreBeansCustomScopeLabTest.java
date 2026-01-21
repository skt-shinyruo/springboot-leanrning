package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansCustomScopeLabTest {

    private static final AtomicLong sequence = new AtomicLong();

    @Test
    void threadScope_createsOneInstancePerThread_whenAccessedDirectly() throws Exception {
        sequence.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DirectThreadScopeConfiguration.class)) {
            Observation o1 = runInThread(() -> context.getBean(ThreadScopedCounter.class).id());
            Observation o2 = runInThread(() -> context.getBean(ThreadScopedCounter.class).id());

            System.out.println("OBSERVE: custom scope 'thread' => one instance per thread");
            assertThat(o1.first()).isEqualTo(o1.second());
            assertThat(o2.first()).isEqualTo(o2.second());
            assertThat(o1.first()).isNotEqualTo(o2.first());
        }
    }

    @Test
    void injectingThreadScopedBeanIntoSingleton_withoutProxy_freezesTheTargetAtInjectionTime() throws Exception {
        sequence.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DirectThreadScopeConfiguration.class)) {
            DirectConsumer consumer = context.getBean(DirectConsumer.class);

            Observation o1 = runInThread(consumer::currentId);
            Observation o2 = runInThread(consumer::currentId);

            System.out.println("OBSERVE: injecting a scoped bean into a singleton without proxy => same instance across threads");
            assertThat(o1.first()).isEqualTo(o1.second());
            assertThat(o2.first()).isEqualTo(o2.second());
            assertThat(o1.first()).isEqualTo(o2.first());
        }
    }

    @Test
    void objectProvider_honorsThreadScope_whenUsedInsideSingleton() throws Exception {
        sequence.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DirectThreadScopeConfiguration.class)) {
            ProviderConsumer consumer = context.getBean(ProviderConsumer.class);

            Observation o1 = runInThread(consumer::currentId);
            Observation o2 = runInThread(consumer::currentId);

            System.out.println("OBSERVE: ObjectProvider defers lookup => each thread resolves its own scoped instance");
            assertThat(o1.first()).isEqualTo(o1.second());
            assertThat(o2.first()).isEqualTo(o2.second());
            assertThat(o1.first()).isNotEqualTo(o2.first());
        }
    }

    @Test
    void scopedProxy_honorsThreadScope_whenInjectedIntoSingleton() throws Exception {
        sequence.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProxyThreadScopeConfiguration.class)) {
            DirectConsumer consumer = context.getBean(DirectConsumer.class);

            Observation o1 = runInThread(consumer::currentId);
            Observation o2 = runInThread(consumer::currentId);

            System.out.println("OBSERVE: scoped proxy routes each call to the current thread's scoped target");
            assertThat(o1.first()).isEqualTo(o1.second());
            assertThat(o2.first()).isEqualTo(o2.second());
            assertThat(o1.first()).isNotEqualTo(o2.first());
        }
    }

    private static Observation runInThread(Callable<Long> task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try {
            Future<Observation> future = executor.submit(() -> {
                long first = task.call();
                long second = task.call();
                return new Observation(first, second);
            });
            return future.get(5, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    record Observation(long first, long second) {
    }

    static class ThreadScopedCounter {
        private final long id;

        ThreadScopedCounter() {
            this.id = sequence.incrementAndGet();
        }

        long id() {
            return id;
        }
    }

    static class DirectConsumer {
        private final ThreadScopedCounter counter;

        DirectConsumer(ThreadScopedCounter counter) {
            this.counter = counter;
        }

        long currentId() {
            return counter.id();
        }
    }

    static class ProviderConsumer {
        private final ObjectProvider<ThreadScopedCounter> counterProvider;

        ProviderConsumer(ObjectProvider<ThreadScopedCounter> counterProvider) {
            this.counterProvider = counterProvider;
        }

        long currentId() {
            return counterProvider.getObject().id();
        }
    }

    @Configuration
    static class DirectThreadScopeConfiguration {

        @Bean
        static BeanFactoryPostProcessor registerThreadScope() {
            return beanFactory -> ((ConfigurableBeanFactory) beanFactory).registerScope("thread", new SimpleThreadScope());
        }

        @Bean
        @Scope("thread")
        ThreadScopedCounter threadScopedCounter() {
            return new ThreadScopedCounter();
        }

        @Bean
        DirectConsumer directConsumer(ThreadScopedCounter counter) {
            return new DirectConsumer(counter);
        }

        @Bean
        ProviderConsumer providerConsumer(ObjectProvider<ThreadScopedCounter> counterProvider) {
            return new ProviderConsumer(counterProvider);
        }
    }

    @Configuration
    static class ProxyThreadScopeConfiguration {

        @Bean
        static BeanFactoryPostProcessor registerThreadScope() {
            return beanFactory -> ((ConfigurableBeanFactory) beanFactory).registerScope("thread", new SimpleThreadScope());
        }

        @Bean
        @Scope(value = "thread", proxyMode = ScopedProxyMode.TARGET_CLASS)
        ThreadScopedCounter threadScopedCounter() {
            return new ThreadScopedCounter();
        }

        @Bean
        DirectConsumer directConsumer(ThreadScopedCounter counter) {
            return new DirectConsumer(counter);
        }
    }
}

package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansCustomScopeLabTest {

    private static final AtomicLong sequence = new AtomicLong();
    private static final AtomicLong prototypeSequence = new AtomicLong();
    private static final AtomicLong destroyed = new AtomicLong();

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

    @Test
    void scopedProxy_registersScopedTargetBeanDefinition_andInterfacesProxyRequiresInterfaceInjection() throws Exception {
        sequence.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProxyThreadScopeInterfacesConfiguration.class)) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
            assertThat(beanFactory.containsBeanDefinition("threadScopedCounter")).isTrue();
            assertThat(beanFactory.containsBeanDefinition("scopedTarget.threadScopedCounter")).isTrue();

            InterfaceConsumer consumer = context.getBean(InterfaceConsumer.class);

            Observation o1 = runInThread(consumer::currentId);
            Observation o2 = runInThread(consumer::currentId);

            System.out.println("OBSERVE: ScopedProxyMode.INTERFACES => JDK proxy; inject by interface, not concrete class");
            System.out.println("OBSERVE: scopedTarget.<beanName> is a real BeanDefinition registered by the container");

            assertThat(o1.first()).isEqualTo(o1.second());
            assertThat(o2.first()).isEqualTo(o2.second());
            assertThat(o1.first()).isNotEqualTo(o2.first());

            ThreadScopedCounter counterByConcreteType = context.getBean(ThreadScopedCounter.class);
            System.out.println("OBSERVE: getBean(ThreadScopedCounter.class) can resolve to scopedTarget.<beanName> (target bean), not the JDK proxy");
            assertThat(Proxy.isProxyClass(counterByConcreteType.getClass())).isFalse();
        }
    }

    @Test
    void prototypeInjectedIntoSingleton_isResolvedOnce_butObjectProviderCanObtainFreshPrototypeEachCall() {
        prototypeSequence.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrototypeInjectionConfiguration.class)) {
            PrototypeDirectConsumer direct = context.getBean(PrototypeDirectConsumer.class);
            PrototypeProviderConsumer provider = context.getBean(PrototypeProviderConsumer.class);

            long directFirst = direct.currentId();
            long directSecond = direct.currentId();

            long providerFirst = provider.currentId();
            long providerSecond = provider.currentId();

            System.out.println("OBSERVE: prototype injected into singleton is resolved once at injection time (frozen reference)");
            System.out.println("OBSERVE: ObjectProvider can defer lookup => each call can obtain a fresh prototype instance");

            assertThat(directFirst).isEqualTo(directSecond);
            assertThat(providerFirst).isNotEqualTo(providerSecond);
        }
    }

    @Test
    void customScope_canTriggerDestructionCallbacks_whenScopeEnds() {
        sequence.set(0);
        destroyed.set(0);
        SwitchableScopeConfiguration.SCOPE.reset();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SwitchableScopeConfiguration.class)) {
            SwitchableScopeConfiguration.SCOPE.setConversationId("A");
            DestroyAwareCounter a1 = context.getBean(DestroyAwareCounter.class);
            DestroyAwareCounter a2 = context.getBean(DestroyAwareCounter.class);
            assertThat(a1.id()).isEqualTo(a2.id());

            SwitchableScopeConfiguration.SCOPE.setConversationId("B");
            DestroyAwareCounter b1 = context.getBean(DestroyAwareCounter.class);
            assertThat(b1.id()).isNotEqualTo(a1.id());

            assertThat(destroyed.get()).isZero();

            SwitchableScopeConfiguration.SCOPE.clearScope("A");
            assertThat(destroyed.get()).isEqualTo(1);

            SwitchableScopeConfiguration.SCOPE.clearScope("B");
            assertThat(destroyed.get()).isEqualTo(2);

            System.out.println("OBSERVE: destruction callbacks are registered by the container, but executed by the Scope implementation");
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

    interface Counter {
        long id();
    }

    static class ThreadScopedCounter implements Counter {
        private final long id;

        ThreadScopedCounter() {
            this.id = sequence.incrementAndGet();
        }

        @Override
        public long id() {
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

    static class InterfaceConsumer {
        private final Counter counter;

        InterfaceConsumer(Counter counter) {
            this.counter = counter;
        }

        long currentId() {
            return counter.id();
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

    @Configuration
    static class ProxyThreadScopeInterfacesConfiguration {

        @Bean
        static BeanFactoryPostProcessor registerThreadScope() {
            return beanFactory -> ((ConfigurableBeanFactory) beanFactory).registerScope("thread", new SimpleThreadScope());
        }

        @Bean
        @Scope(value = "thread", proxyMode = ScopedProxyMode.INTERFACES)
        Counter threadScopedCounter() {
            return new ThreadScopedCounter();
        }

        @Bean
        InterfaceConsumer interfaceConsumer(Counter counter) {
            return new InterfaceConsumer(counter);
        }
    }

    static class PrototypeCounter {
        private final long id;

        PrototypeCounter(long id) {
            this.id = id;
        }

        long id() {
            return id;
        }
    }

    static class DestroyAwareCounter implements DisposableBean {
        private final long id;

        DestroyAwareCounter(long id) {
            this.id = id;
        }

        long id() {
            return id;
        }

        @Override
        public void destroy() {
            destroyed.incrementAndGet();
        }
    }

    static class SwitchableScope implements org.springframework.beans.factory.config.Scope {
        private final AtomicReference<String> conversationId = new AtomicReference<>("default");
        private final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> scopedObjects = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, ConcurrentHashMap<String, Runnable>> destructionCallbacks = new ConcurrentHashMap<>();

        void setConversationId(String id) {
            this.conversationId.set(id);
        }

        void clearScope(String id) {
            ConcurrentHashMap<String, Runnable> callbacks = destructionCallbacks.remove(id);
            if (callbacks != null) {
                callbacks.values().forEach(Runnable::run);
            }
            scopedObjects.remove(id);
        }

        void reset() {
            for (String id : scopedObjects.keySet().toArray(new String[0])) {
                clearScope(id);
            }
            for (String id : destructionCallbacks.keySet().toArray(new String[0])) {
                clearScope(id);
            }
            this.conversationId.set("default");
        }

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            String id = this.conversationId.get();
            ConcurrentHashMap<String, Object> objects =
                    scopedObjects.computeIfAbsent(id, ignored -> new ConcurrentHashMap<>());
            return objects.computeIfAbsent(name, ignored -> objectFactory.getObject());
        }

        @Override
        public Object remove(String name) {
            String id = this.conversationId.get();
            ConcurrentHashMap<String, Runnable> callbacks = destructionCallbacks.get(id);
            if (callbacks != null) {
                Runnable callback = callbacks.remove(name);
                if (callback != null) {
                    callback.run();
                }
            }
            ConcurrentHashMap<String, Object> objects = scopedObjects.get(id);
            return objects == null ? null : objects.remove(name);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            String id = this.conversationId.get();
            ConcurrentHashMap<String, Runnable> callbacks =
                    destructionCallbacks.computeIfAbsent(id, ignored -> new ConcurrentHashMap<>());
            callbacks.put(name, callback);
        }

        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }

        @Override
        public String getConversationId() {
            return this.conversationId.get();
        }
    }

    static class PrototypeDirectConsumer {
        private final PrototypeCounter counter;

        PrototypeDirectConsumer(PrototypeCounter counter) {
            this.counter = counter;
        }

        long currentId() {
            return counter.id();
        }
    }

    static class PrototypeProviderConsumer {
        private final ObjectProvider<PrototypeCounter> provider;

        PrototypeProviderConsumer(ObjectProvider<PrototypeCounter> provider) {
            this.provider = provider;
        }

        long currentId() {
            return provider.getObject().id();
        }
    }

    @Configuration
    static class PrototypeInjectionConfiguration {

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        PrototypeCounter prototypeCounter() {
            return new PrototypeCounter(prototypeSequence.incrementAndGet());
        }

        @Bean
        PrototypeDirectConsumer prototypeDirectConsumer(PrototypeCounter counter) {
            return new PrototypeDirectConsumer(counter);
        }

        @Bean
        PrototypeProviderConsumer prototypeProviderConsumer(ObjectProvider<PrototypeCounter> provider) {
            return new PrototypeProviderConsumer(provider);
        }
    }

    @Configuration
    static class SwitchableScopeConfiguration {
        static final SwitchableScope SCOPE = new SwitchableScope();

        @Bean
        static BeanFactoryPostProcessor registerSwitchableScope() {
            return beanFactory -> ((ConfigurableBeanFactory) beanFactory).registerScope("switchable", SCOPE);
        }

        @Bean
        @Scope("switchable")
        DestroyAwareCounter destroyAwareCounter() {
            return new DestroyAwareCounter(sequence.incrementAndGet());
        }
    }
}

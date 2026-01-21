package com.learning.springboot.springcorebeans.part03_container_internals;

// 参考实现：对齐 SpringCoreBeansContainerInternalsExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansContainerInternalsExerciseSolutionTest {

    @AfterEach
    void cleanupConversationContext() {
        ConversationContext.clear();
    }

    @Test
    void solution_customScopeAndScopedProxy() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConversationScopeConfiguration.class)) {
            ConversationScopedConsumer consumer = context.getBean(ConversationScopedConsumer.class);

            ConversationContext.set("c1");
            long c1First = consumer.currentId();
            long c1Second = consumer.currentId();
            assertThat(c1First).isEqualTo(c1Second);

            ConversationContext.set("c2");
            long c2First = consumer.currentId();
            long c2Second = consumer.currentId();
            assertThat(c2First).isEqualTo(c2Second);

            assertThat(c1First).isNotEqualTo(c2First);
        }
    }

    @Test
    void solution_smartLifecycleDeepDive() {
        List<String> events = new CopyOnWriteArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("a", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("A", -1, events));
            context.registerBean("b", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("B", 0, events));
            context.registerBean("c", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("C", 1, events));
            context.registerBean("d", RecordingSmartLifecycle.class, () -> new RecordingSmartLifecycle("D", 0, events));
            context.refresh();
        }

        int startA = events.indexOf("start:A");
        int startB = events.indexOf("start:B");
        int startD = events.indexOf("start:D");
        int startC = events.indexOf("start:C");

        assertThat(startA).isNotNegative();
        assertThat(startB).isNotNegative();
        assertThat(startD).isNotNegative();
        assertThat(startC).isNotNegative();

        assertThat(startA).isLessThan(startB);
        assertThat(startA).isLessThan(startD);
        assertThat(startB).isLessThan(startC);
        assertThat(startD).isLessThan(startC);

        int stopC = events.indexOf("stop:C");
        int stopB = events.indexOf("stop:B");
        int stopD = events.indexOf("stop:D");
        int stopA = events.indexOf("stop:A");

        assertThat(stopC).isNotNegative();
        assertThat(stopB).isNotNegative();
        assertThat(stopD).isNotNegative();
        assertThat(stopA).isNotNegative();

        assertThat(stopC).isLessThan(stopB);
        assertThat(stopC).isLessThan(stopD);
        assertThat(stopB).isLessThan(stopA);
        assertThat(stopD).isLessThan(stopA);
    }

    @Test
    void solution_factoryBeanTypePitfalls_getObjectTypeReturningNull() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FactoryBeanPitfallConfiguration.class)) {
            String[] namesWithoutEagerInit = context.getBeanFactory().getBeanNamesForType(Value.class, true, false);
            assertThat(namesWithoutEagerInit)
                    .contains("knownValue")
                    .doesNotContain("unknownValue");

            Value unknown = context.getBean("unknownValue", Value.class);
            assertThat(unknown.origin()).isEqualTo("unknown");
        }
    }

    @Test
    void solution_minimalProxyingBeanPostProcessor_reproducesSelfInvocationAndTypeLookupPitfalls() {
        CallLog callLog = new CallLog();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(CallLog.class, () -> callLog);
            context.registerBean(ProxyingPostProcessor.class, () -> new ProxyingPostProcessor(callLog));
            context.registerBean(SelfInvocationService.class);
            context.refresh();

            WorkService service = context.getBean(WorkService.class);
            assertThat(Proxy.isProxyClass(service.getClass())).isTrue();

            callLog.clear();
            assertThat(service.outer("Bob")).isEqualTo("outer:inner:Bob");
            assertThat(callLog.entries()).containsExactly("outer");

            callLog.clear();
            assertThat(service.inner("Bob")).isEqualTo("inner:Bob");
            assertThat(callLog.entries()).containsExactly("inner");

            assertThatThrownBy(() -> context.getBean(SelfInvocationService.class)).isInstanceOf(BeansException.class);
        }
    }

    @Test
    void solution_injectionOptionalRequiredSemantics_fieldAndConstructor() {
        try (GenericApplicationContext optional = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(optional);
            optional.registerBean(OptionalFieldConsumer.class);
            optional.registerBean(OptionalProviderConsumer.class);
            optional.refresh();

            OptionalFieldConsumer fieldConsumer = optional.getBean(OptionalFieldConsumer.class);
            assertThat(fieldConsumer.dependency()).isNull();

            OptionalProviderConsumer providerConsumer = optional.getBean(OptionalProviderConsumer.class);
            assertThat(providerConsumer.dependencyPresent()).isFalse();
        }

        assertThatThrownBy(() -> {
            try (GenericApplicationContext required = new GenericApplicationContext()) {
                AnnotationConfigUtils.registerAnnotationConfigProcessors(required);
                required.registerBean(RequiredConstructorConsumer.class);
                required.refresh();
            }
        }).hasRootCauseInstanceOf(org.springframework.beans.factory.NoSuchBeanDefinitionException.class);
    }

    static final class ConversationContext {
        private static final ThreadLocal<String> currentConversationId = new ThreadLocal<>();

        private ConversationContext() {
        }

        static void set(String conversationId) {
            currentConversationId.set(conversationId);
        }

        static String get() {
            return currentConversationId.get();
        }

        static void clear() {
            currentConversationId.remove();
        }
    }

    static final class ConversationScope implements Scope {

        private final Map<String, Map<String, Object>> store = new ConcurrentHashMap<>();
        private final Map<String, Map<String, Runnable>> destructionCallbacks = new ConcurrentHashMap<>();

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            String conversationId = ConversationContext.get();
            if (conversationId == null) {
                throw new IllegalStateException("conversationId is required for 'conversation' scope");
            }

            Map<String, Object> beans = store.computeIfAbsent(conversationId, ignored -> new ConcurrentHashMap<>());
            return beans.computeIfAbsent(name, ignored -> objectFactory.getObject());
        }

        @Override
        public Object remove(String name) {
            String conversationId = ConversationContext.get();
            if (conversationId == null) {
                return null;
            }

            Map<String, Runnable> callbacks = destructionCallbacks.get(conversationId);
            Runnable callback = callbacks == null ? null : callbacks.get(name);
            if (callback != null) {
                callback.run();
            }

            Map<String, Object> beans = store.get(conversationId);
            if (beans == null) {
                return null;
            }
            return beans.remove(name);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
            String conversationId = ConversationContext.get();
            if (conversationId == null) {
                return;
            }
            destructionCallbacks
                    .computeIfAbsent(conversationId, ignored -> new ConcurrentHashMap<>())
                    .put(name, callback);
        }

        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }

        @Override
        public String getConversationId() {
            return ConversationContext.get();
        }
    }

    @Configuration
    static class ConversationScopeConfiguration {

        private static final AtomicLong sequence = new AtomicLong();

        @Bean
        static BeanFactoryPostProcessor registerConversationScope() {
            ConversationScope scope = new ConversationScope();
            return beanFactory -> ((ConfigurableBeanFactory) beanFactory).registerScope("conversation", scope);
        }

        @Bean
        @org.springframework.context.annotation.Scope(value = "conversation", proxyMode = ScopedProxyMode.TARGET_CLASS)
        ConversationScopedCounter conversationScopedCounter() {
            return new ConversationScopedCounter(sequence.incrementAndGet());
        }

        @Bean
        ConversationScopedConsumer conversationScopedConsumer(ConversationScopedCounter counter) {
            return new ConversationScopedConsumer(counter);
        }
    }

    static class ConversationScopedCounter {
        private final long id;

        ConversationScopedCounter(long id) {
            this.id = id;
        }

        long id() {
            return id;
        }
    }

    static class ConversationScopedConsumer {
        private final ConversationScopedCounter counter;

        ConversationScopedConsumer(ConversationScopedCounter counter) {
            this.counter = counter;
        }

        long currentId() {
            return counter.id();
        }
    }

    static class RecordingSmartLifecycle implements SmartLifecycle {

        private final String name;
        private final int phase;
        private final List<String> events;
        private boolean running;

        RecordingSmartLifecycle(String name, int phase, List<String> events) {
            this.name = name;
            this.phase = phase;
            this.events = events;
        }

        @Override
        public void start() {
            running = true;
            events.add("start:" + name);
        }

        @Override
        public void stop() {
            running = false;
            events.add("stop:" + name);
        }

        @Override
        public void stop(Runnable callback) {
            stop();
            callback.run();
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public int getPhase() {
            return phase;
        }

        @Override
        public boolean isAutoStartup() {
            return true;
        }
    }

    record Value(String origin) {
    }

    static class KnownTypeFactoryBean implements org.springframework.beans.factory.FactoryBean<Value> {
        @Override
        public Value getObject() {
            return new Value("known");
        }

        @Override
        public Class<?> getObjectType() {
            return Value.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    static class UnknownTypeFactoryBean implements org.springframework.beans.factory.FactoryBean<Value> {
        @Override
        public Value getObject() {
            return new Value("unknown");
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    @Configuration
    static class FactoryBeanPitfallConfiguration {
        @Bean(name = "knownValue")
        org.springframework.beans.factory.FactoryBean<Value> knownTypeFactoryBean() {
            return new KnownTypeFactoryBean();
        }

        @Bean(name = "unknownValue")
        org.springframework.beans.factory.FactoryBean<Value> unknownTypeFactoryBean() {
            return new UnknownTypeFactoryBean();
        }
    }

    interface WorkService {
        String outer(String name);

        String inner(String name);
    }

    static class SelfInvocationService implements WorkService {

        @Override
        public String outer(String name) {
            return "outer:" + this.inner(name);
        }

        @Override
        public String inner(String name) {
            return "inner:" + name;
        }
    }

    static class CallLog {
        private final List<String> entries = new CopyOnWriteArrayList<>();

        void add(String methodName) {
            entries.add(methodName);
        }

        List<String> entries() {
            return List.copyOf(entries);
        }

        void clear() {
            entries.clear();
        }
    }

    static class ProxyingPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {
        private final CallLog callLog;

        ProxyingPostProcessor(CallLog callLog) {
            this.callLog = callLog;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (!(bean instanceof WorkService target)) {
                return bean;
            }

            if (Proxy.isProxyClass(bean.getClass())) {
                return bean;
            }

            return Proxy.newProxyInstance(
                    WorkService.class.getClassLoader(),
                    new Class<?>[]{WorkService.class},
                    (proxy, method, args) -> {
                        callLog.add(method.getName());
                        return method.invoke(target, args);
                    }
            );
        }
    }

    static class OptionalFieldConsumer {
        @Autowired(required = false)
        private MissingDependency dependency;

        MissingDependency dependency() {
            return dependency;
        }
    }

    static class OptionalProviderConsumer {
        private final ObjectProvider<MissingDependency> provider;

        OptionalProviderConsumer(ObjectProvider<MissingDependency> provider) {
            this.provider = provider;
        }

        boolean dependencyPresent() {
            return provider.getIfAvailable() != null;
        }
    }

    static class RequiredConstructorConsumer {
        RequiredConstructorConsumer(MissingDependency dependency) {
        }
    }

    static class MissingDependency {
    }
}

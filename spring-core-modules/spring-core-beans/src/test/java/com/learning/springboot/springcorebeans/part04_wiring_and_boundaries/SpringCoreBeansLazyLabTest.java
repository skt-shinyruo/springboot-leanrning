package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ClassUtils;

public class SpringCoreBeansLazyLabTest {

    private static final AtomicInteger serviceConstructorCalls = new AtomicInteger();
    private static final AtomicInteger concreteServiceConstructorCalls = new AtomicInteger();

    @Test
    void lazyInitBean_isNotInstantiatedDuringRefresh_butCreatedOnFirstGetBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            serviceConstructorCalls.set(0);

            context.registerBean("lazyBean", CountingService.class, CountingService::new, bd -> bd.setLazyInit(true));
            context.refresh();

            System.out.println("OBSERVE: lazy-init bean is NOT created during refresh");
            assertThat(serviceConstructorCalls.get()).isEqualTo(0);

            context.getBean(CountingService.class);
            assertThat(serviceConstructorCalls.get()).isEqualTo(1);
        }
    }

    @Test
    void lazyInitDoesNotHelpIfAConsumerEagerlyDependsOnTheBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            serviceConstructorCalls.set(0);

            context.registerBean("service", CountingService.class, CountingService::new, bd -> bd.setLazyInit(true));
            context.registerBean(EagerConsumer.class);
            context.refresh();

            System.out.println("OBSERVE: even if service is lazy-init, a normal dependency forces instantiation");
            assertThat(serviceConstructorCalls.get()).isEqualTo(1);
        }
    }

    @Test
    void lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            serviceConstructorCalls.set(0);

            context.registerBean("service", CountingService.class, CountingService::new, bd -> bd.setLazyInit(true));
            context.registerBean(LazyConsumer.class);
            context.refresh();

            LazyConsumer consumer = context.getBean(LazyConsumer.class);

            System.out.println("OBSERVE: @Lazy injection-point injects a proxy and can defer creation");
            assertThat(serviceConstructorCalls.get()).isEqualTo(0);
            assertThat(Proxy.isProxyClass(consumer.injectedService().getClass())).isTrue();

            assertThat(consumer.ping()).isEqualTo("pong");
            assertThat(serviceConstructorCalls.get()).isEqualTo(1);
        }
    }

    @Test
    void lazyInjectionPoint_onConcreteClass_usesClassBasedProxy_andDefersCreationUntilFirstUse() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            concreteServiceConstructorCalls.set(0);

            context.registerBean(ConcreteService.class, ConcreteService::new, bd -> bd.setLazyInit(true));
            context.registerBean(ConcreteConsumer.class);
            context.refresh();

            ConcreteConsumer consumer = context.getBean(ConcreteConsumer.class);

            System.out.println("OBSERVE: @Lazy on a concrete class injection point usually creates a class-based (CGLIB) proxy");
            System.out.println("OBSERVE: defers target bean creation until first method invocation on the proxy");

            assertThat(concreteServiceConstructorCalls.get()).isEqualTo(0);
            assertThat(Proxy.isProxyClass(consumer.injected().getClass())).isFalse();
            assertThat(ClassUtils.isCglibProxyClass(consumer.injected().getClass())).isTrue();

            assertThat(consumer.ping()).isEqualTo("pong");
            assertThat(concreteServiceConstructorCalls.get()).isEqualTo(1);
        }
    }

    interface Service {
        String ping();
    }

    static class CountingService implements Service {

        CountingService() {
            serviceConstructorCalls.incrementAndGet();
        }

        @Override
        public String ping() {
            return "pong";
        }
    }

    static class EagerConsumer {
        private final Service service;

        EagerConsumer(Service service) {
            this.service = service;
        }

        Service service() {
            return service;
        }
    }

    static class LazyConsumer {
        private final Service service;

        LazyConsumer(@Lazy Service service) {
            this.service = service;
        }

        String ping() {
            return service.ping();
        }

        Service injectedService() {
            return service;
        }
    }

    static class ConcreteService {

        ConcreteService() {
            concreteServiceConstructorCalls.incrementAndGet();
        }

        String ping() {
            return "pong";
        }
    }

    static class ConcreteConsumer {
        private final ConcreteService service;

        ConcreteConsumer(@Lazy ConcreteService service) {
            this.service = service;
        }

        String ping() {
            return service.ping();
        }

        ConcreteService injected() {
            return service;
        }
    }
}

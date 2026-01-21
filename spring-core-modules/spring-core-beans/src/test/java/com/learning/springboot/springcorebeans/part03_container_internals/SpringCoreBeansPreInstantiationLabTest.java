package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansPreInstantiationLabTest {

    private static final AtomicInteger failingConstructorCalls = new AtomicInteger();

    @Test
    void withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled() {
        failingConstructorCalls.set(0);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("service", FailingService.class);

        assertThatThrownBy(context::refresh)
                .isInstanceOf(BeanCreationException.class);

        context.close();

        System.out.println("OBSERVE: Without before-instantiation short-circuit, constructor runs and can fail the context");
        assertThat(failingConstructorCalls.get()).isEqualTo(1);
    }

    @Test
    void postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath() {
        failingConstructorCalls.set(0);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(ShortCircuitingPostProcessor.class);
            context.registerBean("service", FailingService.class);
            context.refresh();

            GreetingService service = context.getBean(GreetingService.class);

            System.out.println("OBSERVE: postProcessBeforeInstantiation returned a surrogate => constructor was never called");
            assertThat(failingConstructorCalls.get()).isEqualTo(0);
            assertThat(Proxy.isProxyClass(service.getClass())).isTrue();
            assertThat(service.greet("Bob")).isEqualTo("surrogate:Bob");
        }
    }

    interface GreetingService {
        String greet(String name);
    }

    static class FailingService implements GreetingService {

        FailingService() {
            failingConstructorCalls.incrementAndGet();
            throw new IllegalStateException("boom");
        }

        @Override
        public String greet(String name) {
            return "real:" + name;
        }
    }

    static class ShortCircuitingPostProcessor implements InstantiationAwareBeanPostProcessor {

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if (beanClass == FailingService.class) {
                return Proxy.newProxyInstance(
                        GreetingService.class.getClassLoader(),
                        new Class<?>[]{GreetingService.class},
                        (proxy, method, args) -> {
                            if (method.getName().equals("greet")) {
                                return "surrogate:" + args[0];
                            }
                            throw new UnsupportedOperationException(method.toString());
                        }
                );
            }
            return null;
        }
    }
}

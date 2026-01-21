package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.PriorityOrdered;

class SpringCoreBeansBeanCreationTraceLabTest {

    @Test
    void beanCreationTrace_recordsPhases_andExposesProxyReplacement() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("dependency", Dependency.class, () -> new Dependency(events));
            context.registerBean("service", TraceableService.class, () -> new TraceableService(events),
                    bd -> bd.getPropertyValues().add("dependency", new RuntimeBeanReference("dependency")));

            context.registerBean(TraceInstantiationAwareBpp.class, () -> new TraceInstantiationAwareBpp(events));
            context.registerBean(ProxyReplacingBpp.class, () -> new ProxyReplacingBpp(events));

            context.refresh();

            WorkService service = context.getBean(WorkService.class);

            assertThat(Proxy.isProxyClass(service.getClass())).isTrue();
            assertThat(service.work()).isEqualTo("work-with-dependency");

            assertThatThrownBy(() -> context.getBean("service", TraceableService.class))
                    .isInstanceOf(BeanNotOfRequiredTypeException.class);
        }

        System.out.println("OBSERVE: instantiate -> populate -> initialize, then BeanPostProcessor wraps/replaces the exposed bean");
        assertThat(events).containsExactly(
                "dependency:constructed",
                "service:constructed",
                "iabpp:afterInstantiation(service)",
                "iabpp:postProcessProperties(service,hasDependencyProperty=true,dependencyInjected=false)",
                "service:setDependency",
                "bpp:beforeInitialization(service)",
                "service:afterPropertiesSet",
                "bpp:afterInitialization(service):replacedByJdkProxy"
        );
    }

    interface WorkService {
        String work();
    }

    static class Dependency {
        Dependency(List<String> events) {
            events.add("dependency:constructed");
        }
    }

    static class TraceableService implements WorkService, InitializingBean {
        private final List<String> events;
        private Dependency dependency;

        TraceableService(List<String> events) {
            this.events = events;
            events.add("service:constructed");
        }

        public void setDependency(Dependency dependency) {
            this.dependency = dependency;
            events.add("service:setDependency");
        }

        @Override
        public void afterPropertiesSet() {
            events.add("service:afterPropertiesSet");
            assertThat(dependency).as("dependency should be injected before init callbacks").isNotNull();
        }

        @Override
        public String work() {
            return "work-with-dependency";
        }
    }

    static class TraceInstantiationAwareBpp implements InstantiationAwareBeanPostProcessor, PriorityOrdered {
        private final List<String> events;

        TraceInstantiationAwareBpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) {
            if ("service".equals(beanName)) {
                events.add("iabpp:afterInstantiation(service)");
            }
            return true;
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
            if ("service".equals(beanName) && bean instanceof TraceableService service) {
                boolean hasDependencyProperty = pvs.contains("dependency");
                boolean dependencyInjected = (service.dependency != null);
                events.add("iabpp:postProcessProperties(service,hasDependencyProperty=" + hasDependencyProperty
                        + ",dependencyInjected=" + dependencyInjected + ")");
            }
            return pvs;
        }
    }

    static class ProxyReplacingBpp implements BeanPostProcessor, PriorityOrdered {
        private final List<String> events;

        ProxyReplacingBpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return 100;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if ("service".equals(beanName)) {
                events.add("bpp:beforeInitialization(service)");
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (!"service".equals(beanName)) {
                return bean;
            }

            events.add("bpp:afterInitialization(service):replacedByJdkProxy");
            return Proxy.newProxyInstance(
                    WorkService.class.getClassLoader(),
                    new Class<?>[]{WorkService.class},
                    (proxy, method, args) -> method.invoke(bean, args)
            );
        }
    }
}

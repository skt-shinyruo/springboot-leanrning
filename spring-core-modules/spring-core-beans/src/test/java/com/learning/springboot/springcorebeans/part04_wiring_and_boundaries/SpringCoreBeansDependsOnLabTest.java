package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansDependsOnLabTest {

    @Test
    void dependsOn_forcesInitializationOrder_evenWithoutDirectDependencies() {
        List<String> events = new ArrayList<>();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("first", First.class, () -> new First(events));
            context.registerBean("second", Second.class, () -> new Second(events), bd -> bd.setDependsOn("first"));
            context.refresh();
        }

        System.out.println("OBSERVE: dependsOn is about initialization order, not about injection");
        assertThat(events).containsExactly(
                "first:constructed",
                "second:constructed"
        );
    }

    @Test
    void dependsOn_cycle_failsFast() {
        List<String> events = new ArrayList<>();

        assertThatThrownBy(() -> {
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
                context.registerBean("first", First.class, () -> new First(events), bd -> bd.setDependsOn("second"));
                context.registerBean("second", Second.class, () -> new Second(events), bd -> bd.setDependsOn("first"));
                context.refresh();
            }
        })
                .isInstanceOf(BeanCreationException.class)
                .hasMessageContaining("Circular depends-on relationship");

        assertThat(events).isEmpty();
    }

    @Test
    void dependsOn_triggersLazyDependencyInstantiation() {
        List<String> events = new ArrayList<>();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("lazyDependency", LazyDependency.class, () -> new LazyDependency(events),
                    bd -> bd.setLazyInit(true));
            context.registerBean("dependent", Dependent.class, () -> new Dependent(events),
                    bd -> bd.setDependsOn("lazyDependency"));

            assertThat(context.getBeanFactory().getBeanDefinition("lazyDependency").isLazyInit()).isTrue();
            context.refresh();
        }

        System.out.println("OBSERVE: dependsOn triggers getBean(dep) before creating the dependent => lazy-init is forced");
        assertThat(events).containsExactly(
                "lazyDependency:constructed",
                "dependent:constructed"
        );
    }

    @Test
    void dependsOn_affectsDestroyOrder_viaDependentBeanMap() {
        List<String> events = new ArrayList<>();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("dependency", DestroyableDependency.class, () -> new DestroyableDependency(events));
            context.registerBean("dependent", DestroyableDependent.class, () -> new DestroyableDependent(events),
                    bd -> bd.setDependsOn("dependency"));
            context.refresh();

            assertThat(context.getBeanFactory().getDependentBeans("dependency")).contains("dependent");
            assertThat(context.getBeanFactory().getDependenciesForBean("dependent")).contains("dependency");
        }

        System.out.println("OBSERVE: destroy order is the reverse of dependency edges => destroy dependent first, then dependency");
        assertThat(events).containsExactly(
                "dependency:constructed",
                "dependent:constructed",
                "dependent:destroyed",
                "dependency:destroyed"
        );
    }

    static class First {
        First(List<String> events) {
            events.add("first:constructed");
        }
    }

    static class Second {
        Second(List<String> events) {
            events.add("second:constructed");
        }
    }

    static class LazyDependency {
        LazyDependency(List<String> events) {
            events.add("lazyDependency:constructed");
        }
    }

    static class Dependent {
        Dependent(List<String> events) {
            events.add("dependent:constructed");
        }
    }

    static class DestroyableDependency implements DisposableBean {
        private final List<String> events;

        DestroyableDependency(List<String> events) {
            this.events = events;
            events.add("dependency:constructed");
        }

        @Override
        public void destroy() {
            events.add("dependency:destroyed");
        }
    }

    static class DestroyableDependent implements DisposableBean {
        private final List<String> events;

        DestroyableDependent(List<String> events) {
            this.events = events;
            events.add("dependent:constructed");
        }

        @Override
        public void destroy() {
            events.add("dependent:destroyed");
        }
    }
}

package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansPostProcessorOrderingLabTest {

    @Test
    void beanFactoryPostProcessors_areInvokedInPriorityOrderedThenOrderedThenUnorderedOrder() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("priorityBfpp", PriorityBfpp.class, () -> new PriorityBfpp(events));
            context.registerBean("orderedBfpp", OrderedBfpp.class, () -> new OrderedBfpp(events));
            context.registerBean("unorderedBfpp", UnorderedBfpp.class, () -> new UnorderedBfpp(events));
            context.refresh();
        }

        System.out.println("OBSERVE: BFPP ordering is PriorityOrdered -> Ordered -> unordered");
        assertThat(events).containsExactly(
                "bfpp:priority",
                "bfpp:ordered",
                "bfpp:unordered"
        );
    }

    @Test
    void beanFactoryPostProcessors_withDifferentOrderValues_areSortedAscendingWithinOrderedGroup() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("orderedBfpp10", OrderedValueBfpp.class, () -> new OrderedValueBfpp(events, "bfpp:ordered10", 10));
            context.registerBean("orderedBfpp0", OrderedValueBfpp.class, () -> new OrderedValueBfpp(events, "bfpp:ordered0", 0));
            context.refresh();
        }

        System.out.println("OBSERVE: within the Ordered phase, smaller order value runs first");
        assertThat(events).containsExactly(
                "bfpp:ordered0",
                "bfpp:ordered10"
        );
    }

    @Test
    void beanPostProcessors_areAppliedInPriorityOrderedThenOrderedThenUnorderedOrder() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("priorityBpp", PriorityBpp.class, () -> new PriorityBpp(events));
            context.registerBean("orderedBpp", OrderedBpp.class, () -> new OrderedBpp(events));
            context.registerBean("unorderedBpp", UnorderedBpp.class, () -> new UnorderedBpp(events));

            context.registerBean(Target.class);
            context.refresh();
        }

        System.out.println("OBSERVE: BPP ordering is PriorityOrdered -> Ordered -> unordered");
        assertThat(events).containsExactly(
                "bpp:priority",
                "bpp:ordered",
                "bpp:unordered"
        );
    }

    @Test
    void beanPostProcessors_withDifferentOrderValues_areSortedAscendingWithinOrderedGroup() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("orderedBpp10", OrderedValueBpp.class, () -> new OrderedValueBpp(events, "bpp:ordered10", 10));
            context.registerBean("orderedBpp0", OrderedValueBpp.class, () -> new OrderedValueBpp(events, "bpp:ordered0", 0));

            context.registerBean(Target.class);
            context.refresh();
        }

        System.out.println("OBSERVE: within the Ordered phase, BeanPostProcessors are sorted by getOrder() ascending");
        assertThat(events).containsExactly(
                "bpp:ordered0",
                "bpp:ordered10"
        );
    }

    @Test
    void beanPostProcessors_annotatedWithOrderButNotOrdered_areNotSorted_andFollowRegistrationOrder() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.getDefaultListableBeanFactory().setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);

            context.registerBean("annotated10Bpp", AnnotatedOrder10Bpp.class, () -> new AnnotatedOrder10Bpp(events));
            context.registerBean("annotated0Bpp", AnnotatedOrder0Bpp.class, () -> new AnnotatedOrder0Bpp(events));

            context.registerBean(Target.class);
            context.refresh();
        }

        System.out.println("OBSERVE: @Order alone does not move a BeanPostProcessor into the Ordered phase; non-Ordered BPPs keep registration order");
        assertThat(events).containsExactly(
                "bpp:annotated10",
                "bpp:annotated0"
        );
    }

    static class PriorityBfpp implements BeanFactoryPostProcessor, PriorityOrdered {
        private final List<String> events;

        PriorityBfpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            events.add("bfpp:priority");
        }
    }

    static class OrderedBfpp implements BeanFactoryPostProcessor, Ordered {
        private final List<String> events;

        OrderedBfpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            events.add("bfpp:ordered");
        }
    }

    static class UnorderedBfpp implements BeanFactoryPostProcessor {
        private final List<String> events;

        UnorderedBfpp(List<String> events) {
            this.events = events;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            events.add("bfpp:unordered");
        }
    }

    static class PriorityBpp implements BeanPostProcessor, PriorityOrdered {
        private final List<String> events;

        PriorityBpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:priority");
            }
            return bean;
        }
    }

    static class OrderedBpp implements BeanPostProcessor, Ordered {
        private final List<String> events;

        OrderedBpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:ordered");
            }
            return bean;
        }
    }

    static class UnorderedBpp implements BeanPostProcessor {
        private final List<String> events;

        UnorderedBpp(List<String> events) {
            this.events = events;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:unordered");
            }
            return bean;
        }
    }

    static class Target {
    }

    static class OrderedValueBfpp implements BeanFactoryPostProcessor, Ordered {
        private final List<String> events;
        private final String event;
        private final int order;

        OrderedValueBfpp(List<String> events, String event, int order) {
            this.events = events;
            this.event = event;
            this.order = order;
        }

        @Override
        public int getOrder() {
            return order;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            events.add(event);
        }
    }

    static class OrderedValueBpp implements BeanPostProcessor, Ordered {
        private final List<String> events;
        private final String event;
        private final int order;

        OrderedValueBpp(List<String> events, String event, int order) {
            this.events = events;
            this.event = event;
            this.order = order;
        }

        @Override
        public int getOrder() {
            return order;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add(event);
            }
            return bean;
        }
    }

    @Order(10)
    static class AnnotatedOrder10Bpp implements BeanPostProcessor {
        private final List<String> events;

        AnnotatedOrder10Bpp(List<String> events) {
            this.events = events;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:annotated10");
            }
            return bean;
        }
    }

    @Order(0)
    static class AnnotatedOrder0Bpp implements BeanPostProcessor {
        private final List<String> events;

        AnnotatedOrder0Bpp(List<String> events) {
            this.events = events;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:annotated0");
            }
            return bean;
        }
    }
}

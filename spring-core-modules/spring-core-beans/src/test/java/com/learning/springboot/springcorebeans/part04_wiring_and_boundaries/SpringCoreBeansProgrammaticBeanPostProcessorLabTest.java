package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansProgrammaticBeanPostProcessorLabTest {

    @Test
    void programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
            beanFactory.addBeanPostProcessor(new RecordingBpp("programmatic", events));

            context.registerBean("priorityBpp", PriorityRecordingBpp.class, () -> new PriorityRecordingBpp(events));
            context.registerBean("target", Target.class, () -> new Target(events));

            context.refresh();
        }

        System.out.println("OBSERVE: programmatic BeanPostProcessor runs before auto-detected BeanPostProcessors");
        assertThat(events).containsExactly(
                "target:constructor",
                "bpp:programmatic",
                "bpp:bean"
        );
    }

    @Test
    void programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
            beanFactory.addBeanPostProcessor(new OrderedProgrammaticBpp("first", events, 100));
            beanFactory.addBeanPostProcessor(new OrderedProgrammaticBpp("second", events, 0));

            context.registerBean("target", Target.class, () -> new Target(events));
            context.refresh();
        }

        System.out.println("OBSERVE: programmatic BPPs do not respect Ordered; registration order dictates execution order");
        assertThat(events).containsExactly(
                "target:constructor",
                "bpp:first",
                "bpp:second"
        );
    }

    static class Target {
        Target(List<String> events) {
            events.add("target:constructor");
        }
    }

    static class RecordingBpp implements BeanPostProcessor {
        private final String name;
        private final List<String> events;

        RecordingBpp(String name, List<String> events) {
            this.name = name;
            this.events = events;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:" + name);
            }
            return bean;
        }
    }

    static class PriorityRecordingBpp implements BeanPostProcessor, PriorityOrdered {
        private final List<String> events;

        PriorityRecordingBpp(List<String> events) {
            this.events = events;
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:bean");
            }
            return bean;
        }
    }

    static class OrderedProgrammaticBpp implements BeanPostProcessor, PriorityOrdered {
        private final String name;
        private final List<String> events;
        private final int order;

        OrderedProgrammaticBpp(String name, List<String> events, int order) {
            this.name = name;
            this.events = events;
            this.order = order;
        }

        @Override
        public int getOrder() {
            return order;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Target) {
                events.add("bpp:" + name);
            }
            return bean;
        }
    }
}

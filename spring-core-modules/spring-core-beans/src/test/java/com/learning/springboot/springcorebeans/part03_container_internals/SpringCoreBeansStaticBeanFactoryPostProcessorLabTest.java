package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreBeansStaticBeanFactoryPostProcessorLabTest {

    @Test
    void nonStaticBeanFactoryPostProcessor_forcesConfigurationClassInstantiation_tooEarly_soItMissesLaterBpps() {
        List<String> events = new ArrayList<>();
        Recorder.use(events);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(NonStaticBfppConfig.class);
            context.registerBean(MarkingBpp.class, MarkingBpp::new);
            context.refresh();
        } finally {
            Recorder.clear();
        }

        System.out.println("OBSERVE: non-static @Bean BFPP requires instantiating the @Configuration class to invoke the factory method");
        System.out.println("OBSERVE: BFPP phase happens before registerBeanPostProcessors, so the configuration bean misses later BPPs");

        assertThat(events).containsExactly(
                "config:constructed",
                "bfpp:factoryMethodInvoked",
                "bfpp:invoked",
                "bpp:constructed"
        );
    }

    @Test
    void staticBeanFactoryPostProcessor_doesNotForceEarlyConfigurationInstantiation_soConfigurationBeanIsProcessedByBpps() {
        List<String> events = new ArrayList<>();
        Recorder.use(events);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(StaticBfppConfig.class);
            context.registerBean(MarkingBpp.class, MarkingBpp::new);
            context.refresh();
        } finally {
            Recorder.clear();
        }

        System.out.println("OBSERVE: static @Bean BFPP can be created without instantiating the @Configuration class");
        System.out.println("OBSERVE: the configuration bean is created later, after BPP registration, so it is processed by BPPs");

        assertThat(events).containsExactly(
                "bfpp:factoryMethodInvoked",
                "bfpp:invoked",
                "bpp:constructed",
                "config:constructed",
                "bpp:markedConfig"
        );
    }

    @Configuration
    static class NonStaticBfppConfig {

        NonStaticBfppConfig() {
            Recorder.add("config:constructed");
        }

        @Bean
        BeanFactoryPostProcessor bfpp() {
            Recorder.add("bfpp:factoryMethodInvoked");
            return beanFactory -> Recorder.add("bfpp:invoked");
        }
    }

    @Configuration
    static class StaticBfppConfig {

        StaticBfppConfig() {
            Recorder.add("config:constructed");
        }

        @Bean
        static BeanFactoryPostProcessor bfpp() {
            Recorder.add("bfpp:factoryMethodInvoked");
            return beanFactory -> Recorder.add("bfpp:invoked");
        }
    }

    static class MarkingBpp implements BeanPostProcessor {

        MarkingBpp() {
            Recorder.add("bpp:constructed");
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof NonStaticBfppConfig || bean instanceof StaticBfppConfig) {
                Recorder.add("bpp:markedConfig");
            }
            return bean;
        }
    }

    private static final class Recorder {

        private static final ThreadLocal<List<String>> EVENTS = new ThreadLocal<>();

        private Recorder() {
        }

        static void use(List<String> events) {
            EVENTS.set(events);
        }

        static void clear() {
            EVENTS.remove();
        }

        static void add(String event) {
            EVENTS.get().add(event);
        }
    }
}

package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansBootstrapInternalsLabTest {

    @Test
    void withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(Dependency.class);
            context.registerBean(Target.class);
            context.refresh();

            Target target = context.getBean(Target.class);

            System.out.println("OBSERVE: Without AnnotationConfigProcessors, @Autowired/@PostConstruct are ignored");
            assertThat(target.dependency()).isNull();
            assertThat(target.postConstructCalled()).isFalse();
        }
    }

    @Test
    void registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean(Dependency.class);
            context.registerBean(Target.class);
            context.refresh();

            Target target = context.getBean(Target.class);

            System.out.println("OBSERVE: registerAnnotationConfigProcessors => @Autowired/@PostConstruct work");
            assertThat(target.dependency()).isNotNull();
            assertThat(target.postConstructCalled()).isTrue();
        }
    }

    @Test
    void configurationClassIsNotParsedWithoutConfigurationClassPostProcessor() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(Config.class);
            context.refresh();

            System.out.println("OBSERVE: Without ConfigurationClassPostProcessor, @Bean methods are NOT parsed");
            assertThatThrownBy(() -> context.getBean(ExampleBean.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean(Config.class);
            context.refresh();

            System.out.println("OBSERVE: With ConfigurationClassPostProcessor, @Bean methods are parsed into definitions");
            assertThat(context.getBean(ExampleBean.class)).isNotNull();
        }
    }

    static class Dependency {
    }

    static class Target {

        @Autowired
        private Dependency dependency;

        private boolean postConstructCalled;

        @PostConstruct
        void init() {
            postConstructCalled = true;
        }

        Dependency dependency() {
            return dependency;
        }

        boolean postConstructCalled() {
            return postConstructCalled;
        }
    }

    static class ExampleBean {
    }

    @Configuration
    static class Config {
        @Bean
        ExampleBean exampleBean() {
            return new ExampleBean();
        }
    }
}

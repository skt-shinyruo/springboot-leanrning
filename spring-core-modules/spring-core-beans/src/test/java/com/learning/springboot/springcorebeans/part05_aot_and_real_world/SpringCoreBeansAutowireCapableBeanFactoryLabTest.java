package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验演示：容器外对象（不是 Spring 创建的）如何使用 AutowireCapableBeanFactory 获取注入与生命周期回调能力。
 *
 * 关键对照：
 * 1) autowireBean：偏“只做注入”
 * 2) initializeBean：触发 @PostConstruct 等初始化回调（依赖 BPP）
 * 3) destroyBean：触发 @PreDestroy 等销毁回调
 */

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansAutowireCapableBeanFactoryLabTest {

    @Test
    void autowireThenInitialize_canApplyInjectionAndPostConstruct_forExternalObject() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean(Dependency.class);
            context.refresh();

            ExternalTarget external = new ExternalTarget();
            assertThat(external.dependency()).isNull();
            assertThat(external.postConstructCalled()).isFalse();

            AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();

            beanFactory.autowireBean(external);
            assertThat(external.dependency()).as("autowireBean 应完成依赖注入").isNotNull();
            assertThat(external.postConstructCalled()).as("@PostConstruct 依赖 initializeBean 链路").isFalse();

            Object initialized = beanFactory.initializeBean(external, "externalTarget");
            assertThat(initialized).isSameAs(external);
            assertThat(external.postConstructCalled()).isTrue();

            System.out.println("OBSERVE: autowireBean -> injection, initializeBean -> @PostConstruct (BPP phase)");
        }
    }

    @Test
    void destroyBean_canTriggerPreDestroy_forExternalObject_afterInitialization() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean(Dependency.class);
            context.refresh();

            ExternalTarget external = new ExternalTarget();
            AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();

            beanFactory.autowireBean(external);
            beanFactory.initializeBean(external, "externalTarget");

            assertThat(external.preDestroyCalled()).isFalse();
            beanFactory.destroyBean(external);
            assertThat(external.preDestroyCalled()).isTrue();

            System.out.println("OBSERVE: destroyBean triggers @PreDestroy for external objects (explicit lifecycle management)");
        }
    }

    static class Dependency {
        String id() {
            return "dep";
        }
    }

    static class ExternalTarget {
        private Dependency dependency;
        private boolean postConstructCalled;
        private boolean preDestroyCalled;

        Dependency dependency() {
            return dependency;
        }

        boolean postConstructCalled() {
            return postConstructCalled;
        }

        boolean preDestroyCalled() {
            return preDestroyCalled;
        }

        @org.springframework.beans.factory.annotation.Autowired
        void setDependency(Dependency dependency) {
            this.dependency = dependency;
        }

        @PostConstruct
        void init() {
            this.postConstructCalled = true;
        }

        @PreDestroy
        void shutdown() {
            this.preDestroyCalled = true;
        }
    }
}


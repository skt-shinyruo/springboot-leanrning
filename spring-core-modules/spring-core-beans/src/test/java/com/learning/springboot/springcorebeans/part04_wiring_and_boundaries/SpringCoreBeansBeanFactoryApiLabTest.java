package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

/*
 * 本 Lab 用最小对照证明两件事：
 * 1) DefaultListableBeanFactory 作为 BeanFactory 内核，可以创建 bean，但不会“自动让注解生效”
 * 2) 注解能力来自 BPP/BFPP；在 plain BeanFactory 场景下你必须手动 bootstrap（例如 addBeanPostProcessor）
 */

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

class SpringCoreBeansBeanFactoryApiLabTest {

    @Test
    void plainBeanFactory_doesNotAutoApplyAnnotationProcessors_but_manualBeanPostProcessorBootstrapCan() {
        DefaultListableBeanFactory plain = new DefaultListableBeanFactory();
        plain.registerBeanDefinition("dep", new RootBeanDefinition(Dependency.class));
        plain.registerBeanDefinition("target", new RootBeanDefinition(Target.class));

        Target plainTarget = plain.getBean(Target.class);
        assertThat(plainTarget.dependency()).as("plain BeanFactory does not process @Autowired field injection").isNull();
        assertThat(plainTarget.postConstructCalled()).isFalse();

        DefaultListableBeanFactory bootstrapped = new DefaultListableBeanFactory();
        bootstrapped.registerBeanDefinition("dep", new RootBeanDefinition(Dependency.class));
        bootstrapped.registerBeanDefinition("target", new RootBeanDefinition(Target.class));

        // 方式 1：显式添加需要的 BPP（最直观，适合教学）
        AutowiredAnnotationBeanPostProcessor autowiredBpp = new AutowiredAnnotationBeanPostProcessor();
        autowiredBpp.setBeanFactory(bootstrapped);
        bootstrapped.addBeanPostProcessor(autowiredBpp);

        CommonAnnotationBeanPostProcessor commonBpp = new CommonAnnotationBeanPostProcessor();
        commonBpp.setBeanFactory(bootstrapped);
        bootstrapped.addBeanPostProcessor(commonBpp);

        Target processed = bootstrapped.getBean(Target.class);
        assertThat(processed.dependency()).isNotNull();
        assertThat(processed.postConstructCalled()).isTrue();

        // 同时演示 ListableBeanFactory 的枚举能力（DefaultListableBeanFactory 实现了它）
        assertThat(bootstrapped.getBeanNamesForType(Dependency.class)).contains("dep");

        System.out.println("OBSERVE: Annotation-based injection/lifecycle requires BeanPostProcessors");
        System.out.println("OBSERVE: ApplicationContext refresh installs and registers those processors automatically");
    }

    @Test
    void registeringAnnotationConfigProcessors_isNotEnough_untilYouActuallyInvokeAndRegisterThem() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("dep", new RootBeanDefinition(Dependency.class));
        RootBeanDefinition target = new RootBeanDefinition(Target.class);
        target.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("targetPrototype", target);

        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        Target withoutProcessors = beanFactory.getBean("targetPrototype", Target.class);
        assertThat(withoutProcessors.dependency())
                .as("only registering processors as BeanDefinitions does not activate them in plain BeanFactory")
                .isNull();

        // 手动“激活”注解处理能力：实例化处理器并注册为 BeanPostProcessor（模拟 ApplicationContext refresh 的关键部分）
        AutowiredAnnotationBeanPostProcessor autowiredBpp = beanFactory.getBean(AutowiredAnnotationBeanPostProcessor.class);
        beanFactory.addBeanPostProcessor(autowiredBpp);

        CommonAnnotationBeanPostProcessor commonBpp = beanFactory.getBean(CommonAnnotationBeanPostProcessor.class);
        beanFactory.addBeanPostProcessor(commonBpp);

        Target afterBootstrap = beanFactory.getBean("targetPrototype", Target.class);
        assertThat(afterBootstrap.dependency()).isNotNull();

        System.out.println("OBSERVE: Plain BeanFactory does not auto-detect/auto-run BFPP/BPP like ApplicationContext does");
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
}

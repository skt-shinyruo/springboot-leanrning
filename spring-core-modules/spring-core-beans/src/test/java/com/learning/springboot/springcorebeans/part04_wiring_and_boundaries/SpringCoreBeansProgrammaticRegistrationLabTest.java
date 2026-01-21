package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

/*
 * 本实验对照三种编程式注册方式的差异：
 * 1) registerBeanDefinition/registerBean：定义层注册（由容器创建/注入/回调，能被 BPP 处理）
 * 2) registerSingleton：实例层注册（注册既有实例，不会 retroactive 触发注入/初始化/BPP）
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansProgrammaticRegistrationLabTest {

    @Test
    void registerBeanDefinitionAndRegisterBean_participateInCreationPipeline_andAreProcessedByBpp() {
        List<String> processed = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean(Dependency.class);
            context.registerBean(MarkingBeanPostProcessor.class, () -> new MarkingBeanPostProcessor(processed));

            context.registerBeanDefinition("byBeanDefinition", new RootBeanDefinition(Target.class));
            context.registerBean("byRegisterBean", Target.class, Target::new);

            context.refresh();

            Target byBeanDefinition = context.getBean("byBeanDefinition", Target.class);
            Target byRegisterBean = context.getBean("byRegisterBean", Target.class);

            assertThat(byBeanDefinition.dependency()).as("定义层注册应完成依赖注入").isNotNull();
            assertThat(byRegisterBean.dependency()).as("定义层注册应完成依赖注入").isNotNull();

            assertThat(byBeanDefinition.processed()).as("定义层注册应被 BeanPostProcessor 处理").isTrue();
            assertThat(byRegisterBean.processed()).as("定义层注册应被 BeanPostProcessor 处理").isTrue();

            assertThat(processed).containsExactly("byBeanDefinition", "byRegisterBean");
        }
    }

    @Test
    void registerSingleton_registersExistingInstance_andDoesNotRetroactivelyApplyInjectionOrBpp() {
        List<String> processed = new ArrayList<>();
        Target existing = new Target();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean(Dependency.class);
            context.registerBean(MarkingBeanPostProcessor.class, () -> new MarkingBeanPostProcessor(processed));

            context.getDefaultListableBeanFactory().registerSingleton("byRegisterSingleton", existing);
            context.refresh();

            Target fromContext = context.getBean("byRegisterSingleton", Target.class);
            assertThat(fromContext).isSameAs(existing);

            assertThat(fromContext.dependency()).as("实例层注册不会自动完成依赖注入").isNull();
            assertThat(fromContext.processed()).as("实例层注册不会 retroactive 触发 BeanPostProcessor").isFalse();
            assertThat(processed).doesNotContain("byRegisterSingleton");
        }
    }

    static class Dependency {
    }

    static class Target {
        @Autowired(required = false)
        private Dependency dependency;

        private boolean processed;

        Dependency dependency() {
            return dependency;
        }

        boolean processed() {
            return processed;
        }

        void markProcessed() {
            this.processed = true;
        }
    }

    static class MarkingBeanPostProcessor implements BeanPostProcessor {
        private final List<String> processed;

        MarkingBeanPostProcessor(List<String> processed) {
            this.processed = processed;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof Target target) {
                target.markProcessed();
                processed.add(beanName);
            }
            return bean;
        }
    }
}

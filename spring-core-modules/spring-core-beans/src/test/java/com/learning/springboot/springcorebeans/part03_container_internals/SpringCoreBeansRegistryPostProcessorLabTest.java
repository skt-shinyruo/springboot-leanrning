package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansRegistryPostProcessorLabTest {

    @Test
    void beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(Registrar.class);
            context.refresh();

            RegisteredBean bean = context.getBean(RegisteredBean.class);

            System.out.println("OBSERVE: BDRPP registered a new BeanDefinition => bean becomes available");
            assertThat(bean.origin()).isEqualTo("from-bdrpp");
        }
    }

    @Test
    void bdrppRunsBeforeRegularBeanFactoryPostProcessor() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(Registrar.class);
            context.registerBean(Modifier.class);
            context.refresh();

            RegisteredBean bean = context.getBean(RegisteredBean.class);

            System.out.println("OBSERVE: BFPP can modify definitions that were registered by BDRPP");
            assertThat(bean.origin()).isEqualTo("modified-by-bfpp");
        }
    }

    @Test
    void getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("earlyTarget", EarlyTarget.class, () -> new EarlyTarget(events));
            context.registerBean("lateTarget", LateTarget.class, () -> new LateTarget(events));

            context.registerBean("recordingBpp", TargetMarkingBeanPostProcessor.class, TargetMarkingBeanPostProcessor::new);
            context.registerBean("earlyInstantiationBdrpp", EarlyInstantiationBdrpp.class, () -> new EarlyInstantiationBdrpp("earlyTarget"));

            context.refresh();

            EarlyTarget earlyTarget = context.getBean("earlyTarget", EarlyTarget.class);
            LateTarget lateTarget = context.getBean("lateTarget", LateTarget.class);

            System.out.println("OBSERVE: calling getBean() during BDRPP/BFPP phase can instantiate a bean before BPPs are registered");
            System.out.println("OBSERVE: earlyTarget was created without BPP; lateTarget was created after BPP registration and is processed");

            assertThat(earlyTarget.processedByBpp()).isFalse();
            assertThat(lateTarget.processedByBpp()).isTrue();
            assertThat(events).contains("earlyTarget:constructor", "lateTarget:constructor", "bpp:lateTarget");
            assertThat(events).doesNotContain("bpp:earlyTarget");
        }
    }

    static class RegisteredBean {
        private String origin;

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        String origin() {
            return origin;
        }
    }

    static class RecordingTarget {
        private final String id;
        private final List<String> events;
        private boolean processedByBpp;

        RecordingTarget(String id, List<String> events) {
            this.id = id;
            this.events = events;
            events.add(id + ":constructor");
        }

        void markProcessedByBpp() {
            processedByBpp = true;
            events.add("bpp:" + id);
        }

        boolean processedByBpp() {
            return processedByBpp;
        }
    }

    static class EarlyTarget extends RecordingTarget {
        EarlyTarget(List<String> events) {
            super("earlyTarget", events);
        }
    }

    static class LateTarget extends RecordingTarget {
        LateTarget(List<String> events) {
            super("lateTarget", events);
        }
    }

    static class TargetMarkingBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RecordingTarget target) {
                target.markProcessedByBpp();
            }
            return bean;
        }
    }

    static class EarlyInstantiationBdrpp implements BeanDefinitionRegistryPostProcessor {

        private final String targetBeanName;

        EarlyInstantiationBdrpp(String targetBeanName) {
            this.targetBeanName = targetBeanName;
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.getBean(targetBeanName);
        }
    }

    static class Registrar implements BeanDefinitionRegistryPostProcessor {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            registry.registerBeanDefinition(
                    "registeredBean",
                    BeanDefinitionBuilder.genericBeanDefinition(RegisteredBean.class)
                            .addPropertyValue("origin", "from-bdrpp")
                            .getBeanDefinition()
            );
            System.out.println("OBSERVE: BDRPP registered definition 'registeredBean'");
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }
    }

    static class Modifier implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.getBeanDefinition("registeredBean")
                    .getPropertyValues()
                    .add("origin", "modified-by-bfpp");

            System.out.println("OBSERVE: BFPP modified definition 'registeredBean' before instantiation");
        }
    }
}

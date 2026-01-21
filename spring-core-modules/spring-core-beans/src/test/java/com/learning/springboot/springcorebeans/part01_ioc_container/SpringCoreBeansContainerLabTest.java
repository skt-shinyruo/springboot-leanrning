package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

class SpringCoreBeansContainerLabTest {

    @Test
    void beanDefinitionIsNotTheBeanInstance() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SimpleBeanConfig.class)) {
            BeanDefinition beanDefinition = context.getBeanFactory().getBeanDefinition("exampleBean");
            ExampleBean bean = context.getBean(ExampleBean.class);

            assertThat(beanDefinition).isNotInstanceOf(ExampleBean.class);
            assertThat(bean).isNotNull();
        }
    }

    @Test
    void beanFactoryPostProcessorCanModifyBeanDefinitionBeforeInstantiation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanFactoryPostProcessorConfig.class)) {
            ExampleBean bean = context.getBean(ExampleBean.class);
            assertThat(bean.value()).isEqualTo("modified-by-bfpp");
        }
    }

    @Test
    void beanPostProcessorCanModifyBeanInstanceAfterInitialization() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class)) {
            ExampleBean bean = context.getBean(ExampleBean.class);
            assertThat(bean.value()).isEqualTo("modified-by-bpp");
        }
    }

    @Test
    void configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProxiedConfig.class)) {
            ConfigA aFromContainer = context.getBean(ConfigA.class);
            ConfigB b = context.getBean(ConfigB.class);

            assertThat(b.a()).isSameAs(aFromContainer);
        }
    }

    @Test
    void configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NonProxiedConfig.class)) {
            ConfigA aFromContainer = context.getBean(ConfigA.class);
            ConfigB b = context.getBean(ConfigB.class);

            assertThat(b.a()).isNotSameAs(aFromContainer);
        }
    }

    @Test
    void liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LiteConfig.class)) {
            ConfigA aFromContainer = context.getBean(ConfigA.class);
            ConfigB b = context.getBean(ConfigB.class);

            assertThat(b.a()).isNotSameAs(aFromContainer);
        }
    }

    @Test
    void factoryBeanByNameReturnsProductAndAmpersandReturnsFactory() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FactoryBeanConfig.class)) {
            Long first = context.getBean("sequence", Long.class);
            Long second = context.getBean("sequence", Long.class);

            assertThat(first).isEqualTo(1L);
            assertThat(second).isEqualTo(2L);

            Object factory = context.getBean("&sequence");
            assertThat(factory).isInstanceOf(SequenceFactoryBean.class);
            assertThat(((SequenceFactoryBean) factory).getObject()).isEqualTo(3L);
        }
    }

    @Test
    void lookupMethodCanObtainFreshPrototypeEachCall() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(LookupConfig.class, LookupConsumer.class);
            context.refresh();
            LookupConsumer consumer = context.getBean(LookupConsumer.class);

            long first = consumer.nextId();
            long second = consumer.nextId();
            assertThat(first).isNotEqualTo(second);
        }
    }

    @Test
    void circularDependencyWithConstructorsFailsFast() {
        assertThatThrownBy(() -> new AnnotationConfigApplicationContext(ConstructorCycleConfig.class))
                .isInstanceOf(BeanCreationException.class)
                .hasRootCauseInstanceOf(BeanCurrentlyInCreationException.class);
    }

    @Test
    void circularDependencyWithSettersMaySucceedViaEarlySingletonExposure() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SetterCycleConfig.class)) {
            SetterA a = context.getBean(SetterA.class);
            SetterB b = context.getBean(SetterB.class);

            assertThat(a.b()).isSameAs(b);
            assertThat(b.a()).isSameAs(a);
        }
    }

    @Configuration
    static class SimpleBeanConfig {
        @Bean
        ExampleBean exampleBean() {
            return new ExampleBean();
        }
    }

    @Configuration
    static class BeanFactoryPostProcessorConfig {
        @Bean
        ExampleBean exampleBean() {
            return new ExampleBean();
        }

        @Bean
        static BeanFactoryPostProcessor modifyExampleBeanValue() {
            return beanFactory -> beanFactory.getBeanDefinition("exampleBean")
                    .getPropertyValues()
                    .add("value", "modified-by-bfpp");
        }
    }

    @Configuration
    static class BeanPostProcessorConfig {
        @Bean
        ExampleBean exampleBean() {
            ExampleBean bean = new ExampleBean();
            bean.setValue("from-factory-method");
            return bean;
        }

        @Bean
        static BeanPostProcessor exampleBeanPostProcessor() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    if (bean instanceof ExampleBean exampleBean) {
                        exampleBean.setValue("modified-by-bpp");
                    }
                    return bean;
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = true)
    static class ProxiedConfig {
        @Bean
        ConfigA configA() {
            return new ConfigA();
        }

        @Bean
        ConfigB configB() {
            return new ConfigB(configA());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class NonProxiedConfig {
        @Bean
        ConfigA configA() {
            return new ConfigA();
        }

        @Bean
        ConfigB configB() {
            return new ConfigB(configA());
        }
    }

    @Component
    static class LiteConfig {
        @Bean
        ConfigA configA() {
            return new ConfigA();
        }

        @Bean
        ConfigB configB() {
            return new ConfigB(configA());
        }
    }

    static class ConfigA {
    }

    record ConfigB(ConfigA a) {
    }

    @Configuration
    static class FactoryBeanConfig {
        @Bean(name = "sequence")
        SequenceFactoryBean sequenceFactoryBean() {
            return new SequenceFactoryBean();
        }
    }

    static class SequenceFactoryBean implements FactoryBean<Long> {
        private final AtomicLong counter = new AtomicLong();

        @Override
        public Long getObject() {
            return counter.incrementAndGet();
        }

        @Override
        public Class<?> getObjectType() {
            return Long.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    @Configuration
    static class LookupConfig {
        private static final AtomicLong sequence = new AtomicLong();

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        PrototypeSequence prototypeSequence() {
            return new PrototypeSequence(sequence.incrementAndGet());
        }
    }

    record PrototypeSequence(long id) {
    }

    static class LookupConsumer {
        long nextId() {
            return prototypeSequence().id();
        }

        @Lookup
        protected PrototypeSequence prototypeSequence() {
            return null;
        }
    }

    @Configuration
    static class ConstructorCycleConfig {
        @Bean
        CycleA cycleA(CycleB cycleB) {
            return new CycleA(cycleB);
        }

        @Bean
        CycleB cycleB(CycleA cycleA) {
            return new CycleB(cycleA);
        }
    }

    record CycleA(CycleB cycleB) {
    }

    record CycleB(CycleA cycleA) {
    }

    @Configuration
    static class SetterCycleConfig {
        @Bean
        SetterA setterA() {
            return new SetterA();
        }

        @Bean
        SetterB setterB() {
            return new SetterB();
        }
    }

    static class SetterA {
        private SetterB b;

        @Autowired
        void setB(SetterB b) {
            this.b = b;
        }

        SetterB b() {
            return b;
        }
    }

    static class SetterB {
        private SetterA a;

        @Autowired
        void setA(SetterA a) {
            this.a = a;
        }

        SetterA a() {
            return a;
        }
    }

    static class ExampleBean {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        String value() {
            return value;
        }
    }
}

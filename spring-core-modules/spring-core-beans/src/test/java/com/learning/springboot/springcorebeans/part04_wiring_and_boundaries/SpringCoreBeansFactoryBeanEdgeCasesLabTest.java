package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class SpringCoreBeansFactoryBeanEdgeCasesLabTest {

    @Test
    void factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
            String[] namesWithoutEagerInit = context.getBeanFactory().getBeanNamesForType(Value.class, true, false);

            System.out.println("OBSERVE: getBeanNamesForType(..., allowEagerInit=false) relies on FactoryBean.getObjectType()");
            assertThat(namesWithoutEagerInit)
                    .contains("knownValue")
                    .doesNotContain("unknownValue");

            Value unknown = context.getBean("unknownValue", Value.class);
            assertThat(unknown.origin()).isEqualTo("unknown");
        }
    }

    @Test
    void factoryBeanWithNullObjectType_isNotDiscoverableAsProductType_butFactoryItselfIsStillRetrievable() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
            Object factory = context.getBean("&unknownValue");

            System.out.println("OBSERVE: even if product type discovery fails (getObjectType=null), you can still retrieve the FactoryBean itself");
            assertThat(factory).isInstanceOf(UnknownTypeFactoryBean.class);

            Class<?> productType = context.getType("unknownValue");
            Class<?> factoryType = context.getType("&unknownValue");

            System.out.println("OBSERVE: getType(\"unknownValue\") may be null/unknown; getType(\"&unknownValue\") is stable");
            assertThat(factoryType).isEqualTo(UnknownTypeFactoryBean.class);
            assertThat(productType).isNull();
        }
    }

    @Test
    void productVsFactoryVsProvider_whenFactoryBeanProductIsNotCached() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NonSingletonProductConfig.class)) {
            ProductConsumer consumer = context.getBean(ProductConsumer.class);
            ProviderConsumer providerConsumer = context.getBean(ProviderConsumer.class);
            FactoryConsumer factoryConsumer = context.getBean(FactoryConsumer.class);

            Value injectedOnce = consumer.value();
            Value injectedTwice = consumer.value();

            Value provided1 = providerConsumer.nextValue();
            Value provided2 = providerConsumer.nextValue();

            System.out.println("OBSERVE: FactoryBean.isSingleton()=false => each provider.getObject() can obtain a fresh product");
            System.out.println("OBSERVE: direct injection resolves once at bean creation time (consumer holds a fixed reference)");
            System.out.println("OBSERVE: injecting FactoryBean<T> gives you the factory itself (not the product)");

            assertThat(injectedOnce).isSameAs(injectedTwice);
            assertThat(provided1).isNotSameAs(provided2);
            assertThat(injectedOnce.origin()).isEqualTo("seq:1");
            assertThat(provided1.origin()).isEqualTo("seq:2");
            assertThat(provided2.origin()).isEqualTo("seq:3");

            assertThat(factoryConsumer.factory()).isInstanceOf(SequencedValueFactoryBean.class);
        }
    }

    record Value(String origin) {
    }

    static class KnownTypeFactoryBean implements FactoryBean<Value> {
        @Override
        public Value getObject() {
            return new Value("known");
        }

        @Override
        public Class<?> getObjectType() {
            return Value.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    static class UnknownTypeFactoryBean implements FactoryBean<Value> {
        @Override
        public Value getObject() {
            return new Value("unknown");
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    @Configuration
    static class Config {
        @Bean(name = "knownValue")
        FactoryBean<Value> knownTypeFactoryBean() {
            return new KnownTypeFactoryBean();
        }

        @Bean(name = "unknownValue")
        FactoryBean<Value> unknownTypeFactoryBean() {
            return new UnknownTypeFactoryBean();
        }
    }

    static class SequencedValueFactoryBean implements FactoryBean<Value> {

        private final AtomicLong sequence = new AtomicLong();

        @Override
        public Value getObject() {
            return new Value("seq:" + sequence.incrementAndGet());
        }

        @Override
        public Class<?> getObjectType() {
            return Value.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    static class ProductConsumer {
        private final Value value;

        ProductConsumer(Value value) {
            this.value = value;
        }

        Value value() {
            return value;
        }
    }

    static class ProviderConsumer {
        private final ObjectProvider<Value> provider;

        ProviderConsumer(ObjectProvider<Value> provider) {
            this.provider = provider;
        }

        Value nextValue() {
            return provider.getObject();
        }
    }

    static class FactoryConsumer {
        private final FactoryBean<Value> factory;

        FactoryConsumer(FactoryBean<Value> factory) {
            this.factory = factory;
        }

        FactoryBean<Value> factory() {
            return factory;
        }
    }

    @Configuration
    static class NonSingletonProductConfig {

        @Bean(name = "valueFactory")
        FactoryBean<Value> valueFactory() {
            return new SequencedValueFactoryBean();
        }

        @Bean
        ProductConsumer productConsumer(Value value) {
            return new ProductConsumer(value);
        }

        @Bean
        ProviderConsumer providerConsumer(ObjectProvider<Value> provider) {
            return new ProviderConsumer(provider);
        }

        @Bean
        FactoryConsumer factoryConsumer(FactoryBean<Value> valueFactory) {
            return new FactoryConsumer(valueFactory);
        }
    }
}

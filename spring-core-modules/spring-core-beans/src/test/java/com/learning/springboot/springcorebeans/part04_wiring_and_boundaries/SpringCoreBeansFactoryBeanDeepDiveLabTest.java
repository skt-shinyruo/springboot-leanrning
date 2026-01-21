package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreBeansFactoryBeanDeepDiveLabTest {

    @Test
    void factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FactoryBeanConfiguration.class)) {
            Value value = context.getBean(Value.class);
            assertThat(value.id()).isEqualTo(1L);

            Object byName = context.getBean("valueFactory");
            Object factory = context.getBean("&valueFactory");

            System.out.println("OBSERVE: getBean(\"valueFactory\") returns product; getBean(\"&valueFactory\") returns factory");
            assertThat(byName).isInstanceOf(Value.class);
            assertThat(factory).isInstanceOf(ValueFactoryBean.class);
        }
    }

    @Test
    void singletonFactoryBeanProduct_isCached_byTheContainer() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SingletonFactoryConfiguration.class)) {
            Value first = context.getBean(Value.class);
            Value second = context.getBean(Value.class);

            System.out.println("OBSERVE: FactoryBean.isSingleton() controls whether the product is cached as a singleton");
            assertThat(first).isSameAs(second);
            assertThat(first.id()).isEqualTo(1L);
        }
    }

    @Test
    void nonSingletonFactoryBeanProduct_isNotCached_byTheContainer() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrototypeFactoryConfiguration.class)) {
            Value first = context.getBean(Value.class);
            Value second = context.getBean(Value.class);

            System.out.println("OBSERVE: when FactoryBean.isSingleton() is false, each getBean returns a new product");
            assertThat(first).isNotSameAs(second);
            assertThat(first.id()).isEqualTo(1L);
            assertThat(second.id()).isEqualTo(2L);
        }
    }

    record Value(long id) {
    }

    static class ValueFactoryBean implements FactoryBean<Value> {

        private final AtomicLong sequence;
        private final boolean singleton;

        ValueFactoryBean(AtomicLong sequence, boolean singleton) {
            this.sequence = sequence;
            this.singleton = singleton;
        }

        @Override
        public Value getObject() {
            return new Value(sequence.incrementAndGet());
        }

        @Override
        public Class<?> getObjectType() {
            return Value.class;
        }

        @Override
        public boolean isSingleton() {
            return singleton;
        }
    }

    @Configuration
    static class FactoryBeanConfiguration {
        @Bean(name = "valueFactory")
        ValueFactoryBean valueFactoryBean() {
            return new ValueFactoryBean(new AtomicLong(), true);
        }
    }

    @Configuration
    static class SingletonFactoryConfiguration {
        @Bean
        ValueFactoryBean valueFactory() {
            return new ValueFactoryBean(new AtomicLong(), true);
        }
    }

    @Configuration
    static class PrototypeFactoryConfiguration {
        @Bean
        ValueFactoryBean valueFactory() {
            return new ValueFactoryBean(new AtomicLong(), false);
        }
    }
}

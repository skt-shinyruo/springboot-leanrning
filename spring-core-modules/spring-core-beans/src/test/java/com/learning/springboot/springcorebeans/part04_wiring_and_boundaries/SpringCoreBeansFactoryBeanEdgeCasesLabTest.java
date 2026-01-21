package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SpringCoreBeansFactoryBeanEdgeCasesLabTest {

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
}

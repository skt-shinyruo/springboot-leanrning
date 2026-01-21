package com.learning.springboot.springcorebeans.appendix;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

@EnabledIfSystemProperty(named = "springcorebeans.explore", matches = "true")
class SpringCoreBeansSingletonCacheExploreTest {

    @Test
    void singletonObjects_cacheContainsSingletonButNotPrototype() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        RootBeanDefinition singletonDef = new RootBeanDefinition(Demo.class);
        singletonDef.setScope(RootBeanDefinition.SCOPE_SINGLETON);
        beanFactory.registerBeanDefinition("singletonDemo", singletonDef);

        RootBeanDefinition prototypeDef = new RootBeanDefinition(Demo.class);
        prototypeDef.setScope(RootBeanDefinition.SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("prototypeDemo", prototypeDef);

        Object singleton1 = beanFactory.getBean("singletonDemo");
        Object singleton2 = beanFactory.getBean("singletonDemo");
        Object proto1 = beanFactory.getBean("prototypeDemo");
        Object proto2 = beanFactory.getBean("prototypeDemo");

        System.out.println("OBSERVE: singleton getBean twice returns same instance");
        System.out.println("OBSERVE: prototype getBean twice returns different instances");

        assertThat(singleton1).isSameAs(singleton2);
        assertThat(proto1).isNotSameAs(proto2);

        @SuppressWarnings("unchecked")
        Map<String, Object> singletonObjects = (Map<String, Object>) getField(beanFactory, "singletonObjects");

        System.out.println("OBSERVE: DefaultSingletonBeanRegistry.singletonObjects contains singleton instances");
        assertThat(singletonObjects).containsKey("singletonDemo");
        assertThat(singletonObjects).doesNotContainKey("prototypeDemo");
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    static class Demo {}
}


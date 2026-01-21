package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansBeanDefinitionOverridingLabTest {

    @Test
    void whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
            beanFactory.setAllowBeanDefinitionOverriding(true);

            context.registerBean("duplicate", Marker.class, () -> new Marker("first"));
            context.registerBean("duplicate", Marker.class, () -> new Marker("second"));
            context.refresh();

            System.out.println("OBSERVE: allowBeanDefinitionOverriding=true => the last registered definition wins");
            assertThat(context.getBean(Marker.class).origin()).isEqualTo("second");
        }
    }

    @Test
    void whenBeanDefinitionOverridingIsDisallowed_registeringSameBeanNameFailsFast() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
            beanFactory.setAllowBeanDefinitionOverriding(false);

            context.registerBean("duplicate", Marker.class, () -> new Marker("first"));

            System.out.println("OBSERVE: allowBeanDefinitionOverriding=false => duplicate bean names fail fast");
            assertThatThrownBy(() -> context.registerBean("duplicate", Marker.class, () -> new Marker("second")))
                    .isInstanceOf(BeanDefinitionOverrideException.class);
        }
    }

    record Marker(String origin) {
    }
}

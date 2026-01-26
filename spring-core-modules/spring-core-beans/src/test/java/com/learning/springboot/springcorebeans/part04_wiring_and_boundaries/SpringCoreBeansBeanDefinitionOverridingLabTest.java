package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.springboot.springcorebeans.testsupport.BeanDefinitionOriginDumper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

public class SpringCoreBeansBeanDefinitionOverridingLabTest {

    @Test
    void whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
            beanFactory.setAllowBeanDefinitionOverriding(true);

            RootBeanDefinition first = new RootBeanDefinition(Marker.class);
            first.setInstanceSupplier(() -> new Marker("first"));
            first.setResourceDescription("lab:first-definition");
            first.setSource("lab-source:first");
            context.registerBeanDefinition("duplicate", first);

            RootBeanDefinition second = new RootBeanDefinition(Marker.class);
            second.setInstanceSupplier(() -> new Marker("second"));
            second.setResourceDescription("lab:second-definition");
            second.setSource("lab-source:second");
            context.registerBeanDefinition("duplicate", second);
            context.refresh();

            System.out.println("OBSERVE: allowBeanDefinitionOverriding=true => the last registered definition wins");
            assertThat(context.getBean(Marker.class).origin()).isEqualTo("second");

            String dump = BeanDefinitionOriginDumper.dump(context.getDefaultListableBeanFactory(), "duplicate");
            System.out.println(dump);
            assertThat(dump).contains("lab:second-definition");
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

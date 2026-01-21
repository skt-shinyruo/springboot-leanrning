package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.context.annotation.Bean;

class SpringCoreBeansAutoConfigurationBackoffTimingLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DefaultGreetingAutoConfiguration.class));

    @Test
    void lateBeanDefinitionRegistration_canBypassConditionalOnMissingBean_andCauseDuplicateCandidates() {
        runner
                .withBean(LateGreetingRegistrar.class, LateGreetingRegistrar::new)
                .run(context -> {
                    Map<String, DemoGreeting> greetings = context.getBeansOfType(DemoGreeting.class);
                    assertThat(greetings).hasSize(2);
                    assertThat(context).hasSingleBean(AutoConfiguredGreeting.class);
                    assertThat(context).hasSingleBean(UserProvidedGreeting.class);

                    System.out.println("OBSERVE: @ConditionalOnMissingBean is evaluated during definition registration (not after refresh finishes)");
                    System.out.println("OBSERVE: LateGreetingRegistrar runs AFTER ConfigurationClassPostProcessor => auto-config already registered default bean");
                    System.out.println("OBSERVE: result => two DemoGreeting candidates exist (may cause NoUniqueBeanDefinitionException in real apps)");
                });
    }

    @Test
    void earlyBeanDefinitionRegistration_runsBeforeConfigurationClassPostProcessor_soAutoConfigurationBacksOffDeterministically() {
        runner
                .withBean(EarlyGreetingRegistrar.class, EarlyGreetingRegistrar::new)
                .run(context -> {
                    Map<String, DemoGreeting> greetings = context.getBeansOfType(DemoGreeting.class);
                    assertThat(greetings).hasSize(1);
                    assertThat(context).doesNotHaveBean(AutoConfiguredGreeting.class);
                    assertThat(context).hasSingleBean(UserProvidedGreeting.class);

                    System.out.println("OBSERVE: EarlyGreetingRegistrar is PriorityOrdered => runs BEFORE ConfigurationClassPostProcessor");
                    System.out.println("OBSERVE: override bean definition is visible to @ConditionalOnMissingBean => auto-config backs off");
                });
    }

    interface DemoGreeting {
        String origin();
    }

    static class AutoConfiguredGreeting implements DemoGreeting {
        @Override
        public String origin() {
            return "auto-config";
        }
    }

    static class UserProvidedGreeting implements DemoGreeting {
        @Override
        public String origin() {
            return "user-registrar";
        }
    }

    @AutoConfiguration
    static class DefaultGreetingAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean(DemoGreeting.class)
        DemoGreeting demoGreeting() {
            return new AutoConfiguredGreeting();
        }
    }

    static class LateGreetingRegistrar implements BeanDefinitionRegistryPostProcessor, Ordered {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
            BeanDefinition userGreeting = new RootBeanDefinition(UserProvidedGreeting.class);
            registry.registerBeanDefinition("userProvidedGreeting", userGreeting);
        }

        @Override
        public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    static class EarlyGreetingRegistrar implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
            BeanDefinition userGreeting = new RootBeanDefinition(UserProvidedGreeting.class);
            registry.registerBeanDefinition("userProvidedGreeting", userGreeting);
        }

        @Override
        public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}


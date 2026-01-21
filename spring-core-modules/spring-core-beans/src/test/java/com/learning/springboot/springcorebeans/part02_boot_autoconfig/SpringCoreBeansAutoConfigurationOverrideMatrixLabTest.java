package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import com.learning.springboot.springcorebeans.testsupport.BeanGraphDumper;

class SpringCoreBeansAutoConfigurationOverrideMatrixLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DefaultGreetingAutoConfiguration.class))
            .withUserConfiguration(ClientConfiguration.class);

    @Test
    void duplicateCandidates_failFast_forSingleInjection_whenNoPrimaryOrQualifierIsPresent() {
        runner.withBean(LateGreetingRegistrar.class, LateGreetingRegistrar::new)
                .run(context -> {
                    Throwable failure = context.getStartupFailure();
                    assertThat(failure).isNotNull();
                    assertThat(failure).hasRootCauseInstanceOf(NoUniqueBeanDefinitionException.class);

                    System.out.println("OBSERVE: two DemoGreeting candidates => single injection fails fast (NoUniqueBeanDefinitionException)");
                    System.out.println("OBSERVE: fix path A => make injection deterministic (@Primary/@Qualifier)");
                    System.out.println("OBSERVE: fix path B => make auto-config back off (ensure override exists BEFORE condition evaluation)");
                });
    }

    @Test
    void duplicateCandidates_canBeMadeDeterministic_withPrimary_evenIfBothBeansExist() {
        runner.withBean(LatePrimaryGreetingRegistrar.class, LatePrimaryGreetingRegistrar::new)
                .run(context -> {
                    assertThat(context.getStartupFailure()).isNull();

                    Map<String, DemoGreeting> greetings = context.getBeansOfType(DemoGreeting.class);
                    assertThat(greetings).hasSize(2);

                    GreeterClient client = context.getBean(GreeterClient.class);
                    assertThat(client.greetingOrigin()).isEqualTo("user-registrar");

                    System.out.println("OBSERVE: still two candidates exist, but @Primary makes single injection deterministic");
                    System.out.println(BeanGraphDumper.dumpCandidates(context.getBeanFactory(), DemoGreeting.class));
                });
    }

    @Test
    void autoConfigurationBacksOff_whenOverrideBeanDefinitionIsRegisteredEarly_soOnlyOneCandidateRemains() {
        runner.withBean(EarlyGreetingRegistrar.class, EarlyGreetingRegistrar::new)
                .run(context -> {
                    assertThat(context.getStartupFailure()).isNull();

                    Map<String, DemoGreeting> greetings = context.getBeansOfType(DemoGreeting.class);
                    assertThat(greetings).hasSize(1);
                    assertThat(context).doesNotHaveBean(AutoConfiguredGreeting.class);
                    assertThat(context).hasSingleBean(UserProvidedGreeting.class);

                    GreeterClient client = context.getBean(GreeterClient.class);
                    assertThat(client.greetingOrigin()).isEqualTo("user-registrar");

                    System.out.println("OBSERVE: early override => @ConditionalOnMissingBean sees it => auto-config backs off (only one candidate)");
                });
    }

    @Test
    void qualifier_canAlsoResolveAmbiguity_explicitlySelectingTheTargetBean() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DefaultGreetingAutoConfiguration.class))
                .withUserConfiguration(QualifierClientConfiguration.class)
                .withBean(LateGreetingRegistrar.class, LateGreetingRegistrar::new)
                .run(context -> {
                    assertThat(context.getStartupFailure()).isNull();

                    Map<String, DemoGreeting> greetings = context.getBeansOfType(DemoGreeting.class);
                    assertThat(greetings).hasSize(2);

                    QualifiedGreeterClient client = context.getBean(QualifiedGreeterClient.class);
                    assertThat(client.greetingOrigin()).isEqualTo("user-registrar");
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

    record GreeterClient(DemoGreeting greeting) {
        String greetingOrigin() {
            return greeting.origin();
        }
    }

    record QualifiedGreeterClient(DemoGreeting greeting) {
        String greetingOrigin() {
            return greeting.origin();
        }
    }

    @Configuration
    static class ClientConfiguration {
        @Bean
        GreeterClient greeterClient(DemoGreeting greeting) {
            return new GreeterClient(greeting);
        }
    }

    @Configuration
    static class QualifierClientConfiguration {
        @Bean
        QualifiedGreeterClient qualifiedGreeterClient(@Qualifier("userProvidedGreeting") DemoGreeting greeting) {
            return new QualifiedGreeterClient(greeting);
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
            registry.registerBeanDefinition("userProvidedGreeting", new RootBeanDefinition(UserProvidedGreeting.class));
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    static class LatePrimaryGreetingRegistrar implements BeanDefinitionRegistryPostProcessor, Ordered {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
            BeanDefinition userGreeting = new RootBeanDefinition(UserProvidedGreeting.class);
            ((AbstractBeanDefinition) userGreeting).setPrimary(true);
            registry.registerBeanDefinition("userProvidedGreeting", userGreeting);
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    static class EarlyGreetingRegistrar implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
            registry.registerBeanDefinition("userProvidedGreeting", new RootBeanDefinition(UserProvidedGreeting.class));
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}

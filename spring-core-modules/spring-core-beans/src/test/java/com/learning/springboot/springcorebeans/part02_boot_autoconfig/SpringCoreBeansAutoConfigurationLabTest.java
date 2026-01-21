package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.learning.springboot.springcorebeans.testsupport.OptionalLibraryMarker;

class SpringCoreBeansAutoConfigurationLabTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DemoAutoConfiguration.class));

    @Test
    void conditionalOnPropertyDoesNotMatchWhenPropertyIsMissing() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(PropertyGatedBean.class);
            System.out.println("OBSERVE: @ConditionalOnProperty demo.feature.enabled is missing => PropertyGatedBean NOT registered");
        });
    }

    @Test
    void conditionalOnPropertyMatchesWhenEnabled() {
        contextRunner
                .withPropertyValues("demo.feature.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(PropertyGatedBean.class);
                    assertThat(context.getBean(PropertyGatedBean.class).origin()).isEqualTo("enabled-by-property");
                    System.out.println("OBSERVE: @ConditionalOnProperty demo.feature.enabled=true => PropertyGatedBean registered");
                });
    }

    @Test
    void conditionalOnClassMatchesWhenOptionalTypeIsPresent() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ClasspathGatedBean.class);
            assertThat(context.getBean(ClasspathGatedBean.class).origin()).isEqualTo("classpath-has-OptionalLibraryMarker");
            System.out.println("OBSERVE: @ConditionalOnClass(OptionalLibraryMarker) => ClasspathGatedBean registered");
        });
    }

    @Test
    void conditionalOnClassDoesNotMatchWhenOptionalTypeIsMissing() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(OptionalLibraryMarker.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ClasspathGatedBean.class);
                    System.out.println("OBSERVE: FilteredClassLoader hides OptionalLibraryMarker => ClasspathGatedBean NOT registered");
                });
    }

    @Test
    void conditionalOnMissingBeanProvidesDefaultWhenUserBeanIsMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DemoGreeting.class);
            assertThat(context.getBean(DemoGreeting.class).greeting()).contains("auto-config");
            System.out.println("OBSERVE: @ConditionalOnMissingBean => DemoGreeting provided by auto-configuration");
        });
    }

    @Test
    void conditionalOnMissingBeanBacksOffWhenUserProvidesBean() {
        contextRunner
                .withUserConfiguration(UserGreetingConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DemoGreeting.class);
                    assertThat(context.getBean(DemoGreeting.class)).isInstanceOf(UserProvidedGreeting.class);
                    System.out.println("OBSERVE: user @Bean DemoGreeting present => auto-config backs off (no duplicate)");
                });
    }

    record PropertyGatedBean(String origin) {
    }

    record ClasspathGatedBean(String origin) {
    }

    interface DemoGreeting {
        String greeting();
    }

    static class AutoConfiguredGreeting implements DemoGreeting {
        @Override
        public String greeting() {
            return "hello-from-auto-config";
        }
    }

    static class UserProvidedGreeting implements DemoGreeting {
        @Override
        public String greeting() {
            return "hello-from-user-config";
        }
    }

    @AutoConfiguration
    static class DemoAutoConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = "demo.feature", name = "enabled", havingValue = "true")
        PropertyGatedBean propertyGatedBean() {
            return new PropertyGatedBean("enabled-by-property");
        }

        @Bean
        @ConditionalOnClass(OptionalLibraryMarker.class)
        ClasspathGatedBean classpathGatedBean() {
            return new ClasspathGatedBean("classpath-has-OptionalLibraryMarker");
        }

        @Bean
        @ConditionalOnMissingBean(DemoGreeting.class)
        DemoGreeting demoGreeting() {
            return new AutoConfiguredGreeting();
        }
    }

    @Configuration
    static class UserGreetingConfiguration {
        @Bean
        DemoGreeting demoGreeting() {
            return new UserProvidedGreeting();
        }
    }
}

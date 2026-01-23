package com.learning.springboot.bootautoconfiguration.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootautoconfiguration.autoconfig.GreetingAutoConfiguration;
import com.learning.springboot.bootautoconfiguration.autoconfig.GreetingDecoratorAutoConfiguration;
import com.learning.springboot.bootautoconfiguration.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class BootAutoConfigurationLabTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    GreetingAutoConfiguration.class,
                    GreetingDecoratorAutoConfiguration.class));

    @Test
    void autoConfigCreatesDefaultBeanWhenEnabled() {
        runner.run(context -> {
            assertThat(context.getBeansOfType(GreetingService.class)).hasSize(1);
            GreetingService service = context.getBean(GreetingService.class);
            assertThat(service.greet("Alice")).contains("Alice");
        });
    }

    @Test
    void decoratorCreatesPrimaryBeanWhenEnabled() {
        runner.withPropertyValues("demo.greeting.decorate=true")
                .run(context -> {
                    assertThat(context.containsBean("defaultGreetingService")).isTrue();
                    assertThat(context.containsBean("greetingService")).isTrue();

                    GreetingService service = context.getBean(GreetingService.class);
                    assertThat(service.greet("Alice")).startsWith("LOG(");
                });
    }

    @Test
    void userBeanOverridesAutoConfig_backoffOccurs() {
        ApplicationContextRunner customRunner = runner
                .withUserConfiguration(UserConfig.class)
                .withPropertyValues("demo.greeting.decorate=true");

        customRunner.run(context -> {
            assertThat(context.containsBean("defaultGreetingService")).isFalse();
            assertThat(context.containsBean("greetingService")).isFalse();
            assertThat(context.containsBean("userGreetingService")).isTrue();

            assertThat(context.getBeansOfType(GreetingService.class)).hasSize(1);
            GreetingService service = context.getBean(GreetingService.class);
            assertThat(service.greet("Alice")).isEqualTo("USER:Alice");
        });
    }

    @Configuration
    static class UserConfig {

        @Bean
        GreetingService userGreetingService() {
            return name -> "USER:" + name;
        }
    }
}

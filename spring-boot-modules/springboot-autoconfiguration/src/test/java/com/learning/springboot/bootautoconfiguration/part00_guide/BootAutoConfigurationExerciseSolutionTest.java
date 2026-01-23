package com.learning.springboot.bootautoconfiguration.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootautoconfiguration.autoconfig.GreetingAutoConfiguration;
import com.learning.springboot.bootautoconfiguration.autoconfig.GreetingDecoratorAutoConfiguration;
import com.learning.springboot.bootautoconfiguration.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 参考实现：对齐 BootAutoConfigurationExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class BootAutoConfigurationExerciseSolutionTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SpecialGreetingAutoConfiguration.class,
                    GreetingAutoConfiguration.class,
                    GreetingDecoratorAutoConfiguration.class));

    @Test
    void solution_addAConditionalBeanAndVerifyBackoff() {
        runner.withPropertyValues(
                        "demo.greeting.enabled=true",
                        "demo.greeting.decorate=true")
                .run(context -> {
                    assertThat(context.containsBean("defaultGreetingService")).isTrue();
                    assertThat(context.containsBean("greetingService")).isTrue();

                    GreetingService delegate = (GreetingService) context.getBean("defaultGreetingService");
                    assertThat(delegate).isInstanceOf(SpecialGreetingService.class);

                    GreetingService service = context.getBean(GreetingService.class);
                    assertThat(service.greet("Alice")).isEqualTo("LOG(SPECIAL:Alice)");
                });
    }

    @Test
    void solution_userBeanOverridesAutoConfig_backoffOccurs() {
        ApplicationContextRunner customRunner = runner
                .withUserConfiguration(UserConfig.class)
                .withPropertyValues(
                        "demo.greeting.enabled=true",
                        "demo.greeting.decorate=true");

        customRunner.run(context -> {
            assertThat(context.containsBean("defaultGreetingService")).isFalse();
            assertThat(context.containsBean("greetingService")).isFalse();
            assertThat(context.containsBean("userGreetingService")).isTrue();

            assertThat(context.getBeansOfType(GreetingService.class)).hasSize(1);
            GreetingService service = context.getBean(GreetingService.class);
            assertThat(service.greet("Alice")).isEqualTo("USER:Alice");
        });
    }

    static class SpecialGreetingService implements GreetingService {
        @Override
        public String greet(String name) {
            return "SPECIAL:" + name;
        }
    }

    @AutoConfiguration(before = GreetingAutoConfiguration.class)
    static class SpecialGreetingAutoConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = "demo.greeting", name = { "decorate", "enabled" }, havingValue = "true")
        @ConditionalOnMissingBean(GreetingService.class)
        public GreetingService defaultGreetingService() {
            return new SpecialGreetingService();
        }
    }

    @Configuration
    static class UserConfig {

        @Bean
        GreetingService userGreetingService() {
            return name -> "USER:" + name;
        }
    }
}


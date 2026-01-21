package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

class SpringCoreBeansEnvironmentPropertySourceLabTest {

    @Test
    void programmaticPropertySourceAddedBeforeRefresh_hasHigherPrecedenceThanPropertySourceAnnotation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            ConfigurableEnvironment environment = context.getEnvironment();
            environment.getPropertySources().addFirst(new MapPropertySource("programmatic-override", Map.of(
                    "demo.name", "fromProgrammatic",
                    "demo.onlyProgrammatic", "p")));

            context.register(EnvConfig.class);
            context.refresh();

            Target target = context.getBean("eagerTarget", Target.class);

            assertThat(target.name()).isEqualTo("fromProgrammatic");
            assertThat(target.onlyInFile()).isEqualTo("fromPropertySource");
            assertThat(target.onlyProgrammatic()).isEqualTo("p");

            assertThat(environment.getProperty("demo.name")).isEqualTo("fromProgrammatic");
            assertThat(environment.getPropertySources().stream().map(org.springframework.core.env.PropertySource::getName))
                    .anyMatch(name -> name.contains("lab.properties"));

            System.out.println("OBSERVE: Environment resolves properties by PropertySources precedence (earlier sources win)");
            System.out.println("OBSERVE: @PropertySource contributes a PropertySource into Environment during configuration parsing");
        }
    }

    @Test
    void addingPropertySourceAfterRefresh_affectsLazyBeans_butNotAlreadyCreatedOnes() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EnvConfig.class)) {
            Target eager = context.getBean("eagerTarget", Target.class);
            assertThat(eager.name()).isEqualTo("fromPropertySource");

            context.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("late-override", Map.of("demo.name", "lateOverride")));

            Target lazy = context.getBean("lazyTarget", Target.class);
            assertThat(lazy.name()).isEqualTo("lateOverride");

            assertThat(eager.name())
                    .as("已创建的 bean 不会因为后续修改 Environment 而自动更新注入值")
                    .isEqualTo("fromPropertySource");

            System.out.println("OBSERVE: Changing Environment after refresh affects only beans created after the change");
        }
    }

    @Configuration
    @PropertySource("classpath:part04_wiring_and_boundaries/environment/lab.properties")
    static class EnvConfig {

        @Bean
        Target eagerTarget() {
            return new Target();
        }

        @Bean
        @Lazy
        Target lazyTarget() {
            return new Target();
        }
    }

    static class Target {

        @Value("${demo.name}")
        private String name;

        @Value("${demo.onlyInFile}")
        private String onlyInFile;

        @Value("${demo.onlyProgrammatic:}")
        private String onlyProgrammatic;

        String name() {
            return name;
        }

        String onlyInFile() {
            return onlyInFile;
        }

        String onlyProgrammatic() {
            return onlyProgrammatic;
        }
    }
}

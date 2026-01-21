package com.learning.springboot.springcorebeans.part02_boot_autoconfig;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

class SpringCoreBeansProfileRegistrationLabTest {

    @Test
    void profileControlledBeans_areNotRegisteredWhenProfileInactive() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProfileConfig.class)) {
            assertThat(context.getBeansOfType(ProfileMarker.class)).isEmpty();
        }
    }

    @Test
    void profileControlledBeans_areRegisteredWhenProfileActive() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("dev");
            context.register(ProfileConfig.class);
            context.refresh();

            ProfileMarker marker = context.getBean(ProfileMarker.class);
            assertThat(marker.profile()).isEqualTo("dev");
        }
    }

    @Configuration
    static class ProfileConfig {

        @Bean
        @Profile("dev")
        ProfileMarker devMarker() {
            return new ProfileMarker("dev");
        }

        @Bean
        @Profile("prod")
        ProfileMarker prodMarker() {
            return new ProfileMarker("prod");
        }
    }

    record ProfileMarker(String profile) {
    }
}


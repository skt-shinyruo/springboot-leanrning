package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

class SpringCoreBeansTypeConversionLabTest {

    @Test
    void stringPropertyValue_canBeConvertedToIntDuringPopulateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StringToIntConfig.class)) {
            ServerPortHolder holder = context.getBean(ServerPortHolder.class);
            assertThat(holder.port()).isEqualTo(8080);
        }
    }

    @Test
    void customConversionService_canConvertStringToCustomValueObjectDuringPopulateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CustomConversionConfig.class)) {
            UserIdConsumer consumer = context.getBean(UserIdConsumer.class);
            assertThat(consumer.userId().value()).isEqualTo(42L);
        }
    }

    @Configuration
    static class StringToIntConfig {

        @Bean
        ServerPortHolder serverPortHolder() {
            return new ServerPortHolder();
        }

        @Bean
        static BeanFactoryPostProcessor setPortAsString() {
            return beanFactory -> beanFactory.getBeanDefinition("serverPortHolder")
                    .getPropertyValues()
                    .add("port", "8080");
        }
    }

    @Configuration
    static class CustomConversionConfig {

        @Bean
        ConversionService conversionService() {
            DefaultConversionService conversionService = new DefaultConversionService();
            conversionService.addConverter(new Converter<String, UserId>() {
                @Override
                public UserId convert(String source) {
                    return new UserId(Long.parseLong(source));
                }
            });
            return conversionService;
        }

        @Bean
        UserIdConsumer userIdConsumer() {
            return new UserIdConsumer();
        }

        @Bean
        static BeanFactoryPostProcessor setUserIdAsString() {
            return beanFactory -> beanFactory.getBeanDefinition("userIdConsumer")
                    .getPropertyValues()
                    .add("userId", "42");
        }
    }

    static class ServerPortHolder {
        private int port;

        public void setPort(int port) {
            this.port = port;
        }

        int port() {
            return port;
        }
    }

    record UserId(long value) {
    }

    static class UserIdConsumer {
        private UserId userId;

        public void setUserId(UserId userId) {
            this.userId = userId;
        }

        UserId userId() {
            return userId;
        }
    }
}


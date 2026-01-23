package com.learning.springboot.bootautoconfiguration.autoconfig;

import com.learning.springboot.bootautoconfiguration.service.GreetingService;
import com.learning.springboot.bootautoconfiguration.service.LoggingGreetingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration(after = GreetingAutoConfiguration.class)
public class GreetingDecoratorAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "demo.greeting", name = "decorate", havingValue = "true")
    @ConditionalOnBean(name = "defaultGreetingService")
    public GreetingService greetingService(@Qualifier("defaultGreetingService") GreetingService delegate) {
        return new LoggingGreetingService(delegate);
    }
}

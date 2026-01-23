package com.learning.springboot.bootautoconfiguration.autoconfig;

import com.learning.springboot.bootautoconfiguration.service.DefaultGreetingService;
import com.learning.springboot.bootautoconfiguration.service.GreetingService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "demo.greeting", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(GreetingService.class)
    public GreetingService defaultGreetingService(GreetingProperties properties) {
        return new DefaultGreetingService(properties.message());
    }
}

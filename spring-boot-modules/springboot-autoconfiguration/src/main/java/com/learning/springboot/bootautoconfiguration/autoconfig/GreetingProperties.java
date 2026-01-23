package com.learning.springboot.bootautoconfiguration.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.greeting")
public record GreetingProperties(String message, boolean enabled, boolean decorate) {

    public GreetingProperties() {
        this("你好，AutoConfiguration", true, false);
    }
}

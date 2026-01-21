package com.learning.springboot.bootwebmvc.part05_real_world;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdvancedCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/advanced/cors/**")
                .allowedOrigins("https://example.com")
                .allowedMethods("GET")
                .allowedHeaders("X-Request-Id")
                .maxAge(3600);
    }
}


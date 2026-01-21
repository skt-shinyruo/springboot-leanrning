package com.learning.springboot.bootwebmvc.part03_internals;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcTraceConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcTraceFilter webMvcTraceFilter() {
        return new WebMvcTraceFilter();
    }

    @Bean
    public WebMvcTraceInterceptor webMvcTraceInterceptor() {
        return new WebMvcTraceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webMvcTraceInterceptor())
                .addPathPatterns("/api/advanced/trace/**");
    }
}


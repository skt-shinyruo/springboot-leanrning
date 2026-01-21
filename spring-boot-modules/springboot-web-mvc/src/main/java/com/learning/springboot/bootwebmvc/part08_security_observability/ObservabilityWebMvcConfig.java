package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本配置用于演示：Interceptor 属于 MVC 链路（在 DispatcherServlet 内部），与 Filter/Security 位置不同。

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ObservabilityWebMvcConfig implements WebMvcConfigurer {

    @Bean
    public TimingInterceptor timingInterceptor() {
        return new TimingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timingInterceptor())
                .addPathPatterns("/api/advanced/**");
    }
}


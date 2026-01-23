package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本配置用于演示 Spring Security 与 Web MVC 的相对位置：FilterChainProxy 在 DispatcherServlet 之前。

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 仅保护教学端点：/api/advanced/secure/**
     * - 目标：不影响既有 Labs（例如 /api/users、/api/advanced/contract/** 等）
     * - 额外：保留 CSRF 以演示 403 分支（缺失 token）
     */
    @Bean
    @Order(1)
    SecurityFilterChain secureApiChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/advanced/secure/**");

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/advanced/secure/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.httpBasic(withDefaults());
        return http.build();
    }

    /**
     * 默认链路：全部放行 + 关闭 CSRF。
     * - 目标：保证现有 POST（例如 /api/users、/api/advanced/contract/echo）不因 CSRF 变成 403。
     */
    @Bean
    @Order(2)
    SecurityFilterChain permitAllChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    UserDetailsService demoUsers() {
        UserDetails user = User.withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}


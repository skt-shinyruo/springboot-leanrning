package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本拦截器用于演示“最小观测”：用响应头把一次请求的耗时暴露出来，便于与指标对照理解。

import org.springframework.web.servlet.HandlerInterceptor;

public class TimingInterceptor implements HandlerInterceptor {

    static final String START_NANOS_ATTR = TimingInterceptor.class.getName() + ".startNanos";

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) {
        request.setAttribute(START_NANOS_ATTR, System.nanoTime());
        return true;
    }
}

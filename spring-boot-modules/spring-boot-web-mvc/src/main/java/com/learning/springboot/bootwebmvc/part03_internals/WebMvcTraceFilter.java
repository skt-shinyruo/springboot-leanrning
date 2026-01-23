package com.learning.springboot.bootwebmvc.part03_internals;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 教学用：用事件序列把 Filter 与 MVC（Interceptor/handler）之间的位置关系变成可断言证据。
 */
public class WebMvcTraceFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/api/advanced/trace/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        WebMvcTraceSupport.record(request, "filter:before[" + WebMvcTraceSupport.dispatch(request) + "]");
        try {
            filterChain.doFilter(request, response);
        } finally {
            WebMvcTraceSupport.record(request, "filter:after[" + WebMvcTraceSupport.dispatch(request) + "]");
        }
    }
}


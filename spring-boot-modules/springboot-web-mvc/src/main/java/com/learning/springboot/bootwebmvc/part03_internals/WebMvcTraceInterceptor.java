package com.learning.springboot.bootwebmvc.part03_internals;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 教学用：把 Interceptor 的 sync 与 async lifecycle 回调变成可观察事件。
 */
public class WebMvcTraceInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        WebMvcTraceSupport.record(request, "interceptor:preHandle[" + WebMvcTraceSupport.dispatch(request) + "]");
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable ModelAndView modelAndView
    ) {
        WebMvcTraceSupport.record(request, "interceptor:postHandle[" + WebMvcTraceSupport.dispatch(request) + "]");
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            @Nullable Exception ex
    ) {
        WebMvcTraceSupport.record(request, "interceptor:afterCompletion[" + WebMvcTraceSupport.dispatch(request) + "]");
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        WebMvcTraceSupport.record(request, "interceptor:afterConcurrentHandlingStarted[" + WebMvcTraceSupport.dispatch(request) + "]");
    }
}


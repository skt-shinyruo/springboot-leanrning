package com.learning.springboot.bootwebmvc.part08_security_observability;

// 本 Advice 用于把 TimingInterceptor 记录的耗时写入响应头（发生在 body 写出之前，避免“写太晚导致 header 丢失”）。

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class TimingResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return body;
        }

        HttpServletRequest rawRequest = servletRequest.getServletRequest();
        String uri = rawRequest.getRequestURI();
        if (uri == null || !uri.startsWith("/api/advanced/")) {
            return body;
        }

        Object raw = rawRequest.getAttribute(TimingInterceptor.START_NANOS_ATTR);
        if (!(raw instanceof Long startNanos)) {
            return body;
        }

        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
        response.getHeaders().add("X-Lab-Elapsed-Ms", String.valueOf(elapsedMs));
        return body;
    }
}


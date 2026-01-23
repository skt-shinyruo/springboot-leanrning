package com.learning.springboot.bootwebmvc.part03_internals;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class MessageConverterTraceAdvice implements ResponseBodyAdvice<Object> {

    public static final String HEADER_SELECTED_CONVERTER = "X-Lab-Selected-Converter";
    public static final String HEADER_SELECTED_CONTENT_TYPE = "X-Lab-Selected-Content-Type";

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
        if (uri == null || !uri.startsWith("/api/advanced/message-converters/")) {
            return body;
        }

        if (selectedConverterType != null) {
            response.getHeaders().add(HEADER_SELECTED_CONVERTER, selectedConverterType.getSimpleName());
        }
        if (selectedContentType != null) {
            response.getHeaders().add(HEADER_SELECTED_CONTENT_TYPE, selectedContentType.toString());
        }
        return body;
    }
}


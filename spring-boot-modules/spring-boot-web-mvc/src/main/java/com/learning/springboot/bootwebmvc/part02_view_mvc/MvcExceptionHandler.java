package com.learning.springboot.bootwebmvc.part02_view_mvc;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.learning.springboot.bootwebmvc.part01_web_mvc.ApiError;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "com.learning.springboot.bootwebmvc.part02_view_mvc")
public class MvcExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Object handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        if (acceptsJson(request)) {
            String message = status == HttpStatus.NOT_FOUND ? "not_found" : "page_error";
            return ResponseEntity.status(status).body(new ApiError(message, Map.of()));
        }

        String viewName = status == HttpStatus.NOT_FOUND ? "error/404" : "error/4xx";
        if (status.is5xxServerError()) {
            viewName = "error/5xx";
        }

        ModelAndView mav = new ModelAndView(viewName);
        mav.setStatus(status);
        if (ex.getReason() != null && !ex.getReason().isBlank()) {
            mav.addObject("customMessage", ex.getReason());
        }
        return mav;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        if (acceptsJson(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError("type_mismatch", Map.of(ex.getName(), "类型不匹配")));
        }

        ModelAndView mav = new ModelAndView("error/4xx");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("customMessage", "参数类型不匹配");
        return mav;
    }

    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        if (acceptsJson(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("page_error", Map.of()));
        }

        ModelAndView mav = new ModelAndView("error/5xx");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("customMessage", "页面处理失败（示例异常）");
        return mav;
    }

    private static boolean acceptsJson(HttpServletRequest request) {
        String rawAccept = request.getHeader(HttpHeaders.ACCEPT);
        if (rawAccept == null || rawAccept.isBlank()) {
            return false;
        }
        return rawAccept.contains(MediaType.APPLICATION_JSON_VALUE) || rawAccept.contains("application/*+json");
    }
}

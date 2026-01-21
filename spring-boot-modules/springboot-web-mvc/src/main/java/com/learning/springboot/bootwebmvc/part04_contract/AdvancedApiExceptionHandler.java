package com.learning.springboot.bootwebmvc.part04_contract;

import java.util.LinkedHashMap;
import java.util.Map;

import com.learning.springboot.bootwebmvc.part01_web_mvc.ApiError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(basePackages = {
        "com.learning.springboot.bootwebmvc.part03_internals",
        "com.learning.springboot.bootwebmvc.part04_contract",
        "com.learning.springboot.bootwebmvc.part05_real_world",
        "com.learning.springboot.bootwebmvc.part06_async_sse"
})
public class AdvancedApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("validation_failed", fieldErrors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("validation_failed", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("malformed_json", Map.of()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> fieldErrors = Map.of(ex.getName(), "类型不匹配");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("type_mismatch", fieldErrors));
    }
}

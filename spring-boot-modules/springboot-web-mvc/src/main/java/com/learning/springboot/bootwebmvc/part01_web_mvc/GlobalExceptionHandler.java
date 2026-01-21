package com.learning.springboot.bootwebmvc.part01_web_mvc;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice(basePackages = "com.learning.springboot.bootwebmvc.part01_web_mvc")
public class GlobalExceptionHandler {

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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        Map<String, String> fieldErrors = Map.of(ex.getParameterName(), "缺少请求参数");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("missing_parameter", fieldErrors));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        Map<String, String> fieldErrors = Map.of(ex.getHeaderName(), "缺少请求头");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("missing_header", fieldErrors));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiError> handleMissingPathVariable(MissingPathVariableException ex) {
        Map<String, String> fieldErrors = Map.of(ex.getVariableName(), "缺少路径变量");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("missing_path_variable", fieldErrors));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiError("method_not_supported", Map.of()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ApiError("unsupported_media_type", Map.of()));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiError> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ApiError("not_acceptable", Map.of()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String name = extractStableParameterName(result);
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                fieldErrors.putIfAbsent(name, error.getDefaultMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("method_validation_failed", fieldErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = String.valueOf(violation.getPropertyPath());
            String key = toLeafPathSegment(path);
            fieldErrors.putIfAbsent(key, violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("method_validation_failed", fieldErrors));
    }

    private static String toLeafPathSegment(String path) {
        if (path == null || path.isBlank()) {
            return "arg";
        }
        int idx = path.lastIndexOf('.');
        if (idx < 0 || idx == path.length() - 1) {
            return path;
        }
        return path.substring(idx + 1);
    }

    private static String extractStableParameterName(ParameterValidationResult result) {
        var parameter = result.getMethodParameter();

        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            if (requestParam.name() != null && !requestParam.name().isBlank()) {
                return requestParam.name();
            }
            if (requestParam.value() != null && !requestParam.value().isBlank()) {
                return requestParam.value();
            }
        }

        PathVariable pathVariable = parameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null) {
            if (pathVariable.name() != null && !pathVariable.name().isBlank()) {
                return pathVariable.name();
            }
            if (pathVariable.value() != null && !pathVariable.value().isBlank()) {
                return pathVariable.value();
            }
        }

        RequestHeader requestHeader = parameter.getParameterAnnotation(RequestHeader.class);
        if (requestHeader != null) {
            if (requestHeader.name() != null && !requestHeader.name().isBlank()) {
                return requestHeader.name();
            }
            if (requestHeader.value() != null && !requestHeader.value().isBlank()) {
                return requestHeader.value();
            }
        }

        String parameterName = parameter.getParameterName();
        if (parameterName != null) {
            return parameterName;
        }

        return parameter.toString();
    }
}

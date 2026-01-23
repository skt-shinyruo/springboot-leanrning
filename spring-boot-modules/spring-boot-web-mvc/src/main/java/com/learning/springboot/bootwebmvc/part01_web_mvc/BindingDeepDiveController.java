package com.learning.springboot.bootwebmvc.part01_web_mvc;

// 本类用于演示 Web MVC 的“binder 路径”：@RequestParam/@ModelAttribute 的绑定、校验与错误分支。

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/binding")
public class BindingDeepDiveController {

    @GetMapping(value = "/age", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> echoAge(@RequestParam("age") int age) {
        return Map.of("age", age);
    }

    @PostMapping(
            value = "/form",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> submitForm(@Valid BindingForm form) {
        return Map.of(
                "name", form.getName(),
                "email", form.getEmail()
        );
    }

    @PostMapping(
            value = "/form-manual",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> submitFormManual(@Valid BindingForm form, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.ok(
                    Map.of(
                            "name", form.getName(),
                            "email", form.getEmail()
                    )
            );
        }

        Map<String, String> fieldErrors = new java.util.LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ApiError("validation_failed_manual", fieldErrors));
    }

    @PostMapping(
            value = "/mass-assignment",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> massAssignment(@Valid MassAssignmentForm form) {
        // 关键点：即使请求携带了 admin=true，也不会被绑定（因为 @InitBinder 限制了 allowedFields）。
        return Map.of(
                "name", form.getName(),
                "admin", form.isAdmin()
        );
    }

    @PostMapping(
            value = "/mass-assignment-debug",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> massAssignmentDebug(@Valid MassAssignmentForm form, BindingResult bindingResult)
            throws BindException {
        // Spring 6.2+：BindingResult 可读取 suppressed fields（被 binder 阻止绑定的字段名），适合作为排障证据。
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return Map.of(
                "name", form.getName(),
                "admin", form.isAdmin(),
                "suppressedFields", bindingResult.getSuppressedFields()
        );
    }

    @InitBinder
    void initBinder(WebDataBinder binder) {
        // 教学用：展示“绑定边界”的一个最小实践（避免不必要字段被绑定）。
        binder.setAllowedFields("name", "email");
    }
}

package com.learning.springboot.bootwebmvc.part01_web_mvc;

import java.util.Map;

import jakarta.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/advanced/binding")
public class MethodValidationController {

    @GetMapping(value = "/age-validated", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> echoAgeWithMethodValidation(@RequestParam("age") @Min(0) int age) {
        return Map.of("age", age);
    }
}


package com.learning.springboot.springcorevalidation.part01_validation_core;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ValidationDemoRunner implements ApplicationRunner {

    private final ProgrammaticValidationService programmaticValidationService;
    private final MethodValidatedUserService methodValidatedUserService;

    public ValidationDemoRunner(
            ProgrammaticValidationService programmaticValidationService,
            MethodValidatedUserService methodValidatedUserService
    ) {
        this.programmaticValidationService = programmaticValidationService;
        this.methodValidatedUserService = methodValidatedUserService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-validation ==");

        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);

        Set<ConstraintViolation<CreateUserCommand>> violations = programmaticValidationService.validate(invalid);
        System.out.println("programmatic.violationCount=" + violations.size());
        System.out.println("programmatic.paths=" + violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .sorted()
                .collect(Collectors.toList()));

        try {
            methodValidatedUserService.register(invalid);
        } catch (RuntimeException ex) {
            System.out.println("methodValidation.exceptionType=" + ex.getClass().getName());
        }
    }
}


package com.learning.springboot.springcorevalidation.part01_validation_core;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.stereotype.Service;

@Service
public class ProgrammaticValidationService {

    private final Validator validator;

    public ProgrammaticValidationService(Validator validator) {
        this.validator = validator;
    }

    public Set<ConstraintViolation<CreateUserCommand>> validate(CreateUserCommand command) {
        return validator.validate(command);
    }
}


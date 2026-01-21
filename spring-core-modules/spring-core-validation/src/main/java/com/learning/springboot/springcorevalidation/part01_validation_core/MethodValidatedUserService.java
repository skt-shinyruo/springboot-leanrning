package com.learning.springboot.springcorevalidation.part01_validation_core;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class MethodValidatedUserService {

    public String register(@Valid CreateUserCommand command) {
        return "registered:" + command.username();
    }
}


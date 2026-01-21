package com.learning.springboot.springcorevalidation.part01_validation_core;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateUserCommand(
        @NotBlank String username,
        @Email String email,
        @Min(0) int age
) {
}


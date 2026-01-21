package com.learning.springboot.springcorevalidation.part01_validation_core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreValidationLabTest {

    @Autowired
    private ProgrammaticValidationService programmaticValidationService;

    @Autowired
    private MethodValidatedUserService methodValidatedUserService;

    @Autowired
    private Validator validator;

    @Test
    void programmaticValidationFindsViolations() {
        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);
        Set<ConstraintViolation<CreateUserCommand>> violations = programmaticValidationService.validate(invalid);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(3);
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("username"));
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("email"));
        assertThat(violations).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("age"));
    }

    @Test
    void methodValidationThrowsForInvalidInput() {
        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);

        assertThatThrownBy(() -> methodValidatedUserService.register(invalid))
                .isInstanceOf(ConstraintViolationException.class)
                .satisfies(ex -> assertThat(((ConstraintViolationException) ex).getConstraintViolations()).hasSize(3));
    }

    @Test
    void programmaticValidationReturnsNoViolationsForValidInput() {
        CreateUserCommand valid = new CreateUserCommand("alice", "alice@example.com", 18);
        Set<ConstraintViolation<CreateUserCommand>> violations = programmaticValidationService.validate(valid);
        assertThat(violations).isEmpty();
    }

    @Test
    void methodValidationReturnsBusinessResultForValidInput() {
        CreateUserCommand valid = new CreateUserCommand("alice", "alice@example.com", 18);
        assertThat(methodValidatedUserService.register(valid)).isEqualTo("registered:alice");
    }

    @Test
    void methodValidatedServiceIsAnAopProxy() {
        assertThat(AopUtils.isAopProxy(methodValidatedUserService)).isTrue();
    }

    @Test
    void validatorIsAvailableFromTheSpringContext() {
        assertThat(validator).isNotNull();
    }
}

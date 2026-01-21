package com.learning.springboot.springcorevalidation.part01_validation_core;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SpringCoreValidationMechanicsLabTest {

    @Test
    void methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy() {
        MethodValidatedUserService direct = new MethodValidatedUserService();
        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);

        // No proxy = no MethodValidation interceptor.
        assertThat(direct.register(invalid)).isEqualTo("registered:");
    }

    @Test
    void groupsControlWhichConstraintsApply() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            GroupedCommand invalid = new GroupedCommand("");

            Set<ConstraintViolation<GroupedCommand>> defaultGroupViolations = validator.validate(invalid, Default.class);
            Set<ConstraintViolation<GroupedCommand>> createGroupViolations = validator.validate(invalid, Create.class);

            assertThat(defaultGroupViolations).isEmpty();
            assertThat(createGroupViolations).hasSize(1);
        }
    }

    @Test
    void customConstraintsCanBeDefinedWithConstraintValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            PrefixCommand invalid = new PrefixCommand("bob");
            PrefixCommand valid = new PrefixCommand("user:bob");

            assertThat(validator.validate(valid)).isEmpty();
            assertThat(validator.validate(invalid)).hasSize(1);
        }
    }

    @Test
    void constraintViolationIncludesMessageAndPropertyPath() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            PrefixCommand invalid = new PrefixCommand("bob");
            Set<ConstraintViolation<PrefixCommand>> violations = validator.validate(invalid);

            assertThat(violations).anySatisfy(v -> {
                assertThat(v.getPropertyPath().toString()).isEqualTo("value");
                assertThat(v.getMessage()).contains("must start with");
            });
        }
    }

    interface Create {
    }

    record GroupedCommand(@NotBlank(groups = Create.class) String value) {
    }

    @Documented
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = StartsWithValidator.class)
    @interface StartsWith {
        String prefix();

        String message() default "must start with {prefix}";

        Class<?>[] groups() default {};

        Class<? extends jakarta.validation.Payload>[] payload() default {};
    }

    public static class StartsWithValidator implements jakarta.validation.ConstraintValidator<StartsWith, String> {
        private String prefix;

        public StartsWithValidator() {
        }

        @Override
        public void initialize(StartsWith annotation) {
            this.prefix = annotation.prefix();
        }

        @Override
        public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            return value.startsWith(prefix);
        }
    }

    record PrefixCommand(@StartsWith(prefix = "user:") String value) {
    }
}

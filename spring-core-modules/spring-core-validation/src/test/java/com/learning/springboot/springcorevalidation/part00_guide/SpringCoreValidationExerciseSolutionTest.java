package com.learning.springboot.springcorevalidation.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.springboot.springcorevalidation.SpringCoreValidationApplication;
import com.learning.springboot.springcorevalidation.part01_validation_core.CreateUserCommand;
import com.learning.springboot.springcorevalidation.part01_validation_core.MethodValidatedUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Payload;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

/**
 * 参考实现：对齐 SpringCoreValidationExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 *
 * <p>说明：
 * <ul>
 *   <li>Exercises 的 TODO 通常会要求你“改动某个生产类”。Solution 这里优先展示“机制可验证”的最小实现，
 *   并避免破坏现有 Labs 的断言。</li>
 *   <li>对于 Groups / 自定义约束，使用 test 内的最小样例类型演示即可（与核心机制一致）。</li>
 * </ul>
 */
@SpringBootTest(classes = { SpringCoreValidationApplication.class, SpringCoreValidationExerciseSolutionTest.SolutionConfig.class })
class SpringCoreValidationExerciseSolutionTest {

    @Autowired
    private Validator validator;

    @Autowired
    private MethodValidatedUserService methodValidatedUserService;

    @Autowired
    private CreateGroupValidatedService createGroupValidatedService;

    @Autowired
    private UpdateGroupValidatedService updateGroupValidatedService;

    @Test
    void solution_addNewConstraint_exampleAddsSizeConstraint() {
        CommandWithSizeConstraint invalid = new CommandWithSizeConstraint("a", "not-an-email", -1);

        Set<ConstraintViolation<CommandWithSizeConstraint>> violations = validator.validate(invalid);

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("username");
            assertThat(v.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(Size.class);
        });
    }

    @Test
    void solution_validationGroups_createVsUpdate_haveDifferentViolationSets() {
        UserUpsertCommand invalid = new UserUpsertCommand(null, "", "not-an-email", -1);

        Set<ConstraintViolation<UserUpsertCommand>> createViolations = validator.validate(invalid, Create.class);
        assertThat(createViolations).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("username"));
        assertThat(createViolations).noneMatch(v -> v.getPropertyPath().toString().equals("id"));

        Set<ConstraintViolation<UserUpsertCommand>> updateViolations = validator.validate(invalid, Update.class);
        assertThat(updateViolations).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("id"));
        assertThat(updateViolations).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("username"));
    }

    @Test
    void solution_customConstraint_canBeValidatedProgrammatically() {
        EmailCommand invalid = new EmailCommand("alice@not-example.com");
        Set<ConstraintViolation<EmailCommand>> violations = validator.validate(invalid);

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(AllowedDomain.class);
        });
    }

    @Test
    void solution_methodValidationNeedsProxy_springBeanThrowsButNewDoesNot() {
        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);

        assertThatThrownBy(() -> methodValidatedUserService.register(invalid))
                .isInstanceOf(ConstraintViolationException.class)
                .satisfies(ex -> assertThat(((ConstraintViolationException) ex).getConstraintViolations()).hasSize(3));

        MethodValidatedUserService direct = new MethodValidatedUserService();
        assertThat(direct.register(invalid)).isEqualTo("registered:");
    }

    @Test
    void solution_methodValidationGroups_canBeControlledByValidatedGroupOnBean() {
        UserUpsertCommand invalid = new UserUpsertCommand(null, "", "not-an-email", -1);

        assertThatThrownBy(() -> createGroupValidatedService.create(invalid))
                .isInstanceOf(ConstraintViolationException.class)
                .satisfies(ex -> {
                    Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ex).getConstraintViolations();
                    assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().endsWith(".id"));
                });

        assertThatThrownBy(() -> updateGroupValidatedService.update(invalid))
                .isInstanceOf(ConstraintViolationException.class)
                .satisfies(ex -> {
                    Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ex).getConstraintViolations();
                    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().endsWith(".id"));
                });
    }

    interface Create {
    }

    interface Update {
    }

    record CommandWithSizeConstraint(
            @NotBlank @Size(min = 2) String username,
            @Email String email,
            @Min(0) int age
    ) {
    }

    record UserUpsertCommand(
            @NotNull(groups = Update.class) Long id,
            @NotBlank(groups = { Create.class, Update.class }) String username,
            @Email(groups = { Create.class, Update.class }) String email,
            @Min(value = 0, groups = { Create.class, Update.class }) int age
    ) {
    }

    record EmailCommand(
            @NotBlank @AllowedDomain(domain = "example.com") String email
    ) {
    }

    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @jakarta.validation.Constraint(validatedBy = AllowedDomainValidator.class)
    @interface AllowedDomain {
        String message() default "email domain not allowed";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        String domain();
    }

    static class AllowedDomainValidator implements jakarta.validation.ConstraintValidator<AllowedDomain, String> {

        private String domain;

        @Override
        public void initialize(AllowedDomain constraintAnnotation) {
            this.domain = constraintAnnotation.domain();
        }

        @Override
        public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
            if (value == null || value.isBlank()) {
                return true;
            }
            return value.endsWith("@" + domain);
        }
    }

    @Validated(Create.class)
    static class CreateGroupValidatedService {
        String create(@Valid UserUpsertCommand command) {
            return "create:" + command.username();
        }
    }

    @Validated(Update.class)
    static class UpdateGroupValidatedService {
        String update(@Valid UserUpsertCommand command) {
            return "update:" + command.username();
        }
    }

    @TestConfiguration
    static class SolutionConfig {
        @Bean
        CreateGroupValidatedService createGroupValidatedService() {
            return new CreateGroupValidatedService();
        }

        @Bean
        UpdateGroupValidatedService updateGroupValidatedService() {
            return new UpdateGroupValidatedService();
        }
    }
}

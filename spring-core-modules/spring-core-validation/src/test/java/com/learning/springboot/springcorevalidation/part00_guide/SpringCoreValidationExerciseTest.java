package com.learning.springboot.springcorevalidation.part00_guide;

import com.learning.springboot.springcorevalidation.part01_validation_core.CreateUserCommand;
import com.learning.springboot.springcorevalidation.part01_validation_core.ProgrammaticValidationService;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreValidationExerciseTest {

    @Autowired
    private ProgrammaticValidationService programmaticValidationService;

    @Test
    @Disabled("练习：给 CreateUserCommand 增加一个新约束（例如 @Size），并更新本测试以断言新的 violation")
    void exercise_addNewConstraint() {
        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);
        Set<ConstraintViolation<CreateUserCommand>> violations = programmaticValidationService.validate(invalid);

        assertThat(violations)
                .as("""
                        练习：给 CreateUserCommand 增加一个新约束（例如 @Size），并在这里断言新的 violation。

                        下一步：
                        1) 打开 `CreateUserCommand`，新增一个你选定的约束（例如对 name 加 `@Size(min=2)`）。
                        2) 修改 `invalid` 的输入，确保能触发你新增的约束。
                        3) 把断言从“数量”升级为“包含特定 violation”（例如 propertyPath=xxx）。

                        建议阅读：
                        - `docs/validation/spring-core-validation/part-01-validation-core/01-constraint-mental-model.md`
                        - `docs/validation/spring-core-validation/part-01-validation-core/02-programmatic-validator.md`

                        提示：
                        - `ConstraintViolation#getPropertyPath()` 与 `#getMessage()` 是最有用的 debug 信息
                        """)
                .hasSizeGreaterThanOrEqualTo(4);
    }

    @Test
    @Disabled("练习：引入校验 groups（Create vs Update），并证明不同 group 下生效的约束不同")
    void exercise_validationGroups() {
        assertThat(true)
                .as("""
                        练习：引入校验 groups（Create vs Update），并证明不同 group 下生效的约束不同。

                        下一步：
                        1) 定义 group 接口（例如 Create/Update）。
                        2) 把部分约束标到指定 group。
                        3) 用 programmatic validation 分别传入不同 group，断言 violation 集合不同。

                        建议阅读：
                        - `docs/validation/spring-core-validation/part-01-validation-core/04-groups.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：新增一个自定义约束注解（@Constraint），并用 programmatic validation 证明它能工作")
    void exercise_customConstraint() {
        assertThat(true)
                .as("""
                        练习：新增一个自定义约束注解（@Constraint），并用 programmatic validation 证明它能工作。

                        下一步：
                        1) 定义注解 + `ConstraintValidator`。
                        2) 把注解加到 `CreateUserCommand` 的某个字段上。
                        3) 写测试：构造非法输入，断言新增 violation 出现。

                        建议阅读：
                        - `docs/validation/spring-core-validation/part-01-validation-core/05-custom-constraint.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：证明 method validation 依赖代理（对比 Spring bean vs 直接 new 的对象）")
    void exercise_methodValidationNeedsProxy() {
        assertThat(true)
                .as("""
                        练习：证明 method validation 依赖代理（对比 Spring bean vs 直接 new 的对象）。

                        下一步：
                        1) 直接 `new` 一个 service，调用带校验的方法，观察不会抛校验异常。
                        2) 再从 Spring 容器里取同一个 service bean，调用相同方法，观察会抛校验异常。

                        建议阅读：
                        - `docs/validation/spring-core-validation/part-01-validation-core/03-method-validation-proxy.md`
                        - `docs/aop/spring-core-aop/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：让 method validation 也按 group 生效，并写测试证明")
    void exercise_methodValidationGroups() {
        assertThat(true)
                .as("""
                        练习：让 method validation 也按 group 生效，并写测试证明。

                        下一步：
                        1) 在 `@Validated` 上指定 groups（或在方法/参数上设计 group）。
                        2) 准备两组输入：分别触发不同 group 的约束。
                        3) 断言异常/violation 的差异。

                        建议阅读：
                        - `docs/validation/spring-core-validation/part-01-validation-core/04-groups.md`
                        - `docs/validation/spring-core-validation/part-01-validation-core/03-method-validation-proxy.md`
                        """)
                .isFalse();
    }
}

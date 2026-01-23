package com.learning.springboot.bootbasics.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class BootBasicsExerciseTest {

    @Test
    @Disabled("练习：新增一个 @ConfigurationProperties 字段，并用测试证明它能正确绑定（同时更新 README）")
    void exercise_addNewPropertyField() {
        assertThat(true)
                .as("""
                        练习：新增一个 @ConfigurationProperties 字段，并用测试证明它能正确绑定（同时更新 README）。

                        下一步：
                        1) 打开 `AppProperties`，新增字段（例如 `color`）并提供 getter。
                        2) 在 `application.properties`（或测试覆盖配置）里添加 `app.color=...`。
                        3) 在这里写断言：读取 `AppProperties`，验证新字段绑定结果。

                        参考：
                        - `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`

                        常见坑：
                        - 配置键的 kebab-case 映射：`featureEnabled` ↔ `feature-enabled`
                        - 忘了让 @ConfigurationProperties 被扫描/启用（本模块用 `@ConfigurationPropertiesScan`）
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：演示配置优先级（多个来源叠加），并把观察记录到 README")
    void exercise_propertyPrecedence() {
        assertThat(true)
                .as("""
                        练习：演示配置优先级（多个来源叠加），并把观察记录到 README。

                        目标：
                        - 证明同一个 key（例如 `app.greeting`）在不同来源下会被谁覆盖。

                        下一步：
                        1) 参考 `BootBasicsOverrideLabTest`：它已经演示了“测试级覆盖”的效果。
                        2) 再增加一种来源（例如 system properties / 命令行参数 / profile 文件），对比最终值。
                        3) 用断言把结论固定下来，再把结论写进 `spring-boot-modules/springboot-basics/README.md`。

                        参考：
                        - `spring-boot-modules/springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`

                        常见坑：
                        - 只看控制台日志容易误判；优先用 `Environment#getProperty(...)` + 断言
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个可选 feature toggle，并在开启时才注册某个 Bean（写测试证明）")
    void exercise_conditionalBeanByProperty() {
        assertThat(true)
                .as("""
                        练习：增加一个可选 feature toggle，并在开启时才注册某个 Bean（写测试证明）。

                        下一步：
                        1) 定义开关：例如 `app.feature-x.enabled=true/false`。
                        2) 用 `@ConditionalOnProperty`（或等价方式）控制 Bean 是否注册。
                        3) 写测试分别覆盖开关为 true/false，断言 Bean 是否存在。

                        参考：
                        - `spring-core-modules/spring-core-profiles/README.md`（条件装配专门模块）

                        常见坑：
                        - 条件注解写在不生效的位置（例如不是配置类/不是组件扫描范围）
                        - key 写错导致永远 matchIfMissing 的错觉
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 @SpringBootTest 改成 ApplicationContextRunner 风格，获得更快反馈")
    void exercise_applicationContextRunner() {
        ApplicationContextRunner runner = new ApplicationContextRunner();
        assertThat(runner).isNotNull();

        assertThat(true)
                .as("""
                        练习：把 @SpringBootTest 改成 ApplicationContextRunner 风格，获得更快反馈。

                        下一步：
                        1) 参考 `spring-core-modules/spring-core-beans` / `spring-core-modules/spring-core-profiles` 里的 runner 用法：用更小的上下文验证一个结论。
                        2) 选 1 个现有 Lab（例如默认 profile 的配置绑定），改写成 runner：
                           - 配置 propertyValues
                           - 启动 contextRunner.run(...)
                           - 断言 Bean / 属性
                        3) 对比两种方式的启动开销与定位速度。

                        参考：
                        - `spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/SpringCoreProfilesLabTest.java`
                        - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansAutoConfigurationLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：构造一个配置类型错误的失败案例，并断言启动错误信息")
    void exercise_invalidPropertyType() {
        assertThat(true)
                .as("""
                        练习：构造一个配置类型错误的失败案例，并断言启动错误信息。

                        示例：
                        - 把 boolean 配置写成字符串：`app.feature-enabled=not-a-boolean`

                        下一步：
                        1) 写一个最小测试：启动上下文时注入这条非法配置。
                        2) 断言启动失败，并断言错误信息包含“绑定失败/类型转换失败”的关键字。

                        常见坑：
                        - 断言过于依赖完整异常文本（不同版本可能略有差异）；建议断言关键片段即可
                        """)
                .isFalse();
    }
}

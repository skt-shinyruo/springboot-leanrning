package com.learning.springboot.springcoreprofiles.part00_guide;

import com.learning.springboot.springcoreprofiles.part01_profiles.DevGreetingConfiguration;
import com.learning.springboot.springcoreprofiles.part01_profiles.GreetingProvider;
import com.learning.springboot.springcoreprofiles.part01_profiles.NonDevGreetingConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SpringCoreProfilesExerciseTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(DevGreetingConfiguration.class, NonDevGreetingConfiguration.class);

    @Test
    @Disabled("练习：新增一个 profile（例如 staging），并让它选择一个新的 GreetingProvider")
    void exercise_addNewProfile() {
        contextRunner
                .withPropertyValues("spring.profiles.active=staging")
                .run(context -> {
                    assertThat(context)
                            .as("""
                                    练习：新增一个 profile（例如 staging），并让它选择一个新的 GreetingProvider。

                                    下一步：
                                    1) 新增一个 `@Profile(\"staging\")` 的配置/Bean。
                                    2) 保证该场景下只存在一个 GreetingProvider（避免注入歧义）。
                                    3) 运行本测试，确认注入与输出都符合预期。

                                    建议阅读：
                                    - `spring-core-modules/spring-core-profiles/README.md`
                                    """)
                            .hasSingleBean(GreetingProvider.class);

                    assertThat(context.getBean(GreetingProvider.class).greeting())
                            .as("""
                                    期望：staging profile 下返回值包含 "staging"。

                                    常见坑：
                                    - `!dev` 这类表达式会让“非 dev 的所有 profile”都落到同一个实现；如果你要 staging 独立实现，请显式声明
                                    """)
                            .contains("staging");
                });
    }

    @Test
    @Disabled("练习：新增一个 ConditionalOnProperty 开关，并让 Bean 选择依赖它")
    void exercise_addNewConditional() {
        assertThat(true)
                .as("""
                        练习：新增一个 ConditionalOnProperty 开关，并让 Bean 选择依赖它。

                        下一步：
                        1) 设计一个 key（例如 `app.mode=fancy`）。
                        2) 用 `@ConditionalOnProperty` 注册不同实现。
                        3) 用 runner 覆盖 propertyValues，断言注入的实现类不同。

                        参考：
                        - `spring-core-modules/spring-core-profiles/src/test/java/.../SpringCoreProfilesLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：演示 property precedence（例如 application.properties vs test override）")
    void exercise_propertyPrecedence() {
        assertThat(true)
                .as("""
                        练习：演示 property precedence（例如 application.properties vs test override）。

                        下一步：
                        1) 在默认配置里给一个 key 赋值。
                        2) 在 runner 的 propertyValues 里覆盖同一个 key。
                        3) 断言最终注入/读取到的是覆盖后的值。

                        常见坑：
                        - 只靠“我以为”判断优先级很容易出错；用断言把结论固定下来
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：写一个负向测试：当 GreetingProvider 缺失时，context 启动应失败")
    void exercise_missingBeanStartupFailure() {
        assertThat(true)
                .as("""
                        练习：写一个负向测试：当 GreetingProvider 缺失时，context 启动应失败。

                        下一步：
                        1) 构造一个场景：让所有 GreetingProvider 都因为条件不匹配而不注册。
                        2) 用 runner 断言：context 启动失败（hasFailed），并检查 failure message。

                        提示：
                        - 这能帮助你理解“条件装配写错会导致什么失败形态”
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 debug helper（输出 activeProfiles 与最终选择的 provider），用于学习")
    void exercise_debugHelper() {
        assertThat(true)
                .as("""
                        练习：增加一个 debug helper（输出 activeProfiles 与最终选择的 provider），用于学习。

                        下一步：
                        1) 写一个小工具方法：打印 activeProfiles + 选中的实现类。
                        2) 测试里调用它，但不要对“长日志”做脆弱断言；只断言关键事实（例如实现类名）。
                        """)
                .isFalse();
    }
}

package com.learning.springboot.boottesting.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootTestingExerciseTest {

    @Test
    @Disabled("练习：新增一个 @WebMvcTest（带校验），断言非法输入返回 400（需要新增一个 endpoint + validation）")
    void exercise_webMvcValidation() {
        assertThat(true)
                .as("""
                        练习：新增一个 @WebMvcTest（带校验），断言非法输入返回 400。

                        下一步：
                        1) 新增一个简单 endpoint（例如 POST /api/echo）并在请求 DTO 上加校验注解。
                        2) 用 `@WebMvcTest` + MockMvc 发送非法 JSON，断言 `400` 与字段错误结构。

                        参考：
                        - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
                        - `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`

                        常见坑：
                        - 忘了在 controller 入参上加 `@Valid`
                        - 切片测试里依赖缺失需要 `@MockBean`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：对比 slice 与 full context：证明某个 Bean 在 @WebMvcTest 中不会加载，但在 @SpringBootTest 会")
    void exercise_sliceVsFull() {
        assertThat(true)
                .as("""
                        练习：对比 slice 与 full context：证明某个 Bean 在 @WebMvcTest 中不会加载，但在 @SpringBootTest 会。

                        下一步：
                        1) 选一个“非 Web 层”的 Bean（例如某个 @Service 或 @Component）。
                        2) 在 `@WebMvcTest` 中断言它不存在（或注入失败）。
                        3) 在 `@SpringBootTest` 中断言它存在。

                        参考：
                        - `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`
                        - `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`

                        常见坑：
                        - `@WebMvcTest` 只加载 Controller/相关 MVC 组件，不会扫描你的所有 Bean
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：使用 @TestConfiguration 覆盖一个 Bean，并写一个更聚焦的测试")
    void exercise_testConfigurationOverride() {
        assertThat(true)
                .as("""
                        练习：使用 @TestConfiguration 覆盖一个 Bean，并写一个更聚焦的测试。

                        下一步：
                        1) 在测试类里写一个 `@TestConfiguration`，提供一个替代 Bean（例如替代 GreetingService）。
                        2) 断言 controller/service 的输出跟随你的替代实现。

                        常见坑：
                        - 覆盖的类型必须与注入点一致（接口 vs 实现类差异会导致覆盖失败）
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：使用 @DynamicPropertySource 注入动态属性，并记录适用场景（写到 README 或测试注释）")
    void exercise_dynamicPropertySource() {
        assertThat(true)
                .as("""
                        练习：使用 @DynamicPropertySource 注入动态属性，并记录适用场景。

                        典型场景：
                        - 需要在测试运行时生成端口/URL（例如 Testcontainers）并注入到 Spring Environment。

                        下一步：
                        1) 写一个 `@DynamicPropertySource` 方法，把一个动态值注册进去。
                        2) 在测试里读取该 property 并断言生效。
                        3) 总结：它与 `@TestPropertySource` / `properties = ...` 的区别是什么。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：故意错误使用一次 @MockBean，并解释它可能掩盖什么问题")
    void exercise_mockBeanPitfall() {
        assertThat(true)
                .as("""
                        练习：故意错误使用一次 @MockBean，并解释它可能掩盖什么问题。

                        示例思路：
                        - 用 @MockBean 把“应该由集成链路暴露出来的问题”遮住（例如缺少配置/错误的 Bean 注册）。

                        下一步：
                        1) 写一条测试：先不 mock，让它失败，观察失败原因。
                        2) 再加 @MockBean 让它通过，并解释：为什么这可能是“假绿”。

                        常见坑：
                        - 过度 mock 会让你学不到真正的装配/配置问题
                        """)
                .isFalse();
    }
}

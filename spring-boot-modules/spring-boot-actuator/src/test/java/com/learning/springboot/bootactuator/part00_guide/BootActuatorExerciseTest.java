package com.learning.springboot.bootactuator.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootActuatorExerciseTest {

    @Test
    @Disabled("练习：新增一个可开关（property）的 HealthIndicator，并用测试证明 UP/DOWN 会随开关变化")
    void exercise_toggleableHealthIndicator() {
        assertThat(true)
                .as("""
                        练习：新增一个可开关（property）的 HealthIndicator，并用测试证明 UP/DOWN 会随开关变化。

                        下一步：
                        1) 定义开关：例如 `learning.health.enabled=true/false`。
                        2) 在 HealthIndicator 中读取配置：关闭时返回 DOWN，开启时返回 UP。
                        3) 写测试：分别覆盖 property，断言 `/actuator/health` 输出变化。

                        参考：
                        - `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`
                        - `spring-boot-modules/spring-boot-actuator/README.md`

                        常见坑：
                        - health details 是否显示，取决于 `management.endpoint.health.show-details`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：新增一个 InfoContributor，并证明它出现在 /actuator/info")
    void exercise_infoContributor() {
        assertThat(true)
                .as("""
                        练习：新增一个 InfoContributor，并证明它出现在 /actuator/info。

                        下一步：
                        1) 实现 `InfoContributor`，往 info 里加一个字段（例如 build/version）。
                        2) 写测试：访问 `/actuator/info` 并断言包含该字段。

                        参考：
                        - `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：限制 endpoint exposure，并证明未暴露的端点会返回 404")
    void exercise_endpointExposure() {
        assertThat(true)
                .as("""
                        练习：限制 endpoint exposure，并证明未暴露的端点会返回 404。

                        下一步：
                        1) 通过 properties 配置 include/exclude。
                        2) 写测试：对未暴露端点发请求，断言 404。

                        参考：
                        - `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：新增一个自定义 actuator endpoint（@Endpoint），并写测试验证")
    void exercise_customEndpoint() {
        assertThat(true)
                .as("""
                        练习：新增一个自定义 actuator endpoint（@Endpoint），并写测试验证。

                        下一步：
                        1) 新建 `@Endpoint(id=...)` + `@ReadOperation` 方法。
                        2) 配置 exposure，把它暴露出来。
                        3) 写测试：访问 `/actuator/{id}` 并断言响应结构。

                        常见坑：
                        - 忘了 exposure 导致 endpoint 存在但访问 404
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 actuator 端点加基础认证，并分别断言授权/未授权响应")
    void exercise_actuatorSecurity() {
        assertThat(true)
                .as("""
                        练习：为 actuator 端点加基础认证，并分别断言授权/未授权响应。

                        下一步：
                        1) 引入 Spring Security（如果你愿意把它当作扩展主题）。
                        2) 仅对 `/actuator/**` 加 basic auth。
                        3) 写测试：不带凭据返回 401，带凭据返回 200。

                        提示：
                        - 这是扩展题：先把 Actuator 与 exposure 机制学扎实，再加安全层更不容易迷路
                        """)
                .isFalse();
    }
}

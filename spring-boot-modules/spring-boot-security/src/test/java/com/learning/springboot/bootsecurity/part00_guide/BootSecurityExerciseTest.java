package com.learning.springboot.bootsecurity.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootSecurityExerciseTest {

    @Test
    @Disabled("练习：新增一个 GET /api/secure/me，返回当前用户名与角色，并用 MockMvc 固定断言")
    void exercise_secureMeEndpoint() {
        assertThat(true)
                .as("""
                        练习：新增一个 GET /api/secure/me，返回当前用户名与角色，并用 MockMvc 固定断言。

                        下一步：
                        1) 新增 endpoint（建议放在 `SecureController` 或新 controller 中）。
                        2) 读取当前认证信息：`Authentication` / `@AuthenticationPrincipal`。
                        3) 写一个 Lab 或在这里写断言：用 Basic Auth 登录后，响应体包含 username 与 roles。

                        参考：
                        - `spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 CSRF 失败的错误响应进一步细分（missing vs invalid），并写测试固定 message")
    void exercise_csrfErrorDetails() {
        assertThat(true)
                .as("""
                        练习：把 CSRF 失败的错误响应进一步细分（missing vs invalid），并写测试固定 message。

                        提示：
                        - 目前实现里把 CSRF failure 统一映射为 `csrf_failed`。
                        - 你可以分别处理 MissingCsrfTokenException / InvalidCsrfTokenException，返回不同 message。

                        下一步：
                        1) 修改 `JsonAccessDeniedHandler`。
                        2) 增加 2 个测试：缺 token 与 token 错误各自断言 message。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个新的 SecurityFilterChain（例如 /api/internal/**），并证明链路匹配顺序")
    void exercise_multipleFilterChains() {
        assertThat(true)
                .as("""
                        练习：增加一个新的 SecurityFilterChain（例如 /api/internal/**），并证明链路匹配顺序。

                        目标：
                        - 让你能解释：多个 SecurityFilterChain 之间如何选择（按 matcher + order）。

                        下一步：
                        1) 在 `SecurityConfig` 新增一个 chain（加 `@Order`）。
                        2) 写 MockMvc tests：对比 /api/internal/** 与 /api/** 的行为差异。
                        3) 修改顺序（@Order）并证明行为变化。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：新增一个自定义 Filter，并把它插入到一个明确的位置（before/after），用断言证明顺序")
    void exercise_customFilterOrdering() {
        assertThat(true)
                .as("""
                        练习：新增一个自定义 Filter，并把它插入到一个明确的位置（before/after），用断言证明顺序。

                        示例思路：
                        - Filter A 先写 header `X-A`
                        - Filter B 读取 `X-A` 再写 header `X-B`
                        - 通过断言证明 A 在 B 前

                        下一步：
                        1) 新建 2 个 OncePerRequestFilter。
                        2) 在 `SecurityConfig` 用 `addFilterBefore/addFilterAfter` 固定顺序。
                        3) 写 MockMvc 测试断言 header。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 JWT 的 claim 映射成 ROLE_*（而不是 SCOPE_*），并更新鉴权规则与测试")
    void exercise_jwtRoleMapping() {
        assertThat(true)
                .as("""
                        练习：把 JWT 的 claim 映射成 ROLE_*（而不是 SCOPE_*），并更新鉴权规则与测试。

                        目标：
                        - 你能解释：JwtGrantedAuthoritiesConverter 如何把 claims 变成 GrantedAuthority。

                        下一步：
                        1) 自定义 JwtAuthenticationConverter / JwtGrantedAuthoritiesConverter。
                        2) 修改 `/api/jwt/admin/**` 的规则从 `SCOPE_admin` 改成 `ROLE_ADMIN`。
                        3) 更新 token 发行逻辑或测试用 token 的 claims。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：证明 JWT 链路是 stateless（不会创建 session），并写测试固定结论")
    void exercise_jwtStateless() {
        assertThat(true)
                .as("""
                        练习：证明 JWT 链路是 stateless（不会创建 session），并写测试固定结论。

                        提示：
                        - `SessionCreationPolicy.STATELESS` 的语义是：不使用/不创建 HttpSession 来保存 SecurityContext。
                        - 你可以从响应头/请求 session 或 SecurityContextRepository 的行为入手做断言。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 @PostAuthorize 的方法安全示例，并构造一个“返回值相关授权”的实验")
    void exercise_postAuthorize() {
        assertThat(true)
                .as("""
                        练习：增加一个 @PostAuthorize 的方法安全示例，并构造一个“返回值相关授权”的实验。

                        示例思路：
                        - 方法返回某个 owner 字段
                        - 用 @PostAuthorize 验证返回值 owner 必须等于当前用户
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 RANDOM_PORT 的端到端测试（TestRestTemplate），验证 401/403 的差异")
    void exercise_endToEndTest() {
        assertThat(true)
                .as("""
                        练习：增加一个 RANDOM_PORT 的端到端测试（TestRestTemplate），验证 401/403 的差异。

                        下一步：
                        1) 参考 `spring-boot-modules/springboot-web-mvc` 或 `spring-boot-modules/springboot-testing` 的 full context 测试写法。
                        2) 验证：
                           - 未登录访问 /api/secure/ping → 401
                           - 普通用户访问 /api/admin/ping → 403
                        """)
                .isFalse();
    }
}


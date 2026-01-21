package com.learning.springboot.bootwebmvc.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootWebMvcExerciseTest {

    @Test
    @Disabled("练习：新增一个接口（例如 GET /api/users/{id}），并用 MockMvc 写测试验证")
    void exercise_pathVariables() {
        assertThat(true)
                .as("""
                        练习：新增一个接口（例如 GET /api/users/{id}），并用 MockMvc 写测试验证。

                        下一步：
                        1) 参考 `BootWebMvcLabTest` 的写法（MockMvc + jsonPath）。
                        2) 在 `UserController` 中新增 `@GetMapping(\"/{id}\")`（注意类级 `@RequestMapping` 可能已经是 `/api/users`）。
                        3) 设计一个最小可用的数据存储，让 POST 创建后可以被 GET 查到。

                        参考：
                        - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
                        - `spring-boot-modules/springboot-web-mvc/README.md`

                        常见坑：
                        - path variable 名称不匹配：`@PathVariable(\"id\") Long id`
                        - `@WebMvcTest` 只加载 Web 层：依赖缺失需要 mock 或显式 `@Import`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 JSON 解析失败（malformed JSON）补充统一错误响应，并写测试固定结构")
    void exercise_handleMalformedJson() {
        assertThat(true)
                .as("""
                        练习：为 JSON 解析失败（malformed JSON）补充统一错误响应，并写测试固定结构。

                        下一步：
                        1) 观察现有行为：`BootWebMvcLabTest#returnsBadRequestWhenJsonIsMalformed` 目前只断言了 400。
                        2) 在 `GlobalExceptionHandler` 中处理 JSON 解析相关异常（例如 HttpMessageNotReadableException）。
                        3) 返回与校验失败一致的 ApiError 形状（至少包含 `message`）。

                        参考：
                        - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
                        - `spring-boot-modules/springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/GlobalExceptionHandler.java`

                        常见坑：
                        - 只处理一种异常类型，导致某些 JSON 错误仍然走默认错误页/默认响应
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 HandlerInterceptor，并证明它只对 /api/* 生效")
    void exercise_interceptor() {
        assertThat(true)
                .as("""
                        练习：增加一个 HandlerInterceptor，并证明它只对 /api/* 生效。

                        下一步：
                        1) 新建一个实现 `HandlerInterceptor` 的类（例如记录请求耗时/写入 header）。
                        2) 用 `WebMvcConfigurer#addInterceptors` 注册，并配置 path pattern 为 `/api/**`。
                        3) 写 MockMvc 测试：请求 `/api/ping` 时能观察到拦截器效果。

                        常见坑：
                        - 忘了把 config 类纳入 Spring 管理（导致 interceptor 根本没注册）
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 Converter/Formatter 做请求绑定，并写测试证明它生效")
    void exercise_converterFormatter() {
        assertThat(true)
                .as("""
                        练习：增加一个 Converter/Formatter 做请求绑定，并写测试证明它生效。

                        示例思路：
                        - 让 controller 接收一个自定义类型（例如 `UserId`），并把 path variable String 转成它。

                        下一步：
                        1) 新建 Converter（`Converter<String, UserId>`）或 Formatter。
                        2) 注册到 Spring MVC（通过 `WebMvcConfigurer#addFormatters`）。
                        3) 写 MockMvc 测试：请求 `/api/users/{id}` 能正确绑定并返回预期。

                        常见坑：
                        - Converter 没被扫描到/没注册，导致仍然按 String 处理
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个端到端集成测试（启动服务器）并验证行为")
    void exercise_integrationTest() {
        assertThat(true)
                .as("""
                        练习：增加一个端到端集成测试（启动服务器）并验证行为。

                        下一步：
                        1) 参考 `BootWebMvcSpringBootLabTest`：`@SpringBootTest(webEnvironment=RANDOM_PORT)` + `TestRestTemplate`。
                        2) 选择一个简单场景（例如 GET /api/ping 或 POST /api/users）。
                        3) 用断言固定响应字段（message/id 等）。

                        参考：
                        - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`
                        - `spring-boot-modules/springboot-testing/README.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：实现/调整 @ClientIp ArgumentResolver，并用测试固定解析规则")
    void exercise_clientIpArgumentResolver() {
        assertThat(true)
                .as("""
                        练习：实现/调整 @ClientIp ArgumentResolver，并用测试固定解析规则。

                        目标：把“Client IP 从哪里来”做成可解释、可回归的行为（不要靠日志猜）。

                        下一步（建议顺序）：
                        1) 阅读 `BootWebMvcInternalsLabTest`，先看断言，再看实现。
                        2) 在 `ClientIpArgumentResolver` 中调整解析策略（示例：优先 X-Forwarded-For；为空时回退 remoteAddr）。
                        3) 写/改 MockMvc 测试，固定以下场景：
                           - `X-Forwarded-For: 1.2.3.4, 5.6.7.8` 时取第一个
                           - 不带 `X-Forwarded-For` 时取 remoteAddr

                        参考：
                        - `spring-boot-modules/springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part03_internals/ClientIpArgumentResolver.java`
                        - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcInternalsLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：strict JSON 下把“未知字段名”写进 ApiError.fieldErrors，并写测试固定")
    void exercise_strictJsonUnknownFieldErrorDetails() {
        assertThat(true)
                .as("""
                        练习：strict JSON 下把“未知字段名”写进 ApiError.fieldErrors，并写测试固定。

                        背景：当前 strict media type 触发未知字段时会走 `malformed_json`，但 fieldErrors 为空。
                        目标：让调用方能明确看到“哪个字段多出来了”，便于排障。

                        下一步：
                        1) 复现现状：运行 `BootWebMvcContractJacksonLabTest#strictMediaTypeRejectsUnknownFields`。
                        2) 在 `AdvancedApiExceptionHandler#handleMalformedJson` 中，尝试从 `HttpMessageNotReadableException` 提取根因信息（常见是 Jackson 的 UnknownProperty 异常）。
                        3) 把未知字段名放入 `ApiError.fieldErrors`（例如 key="extra", value="未知字段"）。
                        4) 为 strict 用例补充断言：`$.fieldErrors.extra` 存在。

                        参考：
                        - `spring-boot-modules/springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/AdvancedApiExceptionHandler.java`
                        - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/BootWebMvcContractJacksonLabTest.java`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：文件不存在时返回 404 + ApiError（而不是默认错误体），并写测试固定")
    void exercise_fileNotFoundReturnsApiError() {
        assertThat(true)
                .as("""
                        练习：文件不存在时返回 404 + ApiError（而不是默认错误体），并写测试固定。

                        目标：把“资源不存在”也纳入可控错误契约（便于前端/调用方统一处理）。

                        下一步：
                        1) 为 `GET /api/advanced/files/{id}` 增加一个找不到文件的测试：
                           - 请求一个不存在的 id（例如 999）
                           - 断言 status=404，且返回 `ApiError` 形状（至少包含 message）
                        2) 选择一种实现方式：
                           - 在 `InMemoryFileStore#getRequired` 抛出你自定义的异常（或 `ResponseStatusException`）
                           - 在 `AdvancedApiExceptionHandler` 中新增对该异常的处理，返回 `ApiError(message="file_not_found")`
                        3) 确保不会影响现有上传/下载闭环测试。

                        参考：
                        - `spring-boot-modules/springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/InMemoryFileStore.java`
                        - `spring-boot-modules/springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/AdvancedApiExceptionHandler.java`
                        - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part05_real_world/BootWebMvcRealWorldHttpLabTest.java`
                        """)
                .isFalse();
    }
}

package com.learning.springboot.bootwebclient.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootWebClientExerciseTest {

    @Test
    @Disabled("练习：为 RestClient 增加一个“错误响应体解析”，把下游 error message 映射进异常")
    void exercise_restClientParseErrorBody() {
        assertThat(true)
                .as("""
                        练习：为 RestClient 增加一个“错误响应体解析”，把下游 error message 映射进异常。

                        目标：
                        - 让你能解释：RestClient 的 onStatus handler 里如何读取 body。

                        下一步：
                        1) 扩展 `DownstreamServiceException`：增加 errorBody/message 字段。
                        2) 在 `RestClientGreetingClient` 的 onStatus handler 中读取 response body。
                        3) 写测试：MockWebServer 返回 400 + JSON body，断言异常包含解析后的信息。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 WebClient 增加一个“根据 status 分类”的重试策略（仅对 5xx 重试）并写测试")
    void exercise_webClientRetryPolicy() {
        assertThat(true)
                .as("""
                        练习：为 WebClient 增加一个“根据 status 分类”的重试策略（仅对 5xx 重试）并写测试。

                        提示：
                        - 现在的示例只做了最小 retry；
                        - 你可以加入 backoff/jitter，或者只对特定 5xx（例如 502/503）重试。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 POST /api/users（JSON body），同时提供 RestClient 与 WebClient 版本，并对比测试写法")
    void exercise_postJsonComparison() {
        assertThat(true)
                .as("""
                        练习：增加一个 POST /api/users（JSON body），同时提供 RestClient 与 WebClient 版本，并对比测试写法。

                        目标：
                        - 你能解释：两种 client 在序列化/错误处理/测试体验上的差异。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个“请求级别 correlation id”（每次请求不同），并用测试证明它变化")
    void exercise_dynamicCorrelationId() {
        assertThat(true)
                .as("""
                        练习：增加一个“请求级别 correlation id”（每次请求不同），并用测试证明它变化。

                        提示：
                        - RestClient 需要 request interceptor（或类似机制）才能做到每次请求动态 header；
                        - WebClient 可以用 filter 做到每次请求动态 header。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 WebClient 从 block 方式改成纯 reactive 测试（StepVerifier + 不阻塞）")
    void exercise_pureReactiveTests() {
        assertThat(true)
                .as("""
                        练习：把 WebClient 从 block 方式改成纯 reactive 测试（StepVerifier + 不阻塞）。

                        目标：
                        - 学会在测试中避免 block 造成的超时/线程问题。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个超时场景，区分 connect timeout vs read/response timeout，并写测试固定差异")
    void exercise_timeoutTypes() {
        assertThat(true)
                .as("""
                        练习：增加一个超时场景，区分 connect timeout vs read/response timeout，并写测试固定差异。

                        提示：
                        - connect timeout 可以通过连接一个不可达地址复现（但要避免 flaky）；
                        - read/response timeout 可以通过 MockWebServer 延迟响应复现。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个“错误重试 + 幂等性”说明：哪些请求可以安全重试？把结论写进 docs/90")
    void exercise_idempotencyNotes() {
        assertThat(true)
                .as("""
                        练习：增加一个“错误重试 + 幂等性”说明：哪些请求可以安全重试？把结论写进 docs/90。

                        目标：
                        - 你能解释：为什么 GET 通常可重试，但 POST 可能产生副作用。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个“客户端观测性”实验：日志/指标（最小版）并在 tests 里验证一个观察点")
    void exercise_observabilityHook() {
        assertThat(true)
                .as("""
                        练习：增加一个“客户端观测性”实验：日志/指标（最小版）并在 tests 里验证一个观察点。

                        示例：
                        - 记录重试次数
                        - 在 response header 写一个标记（仅用于测试）
                        """)
                .isFalse();
    }
}


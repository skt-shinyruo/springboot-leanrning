package com.learning.springboot.bootobservability.part00_guide;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("练习：为 /api/ping 增加一个自定义 tag（例如 feature=ping），并把它固化为断言。")
class BootObservabilityExerciseTest {

    @Test
    void exercise_addCustomTagAndVerifyItAppearsInMeters() {
        // TODO:
        // 1) 在适当的地方添加自定义 tag（例如使用 MeterFilter 或 ObservationConvention）
        // 2) 写出断言证明：请求一次 /api/ping 后，metrics 中出现你的 tag
        // 3) 注意：不要依赖日志；以 MeterRegistry/ObservationRegistry 的可观察结果为准
    }
}

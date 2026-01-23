package com.learning.springboot.bootlogging.part00_guide;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("练习：把 MDC（例如 requestId）带入日志，并用测试固化断言。")
class BootLoggingExerciseTest {

    @Test
    void exercise_addMdcAndAssertItAppearsInLogs() {
        // TODO:
        // 1) 在 LoggingDemoService 中引入 MDC（例如 MDC.put(\"requestId\", \"r1\")）
        // 2) 配置日志 pattern（或使用自定义 appender）确保 MDC 能体现在输出中
        // 3) 用 OutputCaptureExtension 或 ListAppender 固化断言
    }
}

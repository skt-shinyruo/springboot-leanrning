package com.learning.springboot.springcoreaopweaving.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Exercises 默认禁用：学习者手动开启后完成改造题，再运行模块测试验证。")
class SpringCoreAopWeavingExerciseTest {

    private final InvocationLog invocationLog = InvocationLog.getInstance();

    @Test
    void exercise_extendOneJoinPointOrPointcut_thenKeepAllLabsGreen() {
        invocationLog.reset();

        // TODO 1：新增一个 join point 覆盖（例如：handler(...) / initialization(...) / preinitialization(...)）
        // TODO 2：或新增一个高级表达式组合（例如：cflowbelow / if()（慎用））
        // TODO 3：补齐断言：用 InvocationLog 的结构化事件验证“确实命中 + 命中次数正确 + 不影响既有 Labs”

        assertThat(invocationLog.count()).isEqualTo(0);
    }
}

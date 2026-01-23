package com.learning.springboot.bootautoconfiguration.part00_guide;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("练习：增加一个新的条件分支，并把它固化成断言（不要依赖日志）。")
class BootAutoConfigurationExerciseTest {

    @Test
    void exercise_addAConditionalBeanAndVerifyBackoff() {
        // TODO:
        // 1) 新增一个 AutoConfiguration：
        //    - 条件：demo.greeting.decorate=true 且 demo.greeting.enabled=true
        //    - 行为：提供一个不同实现的 GreetingService（例如返回不同前缀）
        // 2) 写出断言证明：当用户自定义 GreetingService 时，默认 auto-config 会 backoff
        // 3) 把断点打在 AutoProxyCreator/ConfigurationClassPostProcessor 相关入口，验证装配顺序
    }
}

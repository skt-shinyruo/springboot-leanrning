package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

/**
 * 注入阶段矩阵入口：聚合注入解析的关键分支与歧义处理实验。
 * 用于与注入分支矩阵形成回归入口。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansInjectionPhaseLabTest.class,
        SpringCoreBeansOptionalInjectionLabTest.class,
        SpringCoreBeansInjectionAmbiguityLabTest.class,
        SpringCoreBeansJsr330InjectionLabTest.class,
        SpringCoreBeansAutowireCandidateSelectionLabTest.class
})
class SpringCoreBeansInjectionPhaseMatrixLabTest {}

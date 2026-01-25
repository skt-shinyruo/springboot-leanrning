package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/**
 * RuntimeHints 边界入口：聚合 AOT 与 RuntimeHints 的核心验证实验。
 * 用于与 AOT/RuntimeHints 章节形成可跑闭环。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansAotRuntimeHintsLabTest.class,
        SpringCoreBeansAotFactoriesLabTest.class
})
class SpringCoreBeansRuntimeHintsBoundaryLabTest {}

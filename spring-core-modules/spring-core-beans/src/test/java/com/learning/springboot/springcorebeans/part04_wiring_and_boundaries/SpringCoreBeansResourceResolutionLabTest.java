package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

/**
 * 资源解析入口：聚合资源注入与占位符解析相关实验。
 * 作为装配与边界章节的可跑入口映射。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansResourceInjectionLabTest.class,
        SpringCoreBeansValuePlaceholderResolutionLabTest.class,
        SpringCoreBeansEnvironmentPropertySourceLabTest.class,
        SpringCoreBeansTypeConversionLabTest.class
})
class SpringCoreBeansResourceResolutionLabTest {}

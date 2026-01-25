package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/**
 * PropertyEditor 解析入口：聚合属性编辑器与值解析的关键实验。
 * 用于与 PropertyEditor/值解析章节形成可跑入口。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansPropertyEditorLabTest.class,
        SpringCoreBeansBeanDefinitionValueResolutionLabTest.class
})
class SpringCoreBeansPropertyEditorResolutionLabTest {}

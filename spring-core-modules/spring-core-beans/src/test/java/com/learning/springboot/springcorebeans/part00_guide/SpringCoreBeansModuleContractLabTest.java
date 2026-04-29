package com.learning.springboot.springcorebeans.part00_guide;

import com.learning.springboot.springcorebeans.testsupport.BeanDefinitionOriginDumperLabTest;
import com.learning.springboot.springcorebeans.testsupport.BeanGraphDumperLabTest;
import com.learning.springboot.springcorebeans.testsupport.DependencyDescriptorDumperLabTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 模块契约入口：同时验证文档导航契约与测试支撑层输出契约。
 * 用于保证 README/docs、Lab 引用与 testsupport 可观察性工具不会在后续重写中漂移。
 */
@Suite
@SelectClasses({
        SpringCoreBeansDocumentationContractTest.class,
        BeanGraphDumperLabTest.class,
        BeanDefinitionOriginDumperLabTest.class,
        DependencyDescriptorDumperLabTest.class
})
public class SpringCoreBeansModuleContractLabTest {
}

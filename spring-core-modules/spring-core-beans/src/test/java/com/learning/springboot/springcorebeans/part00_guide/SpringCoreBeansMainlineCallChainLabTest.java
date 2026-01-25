package com.learning.springboot.springcorebeans.part00_guide;

/**
 * 主线调用链入口：聚合容器启动与创建主线的关键实验。
 * 用于与主线时间线文档形成可跑闭环。
 */
import com.learning.springboot.springcorebeans.part01_ioc_container.SpringCoreBeansContainerLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansBeanCreationTraceLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansBootstrapInternalsLabTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansContainerLabTest.class,
        SpringCoreBeansBootstrapInternalsLabTest.class,
        SpringCoreBeansBeanCreationTraceLabTest.class
})
class SpringCoreBeansMainlineCallChainLabTest {}

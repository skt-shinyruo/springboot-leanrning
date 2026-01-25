package com.learning.springboot.springcorebeans.part01_ioc_container;

/**
 * FactoryBean 边界入口：对齐主线视角下的 FactoryBean 关键边界实验。
 * 作为容器主线章节的可跑入口映射。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansFactoryBeanDeepDiveLabTest.class,
        com.learning.springboot.springcorebeans.part04_wiring_and_boundaries.SpringCoreBeansFactoryBeanEdgeCasesLabTest.class
})
class SpringCoreBeansFactoryBeanEdgeCasesLabTest {}

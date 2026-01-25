package com.learning.springboot.springcorebeans.part01_ioc_container;

/**
 * 循环依赖边界入口：对齐容器主线的循环依赖边界实验。
 * 作为主线章节的可跑入口映射。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansCircularDependencyBoundaryLabTest.class,
        com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansEarlyReferenceLabTest.class
})
class SpringCoreBeansCircularDependencyBoundaryLabTest {}

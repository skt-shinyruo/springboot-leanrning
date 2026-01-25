package com.learning.springboot.springcorebeans.part01_ioc_container;

/**
 * 循环依赖边界入口：对齐容器主线的循环依赖边界实验。
 * 作为主线章节的可跑入口映射。
 */
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansCircularDependencyBoundaryLabTest;
import com.learning.springboot.springcorebeans.part03_container_internals.SpringCoreBeansEarlyReferenceLabTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansCircularDependencyBoundaryLabTest.class,
        SpringCoreBeansEarlyReferenceLabTest.class
})
class SpringCoreBeansCircularDependencyBoundaryLabTest {}

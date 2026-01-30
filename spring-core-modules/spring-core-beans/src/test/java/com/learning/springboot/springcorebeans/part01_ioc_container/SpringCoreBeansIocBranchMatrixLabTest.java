package com.learning.springboot.springcorebeans.part01_ioc_container;

/**
 * 关键分支矩阵入口（IoC）：聚合容器入口分支（注册方式、扫描/导入、容器差异），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansBeanDefinitionRegistrationDiffLabTest.class,
        SpringCoreBeansBeanFactoryVsApplicationContextLabTest.class,
        SpringCoreBeansComponentScanLabTest.class,
        SpringCoreBeansImportLabTest.class
})
class SpringCoreBeansIocBranchMatrixLabTest {}

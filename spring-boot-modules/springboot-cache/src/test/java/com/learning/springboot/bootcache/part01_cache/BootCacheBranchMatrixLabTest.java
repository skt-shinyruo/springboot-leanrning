package com.learning.springboot.bootcache.part01_cache;

/**
 * 关键分支矩阵入口：聚合 Cache 关键分支（SpEL key、缓存命中/更新路径等），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootCacheLabTest.class,
        BootCacheSpelKeyLabTest.class
})
class BootCacheBranchMatrixLabTest {}


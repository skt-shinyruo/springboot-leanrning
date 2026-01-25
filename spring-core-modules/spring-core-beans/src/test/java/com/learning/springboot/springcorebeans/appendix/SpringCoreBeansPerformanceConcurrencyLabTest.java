package com.learning.springboot.springcorebeans.appendix;

/**
 * 性能与并发入口：聚合同一 BeanFactory 并发与缓存相关实验。
 * 用于性能/并发专题的可跑入口映射。
 */
import com.learning.springboot.springcorebeans.part02_perf_concurrency.SpringCoreBeansConcurrentGetBeanLabTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SpringCoreBeansConcurrentGetBeanLabTest.class,
        SpringCoreBeansSingletonCacheExploreTest.class,
        SpringCoreBeansCachedIntrospectionExploreTest.class
})
class SpringCoreBeansPerformanceConcurrencyLabTest {}

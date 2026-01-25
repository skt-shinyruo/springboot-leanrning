# Spring Core Beans（IoC 容器）：目录

> 这一模块是整套 Spring 机制的地基：建议先沿着“主线时间线”顺读，把容器从启动到创建 Bean 的关键分支跑通；需要深入时再按 Part 进入细节。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/010-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/011-00-deep-dive-guide.md)
3. [主线叙事：从 `refresh()` 到 `doCreateBean()`（源码级）](part-03-container-internals/18-refresh-to-bean-creation-mainline.md)
4. [30 分钟快速闭环](part-00-guide/012-01-quickstart-30min.md)
5. [refresh() 调用链（先把阶段感建立起来）](part-00-guide/013-01-applicationcontext-refresh-call-chain.md)
6. [断点图（排障优先）](part-00-guide/013-02-breakpoint-map.md)

## 顺读主线（先把容器跑通）

1. [Bean 心智模型（含注册入口）](part-01-ioc-container/020-01-bean-mental-model.md)
2. [依赖解析与注入](part-01-ioc-container/014-03-dependency-injection-resolution.md)
3. [作用域与 prototype](part-01-ioc-container/015-04-scope-and-prototype.md)
4. [生命周期与回调](part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
5. [扩展点：Post-Processor](part-01-ioc-container/017-06-post-processors.md)
6. [配置类增强](part-01-ioc-container/018-07-configuration-enhancement.md)
7. [Spring Boot 自动配置如何落到容器](part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
8. [调试与观测](part-02-boot-autoconfig/019-11-debugging-and-observability.md)

## 深挖索引（按 Part）

- Part 01：IoC Container（基础主线）：[01](part-01-ioc-container/020-01-bean-mental-model.md)
- Part 02：Boot Autoconfig（把 Boot 接回容器）：[10](part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
- Part 03：Container Internals（内部机制与顺序）：[12](part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- Part 04：Wiring & Boundaries（边界、坑点与真实世界）：[18](part-04-wiring-and-boundaries/023-18-lazy-semantics.md)
- Part 05：AOT & Real World（AOT/Native/输入形态）：[40](part-05-aot-and-real-world/024-40-aot-and-native-overview.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[013-02-breakpoint-map.md](part-00-guide/013-02-breakpoint-map.md)
- Debugger Pack（断点包总入口）：[98-debugger-pack.md](appendix/98-debugger-pack.md)
- refresh() 调用链（源码主线锚点）：[013-01-applicationcontext-refresh-call-chain.md](part-00-guide/013-01-applicationcontext-refresh-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[011-04-branch-decision-matrix.md](part-00-guide/011-04-branch-decision-matrix.md)
- 排障 playbook：[025-90-common-pitfalls.md](appendix/025-90-common-pitfalls.md)
- 自检清单：[026-99-self-check.md](appendix/026-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- 可跑入口（Branch Matrix - IoC 分支）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- 可跑入口（Branch Matrix - 内部机制分支）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
- 练习与答案（Exercises/Solutions 约定）：[exercises-and-solutions.md](../../book/exercises-and-solutions.md)
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-beans -Dtest=*ExerciseSolutionTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（并发/性能 Lab - 同一 BeanFactory 并发 getBean）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConcurrentGetBeanLabTest test`

## 排坑与自检

- [常见坑](appendix/025-90-common-pitfalls.md)
- [自检](appendix/026-99-self-check.md)

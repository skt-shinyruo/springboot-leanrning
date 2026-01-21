# 第 11 章：04：关键分支矩阵（Beans Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Beans Branch Decision Matrix）
    - 怎么使用：把 IoC 容器最常见的关键分支（注册入口、扫描/导入、循环依赖边界、后置处理器顺序）整理成矩阵表，并提供可复现入口（Branch Matrix）。
    - 原理：容器行为=“注册 → refresh → 创建 bean → 扩展点影响顺序/边界”；分支多出现在 BPP 排序与 early reference/circular dependencies。
    - 源码入口：`AbstractApplicationContext#refresh` / `DefaultListableBeanFactory#doGetBean` / `AbstractAutowireCapableBeanFactory#doCreateBean`
    - 推荐 Lab：`SpringCoreBeansInternalsBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 11 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 12 章：01：30 分钟快速闭环（Minimal Mainline）](012-01-quickstart-30min.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 注册入口差异 | BeanFactory vs ApplicationContext | 能解释“多出来的基础设施 bean” | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` | beanDefinition 数量 |
| 扫描 vs 导入 | @ComponentScan / @Import | 注册路径不同但结果可对照 | `SpringCoreBeansIocBranchMatrixLabTest` | BeanDefinition 注册点 |
| 循环依赖边界 | 构造器循环依赖 | 失败并给出可定位异常 | `SpringCoreBeansCircularDependencyBoundaryLabTest` | 创建链路/异常栈 |
| early reference | 提前暴露引用 | 能解释“为什么能解开某些循环” | `SpringCoreBeansEarlyReferenceLabTest` | singletonFactories |
| BPP 顺序 | 多个 PostProcessor | 顺序影响行为/最终 bean | `SpringCoreBeansPostProcessorOrderingLabTest` | postProcess 调用顺序 |

## 推荐运行命令

- IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- Internals 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`

## 推荐断点（与断点地图配合）

- 断点地图（总入口）：[`013-02-breakpoint-map.md`](013-02-breakpoint-map.md)
- refresh 主线：`AbstractApplicationContext#refresh`
- 创建 bean：`AbstractAutowireCapableBeanFactory#doCreateBean`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/025-90-common-pitfalls.md`](../appendix/025-90-common-pitfalls.md)
- 自检：[`../appendix/026-99-self-check.md`](../appendix/026-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`
- Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-quickstart-30min.md](012-01-quickstart-30min.md)

<!-- BOOKIFY:END -->


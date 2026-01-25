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
上一章：[第 11 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 12 章：01：30 分钟快速闭环（Minimal Mainline）](012-01-quickstart-30min.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 关键分支矩阵（最小集合）

> 这张表不是“知识点清单”，而是 **排障压缩器**：
>
> 1) 先用症状把问题归类到某个分支行  
> 2) 跑对应最小复现入口（Lab/Matrix）  
> 3) 在 watchpoints 上确认触发条件与分支走向  
> 4) 再回到对应章节，把“机制解释 + 修复策略”补齐

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 注册入口差异 | BeanFactory vs ApplicationContext | 能解释“多出来的基础设施 bean” | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` | beanDefinition 数量 |
| 扫描 vs 导入 | @ComponentScan / @Import | 注册路径不同但结果可对照 | `SpringCoreBeansIocBranchMatrixLabTest` | BeanDefinition 注册点 |
| 循环依赖边界 | 构造器循环依赖 | 失败并给出可定位异常 | `SpringCoreBeansCircularDependencyBoundaryLabTest` | 创建链路/异常栈 |
| early reference | 提前暴露引用 | 能解释“为什么能解开某些循环” | `SpringCoreBeansEarlyReferenceLabTest` | singletonFactories |
| BPP 顺序 | 多个 PostProcessor | 顺序影响行为/最终 bean | `SpringCoreBeansPostProcessorOrderingLabTest` | postProcess 调用顺序 |
| 候选者选择 | 多个实现 + `@Primary/@Qualifier/@Priority` | 能解释“为什么注入的是它” | `SpringCoreBeansAutowireCandidateSelectionLabTest` | `resolveDependency` 的候选集合与排序 |
| `@Value` 占位符 | `${key}` / 默认值 / strict vs non-strict | 能解释“占位符何时解析、失败为何” | `SpringCoreBeansValuePlaceholderResolutionLabTest` | `Environment` / `PropertySources` / resolved value |
| prototype 注入边界 | singleton 注入 prototype | 能解释“为什么看起来像单例” | `SpringCoreBeansLabTest`（prototype 演示用例） | 注入发生在 `populateBean`；prototype 获取策略（Provider/`@Lookup`/scoped proxy） |
| FactoryBean 双重身份 | `getBean(\"foo\")` vs `getBean(\"&foo\")` | 能解释“拿到的是产品还是工厂” | `SpringCoreBeansFactoryBeanEdgeCasesLabTest` | `FactoryBeanRegistrySupport` 缓存与 `&` 前缀 |
| MergedBeanDefinition | parent/child 合并、BPP 写入 merged | 能解释“为什么定义最终长这样” | `SpringCoreBeansMergedBeanDefinitionLabTest` | `mergedBeanDefinitions` / `RootBeanDefinition` 内容 |

## 推荐运行命令

- IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- Internals 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`

> 提示：Matrix 类通常包含多条用例，它的价值在于**一次运行覆盖多条分支**；
> 当你锁定某一行后，再去跑对应的单一 Lab（更短、更聚焦）会更高效。

## 推荐断点（与断点地图配合）

- 断点地图（总入口）：[`013-02-breakpoint-map.md`](013-02-breakpoint-map.md)
- refresh 主线：`AbstractApplicationContext#refresh`
- 创建 bean：`AbstractAutowireCapableBeanFactory#doCreateBean`

## 关键观察点解释（你在变量里应该看见什么）

- **beanDefinition 数量**：帮助你判断“定义层”是否已经发生（很多问题不是注入问题，而是压根没注册）
- **singletonFactories/earlySingletonObjects**：帮助你判断是否进入 early reference 路径（循环依赖、代理与 raw injection 风险都在这里）
- **候选集合与排序**：`resolveDependency` 会先收集候选者，再按 resolver/primary/priority/order 过滤与排序
- **merged definition**：很多扩展点（尤其是 post-processors）会在 merged definition 上写入信息，影响后续创建

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/025-90-common-pitfalls.md`](../appendix/025-90-common-pitfalls.md)
- 自检：[`../appendix/026-99-self-check.md`](../appendix/026-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`
- Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-quickstart-30min.md](012-01-quickstart-30min.md)

<!-- BOOKIFY:END -->

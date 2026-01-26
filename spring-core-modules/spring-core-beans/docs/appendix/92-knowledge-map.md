# 92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab

## 导读

- 本章主题：**知识地图（从现象直达章节/断点/Lab）**
- 阅读方式建议：把本章当成“导航页”：你在真实项目里遇到问题时，不需要从头顺读文档，而是先在这里按现象定位到章节与断点入口，再回到对应章节补齐机制主线。

!!! summary "本章要点"

    - 本模块的学习目标是“可验证”：每个关键机制都应该有 **对应 Lab（可跑）** 与 **断点入口（可看见）**。
    - 排障优先级：先定位阶段（refresh 的哪一段）→ 再定位关键方法（最短调用链）→ 再看最小观察点（watch list）。
    - 读者 B/C 建议：遇到任何现象，先跑对应 Lab，把现象固定成断言，再去看章节，这样你不会被“像是理解了”的幻觉欺骗。

!!! example "本章配套实验（先跑再读）"

    - Lab（总入口 / 断点包）：
      - `SpringCoreBeansBreakpointPackLabTest`
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`

## 机制主线：用“症状驱动”组织知识点

Spring IoC 的知识点非常多，但真实排障并不会按“章节编号”发生。

你真正需要的是一张地图：

- 我现在看到什么现象？
- 它属于哪一段（定义层/实例层/注入/初始化/缓存/代理）？
- 我该去看哪一章、下哪几个断点、跑哪个 Lab？

下面这张表就是答案。

---

## 1. 现象 → 章节 → 断点入口（建议收藏）

| 现象（Symptoms） | 章节入口（Docs） | 最短断点入口（Entry） | 推荐 Lab |
| --- | --- | --- | --- |
| 注入失败：找不到 bean / 多候选 | [03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33. 候选选择与优先级](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) | `doResolveDependency/findAutowireCandidates/determineAutowireCandidate` | `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 循环依赖：constructor 死 / setter 可能活 | [09. 循环依赖](../part-01-ioc-container/09-circular-dependencies.md) | `DefaultSingletonBeanRegistry#getSingleton` / `addSingletonFactory` | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| early reference / raw vs wrapped | [16. early reference 与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md) | `getEarlyBeanReference` / `doCreateBean` 尾部检查 | `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 代理不生效 / 顺序不对 | [31. 代理发生的阶段](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)、[25. 手工注册 BPP](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md) | `registerBeanPostProcessors` / `applyBeanPostProcessorsAfterInitialization` | `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| `@Resource` 注入怪异（字段名相关） | [32. @Resource name-first](../part-04-wiring-and-boundaries/32-resource-injection-name-first.md) | `CommonAnnotationBeanPostProcessor#postProcessProperties` | `SpringCoreBeansResourceInjectionLabTest` |
| `@Value(\"${...}\")` 缺失不失败 / 原样字符串 | [34. 占位符 strict vs non-strict](../part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md) | `AbstractBeanFactory#resolveEmbeddedValue` | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| “值注入”到底是占位符/SpEL/转换哪一步错？ | [44. SpEL 与 @Value](../part-05-aot-and-real-world/44-spel-and-value-expression.md)、[36. 类型转换](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md) | `resolveEmbeddedValue` / `convertIfNecessary` | `SpringCoreBeansTypeConversionLabTest` |
| 你想把 refresh → doCreateBean 主线打穿 | [18. refresh→创建主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)、[10. 主线时间线](../part-00-guide/010-03-mainline-timeline.md) | `AbstractApplicationContext#refresh` / `doCreateBean` | `SpringCoreBeansMainlineCallChainLabTest` |
| 你需要“从异常到断点入口”的方法论 | [11. Debugging and Observability](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)、[98. Debugger Pack](98-debugger-pack.md) | 见 Debugger Pack | `SpringCoreBeansBreakpointPackLabTest` |
| 想“看见缓存/内部结构变化” | [97. Explore/Debug](97-explore-debug-tests.md) | `getSingleton` / `CachedIntrospectionResults#forClass` | `SpringCoreBeans*ExploreTest` |

---

## 2. 推荐顺读路线（从“能跑”到“能解释”）

如果你希望按最省时间的顺序提升能力，建议：

1) `part-00-guide/012-01-quickstart-30min.md`（30 分钟快启）  
2) `part-00-guide/010-03-mainline-timeline.md`（时间线：把机制放回阶段）  
3) `part-03-container-internals/18-refresh-to-bean-creation-mainline.md`（主线叙事：refresh → doCreateBean）  
4) Part 01/04 的注入、代理、循环依赖关键章（按你遇到的现象挑读）  
5) Appendix 的 Debugger Pack / Troubleshooting Checklist（把经验固化成套路）

---

## 一句话自检

你应该能做到：

1) 看到异常信息，能先判断它更像“定义层问题”还是“实例层问题”。  
2) 看到“注入/代理/循环依赖”的任一现象，能在 30 秒内找到对应章节与断点入口。  
3) 能用对应 Lab 把现象复现出来，而不是在业务项目里盲调。

<!-- BOOKIFY:START -->

上一章：[91. 术语表（Glossary）](91-glossary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[93. 面试复述模板（Interview Playbook）](93-interview-playbook.md)

<!-- BOOKIFY:END -->

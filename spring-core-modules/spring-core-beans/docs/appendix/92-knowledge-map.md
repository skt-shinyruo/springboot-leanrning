# 92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab

## 导读

- 本章主题：**知识地图（从现象直达章节/断点/Lab）**
- 阅读方式建议：把本章当成“导航页”：在真实项目里遇到问题时，不需要从头顺读文档，而是先在这里按现象定位到章节与断点入口，再回到对应章节补齐机制主线。
- 团队内训场景：若要给团队“按课时讲一遍”，优先看：[`99-team-training-kit.md`](99-team-training-kit.md)（60/90/120 分钟脚本 + Labs/断点/互动题）。

!!! summary "本章要点"

    - 本模块的学习目标是“可验证”：每个关键机制都应该有 **对应 Lab（可跑）** 与 **断点入口（可看见）**。
    - 排障优先级：先定位阶段（refresh 的哪一段）→ 再定位关键方法（最短调用链）→ 再看最小观察点（watch list）。
    - 读者 B/C 建议：遇到任何现象，先跑对应 Lab，把现象固定成断言，再去看章节，这样读者不会被“像是理解了”的幻觉欺骗。

!!! example "本章配套实验（先跑再读）"

    - Lab（总入口 / 断点包）：
      - `SpringCoreBeansBreakpointPackLabTest`
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`

## 面试复盘入口（把“地图”变成“可复述答案”）

- 若的目标是面试：建议先刷 `appendix/93-interview-playbook.md` 的题目结构，再回到本知识地图用“现象→章节→断点→Lab”把每个答案补齐证据链。
- 经验法则：面试题的高分答案不是“名词堆砌”，而是“结论 + 方法级证据链 + 一个可运行复现入口”。

## 机制主线：用“症状驱动”组织知识点

Spring IoC 的知识点非常多，但真实排障并不会按“章节编号”发生。

读者真正需要的是一张地图：

- 我现在看到什么现象？
- 它属于哪一段（定义层/实例层/注入/初始化/缓存/代理）？
- 我该去看哪一章、下哪几个断点、跑哪个 Lab？

下面这张表就是答案。

---

## 0. 核心七件套（概念 → 章节 → Lab）

若希望先用一份“检查表”把 Spring Beans 的关键知识点框住，再按需深挖，本模块建议优先掌握下面 7 件事：

| 核心点 | 章节入口（Docs） | 推荐 Lab/Test |
| --- | --- | --- |
| 1. BeanDefinition 体系 | [01. Bean 心智模型](../part-01-ioc-container/020-01-bean-mental-model.md)、[22. BeanName 与 alias](../part-04-wiring-and-boundaries/22-bean-names-and-aliases.md)、[24. BeanDefinition 覆盖](../part-04-wiring-and-boundaries/24-bean-definition-overriding.md)、[35. MergedBeanDefinition](../part-04-wiring-and-boundaries/35-merged-bean-definition.md) | `SpringCoreBeansContainerLabTest`、`SpringCoreBeansBeanNameAliasLabTest`、`SpringCoreBeansBeanDefinitionOverridingLabTest`、`SpringCoreBeansMergedBeanDefinitionLabTest` |
| 2. Bean 创建全链路 | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)、[30. 注入阶段](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)、[05. 生命周期](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)、[17. 回调顺序](../part-03-container-internals/17-lifecycle-callback-order.md) | `SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansInjectionPhaseLabTest`、`SpringCoreBeansLifecycleCallbackOrderLabTest` |
| 3. 依赖解析与注入细节 | [03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33. 候选选择与优先级](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)、[37. 泛型匹配注入误区](../part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md) | `SpringCoreBeansLabTest`、`SpringCoreBeansInjectionAmbiguityLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansOptionalInjectionLabTest` |
| 4. 容器扩展点 | [06. PostProcessor 总览](../part-01-ioc-container/017-06-post-processors.md)、[13. BDRPP](../part-03-container-internals/13-bdrpp-definition-registration.md)、[14. 顺序（Ordering）](../part-03-container-internals/14-post-processor-ordering.md)、[15. 实例化前短路](../part-03-container-internals/15-pre-instantiation-short-circuit.md) | `SpringCoreBeansRegistryPostProcessorLabTest`、`SpringCoreBeansPostProcessorOrderingLabTest`、`SpringCoreBeansPreInstantiationLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 5. 作用域与代理 | [04. Scope 与 prototype](../part-01-ioc-container/015-04-scope-and-prototype.md)、[28. 自定义 Scope + scoped proxy](../part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md)、[18. Lazy 语义](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md) | `SpringCoreBeansLabTest`、`SpringCoreBeansCustomScopeLabTest`、`SpringCoreBeansLazyLabTest` |
| 6. 循环依赖 | [09. 循环依赖](../part-01-ioc-container/09-circular-dependencies.md)、[16. early reference 与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md) | `SpringCoreBeansCircularDependencyBoundaryLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 7. 类型转换与属性绑定 | [36. 类型转换](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)、[50. PropertyEditor 与值解析](../part-05-aot-and-real-world/50-property-editor-and-value-resolution.md) | `SpringCoreBeansTypeConversionLabTest`、`SpringCoreBeansValuePlaceholderResolutionLabTest` |

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
| 若希望把 refresh → doCreateBean 主线打穿 | [18. refresh→创建主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)、[10. 主线时间线](../part-00-guide/010-03-mainline-timeline.md) | `AbstractApplicationContext#refresh` / `doCreateBean` | `SpringCoreBeansMainlineCallChainLabTest` |
| 需要“从异常到断点入口”的方法论 | [11. Debugging and Observability](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)、[98. Debugger Pack](98-debugger-pack.md) | 见 Debugger Pack | `SpringCoreBeansBreakpointPackLabTest` |
| 想“看见缓存/内部结构变化” | [97. Explore/Debug](97-explore-debug-tests.md) | `getSingleton` / `CachedIntrospectionResults#forClass` | `SpringCoreBeans*ExploreTest` |

---

## 1.1 误归因对照（避免把问题看错层）

- **现象**：`@Value("${...}")` 注入失败  
  **常见误归因**：以为配置没加载  
  **正确分流**：先看 `resolveEmbeddedValue` 是否 strict；再看 SpEL/转换（[34]/[44]/[36]）  

- **现象**：注入到了“不是我想要的实现”  
  **常见误归因**：以为 `@Order` 会影响单依赖  
  **正确分流**：看 `determineAutowireCandidate` 的收敛规则（[33]）  

- **现象**：Bean 变成代理导致类型不匹配  
  **常见误归因**：以为“容器拿错类型”  
  **正确分流**：定位 BPP 替换点（[31]）  

## 2. 推荐顺读路线（从“能跑”到“能解释”）

若希望按最省时间的顺序提升能力，建议：

1) `part-00-guide/012-01-quickstart-30min.md`（30 分钟快启）
2) `part-00-guide/010-03-mainline-timeline.md`（时间线：把机制放回阶段）
3) `part-03-container-internals/18-refresh-to-bean-creation-mainline.md`（主线叙事：refresh → doCreateBean）
4) Part 01/04 的注入、代理、循环依赖关键章（按遇到的现象挑读）
5) Appendix 的 Debugger Pack / Troubleshooting Checklist（把经验固化成套路）

---

## 自检要点
应能够做到：

1) 看到异常信息，能先判断它更像“定义层问题”还是“实例层问题”。
2) 看到“注入/代理/循环依赖”的任一现象，能在 30 秒内找到对应章节与断点入口。
3) 能用对应 Lab 把现象复现出来，而不是在业务项目里盲调。

<!-- BOOKIFY:START -->

上一章：[91. 术语表（Glossary）](91-glossary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[93. 面试复述模板（Interview Playbook）](93-interview-playbook.md)

<!-- BOOKIFY:END -->

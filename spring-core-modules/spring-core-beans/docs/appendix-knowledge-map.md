# 知识地图（Knowledge Map）：从现象直达章节/断点/Lab
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    观察对象：知识地图（从现象直达章节/断点/Lab）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBreakpointPackLabTest`。需要下探源码时，可以从 `DefaultSingletonBeanRegistry#getSingleton` / `CommonAnnotationBeanPostProcessor#postProcessProperties` / `AbstractBeanFactory#resolveEmbeddedValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：从现象反查章节与断点

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试收敛答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 知识地图的用法：现象、章节、断点、Lab

- 这页就是导航页：在真实项目里遇到问题时，不必从头顺读文档；先在这里按现象定位到章节与断点入口，再回到对应章节补齐机制主线与边界。
- 团队内训场景：若要给团队“按课时讲一遍”，优先看：[`appendix-team-training-kit.md`](appendix-team-training-kit.md)（60/90/120 分钟脚本 + Labs/断点/互动题）。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab（总入口 / 断点包）：
      - `SpringCoreBeansBreakpointPackLabTest`
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`

## 面试复盘入口（把“地图”变成“可复述答案”）

- 若目标是面试：先刷 `appendix-interview-playbook.md` 的题目结构，再回到本知识地图用“现象→章节→断点→Lab”把每个答案补齐证据链。
- 经验法则：面试题的高分答案不是“名词堆砌”，而是“结论 + 方法级证据链 + 一个可运行复现入口”。

## 机制主线：用“症状驱动”组织知识点

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

Spring IoC 的知识点多，但真实排障并不会按“章节编号”发生。

读者真正需要的是一张地图：

- 当前观察到什么现象？
- 它属于哪一段（定义层/实例层/注入/初始化/缓存/代理）？
- 应参阅哪一章、设置哪些断点、运行哪个 Lab？

下面这张表就是答案。

---

## 核心七件套（概念 → 章节 → Lab）

若希望先用一份“检查表”把 Spring Beans 的关键知识点框住，再按需深入分析，本模块优先掌握下面 7 件事：

| 核心点 | 章节入口（Docs） | 对应实验/测试 |
| --- | --- | --- |
| 1. BeanDefinition 体系 | [Bean 运行机制](ioc-bean-mental-model.md)、[BeanName 与 alias](wiring-bean-names-and-aliases.md)、[BeanDefinition 覆盖](wiring-bean-definition-overriding.md)、[MergedBeanDefinition](wiring-merged-bean-definition.md) | `SpringCoreBeansContainerLabTest`、`SpringCoreBeansBeanNameAliasLabTest`、`SpringCoreBeansBeanDefinitionOverridingLabTest`、`SpringCoreBeansMergedBeanDefinitionLabTest` |
| 2. Bean 创建全链路 | [refresh→doCreateBean 主线](internals-refresh-to-bean-creation-mainline.md)、[注入阶段](wiring-injection-phase-field-vs-constructor.md)、[生命周期](ioc-lifecycle-and-callbacks.md)、[回调顺序](internals-lifecycle-callback-order.md) | `SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansInjectionPhaseLabTest`、`SpringCoreBeansLifecycleCallbackOrderLabTest` |
| 3. 依赖解析与注入细节 | [依赖注入解析](ioc-dependency-injection-resolution.md)、[候选选择与优先级](wiring-autowire-candidate-selection-primary-priority-order.md)、[泛型匹配注入误区](wiring-generic-type-matching-pitfalls.md) | `SpringCoreBeansLabTest`、`SpringCoreBeansInjectionAmbiguityLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansProgrammaticResolveDependencyLabTest`、`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`、`SpringCoreBeansOptionalInjectionLabTest` |
| 4. 容器扩展点 | [PostProcessor 总览](ioc-post-processors.md)、[BDRPP](internals-bdrpp-definition-registration.md)、[顺序（Ordering）](internals-post-processor-ordering.md)、[实例化前短路](internals-pre-instantiation-short-circuit.md) | `SpringCoreBeansRegistryPostProcessorLabTest`、`SpringCoreBeansPostProcessorOrderingLabTest`、`SpringCoreBeansPreInstantiationLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 5. 作用域与代理 | [Scope 与 prototype](ioc-scope-and-prototype.md)、[自定义 Scope + scoped proxy](wiring-custom-scope-and-scoped-proxy.md)、[Lazy 语义](wiring-lazy-semantics.md) | `SpringCoreBeansLabTest`、`SpringCoreBeansCustomScopeLabTest`、`SpringCoreBeansLazyLabTest` |
| 6. 循环依赖 | [循环依赖](ioc-circular-dependencies.md)、[early reference 与循环依赖](internals-early-reference-and-circular.md) | `SpringCoreBeansCircularDependencyBoundaryLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 7. 类型转换与属性绑定 | [类型转换](wiring-type-conversion-and-beanwrapper.md)、[PropertyEditor 与值解析](aot-property-editor-and-value-resolution.md) | `SpringCoreBeansTypeConversionLabTest`、`SpringCoreBeansValuePlaceholderResolutionLabTest` |

## 现象 → 章节 → 断点入口（排障时从这里选路）

| 现象（Symptoms） | 章节入口（Docs） | 最短断点入口（Entry） | 断点组（断点地图） | 对应 Lab |
| --- | --- | --- | --- | --- |
| 注入失败/注入歧义：NoSuch/NoUnique/注入到了不是预期实现 | [依赖注入解析](ioc-dependency-injection-resolution.md)、[候选选择与优先级](wiring-autowire-candidate-selection-primary-priority-order.md) | `doResolveDependency/findAutowireCandidates/determineAutowireCandidate` | [C6](guide-breakpoint-map.md#c6) | `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` |
| 循环依赖：constructor 死 / setter 可能活 | [循环依赖](ioc-circular-dependencies.md) | `DefaultSingletonBeanRegistry#getSingleton` / `addSingletonFactory` | [C5](guide-breakpoint-map.md#c5) | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| early reference / raw vs wrapped | [early reference 与循环依赖](internals-early-reference-and-circular.md) | `getEarlyBeanReference` / `doCreateBean` 尾部检查 | [C5](guide-breakpoint-map.md#c5) / [C7](guide-breakpoint-map.md#c7) | `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 代理不生效 / 顺序不对 | [代理发生的阶段](wiring-proxying-phase-bpp-wraps-bean.md)、[手工注册 BPP](wiring-programmatic-bpp-registration.md) | `registerBeanPostProcessors` / `applyBeanPostProcessorsAfterInitialization` | [C4](guide-breakpoint-map.md#c4) / [C7](guide-breakpoint-map.md#c7) | `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| `@Resource` 注入怪异（字段名相关） | [@Resource name-first](wiring-resource-injection-name-first.md) | `CommonAnnotationBeanPostProcessor#postProcessProperties` | [C6](guide-breakpoint-map.md#c6) | `SpringCoreBeansResourceInjectionLabTest` |
| `@Value("${...}")` 缺失不失败 / 原样字符串 | [占位符 strict vs non-strict](wiring-value-placeholder-resolution-strict-vs-non-strict.md) | `AbstractBeanFactory#resolveEmbeddedValue` | [C3](guide-breakpoint-map.md#c3) / [C6](guide-breakpoint-map.md#c6) | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| “值注入”到底是占位符/SpEL/转换哪一步错？ | [SpEL 与 @Value](aot-spel-and-value-expression.md)、[类型转换](wiring-type-conversion-and-beanwrapper.md) | `resolveEmbeddedValue` / `convertIfNecessary` | [C6](guide-breakpoint-map.md#c6) | `SpringCoreBeansTypeConversionLabTest` |
| 若希望把 refresh → doCreateBean 主线打穿 | [refresh→创建主线](internals-refresh-to-bean-creation-mainline.md)、[主线时间线](guide-mainline-timeline.md) | `AbstractApplicationContext#refresh` / `doCreateBean` | [C1](guide-breakpoint-map.md#c1) / [C5](guide-breakpoint-map.md#c5) | `SpringCoreBeansMainlineCallChainLabTest` |
| 需要“从异常到断点入口”的方法论 | [Debugging and Observability](boot-debugging-and-observability.md)、[断点包](appendix-debugger-pack.md) | 见 断点包 | 见 [断点地图](guide-breakpoint-map.md#c1) | `SpringCoreBeansBreakpointPackLabTest` |
| 想“观察到缓存/内部结构变化” | [Explore/Debug](appendix-explore-debug-tests.md) | `getSingleton` / `CachedIntrospectionResults#forClass` | [C5](guide-breakpoint-map.md#c5) | `SpringCoreBeans*ExploreTest` |

---

## 1.1 误归因对照（避免把问题看错层）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- **现象**：`@Value("${...}")` 注入失败
  **常见误归因**：以为配置没加载
  **正确分流**：先看 `resolveEmbeddedValue` 是否 strict；再看 SpEL/转换（[34]/[44]/[36]）

- **现象**：注入到了“不是预期的实现”
  **常见误归因**：以为 `@Order` 会影响单依赖
  **正确分流**：看 `determineAutowireCandidate` 的收敛规则（[33]）

- **现象**：Bean 变成代理导致类型不匹配
  **常见误归因**：以为“容器拿错类型”
  **正确分流**：定位 BPP 替换点（[31]）

## 顺读路线：从“可运行”到“能解释”

若希望按更快收敛的顺序提升能力，按下面的路径推进：

1. `guide-quickstart-30min.md`（30 分钟快启）
2. `guide-mainline-timeline.md`（时间线：把机制放回阶段）
3. `internals-refresh-to-bean-creation-mainline.md`（主线叙事：refresh → doCreateBean）
4. 相关部分 的注入、代理、循环依赖关键章（按遇到的现象挑读）
5. Appendix 的 断点包 / Troubleshooting Checklist（把经验固化为流程）

---

## 验收口径：30 秒内能找到章节和断点
读完后应能做到：

1. 看到异常信息，能先判断其定位更接近“定义层问题”还是“实例层问题”。
2. 看到“注入/代理/循环依赖”的任一现象，能在 30 秒内找到对应章节与断点入口。
3. 能用对应 Lab 把现象复现出来，而不是在业务项目里盲调。


## 小结：知识地图只负责把入口选准

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

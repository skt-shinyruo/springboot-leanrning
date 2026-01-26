# 93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC

## 导读

- 本章主题：**面试复述模板：用“证据链”回答 Spring IoC**
- 阅读方式建议：把本章当作“可复习题库”。每道题都给出：一句话结论 → 关键证据链（方法/数据结构）→ 对应 Lab。你不靠背诵，而靠“能跑能断点”拿分。

!!! summary "本章要点"

    - 绝大多数 IoC 面试题不是考 API，而是考：你能不能把“概念”落到 **refresh 主线 + 关键分支 + 数据结构**。
    - 复述最常见扣分点：只有结论没有证据；只有名词没有时机；只会说“三级缓存”但说不清它解决了什么问题。
    - 本章每题都给出“最小证据链入口”：你至少能说出 1 个关键方法 + 3 个观察点 + 1 个可运行 Lab。

!!! example "本章配套实验（先跑再读）"

    - Lab（建议作为复习入口总集合）：`SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansBreakpointPackLabTest`

## 机制主线：面试答题的“标准结构”

推荐你用一个固定结构回答（不管题目问什么，都能套）：

1) **一句话结论（What）**：你主张的结论是什么？  
2) **关键约束（When/Where）**：它发生在 refresh 的哪一段？为什么这个时机决定了行为？  
3) **证据链（Evidence）**：关键方法/关键分支/关键数据结构是什么？  
4) **可复现入口（Repro）**：本仓库哪个 Lab 能跑出这个现象？

下面按主题给出高频题模板。

---

## 1. 容器主线：refresh 到底干了什么？

### Q1：`ApplicationContext#refresh` 的主线你能讲清楚吗？

- 一句话结论：refresh = 准备 BeanFactory → 定义层处理（BFPP/BDRPP）→ 注册 BPP 链 → 创建单例（doCreateBean）→ 完成与回调。  
- 证据链：
  - `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（定义层）
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（实例层）
  - `finishBeanFactoryInitialization` → `doCreateBean`（创建单例）
- Lab：`SpringCoreBeansMainlineCallChainLabTest`

对照章节：

- `part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- `part-00-guide/010-03-mainline-timeline.md`

---

## 2. 注入解析：为什么会 NoSuch / NoUnique？

### Q2：按类型注入到底怎么选候选？

- 一句话结论：单依赖注入不是“按类型拿一个”，而是：先收集候选（by type）→ 再按规则收敛（primary/qualifier/name/priority...）→ 最终注入。  
- 证据链：
  - `doResolveDependency` → `findAutowireCandidates` → `determineAutowireCandidate`
  - 观察点：`matchingBeans.keySet()`、`dependencyName`、`primaryCandidate`
- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`

对照章节：

- `part-01-ioc-container/014-03-dependency-injection-resolution.md`
- `part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`

### Q3：`@Resource` 和 `@Autowired` 的核心差异？

- 一句话结论：`@Resource` 更偏 name-first（字段名/显式 name），由 `CommonAnnotationBeanPostProcessor` 处理；`@Autowired` 更偏 type-first，由 `AutowiredAnnotationBeanPostProcessor` 处理。  
- 证据链：
  - `CommonAnnotationBeanPostProcessor#postProcessProperties`
  - 观察点：`resourceName`（默认字段名）是否命中 beanName
- Lab：`SpringCoreBeansResourceInjectionLabTest`

对照章节：

- `part-04-wiring-and-boundaries/32-resource-injection-name-first.md`

---

## 3. 生命周期：初始化回调顺序你能讲到证据吗？

### Q4：Aware/@PostConstruct/afterPropertiesSet/initMethod 的顺序？

- 一句话结论：初始化发生在 `initializeBean`，包含 Aware 回调、before-init BPP、初始化方法、after-init BPP（可能返回 proxy）。  
- 证据链：
  - `AbstractAutowireCapableBeanFactory#initializeBean`
  - `invokeAwareMethods` / `invokeInitMethods` / `applyBeanPostProcessorsBefore/AfterInitialization`
- Lab：生命周期相关 Lab（本模块有多条）

对照章节：

- `part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- `part-03-container-internals/17-lifecycle-callback-order.md`

---

## 4. Post-Processor：BFPP vs BPP 到底差在哪？

### Q5：为什么 BFPP 很“早”，BPP 很“后”？

- 一句话结论：BFPP/BDRPP 发生在实例化之前，改的是 BeanDefinition；BPP 发生在 bean 创建过程中，改的是实例（甚至替换成 proxy）。  
- 证据链：
  - `invokeBeanFactoryPostProcessors` vs `registerBeanPostProcessors`
  - 观察点：BPP 链是否完整、目标 bean 是否创建过早错过 BPP
- Lab：processor/ordering 相关 Lab（本模块有多条）

对照章节：

- `part-01-ioc-container/017-06-post-processors.md`
- `part-03-container-internals/14-post-processor-ordering.md`
- `part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`

---

## 5. 循环依赖：三级缓存到底解决了什么？

### Q6：为什么 constructor cycle 基本 fail-fast，而 setter 有时能救？

- 一句话结论：constructor 依赖发生在实例化之前，没有 early exposure 窗口；setter/field 依赖发生在实例已创建但未初始化完的窗口期，singleton 可以提前暴露引用把环跑起来。  
- 证据链：
  - `doCreateBean` 的 early exposure（`addSingletonFactory`）
  - `getSingleton(beanName, allowEarlyReference)` 三层命中（final/early/factory）
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`

对照章节：

- `part-01-ioc-container/09-circular-dependencies.md`

### Q7：`getEarlyBeanReference` 解决的是什么问题？

- 一句话结论：它解决的是“early 引用是否等于最终暴露形态（proxy/wrapper）”，避免 raw 注入绕过代理与 raw/wrapped 不一致 fail-fast。  
- 证据链：
  - `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
  - `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
  - `doCreateBean` 尾部 raw vs wrapped 一致性检查
- Lab：`SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`

对照章节：

- `part-03-container-internals/16-early-reference-and-circular.md`

---

## 6. FactoryBean：为什么 getBean(\"name\") 拿到的不是工厂？

### Q8：`FactoryBean` 的 product vs factory 怎么区分？

- 一句话结论：`getBean(\"name\")` 默认拿 product；`getBean(\"&name\")` 才拿 factory 本身。  
- 证据链：
  - `FactoryBeanRegistrySupport` 相关路径
  - 观察点：`&` 前缀分流
- Lab：`SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`

对照章节：

- `part-01-ioc-container/08-factorybean.md`
- `part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
- `part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`

---

## 7. 值注入三连：占位符 / SpEL / 类型转换

### Q9：`@Value(\"${missing}\")` 为什么可能不失败？

- 一句话结论：取决于 embedded value resolver 是否 strict；默认可能 non-strict 原样保留 `${...}`；注册 `PropertySourcesPlaceholderConfigurer` 可使缺失占位符 fail-fast。  
- 证据链：`resolveEmbeddedValue` / `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`
- Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`

对照章节：

- `part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`

### Q10：字符串怎么变成 int/Duration/自定义值对象？

- 一句话结论：转换发生在注入/属性填充阶段的 `convertIfNecessary`；决策点通常在 `TypeConverterDelegate`。  
- 证据链：`applyPropertyValues` / `BeanWrapperImpl#setPropertyValue` / `TypeConverterDelegate#convertIfNecessary`
- Lab：`SpringCoreBeansTypeConversionLabTest`

对照章节：

- `part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- `part-05-aot-and-real-world/44-spel-and-value-expression.md`

---

## 一句话自检

你应该能做到：

1) 任意挑一题，说出 1 个关键方法名 + 3 个 watch list + 1 个对应 Lab。  
2) 把“名词”翻译成“时机”：BFPP/BDRPP/BPP 分别发生在哪一段？  
3) 把“我觉得”替换成“我看见”：能在调试器里描述三层缓存/early reference/代理替换发生的瞬间。

<!-- BOOKIFY:START -->

上一章：[92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab](92-knowledge-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[94. 生产排障清单（Troubleshooting Checklist）](94-production-troubleshooting-checklist.md)

<!-- BOOKIFY:END -->

# 92. 知识地图（Concept → Chapter → Lab）

## 导读

这份“知识地图”解决两类阅读方式：

1) **顺读成体系**：按主线把概念串起来（更适合 A/B）
2) **按问题跳读**：按“症状/异常/现象”快速定位章节与可运行 Lab（更适合 B/C）

如果你是第一次系统学 Spring Beans，推荐先把这三份工具页收藏：

- [调试与自检：如何“看见”容器正在做什么](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)
- [生产排障清单（症状 → 下一步断点）](94-production-troubleshooting-checklist.md)
- [面试复述模板（30 秒 / 2 分钟 / 加分点）](93-interview-playbook.md)

---

## 1) 顺读主线（最推荐）

把 Beans 这门课读成“主线故事”，你只需要抓住两条主线：

- **定义层**：BeanDefinition 从哪来、怎么被加工
- **实例层**：Bean 如何被创建/注入/初始化/被替换为 proxy

推荐顺序（读完每段都跑 1 个 Lab 固化手感）：

1. **容器与注册入口（定义层入门）**
   - [Bean 心智模型与注册入口](../part-01-ioc-container/020-01-bean-mental-model.md)
   - [Bean 注册入口（扫描/@Bean/@Import/Registrar/编程式注册）](../part-01-ioc-container/02-bean-registration.md)
2. **依赖注入（实例层主线）**
   - [依赖注入解析：候选收集 → 候选收敛](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
   - [候选收敛边界：@Primary/@Priority/@Order](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
3. **Scope 与生命周期（“看起来像单例/回调顺序说不清”）**
   - [Scope 与 prototype 注入陷阱](../part-01-ioc-container/015-04-scope-and-prototype.md)
   - [生命周期：初始化、销毁与回调](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
4. **扩展点与顺序（解释“为什么注解能工作/为什么增强不生效”）**
   - [容器扩展点：BFPP vs BPP](../part-01-ioc-container/017-06-post-processors.md)
   - [post-processor 顺序（PriorityOrdered/Ordered）](../part-03-container-internals/14-post-processor-ordering.md)

---

## 2) 按问题跳读（症状 → 章节 → Lab）

### 2.1 注入失败：NoSuch / NoUnique / UnsatisfiedDependency

- 先读：
  - [依赖注入解析（主线）](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
  - [候选收敛边界（@Primary/@Priority/@Order）](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
- 再跑：
  - `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 推荐断点：
  - `DefaultListableBeanFactory#doResolveDependency`
  - `DefaultListableBeanFactory#findAutowireCandidates`
  - `DefaultListableBeanFactory#determineAutowireCandidate`

### 2.2 prototype 注入 singleton 后“像单例”

- 先读：
  - [Scope 与 prototype 注入陷阱](../part-01-ioc-container/015-04-scope-and-prototype.md)
- 再跑：
  - `SpringCoreBeansLabTest#demonstratesPrototypeScopeBehavior`
- 推荐断点：
  - `AbstractBeanFactory#doGetBean`（对照 direct vs provider）

### 2.3 `@Value("${...}")` 没解析/值变成 "${...}"（non-strict vs strict）

- 先读：
  - [占位符解析：strict vs non-strict](../part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
- 再跑：
  - `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 推荐断点：
  - `AbstractBeanFactory#resolveEmbeddedValue`
  - `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`

### 2.4 代理导致行为“不符合直觉”（self-invocation / 最终对象不是原始实例）

- 先读：
  - [代理产生阶段：BPP 如何把 Bean 换成 proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
  - [调试与自检：如何锁定是哪一个 BPP 换壳](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)
- 再跑：
  - `SpringCoreBeansProxyingPhaseLabTest`
- 推荐断点：
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

### 2.5 循环依赖：为什么 setter 能救、constructor 无解？

- 先读：
  - [循环依赖：概览与边界](../part-01-ioc-container/09-circular-dependencies.md)
  - [early reference（循环依赖为什么能“先拿到一个对象”）](../part-03-container-internals/16-early-reference-and-circular.md)
- 再跑：
  - `SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`
- 推荐断点：
  - `DefaultSingletonBeanRegistry#getSingleton`
  - `DefaultSingletonBeanRegistry#addSingletonFactory`

### 2.6 “顺序”导致结果不同（post-processors / 增强没生效）

- 先读：
  - [容器扩展点：BFPP vs BPP](../part-01-ioc-container/017-06-post-processors.md)
  - [post-processor 顺序（PriorityOrdered/Ordered）](../part-03-container-internals/14-post-processor-ordering.md)
- 再跑：
  - `SpringCoreBeansPostProcessorOrderingLabTest`
  - `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`
- 推荐断点：
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`

---

## 3) 一页纸：概念 → 章节（用于复盘/面试/查缺补漏）

- **定义层（BeanDefinition）**
  - [Bean 心智模型与注册入口](../part-01-ioc-container/020-01-bean-mental-model.md)
  - [Bean 注册入口（扫描/@Bean/@Import/Registrar）](../part-01-ioc-container/02-bean-registration.md)
  - [MergedBeanDefinition（最终配方）](../part-04-wiring-and-boundaries/35-merged-bean-definition.md)
- **实例层（create/populate/initialize）**
  - [依赖注入解析（主线）](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
  - [生命周期（回调顺序/销毁语义）](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
  - [代理产生阶段（BPP 换壳）](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- **扩展点与顺序（解释“注解为何能工作/为何增强没生效”）**
  - [BFPP vs BPP](../part-01-ioc-container/017-06-post-processors.md)
  - [post-processor 顺序](../part-03-container-internals/14-post-processor-ordering.md)
- **排障入口**
  - [调试与自检（看见容器）](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)
  - [生产排障清单](94-production-troubleshooting-checklist.md)
  - [常见坑清单](025-90-common-pitfalls.md)
  - [自测题](026-99-self-check.md)

---

## 最小可运行实验（Lab）

- 本页是索引类内容，不提供单一 Lab 入口。
- 推荐做法：从本页跳转到对应章节后，按章节中的“本章配套实验（先跑再读）”运行对应 Test。
- 如果你想“一次跑完把手感固化”：优先跑 `SpringCoreBeansLabTest`，再按症状补跑对应 Lab。

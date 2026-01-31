# 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：30 分钟快速闭环：先快后深（3 个最小实验入口）
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 11 章：00. 深入分析指南：把“Bean 三层模型”落到源码与断点](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](013-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章目标：给出一条**30 分钟可完成验证、可设置断点、可形成正反馈**的快启路线。
- 原则：每个实验都满足“命令可运行 + 应当看到什么 + 断点入口 + 最小 watch list + 下一步去哪读”。

!!! summary "本章要点"

    应能够用“现象 → 断点 → 证据”回答三个问题：

    1) **为什么注入的是它？**（候选怎么收集、怎么收敛）
    2) **为什么 prototype 注入进 singleton 会‘像单例’？**（以及怎么修）
    3) **为什么读者获取到的是 proxy？**（换壳发生在哪一段、是谁换的）

## 章节验收口径（10/30/3：快启闭环）

本章的目标不是“学全”，而是让读者用最短时间完成第一次可复现闭环：

- **10 分钟（知道要看什么）**：能说清楚本章 3 个实验分别在验证哪一层问题（候选收敛 / scope 边界 / 代理替换）。
- **30 分钟（能完成验证并证明）**：至少完成验证 1 个实验，并能用断点/观察点解释“为什么结果是这样”（不要只看输出）。
- **3 个抓手（能迁移到真实问题）**：每次遇到 Bean 问题先问清楚三件事：
  1. 定义层：有没有注册到 `BeanDefinition`？
  2. 实例层：对象是在哪里创建/被替换的？
  3. 注入层：候选如何收敛到最终注入对象？

## 30 分钟内要抓住的最小心智模型（5 个对象 + 4 条入口）

不追求背细节，只追求“能在断点里观察到”的 5 个对象/入口：

1) **BeanDefinition（定义层）**  
   - 入口方法：`DefaultListableBeanFactory#registerBeanDefinition`  
   - 需要看到的变化：定义进 registry，`beanDefinitionMap` 里出现条目
2) **DependencyDescriptor（依赖解析层）**  
   - 入口方法：`DefaultListableBeanFactory#doResolveDependency`  
   - 需要看到的变化：候选集合被收集并收敛为唯一候选
3) **BeanWrapper（属性填充层）**  
   - 入口方法：`AbstractAutowireCapableBeanFactory#populateBean`  
   - 需要看到的变化：`PropertyValues` 转换并写入目标对象
4) **BeanPostProcessor（实例增强层）**  
   - 入口方法：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`  
   - 需要看到的变化：`bean` → `result` 的第一次替换（proxy/包装）
5) **Singleton 缓存（生命周期结果层）**  
   - 入口方法：`DefaultSingletonBeanRegistry#getSingleton`  
   - 需要看到的变化：是否命中 `singletonObjects`，以及 early reference 的介入

## 快启路线（按顺序运行）

> 建议：先仅运行单个测试方法（噪音最少），确认能复现后再运行整类测试。

### 实验 1：单依赖注入如何从多个候选里“收敛”到一个（@Qualifier）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans test`

**应当看到什么（证据）：**

- 断言通过：`FormattingService.format("Hello") == "HELLO"`
- 意味着：容器面对多个 `TextFormatter` 候选时，最终注入点命中了 `@Qualifier` 指定的那个实现

**推荐断点（闭环版）：**

1) `DefaultListableBeanFactory#doResolveDependency`：依赖解析总入口（看 `descriptor.getDependencyType()`）
2) `DefaultListableBeanFactory#findAutowireCandidates`：候选收集（看 `matchingBeans` 的 key：beanName）
3) `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`：Qualifier 过滤/匹配（看为什么其它候选被排除）
4) `DefaultListableBeanFactory#determineAutowireCandidate`：候选收敛总入口（最终 winner 在这里确定）

**固定观察点（watch list）：**

- `descriptor`（注入点抽象：需要什么类型/是否 required）
- `matchingBeans`（候选集合：Map<beanName, candidate>）
- `autowiredBeanName` / `candidateName`（最终命中者）

**下一步去哪读（补知识点）：**

- [03. 依赖注入解析：候选收集→收敛→最终注入](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

---

### 实验 2：prototype 注入 singleton 的“反直觉”（以及 ObjectProvider 如何修）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#demonstratesPrototypeScopeBehavior test`

**应当看到什么（证据）：**

- `directPrototypeConsumer.currentId()` 两次返回 **相同** id（看起来像单例）
- `providerPrototypeConsumer.newId()` 两次返回 **不同** id（每次获取新 prototype）

**推荐断点（闭环版）：**

1) `DefaultListableBeanFactory#doResolveDependency`：解析注入点（确认 prototype 依赖被怎样注入进 singleton）
2) `AbstractBeanFactory#doGetBean`：每次取 bean 的总入口（对照 direct vs provider 的调用路径差异）
3) `DefaultListableBeanFactory#getBeanProvider`（可选）：理解 provider 的“延迟获取”语义

**固定观察点（watch list）：**

- `beanName` / `requiredType`（读者到底在取哪个 bean）
- `isSingletonCurrentlyInCreation(beanName)`（理解创建阶段与缓存命中）
- `singletonObjects`（对照：prototype 不会像 singleton 一样被缓存）

**下一步去哪读（补知识点）：**

- [04. scope 与 prototype：prototype 注入陷阱与三种解法](../part-01-ioc-container/015-04-scope-and-prototype.md)

---

### 实验 3：为什么注入的是 proxy？（BPP 何时把对象换掉）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement test`

**应当看到什么（证据）：**

- 输出中出现 `OBSERVE:` 提示（bean 创建阶段记录）
- 关键证据：某个 bean 在初始化链路中 **从原对象变成了 result（proxy/wrapper）**

**推荐断点（闭环版）：**

1) `AbstractAutowireCapableBeanFactory#initializeBean`：进入最终暴露对象产生链路
2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：在循环里观察 `bean` → `result` 的第一次替换
3) （可选）`BeanPostProcessor#postProcessAfterInitialization`：在具体 BPP 上锁定“是谁换的”

**固定观察点（watch list）：**

- `beanName`（条件断点：只看目标 bean）
- `bean`（原对象） vs `result`（最终对象）：`result != bean` 即替换发生
- `beanFactory.getBeanPostProcessors()`（BPP 链与顺序：解释“为什么是它换的/为什么先后顺序这样”）

**下一步去哪读（补知识点）：**

- [06. BFPP vs BPP：定义层改配方 vs 实例层改对象](../part-01-ioc-container/017-06-post-processors.md)
- [31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- [11. 调试与自检：从异常到断点入口](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)

## 新手易卡点与修复路径（快启版）

- **断点不命中**  
  - 常见原因：bean 从未创建（`@Lazy`/未触发 `getBean`/未执行到 `preInstantiateSingletons`）  
  - 修复路径：改成非 lazy 或显式 `getBean`；在 `doGetBean`/`preInstantiateSingletons` 观察是否进入
- **运行耗时过长/输出噪声过多**  
  - 常见原因：运行了全量 `@SpringBootTest` 或扫描了整个 classpath  
  - 修复路径：用 `AnnotationConfigApplicationContext` 只注册最小配置类；仅运行方法级测试（`-Dtest=Class#method`）
- **调试卡死或断点命中频率过高**  
  - 常见原因：断点落在高频循环（如 `isTypeMatch`/`doGetBean`）  
  - 修复路径：加条件断点（`beanName` 过滤）+ 固定 watch list（只看 3–5 个关键变量）

## 小结与下一章

- 读者已经获取到了“最小闭环”：**从测试方法进、用固定断点收敛噪音、用 watch list 获取到证据**
- 下一步建议：按主线继续读 Part 01（01→09），并把每章的“复现入口”运行一次后再读源码锚点

## 证据链（调用链 + 断点 + 断言）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「30 分钟快速闭环：先快后深（3 个最小实验入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`；断言：应能够解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「30 分钟快速闭环：先快后深（3 个最小实验入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory`；断言：应能够解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「30 分钟快速闭环：先快后深（3 个最小实验入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`；断言：应能够解释“为什么此处生效/为什么此处不生效”。
- 建议：运行完成 ``SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans`` 后，把上述观察点逐条对照，写出读者自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

上一章：[00. 深潜指南（如何读/如何断点/如何建立肌肉记忆）](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. Bean 心智模型：BeanDefinition / Bean / 容器三层](../part-01-ioc-container/020-01-bean-mental-model.md)

<!-- BOOKIFY:END -->

!!! example "本章配套实验（先运行再读）"

    - Lab（30 分钟闭环入口）：
      - `SpringCoreBeansLabTest`
      - `SpringCoreBeansMainlineCallChainLabTest`
      - `SpringCoreBeansBreakpointPackLabTest`

## BreakpointPack 深入复盘（可选：把“快启”升级为“可排障”）

若运行了 `SpringCoreBeansBreakpointPackLabTest`，至少能复述以下 3 条“可断言结论”：

1) **循环依赖能不能救，取决于 early reference 介入时机**  
   - 证据链：`DefaultSingletonBeanRegistry#addSingletonFactory` → `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`  
   - 对应 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
2) **BPP 顺序决定“先换壳还是先注入”，会导致 raw 注入**  
   - 证据链：`PostProcessorRegistrationDelegate#registerBeanPostProcessors` → `applyBeanPostProcessorsAfterInitialization`  
   - 对应 Lab：`SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
3) **FactoryBean 与占位符解析是两条“最容易被误解”的分支**  
   - 证据链：`AbstractBeanFactory#getObjectForBeanInstance` / `FactoryBeanRegistrySupport#getObjectFromFactoryBean`  
   - 对应 Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：为每个实验补“机制证据链入口”：运行完成后下一步去哪设置断点证明结论。
	    - B（边界反例）：“常见偏差反例”：为何可能无法观察到预期现象（版本差异/代理/初始化顺序等）。
    - C（排障 SOP）：“从实验到排障”：实验结论如何映射到生产排障（第一断点在哪里）。
    - D（断点观察）：“断点闭环路径”：每个实验推荐 3–5 个断点与 watch list。
    - E（面试复述）：“3 分钟复述训练”：每个实验给 1 个面试式回答模板。
<!-- AE-DEEPENING:END -->
## 机制主线

30 分钟快启只做三件事：

1) 运行一个最小容器（应能够看到 BeanDefinition → bean 实例的转换）
2) 运行一次 refresh 主线（应能够把“处理器/注入/回调”放回时间线）
3) 下两三个关键断点（应能够在调试器里观察到关键数据结构变化）

当读者完成这三件事，再去读后面的章节，可以发现它们都只是“在同一条主线上加分支/加边界”。

## 排障分流（快启版：先别猜，先分类）

若只有 10 分钟排一个 IoC 相关问题，建议读者按这个顺序收敛：

1) **定义层**：BeanDefinition 是否注册？（优先用 `DefaultListableBeanFactory#registerBeanDefinition` 断点证明一次）
2) **创建层**：实例是否创建？（看是否命中 `doCreateBean/populateBean/initializeBean`）
3) **注入/代理层**：是不是错过了 BPP 链？（对照 `registerBeanPostProcessors` 与目标 bean 创建时机）

进一步入口：`appendix/94-production-troubleshooting-checklist.md` / `appendix/98-debugger-pack.md`

## 面试常问（快启版：至少应能够复述 3 句）

1) refresh 主线分几段？每段的代表性方法是什么？
2) BFPP/BDRPP vs BPP 的本质差异是什么？（定义层 vs 实例层）
3) 如何用一个断点证明“这个行为确实发生了”（例如代理替换/候选收敛/early reference）？

推荐复习入口：`appendix/93-interview-playbook.md`

## 自检要点
应能够做到：

1) 30 分钟内完成验证本章推荐的 1–3 个 Lab，并知道每个 Lab 证明了什么结论。
2) 能把 refresh 粗粒度分成“定义层/注册 BPP/创建单例”三段，并说出各自的断点入口。
3) 知道下一步该按“现象驱动”去哪一章（可从 `appendix/92-knowledge-map.md` 开始）。

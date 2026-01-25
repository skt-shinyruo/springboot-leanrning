# 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：30 分钟快速闭环：先快后深（3 个最小实验入口）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点](011-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](013-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章目标：给你一条**30 分钟可跑通、可下断点、可形成正反馈**的快启路线。
- 原则：每个实验都满足“命令可运行 + 你应该看到什么 + 断点入口 + 最小 watch list + 下一步去哪读”。

!!! summary "本章要点"

    你应该能用“现象 → 断点 → 证据”回答三个问题：

    1) **为什么注入的是它？**（候选怎么收集、怎么收敛）
    2) **为什么 prototype 注入进 singleton 会‘像单例’？**（以及怎么修）
    3) **为什么你拿到的是 proxy？**（换壳发生在哪一段、是谁换的）

## 快启路线（按顺序跑）

> 建议：先只跑单个测试方法（噪音最少），确认能复现后再跑整类测试。

### 实验 1：单依赖注入如何从多个候选里“收敛”到一个（@Qualifier）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans test`

**你应该看到什么（证据）：**

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

**你应该看到什么（证据）：**

- `directPrototypeConsumer.currentId()` 两次返回 **相同** id（看起来像单例）
- `providerPrototypeConsumer.newId()` 两次返回 **不同** id（每次获取新 prototype）

**推荐断点（闭环版）：**

1) `DefaultListableBeanFactory#doResolveDependency`：解析注入点（确认 prototype 依赖被怎样注入进 singleton）
2) `AbstractBeanFactory#doGetBean`：每次取 bean 的总入口（对照 direct vs provider 的调用路径差异）
3) `DefaultListableBeanFactory#getBeanProvider`（可选）：理解 provider 的“延迟获取”语义

**固定观察点（watch list）：**

- `beanName` / `requiredType`（你到底在取哪个 bean）
- `isSingletonCurrentlyInCreation(beanName)`（理解创建阶段与缓存命中）
- `singletonObjects`（对照：prototype 不会像 singleton 一样被缓存）

**下一步去哪读（补知识点）：**

- [04. scope 与 prototype：prototype 注入陷阱与三种解法](../part-01-ioc-container/015-04-scope-and-prototype.md)

---

### 实验 3：为什么注入的是 proxy？（BPP 何时把对象换掉）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement test`

**你应该看到什么（证据）：**

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

## 小结与下一章

- 你已经拿到了“最小闭环”：**从测试方法进、用固定断点收敛噪音、用 watch list 拿到证据**
- 下一步建议：按主线继续读 Part 01（01→09），并把每章的“复现入口”跑一遍再读源码锚点

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「30 分钟快速闭环：先快后深（3 个最小实验入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「30 分钟快速闭环：先快后深（3 个最小实验入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「30 分钟快速闭环：先快后深（3 个最小实验入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansLabTest` / `SpringCoreBeansBeanCreationTraceLabTest`

上一章：[主线叙事：从 `refresh()` 到 `doCreateBean()`（源码级）](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[依赖解析与注入](../part-01-ioc-container/014-03-dependency-injection-resolution.md)

<!-- BOOKIFY:END -->

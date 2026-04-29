# 30 分钟快速闭环：先快后深（3 个最小实验入口）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；真实项目里常见路径是：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。

    观察对象：30 分钟快速闭环：先快后深（3 个最小实验入口）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：先跑出事实，再补机制

这页不要求读者先理解完整容器源码。更稳定的顺序是先跑三个小实验，把候选收敛、scope 边界和 proxy 替换都变成可观察事实；再回到正文和断点地图补机制。

读完本页后，至少应能回答三个问题：哪个测试能复现现象，第一断点应该下在哪里，观察到哪些变量后可以说明结论成立。

## 第一次闭环：从三个实验进入 `refresh()` 主线

本页给出一条 30 分钟内能跑通、能打断点、能形成证据链的入门路线。

不需要先记住所有概念。先把 3 个高频现象跑成事实，再回头读对应章节，读者就不会在名词与调用栈里迷路。

从“实验 1（方法级）”开始跑，断点噪音最少；确认能复现后，再继续顺序推进。每个实验只回答一个问题：命令是否可运行、断点应该看什么、这个现象能说明哪条容器规则。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


## 章节验收口径（10/30/3：快启闭环）

本章的目标不是“学全”，而是让阅读者用最短时间完成第一次可复现闭环：

- **10 分钟（知道要看什么）**：能说清楚本章 3 个实验分别在验证哪一层问题（候选收敛 / scope 边界 / 代理替换）。
- **30 分钟（能完成验证并证明）**：至少完成验证 1 个实验，并能用断点/观察点解释“为什么结果是这样”（不要只看输出）。
- **3 个抓手（能迁移到真实问题）**：每次遇到 Bean 问题先问清楚三件事：
  1. 定义层：有没有注册到 `BeanDefinition`？
  2. 实例层：对象是在哪里创建/被替换的？
  3. 注入层：候选如何收敛到最终注入对象？

## 30 分钟内要抓住的最小抓手（5 个对象 + 4 条入口）

不追求背细节，只追求“能在断点里观察到”的 5 个对象/入口：

1. **BeanDefinition（定义层）**
   - 入口方法：`DefaultListableBeanFactory#registerBeanDefinition`
   - 需要看到的变化：定义进 registry，`beanDefinitionMap` 里出现条目
2. **DependencyDescriptor（依赖解析层）**
   - 入口方法：`DefaultListableBeanFactory#doResolveDependency`
   - 需要看到的变化：候选集合被收集并收敛为唯一候选
3. **BeanWrapper（属性填充层）**
   - 入口方法：`AbstractAutowireCapableBeanFactory#populateBean`
   - 需要看到的变化：`PropertyValues` 转换并写入目标对象
4. **BeanPostProcessor（实例增强层）**
   - 入口方法：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
   - 需要看到的变化：`bean` → `result` 的第一次替换（proxy/包装）
5. **Singleton 缓存（生命周期结果层）**
   - 入口方法：`DefaultSingletonBeanRegistry#getSingleton`
   - 需要看到的变化：是否命中 `singletonObjects`，以及 early reference 的介入

## 快启路线（按顺序运行）

> 路径：先仅运行单个测试方法（噪音最少），确认能复现后再运行整类测试。

### 实验 1：单依赖注入如何从多个候选里“收敛”到一个（@Qualifier）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans test`

**应当看到什么（证据）：**

- 断言通过：`FormattingService.format("Hello") == "HELLO"`
- 意味着：容器面对多个 `TextFormatter` 候选时，最终注入点命中了 `@Qualifier` 指定的那个实现

**断点入口（闭环版）：**

1. `DefaultListableBeanFactory#doResolveDependency`：依赖解析总入口（看 `descriptor.getDependencyType()`）
2. `DefaultListableBeanFactory#findAutowireCandidates`：候选收集（看 `matchingBeans` 的 key：beanName）
3. `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`：Qualifier 过滤/匹配（看为什么其它候选被排除）
4. `DefaultListableBeanFactory#determineAutowireCandidate`：候选收敛总入口（最终 winner 在这里确定）

**固定观察点（观察清单）：**

- `descriptor`（注入点抽象：需要什么类型/是否 required）
- `matchingBeans`（候选集合：Map<beanName, candidate>）
- `autowiredBeanName` / `candidateName`（最终命中者）

**下一步去哪读（补知识点）：**

- [依赖注入解析：候选收集→收敛→最终注入](ioc-dependency-injection-resolution.md)
- [候选选择与优先级：@Primary/@Priority/@Order 的边界](wiring-autowire-candidate-selection-primary-priority-order.md)

---

### 实验 2：prototype 注入 singleton 的“反预期”（以及 ObjectProvider 如何修）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#demonstratesPrototypeScopeBehavior test`

**应当看到什么（证据）：**

- `directPrototypeConsumer.currentId()` 两次返回 **相同** id（表面上像单例）
- `providerPrototypeConsumer.newId()` 两次返回 **不同** id（每次获取新 prototype）

**断点入口（闭环版）：**

1. `DefaultListableBeanFactory#doResolveDependency`：解析注入点（确认 prototype 依赖被怎样注入进 singleton）
2. `AbstractBeanFactory#doGetBean`：每次取 bean 的总入口（对照 direct vs provider 的调用路径差异）
3. `DefaultListableBeanFactory#getBeanProvider`（可选）：理解 provider 的“延迟获取”语义

**固定观察点（观察清单）：**

- `beanName` / `requiredType`（读者到底在取哪个 bean）
- `isSingletonCurrentlyInCreation(beanName)`（理解创建阶段与缓存命中）
- `singletonObjects`（对照：prototype 不会像 singleton 一样被缓存）

**下一步去哪读（补知识点）：**

- [scope 与 prototype：prototype 注入陷阱与三种解法](ioc-scope-and-prototype.md)

---

### 实验 3：为什么注入的是 proxy？（BPP 何时把对象换掉）

**运行入口（方法级）：**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement test`

**应当看到什么（证据）：**

- 输出中出现 `OBSERVE:` 提示（bean 创建阶段记录）
- 关键证据：某个 bean 在初始化链路中 **从原对象变成了 result（proxy/wrapper）**

**断点入口（闭环版）：**

1. `AbstractAutowireCapableBeanFactory#initializeBean`：进入最终暴露对象产生链路
2. `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：在循环里观察 `bean` → `result` 的第一次替换
3. （可选）`BeanPostProcessor#postProcessAfterInitialization`：在具体 BPP 上锁定“是谁换的”

**固定观察点（观察清单）：**

- `beanName`（条件断点：只看目标 bean）
- `bean`（原对象） vs `result`（最终对象）：`result != bean` 即替换发生
- `beanFactory.getBeanPostProcessors()`（BPP 链与顺序：解释“为什么是它换的/为什么先后顺序这样”）

**下一步去哪读（补知识点）：**

- [BFPP vs BPP：定义层改配方 vs 实例层改对象](ioc-post-processors.md)
- [代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy](wiring-proxying-phase-bpp-wraps-bean.md)
- [调试与自检：从异常到断点入口](boot-debugging-and-observability.md)

## 新手易卡点与修复路径（快启版）

- **断点不命中**
  - 常见原因：bean 从未创建（`@Lazy`/未触发 `getBean`/未执行到 `preInstantiateSingletons`）
  - 修复路径：改成非 lazy 或显式 `getBean`；在 `doGetBean`/`preInstantiateSingletons` 观察是否进入
- **运行耗时过长/输出噪声过多**
  - 常见原因：运行了全量 `@SpringBootTest` 或扫描了整个 classpath
  - 修复路径：用 `AnnotationConfigApplicationContext` 只注册最小配置类；仅运行方法级测试（`-Dtest=Class#method`）
- **调试卡死或断点命中频率过高**
  - 常见原因：断点落在高频循环（如 `isTypeMatch`/`doGetBean`）
  - 修复路径：加条件断点（`beanName` 过滤）+ 固定观察清单（只看 3–5 个关键变量）

## 三个实验各自证明什么

不要把三个实验当成“跑通即可”。跑完后至少要能把下面三条证据说清楚：

1. **候选收敛**：在 `DefaultListableBeanFactory#doResolveDependency` 里看到 `matchingBeans` 如何被 `@Qualifier` 缩小，最终命中目标 formatter。
2. **scope 边界**：在 `AbstractBeanFactory#doGetBean` 里对比 direct 注入与 `ObjectProvider` 获取路径，确认 prototype 只有“向容器重新获取”时才会新建。
3. **代理替换**：在 `applyBeanPostProcessorsAfterInitialization` 里观察 `bean` 与 `result` 是否不同，确认最终暴露对象可能不是原始实例。

能说出这三条，后续章节的断点和术语才有落点。


!!! example "本章配套实验（先运行再读）"

    - Lab（30 分钟闭环入口）：
      - `SpringCoreBeansLabTest`
      - `SpringCoreBeansMainlineCallChainLabTest`
      - `SpringCoreBeansBreakpointPackLabTest`

## BreakpointPack 深入复盘（可选：把“快启”升级为“可排障”）

若运行了 `SpringCoreBeansBreakpointPackLabTest`，至少能复述以下 3 条“可断言结论”：

1. **循环依赖能不能救，取决于 early reference 介入时机**
   - 证据链：`DefaultSingletonBeanRegistry#addSingletonFactory` → `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
   - 对应 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
2. **BPP 顺序决定“先换壳还是先注入”，会导致 raw 注入**
   - 证据链：`PostProcessorRegistrationDelegate#registerBeanPostProcessors` → `applyBeanPostProcessorsAfterInitialization`
   - 对应 Lab：`SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
3. **FactoryBean 与占位符解析是两条“最容易被误解”的分支**
   - 证据链：`AbstractBeanFactory#getObjectForBeanInstance` / `FactoryBeanRegistrySupport#getObjectFromFactoryBean`
   - 对应 Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

30 分钟快启只做三件事：

1. 运行一个最小容器（能看到 BeanDefinition → bean 实例的转换）
2. 运行一次 refresh 主线（能把“处理器/注入/回调”放回时间线）
3. 下两三个关键断点（能在调试器里观察到关键数据结构变化）

当读者完成这三件事，再去读后面的章节，可以发现它们都只是“在同一条主线上加分支/加边界”。

## 排障分流（快启版：先别猜，先分类）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


若只有 10 分钟排一个 IoC 相关问题，按这个顺序收敛：

1. **定义层**：BeanDefinition 是否注册？（优先用 `DefaultListableBeanFactory#registerBeanDefinition` 断点证明一次）
2. **创建层**：实例是否创建？（看是否命中 `doCreateBean/populateBean/initializeBean`）
3. **注入/代理层**：是不是错过了 BPP 链？（对照 `registerBeanPostProcessors` 与目标 bean 创建时机）

进一步入口：`appendix-production-troubleshooting-checklist.md` / `appendix-debugger-pack.md`

## 面试常问（快启版：至少需要复述 3 句）

1. refresh 主线分几段？每段的代表性方法是什么？
2. BFPP/BDRPP vs BPP 的本质差异是什么？（定义层 vs 实例层）
3. 如何用一个断点证明“这个行为确实发生了”（例如代理替换/候选收敛/early reference）？

复习入口：`appendix-interview-playbook.md`

## 验收口径：30 分钟后要能说清三件事
读完并跑完入口后，应能做到：

1. 30 分钟内完成验证本章列出的 1–3 个 Lab，并知道每个 Lab 证明了什么结论。
2. 能把 refresh 粗粒度分成“定义层/注册 BPP/创建单例”三段，并说出各自的断点入口。
3. 知道下一步该按“现象驱动”去哪一章（可从 `appendix-knowledge-map.md` 开始）。

## 小结：三个实验足够打开后续章节

完成本页后，读者已经有了最小闭环：从测试方法进入，用固定断点降低噪音，再用观察清单 得到可复述的证据。下一步沿 Part 01（01→09）继续读，每章先运行“复现入口”，再读源码锚点。

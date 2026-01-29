# 98. Debugger Pack（断点包总入口）

## 导读

- 本章主题：**Debugger Pack（断点包总入口）**
- 阅读方式建议：把本章当成“进入本模块的调试入口索引页”——先跑一条最小回归，再按本章的断点清单去看关键数据结构变化，最后回到对应章节补齐理论与边界。

!!! summary "本章要点"

    - Debugger Pack 的目标不是“讲知识”，而是给读者一套**可复用的断点入口**：遇到注入失败/循环依赖/代理不生效/占位符不对时，应该第一时间去哪下断点、看什么变量。
    - 一旦能够在调试器里看见 `refresh → doCreateBean → populateBean → initializeBean` 的主线与关键分支，后续任何章节都会变得“可验证、可复述”。
    - 读者 B/C 建议：每次读完一章，至少用本章的断点清单跑一次对应 Lab，把概念变成证据链。

!!! example "本章配套实验（先跑再读）"

    - Lab（总入口/快速回归）：
      - `SpringCoreBeansBreakpointPackLabTest`
      - `SpringCoreBeansMainlineCallChainLabTest`
    - Lab（关键分支矩阵入口）：
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansBreakpointPackLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansMainlineCallChainLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java`

## 机制主线：把“我感觉它应该这样”变成“我看到它就是这样”

Spring IoC 的难点从来不是 API，而是：

- 读者不知道某个“现象”发生在 refresh 的哪个阶段
- 读者不知道某个“增强/代理/注入”到底由哪个处理器做的
- 读者不知道“异常信息”应该回溯到哪个关键分支

Debugger Pack 的做法是：把常见问题压缩成 **断点入口 + 观察点 + 对应 Lab**，让读者最快收敛到证据链。

---

## 1. 使用方式（3 步闭环）

1) **先跑一个可复现入口**（优先跑本章推荐 Lab，而不是直接在业务项目里迷路）
2) **按本章断点清单下断点**（必要时用条件断点过滤 beanName）
3) **只盯 watch list**（避免在大型栈里被噪声淹没）

推荐命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test
```

## 1.2 教程化验收（10/30/3）：把 Debugger Pack 当“能力训练器”

可以用 Debugger Pack 给自己做一个非常明确的训练闭环（适用于源码进阶/团队内训/面试）：

1) **10 分钟（可跑）**：跑通 `SpringCoreBeansBreakpointPackLabTest`，确保环境与入口 OK。
2) **30 分钟（可看见）**：只用本章 2.x 的断点 + 本章 3 的 watch list，把主线数据结构变化看见。
3) **3 分钟（可复述）**：把观察到的现象复述成“结论 → 证据链（方法名）→ 反例/误区”，对标：`appendix/93-interview-playbook.md`。

## 1.1 团队内训如何用（可选）

若正在做团队分享/内训，不建议“从目录按章讲完”。更高效的方式是：

1) 先用本章断点包把主线跑通（建立共同的观察点与语言）
2) 再按课时选择讲解深度（60/90/120 分钟脚本 + 互动题/作业）

内训讲义入口：[`99-team-training-kit.md`](99-team-training-kit.md)

## 1.3 面试怎么用（建议读者形成固定话术）

- 题库入口：`appendix/93-interview-playbook.md`（每题都绑定“关键方法 + 观察点 + 对应 Lab”）
- 排障入口：`appendix/94-production-troubleshooting-checklist.md`（Symptoms → Repro → Evidence → Decision → Fix → Verify）
- 相应的目标不是背“名词”，而是能在面试里说清楚：**发生在哪个阶段（refresh 哪一段）/ 证据链怎么证明 / 典型误区是什么**。

---

## 2. 最常用断点入口（按“主线 → 分支 → 现象”组织）

### 2.1 refresh 主线（把一切放回时间线）

- `AbstractApplicationContext#refresh`：容器生命周期总入口
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BFPP/BDRPP 的入口（定义层）
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：BPP 注册入口（实例层）
- `AbstractApplicationContext#finishBeanFactoryInitialization`：预实例化与单例创建入口

对应章节：`part-03-container-internals/18-refresh-to-bean-creation-mainline.md`

### 2.2 bean 创建主线（实例化/注入/初始化）

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`

对应章节：`part-01-ioc-container/016-05-lifecycle-and-callbacks.md`

### 2.3 依赖解析（候选收集 → 候选收敛 → 注入）

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`
- `DefaultListableBeanFactory#determineAutowireCandidate`

对应章节：`part-01-ioc-container/014-03-dependency-injection-resolution.md`、`part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`

### 2.4 代理/包装发生在哪里（BPP 链）

- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

对应章节：`part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`

### 2.5 循环依赖与 early reference（三级缓存）

- `DefaultSingletonBeanRegistry#getSingleton`
- `DefaultSingletonBeanRegistry#addSingletonFactory`
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

对应章节：
- `part-01-ioc-container/09-circular-dependencies.md`
- `part-03-container-internals/16-early-reference-and-circular.md`

### 2.6 占位符解析 / SpEL / 类型转换（值注入三连）

- `AbstractBeanFactory#resolveEmbeddedValue`（`${...}`/`#{...}` 的入口）
- `TypeConverterDelegate#convertIfNecessary`（字符串 → 目标类型的决策点）

对应章节：
- `part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- `part-05-aot-and-real-world/44-spel-and-value-expression.md`
- `part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`

---

## 3. Watch List（最小够用版）

无需一次看 100 个变量，最小 watch list 够用即可：

- `beanName`：当前处理的 bean（建议优先用条件断点过滤）
- `mbd` / `mergedBeanDefinition`：合并后的定义（见 35 章）
- `exposedObject`：最终暴露对象（判断是否被代理/替换）
- `singletonObjects / earlySingletonObjects / singletonFactories`：循环依赖/提前暴露相关
- `beanFactory.getBeanPostProcessors()`：BPP 链顺序（判断“谁包谁/谁先谁后”）

## 方法级调用链卡片（把断点观察变成可复述答案）

Debugger Pack 的目的不是“列断点”，而是帮读者形成一种稳定输出：

1) **阶段**：我先把问题放回 refresh 主线的哪一段？
2) **调用链**：我用哪 2–4 个方法名把链路串起来？（入口 → 分支 → 落点）
3) **证据**：我在断点里看哪 3 个变量/集合证明结论？
4) **修复**：我改的是“定义层（BeanDefinition）”还是“实例层（对象/代理）”，如何验证？

如果应能够在 3 分钟内按这个卡片说完一个问题，读者就具备“源码进阶/面试/排障”三合一的能力闭环。

---

## 常见误区（Debugger Pack 的使用误区）

1) **只看异常，不看阶段**：同一个异常在不同阶段含义不同，必须先定位到 refresh 的哪一步。
2) **下了断点但没有过滤**：不加 beanName 条件断点，大项目里可以被噪声淹没。
3) **把“看见”当成“理解”**：断点只能提供证据链，真正的边界/代价要回到对应章节阅读。

---

## 自检要点
应能够用 2 句复述：

1) 我遇到注入失败/代理不生效/循环依赖时，第一断点下在哪（各给 1 个方法名）。
2) 我在断点里只看哪 3 个变量/结构，就能判断自己处在 refresh/创建/注入的哪一步。
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：为每个用例/断点包补“它在证明什么机制分支”，让工具页更可复用。
    - B（边界反例）：“反例与踩坑点”：如何避免用例/断点被版本差异误导。
    - C（排障 SOP）：把工具页与排障清单/知识地图/目录页打通，形成统一导航。
    - D（断点观察）： watch list 与判定标准：断点停下后看什么值才算“证据成立”。
    - E（面试复述）：将工具页变成训练脚本：面试复述/团队内训可直接引用其证据链与复现入口。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99. 团队内训讲义（Training Kit）](99-team-training-kit.md)

<!-- BOOKIFY:END -->

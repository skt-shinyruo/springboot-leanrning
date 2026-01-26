# 98. Debugger Pack（断点包总入口）

## 导读

- 本章主题：**Debugger Pack（断点包总入口）**
- 阅读方式建议：把本章当成“进入本模块的调试入口索引页”——先跑一条最小回归，再按本章的断点清单去看关键数据结构变化，最后回到对应章节补齐理论与边界。

!!! summary "本章要点"

    - Debugger Pack 的目标不是“讲知识”，而是给你一套**可复用的断点入口**：你遇到注入失败/循环依赖/代理不生效/占位符不对时，应该第一时间去哪下断点、看什么变量。
    - 一旦你能在调试器里看见 `refresh → doCreateBean → populateBean → initializeBean` 的主线与关键分支，后续任何章节都会变得“可验证、可复述”。
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

- 你不知道某个“现象”发生在 refresh 的哪个阶段
- 你不知道某个“增强/代理/注入”到底由哪个处理器做的
- 你不知道“异常信息”应该回溯到哪个关键分支

Debugger Pack 的做法是：把常见问题压缩成 **断点入口 + 观察点 + 对应 Lab**，让你最快收敛到证据链。

---

## 1. 使用方式（3 步闭环）

1) **先跑一个可复现入口**（优先跑本章推荐 Lab，而不是直接在业务项目里迷路）  
2) **按本章断点清单下断点**（必要时用条件断点过滤 beanName）  
3) **只盯 watch list**（避免在大型栈里被噪声淹没）

推荐命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test
```

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

你不需要一次看 100 个变量，最小 watch list 够用即可：

- `beanName`：当前处理的 bean（建议优先用条件断点过滤）
- `mbd` / `mergedBeanDefinition`：合并后的定义（见 35 章）
- `exposedObject`：最终暴露对象（判断是否被代理/替换）
- `singletonObjects / earlySingletonObjects / singletonFactories`：循环依赖/提前暴露相关
- `beanFactory.getBeanPostProcessors()`：BPP 链顺序（判断“谁包谁/谁先谁后”）

---

## 常见坑（Debugger Pack 的使用误区）

1) **只看异常，不看阶段**：同一个异常在不同阶段含义不同，必须先定位到 refresh 的哪一步。
2) **下了断点但没有过滤**：不加 beanName 条件断点，大项目里你会被噪声淹没。
3) **把“看见”当成“理解”**：断点只能提供证据链，真正的边界/代价要回到对应章节阅读。

---

## 一句话自检

你应该能用 2 句复述：

1) 我遇到注入失败/代理不生效/循环依赖时，第一断点下在哪（各给 1 个方法名）。  
2) 我在断点里只看哪 3 个变量/结构，就能判断自己处在 refresh/创建/注入的哪一步。

<!-- BOOKIFY:START -->

上一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99. 自测题（Self Check）](026-99-self-check.md)

<!-- BOOKIFY:END -->

# 09. Debugger Pack（断点包总入口）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：建议先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    本章围绕Debugger Pack（断点包总入口）展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBreakpointPackLabTest`。需要下探源码时，可以从 `AbstractApplicationContext#refresh` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` / `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[08. Explore/Debug 用例（可选启用，不影响默认回归）](08-explore-debug-tests.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. 团队内训讲义（Training Kit）：可直接用于授课的课时脚本](10-team-training-kit.md)
<!-- GLOBAL-BOOK-NAV:END -->


## 导读

- 阅读方式建议：把本章当成“进入本模块的调试入口索引页”——先运行一条最小回归，再按本章的断点清单去看关键数据结构变化，最后回到对应章节补齐理论与边界。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

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

## 机制主线：将“主观判断”转化为“可观察事实”

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

Spring IoC 的难点从来不是 API，而是：

- 读者不知道某个“现象”发生在 refresh 的哪个阶段
- 读者不知道某个“增强/代理/注入”到底由哪个处理器做的
- 读者不知道“异常信息”应该回溯到哪个关键分支

Debugger Pack 的做法是：把常见问题压缩成 **断点入口 + 观察点 + 对应 Lab**，让读者最快收敛到证据链。

---

## 1. 使用方式（3 步闭环）

1) **运行一个可复现入口**（优先运行本章推荐 Lab，以避免在业务项目中丢失主线）
2) **按本章断点清单设置断点**（必要时用条件断点过滤 beanName）
3) **只盯 watch list**（避免在大型栈里被噪声淹没）

推荐命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test
```

## 1.2 教程化验收（10/30/3）：把 Debugger Pack 当“能力训练器”

可以用 Debugger Pack 给自己做一个非常明确的训练闭环（适用于源码进阶/团队内训/面试）：

1) **10 分钟（可运行）**：完成验证 `SpringCoreBeansBreakpointPackLabTest`，确保环境与入口 OK。
2) **30 分钟（可观察到）**：只用本章 2.x 的断点 + 本章 3 的 watch list，把主线数据结构变化观察到。
3) **3 分钟（可复述）**：把观察到的现象复述成“结论 → 证据链（方法名）→ 反例/误区”，对标：`appendix/04-interview-playbook.md`。

## 1.1 团队内训如何用（可选）

若正在做团队分享/内训，不建议“从目录按章讲完”。更高效的方式是：

1) 先用本章断点包把主线完成验证（建立共同的观察点与语言）
2) 再按课时选择讲解深度（60/90/120 分钟脚本 + 互动题/作业）

内训讲义入口：[`10-team-training-kit.md`](10-team-training-kit.md)

## 1.3 面试使用方式（建议读者形成固定话术）

- 题库入口：`appendix/04-interview-playbook.md`（每题都绑定“关键方法 + 观察点 + 对应 Lab”）
- 排障入口：`appendix/05-production-troubleshooting-checklist.md`（Symptoms → Repro → Evidence → Decision → Fix → Verify）
- 相应的目标不是背“名词”，而是能在面试里说清楚：**发生在哪个阶段（refresh 哪一段）/ 证据链怎么证明 / 典型误区是什么**。

---

## 2. 最常用断点入口（按“主线 → 分支 → 现象”组织）

### 2.1 refresh 主线（把一切放回时间线）

- `AbstractApplicationContext#refresh`：容器生命周期总入口
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BFPP/BDRPP 的入口（定义层）
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：BPP 注册入口（实例层）
- `AbstractApplicationContext#finishBeanFactoryInitialization`：预实例化与单例创建入口

对应章节：`part-03-container-internals/07-refresh-to-bean-creation-mainline.md`

### 2.2 bean 创建主线（实例化/注入/初始化）

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`

对应章节：`part-01-ioc-container/04-lifecycle-and-callbacks.md`

### 2.3 依赖解析（候选收集 → 候选收敛 → 注入）

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`
- `DefaultListableBeanFactory#determineAutowireCandidate`

对应章节：`part-01-ioc-container/02-dependency-injection-resolution.md`、`part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`

### 2.4 代理/包装发生在哪里（BPP 链）

- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

对应章节：`part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`

### 2.5 循环依赖与 early reference（三级缓存）

- `DefaultSingletonBeanRegistry#getSingleton`
- `DefaultSingletonBeanRegistry#addSingletonFactory`
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

对应章节：
- `part-01-ioc-container/08-circular-dependencies.md`
- `part-03-container-internals/05-early-reference-and-circular.md`

### 2.6 占位符解析 / SpEL / 类型转换（值注入三连）

- `AbstractBeanFactory#resolveEmbeddedValue`（`${...}`/`#{...}` 的入口）
- `TypeConverterDelegate#convertIfNecessary`（字符串 → 目标类型的决策点）

对应章节：
- `part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`
- `part-05-aot-and-real-world/05-spel-and-value-expression.md`
- `part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`

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

1) **阶段**：应先将问题放回 refresh 主线的哪一段？
2) **调用链**：用哪 2–4 个方法名将链路串起来？（入口 → 分支 → 落点）
3) **证据**：在断点中观察哪 3 个变量/集合以证明结论？
4) **修复**：修改的是“定义层（BeanDefinition）”还是“实例层（对象/代理）”，如何验证？

如果应能够在 3 分钟内按这个卡片说完一个问题，读者就具备“源码进阶/面试/排障”三合一的能力闭环。

---

## 常见误区（Debugger Pack 的使用误区）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


1) **只看异常，不看阶段**：同一个异常在不同阶段含义不同，必须先定位到 refresh 的哪一步。
2) **下了断点但没有过滤**：不加 beanName 条件断点，大项目里可以被噪声淹没。
3) **把“观察到”当成“理解”**：断点只能提供证据链，真正的边界/代价要回到对应章节阅读。

---

## 自检要点
应能够用 2 句复述：

1) 遇到注入失败/代理不生效/循环依赖时，第一断点应设置在哪（各给 1 个方法名）。
2) 在断点中只观察哪 3 个变量/结构，就能判断当前处在 refresh/创建/注入的哪一步。
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansBreakpointPackLabTest`，再用 `SpringCoreBeansMainlineCallChainLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：将断点包写成“路线”：每条路线明确起点（测试方法）→ 关键断点 → 需要确认的变量/状态，读者可以按路线复刻结论。
    - 下一跳：若是从现象进入，优先回到 [知识地图](03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

## 小结与下一章

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

下一章见：[99. 团队内训讲义（Training Kit）](10-team-training-kit.md)


<!-- BOOKIFY:START -->

上一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](08-explore-debug-tests.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99. 团队内训讲义（Training Kit）](10-team-training-kit.md)

<!-- BOOKIFY:END -->

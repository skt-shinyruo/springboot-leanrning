# 第 11 章：关键分支矩阵（Branch Decision Matrix）

## 导读

- 本章主题：**关键分支矩阵（Branch Decision Matrix）**
- 阅读方式建议：先跑 Branch Matrix 的聚合入口测试（它把关键分支固化成断言），再用本章表格把“现象 → 阶段 → 关键方法 → 必看变量”串成一条排障调用链套路。

!!! summary "本章要点"

    - 这章是“排障索引页”：遇到一个现象时，不要先全局搜代码，而是先把它定位到某个分支点（if/then）。
    - 分支矩阵的价值在于“可复现”：每个分支都应该能在本仓库的 LabTest 里跑出来，而不是靠脑补。
    - 学会用最少观察点做最大判断：一个关键方法 + 3 个变量，往往足够把问题收敛到根因。
    - 先学会“读异常 cause chain”：很多错误的外层异常是 `BeanCreationException`/`UnsatisfiedDependencyException`，真正的分支点往往藏在 root cause。

!!! example "本章配套实验（先跑再读）"

    - Lab（关键分支矩阵入口）：
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java`

## 机制主线：把“排障经验”压缩成决策表

排障最怕两件事：

1) 现象很像，但根因不在同一阶段
2) 读者下了很多断点，却没有一套“判断规则”

Branch Decision Matrix 的目的就是把“判断规则”显式写出来：读者只要回答几个 if/then，就能定位到该下断点的位置。

---

## 0. 先学会读异常 cause chain（别被外层异常骗了）

Spring 容器的“外层异常”非常容易误导读者，因为它们经常只是包装（wrap）：

- `BeanCreationException`：只是说“创建 bean 失败”，但失败点可能在 instantiate / populate / initialize / BPP / destroy 的任何一步。
- `UnsatisfiedDependencyException`：只是说“依赖没满足”，但 root cause 通常是：
  - `NoSuchBeanDefinitionException`（没找到候选）
  - `NoUniqueBeanDefinitionException`（候选太多没收敛）
  - `BeanCurrentlyInCreationException`（循环依赖/创建窗口期）
  - 类型转换/值解析异常（populateBean 阶段）

因此读者排障的固定第一步是：

1) 先打开异常的 **root cause**（最底层 `cause`）
2) 再把 root cause 映射到“阶段 + 关键方法 + watch list”
3) 最后再选 Lab 复现（用断言把分支固化）

> 可以发现：很多“看起来像 DI 的问题”，其实是 `@Value`/类型转换/FactoryBean 类型推断导致的。

## 1. 分支矩阵（现象 → 阶段 → 方法 → 观察点 → Lab）

> 提示：这张表不是为了“覆盖所有情况”，而是覆盖最常见、最能决定走向的分支点。

| 现象（Symptoms） | 分流问题（Decision） | 阶段（Phase） | 关键方法（Entry） | 必看变量（Watch List） | 对应 Lab |
| --- | --- | --- | --- | --- | --- |
| `NoSuchBeanDefinitionException` | 是真的没注册？还是没命中候选？ | 依赖解析 | `DefaultListableBeanFactory#doResolveDependency` | `descriptor/requiredType`、`matchingBeans.keySet()` | `SpringCoreBeansContainerLabTest` / 候选相关 Lab |
| `NoUniqueBeanDefinitionException` | 多候选如何收敛？有没有 @Primary/@Qualifier/by-name fallback？ | 依赖解析 | `determineAutowireCandidate` | `candidates`、`primaryCandidate`、`dependencyName` | `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| “`@Resource` 字段为 null” | 容器是否装了 JSR-250 处理器？ | 注解处理（实例层） | `CommonAnnotationBeanPostProcessor#postProcessProperties` | 容器里是否存在该 BPP、`resourceName` | `SpringCoreBeansResourceInjectionLabTest` |
| `@Value(\"${missing}\")` 未失败，值变成原样字符串 | resolver 是否 non-strict？ | 值解析 | `AbstractBeanFactory#resolveEmbeddedValue` | 输入/输出是否仍含 `${` | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| 类型转换失败（TypeMismatch 等） | 解析后字符串是什么？转换走 ConversionService 还是 PropertyEditor？ | 注入/属性填充 | `TypeConverterDelegate#convertIfNecessary` | `requiredType`、`conversionService`、`customEditor` | `SpringCoreBeansTypeConversionLabTest` |
| 循环依赖：constructor 失败、setter 有时能救 | 是否存在 early exposure 窗口？ | 单例创建 | `DefaultSingletonBeanRegistry#getSingleton` | 三层缓存 key/size、`allowEarlyReference` | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| AOP/代理不生效 | BPP 链是否完整？bean 是否创建过早错过 BPP？ | BPP/创建时机 | `registerBeanPostProcessors` / `applyBeanPostProcessorsAfterInitialization` | `beanFactory.getBeanPostProcessors()`、目标 bean 创建时机 | `SpringCoreBeansRegistryPostProcessorLabTest` |
| “代理形态不一致 / raw vs wrapped” | early 与 final 是否一致？ | 循环依赖/代理 | `getEarlyBeanReference` / `doCreateBean` 尾部检查 | `earlySingletonReference`、`exposedObject`、dependentBeans | `SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| `UnsatisfiedDependencyException`（外层） | root cause 是“无候选”还是“多候选”？还是“值解析/类型转换”？ | 注入/属性填充 | `DefaultListableBeanFactory#doResolveDependency` / `AbstractAutowireCapableBeanFactory#populateBean` | root cause 类型、`descriptor`、`pvs`、`PropertyValue` | `SpringCoreBeansContainerLabTest` / `SpringCoreBeansInjectionPhaseLabTest` |
| `BeanCreationException`（外层） | 失败发生在 instantiate / populate / initialize 哪一步？ | 创建主线 | `AbstractAutowireCapableBeanFactory#doCreateBean` | `beanName`、`mbd`、`BeanWrapper`、`exposedObject` | `SpringCoreBeansBeanCreationTraceLabTest` |
| `BeanCurrentlyInCreationException` | 是 constructor cycle？还是“早期引用介入的窗口期”？ | 单例创建 / 循环依赖 | `DefaultSingletonBeanRegistry#beforeSingletonCreation` / `getSingleton` | `singletonsCurrentlyInCreation`、`allowCircularReferences` | `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest` |
| `Circular depends-on relationship` | 是定义层 dependsOn 拓扑环，不要误判成三级缓存循环依赖 | 创建入口（dependsOn） | `AbstractBeanFactory#doGetBean` / `DefaultSingletonBeanRegistry#isDependent` | `mbd.getDependsOn()`、`dependentBeanMap` | `SpringCoreBeansDependsOnLabTest` |
| `BeanDefinitionOverrideException` / “Cannot register bean definition … already exists” | 是否禁止覆盖？覆盖发生在谁注册得更晚？ | 定义层注册 | `DefaultListableBeanFactory#registerBeanDefinition` | `allowBeanDefinitionOverriding`、旧/新 BD source | overriding 相关 Lab / 注册相关 Lab |
| `BeanNotOfRequiredTypeException` | 需要的是 factory 还是 product？或者被代理后类型发生变化？ | 查找/注入 | `AbstractBeanFactory#getObjectForBeanInstance` / `AbstractBeanFactory#isTypeMatch` | beanName 是否含 `&`、`predictedType`、`targetType` | FactoryBean / proxy 相关 Lab |

---

## 2. 如何使用这张表（固定套路）

当读者拿到一个异常/现象时，按这个顺序：

1) **先找现象行**：它属于注入/占位符/代理/循环依赖哪一类？
2) **回答分流问题**：把“可能原因”缩成 1–2 个分支
3) **直接跳到关键方法下断点**：只看 watch list，不在栈里漫游
4) **用对应 Lab 复现**：确认读者理解的是机制，而不是项目偶然

---

## 面试怎么用这张表（把排障套路复用成答题套路）

面试里很多题其实是“给读者一个现象，让读者解释机制”。可以把答题过程复用为：

1) 先把现象放回阶段：definition / creation / after-init（对应 refresh 时间线）
2) 再用 1 个关键断点给证据（不靠“我觉得”）
3) 最后给出工程建议（如何避免/如何验证）

推荐复习入口：`appendix/93-interview-playbook.md`（每题都对应“阶段 + 关键方法 + 可跑 Lab”）。

## 自检要点
应能够做到：

1) 看到 `NoUniqueBeanDefinitionException`，能立刻说出“下哪个断点、看哪三个变量”。
2) 看到 `@Value(\"${missing}\")` 原样字符串，能立刻判断 strict/non-strict 并给出修复路径。
3) 看到“代理不生效”，能把问题分成“顺序问题 vs 时机问题”两类并给出证据链入口。
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：把每个分支明确到“真实 if/return 发生点”（入口方法 + 分支条件）。
    - B（边界反例）：为每个分支补 1 个反例：何时这条规则不适用/会被更强信号覆盖（如 @Primary 覆盖 @Priority）。
    - C（排障 SOP）：“分支误诊排障”：读者常把哪两个分支混淆？第一断点如何区分？
    - D（断点观察）：为分支矩阵提供“断点套件建议”：每类分支对应最小断点组。
    - E（面试复述）：“追问题”：让读者能解释“为什么是这个顺序，而不是另一个顺序”。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean](010-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 12 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->

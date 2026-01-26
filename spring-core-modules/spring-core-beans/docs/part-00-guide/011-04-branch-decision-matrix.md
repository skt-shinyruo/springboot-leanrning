# 第 11 章：关键分支矩阵（Branch Decision Matrix）

## 导读

- 本章主题：**关键分支矩阵（Branch Decision Matrix）**
- 阅读方式建议：先跑 Branch Matrix 的聚合入口测试（它把关键分支固化成断言），再用本章表格把“现象 → 阶段 → 关键方法 → 必看变量”串成一条排障套路。

!!! summary "本章要点"

    - 这章是“排障索引页”：你遇到一个现象时，不要先全局搜代码，而是先把它定位到某个分支点（if/then）。
    - 分支矩阵的价值在于“可复现”：每个分支都应该能在本仓库的 LabTest 里跑出来，而不是靠脑补。
    - 学会用最少观察点做最大判断：一个关键方法 + 3 个变量，往往足够把问题收敛到根因。

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
2) 你下了很多断点，却没有一套“判断规则”

Branch Decision Matrix 的目的就是把“判断规则”显式写出来：你只要回答几个 if/then，就能定位到该下断点的位置。

---

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

---

## 2. 如何使用这张表（固定套路）

当你拿到一个异常/现象时，按这个顺序：

1) **先找现象行**：它属于注入/占位符/代理/循环依赖哪一类？  
2) **回答分流问题**：把“可能原因”缩成 1–2 个分支  
3) **直接跳到关键方法下断点**：只看 watch list，不在栈里漫游  
4) **用对应 Lab 复现**：确认你理解的是机制，而不是项目偶然

---

## 一句话自检

你应该能做到：

1) 看到 `NoUniqueBeanDefinitionException`，能立刻说出“下哪个断点、看哪三个变量”。  
2) 看到 `@Value(\"${missing}\")` 原样字符串，能立刻判断 strict/non-strict 并给出修复路径。  
3) 看到“代理不生效”，能把问题分成“顺序问题 vs 时机问题”两类并给出证据链入口。

<!-- BOOKIFY:START -->

上一章：[第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean](010-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 12 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->

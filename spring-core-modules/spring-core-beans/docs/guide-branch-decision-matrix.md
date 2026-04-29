# 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，完成主线与断点闭环验证；再回到正文按“时间线/分支矩阵/证据链”定位机制窗口；最后用自检题将表达固化为可复述答案。

    观察对象：关键分支矩阵。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`。需要下探源码时，可以从 `DefaultListableBeanFactory#doResolveDependency` / `CommonAnnotationBeanPostProcessor#postProcessProperties` / `AbstractBeanFactory#resolveEmbeddedValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：先找 root cause，再选断点

这页是排障索引，不是概念章节。读者拿到异常后，先看最底层 root cause，再用矩阵选择阶段、入口方法和观察变量；最后用对应 Lab 把分支跑成断言。

如果只看外层 `BeanCreationException` 或 `UnsatisfiedDependencyException`，很容易把类型转换、值解析、候选选择或循环依赖混成同一个问题。

## 分支矩阵怎么读：把症状压成 if/then

本页的用法偏工具化：先运行 Branch Matrix 的聚合入口测试，把关键分支跑成断言；再用表格把异常/现象翻译成“阶段 → 第一断点 → 观察清单 → 对应 Lab”。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab（关键分支矩阵入口）：
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`
    - 测试文件：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java`

## 机制主线：把“排障经验”压缩成决策表

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

排障最怕两件事：

1. 现象很像，但根因不在同一阶段
2. 读者下了很多断点，却没有一套“判断规则”

关键分支矩阵的目的就是把“判断规则”显式写出来：只要回答几个 if/then，就能定位到该设置断点的位置。

---

## 先学会读异常 cause chain（别被外层异常骗了）

Spring 容器的“外层异常”容易误导读者，因为它们经常只是包装（wrap）：

- `BeanCreationException`：只是说“创建 bean 失败”，但失败点可能在 instantiate / populate / initialize / BPP / destroy 的任何一步。
- `UnsatisfiedDependencyException`：只是说“依赖没满足”，但 root cause 通常是：
  - `NoSuchBeanDefinitionException`（没找到候选）
  - `NoUniqueBeanDefinitionException`（候选太多没收敛）
  - `BeanCurrentlyInCreationException`（循环依赖/创建窗口期）
  - 类型转换/值解析异常（populateBean 阶段）

因此读者排障的固定第一步是：

1. 先打开异常的 **root cause**（最底层 `cause`）
2. 再把 root cause 映射到“阶段 + 关键方法 + 观察清单”
3. 最后再选 Lab 复现（用断言把分支固化）

> 可以发现：很多“表面上像 DI 的问题”，往往由 `@Value`/类型转换/FactoryBean 类型推断导致。

## 分支矩阵（现象 → 阶段 → 方法 → 观察点 → Lab）

> 提示：这张表不是为了“覆盖所有情况”，而是覆盖最常见、最能决定走向的分支点。

| 现象（Symptoms） | 分流问题（Decision） | 阶段（Phase） | 关键方法（Entry） | 必看变量（观察清单） | 对应 Lab |
| --- | --- | --- | --- | --- | --- |
| `NoSuchBeanDefinitionException` | 是真的没注册？还是没命中候选？ | 依赖解析 | `DefaultListableBeanFactory#doResolveDependency` | `descriptor/requiredType`、`matchingBeans.keySet()` | `SpringCoreBeansContainerLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` / 候选相关 Lab |
| `NoUniqueBeanDefinitionException` | 多候选如何收敛？有没有 @Primary/@Qualifier/by-name fallback？ | 依赖解析 | `determineAutowireCandidate` | `candidates`、`primaryCandidate`、`dependencyName` | `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest` |
| “`@Resource` 字段为 null” | 容器是否装了 JSR-250 处理器？ | 注解处理（实例层） | `CommonAnnotationBeanPostProcessor#postProcessProperties` | 容器里是否存在该 BPP、`resourceName` | `SpringCoreBeansResourceInjectionLabTest` |
| `@Value("${missing}")` 未失败，值变成原样字符串 | resolver 是否 non-strict？ | 值解析 | `AbstractBeanFactory#resolveEmbeddedValue` | 输入/输出是否仍含 `${` | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
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

## 如何使用这张表：root cause 先于外层异常

当读者获取到一个异常/现象时，按以下顺序进行：

1. **定位现象行**：它属于注入/占位符/代理/循环依赖哪一类？
2. **回答分流问题**：把“可能原因”缩成 1–2 个分支
3. **直接跳到关键方法设置断点**：只看观察清单，避免在调用栈中无目的追踪
4. **用对应 Lab 复现**：确认读者理解的是机制，而不是项目偶然

---

## 面试复述：把排障流程复用为答题流程
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


面试中的很多题目本质上是“给读者一个现象，让阅读者解释机制”。可将答题过程复用为：

1. 先把现象放回阶段：definition / creation / after-init（对应 refresh 时间线）
2. 再用 1 个关键断点给出证据（避免以主观判断代替证据）
3. 最后给出工程处理路径（如何避免/如何验证）

复习入口：`appendix-interview-playbook.md`（每题都对应“阶段 + 关键方法 + 可运行 Lab”）。

## 验收口径：能从异常行走到断点
读完后应能做到：

1. 看到 `NoUniqueBeanDefinitionException`，能立刻说出“下哪个断点、看哪三个变量”。
2. 看到 `@Value("${missing}")` 原样字符串，能立刻判断 strict/non-strict 并给出修复路径。
3. 看到“代理不生效”，能把问题分成“顺序问题 vs 时机问题”两类并给出证据链入口。


## 小结：矩阵的终点是 `refresh()` 阶段定位

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

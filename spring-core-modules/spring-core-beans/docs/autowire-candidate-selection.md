# 自动装配候选选择：从类型匹配到唯一候选

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释自动装配候选如何经过 type、`autowireCandidate`、qualifier、primary、name、priority、default candidate 和集合排序规则。
    - 重点区分单值依赖的冲突解决与集合/数组/Stream 的排序。
    - 读完后应能把 `NoUniqueBeanDefinitionException`、隐式 by-name 命中和 `@Order` 误用定位到具体步骤。

    观察对象：候选 BeanDefinition、注入点信号和 `determineAutowireCandidate` 收敛过程。
    主线位置：`resolveDependency` 收集候选之后，真正注入对象之前。
    对照入口：`SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansAutowireCandidateSelectionExerciseTest`、`SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest`、`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`。
<!-- CHAPTER-CARD:END -->

自动装配候选选择解决的是一个具体问题：当注入点需要 `Worker` 时，容器中可能有 0 个、1 个或多个 `Worker` Bean。候选选择算法要在这些供给对象中找出符合注入点需求的结果。对单值依赖，它必须收敛到一个；对集合依赖，它可以返回多个并排序。

把它写成决策过程更清楚：

```text
按类型找候选
-> 排除 autowireCandidate=false
-> 应用 qualifier / 泛型 / 自定义 qualifier
-> 单值：primary
-> 单值：dependency name 匹配 bean name / alias
-> 单值：suggested name 匹配 bean name / alias
-> 单值：priority
-> 单值：unique default candidate
-> 单值：directly registered resolvable dependency
-> 多值：按 order comparator 排序
-> 无法收敛则失败或按 API 语义返回空
```

这是 Spring 6.2.x `DefaultListableBeanFactory#determineAutowireCandidate` 的主线顺序。`fallback` 不是这里单独排在最后的一个强选择步骤；它会影响 primary 候选判断中的“唯一非 fallback 候选”收敛。不同 Spring 版本在 default candidate、fallback candidate 等细节上会演进，但排障思路不变：先看候选集合如何缩小，再看单值如何选出唯一结果。

## 1. 类型匹配只是入口

第一步按 dependency type 或 `ResolvableType` 找候选。`Worker` 会收集可赋值给 `Worker` 的 Bean；`Handler<String>` 可以用泛型信号缩小候选。`SpringCoreBeansAutowireCandidateSelectionLabTest#genericType_canNarrowCandidates_forSingleInjection` 证明泛型信息保留时，`Handler<String>` 可以选中 string handler，而不是 long handler。

类型匹配不能代表最终可注入。一个 Bean 存在、类型也匹配，仍可能被定义元数据排除。

## 2. `autowireCandidate=false`：存在但不参与自动装配

BeanDefinition 的 `autowireCandidate` flag 是候选资格开关。设为 false 后，Bean 仍在容器里，可以按名称 `getBean`，但不会进入普通自动装配候选。

`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest#beanDefinitionAutowireCandidateFalse_excludesBeanFromAutowiring_butBeanStillExists` 展示了这个边界：`excludedWorker` 能被 `getBean` 取到，但解析 `Worker` 注入点时被忽略，容器选择 `candidateWorker`。

所以排查“明明有 Bean 却注不进去”时，要看 BeanDefinition 元数据，而不是只看 Bean 名称列表。

## 3. qualifier 是强过滤信号

`@Qualifier` 表示注入点明确要求某类候选。它可以匹配 bean name，也可以匹配 BeanDefinition 上的 qualifier 元数据。`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest#beanDefinitionQualifierMetadata_canBeMatchedByQualifierAnnotation` 证明即使类上没有写注解，定义层 qualifier 也能参与匹配。

qualifier 的强度高于 primary。`SpringCoreBeansAutowireCandidateSelectionLabTest#qualifierOverridesPrimary_forSingleInjection` 中，primary bean 存在，但字段上的 `@Qualifier("secondaryWorker")` 仍选择 secondary。

自定义 qualifier 本质上也是给注入点和候选之间增加匹配条件。它不是排序，也不是默认胜者，而是先把不符合条件的候选排除。

## 4. primary：给单值依赖一个默认胜者

当多个候选都通过过滤，且注入点没有 qualifier 指向具体对象时，`@Primary` 或 BeanDefinition `primary=true` 可以声明默认胜者。

`SpringCoreBeansInjectionAmbiguityLabTest#primary_canResolveSingleInjectionAmbiguity_byChoosingTheDefaultWinner` 展示了两个 `Worker` 中 primary 胜出。`SpringCoreBeansBeanDefinitionMetadataFlagsLabTest#beanDefinitionPrimaryFlag_participatesInCandidateSelection` 说明 primary 不一定来自注解，也可以由 BeanDefinition flag 提供。

primary 主要面向单值依赖。集合注入通常仍会包含所有合格候选，不会因为某个 Bean 是 primary 就只注入它。

## 5. name matching：primary 之后的隐式收敛

当多个候选无法靠 qualifier 和 primary 收敛时，Spring 会尝试使用 dependency name 与 bean name 或 alias 的匹配来缩小候选。字段注入中 dependency name 通常是字段名；构造器参数则依赖参数名发现。随后还会尝试 resolver 给出的 suggested name，例如某些 qualifier 派生出的建议名称。

`SpringCoreBeansAutowireCandidateSelectionLabTest#byNameFallback_canResolveSingleInjectionAmbiguity_forAutowiredFieldInjection` 中字段名 `secondaryWorker` 匹配 bean name，因此两个 `Worker` 中选择 secondary。`byNameFallback_canMatchAlias_forAutowiredFieldInjection` 说明 alias 也可以匹配。

这个规则有工程风险：重构字段名、参数名元数据缺失、别名调整都可能改变结果。更稳定的做法是显式 qualifier 或拆分接口。`SpringCoreBeansAutowireCandidateSelectionExerciseTest` 专门把这个作为练习题，让读者复现并说明风险。

name matching 不是最高优先级。`primaryOverridesByNameFallback_forSingleInjection` 证明 primary 会先于字段名匹配胜出。但它也不是最后一步；在 Spring 6.2.x 中，它早于 `@Priority` 和 unique default candidate。因此排查“字段名刚好等于某个 bean name”时，不要把它当成所有规则之后才发生的兜底。

## 6. priority：名称没有收敛时的单值优先级

`@Priority` 可以在没有 qualifier、primary、dependency name 或 suggested name 收敛时参与单值候选选择。`SpringCoreBeansAutowireCandidateSelectionLabTest#priorityAnnotation_canBreakTieForSingleInjection_whenNoPrimaryOrQualifier` 证明较小 priority 值的候选会胜出。

如果 primary 和 priority 同时出现，primary 优先。`primaryOverridesPriority_forSingleInjection` 用 `PrimaryWorker` 和 `VeryHighPriorityWorker` 证明了这一点：即使另一个候选 priority 数值更小，primary 仍然胜出。字段名或参数名能够命中时，也要先确认名称匹配是否已经在 priority 之前收敛。

## 7. fallback/default candidate：默认性不是强制性

Spring 的候选元数据中有“默认候选”或“fallback 候选”一类语义，用于表达某个 Bean 是退让实现，而不是强信号实现。

排障时要把它们和 primary 区分开：

- primary 表示“多个候选中我优先成为单值结果”。
- fallback 表示“我是兜底候选”，Spring 6.2.x 会在 primary 判断阶段尝试找唯一非 fallback 候选。
- default candidate 是 name、priority 都没有收敛后才检查的唯一默认候选。
- qualifier 仍然可以把需求指向明确候选。

本模块当前 Lab 主要固定 primary、qualifier、priority、autowireCandidate 和 name matching；遇到 fallback/default candidate 问题时，应回到 BeanDefinition 元数据和当前 Spring 版本源码确认具体分支。

## 8. `@Order`：集合排序，不是单值冲突解决

`@Order` 和 `Ordered` 主要影响多值注入顺序，例如 `List<Worker>`、数组、stream 或 `ObjectProvider#orderedStream()`。`SpringCoreBeansAutowireCandidateSelectionLabTest#orderAnnotation_affectsCollectionInjectionOrder` 中较小 order 值排在前面；`objectProvider_orderedStream_respectsOrderAnnotation` 也证明 ordered stream 遵循排序。

它不解决单值依赖冲突。`orderAnnotation_doesNotResolveSingleInjectionAmbiguity` 明确证明：两个 `Worker` 即使有 `@Order(0)` 和 `@Order(1)`，注入单个 `Worker` 仍然抛出 `NoUniqueBeanDefinitionException`。

一句话区分：

- `@Priority` 可以参与单值候选优先级。
- `@Order` 控制集合/数组/stream 中多个结果的顺序。

不要用 `@Order` 试图修复 `NoUniqueBeanDefinitionException`。

## 失败和非失败 API

普通 required 单值注入无法收敛时会失败。`SpringCoreBeansInjectionAmbiguityLabTest` 展示了 refresh 期间的 `UnsatisfiedDependencyException`，根因是 `NoUniqueBeanDefinitionException`。

但有些 API 把不满足唯一性表达成空值。`SpringCoreBeansAutowireCandidateSelectionLabTest#objectProvider_getIfUnique_returnsNull_whenMultipleCandidatesExist` 说明 `ObjectProvider#getIfUnique()` 在多候选时返回 null。`SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest` 又对比了 0/1/多候选下 provider API 的用法。

因此看异常前，先确认注入点或 API 想表达的语义：必须唯一、可选、唯一才要，还是要全部候选。

## 最短排障矩阵

| 现象 | 优先检查 |
| --- | --- |
| 有 Bean 但没有被注入 | `autowireCandidate`、泛型、qualifier 是否排除了它 |
| 多个同类型 Bean 注入失败 | 是否缺少 qualifier、primary、priority 或稳定名称匹配 |
| 写了 `@Order` 仍然单值失败 | `@Order` 只影响集合排序 |
| 字段没写 qualifier 却注入成功 | dependency name 是否匹配了 bean name 或 alias |
| primary 没有生效 | 注入点 qualifier 是否选择了其他候选 |
| `getIfUnique()` 返回 null | 候选不是唯一，而不是没有 Bean |

候选选择的核心不是背注解优先级，而是每次都把“候选集合如何变化”说清楚。

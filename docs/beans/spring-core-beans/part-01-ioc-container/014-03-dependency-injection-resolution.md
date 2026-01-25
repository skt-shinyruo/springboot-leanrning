# 第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：依赖注入解析：类型/名称/@Qualifier/@Primary
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](../part-00-guide/013-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. 依赖注入解析：类型/名称/@Qualifier/@Primary**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest` / `SpringCoreBeansLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

## 机制主线

这一章回答：**当你写下 `private final X x;`，Spring 到底是怎么找到并注入那个 `X` 的？**

在学习阶段，建议把注入问题拆成两类：

1) **有没有候选**（NoSuchBeanDefinition）
2) **候选太多怎么选**（NoUniqueBeanDefinition）

## 1. 本模块里的最小例子：两个 `TextFormatter`

代码位置：

- 接口：`src/main/java/com/learning/springboot/springcorebeans/part01_ioc_container/TextFormatter.java`
- 实现：
  - `UpperCaseTextFormatter`（bean name：`upperFormatter`）
  - `LowerCaseTextFormatter`（bean name：`lowerFormatter`）
- 注入点：`FormattingService`

`FormattingService` 的构造器是：

```java
public FormattingService(@Qualifier("upperFormatter") TextFormatter textFormatter) { ... }
```

它解决的是一个经典问题：**同一类型有多个 Bean，按类型注入会歧义**。

## 2. Spring 的候选选择：先收集，再缩小

在高层层面，可以这样理解：

1) 先按“类型”找候选（所有 `TextFormatter`）
2) 根据限定条件缩小候选
   - `@Qualifier`：显式指定（最常用）
   - `@Primary`：默认胜出者
   - beanName 匹配（在某些场景可作为一种缩小方式）
3) 仍然不唯一就报错（这是好事：避免静默注错）

重点：**`@Qualifier` 是“缩小候选集合”的规则**，而不是“把某个 bean 改名”的方式。

## 3. `@Qualifier` vs `@Primary` 怎么选？

建议的工程决策：

- 业务上“默认实现”明显存在：用 `@Primary`
- 需要按场景明确选择实现：用 `@Qualifier`

常见实践组合：

- 给默认实现加 `@Primary`
- 对“非默认实现”的注入点显式加 `@Qualifier`

## 4. 可选依赖与延迟获取：`ObjectProvider`

当你不想“容器启动时就必须有这个 bean”，或者你希望每次都能获取“最新/新的实例”，可以用：

- `ObjectProvider<T>`

本模块已有例子：

- `ProviderPrototypeConsumer` 注入 `ObjectProvider<PrototypeIdGenerator>`

这类方式本质上是在说：

- 我不要求你立刻注入一个具体对象
- 我要求你给我一个“将来可以向容器要对象”的入口

这对 prototype 注入 singleton 尤其重要，[04 章](015-04-scope-and-prototype.md)会详细解释。

当你希望“没有这个 bean 也能启动”，你需要明确告诉容器：**这个依赖不是强依赖**。

常见的三种做法（按“表达力”从直观到灵活）：

> 经验规则：如果你在写“核心业务依赖”，不要用可选注入把问题藏起来；  
> 可选注入更适合“可插拔能力”（metrics、tracing、某些 adapter）或“演进中的依赖”。

- `DefaultListableBeanFactory#doResolveDependency`（看 `descriptor.isRequired()`、看候选集合为空时的分支）
- `DefaultListableBeanFactory#resolveMultipleBeans`（`Optional`/集合依赖不会走“选唯一候选”的逻辑）

## 4.2 JSR-330：`@Inject` / `@Named` / `Provider<T>`（对照 Spring）

Spring 也支持 JSR-330（`jakarta.inject`）注入体系，但你需要把它和 Spring 自己的注入语义对齐理解：

## 5. 集合注入与排序（你以后一定会遇到）

当你注入：

- `List<T>`：容器会把所有 `T` 类型的 bean 都注入进来
- `Map<String, T>`：key 通常是 beanName

这通常用于“插件式扩展”：

- 多个实现按顺序执行（过滤器链、策略链）

顺序控制常见手段：

- `@Order` / `Ordered`：影响集合注入的顺序（不是“单 bean 选择”的规则）

> 常见误解：很多人以为给 bean 加了 `@Order(1)` 就能“优先被注入到单个依赖里”。通常不是这样：单依赖选择优先看 `@Primary`、`@Qualifier` 等。

## 6. 你应该能从报错里读出什么

当你遇到类似异常时：

- `NoSuchBeanDefinitionException`：没有候选
- `NoUniqueBeanDefinitionException`：候选太多且无法唯一化

你应该能顺着异常信息反推：

1) 注入点需要的类型是什么？
2) 容器里有哪些候选？
3) 缺少的限定条件是什么？（`@Qualifier` / `@Primary` / beanName 等）

## 7. 源码级：候选选择的决策树（determineAutowireCandidate）

如果你的目标是“能定位真实项目里为什么注入的是它 / 为什么歧义”，你必须掌握这一点：

> 单个依赖注入的核心不是“按类型找”，而是：**先收集候选（type match），再用一组规则缩小候选（candidate selection）**。

### 7.1 收集候选（collect）

目标：把“可能的 bean”都收集起来（此时不保证唯一）。

- `DefaultListableBeanFactory#resolveDependency`
- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`
- `AutowireCandidateResolver#isAutowireCandidate`（会参与 qualifier 判断）

你要建立的直觉：

### 7.2 缩小候选（narrow down）

目标：把候选压到 1 个；如果压不下来，就抛 `NoUniqueBeanDefinitionException`。

缩小规则（按常见优先级理解即可；源码里会更细）：

### 面试常问：依赖解析的“源码级决策树”（候选收集 → 候选收敛 → 最终注入）

### 7.3 最终返回值：resolveDependency 返回的到底是什么？

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`
- `DefaultListableBeanFactory#determineAutowireCandidate`
- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（或其子类）

**推荐观察点（watch list）：**

- `dependencyDescriptor.getDependencyType()`（注入点需要什么类型）
- `dependencyDescriptor.getAnnotations()`（注入点有哪些限定信息）
- `matchingBeans` / `candidates`（候选集合怎么被缩小）
- `autowiredBeanNames`（最终注入了哪些 beanName）

## 源码最短路径（call chain）

> 目标：从入口到“候选收敛”的关键分支，给你一条最短可跟的栈路径（不要求把全链路单步到底）。

你在 IDE 里最常见的两个入口：

1) field/method 注入入口（属性填充阶段）  
   `AutowiredAnnotationBeanPostProcessor#postProcessProperties`
2) constructor 注入入口（实例化阶段）  
   `ConstructorResolver#autowireConstructor`（最终也会走到依赖解析）

两条入口最终都会汇合到依赖解析主干：

- `DefaultListableBeanFactory#resolveDependency`
  - `DefaultListableBeanFactory#doResolveDependency`
    - **（特殊通道）** `resolvableDependencies` 命中：直接返回（见 [20](../part-04-wiring-and-boundaries/20-resolvable-dependency.md)）
    - **（集合通道）** `resolveMultipleBeans(...)`：`List/Map/ObjectProvider` 等不会走“选唯一候选”的逻辑
    - **（普通通道）** `findAutowireCandidates(...)`：先按类型收集候选集合
      - `determineAutowireCandidate(...)`：再按规则收敛到唯一候选（`@Qualifier/@Primary/@Priority/beanName` 等）
    - `getBean(candidateName)`：拿到最终注入的实例，并记录依赖边（`dependentBeanMap` / `dependenciesForBeanMap`，见 [19](../part-04-wiring-and-boundaries/19-depends-on.md)）

如果你只想把“候选如何收敛”看清楚，优先在这两个点停住即可：

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#determineAutowireCandidate`

## 固定观察点（watch list）

> 目标：你每次停在 `doResolveDependency` 都只看这几项，就能快速回答“候选有哪些、为什么选它、为什么失败”。

建议在 `doResolveDependency(...)` 里 watch/evaluate：

- `descriptor.getDependencyType()`：注入点要什么类型（最重要）
- `descriptor.getDependencyName()`：注入点的名字（字段名/参数名；在“按名称收敛”分支会用到）
- `descriptor.isRequired()`：是否必填（决定是否允许返回 null）
- `this.resolvableDependencies`：是否有“能注入但不是 bean”的特殊依赖（命中则直接返回）
- `matchingBeans` / `findAutowireCandidates(...)` 的返回值：**候选集合**（by type 的结果）
- `matchingBeans.keySet()`：候选 beanName 列表（先别急着看实例）
- `autowireCandidateResolver`：候选筛选器（`@Qualifier` 的关键逻辑通常在这里）
- `autowiredBeanNames`：容器最终记录的“本次依赖解析涉及到哪些 beanName”（用于依赖图）

建议在 `determineAutowireCandidate(...)` 里重点看这些“收敛点”：

- `determinePrimaryCandidate(...)`：是否存在 `@Primary`
- `determineHighestPriorityCandidate(...)`：是否有 `@Priority` 参与 tie-break
- `descriptor.getDependencyName()` / `matchesBeanName(...)`：是否出现“按名称收敛”（很多人会误以为是随机）

## 反例（counterexample）

**反例：我给 bean 加了 `@Order(1)`，以为就会优先被注入，但还是 NoUnique。**

- `findAutowireCandidates(...)` 的 `matchingBeans` 里仍然会有多个候选（因为它是按类型收集出来的）
- `determineAutowireCandidate(...)` 不会因为 `@Order` 给你选出唯一候选（它处理的是“选谁”，`@Order` 处理的是“集合怎么排”）
- 最终抛出 `NoUniqueBeanDefinitionException`（候选太多，容器拒绝“静默注错”）

把这个反例看懂，你就能把三件事分清：

- 单依赖注入：`@Qualifier/@Primary/@Priority`（见 [33](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)）
- 集合注入排序：`@Order/@Priority/Ordered`
- “能注入但不是 bean”：`resolvableDependencies`（见 [20](../part-04-wiring-and-boundaries/20-resolvable-dependency.md)）

## 源码解析补充：`doResolveDependency` 的关键分支（伪代码）

上面的“候选收集与收敛”讲的是主干逻辑，但你在真实项目里常遇到的“为什么它没走到 findAutowireCandidates？”通常是因为在更早的分支就返回了。

你可以把它当作一张“排障分叉图”：

- 如果你在 `doResolveDependency` 里没看到候选集合的变化，先检查是不是命中 A/B/C/D 之一提前返回了
- 真正的“多候选如何选”只发生在 E 分支里

## 必要时用仓库 src 代码把分支差异讲清楚（最小片段）

- `@Order`：只影响集合注入顺序（List/Stream）
- `@Primary/@Priority`：影响单依赖“选谁”

（最小片段，省略无关方法体）：

```java
interface Worker { String id(); }

@Order(0)
static class FirstOrderedWorker implements Worker { ... }

@Order(1)
static class SecondOrderedWorker implements Worker { ... }

static class OrderedWorkersConsumer {
    OrderedWorkersConsumer(List<Worker> workers) { ... } // 集合注入：会排序
}

static class SingleWorkerConsumer {
    SingleWorkerConsumer(Worker worker) { ... } // 单依赖：@Order 不参与“选谁”
}
```

- 集合注入会走 `resolveMultipleBeans(...)` 分支 → 然后按 order 排序
- 单依赖注入会走 `findAutowireCandidates(...)` → `determineAutowireCandidate(...)` 分支 → 不会因为 `@Order` 变成唯一候选

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

Exercises 里也专门有题让你把 `@Qualifier` 改成 `@Primary` 来体会差异：

- `src/test/java/.../SpringCoreBeansExerciseTest.java`
  - `exercise_resolveMultipleBeansViaPrimaryInsteadOfQualifier()`

## 4.1 可选依赖：`@Autowired(required=false)` / `Optional<T>` / `@Nullable`

1) `@Autowired(required=false)`（更偏 field/setter 注入）
   - 缺失时：不报错，注入 `null`
   - 适合：兼容性开关、可插拔依赖（但要注意 null 处理）
2) `Optional<T>`（更偏 constructor/方法参数注入）
   - 缺失时：注入 `Optional.empty()`
   - 适合：显式表达“可选”，比 `null` 更安全
3) `@Nullable`（对参数/字段标注“可为 null”）
   - 缺失时：允许注入 `null`
   - 适合：你不想引入 Optional，但能接受空值语义

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java`

建议断点：

- `@Inject` ≈ `@Autowired`（默认 required=true）
  - JSR-330 的 `@Inject` **没有** `required=false` 这种属性，想表达“可选”通常靠 `Provider<T>` / `Optional<T>`
- `@Named("beanName")` ≈ `@Qualifier("beanName")`
  - 用于在多候选时做按名选择（本质仍是“候选收敛”）
- `Provider<T>`（JSR-330）与 `ObjectProvider<T>`（Spring）都属于“延迟解析”
  - 共同点：注入阶段不强制创建目标 bean；真正调用 `get()` / `getObject()` 时才解析
  - 差异点：`ObjectProvider` 提供 `getIfAvailable()` / `getIfUnique()` 等更友好的可选语义

复现入口（可断言 + 可断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java`
- 推荐运行命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansJsr330InjectionLabTest test`

本模块的 labs 已经覆盖（建议按顺序跑一遍，能明显缩短你“从报错到断点”的时间）：

- `SpringCoreBeansLabTest.usesQualifierToResolveMultipleBeans()`：最小 `@Qualifier` 场景
- `SpringCoreBeansAutowireCandidateSelectionLabTest.orderAnnotation_doesNotResolveSingleInjectionAmbiguity()`：最小 “候选太多（NoUnique）” 场景
- `SpringCoreBeansBeanGraphDebugLabTest.dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected()`：把“候选集合 → 最终注入谁 → 依赖边”打印出来

下面三段是把这章写成“调试手册”的关键：你可以不背规则，但要能在断点里**用固定观察点快速收敛**。

下面给一个源码级、可直接对照断点的决策树（你可以把它当作 `DefaultListableBeanFactory#determineAutowireCandidate` 的“可复述版本”）：

常见入口与参与者（按你打断点的顺序）：

1. **限定符（Qualifier）先过滤**
   - 注入点有 `@Qualifier` 时，候选必须匹配它（不是“注解存在就行”，是要匹配 qualifier 值/属性）。
2. **名字匹配（byName）通常会在某些路径上成为强信号**
   - 例如注入点名字与 beanName 相同，会成为候选优先项（尤其 `@Resource` 是 name-first，见 [32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](../part-04-wiring-and-boundaries/32-resource-injection-name-first.md)）。
3. **`@Primary` 解决“默认实现”**
   - 多实现时，如果恰好有一个 primary，它通常直接胜出。
4. **`@Priority`/Ordered（谨慎理解）**
   - 它能在某些“单注入 tie-break”场景起作用，但**不能替代** `@Primary`/`@Qualifier`（并且不同场景优先级不同，必须用实验验证）。
5. **仍不唯一 → 明确失败**
   - 失败并不是坏事：它迫使你把依赖关系写清楚，而不是让容器“猜”。

- 题目：依赖解析的关键调用链是什么？请按“候选收集 → 候选收敛 → 最终注入”描述主线。
- 追问：
  - `findAutowireCandidates(...)` 与 `determineAutowireCandidate(...)` 各自解决什么问题？你会在哪两个方法下断点证明“为什么最终注入的是它”？
  - 泛型注入（`List<Foo>` / `Foo<Bar>`）在 type matching 上有哪些坑？为什么有时“看起来同类型”却匹配不上？（提示：raw type、FactoryBean product type、代理导致的类型信息丢失/不一致）
- 复现入口（建议按顺序跑 + 下断点）：
  - 候选收集/依赖边记录：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`
      - `dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected()`
  - 候选收敛（`@Primary/@Priority/@Order` 的差异）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`
      - `orderAnnotation_doesNotResolveSingleInjectionAmbiguity()`
      - `primaryOverridesPriority_forSingleInjection()`
      - `priorityAnnotation_canBreakTieForSingleInjection_whenNoPrimaryOrQualifier()`
  - 泛型匹配坑（“看起来同类型”却匹配不上）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
      - `genericTypeMatching_canFailWhenCandidateLosesGenericInformation_likeJdkProxySingleton()`

## 8. 断点闭环（用本仓库 Lab/Test 跑一遍）

建议你把“规则”变成“手感”：直接跑这些测试方法，并按顺序打断点观察候选变化：

- `SpringCoreBeansAutowireCandidateSelectionLabTest#orderAnnotation_doesNotResolveSingleInjectionAmbiguity`
- `SpringCoreBeansAutowireCandidateSelectionLabTest#primaryOverridesPriority_forSingleInjection`
- `SpringCoreBeansAutowireCandidateSelectionLabTest#priorityAnnotation_canBreakTieForSingleInjection_whenNoPrimaryOrQualifier`
- `SpringCoreBeansAutowireCandidateSelectionLabTest#orderAnnotation_affectsCollectionInjectionOrder`

**推荐断点（够用版）：**

## 9. 排障速查：从异常到下一步断点

当你在真实项目里遇到注入失败，建议用“异常 → 下一步断点”的方式快速定位：

- `NoSuchBeanDefinitionException`：先看是否压根没有候选
  - 断点：`findAutowireCandidates` 是否返回空
- `NoUniqueBeanDefinitionException`：候选>1，缩不下来
  - 断点：`determineAutowireCandidate` 为什么没有选中
  - 快速修复：加 `@Qualifier`（精确）或 `@Primary`（默认实现）
- `UnsatisfiedDependencyException`：外层包装异常
  - 先展开 root cause，通常还是上面两类

> 小技巧：`doResolveDependency` 命中次数很高时，先加条件断点（例如 `descriptor.getDependencyType() == Worker.class`），再看调用栈与变量。

最小复现入口：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`
  - `orderAnnotation_doesNotResolveSingleInjectionAmbiguity()`

你在断点里应该看到什么（用于纠错）：

下面给一份**足够贴近断点观察**的精简伪代码（只保留关键分叉，不追求逐行一致）：

```text
resolveDependency(descriptor):
  // A) 特殊依赖：不是 bean，但允许注入（例如 BeanFactory/ApplicationContext）
  if (resolvableDependencies 命中):
     return 直接返回

  // B) 值注入（@Value / 占位符 / SpEL）：由候选解析器提供 suggested value
  if (resolver.getSuggestedValue(descriptor) != null):
     return convertIfNecessary(...)

  // C) “按名称”直接命中（字段名/参数名 或 resolver 建议名）
  if (dependencyName 或 suggestedName 能唯一命中且类型匹配):
     return getBean(name)

  // D) 多元素依赖：数组/集合/Map/Stream/Provider
  if (isArrayOrCollectionOrMapOrStream(descriptor)):
     return resolveMultipleBeans(...)
  if (isObjectFactoryOrObjectProvider(descriptor)):
     return DependencyObjectProvider(...) // 延迟到 getObject()/getIfAvailable() 再解析

  // E) 普通单依赖：按类型收集候选，再确定化选择
  candidates = findAutowireCandidates(...)
  if (candidates 为空):
     if (required) throw NoSuchBeanDefinitionException
     else return null/Optional.empty

  candidateName = determineAutowireCandidate(candidates, descriptor)
  if (candidateName 为空):
     throw NoUniqueBeanDefinitionException
  return getBean(candidateName)
```

下面这段来自 `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansAutowireCandidateSelectionLabTest.java`，它同时覆盖了：

有了这段代码，你在源码断点里就很容易对照：

下一章我们会把 “候选是怎么创建出来的” 和 “什么时候创建” 结合起来讲：Scope。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`
推荐断点：`DefaultListableBeanFactory#doResolveDependency`、`DefaultListableBeanFactory#determineAutowireCandidate`、`AutowiredAnnotationBeanPostProcessor#postProcessProperties`

## 常见坑与边界

- type matching 不是只看“实现类/接口”，还会考虑泛型、`FactoryBean` 的 product 类型、是否允许 eager init 等因素（见 [23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](../part-04-wiring-and-boundaries/23-factorybean-deep-dive.md)、[29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](../part-04-wiring-and-boundaries/29-factorybean-edge-cases.md)）。
- 收集到的候选通常是一个 `Map<String, Object>`：key 是 beanName，value 可能是实例，也可能是类型/占位（取决于是否 eager resolve）。

## 小结与下一章

- 单依赖：返回一个 bean（或一个代理对象，见 [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）
- 可选依赖：可能返回 `null`、`Optional.empty()`，或者 `ObjectProvider`（延迟到你真正调用 `getObject()` 才解析）
- 集合依赖：返回“所有匹配候选”的集合，并且会排序（`@Order`/`Ordered`），见下一章的集合注入部分

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest` / `SpringCoreBeansLabTest`
- Exercise：`SpringCoreBeansExerciseTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

上一章：[02. Bean 注册入口：扫描、@Bean、@Import、registrar](02-bean-registration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md)

<!-- BOOKIFY:END -->

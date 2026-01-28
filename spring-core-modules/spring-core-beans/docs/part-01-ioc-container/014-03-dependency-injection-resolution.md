# 第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：依赖注入解析：类型/名称/@Qualifier/@Primary
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` / `#findAutowireCandidates` / `#determineAutowireCandidate`
    - 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](../part-00-guide/013-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. 依赖注入解析：类型/名称/@Qualifier/@Primary**
- 阅读方式建议：先跑一次“候选歧义”的最小 Lab，再回到正文按“候选收集 → 候选收敛 → 最终注入”把主线走通。

!!! summary "本章要点"

    - 读者写下 `private final X x;` 时，Spring 做的不是“按类型找一个就行”，而是：**先收集候选（by type），再用一套规则缩小候选（by qualifier/primary/priority/name…）**。
    - `@Order` 管的是“集合注入怎么排”，不是“单依赖注入选谁”。单依赖选谁主要看 `@Primary/@Qualifier`，必要时才用 `@Priority` 做 tie-break。
    - 排障不要靠猜：在 `doResolveDependency(...)` 里盯住固定观察点（dependencyType / candidates / selectedName），即可解释“为什么注入的是它/为什么失败”。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansOptionalInjectionLabTest` / `SpringCoreBeansJsr330InjectionLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java`

## 机制主线：候选收集 → 候选收敛 → 最终注入

这一章回答一个非常具体的问题：**当读者写下 `private final X x;`，Spring 到底是怎么找到并注入那个 `X` 的？**

把所有注入失败先分成两类（在真实项目里 90% 的时间都在处理它们）：

1) **没有候选**（`NoSuchBeanDefinitionException`）
2) **候选太多缩不下来**（`NoUniqueBeanDefinitionException`）

> 经验规则：不要在注入点上“赌 Spring 会选对”。如果候选>1 且读者没有写清楚规则，Spring 选择失败是一个非常好的保护。

### 0.1 DependencyDescriptor 深挖：注入点到底“要什么”？

`DependencyDescriptor` 是依赖解析的真正“需求描述”，应能够否解释它，决定了应能够否解释“为什么注入的是它”。重点看这些字段：

- `required`：是否必须（`@Autowired(required=false)` / Optional 影响这里）
- `annotations`：`@Qualifier/@Value/@Lazy` 等都会在这里被解析
- `resolvableType`：泛型信息（`Handler<String>` vs `Handler<Long>`）会影响候选匹配
- `dependencyName`：字段名/参数名（用于 by-name fallback）

**两个对照注入点（应能够解释差异）：**

1) 字段注入（依赖名可见）：  
   - `@Autowired private Worker secondaryWorker;`  
   - `dependencyName = secondaryWorker`，可能触发 by-name fallback
2) 构造器注入（依赖名来自参数）：  
   - `public Consumer(Worker worker)`  
   - `dependencyName = worker`，如果候选>1 且无 Qualifier/Primary，容易歧义

---

## 1. 本模块里的最小例子：两个 `TextFormatter`

代码位置：

- 接口：`src/main/java/com/learning/springboot/springcorebeans/part01_ioc_container/TextFormatter.java`
- 实现：
  - `UpperCaseTextFormatter`（bean name：`upperFormatter`）
  - `LowerCaseTextFormatter`（bean name：`lowerFormatter`）
- 注入点：`FormattingService`

`FormattingService` 的构造器使用 `@Qualifier` 明确选择：

```java
public FormattingService(@Qualifier("upperFormatter") TextFormatter textFormatter) { ... }
```

它解决的是经典问题：**同一类型有多个 Bean，按类型注入会歧义。**

---

## 2. 候选收集（collect）：先回答“有哪些可能的候选？”

单依赖注入的主干入口在：

- `DefaultListableBeanFactory#resolveDependency`
- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`

需要建立的直觉是：**by type 的候选集合通常不小**，需要先把它“看见”，再谈“为什么最终选中它”。

### 2.0 依赖解析分支树（全链路视角）

把 `doResolveDependency` 看成“分支树”会更容易排障：

1) **快捷路径**：`Optional` / `ObjectProvider` / `@Lazy` / `@Value`  
   - 现象：走 `resolveMultipleBeans` 或 `suggestedValue`，不会进入“候选收敛”
2) **resolvableDependencies**  
   - 现象：能注入 `BeanFactory/ApplicationContext` 等，但它们不是 Bean
3) **候选收集**（by type）  
   - 现象：`matchingBeans` 为空 → `NoSuchBeanDefinitionException`
4) **候选收敛**（Qualifier/Primary/Name/Priority）  
   - 现象：候选>1 且缩不下来 → `NoUniqueBeanDefinitionException`
5) **集合注入与排序**  
   - 现象：`@Order/@Priority` 影响 List/Stream 顺序，但不选唯一候选
6) **fallback**  
   - 现象：by-name / suggestedName 等兜底路径影响最终选择

### 2.1 三条“早返回通道”（很多人以为没走到候选收集，其实是提前返回了）

在 `doResolveDependency` 里，真实项目常见的三类提前返回：

- **resolvableDependencies 命中**：允许注入但不一定是 bean（例如 `BeanFactory`/`ApplicationContext` 等）。见：[20. ResolvableDependency：为什么有些东西能注入但不是 Bean？](../part-04-wiring-and-boundaries/20-resolvable-dependency.md)
- **值注入（@Value / 占位符 / SpEL）**：从 resolver 拿到 suggested value 后直接 `convertIfNecessary`（不会走“按类型找候选”）。
- **集合/流/Provider 通道**：`List/Map/Stream/ObjectProvider/Optional` 会走 `resolveMultipleBeans(...)`，它解决的是“收集全部”，不是“选唯一”。

> 排障提示：在断点里没看到候选集合变化时，先问自己：是不是命中这些早返回分支了？

### 2.2 需要关注的第一个变量：`matchingBeans` / `candidates`

- `findAutowireCandidates(...)` 的结果通常是 `Map<String, Object>`（beanName → candidate instance / type holder）
- 无需先看实例，先看 `matchingBeans.keySet()`：**候选 beanName 到底有哪些？**

这一步就足够读者回答：

- “容器里到底有哪些同类型实现？”
- “是不是某个 auto-config/扫描把我没预期的 bean 也注册进来了？”

### 2.3 关键变量速查表（把“为什么选它”变成可解释）

| 变量 | 含义 | 决策地位 |
| --- | --- | --- |
| `descriptor` | 注入点描述（类型/限定符/是否 required） | 决定候选筛选维度 |
| `matchingBeans` | 候选集合（beanName → candidate） | 决定是否进入收敛 |
| `dependencyName` | 字段/参数名 | by-name fallback 重要输入 |
| `suggestedName` | resolver 推导名称（如 Qualifier 值） | 可能直接命中候选 |
| `primaryCandidate` | 唯一 `@Primary` 候选 | 单依赖优先级高 |
| `highestPriorityCandidate` | `@Priority` tie-break | 兜底路径 |
| `autowiredBeanName` | 最终选中的 beanName | 最终结论落点 |

---

## 3. 候选收敛（narrow down）：从候选集合缩到唯一候选

当候选集合 > 1 时，Spring 会进入收敛逻辑：

- `DefaultListableBeanFactory#determineAutowireCandidate`

需要建立的直觉是：**收敛不是一个 if，而是一套有先后顺序的规则**。

### 3.1 一个足够贴近断点观察的“决策树”（不追求逐行一致，但足够排障）

```text
determineAutowireCandidate(candidates, descriptor):
  // 0) qualifier 过滤（不是最后才看；它会参与候选判定）

  // 1) primary
  if exactlyOnePrimaryCandidate:
     return that

  // 2) by-name match（字段名/参数名）
  if dependencyName matches one candidate:
     return that

  // 3) suggested name（resolver 可能给出）
  if suggestedName matches one candidate:
     return that

  // 4) highest priority（@Priority / Ordered 的 tie-break 语义）
  if exactlyOneHighestPriorityCandidate:
     return that

  // 5) still ambiguous -> fail
  throw NoUniqueBeanDefinitionException
```

无需背源码，但应能够在断点里验证：

- 候选集合为什么是这些（collect）
- 哪个规则把候选收敛到 1 个（narrow down）

### 3.2 `@Qualifier`：它是“缩小候选集合”的规则，不是改名

- `@Qualifier("xxx")` 的意义是“候选必须匹配这个 qualifier 条件”
- 它不是“把 bean 改名为 xxx”

在断点里应该能观察到：

- 注入点上的 qualifier（`descriptor.getAnnotations()`）
- `autowireCandidateResolver.isAutowireCandidate(...)` 过滤后的候选集合

> 补充：自定义 qualifier（meta-annotation）也属于同一体系：最终仍然会落在 resolver 的匹配逻辑上。

### 3.3 `@Primary` vs `@Qualifier`：怎么选？

工程决策（推荐）：

- 业务上“默认实现”明确存在：用 `@Primary`
- 需要按场景明确选择实现：用 `@Qualifier`

常见组合：

- 给默认实现加 `@Primary`
- 对“非默认实现”的注入点显式加 `@Qualifier`

### 3.4 `@Priority` vs `@Order`：必须把边界说清楚

- `@Order`：影响 **集合注入**（`List/Stream`）的排序
- `@Primary/@Qualifier`：影响 **单依赖注入** “选谁”
- `@Priority`：在某些“单依赖 tie-break”场景会参与（但它不是第一优先级，且不能替代 `@Primary/@Qualifier`）

反例（应能够解释并用 Lab 验证）：

> 我给 bean 加了 `@Order(1)`，以为就会优先被注入到单个依赖里，但依然 `NoUnique`。

最小片段（省略无关方法体）：

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

对应 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest#orderAnnotation_doesNotResolveSingleInjectionAmbiguity`

### 3.5 `@Resource`：为什么它更像“按名称找 Bean”（与 @Autowired 的差异）

- `@Autowired` 更偏 type-first（然后再收敛）
- `@Resource` 更偏 name-first（再退回到 type）

若在项目里见过“字段名改了，注入就变了/就坏了”，通常是 `@Resource` 的 name-first 语义触发。

详见：[32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](../part-04-wiring-and-boundaries/32-resource-injection-name-first.md)

### 3.6 机制讲透：候选收集 → 收敛 → 最终注入（条件→分支→结果）

**条件**：候选集合 > 1，且注入点没有明确限定  
**分支**：`determineAutowireCandidate` 依次尝试 Qualifier → Primary → by-name → Priority  
**结果**：  
- 仍然无法缩小 → `NoUniqueBeanDefinitionException`  
- 命中唯一候选 → 注入完成  
**证据链断点**：`DefaultListableBeanFactory#doResolveDependency`（观察 `matchingBeans` 与 `autowiredBeanName` 的变化）

---

## 4. 可选依赖与延迟解析：Optional / required=false / ObjectProvider

当读者希望“没有这个 bean 也能启动”，需要明确告诉容器：**这个依赖不是强依赖**。

### 4.1 可选依赖：三种常见写法

1) `@Autowired(required=false)`（更偏 field/setter 注入）
   - 缺失时：不报错，注入 `null`
   - 适合：兼容性开关、可插拔依赖（但要注意 null 处理）
2) `Optional<T>`（更偏 constructor/方法参数注入）
   - 缺失时：注入 `Optional.empty()`
   - 适合：显式表达“可选”，比 `null` 更安全
3) `@Nullable`（对参数/字段标注“可为 null”）
   - 缺失时：允许注入 `null`
   - 适合：读者不想引入 Optional，但能接受空值语义

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java`

### 4.2 延迟解析：`ObjectProvider<T>`（推荐的“可选 + 延迟”组合）

当读者不想“容器启动时就必须有这个 bean”，或者读者希望每次都能获取“最新/新的实例”，可以用：

- `ObjectProvider<T>`

它表达的是：

- 我不要求读者立刻注入一个具体对象
- 我要求读者给我一个“将来可以向容器要对象”的入口

它对 prototype 注入 singleton 尤其重要，[04 章](015-04-scope-and-prototype.md)会详细解释。

---

## 5. JSR-330 对照：`@Inject` / `@Named` / `Provider<T>`

Spring 也支持 JSR-330（`jakarta.inject`）注入体系，但需要把它与 Spring 的注入语义对齐理解：

- `@Inject` ≈ `@Autowired`（默认 required=true）
  - JSR-330 的 `@Inject` **没有** `required=false` 属性；想表达“可选”通常用 `Provider<T>` / `Optional<T>`
- `@Named("beanName")` ≈ `@Qualifier("beanName")`
  - 用于在多候选时做按名选择（本质仍是“候选收敛”）
- `Provider<T>`（JSR-330）与 `ObjectProvider<T>`（Spring）都属于“延迟解析”
  - 共同点：注入阶段不强制创建目标 bean；真正调用 `get()` / `getObject()` 时才解析
  - 差异点：`ObjectProvider` 提供 `getIfAvailable()` / `getIfUnique()` 等更友好的可选语义

复现入口（可断言 + 可断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java`

---

## 6. 调试闭环：从异常到下一步断点

### 6.1 最短调用链（两条入口最终汇合到 doResolveDependency）

在 IDE 里最常见的两个入口：

1) field/method 注入入口（属性填充阶段）
   - `AutowiredAnnotationBeanPostProcessor#postProcessProperties`
2) constructor 注入入口（实例化阶段）
   - `ConstructorResolver#autowireConstructor`（最终也会走到依赖解析）

两条入口最终都会汇合到依赖解析主干：

- `DefaultListableBeanFactory#resolveDependency`
  - `DefaultListableBeanFactory#doResolveDependency`
    - `findAutowireCandidates(...)`：候选收集（collect）
      - `determineAutowireCandidate(...)`：候选收敛（narrow down）

### 6.2 固定观察点（watch list）：读者每次都只看这几项

在 `doResolveDependency(...)` 里 watch/evaluate：

- `descriptor.getDependencyType()`：注入点要什么类型（最重要）
- `descriptor.getDependencyName()`：注入点的名字（字段名/参数名；by-name 分支会用到）
- `descriptor.isRequired()`：是否必填（决定是否允许返回 null）
- `this.resolvableDependencies`：是否命中“能注入但不是 bean”的特殊依赖
- `matchingBeans` / `findAutowireCandidates(...)` 的返回值：候选集合（by type 的结果）
- `matchingBeans.keySet()`：候选 beanName 列表
- `autowireCandidateResolver`：候选筛选器（`@Qualifier` 的关键逻辑通常在这里）

在 `determineAutowireCandidate(...)` 里重点看这些“收敛点”：

- `determinePrimaryCandidate(...)`：是否存在 `@Primary`
- `determineHighestPriorityCandidate(...)`：是否有 `@Priority` 参与 tie-break
- `descriptor.getDependencyName()` / `matchesBeanName(...)`：是否出现“按名称收敛”

### 6.3 异常 → 下一步断点（速查）

- `NoSuchBeanDefinitionException`：没有候选
  - 断点：`findAutowireCandidates(...)` 是否返回空
- `NoUniqueBeanDefinitionException`：候选>1，缩不下来
  - 断点：`determineAutowireCandidate(...)` 为什么没有选中
  - 快速修复：加 `@Qualifier`（精确）或 `@Primary`（默认实现）
- `UnsatisfiedDependencyException`：外层包装异常
  - 先展开 root cause，通常还是上面两类

> 小技巧：`doResolveDependency` 命中次数很高时，先加条件断点（例如 `descriptor.getDependencyType() == Worker.class`），再看调用栈与变量。

## 可复现闭环（基于 `SpringCoreBeansAutowireCandidateSelectionLabTest`）

把“候选收集→收敛→注入”跑成 3 个可断言结论：

1) **`@Order` 只影响集合注入顺序，不解决单依赖歧义**  
   - 断点：`resolveMultipleBeans` / `AnnotationAwareOrderComparator`  
   - 现象：集合注入有序，单注入仍 `NoUnique`
2) **单依赖收敛优先级：Qualifier > Primary > Priority > by-name**  
   - 断点：`determineAutowireCandidate`（依次观察 `primaryCandidate` / `highestPriorityCandidate`）  
   - 现象：`@Qualifier` 可显式绕开 `@Primary`
3) **泛型与 ObjectProvider 会改变收敛结果**  
   - 断点：`GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`  
   - 现象：`getIfUnique()` 可能返回 null，`orderedStream()` 遵循排序

---

## 源码与断点

- 建议优先从 Lab 的断言反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 推荐入口（覆盖：单注入候选收敛 / 候选与依赖边对照 / 可选依赖 / JSR-330）：
  - `SpringCoreBeansAutowireCandidateSelectionLabTest`
  - `SpringCoreBeansBeanGraphDebugLabTest`
  - `SpringCoreBeansOptionalInjectionLabTest`
  - `SpringCoreBeansJsr330InjectionLabTest`
- 推荐命令：
  - `mvn -pl :spring-core-beans test`
  - 或单跑：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCandidateSelectionLabTest test`

## 面试常问（依赖注入解析）

1) **为什么会 NoSuch / NoUnique？如何用“证据链”解释？**
   - 要点：先收集候选（by type）→ 再收敛（primary/qualifier/name/priority…）→ 最终注入；NoSuch 是“候选为空”，NoUnique 是“候选>1 且缩不下来”。
   - 证据链：`doResolveDependency` → `findAutowireCandidates` → `determineAutowireCandidate`；观察 `matchingBeans.keySet()`、`dependencyName`、`primaryCandidate`。

2) **`@Primary` / `@Qualifier` / by-name fallback 的边界如何讲？**
   - 要点：`@Qualifier` 是精确收敛；`@Primary` 是默认实现；by-name fallback 只在特定条件下参与收敛（容易误判）。
   - 证据链：同上，重点看 `descriptor.getDependencyName()` 与候选集合的变化。

3) **为什么 `@Order` 对单依赖注入不生效？什么时候才看排序？**
   - 要点：单依赖注入的目标是“选出唯一候选”；排序更多用于集合注入或 ordered stream。
   - 证据链：看 `determineAutowireCandidate` 的分支；对比 `ObjectProvider.orderedStream()` 的路径（见 33 章）。

推荐复习入口：`appendix/93-interview-playbook.md`（Q2/Q3）。

## 自检要点
应能够用 3 句复述：

1) 当候选不止一个时，Spring 的“候选收集→候选收敛→最终注入”主线分别在哪个方法里发生？
2) 单依赖注入里 `@Order` 为什么不生效？真正决定单依赖选择的是哪些信号？
3) 如何在 `doResolveDependency` 里用 3 个变量解释“为什么注入的是它/为什么失败”？

<!-- BOOKIFY:START -->

上一章：[第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](../part-00-guide/013-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md)

<!-- BOOKIFY:END -->

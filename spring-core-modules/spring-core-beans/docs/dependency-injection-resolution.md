# 依赖注入解析主线：从注入点需求到候选收敛

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释依赖解析如何从注入点 descriptor 出发，收集候选、过滤候选、处理多值依赖，并收敛到单值结果或失败。
    - 覆盖构造器参数、字段、方法参数、集合、Map、数组、`Provider` 和 `Optional` 的主线行为。
    - 本文讲完整解析流程，但不展开每个候选选择注解的所有边界。

    观察对象：`DependencyDescriptor`、候选 Bean、单值/多值解析和失败异常。
    主线位置：构造器解析和 `populateBean` 属性填充期间。
    对照入口：`SpringCoreBeansInjectionAmbiguityLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansInjectionPhaseLabTest`。
<!-- CHAPTER-CARD:END -->

依赖注入的本质不是“看到 `@Autowired` 就按类型塞一个对象”。容器要先把某个构造器参数、字段或方法参数变成一份需求描述，再用这份描述去 BeanFactory 中寻找候选，过滤掉不合格的 Bean，处理集合或延迟获取等特殊形态，最后对单值依赖选出唯一对象。选不出来时，正确行为通常是尽早失败。

可以把主线看成：

```text
injection point
-> DependencyDescriptor
-> resolveDependency
-> collect candidates by type
-> filter autowire candidates
-> resolve multiple or determine single candidate
-> inject value or throw exception
```

## 注入点先形成需求

构造器参数、字段和方法参数都会变成类似 `DependencyDescriptor` 的需求对象。descriptor 保存的不是一个简单 class，而是一组信号：依赖类型、泛型、是否 required、注解、依赖名称、所在类和 eager/lazy 语义。

不同注入点进入解析的时机不同：

- 构造器参数在实例化阶段解析，依赖对象会参与构造器调用。
- 字段注入在属性填充阶段解析，构造器里看不到字段注入结果。
- 方法注入也在属性填充阶段解析，解析完成后由处理器调用对应方法。

构造器注入在对象构造期间就要拿到依赖；字段和方法注入则要等到属性填充阶段。看待解析流程时，要先区分“构造时已经需要”与“创建后才注入”这两个时机。

## 候选收集：先按需求类型找 Bean

容器通常从 descriptor 的 `ResolvableType` 出发寻找候选。普通 `Worker` 会找所有可赋值给 `Worker` 的 Bean；`Handler<String>` 会保留泛型信息，在类型元数据足够时排除 `Handler<Long>`；集合、数组、Map 会解析元素类型或 value 类型。

这一步回答“哪些 Bean 在类型上可能满足需求”。它还不是最终选择。候选可能因为 `autowireCandidate=false`、qualifier 不匹配、primary 冲突、名称不匹配等原因被排除或无法收敛。

## 候选过滤：存在不等于可注入

BeanDefinition 可以声明自己不参与自动装配候选。这样的 Bean 仍然可以通过 `getBean("name")` 获取，但不会作为普通自动注入的候选。

qualifier、泛型、custom qualifier、候选解析器也都在过滤阶段发挥作用。候选选择文档会进一步展开单值规则；这里先记住：没有额外收敛信号时，多候选单值注入应该失败。

候选选择的细节属于 [autowire-candidate-selection.md](autowire-candidate-selection.md)。在依赖解析主线里，只需要记住：类型匹配只是第一步，自动装配候选还要继续通过一组规则收敛。

## 单值依赖：必须收敛到一个结果

字段 `Worker worker`、构造器参数 `Worker worker`、方法参数 `Worker worker` 这类单值依赖需要一个明确对象。容器通常按以下信号逐步收敛：

- qualifier 或泛型等强过滤信号。
- `@Primary` 或 BeanDefinition primary flag。
- 依赖名称与 bean name 或 alias 的匹配。
- suggested name 与 bean name 或 alias 的匹配。
- `@Priority` 等优先级信号。
- unique default candidate 或直接注册的 resolvable dependency。

如果没有候选，required 单值依赖通常抛出 `NoSuchBeanDefinitionException` 并被包装成注入失败异常。如果有多个候选但无法收敛，通常抛出 `NoUniqueBeanDefinitionException`。`SpringCoreBeansInjectionAmbiguityLabTest` 中两个 `Worker` 同时存在且没有 disambiguation 信号时，`refresh()` 以 `UnsatisfiedDependencyException` 失败，根因是 `NoUniqueBeanDefinitionException`。

这种 fail-fast 是刻意设计：容器不应该在多个同类型 Bean 中静默猜一个。

## 多值依赖：集合不是冲突

`List<Worker>`、`Worker[]`、`Collection<Worker>` 表示“我要所有匹配候选”，因此多个候选不是歧义，而是结果集。容器会收集所有可自动装配候选，并按比较器排序；集合顺序是另一条规则，不等同于单值冲突解决。

`Map<String, Worker>` 的常见语义是 key 为 bean name，value 为候选 Bean。Map 的 key 类型通常需要是 `String`，因为容器要把 bean name 放进去。

多值依赖仍然会过滤候选。例如 `autowireCandidate=false` 的 Bean 不应进入普通自动装配集合。排序也不等于单值选择：`@Order` 可以调整集合顺序，但不能解决 `Worker worker` 的多候选冲突。

## Optional、Provider 和延迟解析

`Optional<T>` 表示没有候选时注入 `Optional.empty()`，有候选时注入 `Optional.of(bean)`。它改变的是 required 语义，不代表延迟到业务方法调用时再解析。

`ObjectProvider<T>`、`ObjectFactory<T>` 和 JSR-330 `Provider<T>` 注入的是一个可稍后取对象的句柄。注入 consumer 时可以不立刻创建目标 Bean；调用 `getObject()` 或 `get()` 时才按当时的 BeanFactory 状态解析。provider 相关 Lab 固定了这一点：获取 consumer 不一定触发目标创建，第一次取值才会把目标拉起来。

这些 API 的细节和差异属于 [optional-and-provider-injection.md](optional-and-provider-injection.md)。在主线中只需把它们看成 descriptor 的特殊包装形态：容器解析的不是立即要一个 `T`，而是要一个表达“可选”或“延迟获取”的值。

## 注入失败看哪一层

排查依赖注入失败时，按需求侧到供给侧走：

1. 注入点 descriptor 里的 dependency type、generic type、required、qualifier、dependency name 是否符合预期。
2. BeanFactory 中按类型能找到哪些候选。
3. 哪些候选被 `autowireCandidate=false`、qualifier、泛型或条件排除。
4. 单值依赖是否有 primary、名称匹配、priority 或 default candidate 信号。
5. 依赖是否被写成集合、Provider 或 Optional，从而改变了失败时机。
6. 异常根因是没有候选，还是多候选无法收敛。

某些 provider API 不会在多候选时抛异常，而是把“非唯一”表达成 null 或空结果。排障时必须先确认注入点要求的语义。

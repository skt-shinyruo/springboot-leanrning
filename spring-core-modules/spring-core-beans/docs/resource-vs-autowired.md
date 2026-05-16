# Resource 与 Autowired：name-first 和 type-first 的差异

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 `@Resource` 与 `@Autowired` 的本质差异：前者默认 name-first，后者默认 type-first。
    - 覆盖 `@Resource` 默认名称、显式 name、名称查找、类型 fallback，以及 `@Autowired` 的类型、qualifier 和名称收敛。
    - 读完后应能解释同一组 Bean 下两个注解为什么可能注入不同对象。

    观察对象：字段名、bean name、候选类型、qualifier 和注解处理器。
    主线位置：属性填充阶段的注入点解析。
    对照入口：`SpringCoreBeansResourceInjectionLabTest`、`SpringCoreBeansResourceResolutionLabTest`。
<!-- CHAPTER-CARD:END -->

`@Resource` 和 `@Autowired` 都能把容器里的对象注入到字段或方法上，但它们的默认思路不同：

- `@Resource` 先按名称定位 Bean。
- `@Autowired` 先按类型收集候选，再用 qualifier、primary、priority、名称等规则收敛。

因此同一组 BeanDefinition 下，两个注解可能得到不同结果。排障时不要把它们都简化成“自动注入”。

## 基础设施先决条件

这两个注解都依赖 BeanPostProcessor 在属性填充阶段处理。`@Resource` 通常由 `CommonAnnotationBeanPostProcessor` 处理，`@Autowired` 由自动装配处理器处理。没有这些基础设施，注解只是类上的元数据。

`SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored` 在 `GenericApplicationContext` 中不注册 annotation config processors，`@Resource` 字段保持 null。注册 `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)` 后，`@Resource` 才开始生效。

所以第一个排障问题不是 name 还是 type，而是对应处理器是否已经进入 BPP 链。

## `@Resource`：默认字段名就是资源名

没有显式 name 时，`@Resource` 通常使用字段名或 setter 属性名作为默认资源名。例如：

```java
@Resource
private Dependency dependency;
```

默认会先查名为 `dependency` 的 Bean。`SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst` 中容器同时有 `dependency` 和 `otherDependency` 两个同类型 Bean，字段 `dependency` 稳定注入 id 为 `main` 的 Bean，没有因为同类型多候选而走向歧义。

显式 name 更直接：

```java
@Resource(name = "otherDependency")
private Dependency explicitlyNamedDependency;
```

这会优先查名为 `otherDependency` 的 Bean。Lab 中该字段注入 id 为 `other` 的 Bean。

## 名称查找失败后的类型 fallback

`@Resource` 的 name-first 不表示它永远只按名称。默认名称找不到时，Spring 的处理器可能退到类型解析，尝试按字段或方法参数类型寻找候选。

这个 fallback 很容易造成误判：

- 如果类型只有一个候选，`@Resource` 可能仍然注入成功。
- 如果类型有多个候选，就可能变成类型歧义。
- 如果你以为字段名会决定结果，但实际没有同名 Bean，最终结果可能来自类型 fallback。

因此使用 `@Resource` 排障时，先确认显式或默认 name 是否真的存在。不要只看字段类型。

## `@Autowired`：先看类型，再用信号收敛

`@Autowired` 的默认入口是类型需求。字段 `private Worker worker` 会先找所有 `Worker` 候选；如果只有一个候选，直接注入；如果多个候选，再看 qualifier、primary、dependency name、suggested name、priority、default candidate 等信号。

字段名也可能参与 `@Autowired`，但它不是第一步。它发生在类型候选已经收集出来之后，并且在 primary 之后尝试匹配 bean name 或 alias；在 Spring 6.2.x 的单值收敛中，它早于 `@Priority` 和 unique default candidate。也就是说，字段名不是 type-first 的入口，但也不是所有规则之后才出现的最后兜底。

这就是 type-first 与 name-first 的差别：

- `@Resource` 字段名命中时，即使同类型多个 Bean，也先按这个名字取。
- `@Autowired` 先形成类型候选集合，再逐步收敛；字段名只是收敛信号之一。

## qualifier 与 name 不是同一种语义

`@Autowired @Qualifier("secondaryWorker")` 表达的是“在类型候选中选择 qualifier 匹配的候选”。这个值可能等于 bean name，也可能匹配 BeanDefinition qualifier 元数据或自定义 qualifier。

`@Resource(name = "secondaryWorker")` 表达的是“先按这个 Bean 名称定位资源”。它更接近命名查找。

两者值相同，不代表算法相同。遇到代理、别名、primary、泛型、自定义 qualifier 时，差异会更明显。

## 什么时候会注入不同对象

假设容器里有：

```text
fastWorker: Worker
slowWorker: Worker @Primary
```

如果字段是：

```java
@Resource
private Worker fastWorker;
```

name-first 会优先找 `fastWorker`。

如果字段是：

```java
@Autowired
private Worker fastWorker;
```

type-first 会先看到两个 `Worker`，然后 primary 的 `slowWorker` 可能先胜出，字段名 fallback 没机会覆盖 primary。

如果你希望 `@Autowired` 明确选择 fast，应使用：

```java
@Autowired
@Qualifier("fastWorker")
private Worker worker;
```

这不是风格问题，而是解析规则不同。

## 排障顺序

排查 `@Resource`：

1. `CommonAnnotationBeanPostProcessor` 是否注册。
2. 注入点显式 name 是什么；没有显式 name 时默认字段名或属性名是什么。
3. 容器中是否存在同名 Bean 或 alias。
4. 名称没命中时，类型 fallback 找到几个候选。
5. 最终对象是否经过代理，导致类型看起来不同。

排查 `@Autowired`：

1. 自动装配处理器是否注册。
2. descriptor 的 dependency type 和 generic type 是什么。
3. 按类型有哪些候选，哪些被排除。
4. qualifier 和 primary 是否改变单值结果。
5. dependency name 或 suggested name 是否匹配了 bean name 或 alias。
6. priority、default candidate 或 resolvable dependency 是否继续参与收敛。

`SpringCoreBeansResourceResolutionLabTest` 是资源相关实验的聚合入口；其中 `SpringCoreBeansResourceInjectionLabTest` 固定了 `@Resource` name-first 的最小现象。把这个现象和自动装配候选选择 Lab 对照看，能避免把两套规则混在一起。

# DependencyDescriptor 与 InjectionPoint：注入需求的元数据

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释注入点元数据保存了什么，以及为什么它是依赖解析的需求侧。
    - 覆盖类型、泛型、required、注解、字段/方法参数、eager/lazy、containing class 和依赖名称。
    - 读完后应能用 descriptor dump 判断“这个注入点到底向容器要什么”。

    观察对象：`DependencyDescriptor`、`InjectionPoint`、字段和方法参数元数据。
    主线位置：自动装配处理器发现注入点之后，BeanFactory 解析候选之前。
    对照入口：`SpringCoreBeansDependencyDescriptorMetadataLabTest`、`SpringCoreBeansProgrammaticResolveDependencyLabTest`、`DependencyDescriptorDumperLabTest`。
<!-- CHAPTER-CARD:END -->

依赖解析有两侧：供给侧是 BeanDefinition 和已创建 Bean，需求侧是注入点。`DependencyDescriptor` 就是 Spring 把需求侧带入解析算法的主要对象。看懂 descriptor，才能知道容器不是“随便找一个类型”，而是在回答一个带有类型、泛型、名称、注解和 required 语义的问题。

`InjectionPoint` 是更通用的注入点视图，常用于把当前字段或方法参数暴露给工厂方法、回调或调试工具。`DependencyDescriptor` 可以看作依赖解析场景里更具体、更可执行的需求描述。

## descriptor 里有什么

一个实际注入点通常包含这些信息：

| 元数据 | 作用 |
| --- | --- |
| dependency type | 原始依赖类型，例如 `Worker` |
| resolvable type | 带泛型的类型，例如 `Handler<String>` |
| required | 没有候选时是否失败 |
| annotations | `@Qualifier`、`@Lazy`、`@Nullable` 等注入点注解 |
| field 或 method parameter | 注入点来自字段、构造器参数还是方法参数 |
| dependency name | 字段名或可发现的参数名，可参与名称收敛 |
| containing class | 注入点所在类，用于泛型解析和上下文判断 |
| eager/lazy | 解析时是否允许急切初始化候选，或是否需要延迟代理/Provider |

`SpringCoreBeansDependencyDescriptorMetadataLabTest` 用字段 `secondaryWorker` 和构造器参数做对照：字段 descriptor dump 中能看到 kind 是 Field、dependencyName 是 `secondaryWorker`，并保留 `@Qualifier("secondaryWorker")`；构造器参数 descriptor dump 中 kind 是 MethodParameter，也能看到 qualifier。

同一个测试还证明泛型签名会进入 `ResolvableType`：`Handler<String>` 不只是 `Handler.class`，descriptor 能保留 `<java.lang.String>`，后续候选选择才有机会区分 `Handler<String>` 和 `Handler<Long>`。

## 字段和方法参数的差异

字段注入天然有字段名，所以 dependency name 通常稳定来自字段名。构造器参数和方法参数的名称需要参数名发现机制；如果编译时没有保留参数名，按名称收敛可能拿不到预期信号。

这就是为什么“字段名等于 bean name，所以注入成功”的规则不能无脑迁移到构造器参数。构造器参数仍然有类型、泛型和注解，但 dependency name 是否可用取决于参数名发现。

`SpringCoreBeansProgrammaticResolveDependencyLabTest` 直接构造字段 descriptor 并调用 `resolveDependency`：字段名 `secondaryWorker` 可以在多候选时通过 by-name fallback 收敛；字段名 `worker` 不匹配任何 bean name 且没有 qualifier/primary 时，会抛出 `NoUniqueBeanDefinitionException`。

## required 是需求语义，不是候选状态

required 描述的是注入点能否接受“没有候选”。`new DependencyDescriptor(field, true)` 表示这个需求必须满足；`@Autowired(required = false)`、`@Nullable`、`Optional<T>` 等会改变这个语义或包装解析结果。

注意 required 不解决多候选冲突。一个非 required 注入点可以接受 0 个候选，但不代表多个候选时容器可以猜一个。多候选是否失败取决于注入点类型和使用的 API，例如 `ObjectProvider#getIfUnique()` 会把非唯一表达成 null，而普通单值 required 依赖通常失败。

## 注解是解析信号

注入点上的注解会被候选解析器读取。典型例子是 `@Qualifier("secondaryWorker")`：它不是运行时装饰，而是过滤候选的条件。BeanDefinition 上的 qualifier 元数据、bean name 或注解属性可以与它匹配。

`SpringCoreBeansProgrammaticResolveDependencyLabTest#resolveDependency_qualifierAnnotation_canNarrowCandidates_programmatically` 说明即使不经过真实字段注入，也可以构造带 qualifier 的 descriptor，调用 `DefaultListableBeanFactory#resolveDependency` 得到被 qualifier 收敛后的候选。

## eager、lazy 与 containing class

descriptor 还携带解析时机相关信号。普通单值注入通常需要立即解析依赖，可能触发候选 Bean 创建；Provider、ObjectFactory 或 lazy injection point 则可能把解析推迟到后续调用，或注入一个代理。

containing class 对泛型解析很关键。假设父类声明 `Repository<T>`，子类绑定 `T` 为 `User`，容器需要知道当前注入点属于哪个类，才能把类型变量解析成实际类型。排查泛型候选不符合预期时，不能只看 raw class。

## 如何观察 descriptor

本模块提供的 `DependencyDescriptorDumperLabTest` 和 `SpringCoreBeansDependencyDescriptorMetadataLabTest` 演示了一个实用方法：

```text
Field or Constructor/MethodParameter
-> new DependencyDescriptor(...)
-> DependencyDescriptorDumper.dump(...)
```

如果要进一步验证“这个 descriptor 会解析到谁”，可以像 `SpringCoreBeansProgrammaticResolveDependencyLabTest` 一样调用：

```text
DefaultListableBeanFactory#resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)
```

这比猜注解行为更可靠。它把真实解析入口暴露出来，并能看到被记录到 `autowiredBeanNames` 的结果。

## 排障读法

看 descriptor 时，优先回答这几个问题：

1. 它是 Field 还是 MethodParameter。
2. dependency type 和 resolvable type 是否符合预期，泛型是否丢失。
3. dependency name 是什么，是否依赖参数名发现。
4. required 是否符合你想要的失败语义。
5. 注解列表里是否真的有 qualifier、lazy、nullable 等信号。
6. containing class 是否能支撑泛型变量解析。

一旦这些问题清楚，依赖解析就从“容器为什么这么选”变成了“这个需求与哪些供给信号匹配”。

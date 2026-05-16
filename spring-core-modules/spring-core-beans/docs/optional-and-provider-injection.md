# 可选依赖与 Provider：缺失、延迟和重复获取

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文比较 `required=false`、`@Nullable`、`Optional<T>`、`ObjectProvider<T>`、`ObjectFactory<T>` 和 JSR-330 `Provider<T>`。
    - 重点说明没有候选、多候选、lazy 目标、重复 lookup、stream/ifAvailable 支持和 Bean 创建触发时机。
    - 读完后应能判断某个依赖应该“启动时必须有”“没有也行”还是“用到时再找”。

    观察对象：可选注入值、延迟解析句柄和目标 Bean 创建时机。
    主线位置：依赖解析阶段和业务代码后续调用 provider 的阶段。
    对照入口：`SpringCoreBeansOptionalInjectionLabTest`、`SpringCoreBeansJsr330InjectionLabTest`、`SpringCoreBeansLazyLabTest`。
<!-- CHAPTER-CARD:END -->

依赖注入不只有“必须注入一个 Bean”这一种语义。真实代码里常见三类需求：

- 没有候选也能启动，只是某个功能关闭。
- consumer 可以先创建，真正用到依赖时再查找。
- 每次使用都希望按当前 scope 或当前容器状态重新获取。

Spring 用 `required=false`、`@Nullable`、`Optional<T>`、`ObjectProvider<T>`、`ObjectFactory<T>` 和 JSR-330 `Provider<T>` 表达这些差异。它们看起来都能处理“依赖可能不存在”，但失败时机、是否延迟、是否重复 lookup、API 能力并不一样。

## 对照表

| 写法 | 没有候选 | 多候选 | 是否延迟解析 | 重复获取 | 额外能力 |
| --- | --- | --- | --- | --- | --- |
| `@Autowired(required=false)` | 跳过注入，字段保持默认值或方法不调用 | 仍可能失败 | 否 | 否 | 只改变 required 语义 |
| `@Nullable T` | 注入 null | 仍可能失败 | 否 | 否 | 可用于参数或字段 |
| `Optional<T>` | `Optional.empty()` | 仍按单值规则处理 | 否 | 否 | 用类型表达可选 |
| `ObjectFactory<T>` | 调用时按普通规则决定 | 调用时可能失败 | 是 | 是 | 最小延迟获取接口 |
| `ObjectProvider<T>` | `getIfAvailable()` 可返回 null | `getIfUnique()` 可返回 null | 是 | 是 | `ifAvailable`、`stream`、`orderedStream` 等 |
| `Provider<T>` | 调用 `get()` 时按普通规则决定 | 调用时可能失败 | 是 | 是 | JSR-330 标准接口 |

这张表的重点是：可选和延迟是两件事。`Optional<T>` 让“没有候选”变成值语义，但它通常在创建 consumer 时就解析完成；Provider 系 API 注入的是获取句柄，可以把目标解析推迟到业务调用时。

## `required=false`：跳过当前注入点

`@Autowired(required=false)` 最直接。字段注入没有候选时，字段保持 null；方法注入没有候选时，方法可能不被调用。它适合兼容旧代码，但需求语义隐藏在注解属性里，调用方仍然要处理 null。

它不代表“有多个候选时随便选一个”。如果类型匹配到多个 Bean 且没有 primary、qualifier 等收敛信号，仍可能失败。

`SpringCoreBeansOptionalInjectionLabTest` 中 `Consumer` 的 `@Autowired(required = false)` 字段在没有 `MissingDependency` 时为 null；加入该 Bean 后字段能正常注入。

## `@Nullable`：把缺失表达成 null 参数

`@Nullable` 常用于构造器或方法参数，语义是当前依赖允许为 null。它比 `required=false` 更贴近参数级语义，但仍要求调用方处理 null。

它也不是延迟解析。consumer 创建时就会解析这个参数；只是没有候选时传入 null，而不是让容器启动失败。

## `Optional<T>`：把缺失表达成值

`Optional<T>` 把缺失候选变成 `Optional.empty()`。相比 null，它让 consumer 的构造器签名直接表达“这个依赖可选”。

`SpringCoreBeansOptionalInjectionLabTest` 展示了两种状态：没有 `MissingDependency` 时，构造器参数 `Optional<MissingDependency>` 为空；注册该 Bean 后，Optional 包含同一个容器 Bean。

注意 `Optional<T>` 仍是创建 consumer 时解析。目标如果是非 lazy singleton，可能已经在 refresh 中创建；即使目标是 lazy，解析 Optional 时也可能触发目标创建，具体取决于注入点和候选解析是否需要实例化。

## `ObjectFactory<T>`：最小延迟获取

`ObjectFactory<T>` 只有核心获取能力：调用 `getObject()` 时再向容器取 `T`。它适合表达“consumer 可以先创建，但依赖使用时再拿”。

每次 `getObject()` 都是一次 lookup。对 singleton，通常返回同一个共享实例；对 prototype，则可能每次创建新实例；对自定义 scope，则取决于 scope 当前上下文。

它没有 `ObjectProvider` 那些可选和流式便利方法。如果你需要 `getIfAvailable()`、`ifAvailable()`、`stream()` 或 `orderedStream()`，优先使用 `ObjectProvider<T>`。

## `ObjectProvider<T>`：Spring 的延迟与可选工具

`ObjectProvider<T>` 是 Spring 提供的增强版 provider。常用语义包括：

- `getObject()`：必须拿到一个对象；没有或不唯一时按普通单值规则失败。
- `getIfAvailable()`：没有候选时返回 null 或使用默认 supplier。
- `ifAvailable()`：有候选时执行回调。
- `getIfUnique()`：只有唯一候选时返回；没有或不唯一时返回 null。
- `stream()` / `orderedStream()`：按类型获取多个候选，后者尊重排序。

`ObjectProvider` 的几个常用方法把“可选”与“延迟”区分得很清楚：`getIfUnique()` 可以在非唯一时返回 null，`orderedStream()` 可以按顺序遍历多个候选，`ifAvailable()` 则只在有候选时执行回调。

`ObjectProvider` 注入本身通常不会创建目标 Bean；调用获取方法时才解析目标。

## JSR-330 `Provider<T>`：标准延迟句柄

`jakarta.inject.Provider<T>` 是 JSR-330 标准接口，方法是 `get()`。Spring 支持把它作为注入点类型，并可配合 `@Inject`、`@Named` 使用。

`SpringCoreBeansJsr330InjectionLabTest` 直接比较了几种方式：

- `@Inject @Named("fastWorker") Provider<Worker>` 注入 consumer 后，lazy 的 `fastWorker` 没有创建。
- `@Autowired @Qualifier("fastWorker") ObjectProvider<Worker>` 行为类似，也等到调用获取方法才触发目标创建。
- `@Inject @Named("fastWorker") Worker` 直接注入则在 refresh/consumer 创建期间解析依赖，触发 lazy 目标创建。

这说明 Provider 的关键价值是延迟 lookup，不只是可选。

## 与 `@Lazy` 的关系

`@Lazy` 可以作用在 BeanDefinition 上，也可以作用在注入点上。BeanDefinition lazy 表示 refresh 预实例化阶段不主动创建该 singleton；但如果有一个非 lazy consumer 直接依赖它，依赖解析仍会把它带出来。

`SpringCoreBeansLazyLabTest#lazyInitDoesNotHelpIfAConsumerEagerlyDependsOnTheBean` 证明 lazy 目标被普通 eager consumer 依赖时仍会创建。`lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse` 则证明注入点 `@Lazy` 会注入代理，直到第一次方法调用才创建目标。

Provider 与 lazy injection point 都能推迟创建，但形态不同：

- Provider 注入的是显式获取句柄，业务代码调用 `get()`。
- `@Lazy` 注入点注入的是代理，业务代码像调用普通依赖一样调用方法。

## 选择建议

如果依赖是必需组件，用普通构造器注入，让容器启动时失败。

如果依赖没有也能工作，优先用 `Optional<T>` 或 `ObjectProvider#getIfAvailable()`，避免裸 null 在业务代码里扩散。

如果依赖创建昂贵、可能形成启动时循环、或需要按 scope 重复获取，用 `ObjectProvider<T>` 或 `Provider<T>`。

如果只是想让 lazy singleton 不被 eager consumer 立刻创建，使用 provider 或注入点 `@Lazy`，不要只在目标 BeanDefinition 上设 lazy。

排障时问两件事就够：consumer 创建时是否应该解析目标？没有或不唯一时应该失败、返回空，还是跳过逻辑？选用的 API 必须把这两个答案表达清楚。

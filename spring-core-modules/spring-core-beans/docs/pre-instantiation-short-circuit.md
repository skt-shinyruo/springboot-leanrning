# 实例化前短路：构造器为什么可以不执行

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 如何在构造器执行前返回替代对象。
    - 重点区分实例化前短路和普通 after-init 包装：前者跳过默认实例化，后者包装已经创建并初始化过的对象。
    - 读完后应能判断“构造器没有执行但容器里有 Bean”是否来自 before-instantiation 分支。

    观察对象：实例化前 BeanPostProcessor、替代对象、默认创建链路跳过。
    主线位置：`createBean` 内部，`doCreateBean` 和构造器调用之前。
    对照入口：`SpringCoreBeansPreInstantiationLabTest`。
<!-- CHAPTER-CARD:END -->

Spring 创建 Bean 时，构造器并不是不可绕过的第一步。进入 `AbstractAutowireCapableBeanFactory#createBean` 后，容器会先给 `InstantiationAwareBeanPostProcessor` 一个机会：如果 `postProcessBeforeInstantiation(beanClass, beanName)` 返回非 null 对象，容器就把这个对象当作当前 Bean 的结果继续处理，而不再执行默认的构造器实例化、属性填充和 init 回调链路。

这个机制很少由业务代码直接使用，但它解释了一个重要现象：某个 BeanDefinition 指向的类构造器没有执行，甚至构造器会抛异常，容器仍然能暴露一个可用 Bean。

## 短路点在哪里

普通创建链路是：

```text
createBean
-> doCreateBean
-> createBeanInstance
-> populateBean
-> initializeBean
```

实例化前短路插在 `createBean` 进入 `doCreateBean` 之前：

```text
createBean
-> postProcessBeforeInstantiation returns object?
   -> yes: apply after-init processors, expose returned object
   -> no: doCreateBean normal path
```

`postProcessBeforeInstantiation` 的参数只有目标 class 和 bean name，因为此时 raw instance 尚不存在。处理器要么返回 null 表示不介入，要么返回一个替代对象，例如代理、stub、特殊工厂结果或其他可作为 Bean 暴露的对象。

## 为什么构造器会被跳过

一旦 before-instantiation 返回非 null，容器认为当前 Bean 已经有对象可用，就不再调用目标类构造器，也不会对目标类实例做属性填充。原因很直接：默认实例根本没有创建出来，`populateBean` 没有目标可填充。

`SpringCoreBeansPreInstantiationLabTest` 的对照很明确：

- 不注册短路处理器时，`FailingService` 构造器执行一次并抛出异常，`refresh()` 失败。
- 注册 `ShortCircuitingPostProcessor` 后，处理器针对 `FailingService.class` 返回一个 JDK proxy，构造器调用次数保持 0，`getBean(GreetingService.class)` 得到可调用的 `surrogate`。

这不是“吞掉构造器异常”，而是构造器从未进入。

## 与普通 after-init 包装的差异

after-init 包装发生在 `initializeBean` 的末尾：

```text
raw instance already exists
-> properties injected
-> aware callbacks
-> init callbacks
-> postProcessAfterInitialization may return wrapper
```

所以普通 after-init 包装有几个特征：

- 目标类构造器已经执行。
- 字段、setter、方法注入通常已经完成。
- `@PostConstruct`、`afterPropertiesSet` 或 init method 已经有机会运行。
- 包装器可以委托真实 target，因为 target 确实存在。

实例化前短路则不同：

- 目标类构造器不执行。
- 默认属性填充和 init 回调不作用在目标类实例上。
- 返回对象可以完全不委托目标类。
- 更适合“用代理或替代对象代表这个 Bean”的极早期分支。

after-init 替换和 before-instantiation 替换属于两条不同分支：前者发生在对象已经完成默认创建和初始化之后，后者发生在构造器之前。本文关注的是后者，因为它解释了为什么构造器可以完全不执行。

## after-init 处理仍可能参与

Spring 在实例化前短路后，通常仍会对返回对象应用 `postProcessAfterInitialization`。这给自动代理等基础设施一个统一出口：无论对象来自普通创建还是短路返回，都可以经过 after-init 后处理。

但不要把它误解为“短路对象也会完整初始化”。默认的 `populateBean`、Aware 回调和 init callbacks 针对的是普通创建出来的 bean instance；短路返回对象绕过的是这条默认生命周期。

## 排障时怎么判断

遇到“类上有 BeanDefinition，但构造器没打到断点”的情况，按下面顺序看：

1. Bean 是否真的被请求或预实例化；lazy Bean 没创建时构造器也不会执行。
2. `createBean` 里是否存在 `postProcessBeforeInstantiation` 返回非 null。
3. 返回对象的类型是否满足调用方按类型获取或注入的需求。
4. 是否有 after-init BPP 又包装了这个短路对象。
5. 目标类上的字段注入、`@PostConstruct`、init method 是否被错误地假设已经执行。

实例化前短路的核心判断只有一句：容器暴露的是处理器提前交出的对象，不是 BeanDefinition 指向 class 的默认实例。

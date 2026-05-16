# 后处理器总览：BFPP、BDRPP、BPP、IABPP、MBDPP、DABPP

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 Spring 容器里几类后处理器各自能改什么、什么时候运行、会不会把 bean 提前实例化。
    - 重点放在“定义层修改”与“实例层修改”的边界，以及它们在 `refresh()` 主线里的位置。
    - 读完后应该能分清 BFPP、BDRPP、BPP、InstantiationAwareBPP、MergedBeanDefinitionPostProcessor 和 DestructionAwareBPP 的职责。

    观察对象：容器扩展点、定义变更和实例变更。
    主线位置：BeanDefinition 加载之后、普通 bean 预实例化之前、单个 bean 创建过程中、以及销毁阶段。
    对照入口：`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`、`SpringCoreBeansRegistryPostProcessorLabTest`、`SpringCoreBeansPostProcessorOrderingLabTest`。
<!-- CHAPTER-CARD:END -->

Spring 的后处理器不是“多写几个回调”这么简单。它们分布在 refresh 的不同窗口，改的对象也不同：有的只改 `BeanDefinition`，有的只改实例，有的只负责缓存合并后的元数据，有的只在销毁前收尾。

最关键的分界线是：

- 定义层后处理器，处理的是注册表里的元数据。
- 实例层后处理器，处理的是已经创建出来的对象。
- 越早执行的后处理器，越容易因为调用 `getBean()` 让别的 bean 提前实例化，而且往往还没进入完整的实例后处理链。

## 先看 refresh 里的位置

`ApplicationContext#refresh()` 在加载完定义后，会先跑注册表和工厂级后处理器，再注册普通 `BeanPostProcessor`，最后才预实例化非懒 singleton。

这意味着：

1. `BeanDefinitionRegistryPostProcessor` 先于普通 `BeanFactoryPostProcessor`。
2. 普通 `BeanFactoryPostProcessor` 先于 `BeanPostProcessor` 的注册。
3. `BeanPostProcessor` 只会影响随后创建的 bean 实例。
4. 销毁相关后处理器只在容器关闭时才有机会介入。

## 各接口到底能改什么

| 接口 | 能改什么 | 运行时机 | 会不会把别的 bean 提前实例化 |
| --- | --- | --- | --- |
| `BeanDefinitionRegistryPostProcessor` | 新增、删除、替换 `BeanDefinition` | 定义加载后，普通 BFPP 前 | 会，若实现里主动查 bean |
| `BeanFactoryPostProcessor` | 修改已有 `BeanDefinition` 的属性值、scope、lazy 等元数据 | BDRPP 之后、BPP 注册之前 | 会，若实现里主动查 bean |
| `BeanPostProcessor` | 修改或替换已创建实例 | 每个 bean 的初始化前后 | 有条件，会，若实现里自己去查 bean |
| `InstantiationAwareBeanPostProcessor` | 在实例化前短路，或在属性填充前干预 | `createBean()` 的实例化与填充阶段 | 有条件，会，若实现里自己去查 bean |
| `MergedBeanDefinitionPostProcessor` | 读取或缓存合并后的运行时定义信息 | merged `RootBeanDefinition` 形成后、实例创建前 | 一般不会，除非实现自己查 bean |
| `DestructionAwareBeanPostProcessor` | 在销毁前做清理、记录、释放资源 | 容器关闭或 bean 销毁时 | 不会影响创建，但实现里仍可主动查 bean |

这里的“会不会提前实例化”指的是对**其他 bean** 的影响。接口本身并不会自动创建别的 bean，但实现里如果写了 `getBean()`，或者自己的依赖在这个阶段被解析，容器就会提前走创建链。

## 定义层：BDRPP 和 BFPP

`BeanDefinitionRegistryPostProcessor` 是最早的扩展点之一。它能直接操作 registry，所以适合做“把一个定义补出来”这类事，比如注册额外 bean、改掉某个定义的属性值、或者基于扫描结果再补定义。

`SpringCoreBeansRegistryPostProcessorLabTest` 里，BDRPP 先注册 `registeredBean`，随后普通 `BeanFactoryPostProcessor` 又把它的 `origin` 改成 `modified-by-bfpp`。这个顺序说明了两件事：

- BDRPP 负责把定义带进容器。
- BFPP 还能在实例化前继续改这份定义。

`BeanFactoryPostProcessor` 只改定义，不改普通实例。它通常用于占位符解析、属性值修正、scope/lazy 等工厂级元数据调整。`SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` 证明了一个典型边界：如果 BFPP 的 `@Bean` 不是 `static`，Spring 需要先实例化配置类才能调用工厂方法，这会让配置类本身比预期更早出现；而 `static @Bean` 可以绕开这个早期实例化。

## 实例层：BPP 家族

`BeanPostProcessor` 进入的是实例窗口。bean 已经被实例化，属性也已经填充得差不多，接下来才进入初始化前后处理。

它最常见的用途有两个：

1. 在初始化前修补 bean 状态。
2. 在初始化后替换暴露对象，例如返回代理。

`InstantiationAwareBeanPostProcessor` 比普通 BPP 更早，它可以在真正执行构造器之前返回一个替代对象，因此属于“实例化前短路”机制。这个接口的价值不在于“再加工一个对象”，而在于“甚至不用创建原始对象就先改写结果”。

`MergedBeanDefinitionPostProcessor` 介于定义和实例之间。它看到的是合并后的运行时定义，常用来缓存注入点、注解元数据、候选信息之类的内容。它更像“为后续实例创建准备上下文”，而不是直接改最终对象。

`DestructionAwareBeanPostProcessor` 只关心销毁前的最后一刻。它不参与创建，也不参与代理生成，但它能在 bean 真正消失前做资源回收和收尾记录。

## BDRPP 和 BFPP 的早期实例化风险

`SpringCoreBeansRegistryPostProcessorLabTest` 里有一个刻意的实验：在 BDRPP/BFPP 阶段调用 `getBean()`，`earlyTarget` 会比正常流程更早创建，而且错过后续 `BeanPostProcessor`。`lateTarget` 则在 BPP 注册完成后再创建，所以能被完整处理。

这类风险说明：

- 定义层后处理器适合“改定义”，不适合“顺手拿一个 bean 用一下”。
- 一旦在这个阶段取 bean，容器状态就可能被提前推进。
- 你拿到的对象可能还没有经过完整实例后处理链。

## 排序不是细节

`SpringCoreBeansPostProcessorOrderingLabTest` 证明了后处理器排序规则：

- `PriorityOrdered` 先于 `Ordered`。
- `Ordered` 先于无序实现。
- 同组内按 `getOrder()` 升序。

这对 BFPP 和 BPP 都成立，但它们影响的是不同阶段的对象。排序不是“谁先打印日志”，而是“谁先有机会改掉后续看到的定义或实例”。

## 本模块的观察入口

运行这些实验时，可以重点看三类现象：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansStaticBeanFactoryPostProcessorLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRegistryPostProcessorLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test
```

- `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`：static BFPP 不会逼着配置类提前实例化。
- `SpringCoreBeansRegistryPostProcessorLabTest`：BDRPP 能补定义，BFPP 还能继续改定义；在这个阶段取 bean 会让它跳过后续 BPP。
- `SpringCoreBeansPostProcessorOrderingLabTest`：后处理器排序遵循 PriorityOrdered、Ordered、无序三段式。


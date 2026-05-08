# Post-Processor Ordering：处理器排序规则
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这一页只回答：PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？
    - 最短命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test`
    - 相邻主题只做跳转，不在本页重复展开。

    观察对象：PriorityOrdered、Ordered、普通处理器分组排序以及注册窗口对行为的影响。
    主线位置：容器与注册。
    对照入口：`SpringCoreBeansPostProcessorOrderingLabTest`。
<!-- CHAPTER-CARD:END -->

## 排序先分组，再看 order 值

后处理器排序不是把所有对象放进一个列表后统一比较数字。Spring 先分组，再在组内排序：

```text
PriorityOrdered -> Ordered -> unordered
```

只有进入 `PriorityOrdered` 或 `Ordered` 组的处理器，较小的 order 值才会更早运行。无序组不因为类上写了一个数字就自动进入有序组。

## BFPP 的排序

BFPP 在 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 中执行。BDRPP 的 registry 回调先收敛定义，然后常规 BFPP 才修改已有定义。每一类内部仍按 `PriorityOrdered`、`Ordered`、无序分组处理。

`SpringCoreBeansPostProcessorOrderingLabTest` 里的 BFPP 顺序验证了两点：

- `PriorityBfpp` 先于 `OrderedBfpp`，`OrderedBfpp` 先于 `UnorderedBfpp`。
- 同在 `Ordered` 组内时，`order=0` 的 `OrderedValueBfpp` 先于 `order=10` 的处理器。

这意味着如果两个 BFPP 都要改同一个 `BeanDefinition`，先确认它们属于哪个组，再看 `getOrder()` 返回值。

## BPP 的排序

BPP 在 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 中被创建并注册到 bean 创建链路。注册顺序会影响后续 `postProcessBeforeInitialization` 和 `postProcessAfterInitialization` 的应用顺序。

Lab 里的 BPP 顺序同样是：

```text
bpp:priority
bpp:ordered
bpp:unordered
```

同在 `Ordered` 组内时，事件是：

```text
bpp:ordered0
bpp:ordered10
```

所以实例级处理器也遵循同一条基本规则：先分组，组内按较小 order 值靠前。

## @Order 和 Ordered 不是同一件事

`@Order` 注解可以被 `AnnotationAwareOrderComparator` 识别，但这不等于某个 BPP 会自动进入 Spring 注册逻辑里的 `Ordered` 组。`registerBeanPostProcessors` 分组时，核心判断是处理器是否参与对应排序机制，例如实现 `PriorityOrdered` 或 `Ordered`。

`SpringCoreBeansPostProcessorOrderingLabTest` 特意把 beanFactory 的 dependency comparator 设为 `AnnotationAwareOrderComparator.INSTANCE`，然后注册两个只带 `@Order`、不实现 `Ordered` 的 BPP：

```text
bpp:annotated10
bpp:annotated0
```

结果仍按注册顺序执行，而不是按注解里的 0、10 排序。这个用例说明：`@Order` 能被比较器理解，不代表它会改变 BPP 在自动注册流程中的分组。

## 手工注册为什么另算

自动发现并注册的 BPP 会经过 `PostProcessorRegistrationDelegate#sortPostProcessors` 等逻辑。手工调用 `ConfigurableBeanFactory#addBeanPostProcessor` 是直接把处理器追加到工厂的 BPP 列表，语义上已经绕过这轮自动分组排序。

手工注册的边界放在 [programmatic-bpp-registration.md](programmatic-bpp-registration.md)。排查时要先确认 BPP 是作为 bean definition 被 Spring 自动发现，还是被代码直接 add 到工厂里。

## 源码阅读顺序

建议按这条线读：

1. `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：看 BFPP/BDRPP 的分组执行。
2. `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：看 BPP 的分组注册。
3. `PostProcessorRegistrationDelegate#sortPostProcessors`：看组内如何调用比较器排序。
4. `PriorityOrdered`：确认最高优先组的 marker 和 `getOrder()` 语义。
5. `Ordered`：确认普通有序组的 marker 和较小值优先规则。
6. `AnnotationAwareOrderComparator`：理解注解 order 被比较器识别的范围，不把它误读成 BPP 分组入口。

## 用本模块怎么验证

运行：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPostProcessorOrderingLabTest test
```

观察五个用例：

- BFPP 分组顺序：`bfpp:priority`、`bfpp:ordered`、`bfpp:unordered`。
- BFPP 组内顺序：`bfpp:ordered0` 早于 `bfpp:ordered10`。
- BPP 分组顺序：`bpp:priority`、`bpp:ordered`、`bpp:unordered`。
- BPP 组内顺序：`bpp:ordered0` 早于 `bpp:ordered10`。
- 仅带 `@Order` 的 BPP 仍按注册顺序：`bpp:annotated10`、`bpp:annotated0`。

## 相邻主题

- [post-processors-overview.md](post-processors-overview.md)：三类后处理器的阶段边界。
- [beanfactory-post-processors.md](beanfactory-post-processors.md)：BFPP 的定义修改窗口。
- [beanpost-processors.md](beanpost-processors.md)：BPP 的实例创建窗口。
- [programmatic-bpp-registration.md](programmatic-bpp-registration.md)：手工注册 BPP 为什么不走自动排序。
- [appendix-knowledge-map.md](appendix-knowledge-map.md)：回到知识点地图。

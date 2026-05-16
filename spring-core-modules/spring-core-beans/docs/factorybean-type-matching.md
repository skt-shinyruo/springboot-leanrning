# FactoryBean 类型匹配：`getObjectType()` 如何影响发现与推断

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 `FactoryBean#getObjectType()` 为什么会影响类型查找、自动装配、条件判断和 AOT 推断。
    - 重点覆盖返回 `null`、返回不稳定类型、以及由此带来的早期初始化风险。
    - 读完后应该能判断一个 FactoryBean 为什么“按名字能取，按类型却看不见”。

    观察对象：FactoryBean 产品类型、类型发现和静态推断。
    主线位置：容器做类型查询、候选发现或 AOT 预分析时。
    对照入口：`SpringCoreBeansFactoryBeanDeepDiveLabTest`、`SpringCoreBeansFactoryBeanEdgeCasesLabTest`、`SpringCoreBeansServiceLoaderFactoryBeansLabTest`。
<!-- CHAPTER-CARD:END -->

`FactoryBean` 的产品不是靠名字被理解的，而是靠产品类型被发现的。容器在做按类型查询时，会先问一个很现实的问题：这个 FactoryBean 最终会产出什么类型？

这个问题的标准答案就是 `getObjectType()`。

## 为什么这个方法重要

容器会把 `getObjectType()` 当作产品类型的主要来源之一，用在这些场景里：

- `getBeanNamesForType(...)`
- 自动装配候选收集
- `context.getType(...)`
- 条件评估和元数据推断
- AOT 阶段的静态分析

如果产品类型稳定可见，容器就能在不真正创建产品的情况下完成很多判断。反过来，如果类型不可见或不可靠，容器只能更保守。

## 返回 `null` 的含义

`SpringCoreBeansFactoryBeanEdgeCasesLabTest` 里，`getObjectType()` 返回 `null` 的 FactoryBean 有一个很典型的结果：

- 按类型找产品时，`allowEagerInit=false` 的查询看不到它。
- 但按名字 `getBean("unknownValue")` 仍然能拿到产品。
- `getBean("&unknownValue")` 仍然能拿到工厂本身。

这说明 `null` 不等于“没有产品”，而是“容器在类型层面无法可靠推断产品类型”。

## 返回错误或不稳定类型的后果

如果 `getObjectType()` 返回了错误类型，类型发现就会偏掉。`SpringCoreBeansFactoryBeanEdgeCasesLabTest` 里，`WrongObjectTypeFactoryBean` 明明产出 `Value`，却声称自己是 `String.class`，结果按 `Value` 查询时就可能被跳过。

如果 `getObjectType()` 会随运行时状态变化，容器对它的静态推断也会变差。对于单值注入、条件判断和 AOT 预分析，这种“不稳定”比 `null` 更难排查，因为它表面上看起来像一个正常的类型方法，实际却让容器在不同阶段看到不同结果。

## 早期初始化风险

类型查询有时会为了更准确而允许 eager init。这样一来，原本只是想做“类型探测”的调用，可能会把 FactoryBean 本身甚至它的产品一起拉起来。

这就是为什么 `getObjectType()` 应该尽量稳定且便宜：

- 稳定，容器才能在不实例化产品的情况下做发现。
- 便宜，容器才不会因为类型检查而提前推进创建流程。

## AOT 为什么也关心它

AOT 需要在运行前尽量确定 bean 图和产品类型。`getObjectType()` 越稳定，AOT 越容易推断：

- 这个 FactoryBean 的产品会被谁注入。
- 是否需要为产品类型生成额外提示。
- 哪些查找可以在编译期固定下来。

如果 `getObjectType()` 返回 `null` 或结果漂移，AOT 阶段就只能保守处理，静态推断的收益会明显下降。这里不是说 AOT 一定失败，而是说它失去了可靠的类型来源。

## ServiceLoader 系 FactoryBean 的意义

`SpringCoreBeansServiceLoaderFactoryBeansLabTest` 里，`ServiceListFactoryBean` 和 `ServiceLoaderFactoryBean` 把服务发现结果包装成可注入的产品对象。它们的价值不在于“会读服务配置”这件事本身，而在于它们把外部发现结果变成了容器能识别的产品类型，从而可以继续参与类型查询和按类型获取。

## 本模块的观察入口

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanEdgeCasesLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansServiceLoaderFactoryBeansLabTest test
```

- `SpringCoreBeansFactoryBeanDeepDiveLabTest`：看稳定产品类型如何支撑 `getBean()`、`getType()` 和缓存。
- `SpringCoreBeansFactoryBeanEdgeCasesLabTest`：看 `null`、错误类型和 eager init 风险。
- `SpringCoreBeansServiceLoaderFactoryBeansLabTest`：看服务发现工厂如何把外部资源变成可匹配产品。


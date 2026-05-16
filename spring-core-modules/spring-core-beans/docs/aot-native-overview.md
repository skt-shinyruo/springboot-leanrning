# AOT / Native 总览：JVM 可运行不等于 Native 可运行

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释为什么 JVM 上跑通的 Bean 图，不等于 Native Image 里也能跑通。
    - 重点看 build-time analysis、RuntimeHints、反射/代理/资源边界，以及 FactoryBean 和工厂发现的限制。
    - 读完后应能判断问题是“运行时动态太强”，还是“构建期声明不够”。

    观察对象：JVM 动态能力、native closed world、RuntimeHints 和 AOT 工厂发现。
    主线位置：AOT / Native 边界。
    对照入口：`SpringCoreBeansAotRuntimeHintsLabTest`、`SpringCoreBeansRuntimeHintsBoundaryLabTest`、`SpringCoreBeansAotFactoriesLabTest`。
<!-- CHAPTER-CARD:END -->

JVM 成功不代表 Native 成功，原因很简单：JVM 允许很多运行时猜测，而 Native 要求很多信息在构建期就确定下来。只要代码依赖反射、动态代理、资源扫描、运行时发现或者不稳定的类型推断，Native 的边界就会比 JVM 更早暴露。

## AOT 先做的是分析，不是执行

AOT 的核心不是“再启动一次”，而是先在构建期把 Bean 图、反射访问、代理形态和资源访问整理成可声明的事实。容器在 Native 里能不能工作，取决于这些事实有没有被前移到构建时。

`SpringCoreBeansAotRuntimeHintsLabTest` 给出的最直接证据是：没有注册时，`RuntimeHintsPredicates` 不会命中；一旦 `RuntimeHintsRegistrar` 把 hints 写进去，反射访问才变成显式可见。这说明 RuntimeHints 不是装饰品，而是运行期访问权限的前置契约。

## RuntimeHints 约束的是哪些能力

在 Native 场景里，下面几类能力都不能默认假设可用：

| 能力 | JVM 上常见做法 | Native 需要什么 |
| --- | --- | --- |
| 反射 | 运行时直接 `Class#getDeclared*` | 预先声明 reflection hints |
| 动态代理 | 运行时组装接口代理 | 预先声明 proxy hints |
| 资源访问 | 运行时扫 classpath | 预先声明 resource hints |
| 构造器/方法访问 | 运行时尝试调用 | 预先声明可访问成员 |

这些限制不是 Spring 特有的，而是 closed world 的基本要求。Spring AOT 的任务是把容器会用到的访问方式翻译成 Native 能接受的声明。

## FactoryBean 和工厂发现会放大边界

FactoryBean 的困难点不在“能不能创建”，而在“构建期能不能稳定知道它会产出什么”。如果产品类型、反射入口或工厂调用路径只能在运行时才确定，Native 就无法像 JVM 一样靠动态分支兜底。

`SpringCoreBeansAotFactoriesLabTest` 说明了另一层边界：AOT 相关的 processor 和 `RuntimeHintsRegistrar` 是通过 `AotServices.factories()` 发现的，而不是靠临时扫描碰运气。也就是说，AOT 自己也依赖一套明确的工厂发现机制；这套机制如果不可见，后续的 hints 和 bean 处理都不会自动出现。

## 为什么 JVM 成功不等于 Native 成功

下面这类现象最容易让人误判：

1. 业务 Bean 在 JVM 上正常创建。
2. 反射路径在测试里看起来没问题。
3. `FactoryBean` 或 service discovery 在开发环境能找到对象。
4. 进入 Native 后，某个成员、代理或资源突然不可达。

问题通常不在“Spring 不会创建 Bean”，而在“构建期没有把访问边界说清楚”。JVM 的宽松会掩盖这个问题，Native 会把它暴露出来。

## 读 AOT 问题的顺序

先问这个 bean 图是不是静态可见，再问访问方式有没有 hints，再问 factory 或 product 类型是不是在构建期就能确定。只要其中一环依赖运行时猜测，Native 就要额外补声明。

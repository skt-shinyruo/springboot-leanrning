# 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

## 导读

- 本章主题：**08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线

`FactoryBean` 是 Spring 里一个非常“老牌但重要”的扩展点，常见于各种框架集成（ORM、RPC、代理生成等）。

这一章解决的问题是：

> 为什么 `getBean("xxx")` 拿到的不是 `xxx` 这个类型本身，而是“它生产的对象”？

## 1. `FactoryBean` 的核心语义

把它记成一句话就够了：

> `FactoryBean<T>` 是一个“能生产 T 的工厂”；**容器里注册的是工厂本身**，但你日常 `getBean("name")`/按类型注入拿到的往往是 **工厂生产出来的 product（T）**。

### 1.1 两个名字，两种语义（必须背下来）

- `"name"` → product（`FactoryBean#getObject()` 的返回值）
- `"&name"` → factory（`FactoryBean` 实例本身）

### 1.1.1 机制讲透：条件 → 分支 → 结果（可断点验证）

**条件**：beanName 是否以 `&` 开头  
**分支**：`AbstractBeanFactory#getObjectForBeanInstance`  
**结果**：  
- `"name"`：走 `FactoryBean#getObject()`，返回 product  
- `"&name"`：直接返回 factory 本体  
**断点建议**：`AbstractBeanFactory#getObjectForBeanInstance`

这不是“语法糖”，而是 Spring IoC 对 FactoryBean 的硬规则：不记牢，排查注入问题会非常痛苦。

### 1.2 缓存语义：缓存的是 product（并且由 isSingleton 决定）

`FactoryBean` 自己也是一个 bean（默认 singleton）；但 **product 是否被容器缓存**，取决于：

- `FactoryBean#isSingleton()` 返回 `true`：容器会缓存 product（下一次取同名 bean 直接返回缓存）
- `FactoryBean#isSingleton()` 返回 `false`：容器不会缓存 product（每次可能重新生产）

### 1.3 类型匹配：按类型找的是 product 的类型

当你做“按类型注入/按类型查找”时，Spring 需要知道 product 的类型：

- 优先依赖 `FactoryBean#getObjectType()` 的返回值做 type matching
- 如果 `getObjectType()` 返回 `null`，很多 **按类型发现**（尤其 `allowEagerInit=false` 的路径）会失效

这一点在复杂项目里非常常见：你会遇到“明明能按名字拿到，但按类型找不到”的怪现象。

### 1.4 类型推断与缓存链路（你需要知道这 3 个入口）

按类型查找/条件装配时，Spring 走的不是 `getObject()`，而是类型推断链路：

1) `FactoryBean#getObjectType()`：产品类型的第一优先级  
2) `AbstractBeanFactory#getTypeForFactoryBean(...)`：必要时会触发 factory 创建/推断  
3) `FactoryBeanRegistrySupport#getObjectFromFactoryBean(...)`：真正创建 product，并处理缓存

**关键影响**：  
- `getObjectType()` 为空 → `allowEagerInit=false` 的路径会直接放弃匹配  
- `isSingleton()` 为 true → product 会进入缓存（`factoryBeanObjectCache`），影响“是否每次创建”

当某个 bean 实现了 `FactoryBean<T>`：

- 容器默认把它当作“工厂”
- **按 beanName 获取时返回的是 `T`（产品）**
- 如果你想拿到工厂本身，需要在 beanName 前加 `&`

这就是很多人第一次碰到 `FactoryBean` 时的迷惑点。

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`（最小闭环：`"name"` vs `"&name"`）
- （深入对照）`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
  - `factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType()`（按类型找的是 product）

你会看到：

- `getBean("sequence", Long.class)` 返回的是 Long（产品），并且每次调用递增
- `getBean("&sequence")` 返回的是 `SequenceFactoryBean`（工厂本身）

你需要记住的就是：

- `"name"` → product
- `"&name"` → factory

## `&` 前缀证据链（最短路径）

从 `getBean("name")` 到“分流”的最短路径：

1) `AbstractBeanFactory#doGetBean`  
2) `AbstractBeanFactory#getObjectForBeanInstance`  
3) `BeanFactoryUtils.isFactoryDereference(beanName)`（判断是否 `&` 前缀）

## FactoryBean 与代理/循环依赖的交叉

- **early reference 一致性**：FactoryBean 产物若被代理，early reference 必须与最终暴露对象一致  
  - 观察点：`getEarlyBeanReference` 与 `getObjectFromFactoryBean` 返回形态是否一致
- **循环依赖边界**：FactoryBean 本体与 product 参与循环依赖时，容易出现“工厂已创建但产品不可用”的窗口  
  - 观察点：`singletonFactories` / `earlySingletonObjects` 是否含 product

## 3. `FactoryBean` 常见用途（理解即可）

- 复杂对象的创建（需要大量配置、或创建过程昂贵）
- 与外部系统集成时，把“连接/代理对象的创建”封装成 bean
- 生成代理对象（你以为注入的是接口实现，其实是代理）

你想在源码里“看见” product/factory 与缓存发生在哪，建议从这几个点切入：

- `AbstractBeanFactory#doGetBean`：`getBean()` 总入口
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：从 factory 拿 product，并处理缓存
- `AbstractBeanFactory#isTypeMatch` / `DefaultListableBeanFactory#getBeanNamesForType`：type matching 的关键路径

你应该能在调用栈里明确看到：

- `"name"` 与 `"&name"` 的分流
- product 缓存命中/未命中的差异
- `allowEagerInit=false` 时为什么不会主动实例化 factory 来推断类型

1) 你以为注册的是 `MyFactoryBean`，结果注入点按类型找不到
   - 因为容器对外暴露的类型是它生产的 `T`
2) 你以为 `@Autowired MyFactoryBean` 能注入工厂
   - 需要按 `&name` 或按工厂类型显式获取/注入（并不常见）
3) 你以为 `FactoryBean` 的 `isSingleton()` 决定工厂是不是单例
   - 它影响的是“产品是否单例”，不是“工厂本身是否单例”（工厂通常也是单例 bean）

## 面试常问（FactoryBean）

1) **FactoryBean 到底是什么？它和“工厂模式”有什么不同？**
   - 要点：它是容器级扩展点：一个 beanName 同时代表“工厂本体”与“工厂产物”；默认对外暴露的是产物（product），不是工厂实例。

2) **为什么 `getBean("x")` 拿到的是 product，而不是你注册的 FactoryBean？**
   - 要点：`AbstractBeanFactory#getObjectForBeanInstance` 会对 `FactoryBean` 做分流；`"x"` 返回 product；`"&x"` 才返回 factory。

3) **`FactoryBean#isSingleton()` 的语义是什么？它决定了什么缓存？**
   - 要点：它决定的是 product 的缓存语义（`FactoryBeanRegistrySupport` 缓存 product），不是“工厂 bean 是否单例”；工厂本体通常仍按普通 bean 的 scope 管理。

## 可复现闭环（基于 `SpringCoreBeansFactoryBeanDeepDiveLabTest`）

用 3 条断言把 FactoryBean 的核心语义固定下来：

1) **`"name"` vs `"&name"` 的分流**  
   - 断点：`getObjectForBeanInstance`  
   - 断言：`&` 返回 factory，本体不等于 product
2) **`isSingleton()` 决定 product 缓存**  
   - 断点：`FactoryBeanRegistrySupport#getObjectFromFactoryBean`  
   - 断言：`isSingleton=true` 时 product 命中缓存
3) **`getObjectType()` 影响 type-based 查找**  
   - 断点：`AbstractBeanFactory#isTypeMatch`  
   - 断言：`getObjectType=null` 时按类型查找失败但按名字可取

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test`

> 注意：这会让“同一个 beanName 每次拿到是否同一对象”变成一个需要你明确验证的点，而不是凭感觉判断。

## 2. 本模块的实验：用 `&sequence` 拿到工厂

建议从这些测试方法开始（它们把 FactoryBean 的关键语义做成了可断言实验）：

- `SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`：`&name` 的硬规则
- `SpringCoreBeansFactoryBeanDeepDiveLabTest#factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType`：product 参与 type matching
- `SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer`：`isSingleton=true` 的缓存
- `SpringCoreBeansFactoryBeanDeepDiveLabTest#nonSingletonFactoryBeanProduct_isNotCached_byTheContainer`：`isSingleton=false` 的非缓存
- `SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName`：`getObjectType=null` 的边界

- `SpringCoreBeansContainerLabTest.factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`

实验里定义了一个 `SequenceFactoryBean implements FactoryBean<Long>`：

## 源码锚点（建议从这里下断点）

- `AbstractBeanFactory#doGetBean`：`getBean()` 总入口（会走到 product/factory 分流）
- `AbstractBeanFactory#getObjectForBeanInstance`：`"name"` vs `"&name"` 的分流与暴露语义（product / factory）
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：从 FactoryBean 拿 product + 处理缓存
- `AbstractBeanFactory#isTypeMatch`：按类型查找/注入时的关键路径（取决于 `getObjectType()`）
- `DefaultListableBeanFactory#getBeanNamesForType`：type-based 发现入口（对照 `allowEagerInit` 的边界）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

把断点打在上面几个方法，然后跑：

- `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- `SpringCoreBeansFactoryBeanEdgeCasesLabTest`

下一章我们讲一个更偏“容器内部”的现象：循环依赖。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
推荐断点：`AbstractBeanFactory#getObjectForBeanInstance`、`FactoryBeanRegistrySupport#getObjectFromFactoryBean`、`AbstractBeanFactory#doGetBean`

## 常见坑与边界

### 常见坑（高频误判）

- 常问：`FactoryBean` 是什么？为什么 `getBean("x")` 拿到的是 product 而不是 factory？
  - 答题要点：`FactoryBean<T>` 是“工厂 bean”；默认通过 beanName 暴露的是它生产的 product；用 `&beanName` 才能拿到 factory 本身。
- 常见追问：`isSingleton()` 决定缓存的是什么？
  - 答题要点：决定 product 的缓存语义（缓存的是 product 不是 factory）；这会影响你观测到的“是不是同一个对象”。
- 常见追问：`getObjectType()` 返回 `null` 有什么坑？为什么 `allowEagerInit=false` 会放大它？
  - 答题要点：会影响 type-based 查找与条件装配（例如 `@ConditionalOnMissingBean`）；需要时对照 [23](../part-04-wiring-and-boundaries/23-factorybean-deep-dive.md) 与 [29](../part-04-wiring-and-boundaries/29-factorybean-edge-cases.md) 深挖。

## 排障决策表（FactoryBean：name/type/缓存三连）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `getBean(\"x\")` 拿到的不是 FactoryBean 本体 | FactoryBean 的默认暴露语义：返回的是 product | 断点 `AbstractBeanFactory#getObjectForBeanInstance`；观察 beanName 是否带 `&` 前缀 | 用 `&x` 获取 factory；文档/代码里把语义写清楚 | `SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory` |
| 按类型查找找不到（但按名字 `getBean(\"x\")` 可以） | `FactoryBean#getObjectType()` 返回 `null` 或不稳定，导致 type discovery 失败 | 断点 `AbstractBeanFactory#isTypeMatch` / `DefaultListableBeanFactory#getBeanNamesForType`；关注 `allowEagerInit` 分支 | 让 `getObjectType()` 可推断；必要时允许 eager init（谨慎）；或用 name/Qualifier 规避 | `SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName` |
| 你以为 `isSingleton()` 决定“工厂是否单例” | 误解：它决定的是 product 的缓存语义 | 断点 `FactoryBeanRegistrySupport#getObjectFromFactoryBean`；观察缓存命中与否 | 把“工厂本体 scope”与“产品缓存语义”分开理解与验证 | `SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer` / `#nonSingletonFactoryBeanProduct_isNotCached_byTheContainer` |

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[07. @Configuration 增强：proxyBeanMethods 与 @Bean 语义](018-07-configuration-enhancement.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[09. 循环依赖概览：三级缓存与现象分类](09-circular-dependencies.md)

<!-- BOOKIFY:END -->

## 一句话自检

你应该能回答：

1) 为什么 `getBean("name")` 返回的是 product，而不是工厂本身？
2) `&name` 的语义是什么？什么时候必须用它？
3) `FactoryBean#getObjectType()` 返回 null 会导致哪类“按类型发现/注入”的问题？

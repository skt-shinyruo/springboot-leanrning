# `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。

    本章围绕08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansContainerLabTest`。需要下探源码时，可以从 `FactoryBean#getObject()` / `AbstractBeanFactory#getObjectForBeanInstance` / `FactoryBean#isSingleton()` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

本章围绕「08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansContainerLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansContainerLabTest`，再用 `SpringCoreBeansFactoryBeanDeepDiveLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#getObjectForBeanInstance`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

`FactoryBean` 是 Spring 里一个非常“老牌但重要”的扩展点，常见于各种框架集成（ORM、RPC、代理生成等）。

这一章解决的问题是：

> 为什么 `getBean("xxx")` 获取到的不是 `xxx` 这个类型本身，而是“它生产的对象”？

## `FactoryBean` 的核心语义

把它记成一句话就够了：

> `FactoryBean<T>` 是一个“能生产 T 的工厂”；**容器里注册的是工厂本身**，但读者日常 `getBean("name")`/按类型注入获取到的往往是 **工厂生产出来的 product（T）**。

### 1.1 两个名字，两种语义（必须背下来）

- `"name"` → product（`FactoryBean#getObject()` 的返回值）
- `"&name"` → factory（`FactoryBean` 实例本身）

### 1.1.1 机制系统阐述：条件 → 分支 → 结果（可断点验证）

- **条件**：beanName 是否以 `&` 开头
- **分支**：`AbstractBeanFactory#getObjectForBeanInstance`
- **结果**：
  - `"name"`：走 `FactoryBean#getObject()`，返回 product
  - `"&name"`：直接返回 factory 本体
- **断点建议**：`AbstractBeanFactory#getObjectForBeanInstance`

这不是“语法糖”，而是 Spring IoC 对 FactoryBean 的硬规则：不记牢，排查注入问题会非常痛苦。

### 1.2 缓存语义：缓存的是 product（并且由 isSingleton 决定）

`FactoryBean` 自己也是一个 bean（默认 singleton）；但 **product 是否被容器缓存**，取决于：

- `FactoryBean#isSingleton()` 返回 `true`：容器会缓存 product（下一次取同名 bean 直接返回缓存）
- `FactoryBean#isSingleton()` 返回 `false`：容器不会缓存 product（每次可能重新生产）

### 1.3 类型匹配：按类型找的是 product 的类型

当读者做“按类型注入/按类型查找”时，Spring 需要知道 product 的类型：

- 优先依赖 `FactoryBean#getObjectType()` 的返回值做 type matching
- 如果 `getObjectType()` 返回 `null`，很多 **按类型发现**（尤其 `allowEagerInit=false` 的路径）会失效

这一点在复杂项目里非常常见：可以遇到“明明能按名字获取到，但按类型找不到”的怪现象。

### 1.4 类型推断与缓存链路（需要知道这 3 个入口）

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
- 若想获取到工厂本身，需要在 beanName 前加 `&`

这就是很多人第一次碰到 `FactoryBean` 时的迷惑点。

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`（最小闭环：`"name"` vs `"&name"`）
- （深入对照）`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
  - `factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType()`（按类型找的是 product）

可以观察到：

- `getBean("sequence", Long.class)` 返回的是 Long（产品），并且每次调用递增
- `getBean("&sequence")` 返回的是 `SequenceFactoryBean`（工厂本身）

需要记住的就是：

- `"name"` → product
- `"&name"` → factory

## `&` 前缀证据链（最短路径）

从 `getBean("name")` 到“分流”的最短路径：

1) `AbstractBeanFactory#doGetBean`
2) `AbstractBeanFactory#getObjectForBeanInstance`
3) `BeanFactoryUtils.isFactoryDereference(beanName)`（判断是否 `&` 前缀）

## FactoryBean 与代理/循环依赖的交叉

- **early reference 一致性**：FactoryBean 产物若被代理，early reference 必须与最终暴露对象一致
  
  调试时建议重点盯：`getEarlyBeanReference` 与 `getObjectFromFactoryBean` 返回形态是否一致。
  
- **循环依赖边界**：FactoryBean 本体与 product 参与循环依赖时，容易出现“工厂已创建但产品不可用”的窗口
  
  调试时建议重点盯：`singletonFactories` / `earlySingletonObjects` 是否含 product。
  

## `FactoryBean` 常见用途（理解即可）

- 复杂对象的创建（需要大量配置、或创建过程昂贵）
- 与外部系统集成时，把“连接/代理对象的创建”封装成 bean
- 生成代理对象（容易误以为注入的是接口实现，但实际为代理）

若希望在源码里“观察到” product/factory 与缓存发生在哪，建议从这几个点切入：

- `AbstractBeanFactory#doGetBean`：`getBean()` 总入口
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：从 factory 拿 product，并处理缓存
- `AbstractBeanFactory#isTypeMatch` / `DefaultListableBeanFactory#getBeanNamesForType`：type matching 的关键路径

应能够在调用栈里明确看到：

- `"name"` 与 `"&name"` 的分流
- product 缓存命中/未命中的差异
- `allowEagerInit=false` 时为什么不会主动实例化 factory 来推断类型

1) 容易误以为注册的是 `MyFactoryBean`，结果注入点按类型找不到
   - 因为容器对外暴露的类型是它生产的 `T`
2) 容易误以为 `@Autowired MyFactoryBean` 能注入工厂
   - 需要按 `&name` 或按工厂类型显式获取/注入（并不常见）
3) 容易误以为 `FactoryBean` 的 `isSingleton()` 决定工厂是不是单例
   - 它影响的是“产品是否单例”，不是“工厂本身是否单例”（工厂通常也是单例 bean）

## 面试常问（FactoryBean）

1) **FactoryBean 到底是什么？它和“工厂模式”有什么不同？**
   - 要点：它是容器级扩展点：一个 beanName 同时代表“工厂本体”与“工厂产物”；默认对外暴露的是产物（product），不是工厂实例。

2) **为什么 `getBean("x")` 获取到的是 product，而不是读者注册的 FactoryBean？**
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


推荐断点锚点（从这里设置断点，能最快把“产品 vs 工厂 vs 缓存 vs 类型匹配”打穿）：

- `AbstractBeanFactory#doGetBean`：`getBean()` 总入口（会走到 product/factory 分流）
- `AbstractBeanFactory#getObjectForBeanInstance`：`"name"` vs `"&name"` 的分流与暴露语义（product / factory）
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：从 FactoryBean 拿 product + 处理缓存
- `AbstractBeanFactory#isTypeMatch`：按类型查找/注入时的关键路径（强依赖 `getObjectType()` 的正确性与缓存语义）
- `DefaultListableBeanFactory#getBeanNamesForType`：type-based 发现入口（对照 `allowEagerInit` 的边界）

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

## 常见误区与边界

### 常见误区（高频误判）

- 常问：`FactoryBean` 是什么？为什么 `getBean("x")` 获取到的是 product 而不是 factory？
  - 答题要点：`FactoryBean<T>` 是“工厂 bean”；默认通过 beanName 暴露的是它生产的 product；用 `&beanName` 才能获取到 factory 本身。
- 常见追问：`isSingleton()` 决定缓存的是什么？
  - 答题要点：决定 product 的缓存语义（缓存的是 product 不是 factory）；这会影响读者观测到的“是不是同一个对象”。
- 常见追问：`getObjectType()` 返回 `null` / 返回错误类型 有什么误区？为什么 `allowEagerInit=false` 会放大它？
  - 答题要点：会影响 type-based 查找与条件装配（例如 `@ConditionalOnMissingBean`）；如果 `getObjectType()` “说谎”，甚至会造成候选集合被污染（找不到本该找到的 product）。需要时对照 [23](wiring-factorybean-deep-dive.md) 与 [29](wiring-factorybean-edge-cases.md) 深入分析。

## 排障决策表（FactoryBean：name/type/缓存三连）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `getBean("x")` 获取到的不是 FactoryBean 本体 | FactoryBean 的默认暴露语义：返回的是 product | 断点 `AbstractBeanFactory#getObjectForBeanInstance`；观察 beanName 是否带 `&` 前缀 | 用 `&x` 获取 factory；文档/代码里把语义写清楚 | `SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory` |
| 按类型查找找不到（但按名字 `getBean("x")` 可以） | `FactoryBean#getObjectType()` 返回 `null` / 返回错误类型 / 不稳定，导致 type discovery 失败或误判 | 断点 `AbstractBeanFactory#isTypeMatch` / `DefaultListableBeanFactory#getBeanNamesForType`；关注 `allowEagerInit` 分支 | 让 `getObjectType()` 可推断且真实；必要时允许 eager init（谨慎）；或用 name/Qualifier 规避 | `SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName` / `#factoryBeanWithWrongObjectType_canBreakTypeBasedDiscovery_evenIfProductTypeIsActuallyCorrect` |
| 容易误以为 `isSingleton()` 决定“工厂是否单例” | 误解：它决定的是 product 的缓存语义 | 断点 `FactoryBeanRegistrySupport#getObjectFromFactoryBean`；观察缓存命中与否 | 把“工厂本体 scope”与“产品缓存语义”分开理解与验证 | `SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer` / `#nonSingletonFactoryBeanProduct_isNotCached_byTheContainer` |

## 小结


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

<!-- BOOKIFY:END -->

## 自检要点
应能够回答：

1) 为什么 `getBean("name")` 返回的是 product，而不是工厂本身？
2) `&name` 的语义是什么？什么时候必须用它？
3) `FactoryBean#getObjectType()` 返回 null 会导致哪类“按类型发现/注入”的问题？

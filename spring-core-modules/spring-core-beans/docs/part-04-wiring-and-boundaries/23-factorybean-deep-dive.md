# 23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`FactoryBean#isSingleton()` / `AbstractBeanFactory#getObjectForBeanInstance` / `FactoryBean#getObject()`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[22. beanName 与 alias：命名规则与别名本质](22-bean-names-and-aliases.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[24. BeanDefinition 覆盖：同名定义的冲突策略](24-bean-definition-overriding.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

!!! summary "本章要点"

    - `FactoryBean` 有两种“对外暴露形态”：
      - `getBean("name")` / `getType("name")` → **product**
      - `getBean("&name")` / `getType("&name")` → **factory**
    - product 会参与按类型匹配/注入；**product 是否缓存**取决于 `FactoryBean#isSingleton()`（这和 factory bean 自己是否 singleton 是两回事）。
    - `getObjectType()` 是 type discovery 的关键输入：返回 `null`/不稳定会导致“按类型发现失效、但按名字仍能取到”的边界（尤其 `allowEagerInit=false` 时）。
    - 当 product 不缓存（`isSingleton=false`）时：
      - direct injection（直接注入 Value）只解析一次（consumer 持有固定引用）
      - `ObjectProvider<Value>` 可以每次获取到新的 product（更贴近“按需解析”语义）


!!! example "本章配套实验（先运行再读）"

    - Lab：
      - `SpringCoreBeansContainerLabTest`（基础版：`name` vs `&name`）
      - `SpringCoreBeansFactoryBeanDeepDiveLabTest`（product 缓存语义 + `getType` 对照）
      - `SpringCoreBeansFactoryBeanEdgeCasesLabTest`（getObjectType 边界 + provider 对照）
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansContainerLabTest`，再用 `SpringCoreBeansFactoryBeanDeepDiveLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#getObjectForBeanInstance`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

`FactoryBean` 是 Spring 里非常“容器味”的机制：

- 这个 bean 本身是工厂
- 容器对它有特殊对待

## 1. 最重要的规则：`&` 前缀

给定一个 FactoryBean 的 beanName（例如 `valueFactory`）：

- `getBean("valueFactory")` 获取到的是 **product**（`getObject()` 的返回值）
- `getBean("&valueFactory")` 获取到的是 **factory**（FactoryBean 自身）

### 1.1 机制系统阐述：条件 → 分支 → 结果

**条件**：beanName 是否以 `&` 开头  
**分支**：`AbstractBeanFactory#getObjectForBeanInstance`  
**结果**：  
- `"name"` → product  
- `"&name"` → factory  
**断点建议**：`AbstractBeanFactory#getObjectForBeanInstance`

同样的规则也适用于“看类型”：

- `getType("valueFactory")` 更像在问：**product 的类型是什么？**
- `getType("&valueFactory")` 更像在问：**factory 的类型是什么？**

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`（最小闭环：`"name"` vs `"&name"`）
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
  - `factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType()`（同名：默认暴露 product）

## 2. product 也参与“按类型查找”

这件事之所以容易让人困惑，是因为读者脑子里常有两个“bean”：

- **factory**：实现了 `FactoryBean` 的那个对象（它自己也是 bean）
- **product**：`FactoryBean#getObject()` 生产出来的对象（它才是默认暴露给业务的 bean）

当读者做“按类型查找/注入”时（例如 `getBean(SomeType.class)` 或 `@Autowired SomeType`），Spring 的默认语义是：

> **把 FactoryBean 当作“生产线”，按类型匹配的是 product 的类型。**

### 2.1 product 类型从哪里来？

容器需要回答一个问题：这个工厂“生产什么类型”？

### 2.2 为什么不要把 getObjectType 当成“随意填写”

- `getObjectType()` 返回 `null` / 不稳定（偶尔变）
- 或者为了推断类型去做昂贵/有副作用的动作

后果是：

- 注入解析结果变得不可预测
- 一些框架能力（例如按类型扫描注册）会表现为“偶现缺 bean”

建议：

因为 FactoryBean 会声明：

- `getObjectType()`：它生产的对象类型

所以可以：

- `getBean(Value.class)` 获取到 product（即使容器里没有直接注册 `Value` 的 BeanDefinition）

这也是为什么 `FactoryBean` 经常被用在：

- 把“复杂构建逻辑”封装成一个容器可管理的工厂

## 3. isSingleton 的语义：容器是否缓存“product”

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
  - `singletonFactoryBeanProduct_isCached_byTheContainer()`（证据：多次 getBean(productType) 返回 same reference）
  - `nonSingletonFactoryBeanProduct_isNotCached_byTheContainer()`（证据：每次 getBean(productType) 都会新建 product）
  - `factoryBeanItself_isASingletonBean_byDefault_evenWhenProductIsNotCached()`（对照：factory 自己仍是 singleton，但 product 不缓存）
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
  - `productVsFactoryVsProvider_whenFactoryBeanProductIsNotCached()`（对照：direct injection vs ObjectProvider，每次是否能获取到新 product）

应当观察到：

- 当 `isSingleton() == true`：多次 `getBean(Value.class)` 返回同一个 product 实例
- 当 `isSingleton() == false`：每次 `getBean(Value.class)` 都会重新调用 `getObject()` 产生新实例

学习重点：

- **isSingleton 控制的是 product 的缓存语义**
- factory bean 自己通常仍然是容器管理的 singleton（除非读者显式把它定义成 prototype）

### 3.1 一个很容易混淆的点：factory bean 自己仍是普通 bean

应能够解释清楚这句话：

> **FactoryBean 的“特殊”只发生在 `getBean("name")` 返回值上；FactoryBean 本身仍然是一个普通 bean（默认 singleton）。**

所以可以观察到这种对照现象：

- `getBean("&valueFactory")` 获取到的 factory 引用通常是同一个（singleton）
- 但 `getBean(Value.class)` 获取到的 product 可能每次都不同（当 `isSingleton=false`）

### 3.2 当 product 不缓存时：ObjectProvider 的意义更直观

当 `isSingleton=false` 时，product 的语义更接近“按需创建”。这时：

- direct injection：在 consumer 创建时解析一次，consumer 内部持有固定 product 引用
- `ObjectProvider<Value>`：每次 `getObject()` 都回到容器再解析一次，更贴近“每次获取新 product”的语义

## FactoryBean 与代理/循环依赖的交叉

- early reference 阶段若需要 product，建议确保 **early == final** 形态一致  
- 代理介入时，优先让 `getEarlyBeanReference` 返回 proxy，避免 raw 注入绕过增强

## 可复现闭环（基于 `SpringCoreBeansContainerLabTest`）

运行完成该 Lab，至少应能够复述 3 条结论：

1) **`"name"` vs `"&name"` 的分流**  
   - 断点：`getObjectForBeanInstance`  
   - 断言：同名不同语义
2) **product 参与 type matching**  
   - 断点：`isTypeMatch`  
   - 断言：按类型返回 product
3) **isSingleton 决定 product 缓存**  
   - 断点：`getObjectFromFactoryBean`  
   - 断言：缓存命中取决于 `isSingleton()`

- `AbstractBeanFactory#getObjectForBeanInstance`：处理 “FactoryBean 的 product vs factory” 分流（`&` 前缀的核心路径）
- `BeanFactoryUtils#isFactoryDereference`：判断 beanName 是否带 `&`（理解为什么 `&name` 获取到的是工厂）
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：调用 `FactoryBean#getObject()` 并决定是否缓存 product
- `FactoryBeanRegistrySupport#getCachedObjectForFactoryBean`：product 的缓存入口（与 `isSingleton()` 语义直接相关）
- `FactoryBean#getObjectType`：product 的类型声明入口（影响 type matching 与条件判断）

入口：

最小复现入口（方法级）：

- `SpringCoreBeansContainerLabTest.factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`
- `SpringCoreBeansFactoryBeanDeepDiveLabTest.factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType()`
- `SpringCoreBeansFactoryBeanDeepDiveLabTest.singletonFactoryBeanProduct_isCached_byTheContainer()`
- `SpringCoreBeansFactoryBeanDeepDiveLabTest.nonSingletonFactoryBeanProduct_isNotCached_byTheContainer()`

推荐断点（闭环版）：

1) `AbstractBeanFactory#doGetBean`：`getBean(...)` 总入口
2) `AbstractBeanFactory#getObjectForBeanInstance`：处理 product vs factory 的核心分流（含 `&` 前缀）
3) `BeanFactoryUtils#isFactoryDereference`：判断是否 `&name`
4) `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：调用 `getObject()` 并决定是否缓存 product
5) `FactoryBeanRegistrySupport#getCachedObjectForFactoryBean`：缓存命中点（对照 `isSingleton()` true/false）
6) （类型匹配链路）`AbstractBeanFactory#isTypeMatch` / `DefaultListableBeanFactory#getBeanNamesForType`

## 排障分流：这是定义层问题还是实例层问题？

- “使用 `getBean("name")` 获取到的不是工厂而是产品” → **实例层（FactoryBean 语义）**：这是 Spring 的特殊规则；要拿工厂请用 `&name`（本章第 1 节）
- “按类型发现/条件装配行为很怪” → **定义层（类型元数据）**：检查 `getObjectType()` 是否可靠（见 [29](29-factorybean-edge-cases.md)）
- “product 缓存像是坏了/每次 get 都创建新对象” → **实例层（缓存语义）**：检查 `isSingleton()` 返回值是否与读者期望一致（本章第 3 节）
- “以为 factory 的 scope 就等于 product 的 scope” → **实例层概念澄清**：`isSingleton()` 控制的是 product 缓存，不是 factory 自己的 scope（本章第 3 节）

## 5. 面试常问（FactoryBean 深入分析）

- 常问：`&beanName` 到底获取到什么？为什么？
  - 答题要点：默认 `getBean("name")` 返回 product；`&name` 是 FactoryBean dereference，返回 factory 自身；分流发生在 `getObjectForBeanInstance`。
- 常见追问：`FactoryBean#isSingleton()` 控制的是“什么是否单例”？
  - 答题要点：控制的是 **product 是否缓存**（同一个 product 实例是否复用），不是 factory 自己是否单例。
- 常见追问：`getObjectType()` 为什么重要？它会影响什么？
  - 答题要点：影响按类型查找/条件装配/候选收集；返回 `null` 或不稳定会导致“按类型发现失效/偶现缺 bean”等问题（见 [29](29-factorybean-edge-cases.md)）。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先运行它们）：
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先运行通再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanDeepDiveLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
- （基础版）`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`（`factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`）

- `SpringCoreBeansFactoryBeanDeepDiveLabTest.factoryBeanProductParticipatesInTypeMatching_andIsRetrievedByProductType()`

- 能返回明确类型就返回明确类型
- 如果确实无法确定，至少在文档/注释中说明原因，并配套测试覆盖边界（本模块已提供，见 [29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](29-factorybean-edge-cases.md) + Lab）

- `SpringCoreBeansFactoryBeanDeepDiveLabTest.singletonFactoryBeanProduct_isCached_byTheContainer()`
- `SpringCoreBeansFactoryBeanDeepDiveLabTest.nonSingletonFactoryBeanProduct_isNotCached_byTheContainer()`

## 源码锚点（建议从这里设置断点）

- `AbstractBeanFactory#doGetBean`：`getBean()` 总入口（会走到 factory/product 分流）
- `AbstractBeanFactory#getObjectForBeanInstance`：`&name` 分流与 “对外暴露 product” 的规则
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：从 FactoryBean 取 product + 处理缓存（singletonFactoryBeanObjectCache）
- `AbstractBeanFactory#isTypeMatch`：type matching 关键路径（高度依赖 `getObjectType/isSingleton`）
- `DefaultListableBeanFactory#getBeanNamesForType`：type-based 发现入口（对照 `allowEagerInit=false` 的边界）

## 断点闭环（用本仓库 Lab/Test 运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
- （基础版）`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`（`factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`）

建议断点：

1) 在 Lab 里的 `FactoryBean#getObject()` / `getObjectType()` / `isSingleton()`：观察 product 创建次数与类型声明
2) `AbstractBeanFactory#getObjectForBeanInstance`：观察 `getBean("name")` 与 `getBean("&name")` 在这里分叉
3) `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：观察容器何时调用 `getObject()`，以及返回值如何被处理
4) `FactoryBeanRegistrySupport#getCachedObjectForFactoryBean`：对照 `isSingleton()` 为 true/false 时缓存是否命中

- 应能够解释清楚：为什么 `&beanName` 可以获取到 factory 自己吗？
- 应能够解释清楚：`isSingleton()` 控制的是“product 是否缓存”而不是“factory 是否单例”吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
推荐断点：`AbstractBeanFactory#getType`、`AbstractBeanFactory#getObjectForBeanInstance`、`FactoryBeanRegistrySupport#getObjectFromFactoryBean`

## 常见误区与边界

- 首选：`FactoryBean#getObjectType()`
- 如果 `getObjectType()` 信息不足（返回 `null`），某些查找路径会选择 **不去实例化 factory**（尤其 `allowEagerInit=false` 时），于是可以观察到“按类型找不到但按名字能获取到”的现象（见 [29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](29-factorybean-edge-cases.md)）。

工程里最常见的误区之一：

## 4. 常见误区

- **误区 1：`getObjectType()` 返回 null 或者返回不准**
  - 会影响按类型匹配与某些条件判断。

- **误区 2：`isSingleton()` 返回与真实行为不一致**
  - 容器会按读者声明的语义缓存/不缓存；声明错了很容易造成“看起来像缓存 bug”。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

## 自检要点
应能够解释清楚：

1) **`getBean(name)` 与 `getBean(&name)` 的差别是什么？两个缓存各自缓存的是什么？**
2) **type matching 为什么经常在 FactoryBean 场景“看似偶发失效”？**（`getObjectType/isSingleton`、allowEagerInit、缓存命中路径）
3) **如何用断点证明“最终暴露对象是 product 还是 factory”？**（`getObjectForBeanInstance` / `getObjectFromFactoryBean`）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[22. beanName 与 alias：命名规则与别名本质](22-bean-names-and-aliases.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[24. BeanDefinition 覆盖：同名定义的冲突策略](24-bean-definition-overriding.md)

<!-- BOOKIFY:END -->

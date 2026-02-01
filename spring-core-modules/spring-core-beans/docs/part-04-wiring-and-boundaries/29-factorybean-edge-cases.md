# 29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`FactoryBean#getObjectType()` / `FactoryBean#getObjectType()==null` / `DefaultListableBeanFactory#getBeanNamesForType`
    - 推荐 Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[28. 自定义 scope 与 scoped proxy：线程 scope 复现](28-custom-scope-and-scoped-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[30. 注入发生在什么时候：field vs constructor](30-injection-phase-field-vs-constructor.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansFactoryBeanEdgeCasesLabTest`，再用 `SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`DefaultListableBeanFactory#getBeanNamesForType` / `FactoryBeanRegistrySupport#getTypeForFactoryBean`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

`FactoryBean` 的核心机制读者已经在 [23 章](23-factorybean-deep-dive.md) 学过了。

- 如果 `FactoryBean#getObjectType()` 返回 `null`
- 那么在“不允许 eager init”的按类型扫描里，它可能不会被当成候选

### 机制系统阐述：条件 → 分支 → 结果

**条件**：`allowEagerInit=false` 且 `FactoryBean#getObjectType()==null`  
**分支**：`getBeanNamesForType` 不能为了“推断类型”而实例化 FactoryBean  
**结果**：按类型扫描 **忽略该 FactoryBean 的 product**  
**断点建议**：`DefaultListableBeanFactory#getBeanNamesForType` / `FactoryBeanRegistrySupport#getTypeForFactoryBean`

## 0. 与代理/循环依赖的交叉边界（只要记住一条）

当 FactoryBean 本身或其 product 进入“提前暴露”路径时：

- `getObjectType()` 的不稳定会放大问题：**类型推断不可靠 → 条件判断/候选匹配更容易误判**
- 若在循环依赖里获取到了 early reference（proxy 或半成品），再叠加“类型不可判定”，排障会非常痛苦

实务建议：**FactoryBean 的 product 类型能稳定就稳定**，不要让它成为“隐形类型黑洞”。

## 1. 现象：getBeanNamesForType(..., allowEagerInit=false) 找不到 unknownValue

这类现象非常“反直觉”，但它背后是一个很合理的设计取舍：

- `allowEagerInit=false` 的含义是：**为了性能与避免副作用，不要为了“类型判断”去创建 bean**。
- 对于 `FactoryBean` 来说，product 的类型往往只能在实例化 factory 后才能确定。
- 若的 `FactoryBean#getObjectType()` 又返回 `null`，容器在“不允许提前实例化”的前提下，就没有足够信息来做 type matching。

所以观察到的结果就会是：

- **按类型发现失败**：`getBeanNamesForType(SomeType, ..., allowEagerInit=false)` 找不到
- **按名字仍然可用**：`getBean("unknownValue")` 依然能创建并返回 product

这不是 bug，而是“元数据不足 + 不允许 eager init”共同导致的必然结果。

### 1.1 为什么真实项目里经常遇到？

很多框架/基础设施在启动时会做“按类型扫描”，但又必须避免触发大量 bean 初始化（否则启动时间不可控、还可能触发外部连接）：

- 因此它们经常走 `allowEagerInit=false` 的路径
- 相应的 `FactoryBean` 如果不能提供稳定的 `getObjectType()`，就会出现“扫描不到”的情况

### 1.2 解决策略（按推荐优先级）

1. **优先：让 `getObjectType()` 返回稳定、明确的类型**
   - 这是最符合 Spring 预期的做法
2. **次选：减少按类型发现对它的依赖**
   - 能按名字注入/获取的场景，显式按名字处理（但要权衡可维护性）
3. **了解即可：通过更激进的 eager init 策略换取可发现性**
   - 在一些场景可以通过允许提前初始化来推断类型，但要非常谨慎：这会把“类型判断”变成“可能触发实例化”，引入副作用与性能风险

对应测试：

- `knownValue`：`getObjectType()` 返回 `Value.class`
- `unknownValue`：`getObjectType()` 返回 `null`

然后用：

- `getBeanNamesForType(Value.class, includeNonSingletons=true, allowEagerInit=false)`

观察点：

- 结果包含 `knownValue`
- 结果不包含 `unknownValue`

## 2. 但读者仍然可以按名字获取到它

- `getBean("unknownValue", Value.class)` 仍然能获取到产品对象

这说明：

- “按类型发现”与“按名字取 bean”是两条不同的路径

- `DefaultListableBeanFactory#getBeanNamesForType`：按类型发现入口（allowEagerInit 会影响 FactoryBean 的处理策略）
- `DefaultListableBeanFactory#doGetBeanNamesForType`：真正遍历候选并判断类型匹配的核心
- `FactoryBeanRegistrySupport#getTypeForFactoryBean`：尝试推断 FactoryBean 的 product type（`getObjectType()` 为 null 时会受限）
- `AbstractBeanFactory#getType`：按 name 获取类型的统一入口（FactoryBean 与普通 bean 都会走这里）
- `FactoryBean#getObjectType`：类型信息的源头（返回 null 会导致“按类型发现”能力退化）

入口：

1) 测试里 `getBeanNamesForType(..., allowEagerInit=false)` 的调用行：对照返回数组为什么缺少 `unknownValue`
2) `DefaultListableBeanFactory#getBeanNamesForType`：观察 allowEagerInit 参数如何影响后续类型推断策略
3) `FactoryBeanRegistrySupport#getTypeForFactoryBean`：观察 `getObjectType()==null` 时容器为什么不能“猜类型”
4) 对照测试后半段 `getBean("unknownValue", Value.class)`：观察按名字取 bean 走的是另一条链路，仍然能获取到产品

## 排障分流：这是定义层问题还是实例层问题？

- “按类型发现不到某个 FactoryBean 的 product（尤其在 allowEagerInit=false）” → **定义层（类型元数据不足）**：检查 `getObjectType()` 是否返回 null（本章结论）
- “按名字能获取到，但按类型扫描/条件判断不稳定” → **定义层（类型匹配路径）**：type matching 与 name-based retrieval 是两条路径（本章第 2 节）
- “在 Boot 条件装配里出现诡异匹配结果” → **定义层 + 条件机制**：FactoryBean 的类型声明不可靠会影响条件判断，建议优先修正 `getObjectType()`（并回看 [10](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)）
- “把它当成缓存/创建 bug 去排查” → **先确认类型信息**：这类问题往往不是实例缓存，而是类型推断与 allowEagerInit 的限制

## 4. 面试常问（FactoryBean 边界）

- 应能够解释：为什么 `getBeanNamesForType(..., allowEagerInit=false)` 可能“按类型发现不到 FactoryBean 的 product”？入口：`SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName`
- 应能够解释：为什么 `getBean("sequence")` 获取到的是 product，但 `getBean("&sequence")` 获取到的是 FactoryBean 本体？入口：`SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`
- 应能够解释：`FactoryBean#isSingleton()` 会如何影响 product 的缓存语义？入口：`SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer` / `SpringCoreBeansFactoryBeanDeepDiveLabTest#nonSingletonFactoryBeanProduct_isNotCached_byTheContainer`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先运行它们）：
- Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先运行通再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanEdgeCasesLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

- `SpringCoreBeansFactoryBeanEdgeCasesLabTest.factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName()`

该实验中注册了两个 FactoryBean：

同一个测试里也验证了：

## 源码锚点（建议从这里设置断点）

- `AbstractBeanFactory#isTypeMatch`：FactoryBean 的 type matching 入口（`getObjectType()` 是否为 null 是关键分支）
- `DefaultListableBeanFactory#getBeanNamesForType`：按类型发现入口（对照 `allowEagerInit=false` 的边界）
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：product 创建与缓存（以及缓存未命中的路径）
- `AbstractBeanFactory#getObjectForBeanInstance`：`&name` / product 分流（排障时确认“读者获取到的是谁”）
- `ResolvableType` 相关路径（IDE 跳转定位）：泛型推断/代理导致的类型信息丢失常见在这里暴露

## 断点闭环（用本仓库 Lab/Test 运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
  - `factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName()`

建议断点：

- 应能够解释清楚：为什么 allowEagerInit=false 时容器不能“猜”出 unknownValue 的类型吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
推荐断点：`AbstractBeanFactory#getType`、`DefaultListableBeanFactory#getBeanNamesForType`、`FactoryBeanRegistrySupport#getTypeForFactoryBean`

## 常见误区与边界

这一章补一个非常实用的边界：

## 3. 常见误区

- **误区 1：以为 FactoryBean 一定能被按类型发现**
  - 取决于 `getObjectType()` 是否可靠。

- **误区 2：类型判断导致条件注解误判**
  - Boot 的条件装配经常依赖 type matching；FactoryBean 的 object type 不准会产生非常诡异的条件匹配结果。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

## 自检要点
应能够解释清楚：

1) **`getObjectType()` 返回 null 会导致哪几类能力失效？**（按类型发现/条件装配/候选收集）
2) **为什么 `allowEagerInit=false` 时更容易“看不到”某些 FactoryBean product？**
3) **如何用断点证明“失败来自 type matching 分支，而不是 bean 根本没注册”？**

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

上一章：[28. 自定义 scope 与 scoped proxy：线程 scope 复现](28-custom-scope-and-scoped-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[30. 注入发生在什么时候：field vs constructor](30-injection-phase-field-vs-constructor.md)

<!-- BOOKIFY:END -->

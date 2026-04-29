# FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`。需要下探源码时，可以从 `FactoryBean#getObjectType()` / `FactoryBean#getObjectType()==null` / `DefaultListableBeanFactory#getBeanNamesForType` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：FactoryBean 边界

先运行 `SpringCoreBeansFactoryBeanEdgeCasesLabTest`，观察“按名字能拿到、按类型却发现不了”的边界；再回到 FactoryBean 的类型暴露、缓存和查找分支解释原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

`FactoryBean` 的核心机制已经在 [23 章](wiring-factorybean-deep-dive.md) 学过了。

- 如果 `FactoryBean#getObjectType()` 返回 `null`
- 那么在“不允许 eager init”的按类型扫描里，它可能不会被当成候选

### 机制边界：条件、分支与结果

**条件**：`allowEagerInit=false` 且 `FactoryBean#getObjectType()==null`
**分支**：`getBeanNamesForType` 不能为了“推断类型”而实例化 FactoryBean
**结果**：按类型扫描 **忽略该 FactoryBean 的 product**
**断点入口**：`DefaultListableBeanFactory#getBeanNamesForType` / `FactoryBeanRegistrySupport#getTypeForFactoryBean`

## 与代理/循环依赖的交叉边界（只要记住一条）

当 FactoryBean 本身或其 product 进入“提前暴露”路径时：

- `getObjectType()` 的不稳定会放大问题：**类型推断不可靠 → 条件判断/候选匹配更容易误判**
- 若在循环依赖里获取到了 early reference（proxy 或半成品），再叠加“类型不可判定”，排障会排查成本高

实务取舍：**FactoryBean 的 product 类型能稳定就稳定**，不要让它成为“隐形类型黑洞”。

## 现象：getBeanNamesForType(..., allowEagerInit=false) 找不到 unknownValue

这类现象“反预期”，但它背后是一个很合理的设计取舍：

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

### 1.2 解决策略（按取舍顺序）

1. **优先：让 `getObjectType()` 返回稳定、明确的类型**
   - 这是最符合 Spring 预期的做法
2. **次选：减少按类型发现对它的依赖**
   - 能按名字注入/获取的场景，显式按名字处理（但要权衡可维护性）
3. **了解即可：通过更激进的 eager init 策略换取可发现性**
  - 在一些场景可以通过允许提前初始化来推断类型，但要谨慎：这会把“类型判断”变成“可能触发实例化”，引入副作用与性能风险

对应测试：

- `knownValue`：`getObjectType()` 返回 `Value.class`
- `unknownValue`：`getObjectType()` 返回 `null`

然后用：

- `getBeanNamesForType(Value.class, includeNonSingletons=true, allowEagerInit=false)`

观察点：

- 结果包含 `knownValue`
- 结果不包含 `unknownValue`

## 但读者仍然可以按名字获取到它

- `getBean("unknownValue", Value.class)` 仍然能获取到产品对象

这说明：

- “按类型发现”与“按名字取 bean”是两条不同的路径

- `DefaultListableBeanFactory#getBeanNamesForType`：按类型发现入口（allowEagerInit 会影响 FactoryBean 的处理策略）
- `DefaultListableBeanFactory#doGetBeanNamesForType`：真正遍历候选并判断类型匹配的核心
- `FactoryBeanRegistrySupport#getTypeForFactoryBean`：尝试推断 FactoryBean 的 product type（`getObjectType()` 为 null 时会受限）
- `AbstractBeanFactory#getType`：按 name 获取类型的统一入口（FactoryBean 与普通 bean 都会走这里）
- `FactoryBean#getObjectType`：类型信息的源头（返回 null 会导致“按类型发现”能力退化）

入口：

1. 测试里 `getBeanNamesForType(..., allowEagerInit=false)` 的调用行：对照返回数组为什么缺少 `unknownValue`
2. `DefaultListableBeanFactory#getBeanNamesForType`：观察 allowEagerInit 参数如何影响后续类型推断策略
3. `FactoryBeanRegistrySupport#getTypeForFactoryBean`：观察 `getObjectType()==null` 时容器为什么不能“猜类型”
4. 对照测试后半段 `getBean("unknownValue", Value.class)`：观察按名字取 bean 走的是另一条链路，仍然能获取到产品

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- “按类型发现不到某个 FactoryBean 的 product（尤其在 allowEagerInit=false）” → **定义层（类型元数据不足）**：检查 `getObjectType()` 是否返回 null（本章结论）
- “按名字能获取到，但按类型扫描/条件判断不稳定” → **定义层（类型匹配路径）**：type matching 与 name-based retrieval 是两条路径（本章第 2 节）
- “在 Boot 条件装配里出现诡异匹配结果” → **定义层 + 条件机制**：FactoryBean 的类型声明不可靠会影响条件判断，优先修正 `getObjectType()`（并回看 [10](boot-spring-boot-auto-configuration.md)）
- “把它当成缓存/创建 bug 去排查” → **先确认类型信息**：这类问题往往不是实例缓存，而是类型推断与 allowEagerInit 的限制

## 面试常问（FactoryBean 边界）

- 需要解释：为什么 `getBeanNamesForType(..., allowEagerInit=false)` 可能“按类型发现不到 FactoryBean 的 product”？入口：`SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName`
- 需要解释：为什么 `getBean("sequence")` 获取到的是 product，但 `getBean("&sequence")` 获取到的是 FactoryBean 本体？入口：`SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`
- 需要解释：`FactoryBean#isSingleton()` 会如何影响 product 的缓存语义？入口：`SpringCoreBeansFactoryBeanDeepDiveLabTest#singletonFactoryBeanProduct_isCached_byTheContainer` / `SpringCoreBeansFactoryBeanDeepDiveLabTest#nonSingletonFactoryBeanProduct_isNotCached_byTheContainer`

## 实验：把现象固定成断言

本章可复核的实验入口：
- Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 从实验现象看边界

## 运行入口

- 入口测试（先运行通过，再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansFactoryBeanEdgeCasesLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

- `SpringCoreBeansFactoryBeanEdgeCasesLabTest.factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName()`

该实验中注册了两个 FactoryBean：

同一个测试里也验证了：

## 源码锚点：从这里设置断点

- `AbstractBeanFactory#isTypeMatch`：FactoryBean 的 type matching 入口（`getObjectType()` 是否为 null 是关键分支）
- `DefaultListableBeanFactory#getBeanNamesForType`：按类型发现入口（对照 `allowEagerInit=false` 的边界）
- `FactoryBeanRegistrySupport#getObjectFromFactoryBean`：product 创建与缓存（以及缓存未命中的路径）
- `AbstractBeanFactory#getObjectForBeanInstance`：`&name` / product 分流（排障时确认“读者获取到的是谁”）
- `ResolvableType` 相关路径（IDE 跳转定位）：泛型推断/代理导致的类型信息丢失常见在这里暴露

## 断点闭环（用本仓库实验/测试运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
  - `factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName()`

断点入口：

- 需要解释清楚：为什么 allowEagerInit=false 时容器不能“猜”出 unknownValue 的类型吗？
对应实验/测试：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
断点入口：`AbstractBeanFactory#getType`、`DefaultListableBeanFactory#getBeanNamesForType`、`FactoryBeanRegistrySupport#getTypeForFactoryBean`

## 边界：FactoryBean 边界

这一章补一个实用的边界：

## 误判点：FactoryBean 边界

- **误区 1：以为 FactoryBean 一定能被按类型发现**
  - 取决于 `getObjectType()` 是否可靠。

- **误区 2：类型判断导致条件注解误判**
  - Boot 的条件装配经常依赖 type matching；FactoryBean 的 object type 不准会产生反预期的条件匹配结果。

## 验收口径：FactoryBean 边界
需要解释清楚：

1. **`getObjectType()` 返回 null 会导致哪几类能力失效？**（按类型发现/条件装配/候选收集）
2. **为什么 `allowEagerInit=false` 时更容易“看不到”某些 FactoryBean product？**
3. **如何用断点证明“失败来自 type matching 分支，而不是 bean 根本没注册”？**

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

<!-- BOOKIFY:END -->

# 11. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把输入层解析或 AOT 契约完成验证；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。

    本章围绕50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象展开，主线可以概括为：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。

    对照入口：`SpringCoreBeansBeanDefinitionValueResolutionLabTest`。需要下探源码时，可以从 `BeanDefinitionValueResolver#resolveValueIfNecessary` / `CustomEditorConfigurer#postProcessBeanFactory` / `PropertyEditorRegistrySupport#registerCustomEditor` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[10. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](10-built-in-factorybeans-gallery.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见误区清单（建议反复对照）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->


## 导读

本章围绕「50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansBeanDefinitionValueResolutionLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（AOT，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/aot.html
- 官方文档对照（Spring Boot Reference，适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBeanDefinitionValueResolutionLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansBeanDefinitionValueResolutionLabTest`，再用 `SpringCoreBeansPropertyEditorLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`BeanDefinitionValueResolver#resolveValueIfNecessary`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“0. `${...}` vs `#{...}` 的职责边界（先分清再排障）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，SpEL 与 @Value 表达式）：https://docs.spring.io/spring-framework/reference/core/expressions.html
> 官方参考（Spring Framework 6.2.x，类型转换（ConversionService））：https://docs.spring.io/spring-framework/reference/core/validation/convert.html

初学者在学习 Spring Beans 时，最容易遇到一个“看起来像黑盒”的问题：

1) **PropertyEditor（可插拔的类型转换）**：决定 “字符串怎么变成目标类型”
2) **BeanDefinition 值解析（BeanDefinitionValueResolver）**：决定 “引用/集合/Map/Properties 等 value 怎么被解析成可注入的最终值”

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：BeanDefinition 中存在“定义层 value”
**分支**：`BeanDefinitionValueResolver` 先解析引用/集合/占位符 → `TypeConverterDelegate` 再做类型转换
**结果**：最终值写入属性（或因转换失败抛异常）
**断点建议**：`BeanDefinitionValueResolver#resolveValueIfNecessary`

## 0. `${...}` vs `#{...}` 的职责边界（先分清再排障）

- `${...}`：占位符解析（Environment/PropertySources）
- `#{...}`：SpEL 求值（表达式计算/bean 引用）

它们都会在 **值解析之后** 再进入类型转换。误判这一步，是“注入失败排障”最大噪声源。

本章有 2 个入口测试：

需要观察的现象：

- 未注册 PropertyEditor 时：`String -> 自定义类型` 注入失败（并且失败发生在 refresh/实例化阶段，而不是注册阶段）
- 注册 PropertyEditor 后：同样的字符串可以成功注入为自定义对象
- BeanDefinitionValueResolver 可以把：
  - `RuntimeBeanReference` 解析为真实 bean 引用
  - `ManagedList/ManagedMap/ManagedProperties` 解析为可注入集合
  - `TypedStringValue` 交给类型转换链路转换为目标类型

---

## 1. 是什么：需要分清 2 个“发生位置不同”的问题

### 1.1 值解析（value resolution）解决的是：value 到底是什么？

BeanDefinition 里保存的 value，可能是：

- 字符串（或 `TypedStringValue`）
- bean 引用（`RuntimeBeanReference`）
- 容器托管集合（`ManagedList/ManagedMap/ManagedProperties`）

这些 value 的共同点是：**它们不是“最终注入到对象里的值”**，只是“定义层表达”。

### 1.2 类型转换（type conversion）解决的是：value 怎么变成目标类型？

当容器决定把某个 value 填到对象属性上时，会发生转换：

- `String -> int`（很常见）
- `String -> enum`
- `String -> 自定义类型`（需要读者注册转换器/editor）

PropertyEditor 是一种“老机制”，但它仍然在 beans 主线上存在并且经常被遗留配置依赖。

---

### 2.1 自定义 PropertyEditor（用 `CustomEditorConfigurer` 注册）

最小闭环就是三件事：

1) 一个目标类型（例如 `HostAndPort`）
2) 一个 `PropertyEditor`（实现 `setAsText`）
3) 一个注册器（`PropertyEditorRegistrar`） + `CustomEditorConfigurer`（把注册动作接入 BeanFactoryPostProcessor 阶段）

可以直接对照本仓库的最小实现：

若想看清 `BeanDefinitionValueResolver` 的分支，最直接的方式是显式使用这些类型：

- `RuntimeBeanReference("h1")`
- `TypedStringValue("8080")`
- `ManagedList / ManagedMap / ManagedProperties`

对应示例见：

---

## 3. 原理：把现象放回容器主线（定义层 → 实例层）

读者只要记住下面这条主线，80% 的“值注入困惑”都能解释清楚：

1) **定义层：** BeanDefinition 保存元数据（包括 propertyValues）
2) **实例层：** 创建实例后，进行属性填充（populate / applyPropertyValues）
3) **值解析：** 把“定义层 value”解析成可注入对象（引用/集合/占位符）
4) **类型转换：** 把解析后的 value 转成目标属性类型（PropertyEditor/ConversionService）

- “注册了 BeanDefinition 就等于创建了对象” → 错
- “类型转换发生在注册阶段” → 错，通常发生在属性填充阶段

---

### 4.1 PropertyEditor 的注册（为什么它能影响注入）

1) `CustomEditorConfigurer#postProcessBeanFactory`
2) `PropertyEditorRegistrySupport#registerCustomEditor`

观察点：

- 哪些目标类型被注册了 editor（key）
- editor 实例是什么（value）
- 它注册到了哪个 registry（通常最终影响 `BeanWrapper`/`TypeConverter`）

### 4.2 BeanDefinitionValueResolver（引用/集合/Map 的解析入口）

若想看清不同分支：

- `BeanDefinitionValueResolver#resolveReference`（`RuntimeBeanReference`）
- `BeanDefinitionValueResolver#resolveManagedList`
- `BeanDefinitionValueResolver#resolveManagedMap`
- `BeanDefinitionValueResolver#resolveManagedProperties`

关键观察点：

- `originalValue` 的真实类型（决定走哪个分支）
- `resolvedValue`（解析后的结果）
- `beanName` / `mbd`：当前正在填充哪个 bean

### 4.3 类型转换（字符串如何变成目标属性类型）

- `TypeConverterDelegate#convertIfNecessary`
- `BeanWrapperImpl#convertForProperty`（或类似转换入口）

观察点：

- `requiredType`（目标属性类型）
- `convertedValue`（转换结果）
- `propertyName`（哪个属性触发的转换）

---

### 4.4 属性路径解析与 auto-grow（复杂属性常见误区）

当相应的属性路径包含嵌套/集合/Map 时，BeanWrapper 会走更复杂的路径解析：

- `order.items[0].price`
- `props["k"]`

常见边界：

- 中间对象为 `null` 且未启用 auto-grow → `NullValueInNestedPathException`
- 集合下标越界 / Map key 不存在 → `InvalidPropertyException`

断点入口：

- `BeanWrapperImpl#setPropertyValue`
- `AbstractNestablePropertyAccessor#processLocalProperty`

---

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

> **在配置中写的是字符串/引用/集合，为什么运行时会变成对象？该步骤发生在何处？如何通过断点证明？**

这一章将两个常被混淆的机制分别阐明，并用 Lab 使读者能够通过断点完成验证：

## 0. 复现入口（可运行）

1) PropertyEditor（自定义 editor）
   - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java`

2) BeanDefinitionValueResolver（引用/集合/Map/Properties）
   - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBeanDefinitionValueResolutionLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPropertyEditorLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionValueResolutionLabTest test
```

## 2. 使用方式：最小可用写法（以 Lab 为准）

- `SpringCoreBeansPropertyEditorLabTest#HostAndPortEditor`
- `SpringCoreBeansPropertyEditorLabTest#HostAndPortRegistrar`
- `SpringCoreBeansPropertyEditorLabTest#withCustomPropertyEditor_stringToCustomType_shouldSucceed`

### 2.2 显式构造“定义层 value”（以便断点命中不同分支）

- `SpringCoreBeansBeanDefinitionValueResolutionLabTest#registerDemoBean`

## 4. 怎么实现的：关键类/方法 + 断点入口 + 观察点

推荐断点（把“定义层 value → 注入对象”拆成三段看）：

1) `AbstractAutowireCapableBeanFactory#applyPropertyValues`
   - 观察：属性填充阶段开始把 `PropertyValues` 应用到 bean 上
2) `BeanDefinitionValueResolver#resolveValueIfNecessary`
   - 观察：`RuntimeBeanReference`/集合/TypedStringValue 等不同“定义层 value”如何被分派解析
3) `BeanWrapperImpl#setPropertyValues` / `AbstractNestablePropertyAccessor#setPropertyValue`
   - 观察：最终 set 到 bean 字段/属性上的到底是什么对象，以及转换是否发生

推荐观察点（看类型分派比看字符串更快）：

- `value` 的实际类型：`RuntimeBeanReference` / `ManagedList` / `TypedStringValue` / plain literal
- `resolvedValue` / `convertedValue`：解析/转换后的最终值
- `typeConverter` / `conversionService`：走 ConversionService 还是 PropertyEditor（可与 [36](../part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md) 对照）

## 常见误区与边界

所以很多新手误区来自于把 1) 和 2) 混在一起：

### 常见边界与误区

1) **误区：类型转换都由 ConversionService 负责**
   - 真实情况：beans 主线里 ConversionService 与 PropertyEditor 可能都参与；PropertyEditor 仍可能影响行为。
2) **误区：PropertyEditor 是线程安全的**
   - 很多 editor 是有状态的（setValue），不要在非预期场景复用实例。
3) **误区：看到 `RuntimeBeanReference` 就以为“这是 XML 才有的东西”**
   - 这是 beans 的抽象：在任何输入源（XML/Properties/Groovy/程序化注册）都可以表达“引用”。

## 排障决策表（属性注入：解析 vs 转换 vs 赋值）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| 属性值是 `RuntimeBeanReference`，但最终没解析成对象 | 引用解析失败 / beanName 不存在 | 断点 `BeanDefinitionValueResolver#resolveValueIfNecessary`；看 `RuntimeBeanReference#getBeanName` | 修正 beanName/alias；确认定义是否注册 | `SpringCoreBeansBeanDefinitionValueResolutionLabTest` |
| `TypedStringValue` 注入失败（TypeMismatch） | 转换链路没命中合适 converter/editor | 断点 `BeanWrapperImpl#setPropertyValues` / `TypeConverterDelegate#convertIfNecessary`；看 requiredType 与分支 | 安装/注册 ConversionService 或 PropertyEditor；区分占位符/SpEL/转换三连 | `SpringCoreBeansPropertyEditorLabTest`（配合 [36](../part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md)） |
| 容易误以为“值已解析”，但占位符未解析 | embedded value resolver non-strict 放行 | 断点 `AbstractBeanFactory#resolveEmbeddedValue` | 启用 strict 或补齐 property source/key | [34](../part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md) |
| PropertyEditor 行为偶发、并发下异常 | editor 有状态且非线程安全 | 看 editor 是否复用、是否共享实例（setValue） | 避免共享 editor 实例；优先用 ConversionService | 结合本章与性能/并发相关用例复盘 |

## 面试常问（BeanDefinition 值解析：为什么它不是“单纯 setProperty”）

### Q1：定义层 value 是如何落到对象属性上的？关键分派点在哪？

- 标准答案（可复述）：
  - 创建阶段先在 `applyPropertyValues` 进入属性填充；定义层 value 由 `BeanDefinitionValueResolver#resolveValueIfNecessary` 按类型分派（引用/集合/字符串等）；最终由 `BeanWrapper` 写入属性并触发类型转换。
- 证据链（方法级）：
  - `AbstractAutowireCapableBeanFactory#applyPropertyValues`
  - `BeanDefinitionValueResolver#resolveValueIfNecessary`
  - `BeanWrapperImpl#setPropertyValues` / `AbstractNestablePropertyAccessor#setPropertyValue`
- 最小复现：
  - `SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`

### Q2：PropertyEditor 和 ConversionService 的边界是什么？为什么现代更推荐 ConversionService？

- 标准答案（可复述）：
  - ConversionService 更现代、更易组合且能感知类型描述；PropertyEditor 主要是历史兼容且常有状态，容易引入并发/复用问题。排障时要能在断点里确认这次到底走了哪条分支。

## 自检要点
- 应能够解释清楚：BeanDefinition 的 value 解析发生在创建阶段的哪一步吗？（提示：applyPropertyValues → value resolver → BeanWrapper）
- 应能够区分：这是“引用解析”（`RuntimeBeanReference`）还是“字符串转换”（`TypedStringValue` → convert）吗？
- 遇到“属性注入值不对/转换失败/引用解析失败”时，能否用 3 个断点把问题固定在“解析 vs 转换 vs 赋值”的哪一段？

## 小结与下一章

1) `AbstractAutowireCapableBeanFactory#applyPropertyValues`（主线入口）
2) `BeanDefinitionValueResolver#resolveValueIfNecessary`（按类型分派）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBeanDefinitionValueResolutionLabTest.java`

上一章：[49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](10-built-in-factorybeans-gallery.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90. 常见误区清单（建议反复对照）](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->

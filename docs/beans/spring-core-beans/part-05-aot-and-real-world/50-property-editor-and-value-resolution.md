# 50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象

## 导读

- 本章主题：**50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBeanDefinitionValueResolutionLabTest.java`

## 机制主线

新手在学 Spring Beans 时最容易卡在一个“看起来像黑盒”的问题上：

1) **PropertyEditor（可插拔的类型转换）**：决定 “字符串怎么变成目标类型”
2) **BeanDefinition 值解析（BeanDefinitionValueResolver）**：决定 “引用/集合/Map/Properties 等 value 怎么被解析成可注入的最终值”

---

本章有 2 个入口测试：

你要观察的现象：

- 未注册 PropertyEditor 时：`String -> 自定义类型` 注入失败（并且失败发生在 refresh/实例化阶段，而不是注册阶段）
- 注册 PropertyEditor 后：同样的字符串可以成功注入为自定义对象
- BeanDefinitionValueResolver 可以把：
  - `RuntimeBeanReference` 解析为真实 bean 引用
  - `ManagedList/ManagedMap/ManagedProperties` 解析为可注入集合
  - `TypedStringValue` 交给类型转换链路转换为目标类型

---

## 1. 是什么：你要分清 2 个“发生位置不同”的问题

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
- `String -> 自定义类型`（需要你注册转换器/editor）

PropertyEditor 是一种“老机制”，但它仍然在 beans 主线上存在并且经常被遗留配置依赖。

---

### 2.1 自定义 PropertyEditor（用 `CustomEditorConfigurer` 注册）

最小闭环就是三件事：

1) 一个目标类型（例如 `HostAndPort`）
2) 一个 `PropertyEditor`（实现 `setAsText`）
3) 一个注册器（`PropertyEditorRegistrar`） + `CustomEditorConfigurer`（把注册动作接入 BeanFactoryPostProcessor 阶段）

你可以直接对照本仓库的最小实现：

如果你想看清 `BeanDefinitionValueResolver` 的分支，最直接的方式是显式使用这些类型：

- `RuntimeBeanReference("h1")`
- `TypedStringValue("8080")`
- `ManagedList / ManagedMap / ManagedProperties`

对应示例见：

---

## 3. 原理：把现象放回容器主线（定义层 → 实例层）

你只要记住下面这条主线，80% 的“值注入困惑”都能解释清楚：

1) **定义层：** BeanDefinition 保存元数据（包括 propertyValues）
2) **实例层：** 创建实例后，进行属性填充（populate / applyPropertyValues）
3) **值解析：** 把“定义层 value”解析成可注入对象（引用/集合/占位符）
4) **类型转换：** 把解析后的 value 转成目标属性类型（PropertyEditor/ConversionService）

- “我注册了 BeanDefinition 就等于创建了对象” → 错
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

如果你想看清不同分支：

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

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> **我在配置里写的是字符串/引用/集合，为什么运行起来就变成了对象？这一步发生在哪里？怎么断点证明？**

这一章把两个常被混在一起的机制拆开讲清楚，并用 Lab 让你能下断点验证：

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

## 2. 怎么用：最小可用写法（以 Lab 为准）

- `SpringCoreBeansPropertyEditorLabTest#HostAndPortEditor`
- `SpringCoreBeansPropertyEditorLabTest#HostAndPortRegistrar`
- `SpringCoreBeansPropertyEditorLabTest#withCustomPropertyEditor_stringToCustomType_shouldSucceed`

### 2.2 显式构造“定义层 value”（以便断点命中不同分支）

- `SpringCoreBeansBeanDefinitionValueResolutionLabTest#registerDemoBean`

## 4. 怎么实现的：关键类/方法 + 断点入口 + 观察点

推荐断点：

推荐断点组合：

推荐断点：

## 常见坑与边界

所以很多新手误区来自于把 1) 和 2) 混在一起：

## 5. 常见边界与误区

1) **误区：类型转换都由 ConversionService 负责**
   - 真实情况：beans 主线里 ConversionService 与 PropertyEditor 可能都参与；PropertyEditor 仍可能影响行为。
2) **误区：PropertyEditor 是线程安全的**
   - 很多 editor 是有状态的（setValue），不要在非预期场景复用实例。
3) **误区：看到 `RuntimeBeanReference` 就以为“这是 XML 才有的东西”**
   - 这是 beans 的抽象：你在任何输入源（XML/Properties/Groovy/程序化注册）都可以表达“引用”。

## 小结与下一章

1) `AbstractAutowireCapableBeanFactory#applyPropertyValues`（主线入口）
2) `BeanDefinitionValueResolver#resolveValueIfNecessary`（按类型分派）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansPropertyEditorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansPropertyEditorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansBeanDefinitionValueResolutionLabTest.java`

上一章：[49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](49-built-in-factorybeans-gallery.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90. 常见坑清单（建议反复对照）](../appendix/025-90-common-pitfalls.md)

<!-- BOOKIFY:END -->

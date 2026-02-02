# 34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`Environment#resolvePlaceholders` / `AbstractBeanFactory#resolveEmbeddedValue` / `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`
    - 推荐 Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](33-autowire-candidate-selection-primary-priority-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[35. MergedBeanDefinition：合并后的 RootBeanDefinition](35-merged-bean-definition.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast**
- 阅读方式建议：先运行本章 Lab，把两种行为（缺失占位符“原样通过” vs fail-fast）固定成断言；再对照 `resolveEmbeddedValue` 与 `PropertySourcesPlaceholderConfigurer` 的断点，看清“到底是谁决定了 strict/non-strict”。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（SpEL，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/expressions.html


!!! summary "本章要点"

    - `@Value` 本身不“读配置”，它把字符串交给 BeanFactory 的 **embedded value resolver** 解析（`${...}`/`#{...}`），再进入后续注入/转换。
    - 默认情况下（本章 Lab 的最小纯容器），embedded value resolver 往往委托给 `Environment.resolvePlaceholders(..)`，它是 **non-strict**：缺失 key 时，`${...}` 可能原样保留，不一定 fail-fast。
    - 想要 strict fail-fast，典型方式是注册 `PropertySourcesPlaceholderConfigurer`（BFPP）：把“缺失占位符就失败”的策略显式安装到容器早期流程里。
    - 默认值语法 `${key:default}` 是在 strict/non-strict 都应该掌握的“回退手段”：缺失 key 时不会失败，而是注入 default（本仓库 Lab 已补齐对照）。
    - 排障时先拆三件事：**占位符解析（本章）**、**SpEL 求值**、**类型转换**（见 [44](../part-05-aot-and-real-world/44-spel-and-value-expression.md)、[36](36-type-conversion-and-beanwrapper.md)）。

!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

## 机制主线：`@Value` 严不严格，取决于 resolver

> 官方参考（Spring Framework 6.2.x，SpEL 与 @Value 表达式）：https://docs.spring.io/spring-framework/reference/core/expressions.html

这一章回答一个在真实项目里非常折磨人的问题：

> 为什么写了 `@Value("${demo.missing}")`，应用居然没有启动失败？
> 注入进来的值甚至变成了字符串 `"${demo.missing}"`？

先说结论（背这句就够排 80% 的误区）：

> **占位符解析是否 strict，不是 @Value 决定的，而是 BeanFactory 里安装的 embedded value resolver 决定的。**

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：BeanFactory 安装了哪一种 embedded value resolver  
**分支**：  
- 默认 resolver（多为 `Environment#resolvePlaceholders`）→ **non-strict**  
- `PropertySourcesPlaceholderConfigurer` → **strict fail-fast**  
**结果**：缺失占位符要么“原样保留”，要么直接抛异常  
**断点建议**：`AbstractBeanFactory#resolveEmbeddedValue` / `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`

## 1. 先把链路拆开：`@Value` 不是“直接读 Environment”

把 `@Value` 想清楚，读者就不会把问题误判成“配置没加载”或“环境没生效”：

1) `@Value` 注解先被基础设施处理器识别（通常是 `AutowiredAnnotationBeanPostProcessor`）
2) 它把注解里的字符串（例如 `"${demo.present}"`）交给 `BeanFactory#resolveEmbeddedValue`
3) 解析后的结果再进入注入（必要时再做类型转换，见 [36](36-type-conversion-and-beanwrapper.md)）

所以严格与否，最终会体现在：

- `AbstractBeanFactory#resolveEmbeddedValue` 的返回值是什么？

---

## 2. 默认行为（non-strict）：缺失占位符可能原样保留

对应实验：

- `SpringCoreBeansValuePlaceholderResolutionLabTest#defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved`

可以观察到两个稳定现象：

- `demo.present` 存在 → `@Value("${demo.present}")` 注入为 `"hello"`
- `demo.missing` 缺失 → `@Value("${demo.missing}")` 注入为 `"${demo.missing}"`（没有 fail-fast）

本章 Lab 的输出已经把关键事实写死：

- 默认 embedded value resolver 往往委托给 `Environment.resolvePlaceholders(..)`
- `resolvePlaceholders(..)` 默认是 **non-strict**：解析不到时可能保留原样 `"${...}"`

> 这类行为最大的误区在于：系统能启动，但在运行中才发现“配置没生效”，排障成本更高。

---

## 3. strict fail-fast：注册 `PropertySourcesPlaceholderConfigurer`（BFPP）

对应实验：

- `SpringCoreBeansValuePlaceholderResolutionLabTest#propertySourcesPlaceholderConfigurer_canMakeMissingPlaceholderFailFast`

实验注册了一个 BFPP：

- `PropertySourcesPlaceholderConfigurer`
  - 并设置 `ignoreUnresolvablePlaceholders = false`

可以观察到：

- `refresh()` 直接失败
- root cause 包含 “Could not resolve placeholder 'demo.missing'”

关键点是它的**时机与职责**：

- BFPP 发生在 bean 实例化之前，因此它可以把“严格策略”尽早装进容器
- 这类 fail-fast 往往比“运行到某个业务路径才发现值不对”更健康

对照阅读（BFPP vs BPP，谁更早）：
- [06. 容器扩展点：BFPP vs BPP](../part-01-ioc-container/017-06-post-processors.md)

---

## 4. 默认值（强烈推荐）：`${key:default}` 让“缺失配置”可控

很多团队走 strict 的原因是：他们宁可启动失败，也不想“原样字符串通过”。

但 strict 并不意味着必须“所有 key 都必须配置齐全”。应当掌握一个更工程化的回退策略：

- `${demo.missing:default-value}`

对应实验（本仓库已补齐）：

- `SpringCoreBeansValuePlaceholderResolutionLabTest#propertySourcesPlaceholderConfigurer_strictMode_allowsMissingPlaceholderWhenDefaultValueIsProvided`

可以观察到：

- strict 模式仍然存在（缺失 key 会 fail-fast）
- 但当读者显式提供 default value 时，缺失 key 不会失败，注入变得可控

这能帮助读者把“必须配置”的项与“可选配置”的项区分开来。

---

## 5. Debug 断点闭环：把 strict/non-strict 变成可见证据

### 4.1 推荐断点（按收益排序）

1) `AbstractBeanFactory#resolveEmbeddedValue`：看 `"${...}"` 最终解析成了什么
2) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：看 `@Value` 注入点把字符串交给谁解析
3) `AbstractApplicationContext#prepareBeanFactory`：看默认 embedded value resolver 是何时安装的（non-strict 的来源）
4) `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`：strict 策略如何介入（BFPP）

### 4.2 固定观察点（watch list）

- 输入字符串：`"${demo.present}"` / `"${demo.missing}"`
- 输出字符串：解析后的结果是否仍包含 `"${"`
- 当前 Environment 的 PropertySources（尤其是 key 是否存在、以及优先级顺序）

---

## 6. 排障分流：先确定问题停留在“解析/求值/转换”的哪一步

| 现象 | 最可能根因 | 下一步 |
| --- | --- | --- |
| 值是 `"${demo.missing}"` 原样 | non-strict resolver 放行了缺失占位符 | 回到本章 2/3；考虑启用 strict |
| 直接启动失败：Could not resolve placeholder | strict resolver fail-fast（更健康） | 修复 property source / key / 默认值策略 |
| `${...}` 解析没问题，但 `#{...}` 异常 | SpEL 求值问题 | 去看 [44](../part-05-aot-and-real-world/44-spel-and-value-expression.md) |
| 解析出来是字符串，但注入到 `int/Duration/...` 失败 | 类型转换问题 | 去看 [36](36-type-conversion-and-beanwrapper.md) |
| `@Value` 完全不生效（字段没注入） | 注解处理器未注册/容器能力不完整 | 回到 [12](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) |

---

## 源码调用链（方法级）：`${...}` 到底在哪一步被解析

在排 `@Value` 时，最容易误诊的是把“占位符解析 / SpEL 求值 / 类型转换”混为一谈。本章只关心第一步（占位符解析），最短调用链如下：

1) 注入点入口：`AutowiredAnnotationBeanPostProcessor#postProcessProperties`（识别 `@Value` 并触发解析）
2) 解析入口：`AbstractBeanFactory#resolveEmbeddedValue`（把 `"${...}"` 交给 embedded value resolvers 逐个处理）
3) strict 策略来源：`PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`（BFPP，在定义阶段安装/替换 resolver）
4) 默认（non-strict）来源：`AbstractApplicationContext#prepareBeanFactory`（安装默认 embedded value resolver）

在 `resolveEmbeddedValue` 里看“输入/输出字符串是否仍包含 `${`”，就能快速判断 strict/non-strict 是否生效。

## 面试常问（`@Value`：strict vs non-strict）

### Q1：为什么缺失的 `${...}` 有时会原样字符串通过、有时会 fail-fast？

- 标准答案（可复述）：
  - 因为 embedded value resolver 的策略不同：non-strict 会放行未解析占位符；strict 会在缺失时抛异常。策略不是 `@Value` 决定的，而是容器里安装了什么 resolver/placeholder configurer 决定的。
- 证据链（方法级）：
  - `AbstractBeanFactory#resolveEmbeddedValue`
  - `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`
- 最小复现：
  - `SpringCoreBeansValuePlaceholderResolutionLabTest`

### Q2：如何快速区分“占位符解析问题” vs “SpEL 求值问题” vs “类型转换问题”？

- 标准答案（可复述）：
  - 先在 `resolveEmbeddedValue` 看解析后的字符串；如果 `${...}` 还在，是解析问题；如果 `${...}` 已变成字符串但 `#{...}` 异常，是 SpEL 问题；如果字符串已正确但注入到目标类型失败，是类型转换问题（看 `convertIfNecessary`）。
- 证据链（方法级）：
  - 解析：`resolveEmbeddedValue`
  - 求值：`StandardBeanExpressionResolver#evaluate`（见 [44](../part-05-aot-and-real-world/44-spel-and-value-expression.md)）
  - 转换：`TypeConverterDelegate#convertIfNecessary`（见 [36](36-type-conversion-and-beanwrapper.md)）

## 自检要点
- 应能够解释清楚：为什么有时缺失 `${...}` 会“原样字符串通过”，有时会 fail-fast 吗？
- strict/non-strict 是谁决定的？是 `@Value` 注解本身吗？（提示：embedded value resolver / `PropertySourcesPlaceholderConfigurer`）
- 如何在排障时快速分清：这是占位符解析问题、SpEL 求值问题、还是类型转换问题？（提示：三连分层）

## 小结与下一章

这一章读者只要记住两件事就够了：

1) `@Value` 是否 strict 取决于 embedded value resolver（不是注解本身）
2) strict fail-fast 的典型来源是 `PropertySourcesPlaceholderConfigurer`（BFPP，早期介入）
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansValuePlaceholderResolutionLabTest`，再用 `SpringCoreBeansValuePlaceholderResolutionLabTest#defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#resolveEmbeddedValue` / `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“6. 排障分流：先确定问题停留读到“解析/求值/转换”的哪一步”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`

上一章：[33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](33-autowire-candidate-selection-primary-priority-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[35. MergedBeanDefinition：合并后的 RootBeanDefinition](35-merged-bean-definition.md)

<!-- BOOKIFY:END -->

# 38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线

## 导读

- 本章主题：**38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

## 机制主线

这一章补齐一个“你每天都在用，但很少系统化理解”的核心机制：

一句话先建立心智模型：

> **Environment = “属性解析器 + profiles 决策器”。它通过一串有序的 PropertySources 来解析 key。**  
> 你看到的“覆盖/优先级/不生效”，几乎都能归因到：**PropertySource 顺序** 或 **解析发生的时机**。

---

---

## 1. 是什么：Environment 抽象解决的是什么问题？

Environment 抽象的核心目标是：把“从哪里拿配置、如何拿配置、如何在不同环境切换”统一成一个可插拔模型。

它主要回答两类问题：

1) **属性从哪里来？**（PropertySource abstraction）
   - system properties / env vars / 文件 / 自定义来源（DB/加密配置中心/动态计算）
2) **哪个配置生效？**（PropertySources precedence + profiles）
   - 相同 key 在多个来源都存在时，谁覆盖谁？
   - 某个 bean 是否应该被注册（`@Profile`）？

在 Spring 容器内部，Environment 的典型落点包括：

- `Environment#getProperty(...)`：直接取值
- `@Value("${...}")`：通过 BeanFactory 的 embedded value resolver 间接取值（见第 4 节）
- `@Profile`：决定某些 `BeanDefinition` 是否会被注册（在配置类解析阶段）

---

## 2. PropertySource 抽象：属性到底来自哪里？

你可以把一个 PropertySource 理解为一个最小接口：

> 给你一个 key，我告诉你 value（可能没有）。

Spring 把“多个来源”组织成一个有序链表：

- `MutablePropertySources`
  - `addFirst/addLast/addBefore/addAfter` 控制顺序

**顺序就是优先级：越靠前，优先级越高。**

这也是为什么在真实项目里你经常会看到类似问题：

这并不神秘：只是你的 key 在更高优先级的 PropertySource 里已经存在。

---

## 3. `@PropertySource`：它是怎么进入 Environment 的？

`@PropertySource` 的关键点是：

- 它不是一个“读取文件并注入”的注解
- 它的效果是：**把一个 PropertySource 加到 Environment 的 propertySources 列表中**

它发生在哪个阶段？

- 发生在 **配置类解析阶段**（`ConfigurationClassPostProcessor` 的工作范围内）
- 早于 bean 实例化（因为它影响后续 `@Value` / 条件装配等）

1) 这个 property source 是否被创建并添加到了 environment
2) 它在链表中的位置（顺序）是什么

---

## 4. 占位符解析：`@Value("${...}")` 与 Environment 的连接点

很多新手会把 `@Value("${k}")` 理解为“直接读 Environment”，但严格来说它是：

1) `@Value` 被注解处理器识别（通常由 `AutowiredAnnotationBeanPostProcessor`）
2) value 字符串交给 BeanFactory 做 embedded value 解析
3) embedded value resolver 通常会委托给 Environment 做 placeholder 解析

因此你在排障时至少要分清两条链：

- **读取链（Environment）：** `environment.getProperty("k")`
- **注入链（@Value）：** `@Value("${k}")` → `BeanFactory.resolveEmbeddedValue` → Environment

对照阅读：

- [34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](34-value-placeholder-resolution-strict-vs-non-strict.md)

---

## 5. 怎么用：最小可用手段（按“排障优先级”排序）

### 5.2 `@PropertySource`（适合“给一个默认文件来源”）

适用场景：

- 你希望一个配置类自带一份 properties（常用于纯 Spring 应用或组件化库）

- 它通常不是最高优先级
- 它不负责“让 @Value 变严格”，strict 行为一般要通过 BFPP（例如 `PropertySourcesPlaceholderConfigurer`）控制

### 5.3 profiles（Environment 另一个核心维度）

关键记忆点：

- active profiles 必须在 refresh 前设置（否则不会影响配置类解析/bean 注册）

---

### 6.1 `@PropertySource` 进入链路

- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`（配置类解析总入口）
- `PropertySourceProcessor#processPropertySource`（把 @PropertySource 加入 Environment 的关键点）

推荐观察点：

- `environment.getPropertySources()`：当前有哪些 sources（名称/顺序）
- `propertySource.getName()`：@PropertySource 加入的那一项叫什么

### 6.2 取值链路（Environment）

- `PropertySourcesPropertyResolver#getProperty`（按顺序遍历 property sources）

观察点：

- `key`：本次在解析哪个 key
- 命中的是哪个 property source（通常能从调用栈/局部变量判断）

### 6.3 注入链路（@Value）

- `AbstractBeanFactory#resolveEmbeddedValue`

观察点：

- `value`：原始字符串是不是 `"${...}"`
- resolver 列表里有没有“基于 Environment 的 resolver”

---

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> `@Value("${...}")` 到底从哪里取值？  
> 为什么同一个 key 在不同环境/不同启动方式下值不一样？  
> `@PropertySource` 加了也不生效，或者被别的配置覆盖了，怎么断点证明？

## 0. 复现入口（可运行）

本章新增 Lab（推荐先跑通再下断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEnvironmentPropertySourceLabTest test
```

本章强相关的已存在 Labs（用于补齐“占位符解析”和“profiles”）：

- `SpringCoreBeansValuePlaceholderResolutionLabTest`（[34](34-value-placeholder-resolution-strict-vs-non-strict.md)）
- `SpringCoreBeansProfileRegistrationLabTest`（profiles 的最小可复现）

- “我在文件里配置了 `demo.key=foo`，但运行时却是 bar”
- “我加了 @PropertySource 还是没生效”

你在断点里应该验证的是：

### 5.1 运行前插入/覆盖 PropertySource（最强、最可控）

本章 Lab 里用的就是这个方式：

- 在 `refresh()` 之前：`environment.getPropertySources().addFirst(...)`
- 明确覆盖某个 key，并且能在断点里看到顺序变化

`@Profile` 的最小复现入口见：

- `SpringCoreBeansProfileRegistrationLabTest`

## 6. Debug / 断点入口与观察点

推荐断点：

推荐断点：

推荐断点：

## 常见坑与边界

边界：

## 7. 常见误区（以及为什么你在真实项目里会踩）

1) **误区：`@PropertySource` 一定覆盖其它配置**
   - 实际是“按顺序”。更高优先级的 source 先命中就结束。
2) **误区：我把 propertySource 加到 environment 里，已经创建过的 bean 会自动更新**
   - 绝大多数场景不会。注入发生在创建时；后改 Environment 不会 retroactive。
3) **误区：profiles 随时都能改**
   - profiles 影响的是“注册阶段”，必须在 refresh 前设置才有意义。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

上一章：[37. 泛型匹配与注入坑：ResolvableType 与代理导致的类型信息丢失](37-generic-type-matching-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界](39-beanfactory-api-deep-dive.md)

<!-- BOOKIFY:END -->

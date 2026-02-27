# 21. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`PropertySourcesPropertyResolver#getProperty` / `Environment#getProperty(...)` / `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
    - 推荐 Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[20. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失](20-generic-type-matching-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[22. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界](22-beanfactory-api-deep-dive.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

本章围绕「38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansEnvironmentPropertySourceLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 速读路径：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansEnvironmentPropertySourceLabTest`，再用 `SpringCoreBeansProfileRegistrationLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`PropertySourcesPropertyResolver#getProperty`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“5. 使用方式：最小可用手段（按“排障优先级”排序）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，Environment / PropertySource）：https://docs.spring.io/spring-framework/reference/core/beans/environment.html

这一章补齐一个“读者每天都在用，但很少系统化理解”的核心机制：

一句话先抓住结论：

> **Environment = “属性解析器 + profiles 决策器”。它通过一串有序的 PropertySources 来解析 key。**
> 观察到的“覆盖/优先级/不生效”，几乎都能归因到：**PropertySource 顺序** 或 **解析发生的时机**。

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：同一个 key 在多个 PropertySource 中同时存在
**分支**：按 `MutablePropertySources` 的顺序从前到后查找
**结果**：**先命中者生效**（顺序即优先级）
**断点建议**：`PropertySourcesPropertyResolver#getProperty`

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

可以把一个 PropertySource 理解为一个最小接口：

> 给定 key，返回 value（可能为空）。

Spring 把“多个来源”组织成一个有序链表：

- `MutablePropertySources`
  - `addFirst/addLast/addBefore/addAfter` 控制顺序

**顺序就是优先级：越靠前，优先级越高。**

这也是为什么在真实项目里读者经常会看到类似问题：

这并不神秘：只是相应的 key 在更高优先级的 PropertySource 里已经存在。

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

## 3.1 PropertySources 的“时序边界”：什么时候加，什么时候才会生效？

必须牢记一条规则：

- **影响注入/条件装配的 PropertySource，必须在 refresh 之前进入 Environment**

否则会出现两类典型现象：

- `@Profile`/条件装配已完成，新增 PropertySource 不会“倒流重算”
- `@Value` 已经解析完成，后续再加 PropertySource 不会影响已创建的 bean

这也是为什么排障时一定要先确认“是谁在什么时候把 source 加进来的”。

## 4. 占位符解析：`@Value("${...}")` 与 Environment 的连接点

很多新手会把 `@Value("${k}")` 理解为“直接读 Environment”，但严格来说它是：

1) `@Value` 被注解处理器识别（通常由 `AutowiredAnnotationBeanPostProcessor`）
2) value 字符串交给 BeanFactory 做 embedded value 解析
3) embedded value resolver 通常会委托给 Environment 做 placeholder 解析

因此在排障时至少要分清两条链：

- **读取链（Environment）：** `environment.getProperty("k")`
- **注入链（@Value）：** `@Value("${k}")` → `BeanFactory.resolveEmbeddedValue` → Environment

对照阅读：

- [34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](17-value-placeholder-resolution-strict-vs-non-strict.md)

---

## 5. 使用方式：最小可用手段（按“排障优先级”排序）
> 官方参考（Spring Framework 6.2.x，Environment / PropertySource）：https://docs.spring.io/spring-framework/reference/core/beans/environment.html


### 5.2 `@PropertySource`（适合“给一个默认文件来源”）

适用场景：

- 读者希望一个配置类自带一份 properties（常用于纯 Spring 应用或组件化库）

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

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

> `@Value("${...}")` 到底从哪里取值？
> 为什么同一个 key 在不同环境/不同启动方式下值不一样？
> `@PropertySource` 加了也不生效，或者被别的配置覆盖了，怎么断点证明？

## 0. 复现入口（可运行）

本章新增 Lab（推荐先运行通再设置断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEnvironmentPropertySourceLabTest test
```

本章强相关的已存在 Labs（用于补齐“占位符解析”和“profiles”）：

- `SpringCoreBeansValuePlaceholderResolutionLabTest`（[34](17-value-placeholder-resolution-strict-vs-non-strict.md)）
- `SpringCoreBeansProfileRegistrationLabTest`（profiles 的最小可复现）

- “在配置文件中设置了 `demo.key=foo`，但运行时却是 bar”
- “已添加 @PropertySource，但仍未生效”

在断点里应该验证的是：

### 5.1 运行前插入/覆盖 PropertySource（最强、最可控）

本章 Lab 里用的就是这个方式：

- 在 `refresh()` 之前：`environment.getPropertySources().addFirst(...)`
- 明确覆盖某个 key，并且能在断点里看到顺序变化

`@Profile` 的最小复现入口见：

- `SpringCoreBeansProfileRegistrationLabTest`

## 6. Debug / 断点入口与观察点

推荐断点（按“读配置 → 解析占位符 → 注入落地”的链路）：

1) `ConfigurableEnvironment#getProperty` / `PropertyResolver#getProperty`
   - 观察：最终读到的值来自哪个 PropertySource（顺序决定胜者）
2) `PropertySourcesPropertyResolver#getProperty`
   - 观察：遍历 propertySources 的命中过程（谁先命中谁赢）
3) `AbstractBeanFactory#resolveEmbeddedValue`
   - 观察：`${...}` 占位符解析发生点（embedded value resolver）
4) `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`
   - 观察：strict/non-strict 策略是如何被安装到 BeanFactory 的（缺失占位符是否 fail-fast）

## 常见误区与边界

### 常见误区（以及为什么在真实项目里会遇到）

1) **误区：`@PropertySource` 一定覆盖其它配置**
   - 实际是“按顺序”。更高优先级的 source 先命中就结束。
2) **误区：把 propertySource 加到 environment 里，已经创建过的 bean 会自动更新**
   - 绝大多数场景不会。注入发生在创建时；后改 Environment 不会 retroactive。
3) **误区：profiles 随时都能改**
   - profiles 影响的是“注册阶段”，必须在 refresh 前设置才有意义。

## 面试常问（Environment / PropertySource：优先级与时机）

### Q1：Environment 的属性优先级从哪来？如何用一句话说清楚？

- 标准答案（可复述）：
  - Environment 通过 `PropertySources` 的顺序解析属性，优先级就是 sources 的顺序；同 key 先命中的 source 胜出。
- 证据链（方法级）：
  - `ConfigurableEnvironment#getPropertySources`（优先级来源）
  - `PropertySourcesPropertyResolver#getProperty`（解析入口）
- 最小复现：
  - `SpringCoreBeansEnvironmentPropertySourceLabTest`

### Q2：为什么“后改 Environment”不一定影响已创建的 bean？

- 标准答案（可复述）：
  - 因为注入/属性填充发生在 bean 创建窗口（`resolveEmbeddedValue` / `populateBean`）；bean 一旦创建完成，容器不会因为 Environment 变化自动重注入（除非读者让它延迟创建/重新创建）。
- 证据链（方法级）：
  - `AbstractBeanFactory#resolveEmbeddedValue`
  - `AbstractAutowireCapableBeanFactory#populateBean`
- 最小复现：
  - `SpringCoreBeansValuePlaceholderResolutionLabTest`（配合 strict/non-strict 观察“解析发生在何时”）

### Q3：`@Profile` 与 PropertySource 的关系是什么？它影响“注册”还是“注入”？

- 标准答案（可复述）：
  - `@Profile` 更偏“定义层是否注册”；PropertySource 更偏“值解析/占位符取值来源”。两者可能共同影响最终行为，但作用点不同：先决定是否注册，再决定注入取到什么值。
- 最小复现：
  - `SpringCoreBeansProfileRegistrationLabTest`

## 自检要点
- 应能够解释清楚：PropertySource 的“顺序”为什么比“有没有某个 key”更重要吗？
- 遇到 `${demo.missing}` 没解析时，如何快速判断是“没有 property source/key”，还是“解析策略 non-strict 放行了”，还是“压根没装 placeholder 处理器”？
- 应能够说出：profiles 为什么必须在 refresh 前确定吗？它影响的是定义阶段还是创建阶段？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansProfileRegistrationLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansEnvironmentPropertySourceLabTest.java`

上一章：[37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失](20-generic-type-matching-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界](22-beanfactory-api-deep-dive.md)

<!-- BOOKIFY:END -->

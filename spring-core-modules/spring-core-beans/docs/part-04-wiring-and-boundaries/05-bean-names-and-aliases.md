# 05. Bean 名称与 alias：同一个实例，多一个名字
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。

    本章围绕22. Bean 名称与 alias：同一个实例，多一个名字展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBeanNameAliasLabTest`。需要下探源码时，可以从 `SimpleAliasRegistry#canonicalName` / `SimpleAliasRegistry#registerAlias` / `AbstractBeanFactory#transformedBeanName` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 父子 ApplicationContext：可见性与覆盖边界](04-context-hierarchy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](06-factorybean-deep-dive.md)
<!-- GLOBAL-BOOK-NAV:END -->


## 导读

- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansBeanNameAliasLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansBeanNameAliasLabTest`，再用 `SpringCoreBeansBeanNameAliasLabTest.aliasResolvesToSameSingletonInstanceAsCanonicalName()` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`SimpleAliasRegistry#canonicalName`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

很多人第一次见 alias 都会把它当成“复制一个 bean”。

- alias 只是名字映射，不会创建第二个实例

## 1. 现象：两个名字获取到的是同一个对象

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
  - `aliasResolvesToSameSingletonInstanceAsCanonicalName()`（证据：两个名字获取到 same reference）

1) 注册 `primaryName`
2) `registerAlias("primaryName", "aliasName")`

结果：

- `getBean("primaryName")` 与 `getBean("aliasName")` 获取到的是同一个实例（same reference）

### 1.1 机制系统阐述：条件 → 分支 → 结果

**条件**：是否传入 aliasName
**分支**：`canonicalName` / `transformedBeanName` 先做名称归一化
**结果**：aliasName 最终映射到同一 canonicalName
**断点建议**：`SimpleAliasRegistry#canonicalName`

## 2. alias 在容器里的定位

可以把 alias 理解为：

- 从 `aliasName` 映射到 `primaryName`
- 最终仍然是“同一个 beanDefinition/同一个 singleton instance”

- `SimpleAliasRegistry#registerAlias`：alias 注册入口（aliasName → canonicalName 的映射建立在这里）
- `SimpleAliasRegistry#canonicalName`：把 aliasName 解析成最终 canonicalName 的关键（查找/注入都会走到）
- `AbstractBeanFactory#transformedBeanName`：统一的 beanName 规范化入口（含别名、FactoryBean `&` 等前缀处理）
- `AbstractBeanFactory#doGetBean`：按 name 取 bean 的主流程（最终总是落到 canonicalName）
- `DefaultSingletonBeanRegistry#getSingleton`：singleton 缓存只存一份实例（解释“alias 不会复制对象”）

### 2.1 名字参与注入的入口集合（容易被忽略）

- `@Resource`：按 name-first
- by-name fallback：字段/参数名匹配
- `@Qualifier("beanName")`：显式指名

### 2.2 工程建议：让名字稳定、可重构

- 给核心 bean 明确 canonicalName，避免依赖默认生成名
- alias 用于兼容旧名/灰度迁移，不要当作“多实例手段”

入口：

1) `SimpleAliasRegistry#registerAlias`：观察 aliasName → primaryName 的映射写入
2) `SimpleAliasRegistry#canonicalName`：在 `getBean("aliasName")` 时观察解析过程
3) `DefaultSingletonBeanRegistry#getSingleton`：观察无论用哪个名字，最终取到的都是同一个 singleton instance

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- “aliasName 找不到/解析不到 primaryName” → **优先定义层**：alias 是否在 refresh 前注册？是否被覆盖/冲突？（本章第 3 节）
- “误认为 alias 会复制一个 bean，但两个名字获取到同一个对象” → **这是预期（实例层语义）**：alias 只是名字映射，不产生第二个实例（本章第 1 节）
- “按类型注入仍然歧义” → **实例层（候选解析）**：alias 不改变候选选择规则（见 [03](../part-01-ioc-container/02-dependency-injection-resolution.md)/[33](16-autowire-candidate-selection-primary-priority-order.md)）
- “与 FactoryBean/`&` 同时出现时容易混淆” → **应先明确 name 变换**：`transformedBeanName` 同时负责 alias 与 `&`（见 [23](06-factorybean-deep-dive.md)）

## 可复现闭环（基于 `SpringCoreBeansBeanNameAliasLabTest`）

运行完成该 Lab，至少应能够复述 3 条结论：

1) **alias 只做名字映射**
   - 断点：`canonicalName`
   - 断言：aliasName 与 primaryName 返回同一实例
2) **singleton 缓存只有一份**
   - 断点：`getSingleton`
   - 断言：两次获取命中同一缓存条目
3) **名称归一化发生在最早入口**
   - 断点：`transformedBeanName`
   - 断言：`&` 与 alias 统一处理

## 4. 面试常问（beanName 与 alias）

- 常问：alias 是“复制一个 bean”吗？它到底是什么？
  - 答题要点：alias 只是 name → canonicalName 的映射，不会创建第二个 BeanDefinition/第二个实例。
- 常见追问：容器查找时 alias 在哪一步被解析成 canonicalName？
  - 答题要点：`canonicalName` / `transformedBeanName` 会把 alias（以及 `&` 等前缀）规范化到最终名称，再进入 `doGetBean`。
- 常见追问：alias 能解决“按类型注入歧义”吗？
  - 答题要点：不能；alias 不改变类型候选集，只是名字入口；歧义仍需 `@Qualifier/@Primary` 收敛。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansBeanNameAliasLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

## 0. 复现入口（可运行）

- 入口测试（推荐先运行通再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanNameAliasLabTest test`

这一章用一个最小实验固定一个结论：

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

- `SpringCoreBeansBeanNameAliasLabTest.aliasResolvesToSameSingletonInstanceAsCanonicalName()`

该实验中：

## 源码锚点（建议从这里设置断点）

- `BeanDefinitionReaderUtils#generateBeanName`：生成 beanName 的默认规则（注册阶段）
- `DefaultListableBeanFactory#registerBeanDefinition`：注册同名定义的入口（也是覆盖/冲突的入口）
- `SimpleAliasRegistry#registerAlias`：alias 注册入口
- `SimpleAliasRegistry#canonicalName`：alias 归一化（alias → 最终 beanName）
- `DefaultListableBeanFactory#transformedBeanName`：`&name` 等前缀规则归一化（FactoryBean 相关）

## 断点闭环（用本仓库 Lab/Test 运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
  - `aliasResolvesToSameSingletonInstanceAsCanonicalName()`

建议断点：

- 应能够解释清楚：alias 解决的是什么问题？（更灵活的名称入口，而不是复制对象）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
推荐断点：`DefaultListableBeanFactory#registerAlias`、`DefaultListableBeanFactory#canonicalName`、`AbstractBeanFactory#doGetBean`

## 常见误区与边界

### 常见误区

- **误区 1：alias 冲突**
  - alias 不能随意复用，否则会导致覆盖/异常（取决于容器设置）。

- **误区 2：alias 不会改变类型**
  - alias 只是名字；它不改变注入规则、不改变 `@Primary`/`@Qualifier` 的语义。

## 自检要点
应能够解释清楚：

1) **alias 的本质是什么？**（同一个实例，多一个名字；canonicalName/aliasMap 如何参与解析）
2) **为什么 `@Resource` 更接近“按名称找 Bean”？alias 会如何影响注入结果？**
3) **看到 `&beanName` 时，读者如何判断这是 FactoryBean 还是 product？**（结合 `getBean` vs `&getBean` 的语义）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanNameAliasLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

上一章：[21. 父子 ApplicationContext：可见性与覆盖边界](04-context-hierarchy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[23. FactoryBean 深入分析：getObjectType/isSingleton 与缓存](06-factorybean-deep-dive.md)

<!-- BOOKIFY:END -->

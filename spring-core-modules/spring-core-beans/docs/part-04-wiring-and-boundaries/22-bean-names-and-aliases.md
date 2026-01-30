# 22. Bean 名称与 alias：同一个实例，多一个名字
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：22. Bean 名称与 alias：同一个实例，多一个名字
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`SimpleAliasRegistry#canonicalName` / `SimpleAliasRegistry#registerAlias` / `AbstractBeanFactory#transformedBeanName`
    - 推荐 Lab：`SpringCoreBeansBeanNameAliasLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[21. 父子 ApplicationContext：可见性与覆盖边界](21-context-hierarchy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[23. FactoryBean 深挖：getObjectType/isSingleton 与缓存](23-factorybean-deep-dive.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**22. Bean 名称与 alias：同一个实例，多一个名字**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanNameAliasLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“alias 注册结构与 lookup 路径”的证据链，并对比按类型发现。
    - B（边界反例）：反例：@Resource 注入错对象、FactoryBean `&` 前缀误判、覆盖策略冲突。
    - C（排障 SOP）：排障：按名注入/按名获取行为异常时如何定位 alias 与 canonicalName。
    - D（断点观察）： watch list：aliasMap、canonicalName、beanName 解析入口。
    - E（面试复述）：面试追问：为什么说“@Resource 更像按名称找”？alias 如何影响它。
<!-- AE-DEEPENING:END -->
## 机制主线

很多人第一次见 alias 都会把它当成“复制一个 bean”。

- alias 只是名字映射，不会创建第二个实例

## 1. 现象：两个名字拿到的是同一个对象

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
  - `aliasResolvesToSameSingletonInstanceAsCanonicalName()`（证据：两个名字拿到 same reference）

1) 注册 `primaryName`
2) `registerAlias("primaryName", "aliasName")`

结果：

- `getBean("primaryName")` 与 `getBean("aliasName")` 拿到的是同一个实例（same reference）

### 1.1 机制讲透：条件 → 分支 → 结果

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

- “aliasName 找不到/解析不到 primaryName” → **优先定义层**：alias 是否在 refresh 前注册？是否被覆盖/冲突？（本章第 3 节）
- “我以为 alias 会复制一个 bean，结果两个名字拿到同一个对象” → **这是预期（实例层语义）**：alias 只是名字映射，不产生第二个实例（本章第 1 节）
- “按类型注入仍然歧义” → **实例层（候选解析）**：alias 不改变候选选择规则（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)/[33](33-autowire-candidate-selection-primary-priority-order.md)）
- “和 FactoryBean/`&` 混在一起更乱” → **先分清 name 变换**：`transformedBeanName` 同时负责 alias 与 `&`（见 [23](23-factorybean-deep-dive.md)）

## 可复现闭环（基于 `SpringCoreBeansBeanNameAliasLabTest`）

跑完该 Lab，至少应能够复述 3 条结论：

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

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBeanNameAliasLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanNameAliasLabTest test`

这一章用一个最小实验固定一个结论：

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

- `SpringCoreBeansBeanNameAliasLabTest.aliasResolvesToSameSingletonInstanceAsCanonicalName()`

该实验中：

## 源码锚点（建议从这里下断点）

- `BeanDefinitionReaderUtils#generateBeanName`：生成 beanName 的默认规则（注册阶段）
- `DefaultListableBeanFactory#registerBeanDefinition`：注册同名定义的入口（也是覆盖/冲突的入口）
- `SimpleAliasRegistry#registerAlias`：alias 注册入口
- `SimpleAliasRegistry#canonicalName`：alias 归一化（alias → 最终 beanName）
- `DefaultListableBeanFactory#transformedBeanName`：`&name` 等前缀规则归一化（FactoryBean 相关）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

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

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

## 自检要点
应能够解释清楚：

1) **alias 的本质是什么？**（同一个实例，多一个名字；canonicalName/aliasMap 如何参与解析）
2) **为什么 `@Resource` 更像“按名称找 Bean”？alias 会如何影响注入结果？**
3) **看到 `&beanName` 时，你如何判断这是 FactoryBean 还是 product？**（结合 `getBean` vs `&getBean` 的语义）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanNameAliasLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

上一章：[21. 父子 ApplicationContext：可见性与覆盖边界](21-context-hierarchy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[23. FactoryBean 深挖：getObjectType/isSingleton 与缓存](23-factorybean-deep-dive.md)

<!-- BOOKIFY:END -->

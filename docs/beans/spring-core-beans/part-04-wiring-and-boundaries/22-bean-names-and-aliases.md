# 22. Bean 名称与 alias：同一个实例，多一个名字

## 导读

- 本章主题：**22. Bean 名称与 alias：同一个实例，多一个名字**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanNameAliasLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

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

## 2. alias 在容器里的定位

你可以把 alias 理解为：

- 从 `aliasName` 映射到 `primaryName`
- 最终仍然是“同一个 beanDefinition/同一个 singleton instance”

- `SimpleAliasRegistry#registerAlias`：alias 注册入口（aliasName → canonicalName 的映射建立在这里）
- `SimpleAliasRegistry#canonicalName`：把 aliasName 解析成最终 canonicalName 的关键（查找/注入都会走到）
- `AbstractBeanFactory#transformedBeanName`：统一的 beanName 规范化入口（含别名、FactoryBean `&` 等前缀处理）
- `AbstractBeanFactory#doGetBean`：按 name 取 bean 的主流程（最终总是落到 canonicalName）
- `DefaultSingletonBeanRegistry#getSingleton`：singleton 缓存只存一份实例（解释“alias 不会复制对象”）

入口：

1) `SimpleAliasRegistry#registerAlias`：观察 aliasName → primaryName 的映射写入
2) `SimpleAliasRegistry#canonicalName`：在 `getBean("aliasName")` 时观察解析过程
3) `DefaultSingletonBeanRegistry#getSingleton`：观察无论用哪个名字，最终取到的都是同一个 singleton instance

## 排障分流：这是定义层问题还是实例层问题？

- “aliasName 找不到/解析不到 primaryName” → **优先定义层**：alias 是否在 refresh 前注册？是否被覆盖/冲突？（本章第 3 节）
- “我以为 alias 会复制一个 bean，结果两个名字拿到同一个对象” → **这是预期（实例层语义）**：alias 只是名字映射，不产生第二个实例（本章第 1 节）
- “按类型注入仍然歧义” → **实例层（候选解析）**：alias 不改变候选选择规则（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)/[33](33-autowire-candidate-selection-primary-priority-order.md)）
- “和 FactoryBean/`&` 混在一起更乱” → **先分清 name 变换**：`transformedBeanName` 同时负责 alias 与 `&`（见 [23](23-factorybean-deep-dive.md)）

## 4. 一句话自检

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

实验里我们：

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

- 你能解释清楚：alias 解决的是什么问题？（更灵活的名称入口，而不是复制对象）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`
推荐断点：`DefaultListableBeanFactory#registerAlias`、`DefaultListableBeanFactory#canonicalName`、`AbstractBeanFactory#doGetBean`

## 常见坑与边界

## 3. 常见坑

- **坑 1：alias 冲突**
  - alias 不能随意复用，否则会导致覆盖/异常（取决于容器设置）。

- **坑 2：alias 不会改变类型**
  - alias 只是名字；它不改变注入规则、不改变 `@Primary`/`@Qualifier` 的语义。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanNameAliasLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`

上一章：[21. 父子 ApplicationContext：可见性与覆盖边界](21-context-hierarchy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[23. FactoryBean 深挖：getObjectType/isSingleton 与缓存](23-factorybean-deep-dive.md)

<!-- BOOKIFY:END -->

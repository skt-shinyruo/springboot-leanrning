# 21. 父子 ApplicationContext：可见性与覆盖边界

## 导读

- 本章主题：**21. 父子 ApplicationContext：可见性与覆盖边界**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContextHierarchyLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`

## 机制主线

当你进入真实工程或复杂测试环境，很容易遇到“多个 ApplicationContext”。

- parent/child context 的可见性规则
- child 的“覆盖”只发生在 child 内部

## 1. 现象：child 能看到 parent，parent 看不到 child

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
  - `childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`（同一个测试同时覆盖：可见性 + name-based override）

你应该观察到：

- child 可以 `getBean(ParentOnlyBean.class)`（它其实来自 parent）
- parent 无法 `getBean(ChildOnlyBean.class)`

## 2. 覆盖（override）是“按名字”的，并且只在 child 生效

同一个 beanName（例如 `shared`）：

- parent 有一个 `shared`
- child 也注册一个 `shared`

结果是：

- `child.getBean("shared")` 返回 child 的 bean
- `parent.getBean("shared")` 仍然是 parent 的 bean

学习重点：

- 覆盖发生在查找链路上：child 先查自己，再查 parent
- 覆盖不等于“删除 parent 的 bean”

- `AbstractApplicationContext#setParent`：建立 parent/child 关系（没有 parent 就没有“向上可见”）
- `AbstractBeanFactory#doGetBean`：查找链路的关键（child 找不到会委托 parent beanFactory）
- `AbstractBeanFactory#containsLocalBean`：判断“本地是否存在某个名字”的入口（解释 override 是 name-based 且只在 child 生效）
- `DefaultListableBeanFactory#containsBeanDefinition`：本地定义层查找（“我到底注册没注册”）
- `BeanFactoryUtils#beanOfTypeIncludingAncestors`：按类型跨层查找的常见工具（也容易引发“多候选”）

入口：

1) 测试里 `child.getBean(...)` 与 `parent.getBean(...)` 的调用行：对照“谁能看到谁”
2) `AbstractBeanFactory#doGetBean`：观察 child 查找失败后如何沿 parent 链路继续找
3) `AbstractBeanFactory#containsLocalBean`：观察同名 beanName 时，child 是如何优先命中自己的

## 排障分流：这是定义层问题还是实例层问题？

## 4. 一句话自检

- 常问：parent/child 的可见性规则是什么？
  - 答题要点：child 能向上查 parent；parent 完全不知道 child。
- 常见追问：override 是“按类型”还是“按名字”？它会影响 parent 吗？
  - 答题要点：override 是 name-based（child 的同名 beanName 覆盖 child 自己的查找结果）；不会反向影响 parent。
- 常见追问：为什么 parent/child 都有同类型 bean 时，按类型注入更容易出现歧义？
  - 答题要点：按类型会把 ancestors 一起纳入候选集（常见用 `BeanFactoryUtils#beanOfTypeIncludingAncestors`），需要 `@Qualifier/@Primary` 收敛。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansContextHierarchyLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContextHierarchyLabTest test`

这一章用一个最小实验展示：

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`

- `SpringCoreBeansContextHierarchyLabTest.childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`

## 源码锚点（建议从这里下断点）

- `AbstractApplicationContext#getParent`：父子上下文关系（Context 层）
- `AbstractBeanFactory#doGetBean`：本地找不到时的 parent fallback（BeanFactory 层）
- `AbstractBeanFactory#containsBean` / `containsLocalBean`：排障“到底在哪个 context 里”的常用入口
- `DefaultListableBeanFactory#setParentBeanFactory`：父工厂挂接点（Context refresh 时建立）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
  - `childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`

建议断点：

- “child 拿不到 parent 的 bean” → **优先定义层/上下文关系问题**：child 是否真的设置了 parent？parent 是否 refresh 并注册了该定义？
- “我在 child 里覆盖了 bean，但 parent 的行为没变” → **这是预期（name-based、只在 child 生效）**：override 发生在查找链路上，不会反向影响 parent（本章第 2 节）
- “按类型注入出现歧义（parent/child 都有同类型）” → **实例层（候选解析）**：需要 `@Qualifier/@Primary` 等规则收敛（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)/[33](33-autowire-candidate-selection-primary-priority-order.md)）
- “以为这是 Boot 专属现象” → **容器机制**：parent/child 是 `ApplicationContext` 层面的通用能力（本章 Lab 用小容器也能复现）

- 你能解释清楚：为什么 child 可以拿到 parent 的 bean，但 parent 拿不到 child 的 bean 吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
推荐断点：`BeanFactoryUtils#beanNamesForTypeIncludingAncestors`、`AbstractBeanFactory#doGetBean`、`AbstractApplicationContext#setParent`

## 常见坑与边界

## 3. 常见坑

- **坑 1：按类型注入时可能出现歧义**
  - 如果 parent 与 child 都有同类型的 bean，按类型注入/查找可能变成多候选。

- **坑 2：以为 child 覆盖会影响 parent**
  - 不会。parent 完全不知道 child 的存在。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContextHierarchyLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`

上一章：[20. registerResolvableDependency：能注入但它不是 Bean](20-resolvable-dependency.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[22. beanName 与 alias：命名规则与别名本质](22-bean-names-and-aliases.md)

<!-- BOOKIFY:END -->

# 24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？

## 导读

- 本章主题：**24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`

## 机制主线

当你注册两个同名 bean 时，会发生什么？

- 有的环境里：**最后一个覆盖前一个**
- 有的环境里：**直接报错**

这不是玄学，是容器的一个开关控制的：

- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)`

## 1. allowBeanDefinitionOverriding=true：最后一个 wins

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins()`（证据：同名注册两次，最后一个生效）

你会看到：

- 同名 `duplicate` 注册两次
- 最终 `getBean(Marker.class)` 得到的是第二次注册的定义

## 2. allowBeanDefinitionOverriding=false：同名注册 fail-fast

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsDisallowed_registeringSameBeanNameFailsFast()`（证据：第二次注册直接抛异常）

你会看到：

- 第二次注册直接抛 `BeanDefinitionOverrideException`
- 甚至还不需要 refresh，注册阶段就失败

## 3. 为什么这个点重要？

因为它会影响你在工程里“怎么理解装配冲突”：

- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：覆盖开关本身（决定同名注册是覆盖还是 fail-fast）
- `DefaultListableBeanFactory#registerBeanDefinition`：同名冲突发生的主入口（抛 `BeanDefinitionOverrideException` 的常见位置）
- `DefaultListableBeanFactory#isAllowBeanDefinitionOverriding`：注册逻辑里读取开关的位置（解释“为什么同一份代码在不同环境不一样”）
- `BeanDefinitionOverrideException#getBeanName`：定位冲突 beanName 的直接抓手（排查时先锁定名称而非类型）
- `DefaultListableBeanFactory#getBeanDefinition`：确认最终注册进容器的定义是哪一个（对照“last wins”）

入口：

1) `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：确认本次测试里开关的取值
2) `DefaultListableBeanFactory#registerBeanDefinition`：观察第二次注册同名 beanName 时走“覆盖”还是“抛异常”分支
3) `DefaultListableBeanFactory#getBeanDefinition`：在允许覆盖的场景下，确认最终 registry 里保存的是哪一个定义

## 排障分流：这是定义层问题还是实例层问题？

## 5. 一句话自检

- 常问：BeanDefinition overriding 解决的是什么问题？
  - 答题要点：解决“同名 BeanDefinition 冲突”的定义层问题；开关决定是 last-wins 还是 fail-fast。
- 常见追问：overriding 和“按类型注入歧义（NoUnique）”是一回事吗？
  - 答题要点：不是；overriding 是 name-based 定义冲突；注入歧义是 type-based 候选收敛问题。
- 常见追问：你如何用断点证明“冲突发生在注册阶段，而不是实例化阶段”？
  - 答题要点：在 `registerBeanDefinition` 处观察分支；fail-fast 场景甚至不需要 refresh 就会抛 `BeanDefinitionOverrideException`。

## 面试常问（overriding 与注入歧义不是一回事）

- 常问：BeanDefinition overriding 是什么？它解决什么问题？
  - 答题要点：解决“同名 BeanDefinition 冲突”的定义层问题；开关控制是否允许后注册覆盖先注册。
- 常见追问：它和“按类型注入选择（多候选）”是什么关系？
  - 答题要点：几乎无关：注入歧义是“同类型多候选怎么收敛”；overriding 是“同名定义冲突怎么处理”。不要混用概念。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanDefinitionOverridingLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`

- `SpringCoreBeansBeanDefinitionOverridingLabTest.whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins()`

- `SpringCoreBeansBeanDefinitionOverridingLabTest.whenBeanDefinitionOverridingIsDisallowed_registeringSameBeanNameFailsFast()`

- 如果允许覆盖：你可能会得到“悄悄被覆盖”的行为（难 debug）
- 如果禁止覆盖：你会在启动期得到明确错误（更安全、更可控）

## 源码锚点（建议从这里下断点）

- `DefaultListableBeanFactory#registerBeanDefinition`：注册入口（同名冲突与覆盖策略的分支发生处）
- `DefaultListableBeanFactory#isBeanDefinitionOverridable`：是否允许覆盖的判定入口
- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：全局开关来源（Spring Boot 属性会影响这里）
- `DefaultListableBeanFactory#removeBeanDefinition`：覆盖/替换时对旧定义的处理路径

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins()`
  - `whenBeanDefinitionOverridingIsDisallowed_registeringSameBeanNameFailsFast()`

建议断点：

- “启动时报 BeanDefinitionOverrideException/同名冲突” → **定义层**：这是 BeanDefinition 注册阶段就失败，与实例化/注入无关（本章第 2 节）
- “我以为覆盖能解决按类型歧义” → **不是覆盖问题，是注入候选选择问题**：同名覆盖 ≠ 同类型多候选（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)/[33](33-autowire-candidate-selection-primary-priority-order.md)）
- “行为在不同环境不一致（有时覆盖、有时报错）” → **定义层配置差异**：检查 allowBeanDefinitionOverriding 的默认值/配置来源（本章第 4 节）
- “覆盖发生了但结果难 debug” → **定义层可观测性问题**：优先用 `getBeanDefinition(beanName)` 确认最终定义，并结合 [11](../part-02-boot-autoconfig/019-11-debugging-and-observability.md) 的排查流程

- 你能解释清楚：覆盖开关控制的是“同名 BeanDefinition”冲突，而不是“按类型注入”的选择规则吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
推荐断点：`DefaultListableBeanFactory#registerBeanDefinition`、`DefaultListableBeanFactory#getBeanDefinition`、`AbstractBeanFactory#getMergedLocalBeanDefinition`

## 常见坑与边界

## 4. 常见坑

- **坑 1：把覆盖当成“解决歧义”的手段**
  - 覆盖解决的是“同名冲突”，不是“同类型多实现注入”的歧义。

- **坑 2：不同环境默认值不同**
  - Boot 环境与纯 Spring 容器的默认行为可能不同；不要靠猜。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`

上一章：[23. FactoryBean 深挖：getObjectType/isSingleton 与缓存](23-factorybean-deep-dive.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](25-programmatic-bpp-registration.md)

<!-- BOOKIFY:END -->

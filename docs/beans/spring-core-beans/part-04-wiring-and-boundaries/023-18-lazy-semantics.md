# 第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansLazyLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansLazyLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`

## 机制主线

懒加载经常被误用：很多人以为“加了 `@Lazy` 就不会启动慢了”，但实际效果取决于你把 lazy 放在哪里。

## 1. lazy-init bean：refresh 阶段不创建

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInitBean_isNotInstantiatedDuringRefresh_butCreatedOnFirstGetBean()`（证据：refresh 后构造器未调用，首次 getBean 才创建）

当 bean 定义是 lazy-init：

- refresh 阶段不会创建它
- 第一次 `getBean(...)` 才会创建

## 2. 关键反直觉点：lazy-init 也挡不住“被别人依赖”

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInitDoesNotHelpIfAConsumerEagerlyDependsOnTheBean()`（证据：consumer 非 lazy 会强制创建依赖）

如果 A（非 lazy）依赖 B（lazy-init）：

- A 的创建需要 B
- 容器会为了创建 A 去创建 B

因此你会看到：

- B 仍然在 refresh 阶段被创建

这能解释很多“我明明标了 lazy，但它还是启动时创建了”的问题。

## 3. `@Lazy` 放在注入点：注入一个 proxy，而不是直接注入目标对象

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse()`（证据：注入的是 proxy，首次调用才触发目标 bean 创建）

当你把 `@Lazy` 放在依赖注入点：

- 容器会注入一个 proxy
- proxy 在第一次真正调用时，再去容器里解析目标 bean

- 没有“外部因素”提前创建目标 bean
- 你能清晰观测到：目标 bean 的构造器是在“第一次调用”时才执行

入口：

1) lazy 目标 bean 的构造器：作为“到底什么时候创建”的最直观观察点
2) `DefaultListableBeanFactory#preInstantiateSingletons`：在 refresh 期间观察 lazy-init bean 是否被跳过
3) `AbstractBeanFactory#doGetBean`：第一次按 name/type 取 bean 时触发创建的路径
4) `ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`：观察注入点 `@Lazy` 是如何变成 proxy 的
5) `DefaultListableBeanFactory#doResolveDependency`：在“consumer 依赖 lazy bean”的测试里观察为什么会提前创建

## 排障分流：这是定义层问题还是实例层问题？

- “我标了 lazy-init，但 bean 还是在启动时创建” → **优先实例层（依赖链）**：是否有非 lazy 的 consumer 直接依赖它？（本章第 2 节 + `doResolveDependency`）
- “我在注入点加了 `@Lazy`，但仍然提前创建” → **优先实例层（proxy 触发点）**：是不是调用了会触发真实解析的方法（如 `toString/equals`）或其他路径提前拿到了目标 bean？
- “我以为 `@Lazy` 会影响 beanDefinition 的 lazy-init” → **优先定义层澄清**：注入点 `@Lazy` 与 beanDefinition `lazy-init` 是两种语义（本章第 3 节）
- “看到的是 proxy 类型而不是目标类” → **实例层（代理语义）**：这是注入点 `@Lazy` 的本质（对照 [31](31-proxying-phase-bpp-wraps-bean.md)）

## 5. 一句话自检

- 常问：`lazy-init` 与注入点 `@Lazy` 有什么本质差别？
  - 答题要点：`lazy-init` 是定义层“延迟创建策略”；注入点 `@Lazy` 是“注入延迟解析 proxy”，把解析推迟到首次使用。
- 常见追问：为什么 lazy-init 仍可能在 refresh 时被创建？
  - 答题要点：被 eager 依赖/被提前触发（例如非 lazy 单例依赖它）时仍会创建；排障要找“是谁触发了依赖解析”。
- 常见追问：如何用断点证明“提前创建”是由依赖解析触发，而不是 lazy 失效？
  - 答题要点：在 `doResolveDependency` / `doGetBean` 加条件断点（beanName），观察创建链路的触发源。

## 面试常问（`@Lazy` 的两种语义）

- 常问：`lazy-init` 与注入点 `@Lazy` 有什么本质差别？
  - 答题要点：`lazy-init` 是定义层的“延迟创建策略”；注入点 `@Lazy` 更像“注入一个延迟解析的代理/提供者”，把真正解析推迟到首次使用。
- 常见追问：为什么标了 lazy-init 仍可能在 refresh 时被创建？
  - 答题要点：被 eager 依赖/被提前触发（例如非 lazy 单例依赖它）时仍会创建；排障要找“谁触发了依赖解析”。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansLazyLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLazyLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`

- `SpringCoreBeansLazyLabTest.lazyInitBean_isNotInstantiatedDuringRefresh_butCreatedOnFirstGetBean()`

- `SpringCoreBeansLazyLabTest.lazyInitDoesNotHelpIfAConsumerEagerlyDependsOnTheBean()`

- `SpringCoreBeansLazyLabTest.lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse()`

本实验还配合把目标 bean 标成 lazy-init，从而确保：

## 源码锚点（建议从这里下断点）

- `DefaultListableBeanFactory#preInstantiateSingletons`：refresh 阶段批量创建非 lazy 单例（lazy-init bean 会被跳过）
- `AbstractBeanFactory#doGetBean`：首次按 name/type 取 bean 时触发创建（lazy-init 的典型入口）
- `DefaultListableBeanFactory#doResolveDependency`：解释“lazy bean 仍可能因为被依赖而提前创建”的触发点
- `ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`：注入点 `@Lazy` 变成 proxy 的关键入口
- `AbstractAutowireCapableBeanFactory#initializeBean`：对照代理/增强发生在生命周期的哪一段

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInitBean_isNotInstantiatedDuringRefresh_butCreatedOnFirstGetBean()`
  - `lazyInitDoesNotHelpIfAConsumerEagerlyDependsOnTheBean()`
  - `lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse()`

建议断点：

- 你能解释清楚：为什么“lazy-init 的 bean”仍可能在 refresh 期间被创建吗？
- 你能解释清楚：注入点 `@Lazy` 的本质是“注入 proxy”吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
推荐断点：`AbstractBeanFactory#doGetBean`、`DefaultListableBeanFactory#preInstantiateSingletons`、`ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`

## 常见坑与边界

## 4. 常见坑

- **坑 1：以为 `@Lazy` 能让所有依赖都不创建**
  - 如果目标 bean 不是 lazy-init，它仍可能在 refresh 阶段被 pre-instantiate。

- **坑 2：在 proxy 上调用 `toString()` / `equals()` 触发真实创建**
  - 学习阶段尽量不要依赖日志；用断言固定“构造器是否被调用”。

## 小结与下一章

- `DefaultListableBeanFactory#preInstantiateSingletons`：refresh 时批量创建非 lazy 单例（lazy-init bean 会被跳过）
- `AbstractBeanFactory#doGetBean`：第一次 `getBean(...)` 触发真正创建（lazy-init 的典型入口）
- `DefaultListableBeanFactory#doResolveDependency`：依赖解析入口（解释“lazy bean 仍可能因为被依赖而提前创建”）
- `ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`：注入点 `@Lazy` 的关键（决定是否注入一个懒代理）
- `AbstractAutowireCapableBeanFactory#createBean`：创建入口（对照“什么时候真的 new 出目标对象”）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansLazyLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`

上一章：[17. 生命周期回调顺序：Aware/@PostConstruct/afterPropertiesSet/initMethod](../part-03-container-internals/17-lifecycle-callback-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[19. dependsOn：强制初始化顺序与依赖关系记录](19-depends-on.md)

<!-- BOOKIFY:END -->

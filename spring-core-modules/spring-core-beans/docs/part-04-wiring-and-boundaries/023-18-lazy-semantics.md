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
上一章：[第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - `lazy-init` 是**定义层策略**：控制 bean 是否在 refresh 的 `preInstantiateSingletons` 阶段被创建；但它挡不住“被 eager 依赖触发创建”。
    - 注入点 `@Lazy` 是**实例层策略**：它不是“让 bean 变懒”，而是“在注入点注入一个延迟解析代理（lazy proxy）”，把真正的解析/创建推迟到首次使用。
    - `@Lazy` 代理的形态与注入点类型相关：
      - 注入点是接口 → 多数情况下是 JDK proxy（`Proxy.isProxyClass(...) == true`）
      - 注入点是具体类 → 多数情况下是 CGLIB（`ClassUtils.isCglibProxyClass(...) == true`）
    - 想证明“到底什么时候创建”，不要靠猜：跑本章 Lab，用构造器计数与断言把现象固定下来。


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

---

## 4. 代理类型边界：接口注入点 vs 类注入点（必须会排障）

这一段是很多人学 `@Lazy` 学不明白的关键原因：你看到的是 proxy，但 proxy 的“类型形态”并不总一样。

在本仓库的 Lab 中你可以直接对照：

1) **接口注入点（JDK proxy）**
   - 对应实验：`SpringCoreBeansLazyLabTest#lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse`
   - 现象：注入对象满足接口类型，但**不是具体实现类**（按实现类查找/强转会踩坑）
2) **类注入点（CGLIB proxy）**
   - 对应实验：`SpringCoreBeansLazyLabTest#lazyInjectionPoint_onConcreteClass_usesClassBasedProxy_andDefersCreationUntilFirstUse`
   - 现象：注入对象是目标类的子类代理，通常仍可按具体类类型工作，但调试时类名会带 `$$` 等 proxy 痕迹

排障分流建议（先问自己这 2 个问题）：

- 你是“按接口”注入/查找，还是“按实现类”注入/查找？
- 你拿到的是 JDK proxy 还是 CGLIB proxy？（决定“类型边界”与“能不能强转”）

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

## 源码调用链（方法级）：lazy-init vs 注入点 `@Lazy`

### 1) lazy-init（定义层）：refresh 期间“跳过”，但挡不住被依赖

- refresh 批量创建单例入口：`DefaultListableBeanFactory#preInstantiateSingletons`
  - lazy-init bean 会被跳过（不主动创建）
- 首次取用触发创建：`AbstractBeanFactory#doGetBean(beanName)`
  - 进入 `doCreateBean` 完成实例化/注入/初始化
- 关键边界：只要有 eager consumer 依赖它，依赖解析就会提前触发 `getBean`
  - 证据链入口：`DefaultListableBeanFactory#doResolveDependency`

### 2) 注入点 `@Lazy`（实例层）：注入 proxy，把解析推迟到首次使用

- 注入解析入口：`DefaultListableBeanFactory#doResolveDependency`
- 生成 lazy proxy 的关键分支：`ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`
- 首次调用 proxy 时才触发真实解析：回到 `AbstractBeanFactory#doGetBean(beanName)`

> 结论：lazy-init 控制“容器是否主动创建”；注入点 `@Lazy` 控制“注入的是不是一个延迟解析的代理”。

## 常见坑与边界


### 常见坑

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

上一章：[17. 生命周期回调顺序：Aware/@PostConstruct/afterPropertiesSet/initMethod](../part-03-container-internals/17-lifecycle-callback-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[19. dependsOn：强制初始化顺序（即使没有显式依赖）](19-depends-on.md)

<!-- BOOKIFY:END -->

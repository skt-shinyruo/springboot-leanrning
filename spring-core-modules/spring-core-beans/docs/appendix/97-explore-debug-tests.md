# 97. Explore/Debug 用例（可选启用，不影响默认回归）

## 导读

- 本章主题：**97. Explore/Debug 用例（可选启用，不影响默认回归）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`

## 机制主线

## 0. 你为什么需要它？

这一章只做一件事：把 Explore 用例的**开启方式**与**观察点**写成显式规则。

---

- 开启 Explore：`-Dspringcorebeans.explore=true`

示例：只跑 Explore 用例里的某个测试类：

如果你想“在不改任何代码的前提下”跑一轮完整深挖（会包含核心回归测试）：

---

## 2. Explore 用例清单（入口与观察点）

### 2.1 单例缓存：`DefaultSingletonBeanRegistry` 的核心缓存表

### 2.2 JavaBeans 内省缓存：`CachedIntrospectionResults` 的缓存行为

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

`spring-core-beans` 的 Labs 有两类：

1. **Core Labs（默认参与回归）**：用于保证“主线机制可复现/可断言”，CI 默认会跑。  
2. **Explore/Debug 用例（可选启用）**：用于“更深一层的断点观察/内部状态验证”，默认不影响 CI 稳定性与耗时。

## 1. 运行方式（显式开关）

Explore 用例默认不运行。你需要显式打开系统属性：

```bash
mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeansSingletonCacheExploreTest test
```

```bash
mvn -pl :spring-core-beans -Dspringcorebeans.explore=true test
```

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java`
- 你要观察的点：
  - singleton 与 prototype 在缓存层面的差异（prototype 不会进 `singletonObjects`）
- 断点建议：
  - `DefaultSingletonBeanRegistry#getSingleton`
  - `DefaultSingletonBeanRegistry#addSingleton`

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`
- 你要观察的点：
  - 为什么 BeanWrapper/属性注入不会每次都重新 `Introspector.getBeanInfo`
  - 缓存如何与 ClassLoader 绑定（`acceptClassLoader` / `clearClassLoader`）
- 断点建议：
  - `CachedIntrospectionResults`（查看缓存命中）
  - `BeanWrapperImpl` / `AbstractNestablePropertyAccessor`（调用链入口）

## 常见坑与边界

1) **误区：为什么 tests 没跑/IDE 里看不到用例？**
   - Explore 用例默认不启用：它们用 `@EnabledIfSystemProperty(named = "springcorebeans.explore", matches = "true")` 保护。
   - 需要显式开启：`-Dspringcorebeans.explore=true`（见本章命令）。
2) **边界：这些用例可能随 Spring 版本变化而失效**
   - Explore 测试会用反射读取内部字段（例如 `DefaultSingletonBeanRegistry` 的缓存字段），属于“观察型断点材料”，不是稳定 API。
   - 因此它们默认不参与回归，避免版本升级时误伤主线测试。
3) **误区：把 Explore 输出当成“生产诊断手段”**
   - Explore 的目标是帮助你在学习阶段“看见容器内部数据结构变化”，生产排障仍以主线章节（尤其是 [11](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)）的方法论为主。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`

上一章：[96. spring-beans Public API Gap 清单（按包/机制域分批深化）](96-spring-beans-public-api-gap.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99. 自测题（Self Check）](026-99-self-check.md)

<!-- BOOKIFY:END -->

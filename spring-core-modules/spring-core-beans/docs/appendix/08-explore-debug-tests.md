# 08. Explore/Debug 用例（可选启用，不影响默认回归）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Explore/Debug 用例：如何开启、看什么、怎么把观察结果“用回主线”
    - 使用方式：建议先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`DefaultSingletonBeanRegistry#getSingleton` / `DefaultSingletonBeanRegistry#addSingleton` / `DefaultSingletonBeanRegistry#addSingletonFactory`
    - 推荐 Lab：`SpringCoreBeansSingletonCacheExploreTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[07. spring-beans Public API Gap 清单（按包/机制域分批深化）](07-spring-beans-public-api-gap.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[11. 自测题：是否能够真的理解了？](11-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

本章围绕「Explore/Debug 用例：如何开启、看什么、怎么把观察结果“用回主线”」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `SpringCoreBeansSingletonCacheExploreTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! summary "本章要点"

    - Explore 用例的定位：**学习期的“显微镜”**——把 Spring 内部数据结构（缓存/表/映射）变化变成应能够在调试器里观察到的东西。
    - 它们默认不运行：用 `@EnabledIfSystemProperty(named = "springcorebeans.explore", matches = "true")` 保护，避免 CI/回归因为“观察型断言”而不稳定。
    - 它们不是“生产诊断方案”：测试会用反射访问内部字段、依赖实现细节；Spring 升级后可能需要同步调整。
    - 正确使用方式：先用 Core Labs 固化结论（可断言、稳定），再用 Explore 用例补齐“希望观察缓存如何变化”的证据链。

!!! example "本章配套实验（Explore 用例，先运行再读）"

    - Explore Test（默认不参与回归）：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`

## 0. 为什么需要 Explore 用例？

在这个仓库里，“教程闭环”的基座是两类东西：

1) **Core Labs（默认参与回归）**：用断言把机制结论固化下来，保证“读者学到的结论能稳定复现”。
2) **Explore/Debug 用例（可选启用）**：用断点 + 内部状态读取，把“读者脑子里想象的容器内部结构”变成可观察事实。

这两类用例的关系是：

- Core Labs：回答“结论对不对（能不能稳定复现）？”
- Explore 用例：回答“为什么（内部结构到底怎么变）？”

---

## 1. 如何开启 Explore 用例？

Explore 用例默认被系统属性 gate 掉，需要显式开启：

- 系统属性：`-Dspringcorebeans.explore=true`

### 1.1 仅运行某一个 Explore 测试类（推荐）

```bash
mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeansSingletonCacheExploreTest test
```

```bash
mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeansCachedIntrospectionExploreTest test
```

### 1.2 仅运行 Explore 测试集合（两个同时运行）

```bash
mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test
```

### 1.3 运行全量测试（包含 Core Labs + Explore）

```bash
mvn -pl :spring-core-beans -Dspringcorebeans.explore=true test
```

### 1.4 IDE 运行方式

在 IDE 的 Run/Debug 配置里加 JVM 参数：

- `-Dspringcorebeans.explore=true`

然后直接运行 Explore 测试类即可。

---

## 2. Explore 用例清单：入口 & 观察点

### 2.1 单例缓存：`DefaultSingletonBeanRegistry` 三层缓存

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

入口测试：

- `SpringCoreBeansSingletonCacheExploreTest`

需要观察的点（建议按这个顺序看）：

1) **final / early / factory 的语义差异**：
   - `singletonObjects`：final（完全初始化后的单例）
   - `earlySingletonObjects`：early（循环依赖窗口期的 early reference）
   - `singletonFactories`：factory（按需生成 early reference 的工厂）
2) **prototype 不进入 singleton 缓存**：不要用“缓存行为”去推断 prototype 生命周期（两套语义）
3) **循环依赖窗口期缓存变化**：结合 [09](../part-01-ioc-container/08-circular-dependencies.md)、[16](../part-03-container-internals/05-early-reference-and-circular.md) 对照观察

推荐断点（按收益排序）：

1) `DefaultSingletonBeanRegistry#getSingleton`
2) `DefaultSingletonBeanRegistry#addSingleton`
3) `DefaultSingletonBeanRegistry#addSingletonFactory`
4) `DefaultSingletonBeanRegistry#beforeSingletonCreation` / `afterSingletonCreation`
5) `AbstractAutowireCapableBeanFactory#doCreateBean`（把缓存变化放回 bean 创建主线）

Watch List（最小够用版）：

- `beanName`
- `allowEarlyReference`
- `isSingletonCurrentlyInCreation(beanName)`
- `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的 `containsKey(beanName)` 与 `size()`

应能够用 2–3 句话复述：

- “setter 循环依赖为什么可能成功？”（early exposure 窗口期 + getSingleton 的 early 分支）
- “为什么要三层而不是一层？”（factory 延迟决定 early 形态；early 与 final 的角色不同）

### 2.2 JavaBeans 内省缓存：`CachedIntrospectionResults` 的缓存行为

入口测试：

- `SpringCoreBeansCachedIntrospectionExploreTest`

需要观察的点：

1) 为什么属性注入/BeanWrapper 不会每次都重新 `Introspector.getBeanInfo(...)`
2) `CachedIntrospectionResults` 如何做缓存（按 Class/ClassLoader 维度绑定）
3) “缓存命中/失效”在源码里出现在哪里（应能够否找到稳定入口）

推荐断点（按收益排序）：

1) `CachedIntrospectionResults#forClass`
2) `CachedIntrospectionResults#acceptClassLoader` / `clearClassLoader`
3) `java.beans.Introspector#getBeanInfo`（想确认“到底有没有真正做 JDK 内省”时再下）
4) `BeanWrapperImpl` / `AbstractNestablePropertyAccessor`（把内省缓存放回“属性访问/注入”的调用链）

Watch List（建议盯“缓存容器的 key/size”，不要依赖具体字段名）：

- 缓存 Map 的 `size()` / `keySet()`
- 目标 `beanClass` 与对应的 `ClassLoader`

---

## 3. 如何把 Explore 结果“用回主线”（让它真的变成教程）

推荐一个最省时间的学习闭环：

1) 先运行一遍 Core Labs（不加 explore 开关）：`mvn -pl :spring-core-beans test`
2) 再运行 Explore（加开关）：`mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`
3) 对照章节回主线：
   - 缓存/循环依赖：看 [09](../part-01-ioc-container/08-circular-dependencies.md) 与 [16](../part-03-container-internals/05-early-reference-and-circular.md)
   - 更系统的“从异常到断点入口”：看 [11. 调试与可观察性](../part-02-boot-autoconfig/01-debugging-and-observability.md)

可以发现：Explore 用例的价值不是“多了一堆测试”，而是“读者终于能在调试器里观察到那个一直被口述的内部结构”。

---

## 面试使用方式（将“观察结果”组织为可复述答案）

Explore 用例本身不是面试题，但它能显著提升读者答题的“可信度”：

1) 面试官问循环依赖/early reference：可以补充一句：“在调试器中观察过三层缓存命中分支”，并能说出方法名：`getSingleton/addSingletonFactory/getEarlyBeanReference`。
2) 面试官问属性填充/类型转换：可以补充一句：“在调试器中观察过 JDK 内省缓存的命中/失效”，并能说出入口：`CachedIntrospectionResults#forClass`。
3) 面试官追问“如何证明”：可以直接回指本章的 ExploreTest + 断点 + watch list。

更标准的答题模板：`appendix/04-interview-playbook.md`

## 常见误区
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


1) **用例未运行 / IDE 中看不到执行**
   - 大概率是读者没加 `-Dspringcorebeans.explore=true`；这些用例默认是被 gate 掉的。
2) **把 Explore 用例当成生产诊断手段**
   - Explore 用例依赖内部实现细节（反射读取字段等），生产排障请回到主线方法论与可观测性工具。
3) **升级 Spring 后 Explore 用例失败**
   - 这是预期风险：它们本来就是“观察型材料”；修复方式通常是更新反射字段/断言口径，而不是把开关常态化打开。

## 自检要点
- 应能够解释清楚：为什么 Explore 用例默认不参与回归吗？它适合解决什么问题、不适合解决什么问题？
- 应能够把“观察点”落到可复现材料上吗：哪一个 ExploreTest、哪几个断点、需要看哪几个结构/变量？
- 应能够把 Explore 观察“用回主线”吗：把观察点映射回对应章节与 Core Labs 的稳定结论？

## 小结与下一章

- Explore 用例的目标是“观察到”，不是“保证稳定结论”；稳定结论以 Core Labs 为准。
- 完成验证本章后，应能够在调试器里把“三级缓存”与“内省缓存”的变化看出来，并能把观察点准确复述给别人。
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansSingletonCacheExploreTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：把该页从“信息堆”变成“可用入口”：每个条目尽量落到“去哪里验证/怎么验证”，避免只列名词。
    - 下一跳：若是从现象进入，优先回到 [知识地图](03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Explore Test：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`

上一章：[96. spring-beans Public API Gap 清单（按包/机制域分批深化）](07-spring-beans-public-api-gap.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99. 自测题（Self Check）](11-self-check.md)

<!-- BOOKIFY:END -->

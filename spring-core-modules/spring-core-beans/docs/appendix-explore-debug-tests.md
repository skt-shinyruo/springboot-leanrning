# Explore/Debug 用例（可选启用，不影响默认回归）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    观察对象：Explore/Debug 用例：如何开启、看什么、怎么把观察结果“用回主线”。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansSingletonCacheExploreTest`。需要下探源码时，可以从 `DefaultSingletonBeanRegistry#getSingleton` / `DefaultSingletonBeanRegistry#addSingleton` / `DefaultSingletonBeanRegistry#addSingletonFactory` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：把 Explore 用例当作观察窗口

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试收敛答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 问题：Explore/Debug 用例（可选启用，不影响默认回归）

本页介绍一类“可选启用”的用例：它们不追求断言覆盖面，而追求把容器内部状态（缓存/内省结果/窗口期变量）变成可观察事实。

当已经能跑通 Core Labs（默认回归的 `*LabTest`），但在“为什么会这样”上仍缺少预期时，可以打开 Explore 用例做一次观察：看内部结构怎么变、在哪个阶段变、变完如何影响最终行为。

先运行 `SpringCoreBeansSingletonCacheExploreTest`（三层缓存最直观），再按本页的断点与观察清单扩展到其它 Explore 用例。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（Explore 用例，先运行再读）"

    - Explore Test（默认不参与回归）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java`
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`

## 为什么需要 Explore 用例？

在这个仓库里，“教程闭环”的基座是两类东西：

1. **Core Labs（默认参与回归）**：用断言把机制结论固化下来，保证“读者学到的结论能稳定复现”。
2. **Explore/Debug 用例（可选启用）**：用断点 + 内部状态读取，把“读者脑子里想象的容器内部结构”变成可观察事实。

这两类用例的关系是：

- Core Labs：回答“结论对不对（能不能稳定复现）？”
- Explore 用例：回答“为什么（内部结构到底怎么变）？”

---

## 如何开启 Explore 用例？

Explore 用例默认被系统属性 gate 掉，需要显式开启：

- 系统属性：`-Dspringcorebeans.explore=true`

### 1.1 仅运行某一个 Explore 测试类

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

## Explore 用例清单：入口 & 观察点

### 2.1 单例缓存：`DefaultSingletonBeanRegistry` 三层缓存

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

入口测试：

- `SpringCoreBeansSingletonCacheExploreTest`

需要观察的点（按这个顺序看）：

1. **final / early / factory 的语义差异**：
   - `singletonObjects`：final（完全初始化后的单例）
   - `earlySingletonObjects`：early（循环依赖窗口期的 early reference）
   - `singletonFactories`：factory（按需生成 early reference 的工厂）
2. **prototype 不进入 singleton 缓存**：不要用“缓存行为”去推断 prototype 生命周期（两套语义）
3. **循环依赖窗口期缓存变化**：结合 [09](ioc-circular-dependencies.md)、[16](internals-early-reference-and-circular.md) 对照观察

断点入口（按收益排序）：

1. `DefaultSingletonBeanRegistry#getSingleton`
2. `DefaultSingletonBeanRegistry#addSingleton`
3. `DefaultSingletonBeanRegistry#addSingletonFactory`
4. `DefaultSingletonBeanRegistry#beforeSingletonCreation` / `afterSingletonCreation`
5. `AbstractAutowireCapableBeanFactory#doCreateBean`（把缓存变化放回 bean 创建主线）

观察清单（最小够用版）：

- `beanName`
- `allowEarlyReference`
- `isSingletonCurrentlyInCreation(beanName)`
- `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的 `containsKey(beanName)` 与 `size()`

需要用 2–3 句话复述：

- “setter 循环依赖为什么可能成功？”（early exposure 窗口期 + getSingleton 的 early 分支）
- “为什么要三层而不是一层？”（factory 延迟决定 early 形态；early 与 final 的角色不同）

### 2.2 JavaBeans 内省缓存：`CachedIntrospectionResults` 的缓存行为

入口测试：

- `SpringCoreBeansCachedIntrospectionExploreTest`

需要观察的点：

1. 为什么属性注入/BeanWrapper 不会每次都重新 `Introspector.getBeanInfo(...)`
2. `CachedIntrospectionResults` 如何做缓存（按 Class/ClassLoader 维度绑定）
3. “缓存命中/失效”在源码里出现在哪里（是否能找到稳定入口）

断点入口（按收益排序）：

1. `CachedIntrospectionResults#forClass`
2. `CachedIntrospectionResults#acceptClassLoader` / `clearClassLoader`
3. `java.beans.Introspector#getBeanInfo`（想确认“到底有没有真正做 JDK 内省”时再下）
4. `BeanWrapperImpl` / `AbstractNestablePropertyAccessor`（把内省缓存放回“属性访问/注入”的调用链）

观察清单（盯“缓存容器的 key/size”，不要依赖具体字段名）：

- 缓存 Map 的 `size()` / `keySet()`
- 目标 `beanClass` 与对应的 `ClassLoader`

---

## 如何把 Explore 结果“用回主线”（让它真的变成教程）

更快收敛的学习闭环：

1. 先运行一遍 Core Labs（不加 explore 开关）：`mvn -pl :spring-core-beans test`
2. 再运行 Explore（加开关）：`mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`
3. 对照章节回主线：
   - 缓存/循环依赖：看 [09](ioc-circular-dependencies.md) 与 [16](internals-early-reference-and-circular.md)
   - 更系统的“从异常到断点入口”：看 [调试与可观察性](boot-debugging-and-observability.md)

可以发现：Explore 用例的价值不是“多了一堆测试”，而是“读者终于能在调试器里观察到那个一直被口述的内部结构”。

---

## 面试使用方式（将“观察结果”组织为可复述答案）

Explore 用例本身不是面试题，但它能显著提升读者答题的“可信度”：

1. 面试官问循环依赖/early reference：可以补充一句：“在调试器中观察过三层缓存命中分支”，并能说出方法名：`getSingleton/addSingletonFactory/getEarlyBeanReference`。
2. 面试官问属性填充/类型转换：可以补充一句：“在调试器中观察过 JDK 内省缓存的命中/失效”，并能说出入口：`CachedIntrospectionResults#forClass`。
3. 面试官追问“如何证明”：可以直接回指本章的 ExploreTest + 断点 + 观察清单。

更标准的答题模板：`appendix-interview-playbook.md`

## 误判点：Explore/Debug 用例（可选启用，不影响默认回归）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


1. **用例未运行 / IDE 中看不到执行**
   - 大概率是读者没加 `-Dspringcorebeans.explore=true`；这些用例默认是被 gate 掉的。
2. **把 Explore 用例当成生产诊断手段**
   - Explore 用例依赖内部实现细节（反射读取字段等），生产排障请回到主线方法论与可观测性工具。
3. **升级 Spring 后 Explore 用例失败**
   - 这是预期风险：它们本来就是“观察型材料”；修复方式通常是更新反射字段/断言口径，而不是把开关常态化打开。

## 验收口径：Explore/Debug 用例（可选启用，不影响默认回归）
- 需要解释清楚：为什么 Explore 用例默认不参与回归吗？它适合解决什么问题、不适合解决什么问题？
- 需要把“观察点”落到可复现材料上吗：哪一个 ExploreTest、哪几个断点、需要看哪几个结构/变量？
- 需要把 Explore 观察“用回主线”吗：把观察点映射回对应章节与 Core Labs 的稳定结论？

## 小结：Explore/Debug 用例（可选启用，不影响默认回归）

- Explore 用例的目标是“观察到”，不是“保证稳定结论”；稳定结论以 Core Labs 为准。
- 完成验证本章后，需要在调试器里把“三级缓存”与“内省缓存”的变化看出来，并能把观察点准确复述给别人。


<!-- BOOKIFY:START -->

### 对应实验/测试

- Explore Test：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansSingletonCacheExploreTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansCachedIntrospectionExploreTest.java`

<!-- BOOKIFY:END -->

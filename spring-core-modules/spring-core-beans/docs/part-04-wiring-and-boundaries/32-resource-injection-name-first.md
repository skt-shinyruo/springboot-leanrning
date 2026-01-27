# 32. `@Resource` 注入：为什么它更像“按名称找 Bean”？

## 导读

- 本章主题：**`@Resource` 注入：为什么它更像“按名称找 Bean”？**
- 阅读方式建议：先用本章 Lab 跑出两个对照结论（没装处理器 → 注解无效；装了处理器 → name-first 稳定注入），再回到源码把“是谁在什么时候把字段赋值”的证据链走通。

!!! summary "本章要点"

    - `@Resource` 不是 “另一个 @Autowired”。它更像：**先按 name 找（字段名/显式 name），必要时才按 type 兜底**。
    - `@Resource` 能工作，前提是容器里安装了 `CommonAnnotationBeanPostProcessor`（JSR-250/Jakarta 注解处理器）。没装处理器，注解就只是“写在代码上的字”。
    - name-first 的代价也很明确：**重构字段名/beanName/alias** 时更容易产生隐性回归；当你想要“按类型 + 候选规则”时，应切回 `@Autowired + @Qualifier/@Primary`。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansResourceInjectionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

## 机制主线：`@Resource` 的三个关键事实

当你在项目里看到 `@Resource`，先把它压缩成三句话（排障时非常省命）：

1) 它由 `CommonAnnotationBeanPostProcessor` 处理（不是 `AutowiredAnnotationBeanPostProcessor`）。
2) 默认 name-first：不写 name 时，用 **字段名** 当 beanName。
3) 它发生在属性填充阶段（`populateBean`），本质是“把字段赋值”。

---

## 1. 先跑实验：没有处理器时，`@Resource` 会“完全失效”

对应实验：

- `SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored`

你会观察到两个稳定现象：

- 容器能正常 `refresh()`，目标 bean 也能创建出来
- 但 `@Resource` 标注的字段是 `null`

这不是 “@Resource 不稳定”，而是一个更底层的事实：

> 注解不是语言层魔法。注入发生，是因为容器里有处理器（BPP）把“注解元数据”翻译成“实际赋值动作”。

对照阅读（注解为什么能工作）：
- [12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)

---

## 2. 再跑实验：装上处理器后，`@Resource` 默认按字段名注入（name-first）

对应实验：

- `SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst`

实验的关键差异只是一行：

- `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)`

之后你会看到两个更关键的结论：

1) `@Resource` 注入生效（字段有值）
2) 即使容器里有多个同类型候选，默认 `@Resource` 仍然“稳定且可预测” —— 因为它先按字段名锁定 beanName

本章 Lab 用最小代码把默认规则写死：

- `@Resource`（不写 name）→ 使用字段名作为 beanName
- `@Resource(name = "otherDependency")` → 显式指定 beanName

---

## 3. 源码最短路径：是谁在什么时候把字段赋值的？

> 目标：不要把 “@Resource 注入”想象成某个神秘行为，而是把它放回“属性填充阶段”里看见真实赋值点。

一条足够实用的最短链路是：

1) `AbstractAutowireCapableBeanFactory#populateBean`（属性填充阶段入口）
2) `CommonAnnotationBeanPostProcessor#postProcessProperties`（扫描并执行 `@Resource` 注入）
3) `CommonAnnotationBeanPostProcessor#autowireResource`（name-first 的分流入口：按 name 找不到时才考虑 fallback）

只要你在这条链上走通一次，后面遇到 “@Resource 为什么没注入/注入错了” 都能快速定位。

---

## 4. Debug 断点闭环（推荐照做一次）

### 4.1 推荐断点（按收益排序）

1) `AnnotationConfigUtils#registerAnnotationConfigProcessors`：确认处理器是否被注册（关键是 `CommonAnnotationBeanPostProcessor`）
2) `CommonAnnotationBeanPostProcessor#postProcessProperties`：`@Resource` 注入介入点（发生在属性填充阶段）
3) `CommonAnnotationBeanPostProcessor#autowireResource`：观察 name-first 的具体分支（字段名/显式 name）
4) `AbstractAutowireCapableBeanFactory#populateBean`：把“注入”放回 bean 创建主线

### 4.2 固定观察点（watch list）

- `field.getName()`：默认 resourceName（不写 name 时）
- `resourceName`（如果你在 `autowireResource` 里）：最终用于查找的 beanName
- `beanFactory.containsBean(resourceName)`：name-first 能否命中
- （当走 fallback 时）`requiredType`：兜底的类型是什么，是否会多候选

---

## 5. 排障分流：三类问题，三条路

| 现象 | 最可能根因 | 处理策略 |
| --- | --- | --- |
| 字段一直是 `null` | 容器没装 `CommonAnnotationBeanPostProcessor` | 注册 processors；或使用 Boot/AnnotationConfigApplicationContext |
| 注入到了“不是我想要的那个” | 字段名/显式 name 与 beanName/alias 不一致 | 明确写 `@Resource(name=...)`；或改用 `@Autowired + @Qualifier` |
| 报多候选/歧义异常 | name 未命中，fallback 走 type 时遇到多个候选 | 优先指定 name；或回到 [33](33-autowire-candidate-selection-primary-priority-order.md) 的候选规则 |

---

## 6. `@Resource` vs `@Autowired`：该用哪个？

这不是“谁更好”，而是“你想让依赖关系由什么来约束”：

- 你想 **按名称精确绑定**（确定就是那个 beanName）：
  - `@Resource(name = "...")` 很直观
  - 代价：beanName/字段名重构更敏感
- 你想 **按类型装配 + 候选规则收敛**（Primary/Qualifier/泛型收敛等）：
  - 优先 `@Autowired + @Qualifier/@Primary`
- 参见：[03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33](33-autowire-candidate-selection-primary-priority-order.md)

---

## 源码调用链（方法级）：`@Resource` 的 name-first 是怎么发生的

`@Resource` 的关键不是“注解长什么样”，而是它进入了哪条创建期调用链：

1) bean 创建主线：`AbstractAutowireCapableBeanFactory#doCreateBean`
2) 注入阶段入口：`AbstractAutowireCapableBeanFactory#populateBean`
3) 注解处理器介入：`CommonAnnotationBeanPostProcessor#postProcessProperties`
4) name-first 查找：内部 `autowireResource(...)` / `beanFactory.containsBean(resourceName)`
5) fallback 到 type：当 name 未命中时，才会走“按类型解析”（因此可能出现多候选）

你在断点里盯住 `resourceName` 与 `containsBean(resourceName)`，就能立刻判断自己处于 name-first 还是 fallback。

## 面试常问（`@Resource` vs `@Autowired`）

### Q1：为什么说 `@Resource` 更像 name-first，而 `@Autowired` 更像 type-first？

- 标准答案（可复述）：
  - `@Resource` 默认用字段名作为 beanName 先查；`@Autowired` 以类型解析为主，必要时才触发 by-name fallback（且条件更苛刻），并通过 `@Qualifier/@Primary` 收敛候选。
- 证据链（方法级）：
  - `@Resource`：`CommonAnnotationBeanPostProcessor#postProcessProperties`
  - `@Autowired`：`AutowiredAnnotationBeanPostProcessor#postProcessProperties` → `DefaultListableBeanFactory#doResolveDependency`
- 最小复现：
  - `SpringCoreBeansResourceInjectionLabTest`

### Q2：为什么在某些容器里 `@Resource` “完全不生效”？

- 标准答案（可复述）：
  - 因为没有注册 annotation processors（没有对应的 BPP），注解无人处理；plain BeanFactory 并不会自动装这些基础设施。
- 证据链（方法级）：
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（是否注册了 CABPP）
  - 观察点：`beanFactory.getBeanPostProcessors()` 是否包含 `CommonAnnotationBeanPostProcessor`

## 一句话自检

你应该能用 2 句答题：

1) `@Resource` 为什么更像 name-first？（默认用字段名当 beanName；由 CommonAnnotationBeanPostProcessor 处理）
2) 为什么在某些容器里它完全不生效？（没注册 annotation processors，注解无人处理）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansResourceInjectionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

上一章：[31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy](31-proxying-phase-bpp-wraps-bean.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[33. 候选选择与优先级：@Primary/@Priority/@Order 的边界](33-autowire-candidate-selection-primary-priority-order.md)

<!-- BOOKIFY:END -->

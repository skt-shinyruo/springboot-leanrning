# `@Resource` 注入：为什么其定位更接近“按名称找 Bean”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单 收敛原因。

    观察对象：`@Resource` 注入：为什么其定位更接近“按名称找 Bean”？。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansResourceInjectionLabTest`。需要下探源码时，可以从 `CommonAnnotationBeanPostProcessor#autowireResource` / `SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored` / `SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 起点：`@Resource` 注入：为什么其定位更接近“按名称找 Bean”？

- 阅读路径：先运行本章 Lab 得到两个对照结论（没装处理器 → 注解无效；装了处理器 → name-first 稳定注入），再回到源码把“是谁在什么时候把字段赋值”的证据链走通。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（Resources，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/resources.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansResourceInjectionLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

## 机制主线：`@Resource` 的三个关键事实

> 官方参考（Spring Framework 6.2.x，注解驱动与依赖注入语义）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html

当在项目里看到 `@Resource`，先把它压缩成三句话（排障时省命）：

1. 它由 `CommonAnnotationBeanPostProcessor` 处理（不是 `AutowiredAnnotationBeanPostProcessor`）。
2. 默认 name-first：不写 name 时，用 **字段名** 当 beanName。
3. 它发生在属性填充阶段（`populateBean`），本质是“把字段赋值”。

---

## 机制系统阐述：条件 → 分支 → 结果

**条件**：注入点标注 `@Resource`，且容器已注册 `CommonAnnotationBeanPostProcessor`
**分支**：`autowireResource` 先按 **name** 查找，找不到再 fallback 按 **type**
**结果**：命名稳定时注入可预测；命名失配时容易退化为“按类型歧义”
**断点入口**：`CommonAnnotationBeanPostProcessor#autowireResource`

## DependencyDescriptor 深入分析：`@Resource` 的注入点语义从哪来？

虽然 `@Resource` 不是走 `AutowiredAnnotationBeanPostProcessor`，但它仍然要解析“注入点语义”：

- 字段名/显式 name → `resourceName`
- 字段类型/泛型 → `requiredType` / `ResolvableType`
- 该注入点是否为可选 → 决定 fallback 失败时是否抛错

所以当看到“注入错了/注入不到”，第一步是确认 **name 与 type 的一致性**。

## 依赖解析分支树（`@Resource` 专用简化版）

1. **name-first**：`containsBean(resourceName)` 命中 → 直接注入
2. **fallback type**：name 未命中 → 按类型解析（可能触发多候选歧义）
3. **失败处理**：不可选依赖 → 抛异常；可选依赖 → 注入 `null`

## 先运行实验：没有处理器时，`@Resource` 会“完全失效”

对应实验：

- `SpringCoreBeansResourceInjectionLabTest#withoutAnnotationConfigProcessors_resourceIsIgnored`

可以观察到两个稳定现象：

- 容器能正常 `refresh()`，目标 bean 也能创建出来
- 但 `@Resource` 标注的字段是 `null`

这不是 “@Resource 不稳定”，而是一个更底层的事实：

> 注解不是语言层隐式行为。注入发生，是因为容器里有处理器（BPP）把“注解元数据”翻译成“实际赋值动作”。

对照阅读（注解为什么能工作）：
- [容器启动与基础设施处理器：为什么注解能工作？](internals-container-bootstrap-and-infrastructure.md)

---

## 再运行实验：装上处理器后，`@Resource` 默认按字段名注入（name-first）

对应实验：

- `SpringCoreBeansResourceInjectionLabTest#registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst`

实验的关键差异只是一行：

- `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)`

之后可以观察到两个更关键的结论：

1. `@Resource` 注入生效（字段有值）
2. 即使容器里有多个同类型候选，默认 `@Resource` 仍然“稳定且可预测” —— 因为它先按字段名锁定 beanName

本章 Lab 用最小代码把默认规则写死：

- `@Resource`（不写 name）→ 使用字段名作为 beanName
- `@Resource(name = "otherDependency")` → 显式指定 beanName

---

## 源码最短路径：是谁在什么时候把字段赋值的？

> 落点：不要把 “@Resource 注入”想象成某个神秘行为，而是把它放回“属性填充阶段”里观察到真实赋值点。

一条足够实用的最短链路是：

1. `AbstractAutowireCapableBeanFactory#populateBean`（属性填充阶段入口）
2. `CommonAnnotationBeanPostProcessor#postProcessProperties`（扫描并执行 `@Resource` 注入）
3. `CommonAnnotationBeanPostProcessor#autowireResource`（name-first 的分流入口：按 name 找不到时才考虑 fallback）

只要在这条链上走通一次，后面遇到 “@Resource 为什么没注入/注入错了” 都能快速定位。

---

## Debug 断点闭环（照做一次）

### 4.1 断点入口（按收益排序）

1. `AnnotationConfigUtils#registerAnnotationConfigProcessors`：确认处理器是否被注册（关键是 `CommonAnnotationBeanPostProcessor`）
2. `CommonAnnotationBeanPostProcessor#postProcessProperties`：`@Resource` 注入介入点（发生在属性填充阶段）
3. `CommonAnnotationBeanPostProcessor#autowireResource`：观察 name-first 的具体分支（字段名/显式 name）
4. `AbstractAutowireCapableBeanFactory#populateBean`：把“注入”放回 bean 创建主线

### 4.2 固定观察点（观察清单）

- `field.getName()`：默认 resourceName（不写 name 时）
- `resourceName`（若在 `autowireResource` 里）：最终用于查找的 beanName
- `beanFactory.containsBean(resourceName)`：name-first 能否命中
- （当走 fallback 时）`requiredType`：回退的类型是什么，是否会多候选

---

## 排障分流：三类问题，三条路
> 官方参考（Spring Framework 6.2.x，注解驱动与依赖注入语义）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html


| 现象 | 最可能根因 | 处理策略 |
| --- | --- | --- |
| 字段一直是 `null` | 容器没装 `CommonAnnotationBeanPostProcessor` | 注册 processors；或使用 Boot/AnnotationConfigApplicationContext |
| 注入到了“不是预期的那个” | 字段名/显式 name 与 beanName/alias 不一致 | 明确写 `@Resource(name=...)`；或改用 `@Autowired + @Qualifier` |
| 报多候选/歧义异常 | name 未命中，fallback 走 type 时遇到多个候选 | 优先指定 name；或回到 [33](wiring-autowire-candidate-selection-primary-priority-order.md) 的候选规则 |

---

## `@Resource` vs `@Autowired`：该用哪个？

这不是“谁更好”，而是“若希望让依赖关系由什么来约束”：

- 若希望 **按名称精确绑定**（确定就是那个 beanName）：
  - `@Resource(name = "...")` 很直观
  - 代价：beanName/字段名重构更敏感
- 若希望 **按类型装配 + 候选规则收敛**（Primary/Qualifier/泛型收敛等）：
  - 优先 `@Autowired + @Qualifier/@Primary`
- 参见：[03](ioc-dependency-injection-resolution.md)、[33](wiring-autowire-candidate-selection-primary-priority-order.md)

---

## 源码调用链（方法级）：`@Resource` 的 name-first 是怎么发生的

`@Resource` 的关键不是“注解长什么样”，而是它进入了哪条创建期调用链：

1. bean 创建主线：`AbstractAutowireCapableBeanFactory#doCreateBean`
2. 注入阶段入口：`AbstractAutowireCapableBeanFactory#populateBean`
3. 注解处理器介入：`CommonAnnotationBeanPostProcessor#postProcessProperties`
4. name-first 查找：内部 `autowireResource(...)` / `beanFactory.containsBean(resourceName)`
5. fallback 到 type：当 name 未命中时，才会走“按类型解析”（因此可能出现多候选）

在断点里盯住 `resourceName` 与 `containsBean(resourceName)`，就能立刻判断自己处于 name-first 还是 fallback。

## 面试常问（`@Resource` vs `@Autowired`）

### Q1：为什么说 `@Resource` 更接近 name-first，而 `@Autowired` 更接近 type-first？

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

 调试时重点盯：`beanFactory.getBeanPostProcessors()` 是否包含 `CommonAnnotationBeanPostProcessor`。


## 验证标准：`@Resource` 注入：为什么其定位更接近“按名称找 Bean”？
需要用 2 句答题：

1. `@Resource` 为什么更接近 name-first？（默认用字段名当 beanName；由 CommonAnnotationBeanPostProcessor 处理）
2. 为什么在某些容器里它完全不生效？（没注册 annotation processors，注解无人处理）


## 收束：`@Resource` 注入：为什么其定位更接近“按名称找 Bean”？

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansResourceInjectionLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

<!-- BOOKIFY:END -->

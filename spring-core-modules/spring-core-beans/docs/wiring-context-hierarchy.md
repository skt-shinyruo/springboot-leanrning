# 父子 ApplicationContext：可见性与覆盖边界
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。

    本章围绕21. 父子 ApplicationContext：可见性与覆盖边界展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansContextHierarchyLabTest`。需要下探源码时，可以从 `AbstractBeanFactory#doGetBean` / `AbstractApplicationContext#setParent` / `AbstractBeanFactory#containsLocalBean` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

本章围绕「21. 父子 ApplicationContext：可见性与覆盖边界」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansContextHierarchyLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansContextHierarchyLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
    - 可先运行的方法：
      - `childContext_canSeeParentBeans_butParentCannotSeeChildBeans`
      - `containsLocalBean_differsFromContainsBean_inChildContext`
      - `typeLookupIncludingAncestors_canBecomeAmbiguous_whenParentAndChildBothProvideSameType`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansContextHierarchyLabTest`，再用 `SpringCoreBeansContextHierarchyLabTest.childContext_canSeeParentBeans_butParentCannotSeeChildBeans()` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractBeanFactory#doGetBean`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

当读者进入真实工程或复杂测试环境，很容易遇到“多个 ApplicationContext”。

- parent/child context 的可见性规则
- child 的“覆盖”只发生在 child 内部

### 机制系统阐述：条件 → 分支 → 结果

**条件**：当前 BeanFactory 是否存在 parent
**分支**：`AbstractBeanFactory#doGetBean` 本地找不到就 fallback parent
**结果**：
- child 能看到 parent
- parent 永远看不到 child
**断点建议**：`AbstractBeanFactory#doGetBean`

## 现象：child 能看到 parent，parent 看不到 child

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
  - `childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`（同一个测试同时覆盖：可见性 + name-based override）

应当观察到：

- child 可以 `getBean(ParentOnlyBean.class)`（该对象来自 parent）
- parent 无法 `getBean(ChildOnlyBean.class)`

## 覆盖（override）是“按名字”的，并且只在 child 生效

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
- `DefaultListableBeanFactory#containsBeanDefinition`：本地定义层查找（用于确认“是否已注册”）
- `BeanFactoryUtils#beanOfTypeIncludingAncestors`：按类型跨层查找的常见工具（也容易引发“多候选”）

入口：

1) 测试里 `child.getBean(...)` 与 `parent.getBean(...)` 的调用行：对照“谁能看到谁”
2) `AbstractBeanFactory#doGetBean`：观察 child 查找失败后如何沿 parent 链路继续找
3) `AbstractBeanFactory#containsLocalBean`：观察同名 beanName 时，child 是如何优先命中自己的

## 可复现闭环（基于 `SpringCoreBeansContextHierarchyLabTest`）

运行完成该 Lab，至少应能够复述 3 条结论：

1) **child 能看到 parent，parent 看不到 child**
   - 断点：`doGetBean`
   - 断言：fallback 只向上
2) **override 只在 child 生效**
   - 断点：`containsLocalBean`
   - 断言：child 命中本地，parent 不受影响
3) **按类型包含祖先会扩大候选集**
   - 断点：`beanOfTypeIncludingAncestors`
   - 断言：容易出现 NoUnique

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


这类问题很适合先按“查找链路”分层：

- **定义层（注册/上下文关系）**：child 拿不到 parent 的 bean
  - 优先确认：parent 是否 `refresh` 完成、child 是否真的设置了 parent（`AbstractApplicationContext#setParent`）
  - 然后确认：目标 beanName 是否确实存在于 parent 的 registry（`containsBeanDefinition`）
- **查找链路（本章重点）**：同名覆盖是否生效、child/parent 的可见性是否符合预期
  - 关键结论：child 先查自己，再 fallback parent；parent 不会向下查 child
  - 典型断点：`AbstractBeanFactory#doGetBean`（看 fallback 到 parent 的时机）
- **实例层（候选解析）**：parent 与 child 都有同类型 bean，按类型注入出现歧义
  - 这不是 context hierarchy 本身的 bug，而是候选集扩大后的收敛问题（用 `@Qualifier/@Primary`）
## 面试常问（父子 ApplicationContext）

- 常问：parent/child 的可见性规则是什么？
  - 答题要点：child 能向上查 parent；parent 完全不知道 child。
- 常见追问：override 是“按类型”还是“按名字”？它会影响 parent 吗？
  - 答题要点：override 是 name-based（child 的同名 beanName 覆盖 child 自己的查找结果）；不会反向影响 parent。
- 常见追问：为什么 parent/child 都有同类型 bean 时，按类型注入更容易出现歧义？
  - 答题要点：按类型会把 ancestors 一起纳入候选集（常见用 `BeanFactoryUtils#beanOfTypeIncludingAncestors`），需要 `@Qualifier/@Primary` 收敛。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansContextHierarchyLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

## 复现入口（可运行）

- 入口测试（推荐先运行通再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContextHierarchyLabTest test`

这一章用一个最小实验展示：

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`

- `SpringCoreBeansContextHierarchyLabTest.childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`

## 源码锚点（建议从这里设置断点）

- `AbstractApplicationContext#getParent`：父子上下文关系（Context 层）
- `AbstractBeanFactory#doGetBean`：本地找不到时的 parent fallback（BeanFactory 层）
- `AbstractBeanFactory#containsBean` / `containsLocalBean`：排障“到底在哪个 context 里”的常用入口
- `DefaultListableBeanFactory#setParentBeanFactory`：父工厂挂接点（Context refresh 时建立）

## 断点闭环（用本仓库 Lab/Test 运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
  - `childContext_canSeeParentBeans_butParentCannotSeeChildBeans()`

建议断点：

- “child 拿不到 parent 的 bean” → **优先定义层/上下文关系问题**：child 是否真的设置了 parent？parent 是否 refresh 并注册了该定义？
- “在 child 中覆盖了 bean，但 parent 的行为未变化” → **这是预期（name-based、只在 child 生效）**：override 发生在查找链路上，不会反向影响 parent（本章第 2 节）
- “按类型注入出现歧义（parent/child 都有同类型）” → **实例层（候选解析）**：需要 `@Qualifier/@Primary` 等规则收敛（见 [03](ioc-dependency-injection-resolution.md)/[33](wiring-autowire-candidate-selection-primary-priority-order.md)）
- “以为这是 Boot 专属现象” → **容器机制**：parent/child 是 `ApplicationContext` 层面的通用能力（本章 Lab 用小容器也能复现）

- 应能够解释清楚：为什么 child 可以获取到 parent 的 bean，但 parent 拿不到 child 的 bean 吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
推荐断点：`BeanFactoryUtils#beanNamesForTypeIncludingAncestors`、`AbstractBeanFactory#doGetBean`、`AbstractApplicationContext#setParent`

## 常见误区与边界

### 常见误区

- **误区 1：按类型注入时可能出现歧义**
  - 如果 parent 与 child 都有同类型的 bean，按类型注入/查找可能变成多候选。

- **误区 2：以为 child 覆盖会影响 parent**
  - 不会。parent 完全不知道 child 的存在。

## 自检要点
应能够解释清楚：

1) **child 为什么能看到 parent 的 bean，但 parent 看不到 child 的 bean？**（搜索顺序与可见性规则）
2) **同名 bean 在父子容器中是什么语义？**（覆盖只在 child 查找链路生效，不会反向影响 parent）
3) **parent/child 同类型 bean 造成注入歧义时，应该把问题归到哪一层？**（依赖解析/候选收敛：`@Qualifier/@Primary` 等）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContextHierarchyLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`

<!-- BOOKIFY:END -->

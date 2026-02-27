# 03. registerResolvableDependency：能注入，但它不是 Bean
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：registerResolvableDependency：能注入，但它不是 Bean
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`DefaultListableBeanFactory#resolvableDependencies` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultListableBeanFactory#resolveDependency`
    - 推荐 Lab：`SpringCoreBeansResolvableDependencyLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. dependsOn：强制初始化顺序（即使没有显式依赖）](02-depends-on.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 父子 ApplicationContext：可见性与覆盖边界](04-context-hierarchy.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

本章围绕「registerResolvableDependency：能注入，但它不是 Bean」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansResolvableDependencyLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（注解驱动与注入，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html

  应能够 `@Autowired` 进来一个东西，但它**不是 BeanDefinition**、`getBean(类型)` 也找不到它。

!!! summary "本章要点"

    - `registerResolvableDependency` 注册的是一张“特殊依赖表”：`DefaultListableBeanFactory#resolvableDependencies`（**type → value**），不是 BeanDefinition。
    - 命中位置在依赖解析主入口 `DefaultListableBeanFactory#doResolveDependency`：在“按类型找候选 bean”之前，会先尝试从 `resolvableDependencies` 里按可赋值关系匹配。
    - 所以它的典型外观是：**能注入（resolveDependency 命中）**，但**不是 bean（getBean/getBeansOfType 查不到）**。
    - 这条机制经常与 `*Aware` 混淆：两者都能把“容器对象/上下文对象”交给业务 bean，但生效点和生命周期完全不同。

!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansResolvableDependencyLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java`

## 机制主线：它是“可解析依赖”，不是“可获取 Bean”

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

一句话结论：

> `registerResolvableDependency` 是在告诉容器：**遇到注入点需要这个 type，就给它这个 value**。
> 但这个 value **不进入 BeanDefinition 注册表**，也**不进单例池/生命周期/后置处理器链**。

### 机制系统阐述：条件 → 分支 → 结果

**条件**：依赖解析命中 `resolvableDependencies`
**分支**：`doResolveDependency` 在“找候选 bean”之前先检查特殊依赖表
**结果**：
- 可注入但不可 `getBean`
- 不走 BPP/生命周期链
**断点建议**：`DefaultListableBeanFactory#doResolveDependency`

因此它适用的典型对象是“容器基础设施对象”，例如：

- `BeanFactory` / `ApplicationContext` / `ResourceLoader`
- `ApplicationEventPublisher`
- `Environment`

## 1. 方法级入口：注入是怎么进入 `doResolveDependency` 的？

无需记住所有注入触发点，但必须知道**最后汇聚点**：

- 汇聚点：`DefaultListableBeanFactory#resolveDependency` → `doResolveDependency`

常见上游触发：

1) 属性注入（字段/Setter）
   `AutowiredAnnotationBeanPostProcessor#postProcessProperties` → `beanFactory.resolveDependency(...)`
2) 构造器注入（`@Autowired` 构造器/单构造器）
   `ConstructorResolver#autowireConstructor` → `resolveDependency(...)`
3) `@Resource`（按名优先）
   `CommonAnnotationBeanPostProcessor` 最终也会走 `resolveDependency` 或 `getBean(name)`（见第 32 章）

### 1.1 DependencyDescriptor 深入分析（决定“能不能命中”）

- `required`：是否允许为 null（`@Autowired(required=false)` / Optional）
- `annotations`：`@Qualifier/@Lazy/@Value` 影响解析路径
- `resolvableType`：泛型信息会影响匹配
- `dependencyName`：字段名/参数名（影响 by-name fallback）

**两个对照注入点**：
1) 字段注入（有名字）：`@Autowired private Environment env;`
2) 构造器注入（名字来自参数）：`Consumer(Environment environment)`

**本章的关键点**：在 `doResolveDependency` 内部，Spring 会在“查找候选 bean”之前先检查 `resolvableDependencies`。

## 2. 机制：`resolvableDependencies` 到底是什么？

### 2.1 这张“特殊依赖表”在源码里是什么？

它就是 `DefaultListableBeanFactory` 里的一个 map：

- key：`Class<?>`（通常是接口/基础设施类型）
- value：`Object`（可以是实例，也可以是 `ObjectFactory`）

注册入口：

- `DefaultListableBeanFactory#registerResolvableDependency(Class<?> dependencyType, Object autowiredValue)`

> 注意：这是“容器级”的注册行为；注册到哪个 `BeanFactory`，就只对哪个上下文的注入生效（见第 21 章父子容器可见性）。

### 2.2 命中逻辑在依赖解析链路的哪个位置？

核心发生在：

- `DefaultListableBeanFactory#doResolveDependency`

在断点里通常会看到类似流程（表达的是顺序，不是源码逐行复刻）：

1) 处理快捷路径（`Optional` / `ObjectProvider` / `@Lazy` 等）
2) 处理 `@Value`（字符串/表达式）
3) **尝试匹配 `resolvableDependencies`**
4) 再去 `findAutowireCandidates`（按类型收集候选 bean）并做候选收敛（见第 33 章）

### 2.2.1 依赖解析分支树（简化版）

- 快捷路径（Optional/Provider/@Lazy/@Value）
- `resolvableDependencies` 命中 → 直接返回
- 候选收集/收敛 → 正常注入
- 仍不满足 → `NoSuchBeanDefinitionException`

### 2.2.2 关键变量速查

| 变量 | 含义 | 决策地位 |
| --- | --- | --- |
| `descriptor` | 注入点描述 | 决定解析路径 |
| `resolvableDependencies` | 特殊依赖表 | 命中即短路 |
| `matchingBeans` | 候选集合 | 是否进入收敛 |

### 2.3 为什么 `getBean(type)` 查不到？（查找路径完全不同）

因为 `getBean` 查的是：

- BeanDefinition 注册表
- 单例池（singletonObjects 等缓存）
- FactoryBean 产物（`&` 前缀语义见第 23/29 章）

而 `resolvableDependencies`：

- 不会生成 BeanDefinition
- 不进入 singletonObjects
- 不经过完整生命周期（BPP/BFPP/Aware/init/destroy…）

所以现象就变得非常“稳定”：**能注入 ≠ 能 getBean**。

## 3. 容器默认会注册哪些 ResolvableDependency？（以及怎么确认）

默认注册发生在 `ApplicationContext` 启动过程中，最关键的方法级入口是：

- `AbstractApplicationContext#prepareBeanFactory`

这里会为读者注册一批基础设施对象（不同 Spring 版本略有差异，但核心思想一致），常见包括：

- `BeanFactory` / `ApplicationContext`
- `ResourceLoader`
- `ApplicationEventPublisher`
- `Environment`

需要确认“该注入为什么能命中？”最直接的方法不是猜，而是：

- 在 `prepareBeanFactory` 或 `registerResolvableDependency` 设置断点，看注册了哪些 type
- 在 `doResolveDependency` 的 resolvableDependencies 命中分支设置断点，看命中了哪一条

## 4. 高级用法：用 `ObjectFactory` 做“按需提供”

`resolvableDependencies` 的 value 允许是 `ObjectFactory`，Spring 会在注入时把它“解包”为真实对象（常见落点：`AutowireUtils#resolveAutowiringValue`）。

适用场景：

- 需要注入一个“**按线程/按请求动态变化**”的上下文对象（例如 requestId），但它不是 Spring Bean（或者读者不想把它做成 Bean + Scope）
- 需要把“获取动作”延迟到注入发生时（而不是注册时就固定一个实例）

反例与警告：

- 不要用它塞业务单例对象来“绕过” BeanDefinition（这会让生命周期、代理、AOP 变得不可预测）
- 若想要真正的生命周期/代理/范围语义，应该用 **Scope/ScopedProxy** 或 **Provider/ObjectProvider**（见第 28/30 章）

## 5. 它和 `*Aware` 是什么关系？

两者都是“把容器信息交给 bean”，但路径不同：

- `registerResolvableDependency`：注入阶段命中
  典型入口：`doResolveDependency`（依赖解析）
- `*Aware`：实例创建之后的回调阶段命中
  典型入口：`AbstractAutowireCapableBeanFactory#invokeAwareMethods`

可以这样记：

> **ResolvableDependency 是“像注入一样给读者一个对象”；Aware 是“给读者一个回调机会”。**

## 可复现闭环（基于 `SpringCoreBeansResolvableDependencyLabTest`）

运行完成该 Lab，至少应能够复述 3 条结论：

1) **能注入但不可 getBean**
   - 断点：`doResolveDependency`
   - 断言：命中 `resolvableDependencies` 分支
2) **注册发生在 prepareBeanFactory**
   - 断点：`prepareBeanFactory`
   - 断言：默认基础设施对象被注册
3) **ObjectFactory 会延迟解析**
   - 断点：`AutowireUtils#resolveAutowiringValue`
   - 断言：注入时才解包 value

## 6. 排障决策表（能注入/不能 getBean/命中不了 → 证据链）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象/异常 | 最可能原因 | 证据链（方法级） | 推荐修复 |
| --- | --- | --- | --- |
| `@Autowired` 成功，但 `getBean(类型)` 失败 | 这是 resolvable dependency，不是 bean | `doResolveDependency` 命中 `resolvableDependencies`；`getBean` 查不到对应 BeanDefinition | 接受它的定位；如果需要 bean 语义，就改成注册 BeanDefinition（`registerBeanDefinition`/`registerSingleton`） |
| 读者自己注册了 `registerResolvableDependency`，但注入点还是报 `NoSuchBeanDefinitionException`/`UnsatisfiedDependencyException` | 注册到了**另一个** `BeanFactory`（父子容器/测试 context 变化） | `prepareBeanFactory`/自定义注册处断点看目标工厂；`doResolveDependency` 里 map 是否包含该 key | 确认注册发生在“注入发生的那个 context”的 `BeanFactory` 上 |
| 容易误以为 `@Qualifier` 能约束它，但没有效果 | resolvableDependencies 按 type 命中，不走候选选择 | 命中发生在 `doResolveDependency` 的 resolvableDependencies 分支，未进入 `findAutowireCandidates` | 若需要 Qualifier 语义，则不宜使用 resolvableDependency；应改为注册多个 bean，并通过 Qualifier 进行选择 |
| 读者把一个对象塞进 resolvableDependencies，期望它被 AOP/后置处理器增强，但没有 | 它不是 bean，不会走 BPP 链 | 不经过 `createBean` / `initializeBean` / `applyBeanPostProcessors...` | 需要增强就让它成为 bean，或把增强逻辑放在读者自己的 factory/provider 里 |

## 7. 断点闭环（建议照做一次）

### 7.1 推荐断点（按收益排序）

1) `AbstractApplicationContext#prepareBeanFactory`：看默认注册了哪些 resolvable dependencies
2) `DefaultListableBeanFactory#registerResolvableDependency`：看相应的 type/value 如何进入 map
3) `DefaultListableBeanFactory#doResolveDependency`：看注入点命中的是 resolvableDependencies 还是候选 bean
4) `AutowireUtils#resolveAutowiringValue`：value 是 `ObjectFactory` 时，看它何时解包

### 7.2 固定观察点（watch list）

- `DependencyDescriptor#getDependencyType()` / `descriptor.getResolvableType()`
- `this.resolvableDependencies`
- 命中条目：`entry.getKey()` / `entry.getValue()`
- 是否进入 `findAutowireCandidates(...)`

## 8. 面试常问（标准答案 + 方法级证据链）

### Q1：`registerResolvableDependency` 是什么？为什么“能注入但不是 Bean”？

- 标准答案：它注册的是“可解析依赖表”（type → value），注入时在 `doResolveDependency` 里先命中这张表；但它不是 BeanDefinition，因此 `getBean` 查不到。
- 方法级证据链：`registerResolvableDependency` → `resolvableDependencies`；注入：`doResolveDependency` 命中；查找：`getBean` 走 BeanDefinition/单例池，不看这张表。

### Q2：它和 `*Aware` 的区别是什么？

- 标准答案：ResolvableDependency 在“注入解析阶段”命中；`*Aware` 在“bean 实例创建后回调阶段”命中；两者都能获取到容器对象，但生命周期与可测试性不同。
- 方法级证据链：`doResolveDependency` vs `invokeAwareMethods`。

### Q3：能不能用它来实现 `@Qualifier` 多实现选择？

- 标准答案：不适合。ResolvableDependency 按 type 直接命中，跳过候选收敛逻辑；要 Qualifier 就应该走候选选择（注册多个 bean）。
- 方法级证据链：命中在 `doResolveDependency` 的 resolvableDependencies 分支，没有进入 `determineAutowireCandidate`（见第 33 章）。

## 自检要点
ResolvableDependency = **注入时可解析的 type→value 映射**；命中在 `doResolveDependency`；它不是 bean，因此没有 BeanDefinition/生命周期/BPP/AOP 增强。

## 小结与下一章

- 本章完成后：需要把三件事分清楚：**能注入**、**能 getBean**、**会不会走生命周期/代理**。
- 下一章将进入父子容器：同一个 type 在不同 `ApplicationContext` 下为何“可见性不同、覆盖规则不同”。
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansResolvableDependencyLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`DefaultListableBeanFactory#doResolveDependency`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“6. 排障决策表（能注入/不能 getBean/命中不了 → 证据链）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansResolvableDependencyLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java`

上一章：[19. dependsOn：强制初始化顺序（即使没有显式依赖）](02-depends-on.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[21. 父子 ApplicationContext：可见性与覆盖边界](04-context-hierarchy.md)

<!-- BOOKIFY:END -->

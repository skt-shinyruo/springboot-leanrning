# 39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界

## 导读

- 本章主题：**39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`

## 机制主线

这一章解决一个常见“源码阅读/排障”卡点：

> 我明明用的是 Spring（注解都写了），为什么在某些启动方式/某些测试里注解不生效？  
> 为什么 `DefaultListableBeanFactory` 看起来“很强”，但很多能力又像是缺失的？  
> BeanFactory 到底是一套什么 API？哪些能力是它自带的，哪些是靠 post-processors “装上去”的？

一句话先讲清楚：

> **BeanFactory 是 Spring 容器的“最小内核 API”。你看到的大量“注解能力/自动行为”，本质上是 post-processors 在容器启动阶段把能力装配出来的。**

---

---

## 1. 是什么：BeanFactory 在 Spring 体系里的位置

你可以把 Spring 的容器能力拆成两层：

1) **容器内核（BeanFactory API）**：负责定义→实例化→依赖解析→生命周期基本骨架  
2) **容器增强（post-processors + 上层设施）**：负责注解处理、AOP 代理、条件装配、占位符解析、事件/资源等

其中 BeanFactory 是第一层的核心接口，它解决的是：

- 通过 beanName / 类型拿到对象（`getBean`）
- 管理单例缓存与创建（内部 `doGetBean`/createBean 链路）
- 解析依赖（`doResolveDependency`）

而 `ApplicationContext` 是一个更“开箱即用”的上层抽象：

- 在 refresh 生命周期中 **自动发现并执行** BFPP/BPP
- 集成资源、事件、国际化等上层设施

---

## 2. BeanFactory 接口族谱（你在源码里看到的都从这里来）

你不需要背每个方法，但需要能把“你遇到的 API”归类到下面这些角色：

一个非常关键的事实：

---

- `@Autowired` field 没注入
- `@PostConstruct` 没执行
- `@Resource` 没生效
- `@Value("${...}")` 解析行为不符合预期

这不是因为 BeanFactory “不支持注解”，而是因为：

> 注解能力靠的是 BPP/BFPP，而 plain BeanFactory 不会像 ApplicationContext 那样自动发现并注册它们。

所以你要么：

1) 用 `ApplicationContext`（默认推荐）  
2) 或者你明确知道自己在干什么：手动 bootstrap 必要的 post-processors

- plain BeanFactory：注解不生效
- 手动 addBeanPostProcessor：注解生效

---

## 4. 怎么用：你在真实项目里会如何接触 BeanFactory？

### 4.1 作为框架/中间件作者（更常见）

你可能会：

- 写一个 `BeanFactoryPostProcessor` / `BeanPostProcessor`
- 在其中拿到 `ConfigurableListableBeanFactory`
- 读取 BeanDefinition / 注册额外定义 / 修改属性 / 注册 value resolver 等

### 4.2 作为业务开发者（更少见，但排障常见）

- `DefaultListableBeanFactory#doResolveDependency`（注入失败/候选收敛）
- `AbstractBeanFactory#doGetBean`（循环依赖、FactoryBean、提前暴露）

以及在排障时用到：

- `getBean("&x")`（区分 FactoryBean 本体与 product）
- `getBeansOfType`（枚举候选）

---

### 5.1 BeanFactory 主线入口

观察点：

- `beanName`
- `mbd`（merged definition）
- `singletonObjects`（是否命中单例缓存）

### 5.2 “注解生效”的关键入口（BPP 视角）

最关键的观察点（建议 watch）：

- `beanFactory.getBeanPostProcessors()`（或等价字段）：plain vs 手动 bootstrap 的差异
- 当前 bean 的注入元数据（是否解析到了 @Autowired 字段/方法）

### 5.3 “为什么 ApplicationContext 开箱即用”

如果你要把根因讲得更完整，可以对照阅读：

- [12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- [06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](../part-01-ioc-container/017-06-post-processors.md)

---

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

本章新增 Lab（推荐先跑通再下断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanFactoryApiLabTest test
```

强相关对照 Lab（已存在）：

- `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`（BeanFactory vs ApplicationContext 对比）
- `SpringCoreBeansBootstrapInternalsLabTest`（为什么注解能工作：基础设施处理器 bootstrap）

- `BeanFactory`：最小 getBean/类型判断 API
- `HierarchicalBeanFactory`：父子工厂可见性（与 Context hierarchy 相关）
- `ListableBeanFactory`：枚举能力（`getBeansOfType` / `getBeanNamesForType`）
- `AutowireCapableBeanFactory`：对“容器外对象”做注入/初始化（见 Part05 的对应章节/Lab）
- `ConfigurableBeanFactory`：更底层的配置能力（scope/aliases/value resolvers 等）
- `ConfigurableListableBeanFactory`：综合性最强（可枚举 + 可配置 + 可用于内部框架扩展）
- `BeanDefinitionRegistry`：定义层的注册/移除（BeanDefinition 的“仓库”）

> `DefaultListableBeanFactory` 基本上是这些接口的“集大成者”，所以你在断点里大概率会看到它。

本章 Lab 就是用 “同一个 bean” 做对照：

你会在日志/断点里看到：

## 5. Debug / 断点入口与观察点（把“注解为什么不生效”变成可证明结论）

推荐断点：

推荐断点：

## 常见坑与边界

## 3. 关键边界：plain BeanFactory 不会“自动让注解生效”

很多人第一次直接 new 一个 `DefaultListableBeanFactory` 会踩坑：

## 6. 常见误区

1) **误区：BeanFactory = “更轻量更推荐”**
   - 轻量不等于省心。除非你非常明确自己要控制哪些 post-processors，否则默认用 ApplicationContext。
2) **误区：我注册了 `ConfigurationClassPostProcessor` 这个 bean，就等于注解能工作**
   - 不够：你还需要“执行/注册”整套基础设施链路（ApplicationContext refresh 会做，plain BeanFactory 不会自动做）。
3) **误区：只要加了 BPP，就能让以前创建过的 bean 也被处理**
   - BPP 通常不 retroactive。顺序与时机是排障关键点。

## 小结与下一章

- `AbstractBeanFactory#doGetBean`（拿 bean 的主入口）
- `AbstractAutowireCapableBeanFactory#doCreateBean`（实例化/填充/初始化）
- `DefaultListableBeanFactory#doResolveDependency`（依赖解析/候选收敛）

- `AbstractAutowireCapableBeanFactory#populateBean`（准备进入属性填充）
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（@Autowired/@Value 等注解注入）
- `CommonAnnotationBeanPostProcessor#postProcessProperties`（@Resource/@PostConstruct 等）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`

上一章：[38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](38-environment-and-propertysource.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md)

<!-- BOOKIFY:END -->

# 09. 循环依赖：现象、原因与规避（constructor vs setter）

## 导读

- 本章主题：**09. 循环依赖：现象、原因与规避（constructor vs setter）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线

循环依赖是 Spring 学习路上绕不开的一关。它既是“容器能力的体现”，也是“架构设计的警报”。

这一章回答：

1) 为什么构造器循环依赖会失败？
2) 为什么 setter 循环依赖有时会成功？
3) 在工程里你应该怎么处理？

## 1. 两个最经典的环

### 1.1 构造器循环（通常失败）

原因直观到一句话就够：

> 构造器注入要求依赖在“创建对象之前”就必须准备好，因此环上任何一环都无法先完成实例化。

### 1.2 setter 循环（有时能成功）

它能成功的关键在于 Spring 对 singleton 的一个机制：**提前暴露（early singleton exposure）**。

你可以把它理解为：

1) 容器创建 A（先 new 出来）
2) 发现 A 需要 B，于是去创建 B
3) 创建 B 时发现它需要 A
4) 如果容器允许，它可以把“一个尚未完全初始化的 A 引用（或一个工厂）”提前暴露给 B
5) B 创建完成后再回头把 B 注入 A

这是一种“让环先跑起来”的机制，但它也带来风险：你可能拿到半初始化对象，或者代理/增强顺序更复杂。

## 2. Spring Framework vs Spring Boot：一个重要差异

但在 Spring Boot 里，循环依赖在较新的版本中往往默认更严格（常见做法是默认禁止），并提供开关允许：

- `spring.main.allow-circular-references`

这也是为什么你会遇到：

- 同样的代码，单测用 `AnnotationConfigApplicationContext` 能过
- 放到 Boot 应用里就启动失败

学习时建议你把它当作“工具差异”，更重要的是理解机制与规避策略。

## 3. 工程上怎么处理（强烈建议按优先级）

### 3.1 首选：重构消除环（正确方向）

常见做法：

- 抽取一个更小的职责接口
- 把共享逻辑下沉到第三个组件
- 用事件/回调解耦（见 `spring-core-events`）

### 3.2 次选：用延迟依赖打断环（了解，谨慎）

例如：

- `ObjectProvider<T>`
- `@Lazy`（让某个依赖延迟初始化）

它们的本质都是：不要在“创建对象时”就强行拿到完整依赖，推迟到使用时再获取。

### 3.3 不推荐：为了让它“能启动”而改成 setter 注入

setter 注入在某些情况下能让环跑起来，但会：

- 降低不可变性
- 增加半初始化风险
- 让 bug 更隐蔽

> 你不需要背三层缓存的字段名，但你必须能解释：**容器为了打断循环，允许在对象未完全初始化时先暴露一个引用**，并且这件事只对 singleton 才有意义。

## 源码解析：三层缓存（三级缓存）在源码里是怎么“救火”的

很多资料把循环依赖讲成一句话：“Spring 用三级缓存解决了 setter 循环依赖”。这句话如果不落到源码细节，容易产生两个误解：

1) 以为“任何循环依赖都能救”  
2) 以为“三级缓存只是个数据结构技巧”，忽略了它真正的语义：**允许 early reference（早期引用）出现**

### 1) `getSingleton` 的三层命中逻辑（精简伪代码）

- `singletonObjects`：完全初始化完成的单例（最终成品）
- `earlySingletonObjects`：早期引用（半成品/可能是代理）
- `singletonFactories`：`ObjectFactory<?>`（用来“延迟生成 early reference”）

```text
getSingleton(beanName, allowEarlyReference):
  singleton = singletonObjects.get(beanName)
  if (singleton == null && isSingletonCurrentlyInCreation(beanName)):
     singleton = earlySingletonObjects.get(beanName)
     if (singleton == null && allowEarlyReference):
        factory = singletonFactories.get(beanName)
        if (factory != null):
           singleton = factory.getObject()                // 关键：创建 early reference
           earlySingletonObjects.put(beanName, singleton) // 放入二级缓存
           singletonFactories.remove(beanName)            // 移除三级工厂
  return singleton
```

这个逻辑解释了“setter 循环为什么可能成功”：

- B 创建过程中需要 A 时，A 可能已经“创建了一半”，但还没初始化完成
- `getSingleton(..., allowEarlyReference=true)` 会允许从 factory 里拿到 A 的 early reference（可能是 raw，也可能是 proxy）
- B 先拿到一个能用的引用把环跑起来，等 A 后续初始化完成再替换到一级缓存

### 2) early singleton exposure 是在 `doCreateBean` 哪一步发生的？

1) bean 实例已经创建出来（instantiate 已完成）
2) 但还没有执行完整的 populate + initialize（因此仍然是“半成品”）
3) 若允许循环依赖，容器会提前注册一个 `singletonFactory`，其 `getObject()` 通常会调用 `getEarlyBeanReference(...)`

因此你会看到一个很关键的事实：

- **循环依赖不是“注入阶段的魔法”，而是“实例创建阶段刻意留出的窗口”**

### 3) 代理介入时为什么更关键：`getEarlyBeanReference`

如果最终暴露对象会被代理（尤其是 JDK 代理），循环依赖里就会出现一个危险点：

- B 先注入了 raw A（具体类实例）
- 但 A 初始化完成后被 BPP 包装成了 proxy（最终暴露对象变了）
- 这会导致“raw 注入 vs 最终暴露对象”不一致，出现类型不匹配或行为不一致

为了解决这类问题，Spring 提供了 early reference 的扩展点：

- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`

```java
static class EarlyProxyingPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        // 在循环依赖窗口期提前返回 proxy，避免 raw 注入
        ...
    }
}
```

你把这段代码与上面的 `getSingleton` 伪代码对照，就会明白：

- 三级缓存不是“缓存技巧”，它承载的是“early reference 的时机与语义”
- `getEarlyBeanReference` 决定了 early reference 是 raw 还是 proxy

```java
record CycleA(CycleB cycleB) {}
record CycleB(CycleA cycleA) {}
```

**setter 循环（可能成功）**：同一文件里用 `@Autowired` setter 形成环，容器通过 early exposure 让引用先跑起来。

建议直接从这些测试方法开始（每个都对应一个经典结论）：

- constructor cycle（fail-fast）：
  - `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`
  - `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleFailsFast`
- setter cycle（可能成功：early singleton exposure）：
  - `SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`
- constructor cycle 的两种“打断环”手段（理解边界与代价）：
  - `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaLazyInjectionPointProxy`（@Lazy 注入点代理）
  - `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaObjectProvider`（ObjectProvider 延迟获取）
- early proxy（代理介入 early reference 的关键分支）：
  - `SpringCoreBeansEarlyReferenceLabTest#getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution`

## Boot vs Framework：你必须知道的“默认策略差异”

- 纯 Spring Framework 的 `DefaultListableBeanFactory` 默认允许 circular references（因此 setter 场景经常“能救”）
- Spring Boot 会在启动时基于配置设置 `allowCircularReferences`（默认更严格），因此同样的设计在 Boot 下可能直接 fail-fast

这也是为什么工程实践里更推荐“从设计上消除环”，而不是依赖容器救场：一旦默认策略变化，你的系统就可能在升级/换环境时突然启动失败。

## 4. 一句话自检

你应该能回答：

1) “构造器循环为什么必然失败？”
2) “setter 循环为什么有时能成功？early exposure 是什么？”
3) “为什么循环依赖在架构上通常是坏味道？”

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEarlyReferenceLabTest test`

本模块实验：

- `SpringCoreBeansContainerLabTest.circularDependencyWithConstructorsFailsFast()`

本模块实验：

- `SpringCoreBeansContainerLabTest.circularDependencyWithSettersMaySucceedViaEarlySingletonExposure()`

在纯 Spring Framework 容器里，setter 的单例循环依赖通常默认允许（如本模块实验所示）。

## 源码锚点（建议从这里下断点）

如果你想把“为什么 setter 有时能救、constructor 基本救不了”彻底打穿，建议至少跑一次三层缓存断点闭环：

你在断点里看到的三层缓存通常对应这三类语义（字段名不必背，但建议能识别）：

- `singletonObjects`：完全初始化完成的单例（最终成品）
- `earlySingletonObjects`：early reference（半成品/可能是 proxy）
- `singletonFactories`：ObjectFactory（用来延迟生成 early reference）

精简伪代码（足够对照断点理解）：

本模块的实验（`SpringCoreBeansEarlyReferenceLabTest`）用最小代码把这件事讲透：

## 必要时用仓库 src 代码复现两类环（最小片段）

**构造器循环（fail-fast）**：`SpringCoreBeansContainerLabTest`（最小片段）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- 构造器循环为什么失败：
  - `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`
- setter 循环为什么可能成功（early singleton exposure）：
  - `SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`
- 代理介入时，early reference 为什么更关键：
  - `SpringCoreBeansEarlyReferenceLabTest#getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution`

下一章我们把这些概念和 Spring Boot 联系起来：自动装配如何“加入更多 bean”，从而让依赖图变复杂。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
推荐断点：`DefaultSingletonBeanRegistry#getSingleton`、`DefaultSingletonBeanRegistry#addSingletonFactory`、`AbstractAutowireCapableBeanFactory#doCreateBean`

## 常见坑与边界

- **坑 1：误以为“Spring 能解决所有循环依赖”**
  - 事实：Spring 只能在非常特定的窗口期（singleton + early exposure）里“救活某些环”，constructor 环通常 fail-fast。
  - 对照：`SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`
- **坑 2：为了让它“能启动”把所有依赖改成 setter 注入**
  - setter 有时能救活环，但会引入半初始化窗口与更隐蔽的 bug。
  - 学习阶段可以用 setter 环理解机制；工程上仍优先消除环（重构职责边界）。
- **坑 3：把 `@Lazy` 当作默认解法（只看结果不看代价）**
  - `@Lazy` 注入点通常会注入一个延迟解析的代理：能打断 constructor 环，但也会让类型/调试复杂度上升（尤其在代理叠加场景）。
  - 对照：`SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaLazyInjectionPointProxy`
- **坑 4：`ObjectProvider<T>` 能救场，但别把依赖“藏起来”**
  - `ObjectProvider` 的核心价值是 **延迟获取**：把“必须立刻拿到依赖”变成“需要时再拿”。
  - 代价是：依赖关系从“构造器签名”退化为“运行时分支”，需要更强的自检与测试覆盖来兜底。
  - 对照：`SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaObjectProvider`

## 小结与下一章

- 取单例总入口：`DefaultSingletonBeanRegistry#getSingleton`
  - 重点观察：是否命中 `singletonObjects` / `earlySingletonObjects` / `singletonFactories`
- 创建 bean 主线：`AbstractAutowireCapableBeanFactory#doCreateBean`
  - 重点观察：什么时候会 `addSingletonFactory`（提前暴露），什么时候才 `addSingleton`（完全初始化后）
- 注入阶段：`AbstractAutowireCapableBeanFactory#populateBean`
  - 重点观察：setter 注入为什么可能在“对象未完全初始化”时先拿到一个引用

对应到实例创建主线（`AbstractAutowireCapableBeanFactory#doCreateBean`），early exposure 的窗口期大致是：

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[08. FactoryBean：product vs factory（& 前缀）](08-factorybean.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)

<!-- BOOKIFY:END -->

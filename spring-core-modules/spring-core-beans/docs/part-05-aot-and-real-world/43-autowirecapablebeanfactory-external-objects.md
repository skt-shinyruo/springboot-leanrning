# 43. 容器外对象注入：AutowireCapableBeanFactory

## 导读

- 本章主题：**43. 容器外对象注入：AutowireCapableBeanFactory**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`

## 机制主线

真实项目里你一定会遇到这种场景：

> “这个对象不是 Spring 创建的，但我希望它能用到 Spring 的依赖注入/回调能力。”

典型例子：

- 某些第三方框架创建对象（非 Spring 托管）
- 你手工 new 了对象（工具类/策略对象/回调对象）
- 测试里构造了对象，但想复用容器的注入能力

这类问题的核心是：**区分“容器管理的 bean”与“容器外对象”**。

---

## 1. 结论先行：注入 ≠ 生命周期托管 ≠ 代理替换

对容器外对象，你能做到的事情通常分成三层：

1) **注入（populate）**：把 `@Autowired/@Value` 等依赖填进去
2) **初始化（initialize）**：执行 Aware、`@PostConstruct`、`afterPropertiesSet`、以及 BeanPostProcessor
3) **销毁（destroy）**：触发 `@PreDestroy` 等销毁回调

在 Spring 里，这三层能力对外的入口就是：

- `AutowireCapableBeanFactory#autowireBean`：尽力完成依赖注入（populate）
- `AutowireCapableBeanFactory#initializeBean`：触发初始化链路（Aware / BPP / init callbacks）
- `AutowireCapableBeanFactory#destroyBean`：触发销毁回调（@PreDestroy 等）

一个非常容易踩的点是：`initializeBean(...)` **可能返回一个“被 BPP 包装/替换后的对象”**（例如代理）。
因此在“容器外对象”场景里，如果你想要 AOP/代理语义，必须使用 `initializeBean` 的返回值，而不是继续拿原始对象用。

---

- 只做 `autowireBean`：依赖可能已注入，但 `@PostConstruct` 还没跑
- 再做 `initializeBean`：`@PostConstruct` 才会被触发（因为它依赖 BPP）
- 最后 `destroyBean`：触发销毁回调（用于你理解“prototype 默认不销毁”的反面）

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`
  - `autowireThenInitialize_canApplyInjectionAndPostConstruct_forExternalObject()`（注入 vs @PostConstruct 的边界）
  - `destroyBean_canTriggerPreDestroy_forExternalObject_afterInitialization()`（显式销毁回调）

你只需要记住两个入口就能覆盖大多数排障（注入 vs 初始化）：

- `AutowireCapableBeanFactory#autowireBean`：只做注入，不等价于“完整生命周期”
- `AutowireCapableBeanFactory#initializeBean`：把对象送进初始化链路（BPP/@PostConstruct 等从这里开始）

当你想对照“容器外对象”与“容器管理 bean”的差异时，再回到：

- `AbstractAutowireCapableBeanFactory#populateBean`（容器内：注入发生点）
- `AbstractAutowireCapableBeanFactory#initializeBean`（容器内：初始化串联点）

这一章你应该能回答：

- autowireBean / initializeBean / destroyBean 分别解决什么问题？
- 为什么 `@PostConstruct` 不会在 autowireBean 之后自动发生？
- 为什么说 `initializeBean` 的返回值才是“最终可用对象”？（BPP 可能返回 proxy/wrapper）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 2. 复现入口（可运行）

本模块提供一个最小对照实验，帮助你建立直觉：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`

推荐运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCapableBeanFactoryLabTest test
```

## 3. 源码 / 断点建议（把“容器外对象”放回统一生命周期主线）

当你要进一步解释“到底是谁在做注入/谁在触发 @PostConstruct”时，常用加深断点：

1) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：`@Autowired/@Value` 等注入入口（证明注入不等价于 init）
2) `AbstractAutowireCapableBeanFactory#initializeBean`：初始化串联点（Aware + init callbacks + BPP）
3) `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：`@PostConstruct` 触发点之一（也解释为什么必须 initialize）
4) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：BPP 可能在这里返回 proxy（解释“final object != raw object”）

## 常见坑与边界

- [25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)
- [05. 初始化、销毁与回调](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)

## 4. 常见误区

1) **误区：autowireBean 之后就等同于容器管理**
   - 实际：它只是“尽力帮你补上部分管道”，你仍要对生命周期与代理替换保持警惕。
2) **误区：容器外对象一定不能用 @PostConstruct**
   - 可以，但你要显式调用 initialize 链路（否则 BPP 不会触发）。

## 面试常问（容器外对象：AutowireCapableBeanFactory 的边界）

### Q1：AutowireCapableBeanFactory 解决的是什么问题？它“没解决什么”？

- 标准答案（可复述）：
  - 它让容器外对象也能获得“注入/初始化/销毁”等能力入口；但它不等于完整托管生命周期（对象的创建、持有、使用时机仍由你控制），也不会自动把所有容器语义（例如完整的创建时序/代理链）都补齐。
- 证据链（方法级）：
  - `AutowireCapableBeanFactory#autowireBean`
  - `AutowireCapableBeanFactory#initializeBean`
  - `AutowireCapableBeanFactory#destroyBean`
- 最小复现：
  - `SpringCoreBeansAutowireCapableBeanFactoryLabTest`

### Q2：为什么这类“手工拼装”在工程里要谨慎使用？

- 标准答案（可复述）：
  - 它容易引入时机不确定、重复注入/重复初始化、以及与容器内 bean 语义不一致等问题；更推荐把对象创建权交回容器（定义层注册），让创建链路可预测。

## 一句话自检

- 你能解释清楚：为什么“容器外对象”不会自动触发 `@Autowired/@PostConstruct/@PreDestroy` 吗？
- 你能说出：`autowireBean`、`initializeBean`、`destroyBean` 三个 API 分别补的是哪一段管道吗？
- 你能说明：在容器外对象场景里，为什么仍然要警惕“最终暴露对象可能是 proxy”这件事吗？（提示：BPP 仍可能替换对象）

## 小结与下一章

- `AutowireCapableBeanFactory#autowireBean`（偏“只做注入”）
- `AutowireCapableBeanFactory#initializeBean`（触发初始化链路）
- `AutowireCapableBeanFactory#destroyBean`（触发销毁链路）

- `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点）
- `AbstractAutowireCapableBeanFactory#initializeBean`（Aware/BPP/init callbacks 串联点）

- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（`@Autowired/@Value` 等注入入口）
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 入口之一）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（BPP 可能在这里返回 proxy）

## 5. 小结与下一章预告

下一章我们补齐另一个真实项目高频点：`@Value("#{...}")`（SpEL）—— 值注入链路如何拆成“解析 vs 计算 vs 转换”三段。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`

上一章：[42. XML → BeanDefinitionReader：定义层解析与错误分型](42-xml-bean-definition-reader.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[44. SpEL 与 `@Value("#{...}")`：表达式解析链路](44-spel-and-value-expression.md)

<!-- BOOKIFY:END -->

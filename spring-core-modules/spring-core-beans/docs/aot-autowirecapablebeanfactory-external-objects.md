# 容器外对象注入：AutowireCapableBeanFactory
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把输入层解析或 AOT 契约变成可验证结果；再回到正文用断点把关键分支（reader/hints/值解析）观察到并能解释。

    观察对象：43. 容器外对象注入：AutowireCapableBeanFactory。
    主线位置：输入层（XML/Properties/Groovy）解析的落点仍是 BeanDefinition；AOT/Native 的关键是把反射/代理/资源等需求变成可测试的构建期契约（RuntimeHints）。

    对照入口：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`。需要下探源码时，可以从 `AutowireCapableBeanFactory#initializeBean` / `AutowireCapableBeanFactory#autowireBean` / `AutowireCapableBeanFactory#destroyBean` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：容器外对象注入：AutowireCapableBeanFactory

先运行 `SpringCoreBeansAutowireCapableBeanFactoryLabTest`，把核心现象固定为可复现事实；随后围绕入口方法、关键分支和可观察变量阅读正文。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（AOT，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/aot.html
- 官方文档对照（Spring Boot Reference，适用版本：3.5.9）：https://docs.spring.io/spring-boot/reference/


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

真实项目里读者一定会遇到这种场景：

> “这个对象不是 Spring 创建的，但希望它能用到 Spring 的依赖注入/回调能力。”

典型例子：

- 某些第三方框架创建对象（非 Spring 托管）
- 读者手工 new 了对象（工具类/策略对象/回调对象）
- 测试里构造了对象，但想复用容器的注入能力

这类问题的核心是：**区分“容器管理的 bean”与“容器外对象”**。

---

### 机制边界：条件、分支与结果

**条件**：对象不是 Spring 创建的
**分支**：是否显式调用 `autowireBean/initializeBean/destroyBean`
**结果**：不调用 → 注解/回调不生效；调用 → 只补齐被调用的那一段管道
**断点入口**：`AutowireCapableBeanFactory#initializeBean`

## 集成案例（真实项目高频）：第三方回调对象如何“补齐注入”

典型场景：框架回调/监听器由第三方创建，但读者希望它能注入 Spring 依赖。

最小实践路径：

1. `autowireBean`：把依赖塞进去
2. `initializeBean`：触发 `@PostConstruct` 与 BPP（获取到最终对象）

> 关键提醒：一定要使用 `initializeBean` 的返回值，否则可能丢失代理语义。

## 核心结论：注入、生命周期托管与代理替换不是同一件事

对容器外对象，需要做到的事情通常分成三层：

1. **注入（populate）**：把 `@Autowired/@Value` 等依赖填进去
2. **初始化（initialize）**：执行 Aware、`@PostConstruct`、`afterPropertiesSet`、以及 BeanPostProcessor
3. **销毁（destroy）**：触发 `@PreDestroy` 等销毁回调

在 Spring 里，这三层能力对外的入口就是：

- `AutowireCapableBeanFactory#autowireBean`：尽力完成依赖注入（populate）
- `AutowireCapableBeanFactory#initializeBean`：触发初始化链路（Aware / BPP / init callbacks）
- `AutowireCapableBeanFactory#destroyBean`：触发销毁回调（@PreDestroy 等）

一个易出错的点是：`initializeBean(...)` **可能返回一个“被 BPP 包装/替换后的对象”**（例如代理）。
因此在“容器外对象”场景里，若想要 AOP/代理语义，必须使用 `initializeBean` 的返回值，而不是继续拿原始对象用。

---

- 只做 `autowireBean`：依赖可能已注入，但 `@PostConstruct` 尚未执行
- 再做 `initializeBean`：`@PostConstruct` 才会被触发（因为它依赖 BPP）
- 最后 `destroyBean`：触发销毁回调（用于读者理解“prototype 默认不销毁”的反面）

入口测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`
  - `autowireThenInitialize_canApplyInjectionAndPostConstruct_forExternalObject()`（注入 vs @PostConstruct 的边界）
  - `destroyBean_canTriggerPreDestroy_forExternalObject_afterInitialization()`（显式销毁回调）

读者只需要记住两个入口就能覆盖大多数排障（注入 vs 初始化）：

- `AutowireCapableBeanFactory#autowireBean`：只做注入，不等价于“完整生命周期”
- `AutowireCapableBeanFactory#initializeBean`：把对象送进初始化链路（BPP/@PostConstruct 等从这里开始）

当需要对照“容器外对象”与“容器管理 bean”的差异时，再回到：

- `AbstractAutowireCapableBeanFactory#populateBean`（容器内：注入发生点）
- `AbstractAutowireCapableBeanFactory#initializeBean`（容器内：初始化串联点）

这一章需要能回答：

- autowireBean / initializeBean / destroyBean 分别解决什么问题？
- 为什么 `@PostConstruct` 不会在 autowireBean 之后自动发生？
- 为什么说 `initializeBean` 的返回值才是“最终可用对象”？（BPP 可能返回 proxy/wrapper）

---

## 实验：把现象固定成断言

本章可复核的实验入口：
- Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 从实验现象看边界

## 运行入口

本模块提供一个最小对照实验，帮助读者建立预期：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`

运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutowireCapableBeanFactoryLabTest test
```

## 源码 / 断点入口（把“容器外对象”放回统一生命周期主线）

当需要进一步解释“到底是谁在做注入/谁在触发 @PostConstruct”时，常用加深断点：

1. `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：`@Autowired/@Value` 等注入入口（证明注入不等价于 init）
2. `AbstractAutowireCapableBeanFactory#initializeBean`：初始化串联点（Aware + init callbacks + BPP）
3. `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：`@PostConstruct` 触发点之一（也解释为什么必须 initialize）
4. `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：BPP 可能在这里返回 proxy（解释“final object != raw object”）

## 边界：容器外对象注入：AutowireCapableBeanFactory
> 官方参考（Spring Framework 6.2.x，注解驱动与依赖注入语义）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html


- [手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](wiring-programmatic-bpp-registration.md)
- [初始化、销毁与回调](ioc-lifecycle-and-callbacks.md)

## 误判点：容器外对象注入：AutowireCapableBeanFactory

1. **误区：autowireBean 之后就等同于容器管理**
   - 实际：它只是“尽力帮读者补上部分管道”，读者仍要对生命周期与代理替换保持警惕。
2. **误区：容器外对象一定不能用 @PostConstruct**
   - 可以，但需要显式调用 initialize 链路（否则 BPP 不会触发）。

## 面试常问（容器外对象：AutowireCapableBeanFactory 的边界）

### Q1：AutowireCapableBeanFactory 解决的是什么问题？它“没解决什么”？

- 标准答案（可复述）：
  - 它让容器外对象也能获得“注入/初始化/销毁”等能力入口；但它不等于完整托管生命周期（对象的创建、持有、使用时机仍由读者控制），也不会自动把所有容器语义（例如完整的创建时序/代理链）都补齐。
- 证据链（方法级）：
  - `AutowireCapableBeanFactory#autowireBean`
  - `AutowireCapableBeanFactory#initializeBean`
  - `AutowireCapableBeanFactory#destroyBean`
- 最小复现：
  - `SpringCoreBeansAutowireCapableBeanFactoryLabTest`

### Q2：为什么这类“手工拼装”在工程里要谨慎使用？

- 标准答案（可复述）：
  - 它容易引入时机不确定、重复注入/重复初始化、以及与容器内 bean 语义不一致等问题；更稳妥的方式是把对象创建权交回容器（定义层注册），让创建链路可预测。

## 验收口径：容器外对象注入：AutowireCapableBeanFactory
- 需要解释清楚：为什么“容器外对象”不会自动触发 `@Autowired/@PostConstruct/@PreDestroy` 吗？
- 需要说出：`autowireBean`、`initializeBean`、`destroyBean` 三个 API 分别补的是哪一段管道吗？
- 需要说明：在容器外对象场景里，为什么仍然要警惕“最终暴露对象可能是 proxy”这件事吗？（提示：BPP 仍可能替换对象）

## 小结：容器外对象注入：AutowireCapableBeanFactory

- `AutowireCapableBeanFactory#autowireBean`（偏“只做注入”）
- `AutowireCapableBeanFactory#initializeBean`（触发初始化链路）
- `AutowireCapableBeanFactory#destroyBean`（触发销毁链路）

- `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点）
- `AbstractAutowireCapableBeanFactory#initializeBean`（Aware/BPP/init callbacks 串联点）

- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（`@Autowired/@Value` 等注入入口）
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 入口之一）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（BPP 可能在这里返回 proxy）

## 小结：容器外对象注入：AutowireCapableBeanFactory 与下一章入口


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`

<!-- BOOKIFY:END -->

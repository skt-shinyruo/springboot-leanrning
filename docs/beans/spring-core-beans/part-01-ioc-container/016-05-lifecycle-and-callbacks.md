# 第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAwareInfrastructureLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](015-04-scope-and-prototype.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](017-06-post-processors.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAwareInfrastructureLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java`

## 机制主线

这一章解决两个问题：

1) **Bean 从“还不存在”到“可以被使用”经历了什么阶段？**
2) 你写的各种回调（`@PostConstruct` / `@PreDestroy` / `initMethod` …）到底在什么时机执行？

## 1. 一个典型 Bean 的生命周期（高层版）

如果你只记住一句话：**生命周期 = instantiate → populate → initialize → (use) → destroy**。

但读者 C（源码级）需要更具体：你至少要能把“高层版”映射到 Spring 的关键方法名与扩展点，否则一旦遇到代理/循环依赖/后处理器介入，你会完全失去定位能力。

### 1.1 源码级时间线（建议能复述）

> 重要提醒：`@PostConstruct/@PreDestroy` 不是 Java 语法“自带”的生命周期，它依赖容器注册了对应的后处理器；这也是为什么理解 [06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](017-06-post-processors.md) 与 [12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) 非常关键。

### 1.3 源码解析：`initializeBean` 的回调链路（精简伪代码）

你不需要逐行背 Spring 源码，但**必须能把“回调顺序”落到一个稳定的方法骨架**，否则遇到 BPP/代理介入时很难解释“为什么是这个顺序”。

在 Spring Framework 里，初始化阶段的骨架非常稳定（精简伪代码）：

```text
initializeBean(beanName, bean, mbd):
  invokeAwareMethods(beanName, bean)                      // BeanNameAware/BeanFactoryAware 等
  bean = applyBeanPostProcessorsBeforeInitialization(...) // 这里可能触发 @PostConstruct
  invokeInitMethods(beanName, bean, mbd)                  // afterPropertiesSet / initMethod
  bean = applyBeanPostProcessorsAfterInitialization(...)  // 这里经常产生 proxy（最终暴露对象）
  return bean
```

几个“不要说错”的点（框架岗常追问）：

1) **Aware 发生在 init callbacks 之前**：因为很多 init 逻辑需要先拿到 beanName/BeanFactory 等容器信息  
2) **`@PostConstruct` 发生在 before-init BPP 链路中**：它不是 `initializeBean` 的“硬编码步骤”，而是由一个 BPP 触发  
3) **after-init 可能返回代理**：因此“最终暴露对象”可能不是原始实例（这也是为什么生命周期与代理经常被放在一起讲）

### 1.4 源码解析：销毁链路（singleton 的 destroy 主线）

销毁不是“某个注解自动执行”，它同样是一条容器主线：

1) `AbstractApplicationContext#close` / `doClose`
2) `DefaultSingletonBeanRegistry#destroySingletons`
3) `DisposableBeanAdapter#destroy`（把各种 destroy 方式统一成一个适配器）

```text
destroySingletons():
  for each disposableBean:
    invokeDestructionAwareBeanPostProcessorsBeforeDestruction(...)
    invoke @PreDestroy (JSR-250)
    invoke DisposableBean#destroy
    invoke custom destroyMethod (e.g., @Bean(destroyMethod=...))
```

对应到工程结论就是：

### 1.5 必要时用仓库 src 代码把“顺序”固化成可断言结论

1) 它用一个 `RecordingBeanPostProcessor` 在 before/after-init 打点
2) bean 本身同时实现 Aware、InitializingBean、DisposableBean，并声明 `@PostConstruct/@PreDestroy`
3) 最后断言事件顺序（你不需要看日志，直接看断言即可）

最小片段（省略无关实现）：

```java
assertThat(events).containsExactly(
        "constructor",
        "aware:beanName=recordingBean",
        "aware:beanFactory",
        "aware:applicationContext",
        "bpp:beforeInit",
        "postConstruct",
        "afterPropertiesSet",
        "initMethod",
        "bpp:afterInit",
        "preDestroy",
        "destroy",
        "destroyMethod"
);
```

以及一个更贴近应用直觉的例子（`src/main/java`）：

```java
@Component
public class LifecycleLogger {
    @PostConstruct void onInit() { ... }
    @PreDestroy void onDestroy() { ... }
}
```

### 1.2 Aware 系列回调：真实作用、触发者与发生时机

很多人把 Aware 理解成“知道自己叫什么名字”，但对原理/框架岗来说，更关键的是：

- **它发生在生命周期的哪一段？**
- **是谁触发（调用）这些回调的？**
- **如果容器不具备对应的基础设施，会发生什么？**

你需要把 Aware 分成两类理解（这是面试追问的关键分界线）：

这里还有一个容易说错的点（面试官很爱追）：  
**Aware 发生在“初始化（initialize）阶段”，不是“属性填充（populate）阶段”。**  
也就是说：依赖注入通常已经完成（字段/构造器参数已经有值），Aware 是在此基础上把“容器信息”补给 bean，然后才进入 `@PostConstruct` 等 init 回调链路。

#### 面试常问：Aware 系列接口的真实作用与时机

把一个 bean 的创建过程粗略拆成：

1) 实例化（constructor）
2) 依赖注入（populate properties / resolve dependencies）
3) 各种 Aware 回调（告诉 bean 容器信息）
4) 初始化前后回调（BPP 可能介入）
5) 初始化方法（`@PostConstruct` / `afterPropertiesSet` / `initMethod`）
6) 初始化后回调（BPP 可能返回代理）

销毁阶段（当容器关闭时）：

1) `@PreDestroy` / `DisposableBean.destroy` / `destroyMethod`

关键点：

- **BPP 可能改变最终暴露给外界的对象**（比如返回代理）
- **prototype 默认不会走销毁回调**（因为容器不负责其生命周期终点）

## 2. 本模块的可观测例子：`LifecycleLogger`

代码：`src/main/java/com/learning/springboot/springcorebeans/part01_ioc_container/LifecycleLogger.java`

- `@PostConstruct`：容器启动初始化时执行
- `@PreDestroy`：容器关闭时执行（例如应用退出）

你可以通过：

以及通过测试确认：

## 3. 常见生命周期回调方式（按“推荐度/常见度”）

### 3.1 `@PostConstruct` / `@PreDestroy`（推荐，最直观）

优点：

- 与业务代码耦合低（不需要实现 Spring 接口）
- 语义清晰

- 回调方法通常不应有参数
- 不建议做重 IO/长耗时工作（会拉长启动/关闭时间）

### 3.2 `InitializingBean` / `DisposableBean`（了解即可）

优点：Spring 原生接口  
缺点：让业务类依赖 Spring 接口（耦合）

### 3.3 `@Bean(initMethod=..., destroyMethod=...)`（配置级别控制）

适用场景：

- 你不想修改第三方类源码
- 你想集中管理初始化/销毁方法

## 4. 生命周期与 Scope 的交互（重点）

- singleton：通常在容器 refresh 时创建（除非 `@Lazy`）
- prototype：在你向容器请求它时创建（可能发生在注入时、也可能发生在 `ObjectProvider.getObject()` 调用时）

销毁回调：

- singleton：容器关闭时触发
- prototype：容器通常不触发（需要调用方自己管理）

这也是为什么 prototype 更像“容器帮你 new，一次性交付”，而不是“完整托管生命周期”。

### 4.1 prototype 销毁语义补齐：destroy callbacks 为什么不会自动发生？怎么手动触发？

一句话结论：

- **prototype 的销毁不是容器的职责**，而是“创建者（调用方）”的职责

原因并不玄学，本质是“容器没法帮你回收它不持有引用的对象”：

- singleton：容器会缓存实例，并在 close 时统一遍历并销毁（`destroySingletons` 主线）
- prototype：容器每次 `getBean` 都 new 一个给你，但通常不会把这些实例登记到“待销毁列表”里  
  → 因此 close 时它也不知道要销毁哪些实例

如果你确实需要触发 prototype 的销毁回调（例如释放连接/文件句柄等），需要显式调用销毁 API：

- `ConfigurableBeanFactory#destroyBean(beanName, instance)`：显式触发 `@PreDestroy` / `DisposableBean#destroy` / destroyMethod 等回调

你应该观察到：

1) `context.close()` 不会触发 prototype 的 `@PreDestroy`
2) 显式 `destroyBean(...)` 才会触发 destroy callbacks（资源释放责任在调用方）

- 初始化阶段的“BPP → @PostConstruct → afterPropertiesSet → initMethod → BPP after-init”顺序
- 为什么 prototype 默认不会走销毁回调（见 [17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）](../part-03-container-internals/17-lifecycle-callback-order.md) 的进一步解释）

## 5. 你应该能回答的 3 个问题

1) `@PostConstruct` 与依赖注入的先后顺序是什么？
2) 为什么 prototype 的 `@PreDestroy` 可能不会被调用？
3) 为什么 BPP 经常与生命周期放在一起讲？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAwareInfrastructureLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

下面这条时间线不是为了背源码，而是为了让你在断点里“认路”：

精简伪代码（帮助你在断点里认路）：

- singleton：容器负责销毁（因此你能看到 @PreDestroy）
- prototype：容器通常不负责销毁（因此你“看不到 @PreDestroy”，见下文的 Lab）

本模块的 `SpringCoreBeansLifecycleCallbackOrderLabTest` 就是把上面的骨架变成可重复实验：

- 题目：`BeanNameAware/BeanFactoryAware/ApplicationContextAware` 分别在生命周期哪一段触发？为什么它们要发生在 init callbacks 之前？
- 追问：
  - Aware 是谁调用的？哪些是容器直接调的，哪些依赖基础设施处理器（BPP）？
  - 如果某个“容器”不注册 `ApplicationContextAwareProcessor` 会怎样？你如何用断点/断言证明？
- 复现入口（可断言 + 可断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
    - `singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization()`
  - “没有基础设施处理器就不会触发”的最小对照：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`
      - `beanFactoryAware_isInvokedByBeanFactory_butApplicationContextAware_needsAnInfrastructureProcessor()`
      - `applicationContextAware_isInvokedByInfrastructureBeanPostProcessor()`

- 运行：`mvn -pl :spring-core-beans spring-boot:run`
- 观察控制台输出：
  - `LifecycleLogger: @PostConstruct called`

- `SpringCoreBeansLabTest.postConstructRunsDuringContextInitialization()`

最小可复现入口（必现，且可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPrototypeDestroySemanticsLabTest test`

## 源码锚点（建议从这里下断点）

想把生命周期“看见”，你至少需要这几个断点入口：

## 断点闭环（用本仓库 Lab/Test 跑一遍）

建议直接跑并断点：

- `SpringCoreBeansLifecycleCallbackOrderLabTest#singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization`
- `SpringCoreBeansLifecycleCallbackOrderLabTest#prototypeBeans_areNotDestroyedByContainerByDefault`
- `SpringCoreBeansPrototypeDestroySemanticsLabTest#prototypeBean_canBeDestroyedManually_viaDestroyBean`

你应该能在断点里明确验证：

下一章我们专门讲扩展点：BFPP/BPP，它们就是影响“定义层/实例层”的关键入口。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`
推荐断点：`AbstractAutowireCapableBeanFactory#initializeBean`、`CommonAnnotationBeanPostProcessor#postProcessBeforeInitialization`、`DisposableBeanAdapter#destroy`

## 常见坑与边界

注意：

## 小结与下一章

1. **实例化（instantiate）**
   - 入口常见在 `AbstractAutowireCapableBeanFactory#createBean` / `doCreateBean`
   - 关注点：构造器选择、工厂方法、`Supplier`、`FactoryBean` 等
2. **依赖注入/属性填充（populate properties）**
   - 关键方法：`AbstractAutowireCapableBeanFactory#populateBean`
   - 关注点：`@Autowired` 解析、候选选择、`@Value` 占位符解析（见 [03. 依赖注入解析：类型/名称/@Qualifier/@Primary](014-03-dependency-injection-resolution.md)、[34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](../part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)）
3. **初始化（initialize）**
   - 关键方法：`AbstractAutowireCapableBeanFactory#initializeBean`
   - Aware：`BeanNameAware`/`BeanFactoryAware` 等
   - BPP before-init：`BeanPostProcessor#postProcessBeforeInitialization`
   - `@PostConstruct`：由 `InitDestroyAnnotationBeanPostProcessor` 触发（见 [12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)）
   - `InitializingBean#afterPropertiesSet`
   - `@Bean(initMethod=...)`
   - BPP after-init：`BeanPostProcessor#postProcessAfterInitialization`（**代理通常在这里产生**，见 [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）
4. **销毁（destroy）**
   - 发生在容器关闭（或 scope 结束）时
   - 关键路径：`DefaultSingletonBeanRegistry#destroySingletons` → `DisposableBeanAdapter#destroy`
   - `@PreDestroy` / `DisposableBean#destroy` / `@Bean(destroyMethod=...)`

1) **BeanFactory 直接调用的 Aware（更底层、更稳定）**
   - 典型：`BeanNameAware` / `BeanFactoryAware` / `BeanClassLoaderAware`
   - 发生点：`AbstractAutowireCapableBeanFactory#initializeBean` 内部的 `invokeAwareMethods`
   - 时机：发生在 init callbacks 之前（因此能在 `@PostConstruct/afterPropertiesSet/initMethod` 前拿到容器基础信息）
2) **通过“基础设施处理器（BPP）”触发的 Aware（ApplicationContext 能力的一部分）**
   - 典型：`ApplicationContextAware` / `EnvironmentAware` / `ResourceLoaderAware` / `MessageSourceAware` 等
   - 触发者：`ApplicationContextAwareProcessor`（它本质是一个 `BeanPostProcessor`，由 `ApplicationContext` 在 refresh 过程中注册）
   - 结论：如果你只是在底层 `BeanFactory` 里用（没有注册对应处理器），这些 Aware 回调就不会发生

- 创建主线：`AbstractAutowireCapableBeanFactory#doCreateBean`
- 注入主线：`AbstractAutowireCapableBeanFactory#populateBean`
- 初始化主线：`AbstractAutowireCapableBeanFactory#initializeBean`、`invokeInitMethods`
- 销毁主线：`DefaultSingletonBeanRegistry#destroySingletons`、`DisposableBeanAdapter#destroy`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAwareInfrastructureLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansPrototypeDestroySemanticsLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest.java`

上一章：[作用域与 prototype](015-04-scope-and-prototype.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[扩展点：Post-Processor](017-06-post-processors.md)

<!-- BOOKIFY:END -->

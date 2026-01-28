# 第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](017-06-post-processors.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 19 章：11. 调试与自检：如何“看见”容器正在做什么](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线

这一章解释一个经常让人“以为 Spring 坏了”的现象：

> 为什么在 `@Configuration` 里调用另一个 `@Bean` 方法，有时会得到同一个实例，有时会 new 出一个新实例？

答案就在 `proxyBeanMethods`。

## 配置类解析主线（定义层发生了什么）

配置类解析发生在 **定义层**，核心入口在：

- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
- `ConfigurationClassParser#parse`
- `ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`

它把 `@Configuration/@Bean/@Import` 翻译成 **BeanDefinition** 并注册进 registry；  
实例化发生在 refresh 后段的 `preInstantiateSingletons`（不是这里）。

## 增强机制细节（proxyBeanMethods=true 才发生）

- 入口：`ConfigurationClassEnhancer#enhance`
- 关键拦截：`BeanMethodInterceptor#intercept`
- 它拦截的是 **配置类里 `@Bean` 方法的 Java 调用**，并把它重定向为 `BeanFactory#getBean`
- 因此“增强”影响的是 **方法调用语义**，不是 BeanDefinition 的注册

## 1. 两种配置方式的核心差异

`@Configuration(proxyBeanMethods = true)`（默认 true 的经典行为）：

- Spring 会对配置类做增强（通常是 CGLIB 子类）
- 当你调用 `@Bean` 方法时，会被拦截并重定向到容器
- 因此即便你在 `@Bean` 方法里直接调用另一个 `@Bean` 方法，也能维持“单例语义”

`@Configuration(proxyBeanMethods = false)`（Lite 模式 / 更偏性能）：

- Spring 不会为“方法调用语义”提供额外保障
- 你在方法体里直接调用另一个 `@Bean` 方法，就是一次普通 Java 方法调用
- 这可能会产生额外实例（绕过容器缓存）

### 1.1 机制讲透：条件 → 分支 → 结果（可断点验证）

**条件**：配置类是 Full（`@Configuration`）还是 Lite（`@Component + @Bean`），以及 `proxyBeanMethods` 取值  
**分支**：`ConfigurationClassPostProcessor#processConfigBeanDefinitions` 标记 Full/Lite  
**结果**：  
- Full + proxy=true：`ConfigurationClassEnhancer` 介入，`@Bean` 方法互调走容器  
- Lite 或 proxy=false：互调为普通 Java 调用，可能产生额外对象  
**断点建议**：`ConfigurationClassPostProcessor#processConfigBeanDefinitions` / `ConfigurationClassEnhancer#enhance`

建议先把“现象”做成可断言的闭环（别靠日志猜）：

### 2.1 你到底在对比什么？

两种模式都能把 `@Bean` 注册进容器；差异在于：**配置类自身是否会被增强（enhance）**，从而拦截 `@Bean` 方法调用。

- `@Configuration(proxyBeanMethods=true)`（默认）
  - 配置类会被 CGLIB 增强（你常会在类名里看到 `$$SpringCGLIB$$`）。
  - 在同一个配置类里，`@Bean` 方法互相调用时，会被拦截并改成 **从容器取 bean**。
  - 结果：你在 `@Bean` 方法里调用另一个 `@Bean` 方法，仍能保持 singleton 语义（同一个实例）。
- `@Configuration(proxyBeanMethods=false)`
  - 配置类不会拦截 `@Bean` 方法调用。
  - 在配置类内部互相调用 `@Bean` 方法，本质就是 **普通 Java 方法调用**。
  - 结果：你可能 new 出“额外对象”，即使容器里的对应 bean 依然是 singleton。

> 关键点：`proxyBeanMethods=false` 不是“Bean 变多例”，而是“你在配置类里手写的互调绕过了容器语义”。

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls()`（proxy=true：互调会走容器）
  - `configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance()`（proxy=false：互调是普通 Java 调用）
  - `liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance()`（Lite 模式：`@Component + @Bean` 不会增强）

你会看到：

- proxy=true：`configB()` 内调用 `configA()`，拿到的是容器里的同一个 `ConfigA`
- proxy=false：方法体直接调用导致 new 出另一个 `ConfigA`

## 3. 最推荐的写法：用“方法参数”声明依赖

如果你写：

```java
@Bean
ConfigB configB(ConfigA a) {
  return new ConfigB(a);
}
```

那么：

- 依赖解析由容器完成
- 不需要在方法体里调用另一个 `@Bean` 方法
- `proxyBeanMethods=false` 也能保持语义正确

这也是 Spring Boot / 自动配置里非常常见的写法：性能更好、语义更清晰。

- 配置类解析与增强入口：
  - `ConfigurationClassPostProcessor#postProcessBeanFactory`
  - `ConfigurationClassEnhancer#enhance`
- `@Bean` 方法拦截入口（proxyBeanMethods=true 才会走到）：
  - `ConfigurationClassEnhancer.BeanMethodInterceptor#intercept`（内部类名可能随版本略有变化）

### 4.1 推荐观察点（watch list）

- 配置类 bean 的运行时 class：
  - proxy=true：类名通常包含 `$$SpringCGLIB$$`（说明发生了 CGLIB 增强）
  - proxy=false / Lite：通常就是原始类（没有增强）
- `@Bean` 方法互调发生时：
  - 看调用栈是否进入 `ConfigurationClassEnhancer.BeanMethodInterceptor#intercept`
  - 看 `bean` / `beanName`：最终返回的是“容器里的单例”还是“方法体 new 出来的对象”
- （对照）容器里同名 bean 的获取路径：
  - `AbstractBeanFactory#doGetBean`：证明“从容器拿到的那个对象”与“方法互调返回的对象”是否一致

## 5. 你应该能回答的 2 个问题

1) `proxyBeanMethods` 影响的到底是什么？（提示：不是“这个 bean 是否是单例”，而是“配置类里方法调用会不会走容器”）
2) 为什么在大规模应用里，经常把 `proxyBeanMethods` 设为 false？

## 面试常问（`@Configuration(proxyBeanMethods=...)` 的语义）

- 常问：`proxyBeanMethods=true/false` 有什么差异？为什么 `false` 可能出现“额外实例”？
  - 答题要点：`true` 时配置类被增强，`@Bean` 方法互调会被拦截并走容器缓存，保持单例语义；`false` 时互调是普通 Java 调用，可能 new 出额外对象。
- 常见追问：在工程里如何避免误用？
  - 答题要点：避免在 `@Bean` 方法体内直接调用另一个 `@Bean` 方法；优先使用方法参数注入或构造注入，让依赖解析回到容器。

## 可复现闭环（基于 `SpringCoreBeansContainerLabTest`）

至少跑出 3 条可断言结论：

1) **proxy=true 时，`@Bean` 方法互调会回到容器**  
   - 断点：`BeanMethodInterceptor#intercept`  
   - 断言：互调返回同一实例
2) **proxy=false/Lite 时，互调是普通 Java 调用**  
   - 断点：互调调用栈不进入 `intercept`  
   - 断言：方法体内 new 出额外实例
3) **参数注入是最稳妥写法**  
   - 断点：`doResolveDependency`  
   - 断言：即使 proxy=false，容器注入仍保持单例语义

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansContainerLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`

## 2. 本模块的实验：一对比就明白

- 对比入口（直接从测试方法开始打断点）：
  - `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls`
  - `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance`
- 你要观察的不是“能不能注入”，而是 **配置类内部 `@Bean` 方法互相调用时：返回的是容器 singleton，还是一个新的 Java 对象**。

- `SpringCoreBeansContainerLabTest.configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls()`
- `SpringCoreBeansContainerLabTest.configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance()`

## 4. 源码锚点（建议从这里下断点）

如果你想把 `proxyBeanMethods` 的本质打穿（读者 C 目标），建议至少走一遍下面的断点闭环：

- 配置类 bean 的运行时 class：是否出现 `$$SpringCGLIB$$`
- `@Bean` 方法互调时的调用栈：是否进入 `BeanMethodInterceptor`

下一章我们讲另一个“名字相同但拿到的东西不同”的概念：`FactoryBean`。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
推荐断点：`ConfigurationClassPostProcessor#postProcessBeanFactory`、`ConfigurationClassEnhancer#enhance`

## 常见坑与边界

- **坑 1：`proxyBeanMethods=false` + `@Bean` 方法互调**
  - 现象：容器里 bean 依然是单例，但你在配置类内部互调会 new 出“额外对象”
  - 证据链：`SpringCoreBeansContainerLabTest.configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance()`
  - 修复：避免互调；改成“方法参数注入”（让依赖解析回到容器）
- **坑 2：Lite 模式（`@Component + @Bean`）没有增强**
  - 现象：你以为“写了 @Bean 就等于 @Configuration”，结果互调语义与 proxy=false 一样（不会拦截）
  - 证据链：`SpringCoreBeansContainerLabTest.liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance()`
  - 修复：把配置类显式改成 `@Configuration`（并明确 `proxyBeanMethods`）；或者同样避免互调
- **坑 3：误把它当成“scope 语义变化”**
  - 澄清：bean 还是单例；变的是“你在配置类里写的 Java 调用有没有被容器拦截并重定向”
  - 经验法则：只要你看到“配置类内部互调 @Bean 方法”，就默认它是风险点，优先改成“参数注入”
- **坑 4：跨配置类/自调用导致“绕过容器”或触发循环依赖**
  - 现象：final/private 方法无法被增强；互调时提前触发 `getBean`，可能让循环依赖更早暴露
  - 修复：避免在 `@Bean` 方法体内互调；用方法参数注入或拆分配置类

## 排障决策表（`@Configuration` 增强 / `proxyBeanMethods`）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `@Bean` 方法互调出现“额外实例”（对象不相等） | `proxyBeanMethods=false` 或 Lite 模式导致没有增强 | 观察配置类运行时 class 是否包含 `$$SpringCGLIB$$`；互调时调用栈是否进入 `BeanMethodInterceptor#intercept` | 避免 `@Bean` 方法互调；改用方法参数注入；必要时显式 `@Configuration(proxyBeanMethods=true)` | `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance` |
| 明明写了 `@Bean`，但行为像普通组件（互调不走容器） | Lite 模式（`@Component + @Bean`）默认不增强 | 断点 `ConfigurationClassPostProcessor#processConfigBeanDefinitions` 看 Full/Lite 判定；运行时 class 不增强 | 视需求改成 Full `@Configuration`；或同样避免互调 | `SpringCoreBeansContainerLabTest#liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance` |
| 你以为“这是 scope 问题”但其实不是 | 混淆了“bean 是否单例”与“方法调用是否走容器” | 对照：容器 `getBean` 仍返回同一个 singleton；互调返回的是方法体 new | 把依赖解析交回容器（参数注入），不要在方法里 new | 同上两条对照用例 |

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](017-06-post-processors.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. FactoryBean：product vs factory（& 前缀）](08-factorybean.md)

<!-- BOOKIFY:END -->

## 一句话自检

你应该能回答：

1) `@Configuration` 的增强解决了什么问题？（提示：@Bean 方法调用语义）
2) `proxyBeanMethods` 为 true/false 时，行为差异在哪里体现？
3) 你如何在调试器里证明“同一个 @Bean 方法多次调用是否返回同一对象”？

# 06. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。

    本章围绕 `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansContainerLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](05-post-processors.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 调试与自检：如何“观察到”容器正在做什么](../part-02-boot-autoconfig/01-debugging-and-observability.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansContainerLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（Java Config / @Bean，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/java.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansContainerLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansContainerLabTest`，再用 `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalse_stillPreservesSingleton_whenUsingMethodParameterInjection` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ConfigurationClassPostProcessor#processConfigBeanDefinitions` / `ConfigurationClassEnhancer#enhance`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，Java Config / @Configuration/@Bean 语义）：https://docs.spring.io/spring-framework/reference/core/beans/java.html

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
- 当调用 `@Bean` 方法时，会被拦截并重定向到容器
- 因此即便在 `@Bean` 方法里直接调用另一个 `@Bean` 方法，也能维持“单例语义”

`@Configuration(proxyBeanMethods = false)`（Lite 模式 / 更偏性能）：

- Spring 不会为“方法调用语义”提供额外保障
- 在方法体里直接调用另一个 `@Bean` 方法，就是一次普通 Java 方法调用
- 这可能会产生额外实例（绕过容器缓存）

### 1.1 机制系统阐述：条件 → 分支 → 结果（可断点验证）

**条件**：配置类是 Full（`@Configuration`）还是 Lite（`@Component + @Bean`），以及 `proxyBeanMethods` 取值
**分支**：`ConfigurationClassPostProcessor#processConfigBeanDefinitions` 标记 Full/Lite
**结果**：
- Full + proxy=true：`ConfigurationClassEnhancer` 介入，`@Bean` 方法互调走容器
- Lite 或 proxy=false：互调为普通 Java 调用，可能产生额外对象
**断点建议**：`ConfigurationClassPostProcessor#processConfigBeanDefinitions` / `ConfigurationClassEnhancer#enhance`

建议将相关“现象”固化为可断言的验证闭环，而不宜仅凭日志推断：

### 2.1 读者到底在对比什么？

两种模式都能把 `@Bean` 注册进容器；差异在于：**配置类自身是否会被增强（enhance）**，从而拦截 `@Bean` 方法调用。

- `@Configuration(proxyBeanMethods=true)`（默认）
  - 配置类会被 CGLIB 增强（读者常会在类名里看到 `$$SpringCGLIB$$`）。
  - 在同一个配置类里，`@Bean` 方法互相调用时，会被拦截并改成 **从容器取 bean**。
  - 结果：在 `@Bean` 方法里调用另一个 `@Bean` 方法，仍能保持 singleton 语义（同一个实例）。
- `@Configuration(proxyBeanMethods=false)`
  - 配置类不会拦截 `@Bean` 方法调用。
  - 在配置类内部互相调用 `@Bean` 方法，本质就是 **普通 Java 方法调用**。
  - 结果：读者可能 new 出“额外对象”，即使容器里的对应 bean 依然是 singleton。

> 关键点：`proxyBeanMethods=false` 不是“Bean 变多例”，而是“在配置类里手写的互调绕过了容器语义”。

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls()`（proxy=true：互调会走容器）
  - `configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance()`（proxy=false：互调是普通 Java 调用）
  - `liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance()`（Lite 模式：`@Component + @Bean` 不会增强）

可以观察到：

- proxy=true：`configB()` 内调用 `configA()`，获取到的是容器里的同一个 `ConfigA`
- proxy=false：方法体直接调用导致 new 出另一个 `ConfigA`

## 3. 最推荐的写法：用“方法参数”声明依赖

若编写：

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

### 3.1 为何该方式更为稳健：方法参数注入点即 `MethodParameter`（无需依赖“互相调用”亦可获取依赖）

把依赖写在 `@Bean` 方法参数上，最大的收益不是“看起来更优雅”，而是它天然满足两个可验证的事实：

1. **依赖解析发生在容器创建阶段（工厂方法参数解析）**
   Spring 在调用 `@Bean` 工厂方法时，会把参数当作注入点来解析；该注入点在内部即 `org.springframework.core.MethodParameter`（可在断点中直接观察到）。
2. **它不依赖配置类是否被 CGLIB 增强**
   即便 `proxyBeanMethods=false`（不做配置类增强），工厂方法仍然由容器调用，参数解析仍会走标准的依赖解析链路；因此这条写法对“性能/语义/可测试性”的折中更可控。

> 对照理解：问题往往出在“`@Bean` 方法里互相调用另一个 `@Bean` 方法”。这种写法在 `proxyBeanMethods=false` 时会退化成普通方法调用，绕开容器，自然也绕开了依赖解析/代理/生命周期等一整套机制。

**建议的断点验证：**

- `ConstructorResolver#resolveAutowiredArgument(...)`（参数解析入口）
- 观察 `MethodParameter` / `DependencyDescriptor` 是如何被构造出来的

### 4.1 推荐观察点（watch list）

- 配置类 bean 的运行时 class：
  - proxy=true：类名通常包含 `$$SpringCGLIB$$`（说明发生了 CGLIB 增强）
  - proxy=false / Lite：通常就是原始类（没有增强）
- `@Bean` 方法互调发生时：
  - 看调用栈是否进入 `ConfigurationClassEnhancer.BeanMethodInterceptor#intercept`
  - 看 `bean` / `beanName`：最终返回的是“容器里的单例”还是“方法体 new 出来的对象”
- （对照）容器里同名 bean 的获取路径：
  - `AbstractBeanFactory#doGetBean`：证明“从容器获取到的那个对象”与“方法互调返回的对象”是否一致

## 5. 应能够回答的 2 个问题

1) `proxyBeanMethods` 影响的到底是什么？（提示：不是“这个 bean 是否是单例”，而是“配置类里方法调用会不会走容器”）
2) 为什么在大规模应用里，经常把 `proxyBeanMethods` 设为 false？

## 面试常问（`@Configuration(proxyBeanMethods=...)` 的语义）

- 常问：`proxyBeanMethods=true/false` 有什么差异？为什么 `false` 可能出现“额外实例”？
  - 答题要点：`true` 时配置类被增强，`@Bean` 方法互调会被拦截并走容器缓存，保持单例语义；`false` 时互调是普通 Java 调用，可能 new 出额外对象。
- 常见追问：在工程里如何避免误用？
  - 答题要点：避免在 `@Bean` 方法体内直接调用另一个 `@Bean` 方法；优先使用方法参数注入或构造注入，让依赖解析回到容器。

## 可复现闭环（基于 `SpringCoreBeansContainerLabTest`）

至少得到 3 条可断言结论：

1) **proxy=true 时，`@Bean` 方法互调会回到容器**
   - 断点：`BeanMethodInterceptor#intercept`
   - 断言：互调返回同一实例
2) **proxy=false/Lite 时，互调是普通 Java 调用**
   - 断点：互调调用栈不进入 `intercept`
   - 断言：方法体内 new 出额外实例
3) **参数注入是最稳妥写法**
   - 断点：`doResolveDependency`（方法参数注入点是 `MethodParameter`）
   - 断言：即使 `proxyBeanMethods=false` 或 Lite 配置类不增强，**方法参数注入仍能保持容器语义（singleton 仍是同一实例）**
   - 可运行入口：
     - `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalse_stillPreservesSingleton_whenUsingMethodParameterInjection`
     - `SpringCoreBeansContainerLabTest#liteConfiguration_stillPreservesSingleton_whenUsingMethodParameterInjection`
   - 关联章节：依赖解析的“候选收敛/注入点元数据证据链”见 [03](02-dependency-injection-resolution.md)

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansContainerLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

## 常见误区与边界

- **误区 1：`proxyBeanMethods=false` + `@Bean` 方法互调**
  - 现象：容器里 bean 依然是单例，但在配置类内部互调会 new 出“额外对象”
  - 证据链：`SpringCoreBeansContainerLabTest.configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance()`
  - 修复：避免互调；改成“方法参数注入”（让依赖解析回到容器）
- **误区 2：Lite 模式（`@Component + @Bean`）没有增强**
  - 现象：容易误以为“写了 @Bean 就等于 @Configuration”，结果互调语义与 proxy=false 一样（不会拦截）
  - 证据链：`SpringCoreBeansContainerLabTest.liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance()`
  - 修复：把配置类显式改成 `@Configuration`（并明确 `proxyBeanMethods`）；或者同样避免互调
- **误区 3：误把它当成“scope 语义变化”**
  - 澄清：bean 还是单例；变的是“在配置类里写的 Java 调用有没有被容器拦截并重定向”
  - 经验法则：只要读者看到“配置类内部互调 @Bean 方法”，就默认它是风险点，优先改成“参数注入”
- **误区 4：跨配置类/自调用导致“绕过容器”或触发循环依赖**
  - 现象：final/private 方法无法被增强；互调时提前触发 `getBean`，可能让循环依赖更早暴露
  - 修复：避免在 `@Bean` 方法体内互调；用方法参数注入或拆分配置类

## 排障决策表（`@Configuration` 增强 / `proxyBeanMethods`）
> 官方参考（Spring Framework 6.2.x，Java Config / @Configuration/@Bean 语义）：https://docs.spring.io/spring-framework/reference/core/beans/java.html


| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| `@Bean` 方法互调出现“额外实例”（对象不相等） | `proxyBeanMethods=false` 或 Lite 模式导致没有增强 | 观察配置类运行时 class 是否包含 `$$SpringCGLIB$$`；互调时调用栈是否进入 `BeanMethodInterceptor#intercept` | 避免 `@Bean` 方法互调；改用方法参数注入；必要时显式 `@Configuration(proxyBeanMethods=true)` | `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance` |
| 明明写了 `@Bean`，但行为像普通组件（互调不走容器） | Lite 模式（`@Component + @Bean`）默认不增强 | 断点 `ConfigurationClassPostProcessor#processConfigBeanDefinitions` 看 Full/Lite 判定；运行时 class 不增强 | 视需求改成 Full `@Configuration`；或同样避免互调 | `SpringCoreBeansContainerLabTest#liteConfiguration_componentWithBeanMethods_doesNotEnhance_beanMethodInterCallsCreateExtraInstance` |
| 容易误以为“这是 scope 问题”，但并非 scope 问题 | 混淆了“bean 是否单例”与“方法调用是否走容器” | 对照：容器 `getBean` 仍返回同一个 singleton；互调返回的是方法体 new | 把依赖解析交回容器（参数注入），不要在方法里 new | 同上两条对照用例 |

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](05-post-processors.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. FactoryBean：product vs factory（& 前缀）](07-factorybean.md)

<!-- BOOKIFY:END -->

## 自检要点
应能够回答：

1) `@Configuration` 的增强解决了什么问题？（提示：@Bean 方法调用语义）
2) `proxyBeanMethods` 为 true/false 时，行为差异在哪里体现？
3) 如何在调试器里证明“同一个 @Bean 方法多次调用是否返回同一对象”？

# 第 20 章：01. Bean 心智模型：从 BeanDefinition 到最终暴露对象
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Bean 心智模型：从 BeanDefinition 到最终暴露对象
    - 怎么使用：建议先跑本章推荐 Lab，把“定义不等于实例、最终不一定等于原始实例”固化为断言；再回到正文用主线与断点把证据链走通。
    - 原理：`ApplicationContext#refresh` 主线：注册定义（BeanDefinition）→ 定义层处理（BFPP/BDRPP）→ 注册 BPP 链 → 创建/注入/初始化（doCreateBean）→ 最终暴露对象（可能是 proxy）。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanCreationTraceLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 19 章：11. 调试与自检：如何“看见”容器正在做什么](../part-02-boot-autoconfig/019-11-debugging-and-observability.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**Bean 心智模型：从 BeanDefinition 到最终暴露对象**
- 这章解决的不是“记名词”，而是解决一个高频误判：把 **Bean = 某个对象实例**。
  正确心智模型应该是：**Bean = 容器托管的一套机制**（定义、创建、注入、回调、代理、销毁）。

!!! summary "本章要点"

    - 注册阶段的第一性对象是 `BeanDefinition`，不是实例。
    - 创建阶段的主线是 `doCreateBean`：实例化 → 注入（populate）→ 初始化（initialize）→ 产出最终暴露对象。
    - `getBean()` 拿到的是“最终暴露对象”，它可能不是编写的那个类的原始实例（可能是 proxy/wrapper）。

!!! example "本章配套实验（先跑再读）"

    - Lab：
      - `SpringCoreBeansContainerLabTest`
      - `SpringCoreBeansBeanCreationTraceLabTest`
      - `SpringCoreBeansProxyingPhaseLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

## 机制主线：三层模型 + 一个“最终对象”概念

把容器理解成三层（这是读者后面所有排障的基础）：

1) **输入层（inputs）**：注解、`@Bean`、`@Import`、XML、programmatic 注册……
2) **定义层（definitions）**：解析输入并注册 `BeanDefinition`（“怎么造”的配方与元数据）
3) **实例层（instances）**：按定义创建对象、注入依赖、执行回调、注册销毁钩子

在三层之上，再补一个“最终对象”概念：

4) **最终暴露对象（exposed object）**：容器对外返回的对象（`getBean()`/注入点拿到的对象），它可能在多个阶段被替换/包装成 proxy。

> 读者真正要背进肌肉记忆的一句话：
> **定义层回答“有没有/谁注册的/配方是什么”，实例层回答“什么时候创建/注入选了谁/最终是不是 proxy”。**

### 机制讲透：三层模型 + 最终对象（条件 → 分支 → 结果）

**条件**：bean 是否走完整 `doCreateBean`，以及是否被 BPP/early reference 替换  
**分支**：`applyBeanPostProcessorsAfterInitialization` / `getEarlyBeanReference`  
**结果**：  
- 走完整创建链 → 有机会被 after-init BPP 替换成 proxy  
- 进入 early reference → 最终对象可能不是 raw instance  
**断点建议**：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

## 1. 四类对象一张表：读者到底在看什么？

| 在调试器里看到的对象 | 它代表什么 | 最直接 API/入口 | 读者通常用它回答什么问题 |
| --- | --- | --- | --- |
| `BeanDefinition` | 原始定义（配方） | `BeanFactory#getBeanDefinition(beanName)` | “到底有没有注册？谁注册的？scope/lazy/dependsOn 是什么？” |
| merged `RootBeanDefinition` | 最终生效配方 | `AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)` | “为什么最终是 Root？parent 合并后哪些元数据生效？” |
| raw instance（原始实例） | 刚创建出来的对象 | `doCreateBean` 内部的 `bean`/`bw.getWrappedInstance()` | “构造器/工厂方法到底有没有执行？注入发生了吗？” |
| exposed object（最终暴露对象） | 容器对外的最终返回 | `initializeBean` 之后的返回值 / `getBean()` 的结果 | “为什么我拿到的是 proxy？是谁把它换掉的？” |

## 2. 方法级主线：refresh → doCreateBean → 最终暴露对象

> 目标：无需背全流程，但要能说出“关键窗口在哪、证据链在哪”。

refresh 的骨架（只保留与本章相关的关键节点）：

1) `AbstractApplicationContext#refresh`
2) `invokeBeanFactoryPostProcessors`（定义层处理：BDRPP/BFPP 改定义/加定义）
3) `registerBeanPostProcessors`（把实例层拦截链装好：注解注入/AOP/回调都依赖它）
4) `finishBeanFactoryInitialization` → `preInstantiateSingletons`（批量创建非 lazy 单例）

### 2.1 关键分支解释（围绕 refresh 的 if/then）

- **是否预实例化**：`mbd.isLazyInit()` 决定是否在 `preInstantiateSingletons` 被创建  
- **是否走 BPP 链**：BPP 注册发生在 `registerBeanPostProcessors`，过早创建会错过  
- **是否进入 early reference**：循环依赖窗口期决定最终暴露对象形态  
- **是否为 FactoryBean**：`getObjectForBeanInstance` 决定拿到的是工厂还是产品

单个 bean 的创建主线（方法级锚点）：

1) `AbstractBeanFactory#doGetBean(beanName)`
2) `AbstractAutowireCapableBeanFactory#doCreateBean(beanName, mbd, args)`
3) `populateBean(beanName, mbd, bw)`（注入发生点：依赖解析、属性填充、类型转换）
4) `initializeBean(beanName, bean, mbd)`（回调与 BPP：Aware → before-init BPP → init callbacks → after-init BPP）
5) 返回 exposed object（可能是 proxy）

## 可复现闭环（基于 `SpringCoreBeansBeanCreationTraceLabTest`）

跑完该 Lab，至少应能够复述 3 条结论：

1) **raw instance 与最终暴露对象可能不同**  
   - 断点：`applyBeanPostProcessorsAfterInitialization`  
   - 断言：`result != bean`
2) **注入发生在 populateBean 阶段**  
   - 断点：`populateBean`  
   - 断言：属性填充发生在初始化之前
3) **最终暴露对象在 initializeBean 之后确定**  
   - 断点：`initializeBean`  
   - 断言：`getBean()` 拿到的是 initialize 之后的返回值

## 3. 三个“最终对象被替换”的高频入口

把“我怎么拿到 proxy 的”拆成三类（记住它们存在即可）：

1) **实例化前短路（pre）**：`resolveBeforeInstantiation`
   - 典型：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 返回替身对象（见 [15](../part-03-container-internals/15-pre-instantiation-short-circuit.md)）
2) **循环依赖窗口期（early）**：`getEarlyBeanReference`
   - 典型：early 引用与最终暴露形态一致性问题（见 [16](../part-03-container-internals/16-early-reference-and-circular.md)）
3) **初始化后替换（after-init）**：`postProcessAfterInitialization`
   - 典型：AOP/事务/懒代理等最常见 proxy 产生点（见 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）

## 4. 排障决策表（把“我感觉”变成“我能证明”）

| 现象 | 先分层到哪里 | 证据（断点/观察点） | 最可能根因 | 修复思路 |
| --- | --- | --- | --- | --- |
| `NoSuchBeanDefinitionException` | 定义层 | `containsBeanDefinition` / `getBeanDefinition` 是否存在 | 根本没注册；或条件未满足/被排除 | 回到注册入口与条件：见 [02](02-bean-registration.md)、[21](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md) |
| 注入报 `NoUniqueBeanDefinitionException` | 实例层（依赖解析） | `doResolveDependency`→`findAutowireCandidates`→`determineAutowireCandidate` | 候选太多且没收敛信号 | 用 `@Qualifier/@Primary` 收敛；或让 auto-config back-off（见 [03](014-03-dependency-injection-resolution.md)、[33](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)） |
| 容易误以为拿到原对象但行为像 proxy | 实例层（最终暴露对象） | `applyBeanPostProcessorsAfterInitialization` 里 `result != bean` | after-init BPP 替换了对象 | 追到具体 BPP，再回看其注册顺序与触发条件（见 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)） |
| “明明写了 @Bean，但容器里没有” | 定义层（注解基础设施） | `ConfigurationClassPostProcessor` 是否存在并执行 | 没装 annotation processors / 配置类没被解析 | 先把注解基础设施装起来（见 [22](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)） |

## 5. 面试常问（标准答案 + 方法级证据链）

### Q1：BeanDefinition、bean instance、最终 `getBean()` 拿到的对象分别是什么？

- 标准答案（可复述）：
  - BeanDefinition 是配方；bean instance 是创建出来的原始对象；`getBean()` 返回最终暴露对象，可能被 short-circuit/early reference/after-init BPP 替换成 proxy/wrapper。
- 证据链（方法级）：
  - 定义层：`registerBeanDefinition`
  - 创建层：`doCreateBean` / `populateBean` / `initializeBean`
  - 最终暴露：`applyBeanPostProcessorsAfterInitialization` / `getEarlyBeanReference`
- 最小复现：
  - `SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance`
  - `SpringCoreBeansProxyingPhaseLabTest`

### Q2：为什么说“先看定义层，再看实例层”能显著提升排障效率？

- 标准答案（可复述）：
  - 定义层回答“有没有/谁注册的/配方是什么”，实例层回答“何时创建/注入选了谁/是否被代理替换”；把问题分层后，断点入口和观察点会立刻收敛，避免在巨大调用栈里盲追。

### Q3：BeanFactory vs ApplicationContext 的核心差别是什么？

- 标准答案（可复述）：
  - BeanFactory 是底层容器（创建/注入/生命周期骨架）；ApplicationContext 在其之上增加应用级设施（事件、多语言、资源、环境）并在 `refresh` 中把这些设施接入主线。
- 最小复现：
  - `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`

## 6. 自检要点

应能够用 3 句说明白：

1) 为什么说“注册 bean”注册的第一性对象是 BeanDefinition？
2) 为什么 `getBean()` 拿到的不一定是原始实例？（在哪 3 个阶段可能被替换）
3) 看到一个异常时，如何先分层到定义层/实例层，并给出第一个断点入口？

## 小结与下一章

- 本章把 Bean 的最小心智模型固定成“四个对象”：BeanDefinition / merged RootBeanDefinition / raw instance / exposed object。
- 下一章开始进入 Boot 的自动装配：可以观察到“定义层”的复杂度显著上升，但排障方法论不变（先分层，再证据链）。
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：把 pre/early/after-init 三个替换窗口做成“证据链对照表”，并给每类窗口的关键入口方法。
    - B（边界反例）：反例：early reference 与最终代理不一致导致的行为差异；FactoryBean 造成的“看起来类型不对”。
    - C（排障 SOP）：排障：看到异常先分层到定义/实例/最终对象，并给第一断点入口。
    - D（断点观察）：“如何快速识别 proxy/wrapper”：调试器判别方法与代理链定位。
    - E（面试复述）：面试追问：BeanFactory vs ApplicationContext 的差异如何落到 refresh 证据链。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[11. 调试与自检：如何“看见”容器正在做什么](../part-02-boot-autoconfig/019-11-debugging-and-observability.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)

<!-- BOOKIFY:END -->

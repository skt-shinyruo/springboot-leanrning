# 第 36 章：07. AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor / Advice / Pointcut 三层模型）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor / Advice / Pointcut 三层模型）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 35 章：06. Debug / 观察：如何“看见”代理与切点](../part-01-proxy-fundamentals/035-06-debugging.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 37 章：08. Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）](037-08-pointcut-expression-system.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**07. AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor / Advice / Pointcut 三层模型）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`

## 机制主线

你会看到三件事如何串起来：

---

## 1. 把 AOP 放回 IoC：AutoProxyCreator 是典型 BPP

### 1.1 它在哪里被注册？

当你写下 `@EnableAspectJAutoProxy`（或 Spring Boot AOP 自动装配开启相关能力）时，本质发生的是：

- 容器在“注册阶段”把一个内部基础设施 bean（AutoProxyCreator）注册进 BeanFactory
- 在 `refresh` 流程里，Spring 会把它加入到 `beanFactory.getBeanPostProcessors()` 列表
- 随后创建普通 bean 时，它会在 BPP 回调里决定“要不要把这个 bean 换成 proxy”

你应该能在源码里复述的最短主线（容器视角）：

> 这条主线与 `spring-core-beans` 的“BPP 替换阶段”完全一致，只是这里“替身对象”是 AOP proxy。

### 1.2 它为什么是 `SmartInstantiationAwareBeanPostProcessor`？

AutoProxyCreator 之所以“强”，不是因为它“会代理”，而是因为它能在多个关键时机介入：

- **pre-instantiation**：实例化前短路（某些场景直接返回 proxy）
  - `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
- **early reference**：循环依赖注入时的“提前暴露引用”（可能是 early proxy）
  - `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
- **after-init**：最常见的最终替换（final proxy）
  - `BeanPostProcessor#postProcessAfterInitialization`

> 对应 beans 深挖章节：
> - `docs/beans/spring-core-beans/part-03-container-internals/15-pre-instantiation-short-circuit.md`
> - `docs/beans/spring-core-beans/part-03-container-internals/16-early-reference-and-circular.md`
> - `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`

---

## 2. 三层模型：Advisor / Pointcut / Advice

### 2.1 你需要的最低精度定义

先用“够用版”定义稳住心智模型：

- **Advice**：要织入的横切逻辑（比如 around/before/after），最终会被适配成拦截器（`MethodInterceptor`）。
- **Pointcut**：选择规则，决定哪些 joinpoint（哪些方法调用）会被 advice 包住。
- **Advisor**：组合体（Pointcut + Advice），AutoProxyCreator 以 Advisor 为粒度做“是否增强”的决策与装配。

> 关键点：**AOP 的决策粒度不是“Aspect 类”，而更接近“Advisor 列表”**。

### 2.2 `@Aspect` 最终也会变成 Advisor

当你写一个 `@Aspect`：

- 每个 `@Around/@Before/...` 方法都会被解析成一条 Advisor（背后有 pointcut 与 advice）
- 解析后的结果会进入“候选 Advisors 池”
- AutoProxyCreator 再把这些候选 Advisors 按规则筛选，挂到“需要增强的 bean”上

- `List<Advisor>`（候选 / 适用 / 最终挂载）
- 以及这些 Advisor 的 `getPointcut()` 与 `getAdvice()`

---

## 3. AutoProxyCreator 的决策管线（你要能复述）

当容器创建一个 bean 并走到 BPP 阶段，AutoProxyCreator 的核心问题只有一个：

> **这个 bean 是否需要被代理？如果要，挂哪些 Advisors？**

1. **跳过基础设施 bean**
   - AOP/容器内部的基础设施类通常不应被代理（否则会自我增强、风险很高）
2. **拿到候选 Advisors**
   - 来源包括：`@Aspect` 解析出来的 Advisors、以及你显式声明的 `Advisor` beans（Tx/Cache/Security 本质也在这里）
3. **筛选“对当前 bean 适用”的 Advisors**
   - 核心是判断 pointcut 是否对目标类/方法可应用（否则不挂）
4. **如果有适用 Advisors：创建 proxy**
   - 构造 `ProxyFactory`，设置 target、添加 advisors、选择 proxy 类型（JDK/CGLIB）
5. **返回 proxy 作为最终暴露的 bean**
   - 之后容器对外暴露的就是 proxy，而不是原始实例

这条管线的价值是：它能让你把真实项目里的 AOP 问题稳定分流：

---

### 4.1 注册阶段（确认它确实作为 BPP 存在）

- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
  - 观察：`beanFactory.getBeanPostProcessors()` 最终列表里是否出现 AutoProxyCreator

### 4.2 proxy 产生阶段（容器阶段）

- `AbstractAutoProxyCreator#postProcessAfterInitialization`
- `AbstractAutoProxyCreator#wrapIfNecessary`
- `AbstractAutoProxyCreator#createProxy`
- `DefaultAopProxyFactory#createAopProxy`（JDK vs CGLIB 选择点，配合 [02. jdk-vs-cglib](../part-01-proxy-fundamentals/031-02-jdk-vs-cglib.md)）

### 4.3 advisor 选择阶段（把“三层模型”看清楚）

- `AbstractAdvisorAutoProxyCreator#findEligibleAdvisors`（如果你需要更细）
- `AopUtils#canApply`（pointcut 适用性判断的常见落点）

### 4.4 推荐观察点（watch list）

- `beanName`
- `bean.getClass()` vs `AopUtils.getTargetClass(bean)` vs `AopProxyUtils.ultimateTargetClass(bean)`
- `advisors` / `specificInterceptors`（数量与来源）
- `proxyFactory`：interfaces、proxyTargetClass、targetSource

---

- AutoProxyCreator 在什么时候被注册进 `BeanFactory`？
- 它在创建哪个 bean 时决定“要代理/不代理”？
- `Advised#getAdvisors()` 里有哪些 Advisor？每个 Advisor 的 pointcut 与 advice 分别是什么？

---

## 6. 常见误判（与排障分流）

### 6.1 “我加了 @Aspect，为什么这个 bean 没被代理？”

先按顺序排查：

1. **调用是否走 proxy？**（见 [01. aop-proxy-mental-model](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)：call path）
2. **候选 Advisors 是否存在？**（看看容器里是否真的注册了 advisor）
3. **pointcut 是否对目标类/方法适用？**（见 [08. pointcut-expression-system](037-08-pointcut-expression-system.md)：pointcut 系统）
4. **是否踩中代理限制？**（[02. jdk-vs-cglib](../part-01-proxy-fundamentals/031-02-jdk-vs-cglib.md)、[04. final-and-proxy-limits](../part-01-proxy-fundamentals/033-04-final-and-proxy-limits.md)：JDK/CGLIB、final/self-invocation）

### 6.2 “明明代理了，但顺序不对/效果怪”

你需要区分两类顺序（不要混在一起）：

- **BPP 顺序**：影响“是否出现多层代理、谁先包谁后包”（容器阶段）
- **Advisor 顺序**：影响“拦截器链谁在外层、谁先执行”（调用阶段）

多代理叠加与顺序的完整解释见：[09 - 多代理叠加与顺序](../part-03-proxy-stacking/038-09-multi-proxy-stacking.md)。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
- 建议命令：`mvn -pl :spring-core-aop test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

这一章的目标是把 Spring AOP 从“会写 @Aspect”提升到“能在源码断点里复述主线”。

1. **AutoProxyCreator 不是魔法**：它就是一个 `BeanPostProcessor`（更准确地说：`SmartInstantiationAwareBeanPostProcessor`）。
2. **Advisor / Advice / Pointcut 是三层模型**：Advisor=（Pointcut + Advice），最后会被组装为拦截器链。
3. **proxy 的产生时机是容器阶段**：proxy 是 bean 创建流程中的“替身对象”，不是运行时“动态改类”。

> 推荐配套 Labs：`SpringCoreAopAutoProxyCreatorInternalsLabTest`（可断点闭环）。

这就是为什么你在源码断点里应该关注的对象是：

> Labs 里我们也会给出“非 @Aspect 的写法”：手工声明 `Advisor` bean，让你更直观看到三层模型。

你不需要记住每一个方法名，但你需要能在断点里看懂“决策步骤”：

## 4. 断点清单：主线够用版（建议跟着 Labs 跑）

这类断点的命中会比较频繁，建议加条件（只看目标 beanName）。

## 5. Labs：把主线做成“可断言”的闭环

建议从这个 Lab 开始跑（只跑方法更适合打断点）：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyCreatorInternalsLabTest test
```

你应该能在断点里回答：

## 常见坑与边界

这也是你在真实项目里遇到循环依赖/代理边界时必须具备的“容器视角解释能力”。

- 没走代理（call path 问题）
- 没命中 pointcut（匹配问题）
- 被跳过/被排除/不满足可代理条件（边界问题）

## 小结与下一章

- `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
    - **AutoProxyCreator 被创建并注册为 BPP**
  - 创建普通 bean：
    - `AbstractAutowireCapableBeanFactory#doCreateBean`
      - `initializeBean`
        - `applyBeanPostProcessorsAfterInitialization`
          - `AbstractAutoProxyCreator#postProcessAfterInitialization`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`

上一章：[06-debugging](../part-01-proxy-fundamentals/035-06-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08-pointcut-expression-system](037-08-pointcut-expression-system.md)

<!-- BOOKIFY:END -->

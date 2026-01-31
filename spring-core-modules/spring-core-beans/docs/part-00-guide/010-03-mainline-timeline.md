# 第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：IoC 容器从 refresh 到创建 Bean
    - 使用方式：可先运行本章推荐 Lab，把主线/断点闭环完成验证，再回到正文按“时间线/分支矩阵/证据链”定位机制窗口；最后用自检题把表达固化成可复述答案。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`AbstractApplicationContext#refresh` / `AbstractApplicationContext#prepareBeanFactory` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
    - 推荐 Lab：`SpringCoreBeansMainlineCallChainLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 9 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 13 章：01. `ApplicationContext#refresh` 调用链（主线）](013-01-applicationcontext-refresh-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**主线时间线：IoC 容器从 refresh 到创建 Bean**
- 阅读方式建议：这章不是“讲知识点”，而是给读者一张时间线地图。读者先运行一个主线 Lab，把 refresh 走一遍；然后拿这张时间线去定位每个现象属于哪个阶段。

!!! summary "本章要点"

    - 读者只要记住一件事：**99% 的排障都能被归到 refresh 的某一段**（定义层/实例层/初始化/完成后回调）。
    - BFPP/BDRPP（定义层）与 BPP（实例层）是两个世界：先改“定义”，再造“实例”；顺序错了，后果往往是“代理/注入/回调不生效”。
    - 无需背完整 refresh 步骤，但必须能说清：BPP 什么时候注册？单例什么时候创建？循环依赖窗口在哪里？

!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBreakpointPackLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansMainlineCallChainLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansBreakpointPackLabTest.java`

## 机制主线：把所有章节放回同一条时间线

当读者学习 Spring IoC 时，最容易迷失的不是“方法太多”，而是：

- 读者不知道某个机制发生在 refresh 的哪一步
- 读者不知道“修改定义/添加处理器/触发 getBean”会影响哪一段

因此先用一张时间线，把 IoC 的主线粗粒度切成几段（每段对应一类问题/一类断点入口）。

---

## 1. refresh 主线时间线（粗粒度分段）

> 目标：遇到任何现象，先回答：它属于哪一段？

### 1.1 段 A：准备阶段（容器骨架搭好，但还没处理相应的 bean）

关键点：

- Environment/PropertySources 基本就位
- BeanFactory 创建/替换/准备完成（后续所有定义与实例都围绕它发生）

典型断点：

- `AbstractApplicationContext#refresh`（总入口）
- `AbstractApplicationContext#prepareBeanFactory`

### 1.2 段 B：定义层（Definition Phase：BFPP/BDRPP）

关键点：

- 注册/解析 BeanDefinition（包括扫描/导入/XML/Reader）
- BFPP/BDRPP 可以批量改写 BeanDefinition

典型断点：

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`
- `BeanFactoryPostProcessor#postProcessBeanFactory`

对应章节：

- `part-03-container-internals/13-bdrpp-definition-registration.md`
- `part-01-ioc-container/02-bean-registration.md`

### 1.3 段 C：实例层准备（注册 BPP 链）

关键点：

- 把所有 BeanPostProcessor 实例创建出来并按规则注册进链
- AOP/注解注入/生命周期回调等“能力”都依赖这条链

典型断点：

- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `DefaultListableBeanFactory#addBeanPostProcessor`

对应章节：

- `part-01-ioc-container/017-06-post-processors.md`
- `part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- `appendix/98-debugger-pack.md`

### 1.4 段 D：创建单例（实例化 → 注入 → 初始化 → 入缓存）

关键点：

- `finishBeanFactoryInitialization` 会触发单例预实例化与创建
- `doCreateBean` 中存在循环依赖窗口（early exposure）

典型断点：

- `AbstractApplicationContext#finishBeanFactoryInitialization`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `DefaultSingletonBeanRegistry#getSingleton`

对应章节：

- `part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- `part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- `part-01-ioc-container/09-circular-dependencies.md`
- `part-03-container-internals/16-early-reference-and-circular.md`

### 1.5 段 E：完成与后置回调（容器就绪）

关键点：

- 容器完成 refresh，发布事件，执行“容器就绪”类回调（例如 SmartInitializingSingleton）

典型断点：

- `AbstractApplicationContext#finishRefresh`
- `SmartInitializingSingleton#afterSingletonsInstantiated`

对应章节：

- `part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`

### 1.6 段内关键对象变化（在 debugger 里应该观察到什么）

这一小节只做一件事：把“阶段”变成“可观察对象”。

无需记住全部字段，但应能够在断点里回答：**当前处于哪个阶段？该阶段改变了什么？**

| 段 | 在断点里看什么 | 关键对象/变量（建议优先） | 可以得到的判断 |
| --- | --- | --- | --- |
| A 准备 | 容器是否已经具备“解析属性/注入容器对象”的基础能力 | `AbstractApplicationContext#prepareBeanFactory` 内：`beanFactory.resolvableDependencies`、embedded value resolvers、`beanFactory.getBeanClassLoader()` | 还没处理相应的 bean，但容器的“基础设施”已就绪（后续注解能否工作取决于下一段） |
| B 定义层 | BeanDefinition 是否已经齐全、是否被改写过 | `beanFactory.getBeanDefinitionCount()`、`getBeanDefinitionNames()`、`BeanDefinition#getSource()`、`BeanDefinition#getRole()` | 问题属于“没注册/注册错/被覆盖/被改写”时，这一段就能定位根因 |
| C 注册 BPP | BPP 链是否完整、顺序是否符合预期 | `beanFactory.getBeanPostProcessors()`（数量/类型/顺序）、关键处理器是否存在（注入/AOP/JSR-250） | “注解/AOP/回调不生效”的高频根因：BPP 没注册、注册晚了、顺序错了 |
| D 创建单例 | 单例缓存是否进入“创建窗口期”，是否出现 early reference | `singletonObjects/earlySingletonObjects/singletonFactories`、`singletonsCurrentlyInCreation`、`mbd`、`pvs` | 绝大多数运行期问题都在这里落地：注入、类型转换、代理替换、循环依赖边界 |
| E 容器就绪 | “容器就绪后”回调是否触发、事件是否发布 | `finishRefresh`、`SmartInitializingSingleton#afterSingletonsInstantiated`、事件发布 | 适合放“容器一致性校验/外部资源健康检查/延迟启动”类逻辑；也能解释“为什么某些逻辑必须等到这里” |

> 提醒：若在 D 段看到目标 bean 已经创建，但 C 段的关键 BPP 还没注册完成，那几乎必然是“创建过早/时机错误”。

---

## 2. 这条时间线使用方式来排障（3 个经典分流）

1) **注入失败（NoSuchBeanDefinition / NoUniqueBeanDefinition）**
   - 优先看段 D：`doResolveDependency/findAutowireCandidates/determineAutowireCandidate`
2) **代理/增强不生效**
   - 先分清是段 C（BPP 没注册/顺序不对）还是段 D（bean 创建过早错过 BPP）
3) **循环依赖/提前引用相关异常**
   - 段 D：`getSingleton` 的三层缓存分支 + `doCreateBean` 的 early exposure 窗口

---

## 面试常问（refresh 时间线）

1) **refresh 的关键阶段如何讲（且能落到方法名）？**
   - 要点：prepare → 定义层（BFPP/BDRPP）→ 注册 BPP 链 → 创建单例（doCreateBean）→ finishRefresh 回调。
   - 证据链：`AbstractApplicationContext#refresh` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` / `PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `finishBeanFactoryInitialization`。

2) **为什么说“时机决定能力”（尤其是代理/注解/回调）？**
   - 要点：BPP 链注册完成前创建的 bean，可能错过后续 BPP（代理/注解注入），表现为“有时生效有时不生效”。
   - 证据链：对照 `registerBeanPostProcessors` 与目标 bean 的创建时机，结合条件断点过滤 beanName。

推荐复习入口：`appendix/93-interview-playbook.md`（Q1/Q5 等题型都以时间线为骨架）。

## 自检要点
应能够用 3 句复述：

1) BFPP/BDRPP 发生在 refresh 的哪一段？它改的是“定义”还是“实例”？
2) BPP 链是在什么时候注册的？为什么它决定了“注解/AOP/回调”是否生效？
3) 单例创建主线从哪开始（哪一步触发预实例化）？循环依赖窗口在 `doCreateBean` 的哪里？
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“主线关键窗口最短调用链”：每个阶段至少给出 1 个入口方法与 1 个必看对象快照（definitions / processors / singleton caches）。
    - B（边界反例）：“时间线误判反例”：例如把“创建顺序”与“注入选择”混为一谈；把 lazy-init 与 @Lazy 注入点混为一谈。
    - C（排障 SOP）：“从症状回放到时间线窗口”：给 5 个症状（注入失败/代理/循环依赖/占位符/FactoryBean）对应时间线分叉点。
    - D（断点观察）：“断点组 + watch list”：把时间线每一段映射到断点地图（在哪看处理器列表、在哪看缓存变化）。
    - E（面试复述）：“面试复述模板”：要求用“主线→分支→证据链”三句复述，并给出示范答案结构。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[第 9 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 13 章：01. `ApplicationContext#refresh` 调用链（主线）](013-01-applicationcontext-refresh-call-chain.md)

<!-- BOOKIFY:END -->

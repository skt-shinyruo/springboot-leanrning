# 主线时间线：IoC 容器从 refresh 到创建 Bean
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：可先运行章首 Lab，把主线/断点闭环完成验证，再回到正文按“时间线/分支矩阵/证据链”定位机制窗口；最后用自检题把表达固化成可复述答案。

    观察对象：主线时间线：IoC 容器从 refresh 到创建 Bean。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansMainlineCallChainLabTest`。需要下探源码时，可以从 `AbstractApplicationContext#refresh` / `AbstractApplicationContext#prepareBeanFactory` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：用时间线定位阶段

本页是一张 `refresh()` 时间线。读者不需要一开始就下钻所有方法，而是先把一次启动过程分成准备、定义处理、BPP 注册、单例创建和容器就绪几段，再把具体异常或现象放回其中一段。

运行 `SpringCoreBeansMainlineCallChainLabTest` 后，再对照本页设置断点。每一次观察都应回答同一个问题：当前处于哪个阶段，这一阶段正在改变哪类对象。

## `refresh()` 时间线：定义、处理器、单例创建

本章是一张时间线地图：读者先运行一个主线 Lab，把 `refresh()` 走一遍；再用本页把每个现象定位到“它发生在哪一段、第一断点该下在哪里”。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBreakpointPackLabTest`
    - 测试文件：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansMainlineCallChainLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansBreakpointPackLabTest.java`

## 机制主线：把所有章节放回同一条时间线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

当读者学习 Spring IoC 时，最容易迷失的不是“方法太多”，而是：

- 不知道某个机制发生在 refresh 的哪一步
- 不知道“修改定义/添加处理器/触发 getBean”会影响哪一段

因此，本页先把 IoC 主线切成几个粗粒度阶段。每个阶段都对应一类问题和一组断点入口。

---

## refresh 主线时间线（粗粒度分段）

> 落点：遇到任何现象，先回答：它属于哪一段？

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

- `internals-bdrpp-definition-registration.md`
- `ioc-bean-registration.md`

### 1.3 段 C：实例层准备（注册 BPP 链）

关键点：

- 把所有 BeanPostProcessor 实例创建出来并按规则注册进链
- AOP/注解注入/生命周期回调等“能力”都依赖这条链

典型断点：

- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `DefaultListableBeanFactory#addBeanPostProcessor`

对应章节：

- `ioc-post-processors.md`
- `wiring-programmatic-bpp-registration.md`
- `appendix-debugger-pack.md`

### 1.4 段 D：创建单例（实例化 → 注入 → 初始化 → 入缓存）

关键点：

- `finishBeanFactoryInitialization` 会触发单例预实例化与创建
- `doCreateBean` 中存在循环依赖窗口（early exposure）

典型断点：

- `AbstractApplicationContext#finishBeanFactoryInitialization`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `DefaultSingletonBeanRegistry#getSingleton`

对应章节：

- `internals-refresh-to-bean-creation-mainline.md`
- `ioc-lifecycle-and-callbacks.md`
- `ioc-circular-dependencies.md`
- `internals-early-reference-and-circular.md`

### 1.5 段 E：完成与后置回调（容器就绪）

关键点：

- 容器完成 refresh，发布事件，执行“容器就绪”类回调（例如 SmartInitializingSingleton）

典型断点：

- `AbstractApplicationContext#finishRefresh`
- `SmartInitializingSingleton#afterSingletonsInstantiated`

对应章节：

- `wiring-smart-initializing-singleton.md`

### 1.6 段内关键对象变化（在 debugger 里应该观察到什么）

这一小节只承担一个职责：把“阶段”变成“可观察对象”。

无需记住全部字段，但应能在断点里回答：**当前处于哪个阶段？该阶段改变了什么？**

| 段 | 在断点里看什么 | 关键对象/变量（优先） | 可以得到的判断 |
| --- | --- | --- | --- |
| A 准备 | 容器是否已经具备“解析属性/注入容器对象”的基础能力 | `AbstractApplicationContext#prepareBeanFactory` 内：`beanFactory.resolvableDependencies`、embedded value resolvers、`beanFactory.getBeanClassLoader()` | 还没处理相应的 bean，但容器的“基础设施”已就绪（后续注解能否工作取决于下一段） |
| B 定义层 | BeanDefinition 是否已经齐全、是否被改写过 | `beanFactory.getBeanDefinitionCount()`、`getBeanDefinitionNames()`、`BeanDefinition#getSource()`、`BeanDefinition#getRole()` | 问题属于“没注册/注册错/被覆盖/被改写”时，这一段就能定位根因 |
| C 注册 BPP | BPP 链是否完整、顺序是否符合预期 | `beanFactory.getBeanPostProcessors()`（数量/类型/顺序）、关键处理器是否存在（注入/AOP/JSR-250） | “注解/AOP/回调不生效”的高频根因：BPP 没注册、注册晚了、顺序错了 |
| D 创建单例 | 单例缓存是否进入“创建窗口期”，是否出现 early reference | `singletonObjects/earlySingletonObjects/singletonFactories`、`singletonsCurrentlyInCreation`、`mbd`、`pvs` | 绝大多数运行期问题都在这里落地：注入、类型转换、代理替换、循环依赖边界 |
| E 容器就绪 | “容器就绪后”回调是否触发、事件是否发布 | `finishRefresh`、`SmartInitializingSingleton#afterSingletonsInstantiated`、事件发布 | 适合放“容器一致性校验/外部资源健康检查/延迟启动”类逻辑；也能解释“为什么某些逻辑必须等到这里” |

> 提醒：若在 D 段看到目标 bean 已经创建，但 C 段的关键 BPP 还没注册完成，那几乎必然是“创建过早/时机错误”。

---

## 这条时间线使用方式来排障（3 个经典分流）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


1. **注入失败（NoSuchBeanDefinition / NoUniqueBeanDefinition）**
   - 优先看段 D：`doResolveDependency/findAutowireCandidates/determineAutowireCandidate`
2. **代理/增强不生效**
   - 先分清是段 C（BPP 没注册/顺序不对）还是段 D（bean 创建过早错过 BPP）
3. **循环依赖/提前引用相关异常**
   - 段 D：`getSingleton` 的三层缓存分支 + `doCreateBean` 的 early exposure 窗口

---

## 面试常问（refresh 时间线）

1. **refresh 的关键阶段如何讲（且能落到方法名）？**
   - 要点：prepare → 定义层（BFPP/BDRPP）→ 注册 BPP 链 → 创建单例（doCreateBean）→ finishRefresh 回调。
   - 证据链：`AbstractApplicationContext#refresh` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` / `PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `finishBeanFactoryInitialization`。

2. **为什么说“时机决定能力”（尤其是代理/注解/回调）？**
   - 要点：BPP 链注册完成前创建的 bean，可能错过后续 BPP（代理/注解注入），表现为“有时生效有时不生效”。
   - 证据链：对照 `registerBeanPostProcessors` 与目标 bean 的创建时机，结合条件断点过滤 beanName。

复习入口：`appendix-interview-playbook.md`（Q1/Q5 等题型都以时间线为骨架）。

## 验收口径：三句话复述 refresh 时间线
读完后应能用 3 句复述：

1. BFPP/BDRPP 发生在 refresh 的哪一段？它改的是“定义”还是“实例”？
2. BPP 链是在什么时候注册的？为什么它决定了“注解/AOP/回调”是否生效？
3. 单例创建主线从哪开始（哪一步触发预实例化）？循环依赖窗口在 `doCreateBean` 的哪里？


## 小结：时间线让分支有位置

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

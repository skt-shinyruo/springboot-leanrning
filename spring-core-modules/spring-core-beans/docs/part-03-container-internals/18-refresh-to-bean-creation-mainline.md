# 18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）

## 导读

- 本章主题：**把 `ApplicationContext#refresh` 的“定义阶段”与“创建阶段”连成一条可下断点的主线**
- 阅读方式建议：先跑本章推荐 Lab（把现象固化为断言），再对照本文的“十步走/五段式/分支决策表”去源码下断点。

!!! summary "本章要点"

    - 读者只需要记住两条流水线：**图（BeanDefinition）如何扩张**、**图如何变成对象（bean instance）**。
    - 需要能按现象分流：注册缺失/条件没生效 → 看 refresh 第 5 步（BFPP/BDRPP）；注入/代理/生命周期 → 看第 9 步（`getBean` → `doCreateBean`）。
    - 需要能复述三类关键分支：`PriorityOrdered/Ordered` 顺序、`preInstantiateSingletons` 预实例化 vs lazy、early reference vs circular boundary。
    - 需要知道“该在哪下断点”：`AbstractApplicationContext#refresh`、`PostProcessorRegistrationDelegate`、`AbstractBeanFactory#doGetBean`、`AbstractAutowireCapableBeanFactory#doCreateBean`。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansPreInstantiationLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`

## 机制主线

> 基线版本：Spring Framework `6.2.15`（本仓库由 Spring Boot `3.5.9` 管理依赖版本）。

这一章只做一件事：**把容器启动与 Bean 创建的主线串起来**，并且落到“关键方法 + 关键分支”上。

读者读完后，不需要背完 Spring 源码，但应该能做到：

- 看到一个 Bean 的奇怪行为，能快速判断它发生在 **refresh 哪一段**。
- 知道应该去哪个方法下断点，看哪个变量，定位是 **BeanDefinition 阶段** 的问题，还是 **对象创建阶段** 的问题。

## 0. 先把“主线地图”记住：容器做两件事

Spring 的容器启动（`ApplicationContext#refresh`）看起来很长，但逻辑上只有两条主线：

1. **形成“图”**：把“配置/注解/XML/Import/自动配置”等输入，最终变成一张 `BeanDefinition` 关系图（**定义阶段**）。
2. **把图变成对象**：按需或预先把 `BeanDefinition` 实例化成真实对象，并完成注入、初始化、代理包装（**创建阶段**）。

为阐明该机制，下文用两层主线将主线串联起来：

- 容器主线：`AbstractApplicationContext#refresh`
- Bean 主线：`AbstractBeanFactory#doGetBean` → `AbstractAutowireCapableBeanFactory#doCreateBean`

可以把它想成两条流水线：第一条生产“施工图纸”（BeanDefinition），第二条把图纸“开工落地”（创建对象并装配）。

若希望把本章“读成故事”而不是“读成百科”，建议读者只盯两个问题推进阅读：

1. **现在是在扩张定义（BeanDefinition），还是在创建对象（bean instance）？**
2. **此刻是谁在改规则：BDRPP/BFPP（改定义）还是 BPP（改对象）？**

### 0.1 机制讲透：条件 → 分支 → 结果（主线版）

**条件**：问题发生在定义阶段还是创建阶段  
**分支**：`invokeBeanFactoryPostProcessors`（定义层） vs `finishBeanFactoryInitialization`（创建层）  
**结果**：  
- 定义层：决定“有没有/谁注册的/配方是什么”  
- 创建层：决定“什么时候创建/是否代理/生命周期顺序”  
**断点建议**：`AbstractApplicationContext#finishBeanFactoryInitialization`

---

## 1. 第一幕：`refresh()` 的骨架（容器主线）

核心方法在：`org.springframework.context.support.AbstractApplicationContext#refresh`

为了读源码不迷路，读者先把 refresh 的骨架背成“十步走”（名字不要求完全背下来，但每一步的**职责**要记住）：

```text
refresh()
  1) prepareRefresh()
  2) obtainFreshBeanFactory()
  3) prepareBeanFactory(beanFactory)
  4) postProcessBeanFactory(beanFactory)
  5) invokeBeanFactoryPostProcessors(beanFactory)
  6) registerBeanPostProcessors(beanFactory)
  7) initMessageSource()
  8) initApplicationEventMulticaster() + registerListeners()
  9) finishBeanFactoryInitialization(beanFactory)
 10) finishRefresh()
```

需要把这十步映射到“图/对象”两条主线：

- **图的扩张**主要发生在第 5 步：`invokeBeanFactoryPostProcessors`
- **对象的落地**主要发生在第 9 步：`finishBeanFactoryInitialization`

### 1.3 关键分支解释（为什么有些问题“启动就爆”）

- **是否预实例化**：`mbd.isLazyInit()` 为 false 的 singleton 会在 `preInstantiateSingletons` 被创建  
- **是否 FactoryBean**：`getObjectForBeanInstance` 决定拿到工厂还是产品  
- **是否 prototype**：prototype 不进单例缓存，循环依赖更容易 fail-fast  
- **是否允许循环依赖**：`allowCircularReferences` 决定 early exposure 是否开启

### 1.1 `obtainFreshBeanFactory()`：读者真正持有的是哪个 `BeanFactory`

读者后面会反复看到 `DefaultListableBeanFactory`，因为它是 Spring 最常用的默认实现之一。

在 refresh 的早期阶段，容器会“准备好一台工厂”（BeanFactory），其核心职责是：

- 持有 `BeanDefinition`（定义阶段）
- 提供 `getBean()`（创建阶段）
- 维护单例缓存与生命周期状态（创建阶段）

**关键观察点**（调试时非常值钱）：

- `ConfigurableListableBeanFactory` 的实际类型（常见是 `DefaultListableBeanFactory`）
- 当前已经注册的 `BeanDefinition` 数量（是否在“图的扩张”阶段不断增长）

### 1.2 `prepareBeanFactory()`：把“容器级基础设施”先塞进去

这一步很容易被忽略，但它是后面很多“为什么能注入/为什么能解析”的根。

这一步会做很多基础设施注册，例如：

- `ClassLoader` / 表达式解析器 / 属性编辑器等（影响 `@Value`、类型转换）
- 一些 `ResolvableDependency`（例如 `Environment`、`ResourceLoader` 等可直接注入的对象）
- 对 `Aware` 系列接口的支持（影响 initialize 阶段）

调试建议：

- 若怀疑是“容器基础设施缺失”导致的解析失败，优先回到这里看：它决定了很多“全局能力”的开关是否就位。

---

## 2. 第二幕：`invokeBeanFactoryPostProcessors()` —— 图为什么会“越长越大”

核心入口在：`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`

若希望理解 `@Configuration`、`@ComponentScan`、`@Import`、以及 Spring Boot 自动配置为什么会生效，绕不开这一段。

### 2.1 关键分支：`BeanDefinitionRegistryPostProcessor`（BDRPP） vs `BeanFactoryPostProcessor`（BFPP）

这两个名字非常像，但职责不同：

- `BeanDefinitionRegistryPostProcessor`：可以直接往 `BeanDefinitionRegistry` 里**新增/修改** BeanDefinition（它能让“图继续长大”）
- `BeanFactoryPostProcessor`：主要是对 `BeanFactory`/已存在定义做“全局修饰”，但**原则上不负责无限制扩张图**

真正关键的是：**BDRPP 必须先执行**，因为它可能注册更多 BeanDefinition；执行完后，容器才知道“图上到底有哪些节点”。

这里有一个非常实用的“落地判断”：

- 遇到的问题如果是“某个 Bean 根本没注册/注册数量不对/某个 @Bean 没出现”，优先怀疑 **BDRPP/BFPP（定义阶段）**。
- 遇到的问题如果是“Bean 已存在但注入不对/变成 proxy/生命周期回调顺序奇怪”，优先怀疑 **BPP（创建阶段）**。

### 2.2 关键分支：按顺序分三批（`PriorityOrdered` → `Ordered` → others）

无论 BDRPP 还是 BFPP，都要遵循统一的优先级语义：

1. `PriorityOrdered`
2. `Ordered`
3. 没有顺序语义的“普通”后处理器

为什么这是关键分支？

- 在工程里“插入一个全局规则”（例如替换占位符、修改 BeanDefinition、注册额外 Bean），能否按读者预期的时机生效，取决于它落在哪个优先级组里。
- 这也是很多“为什么我注册了一个 processor，但它没影响到某些 Bean”的根因之一。

### 2.3 最容易忽略的关键机制：BDRPP 可能会“循环发现”

这一段源码里有一个非常重要的策略：**执行 BDRPP 时，要考虑它执行过程中又注册了新的 BDRPP**。

所以它不是“一次遍历就结束”，而是倾向于：

```text
while (还能发现新的 BDRPP) {
  取出当前还没执行的 BDRPP
  执行它们（仍按 PriorityOrdered/Ordered/others 分组）
}
```

这就是为什么：

- 读者看 refresh 过程中 BeanDefinition 数量会“跳跃式”增长
- 读者看某些关键后处理器会“看似晚出现”，但实际上是被前一批 processor 注册出来的

### 2.4 把 `@Configuration` 拉到主线上：`ConfigurationClassPostProcessor` 是图扩张的发动机

在纯 Spring（以及 Boot）世界里，最重要的一类 BDRPP 就是 `ConfigurationClassPostProcessor`：

- 它会解析 `@Configuration`、`@ComponentScan`、`@Import` 等
- 把“注解声明的世界”翻译成真正的 BeanDefinition
- 并可能注册更多候选组件（让图继续扩张）

把它说得更源码一点，它至少有两个必须记住的入口：

- `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`：在 registry 上“扩张图”（新增 BeanDefinition）
- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`：真正解析配置类、处理 `@Import/@ComponentScan/@Bean`

读者如果只记一个结论：

> **很多容易误以为是“注解直接生效”的机制，本质是在 refresh 的第 5 步被翻译成 BeanDefinition。**

### 2.5 在源码里会看到的“两段式循环”（理解 BDRPP 何时“发现新东西”）

若去读 `PostProcessorRegistrationDelegate`，它的核心策略不是“把 processor 列表排个序就完事”，而是把工作拆成两段：

1. **先处理能改 registry 的那批（BDRPP）**：因为它们会继续注册更多定义/更多 processor
2. **再处理只改 factory 的那批（BFPP）**

而且 BDRPP 这段通常不是“一次遍历”，而是“循环发现直到收敛”。

在调试时的价值是：当读者看到 BeanDefinition 数量在 refresh 期间跳增，应当自然想到——**不是“Spring 随机注册了一堆东西”，而是在 BDRPP 执行过程中又引入了新的定义。**

对应本仓库的验证入口（建议按顺序做）：

- `SpringCoreBeansRegistryPostProcessorLabTest`
- `SpringCoreBeansPostProcessorOrderingLabTest`
- `SpringCoreBeansBootstrapInternalsLabTest`

---

## 3. 第三幕：`registerBeanPostProcessors()` —— 容易误以为是“初始化”，其实是在“装规则”

核心入口在：`org.springframework.context.support.PostProcessorRegistrationDelegate#registerBeanPostProcessors`

如果说 BFPP/BDRPP 决定“图长成什么样”，那 BPP 决定“对象落地时会被怎么加工”。

### 3.1 BeanPostProcessor 的价值：它能介入“创建阶段”的每一步

读者后面会看到 `doCreateBean()` 的多个阶段都允许 BPP 介入：

- 实例化前：`postProcessBeforeInstantiation`（可能短路创建）
- 属性注入：`postProcessProperties`（例如 `@Autowired` 注入发生在哪里）
- 初始化前后：`postProcessBeforeInitialization` / `postProcessAfterInitialization`（AOP 代理常在这里出现）

这意味着：**BeanPostProcessor 是对象创建阶段的规则引擎**。

### 3.2 关键分支：BPP 的顺序同样是 `PriorityOrdered` → `Ordered` → others

它的影响更“隐蔽”：

- 同一个 Bean 被多个 BPP 处理时，谁先谁后可能决定最终对象形态
- AOP 代理叠加、多层包装的顺序问题，本质是 BPP 顺序问题

对应本仓库验证入口：

- `SpringCoreBeansPostProcessorOrderingLabTest`
- `SpringCoreBeansPreInstantiationLabTest`（关注“短路”）

### 3.3 一个很“反直觉”的事实：BPP 不是“会自动生效的”

很多人第一次读 Spring 容器会误以为：`@Autowired/@PostConstruct/@Resource` 是“注解自带隐式行为”。

实际上这类“注解行为”大多依赖某些 BPP（例如处理注入与生命周期的后处理器）。

这也是为什么：

- 读者用纯 `BeanFactory` 手动注册/获取 bean 时，经常会发现“注解没生效”
- 读者用 `ApplicationContext#refresh` 后就正常了

从主线角度看，这不是黑箱：**BPP 的注册时机在 refresh 中是一个明确节点**（第 6 步），早于它的创建行为通常“看不到”BPP 的影响。

---

## 4. 第四幕：`finishBeanFactoryInitialization()` —— 图开始变成对象

核心入口在：`AbstractApplicationContext#finishBeanFactoryInitialization`

这一步真正做的事情是：

- 触发 `DefaultListableBeanFactory#preInstantiateSingletons`
- 也就是“把绝大多数非懒加载的单例 Bean 先创建出来”

可以把它理解为：**从“定义阶段”切换到“创建阶段”的关键切换点**。

### 4.1 关键分支：哪些 Bean 会在这里被创建（而不是在第一次 `getBean` 时）

`preInstantiateSingletons()` 并不是“把所有 Bean 都 new 一遍”，它有一套筛选逻辑，典型分支包括：

- 抽象 BeanDefinition 跳过
- `lazy-init` 的 Bean 先不创建
- `FactoryBean` 需要特殊处理（因为它可能生产另外一个对象）

这也是“为什么有的 Bean 启动时就报错，有的要到第一次调用才报错”的根因之一：它们在主线上的落点不同。

对应本仓库验证入口：

- `SpringCoreBeansPreInstantiationLabTest`

### 4.2 `finishBeanFactoryInitialization` 的两个“决定性动作”

读者如果只想记住这里最关键的两句源码级结论：

1. `beanFactory.freezeConfiguration()`：把“定义阶段”锁死（很多动态注册/覆盖行为到此会变得受限）
2. `beanFactory.preInstantiateSingletons()`：开始批量创建非懒加载单例（触发整条 getBean → createBean 链）

这两句把“图”推进成“对象”，并且让很多问题从“启动阶段”暴露出来。

### 4.3 把 `preInstantiateSingletons()` 写成伪代码：应能够“顺着念出分支”

读者如果希望把“为什么启动时会创建某些 Bean”讲清楚，不能只停留在“非懒加载单例会被创建”这句话。

真正的价值在于：应能够指出**源码里是哪些判断把 Bean 分成了不同命运**。

下面是一份“只保留关键分支”的伪代码（方法名以 Spring Framework `6.2.15` 为准）：

```text
finishBeanFactoryInitialization(beanFactory):
  beanFactory.freezeConfiguration()
  beanFactory.preInstantiateSingletons()

preInstantiateSingletons():
  names = copy(beanDefinitionNames)

  for each beanName in names:
    mbd = getMergedLocalBeanDefinition(beanName)       // RootBeanDefinition

    if (mbd.isAbstract()) continue
    if (!mbd.isSingleton()) continue
    if (mbd.isLazyInit()) continue

    if (isFactoryBean(beanName)):
      factory = getBean("&" + beanName)                // 先创建 FactoryBean 本体
      if (factory is SmartFactoryBean && factory.isEagerInit()):
        getBean(beanName)                              // 再创建“产品对象”
    else:
      getBean(beanName)                                // 普通单例：直接创建

  // 单例们都 ready 之后的“收尾回调”
  for each bean in getBeansOfType(SmartInitializingSingleton):
    bean.afterSingletonsInstantiated()
```

可以得到 3 个非常实用的“排障结论”：

1. **“启动即创建”不是一个开关，而是一串 if 判断。**
   只要读者把 `abstract/lazy/singleton/FactoryBean/SmartFactoryBean` 这几类分支钉住，即可解释大多数“为什么它启动时就出问题”的案例。
2. **FactoryBean 的预实例化是“两段式”。**
   `&name` 创建的是 FactoryBean 本体；是否预先创建产品对象，要看 `SmartFactoryBean#isEagerInit()` 这条分支。
3. **有些初始化逻辑不属于 doCreateBean，它发生在“单例都出来之后”。**
   `SmartInitializingSingleton#afterSingletonsInstantiated()` 就是典型例子：它在 preInstantiateSingletons 收尾阶段触发。

#### 4.3.1 一个容易误判的高级分支：background init 与 `bootstrapExecutor`

若在日志里见过类似：

> Bean 'xxx' marked for pre-instantiation ... but currently initialized by other thread - skipping it in mainline thread

这通常意味着读者命中了“后台预实例化”分支（不是 lazy-init），其关键抓手是：

- `DefaultListableBeanFactory#setBootstrapExecutor(Executor)` / `getBootstrapExecutor()`
- `AbstractBeanDefinition#setBackgroundInit(true)` / `isBackgroundInit()`

这类分支的学习意义在于：它会让“谁在创建 bean（哪个线程）”这个问题变得真实可见。排障时不要只盯调用栈，还要盯线程与 dispatcher。

对应本仓库验证入口（先跑再读，会更有感觉）：

- `SpringCoreBeansPreInstantiationLabTest`

---

## 5. 第五幕：一次 `getBean()` 的内核（`doGetBean`）

当读者进入创建阶段后，绝大多数问题最终都会落到这一条链路：

`AbstractBeanFactory#getBean` → `AbstractBeanFactory#doGetBean`

需要重点理解 `doGetBean` 的三个“关键分支”：

1. **单例缓存命中**：如果对象已存在，直接返回（可能返回 early reference）
2. **正在创建中**：如果同名 Bean 正在创建，且允许“早期引用”，可能提前返回一个未完全初始化的对象（循环依赖相关）
3. **需要创建**：最终走向 `createBean`，进入 `doCreateBean`

与循环依赖相关的缓存结构（读者调试时经常要盯）：

- `singletonObjects`（完整单例）
- `earlySingletonObjects`（早期引用）
- `singletonFactories`（用于生成早期引用的工厂）

这些都在：`DefaultSingletonBeanRegistry` 里维护。

对应本仓库验证入口：

- `SpringCoreBeansEarlyReferenceLabTest`
- `SpringCoreBeansCircularDependencyBoundaryLabTest`

### 5.1 `FactoryBean` 分支（很多人第一次易错点就在这里）

`doGetBean` 的一个经典“关键分支”是：**同一个名字，可能代表两种东西**：

- `beanName`：默认拿到的是 FactoryBean 生产的“产品对象”（`FactoryBean#getObject()` 的结果）
- `&beanName`：显式拿到的是 FactoryBean 自己这个对象

源码层面的关键点在于 `AbstractBeanFactory#getObjectForBeanInstance(...)` 这一步：

- 如果它判断当前实例是 `FactoryBean`，且读者不是用 `&` 前缀来拿它本体，它会继续走“取产品对象”的路径。

这类问题的现象往往是：

- “我明明注册的是 A，但 getBean 拿到的是 B”

遇到它别急着怀疑容器“乱了”，先检查自己是不是在 FactoryBean 语义里。

### 5.2 `dependsOn` 分支：它控制“初始化顺序”，不是“注入方式”

很多人第一次遇到 `dependsOn` 会误解它的语义：以为它会影响注入解析（其实不会），它影响的是**创建顺序**。

`doGetBean` 在进入真正创建前，会先检查合并后的定义上是否声明了 `dependsOn`：

- 定义来源：`mbd.getDependsOn()`（`RootBeanDefinition`）
- 行为语义：对每个 depends-on bean 先执行一次 `getBean(dep)`，确保它先完成创建

这条分支的排障价值非常高，因为它能解释两类“看似反直觉”的现象：

1. **“我明明把 bean 标成 lazy-init，但它还是启动时就被创建了。”**
   可能原因：它被别的 bean 声明为 `dependsOn`（或被别的 bean 依赖，依赖也会强制创建）。
2. **“我没有注入它，但它为什么先被创建？”**
   `dependsOn` 就是“非注入式的初始化边”，它强制了创建顺序。

对应本仓库验证入口：

- `SpringCoreBeansDependsOnLabTest`

### 5.3 parent factory fallback：为什么 child context 能看到 parent，但 parent 看不到 child

`ApplicationContext`（以及底层 `BeanFactory`）是可以形成层级的：child context 持有自己的 `BeanFactory`，同时有一个 parent。

当 `doGetBean` 在当前工厂查不到目标 bean 时，会进入一个非常关键的兜底分支：

- `getParentBeanFactory() != null` ⇒ 把查找委托给 parent（本质是一个 fallback）

这条分支能解释三种常见现象：

1. **child 能拿到 parent 的 bean（因为会 fallback）**
2. **parent 拿不到 child 的 bean（因为 parent 不会反向查 child）**
3. **child 可以按 name 覆盖同名 bean，而不影响 parent**

对应本仓库验证入口：

- `SpringCoreBeansContextHierarchyLabTest`

### 5.4 prototype guard：为什么 prototype 的循环依赖通常“救不了”

在前面理解了“单例循环依赖能通过 early reference 被救”，但这个结论不可以无脑推广到 prototype。

原因是：prototype 的创建过程没有“全局单例缓存”这条救援通道，它会用一个“正在创建中的标记”保护自己：

- `isPrototypeCurrentlyInCreation(beanName)`
- `beforePrototypeCreation(beanName)` / `afterPrototypeCreation(beanName)`

这类 guard 的意义是：当 prototype 在创建过程中再次触发同名 prototype 创建，容器可以尽早识别并失败（避免无限递归）。

对应本仓库的“可观察证据”（prototype 的核心语义：不进入 singleton 缓存，且多次 getBean 会产生不同实例）：

- `SpringCoreBeansSingletonCacheExploreTest`

---

## 6. 第六幕：`doCreateBean()` 的“五段式”（Bean 创建主线）

核心方法在：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`

可以把一次 Bean 的创建理解成“五段式”，每一段都对应一组关键方法与关键分支：

```text
doCreateBean(beanName, mbd, args)
  1) createBeanInstance(...)          // 实例化：怎么 new？
  2) applyMergedBeanDefinition...     // 合并定义 + 让 MergedBD BPP 介入
  3) earlySingletonExposure?          // 循环依赖：是否要提前暴露？
  4) populateBean(...)                // 注入：属性/字段/方法参数怎么塞？
  5) initializeBean(...)              // 初始化：aware + init + BPP 包装（AOP常在此）
  6) registerDisposableBeanIfNecessary
```

下文将把每一段的“关键分支”点出来（这是读源码的价值）。

### 6.0 先分清一个重要边界：`createBean` 里可能根本不进入 `doCreateBean`

很多“对象形态（proxy/短路）”的关键行为其实发生在 `createBean(...)` 的早期阶段：

- `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(...)`
  - 内部会调用 `applyBeanPostProcessorsBeforeInstantiation`
  - 如果某个 `InstantiationAwareBeanPostProcessor` 返回了一个替代对象（常见是代理），后续就可能 **直接跳过 `doCreateBean`**（可以感觉“构造器都没跑，怎么就有 bean 了？”）

这也是在断点里经常看到的现象：

- 有些 bean 的构造器不会触发（被“实例化前短路”了）

对应本仓库验证入口：

- `SpringCoreBeansPreInstantiationLabTest`（里面专门固定了“短路 vs 不短路”的对照）

### 6.1 `createBeanInstance`：实例化策略的选择树

实例化阶段最常见的分支路线（从高优先级到低优先级）大体是：

1. `Supplier`/自定义实例供应（如果定义了）
2. 工厂方法（`factory-method` 或 `@Bean` 工厂方法）
3. 构造器解析与自动注入（构造器注入）
4. 默认无参构造

读者调试时主要盯两个东西：

- `RootBeanDefinition` 上与实例化相关的元数据（例如是否有工厂方法、构造器候选）
- `InstantiationStrategy` 的选择（最终走的是哪条实例化路径）

### 6.2 `applyMergedBeanDefinitionPostProcessors`：哪些规则会影响“合并后的定义”

这一段的存在提醒读者：**BeanDefinition 不是最终形态**。

很多信息会在“合并”后才变得完整（例如继承、合并属性、解析后的元数据），并且允许 `MergedBeanDefinitionPostProcessor` 介入。

若看到“同一个 BeanDefinition 在不同阶段表现不同”，通常要回到这里看：它可能在合并阶段被补全/修正过。

### 6.3 `earlySingletonExposure`：循环依赖为什么能“先拿到一个对象”

这是 `doCreateBean` 里最典型、也最容易误解的关键分支之一。

源码层面的核心判断大意是：

- 这个 Bean 是单例
- 容器允许循环依赖（开关）
- 当前 Bean 正处于创建中

满足后会把一个 “ObjectFactory” 放进 `singletonFactories`，让别人在依赖解析时能拿到一个“早期引用”。

**关键点**：早期引用不一定是“原始对象”，它可能经过 `getEarlyBeanReference` 处理（例如提前包一层代理）。

这就是为什么必须同时理解：

- `DefaultSingletonBeanRegistry#addSingletonFactory` / `getSingleton`
- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`

### 6.3.1 一个极其关键但经常被忽略的分支：early reference 与最终对象“不一致”怎么办？

当读者开启 early exposure 时，容器可能先把一个“早期引用”交给依赖方；但后续初始化完成后，最终暴露对象可能被 BPP 替换成了另一个对象（例如代理）。

这会出现一个非常危险的状态：

- 依赖方拿到的是 raw instance（原始对象）
- 容器最终暴露的是 wrapped instance（代理对象）

Spring 默认会尽量避免这种“raw injection despite wrapping”，否则可以在系统里同时存在两个“看起来像同一个 bean 的对象”，很多 AOP/事务/缓存语义会变得不可预测。

对应本仓库验证入口：

- `SpringCoreBeansRawInjectionDespiteWrappingLabTest`（把这个边界变成断言，而不是靠记忆）

### 6.4 `populateBean`：注入发生在哪（以及为什么可能被跳过）

注入阶段最典型的“关键分支”是：

- `InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation` 返回 `false` 时，可以**短路**属性填充
- `InstantiationAwareBeanPostProcessor#postProcessProperties` 是很多注解注入（如 `@Autowired`）真正发生的入口之一

换句话说：容易误以为注入是“BeanFactory 直接干的”，但实际上很多逻辑是 BPP 驱动的。

### 6.5 `initializeBean`：初始化 ≠ 只调一个 `init-method`

初始化阶段至少包含三段：

1. 处理各种 `Aware`（让 Bean 拿到容器能力）
2. `BeanPostProcessor#postProcessBeforeInitialization`
3. 执行 init 回调（`InitializingBean#afterPropertiesSet`、自定义 init-method、JSR-250 等）
4. `BeanPostProcessor#postProcessAfterInitialization`

在工程里经常看到的“代理对象”就是在第 4 步成型的（例如 AOP 的自动代理）。

这也解释了为什么：

- 在某些时刻拿到的是原始对象
- 再往后拿到的就变成代理对象

对应本仓库验证入口：

- `SpringCoreBeansBeanCreationTraceLabTest`
- `SpringCoreBeansLifecycleCallbackOrderLabTest`
- `SpringCoreBeansRawInjectionDespiteWrappingLabTest`

## 可复现闭环（基于 `SpringCoreBeansBeanCreationTraceLabTest`）

跑完该 Lab，至少应能够复述 3 条结论：

1) **创建链路可被分段观测**  
   - 断点：`doCreateBean` / `populateBean` / `initializeBean`  
   - 断言：阶段顺序稳定
2) **最终暴露对象可能被替换**  
   - 断点：`applyBeanPostProcessorsAfterInitialization`  
   - 断言：`result != bean`
3) **启动期异常多发生在预实例化阶段**  
   - 断点：`preInstantiateSingletons`  
   - 断言：非 lazy 单例在此被创建

---

## 7. 把“关键分支”变成调试能力：建议的断点与观察变量

当需要定位 Bean 的异常行为，建议按“主线阶段”来下断点：

### 7.1 容器阶段断点（定位“图”的问题）

- `AbstractApplicationContext#refresh`
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`（注解配置翻译入口之一）

观察变量建议：

- 当前 `beanDefinitionNames` 的变化（图是否在扩张）
- processor 的执行顺序（是否符合相应的优先级预期）

### 7.2 创建阶段断点（定位“对象”的问题）

- `DefaultListableBeanFactory#preInstantiateSingletons`
- `AbstractBeanFactory#doGetBean`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `DefaultSingletonBeanRegistry#addSingletonFactory` / `getSingleton`

观察变量建议：

- `beanName` / `mbd`（RootBeanDefinition）
- `singletonsCurrentlyInCreation`（是否在循环依赖窗口）
- `earlySingletonExposure`（是否走了提前暴露）
- `exposedObject`（最终返回对象是否已被包装/代理）

## 排障分流：现象 → 阶段 → 关键方法 → 必看变量 → 对应 LabTest

下面这张表的目的不是“背诵”，而是把本章的主线叙事进一步压缩成一个**可复用的排障套路**：

> 读者只要拿到“现象”，就能反推出它大概率发生在哪个阶段、该去哪一段源码下断点、以及用哪个 Lab 把它变成可回归的证据链。

| 现象（观察到的） | 所在阶段（大概率落点） | 关键方法（建议断点） | 必看变量/结构（解释分支走向） | 对应 LabTest（可复现） |
|---|---|---|---|---|
| 某个 Bean “根本没注册出来”（`@Bean/@ComponentScan/@Import` 看起来没生效） | **定义阶段**：refresh 第 5 步（图还在扩张） | `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`<br>`ConfigurationClassPostProcessor#processConfigBeanDefinitions` | `beanDefinitionNames`（数量是否跳增）<br>BDRPP 执行顺序/是否循环发现新 BDRPP | `SpringCoreBeansRegistryPostProcessorLabTest`<br>`SpringCoreBeansBootstrapInternalsLabTest` |
| “我写了 BFPP/BDRPP，但没影响到某些定义”（时机/顺序不符合预期） | **定义阶段**：refresh 第 5 步（PriorityOrdered/Ordered 分批） | `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` | processor 分组（PriorityOrdered/Ordered/others）<br>“循环发现”是否还在继续 | `SpringCoreBeansPostProcessorOrderingLabTest` |
| `@Autowired/@PostConstruct` 等行为在某些启动方式下“不生效”（特别是手动 new BeanFactory） | **规则装载阶段**：refresh 第 6 步（BPP 是否注册齐） | `PostProcessorRegistrationDelegate#registerBeanPostProcessors`<br>`AbstractAutowireCapableBeanFactory#populateBean`<br>`AbstractAutowireCapableBeanFactory#initializeBean` | `beanFactory.getBeanPostProcessors()` 是否包含关键 BPP（注入/生命周期相关） | `SpringCoreBeansBootstrapInternalsLabTest`<br>`SpringCoreBeansLifecycleCallbackOrderLabTest` |
| 有的 Bean “启动时就报错”，有的“第一次 getBean 才报错” | **创建阶段入口**：refresh 第 9 步（预实例化） vs 首次 `getBean` | `DefaultListableBeanFactory#preInstantiateSingletons`<br>`AbstractBeanFactory#doGetBean` | `mbd.isLazyInit()`<br>`mbd.isAbstract()`<br>是否单例/是否 FactoryBean | `SpringCoreBeansPreInstantiationLabTest` |
| `lazy-init` 看起来没用：我明明想延迟创建，但它还是启动时就出来了 | **创建阶段入口/创建顺序**：预实例化 + `dependsOn` | `DefaultListableBeanFactory#preInstantiateSingletons`<br>`AbstractBeanFactory#doGetBean`（dependsOn 检查） | `mbd.getDependsOn()`<br>初始化顺序边是否存在 | `SpringCoreBeansDependsOnLabTest` |
| child context 能拿到 parent 的 bean，但 parent 拿不到 child；同名 bean 在 child 覆盖不影响 parent | **层级容器查找**：parent factory fallback | `AbstractBeanFactory#doGetBean`（fallback 到 parent）<br>`BeanFactory#getParentBeanFactory` | `parentBeanFactory` 是否存在<br>“当前工厂没定义时才会 fallback” | `SpringCoreBeansContextHierarchyLabTest` |
| Bean “看起来没走构造器就有了”（被代理/替换了） | **创建短路**：`createBean` 早期（可能不进入 `doCreateBean`） | `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`<br>`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` | `bean` 是否在实例化前被替代（proxy）<br>是否直接跳过 `doCreateBean` | `SpringCoreBeansPreInstantiationLabTest` |
| `getBean("x")` 拿到的不是容易误以为的类型，`&x` 又变了 | **FactoryBean 分支**：同名两种语义（本体 vs 产品） | `AbstractBeanFactory#getObjectForBeanInstance` | beanName 是否带 `&` 前缀<br>instance 是否 `FactoryBean` | `SpringCoreBeansFactoryBeanDeepDiveLabTest` |
| 同一个 bean 在依赖注入与最终容器暴露对象之间“形态不一致”（raw vs proxy） | **循环依赖窗口 + 包装阶段**：early reference vs 最终对象 | `DefaultSingletonBeanRegistry#addSingletonFactory/getSingleton`<br>`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`<br>`AbstractAutowireCapableBeanFactory#initializeBean` | `singletonFactories/earlySingletonObjects/singletonObjects`<br>`exposedObject`（最终返回对象）<br>`allowRawInjectionDespiteWrapping` 边界 | `SpringCoreBeansEarlyReferenceLabTest`<br>`SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 循环依赖：setter 能跑、constructor 就死；或者加了 `@Lazy` 又好了 | **循环依赖边界**：依赖解析时机不同 | `AbstractBeanFactory#doGetBean`<br>`AbstractAutowireCapableBeanFactory#doCreateBean`（early exposure） | `singletonsCurrentlyInCreation`<br>依赖解析发生在构造器阶段还是属性填充阶段 | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| 注入发生了但“字段/属性没被填充”（或被某个框架短路掉了） | **populateBean 分支**：注入可被 BPP 介入或短路 | `AbstractAutowireCapableBeanFactory#populateBean`<br>`InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation`<br>`InstantiationAwareBeanPostProcessor#postProcessProperties` | `postProcessAfterInstantiation` 返回值（是否 short-circuit）<br>`PropertyValues`/`pvs` 变化 | `SpringCoreBeansInjectionPhaseLabTest` |
| 读者怀疑“BeanDefinition 在不同阶段不一样”（合并/父子定义/元数据补全） | **合并定义阶段**：MergedBeanDefinition 形成 | `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors` | `mbd`（RootBeanDefinition）合并后的属性/元数据 | `SpringCoreBeansMergedBeanDefinitionLabTest` |
| 生命周期回调顺序“跟若希望的不一样”（Aware/InitializingBean/init-method/BPP 顺序） | **initializeBean 五段式**：aware → beforeInit → init → afterInit | `AbstractAutowireCapableBeanFactory#initializeBean` | 事件/日志序列（哪一步先发生）<br>`wrappedBean` vs 原始 bean | `SpringCoreBeansLifecycleCallbackOrderLabTest` |

---

## 8. 推荐的“主线验证路径”（用本仓库把它走一遍）

若希望把这一章内容落到手上（而不是停留在文字），建议按这个顺序跑测试：

1. `SpringCoreBeansBootstrapInternalsLabTest`：先把 refresh 走一遍，看到“十步走”在哪里发生。
2. `SpringCoreBeansRegistryPostProcessorLabTest`：重点看 BDRPP 如何扩张 BeanDefinition 图。
3. `SpringCoreBeansPostProcessorOrderingLabTest`：把顺序语义（PriorityOrdered/Ordered）看成“规则加载顺序”。
4. `SpringCoreBeansPreInstantiationLabTest`：看哪些 Bean 会在启动时创建。
5. `SpringCoreBeansBeanCreationTraceLabTest`：从 `doGetBean` 走到 `doCreateBean` 的五段式。
6. `SpringCoreBeansEarlyReferenceLabTest` + `SpringCoreBeansCircularDependencyBoundaryLabTest`：把 early reference 与循环依赖边界走通。
7. `SpringCoreBeansLifecycleCallbackOrderLabTest`：把初始化回调的顺序钉死在大脑里。

---

## 9. 读完这一章，下一步怎么学（把“主线”扩成“能力图”）

读者已经把主线走通后，建议读者用“专题页”把每个关键分支补齐细节：

- 后处理器与顺序：[14-post-processor-ordering.md](14-post-processor-ordering.md)
- BDRPP 注册与定义扩张：[13-bdrpp-definition-registration.md](13-bdrpp-definition-registration.md)
- 预实例化短路：[15-pre-instantiation-short-circuit.md](15-pre-instantiation-short-circuit.md)
- early reference 与循环依赖：[16-early-reference-and-circular.md](16-early-reference-and-circular.md)
- 生命周期回调顺序：[17-lifecycle-callback-order.md](17-lifecycle-callback-order.md)

可以发现：这些文件不是“散点知识”，而是主线上的分支专题。

## 源码调用链（方法级）：refresh → doCreateBean 的最短主线

这章无需背所有步骤，但必须能把主线串成“方法级调用链”（面试/排障都用得上）：

1) `AbstractApplicationContext#refresh`（总入口）
2) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（定义层：BDRPP/BFPP 稳定“配方”）
3) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（实例层：BPP 链准备）
4) `AbstractApplicationContext#finishBeanFactoryInitialization` → `DefaultListableBeanFactory#preInstantiateSingletons`（大规模创建开始）
5) `AbstractBeanFactory#doGetBean`（按需创建入口）
6) `AbstractAutowireCapableBeanFactory#doCreateBean`（单 bean 五段式：instance→populate→initialize→expose）

读者只要能在调试器里把这条链走一遍，并解释每一步“为什么在这里”，就能把 IoC 主线讲清楚。

## 面试常问（refresh 主线：应能够讲“阶段”而不是“名词”）

### Q1：refresh 为什么要“先定义层、再实例层”？顺序错了会怎样？

- 标准答案（可复述）：
  - 因为后续实例化/注入依赖“稳定的 BeanDefinition 与基础设施处理器”；如果定义层没稳定就开始创建实例，会出现错过 BPP/代理不生效/条件不一致等时序问题。
- 证据链（方法级）：
  - `invokeBeanFactoryPostProcessors`（定义层稳定）
  - `registerBeanPostProcessors`（实例层拦截链准备）
  - `preInstantiateSingletons`（创建开始）
- 最小复现：
  - `SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBeanCreationTraceLabTest`

### Q2：为什么 BPP 是“创建时拦截链”，不是“创建后补丁”？

- 标准答案（可复述）：
  - BPP 只在 bean 创建流程的钩子点被调用；如果某个 bean 在 BPP 链完整之前就被创建，它不会 retroactively 被后来注册的 BPP 处理，导致增强缺失或顺序反直觉。
- 证据链（方法级）：
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

## 自检要点
- 应能够否用一句话区分：**定义阶段** 与 **创建阶段**？
  - 答题要点：定义阶段产物是 `BeanDefinition`（图在长）；创建阶段产物是“最终暴露对象”（可能被 BPP 代理/替换），入口是 `getBean`/`preInstantiateSingletons`。
- 遇到“Bean 根本没注册/条件没生效”，第一反应应该去 refresh 的哪一步？
  - 答题要点：优先看第 5 步（`invokeBeanFactoryPostProcessors`），尤其是 `ConfigurationClassPostProcessor`/条件装配相关后处理器是否执行、顺序是否正确。
- 遇到“注入不对/代理不生效/生命周期回调顺序怪”，第一反应应该去哪条链路？
  - 答题要点：优先走创建阶段：`AbstractBeanFactory#doGetBean` → `AbstractAutowireCapableBeanFactory#doCreateBean`（populate/initialize/BPP 链）。

## 源码与断点

- 主线入口：`org.springframework.context.support.AbstractApplicationContext#refresh`
- 定义阶段核心：`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- 规则装载核心：`org.springframework.context.support.PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- 创建阶段入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` / `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`
- 创建阶段主线：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`

## 最小可运行实验（Lab）

- 建议按“先容器后对象”的顺序跑（先把 refresh 的阶段感建立起来，再看 doCreateBean 五段式）：
  - `SpringCoreBeansBootstrapInternalsLabTest`
  - `SpringCoreBeansRegistryPostProcessorLabTest`
  - `SpringCoreBeansPostProcessorOrderingLabTest`
  - `SpringCoreBeansPreInstantiationLabTest`
  - `SpringCoreBeansBeanCreationTraceLabTest`
  - `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`
  - `SpringCoreBeansLifecycleCallbackOrderLabTest`
- 推荐命令：`mvn -pl :spring-core-beans test`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansPreInstantiationLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

上一章：[17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）](17-lifecycle-callback-order.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md)

<!-- BOOKIFY:END -->

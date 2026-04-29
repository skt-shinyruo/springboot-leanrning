# Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；真实项目里常见路径是：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。

    观察对象：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansLazyLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 起点：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 阅读路径：先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansLazyLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

懒加载经常被误用：很多人以为“加了 `@Lazy` 就不会启动慢了”，但实际效果取决于读者把 lazy 放在哪里。

### 回调与代理交织（什么时候才会触发生命周期回调）

- lazy-init 只是“延迟创建”，回调仍然发生在 **真正创建时**
- 注入点 `@Lazy` 注入的是 proxy，**回调不会在注入时发生**
- 只有当 proxy 首次触发真实解析时，`@PostConstruct/afterPropertiesSet` 等才会执行

## lazy-init bean：refresh 阶段不创建

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInitBean_isNotInstantiatedDuringRefresh_butCreatedOnFirstGetBean()`（证据：refresh 后构造器未调用，首次 getBean 才创建）

当 bean 定义是 lazy-init：

- refresh 阶段不会创建它
- 第一次 `getBean(...)` 才会创建

### 1.1 机制系统阐述：条件 → 分支 → 结果

**条件**：`mbd.isLazyInit()` 是否为 true
**分支**：`preInstantiateSingletons` 是否跳过
**结果**：
- lazy-init：启动期跳过，首次 `getBean` 才创建
- 非 lazy：启动期即创建
**断点入口**：`DefaultListableBeanFactory#preInstantiateSingletons`

## 关键反预期点：lazy-init 也挡不住“被别人依赖”

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInitDoesNotHelpIfAConsumerEagerlyDependsOnTheBean()`（证据：consumer 非 lazy 会强制创建依赖）

如果 A（非 lazy）依赖 B（lazy-init）：

- A 的创建需要 B
- 容器会为了创建 A 去创建 B

因此可以观察到：

- B 仍然在 refresh 阶段被创建

这能解释很多“明明标注了 lazy，但它仍在启动时创建”的问题。

## `@Lazy` 放在注入点：注入一个 proxy，而不是直接注入目标对象

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - `lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse()`（证据：注入的是 proxy，首次调用才触发目标 bean 创建）

当读者把 `@Lazy` 放在依赖注入点：

- 容器会注入一个 proxy
- proxy 在第一次真正调用时，再去容器里解析目标 bean

- 没有“外部因素”提前创建目标 bean
- 需要清晰观测到：目标 bean 的构造器是在“第一次调用”时才执行

---

## 补充：注入点 `@Lazy` 的内部实现（不是 lazy-init，而是“延迟解析代理”）

注入点 `@Lazy` 最容易被误判为“等同于 lazy-init”，但它的本质是：**在依赖解析阶段直接返回一个 proxy，把解析/创建目标对象推迟到首次调用**。

源码层面，它通常落在这样一条链路上（不要求背，但要能在断点里观察到）：

- `DefaultListableBeanFactory#doResolveDependency(...)` 解析注入点
- `AutowireCandidateResolver` 判断注入点是否带 `@Lazy`
- 典型实现：`ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary(...)`
  - 创建 `ProxyFactory`
  - 绑定 `LazyDependencyTargetSource`（内部通过 `ObjectFactory` 在首次调用时获取到真实 target）

**为什么这需要明确？**

- 它解释了：为什么注入结果可能是 proxy，以及为何 `@PostConstruct`/初始化回调会延后到“首次使用”才触发；
- 也解释了：为什么在未配置 lazy-init 的情况下，仍可能观察到“对象没有在启动期创建”。

**关联阅读：**

- 候选解析与注入点元数据：`wiring-dependency-injection-resolution.md`
- 代理替换发生点：`wiring-proxying-phase-bpp-wraps-bean.md`
- scope/prototype 与延迟获取：`wiring-scope-and-prototype.md`

## 可复现闭环（基于 `SpringCoreBeansLazyLabTest`）

运行完成该 Lab，至少需要复述 3 条结论：

1. **lazy-init 只影响预实例化**
   - 断点：`preInstantiateSingletons`
   - 断言：lazy bean 在 refresh 时不创建
2. **eager 依赖会提前触发创建**
   - 断点：`doResolveDependency`
   - 断言：非 lazy consumer 会触发 lazy bean 创建
3. **注入点 `@Lazy` 只是 proxy**
   - 断点：`getLazyResolutionProxyIfNecessary`
   - 断言：首次调用才触发真实 bean 创建

## 代理类型边界：接口注入点 vs 类注入点（必须会排障）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


这一段是很多人学 `@Lazy` 学不明白的关键原因：观察到的是 proxy，但 proxy 的“类型形态”并不总一样。

在本仓库的 Lab 中可以直接对照：

1. **接口注入点（JDK proxy）**
   - 对应实验：`SpringCoreBeansLazyLabTest#lazyInjectionPoint_canDeferCreationOfLazyBeanUntilFirstUse`
   - 现象：注入对象满足接口类型，但**不是具体实现类**（按实现类查找/强转会易错点）
2. **类注入点（CGLIB proxy）**
   - 对应实验：`SpringCoreBeansLazyLabTest#lazyInjectionPoint_onConcreteClass_usesClassBasedProxy_andDefersCreationUntilFirstUse`
   - 现象：注入对象是目标类的子类代理，通常仍可按具体类类型工作，但调试时类名会带 `$$` 等 proxy 痕迹

排障分流（先问自己这 2 个问题）：

- 读者是“按接口”注入/查找，还是“按实现类”注入/查找？
- 读者获取到的是 JDK proxy 还是 CGLIB proxy？（决定“类型边界”与“能不能强转”）

入口：

1. lazy 目标 bean 的构造器：作为“到底什么时候创建”的最直观观察点
2. `DefaultListableBeanFactory#preInstantiateSingletons`：在 refresh 期间观察 lazy-init bean 是否被跳过
3. `AbstractBeanFactory#doGetBean`：第一次按 name/type 取 bean 时触发创建的路径
4. `ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`：观察注入点 `@Lazy` 是如何变成 proxy 的
5. `DefaultListableBeanFactory#doResolveDependency`：在“consumer 依赖 lazy bean”的测试里观察为什么会提前创建

## 排障分流：这是定义层问题还是实例层问题？

- “已标注 lazy-init，但 bean 仍在启动时创建” → **优先实例层（依赖链）**：是否有非 lazy 的 consumer 直接依赖它？（本章第 2 节 + `doResolveDependency`）
- “在注入点添加 `@Lazy`，但仍然提前创建” → **优先实例层（proxy 触发点）**：是否调用了会触发真实解析的方法（如 `toString/equals`），或经由其他路径提前获取到了目标 bean？
- “误认为 `@Lazy` 会影响 beanDefinition 的 lazy-init” → **优先定义层澄清**：注入点 `@Lazy` 与 beanDefinition `lazy-init` 是两种语义（本章第 3 节）
- “看到的是 proxy 类型而不是目标类” → **实例层（代理语义）**：这是注入点 `@Lazy` 的本质（对照 [31](wiring-proxying-phase-bpp-wraps-bean.md)）

## 验证标准：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 常问：`lazy-init` 与注入点 `@Lazy` 有什么本质差别？
  - 答题要点：`lazy-init` 是定义层“延迟创建策略”；注入点 `@Lazy` 是“注入延迟解析 proxy”，把解析推迟到首次使用。
- 常见追问：为什么 lazy-init 仍可能在 refresh 时被创建？
  - 答题要点：被 eager 依赖/被提前触发（例如非 lazy 单例依赖它）时仍会创建；排障要找“是谁触发了依赖解析”。
- 常见追问：如何用断点证明“提前创建”是由依赖解析触发，而不是 lazy 失效？
  - 答题要点：在 `doResolveDependency` / `doGetBean` 加条件断点（beanName），观察创建链路的触发源。

## 面试常问（`@Lazy` 的两种语义）

- 常问：`lazy-init` 与注入点 `@Lazy` 有什么本质差别？
  - 答题要点：`lazy-init` 是定义层的“延迟创建策略”；注入点 `@Lazy` 更接近“注入一个延迟解析的代理/提供者”，把真正解析推迟到首次使用。
- 常见追问：为什么标了 lazy-init 仍可能在 refresh 时被创建？
  - 答题要点：被 eager 依赖/被提前触发（例如非 lazy 单例依赖它）时仍会创建；排障要找“谁触发了依赖解析”。

## 源码调用链（方法级）：lazy-init vs 注入点 `@Lazy`

### 1) lazy-init（定义层）：refresh 期间“跳过”，但挡不住被依赖

- refresh 批量创建单例入口：`DefaultListableBeanFactory#preInstantiateSingletons`
  - lazy-init bean 会被跳过（不主动创建）
- 首次取用触发创建：`AbstractBeanFactory#doGetBean(beanName)`
  - 进入 `doCreateBean` 完成实例化/注入/初始化
- 关键边界：只要有 eager consumer 依赖它，依赖解析就会提前触发 `getBean`
  - 证据链入口：`DefaultListableBeanFactory#doResolveDependency`

### 2) 注入点 `@Lazy`（实例层）：注入 proxy，把解析推迟到首次使用

- 注入解析入口：`DefaultListableBeanFactory#doResolveDependency`
- 生成 lazy proxy 的关键分支：`ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`
- 首次调用 proxy 时才触发真实解析：回到 `AbstractBeanFactory#doGetBean(beanName)`

> 结论：lazy-init 控制“容器是否主动创建”；注入点 `@Lazy` 控制“注入的是不是一个延迟解析的代理”。

## 边界分流：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）


### 误判点：不要把外层现象当成根因

- **误区 1：以为 `@Lazy` 能让所有依赖都不创建**
  - 如果目标 bean 不是 lazy-init，它仍可能在 refresh 阶段被 pre-instantiate。

- **误区 2：在 proxy 上调用 `toString()` / `equals()` 触发真实创建**
  - 学习阶段尽量不要依赖日志；用断言固定“构造器是否被调用”。

## 收束：Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- `DefaultListableBeanFactory#preInstantiateSingletons`：refresh 时批量创建非 lazy 单例（lazy-init bean 会被跳过）
- `AbstractBeanFactory#doGetBean`：第一次 `getBean(...)` 触发真正创建（lazy-init 的典型入口）
- `DefaultListableBeanFactory#doResolveDependency`：依赖解析入口（解释“lazy bean 仍可能因为被依赖而提前创建”）
- `ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`：注入点 `@Lazy` 的关键（决定是否注入一个懒代理）
- `AbstractAutowireCapableBeanFactory#createBean`：创建入口（对照“什么时候真的 new 出目标对象”）

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansLazyLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`

<!-- BOOKIFY:END -->

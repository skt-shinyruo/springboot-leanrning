# BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单 收敛原因。

    观察对象：39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBeanFactoryApiLabTest`。需要下探源码时，可以从 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `DefaultListableBeanFactory#doResolveDependency` / `AbstractBeanFactory#doGetBean` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 起点：BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界

- 阅读路径：先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章解决一个常见“源码阅读/排障”卡点：

> 明明使用的是 Spring（注解都已声明），为什么在某些启动方式/某些测试里注解不生效？
> 为什么 `DefaultListableBeanFactory` 表面上“很强”，但很多能力又像是缺失的？
> BeanFactory 到底是一套什么 API？哪些能力是它自带的，哪些是靠 post-processors “装上去”的？

一句话先讲清楚：

> **BeanFactory 是 Spring 容器的“最小内核 API”。观察到的大量“注解能力/自动行为”，本质上是 post-processors 在容器启动阶段把能力装配出来的。**

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：读者使用的是 plain `BeanFactory` 还是 `ApplicationContext`
**分支**：
- `BeanFactory`：不会自动执行 BFPP/BPP
- `ApplicationContext`：refresh 时自动 bootstrap 全套处理器
**结果**：前者“注解不生效/能力缺失”，后者“开箱即用”
**断点入口**：`PostProcessorRegistrationDelegate#registerBeanPostProcessors`

---

## 是什么：BeanFactory 在 Spring 体系里的位置

可以把 Spring 的容器能力拆成两层：

1. **容器内核（BeanFactory API）**：负责定义→实例化→依赖解析→生命周期基本骨架
2. **容器增强（post-processors + 上层设施）**：负责注解处理、AOP 代理、条件装配、占位符解析、事件/资源等

其中 BeanFactory 是第一层的核心接口，它解决的是：

- 通过 beanName / 类型获取到对象（`getBean`）
- 管理单例缓存与创建（内部 `doGetBean`/createBean 链路）
- 解析依赖（`doResolveDependency`）

而 `ApplicationContext` 是一个更“开箱即用”的上层抽象：

- 在 refresh 生命周期中 **自动发现并执行** BFPP/BPP
- 集成资源、事件、国际化等上层设施

---

## BeanFactory 接口族谱（在源码里看到的都从这里来）

无需背每个方法，但需要能把“遇到的 API”归类到下面这些角色：

一个会直接影响后续判断的事实：

---

- `@Autowired` field 没注入
- `@PostConstruct` 没执行
- `@Resource` 没生效
- `@Value("${...}")` 解析行为不符合预期

这不是因为 BeanFactory “不支持注解”，而是因为：

> 注解能力靠的是 BPP/BFPP，而 plain BeanFactory 不会像 ApplicationContext 那样自动发现并注册它们。

所以需要么：

1. 用 `ApplicationContext`（默认路径）
2. 或者读者明确知道自己在干什么：手动 bootstrap 必要的 post-processors

- plain BeanFactory：注解不生效
- 手动 addBeanPostProcessor：注解生效

---

## 最小容器边界：哪些能力来自 BeanFactory，哪些必须由 ApplicationContext 承接？

**BeanFactory 自带的最小能力**：

- `getBean` / 单例缓存 / 依赖解析 / 基础生命周期骨架

**ApplicationContext 额外提供的能力**（通过 refresh 统一装配）：

- BFPP/BPP 自动发现与执行（注解、AOP、条件装配、占位符解析）
- 事件、资源、国际化等上层设施

排障时必须先回答：**读者当前获取到的是哪一层？**

## 3.1 容器外对象三段能力：autowire / initialize / destroy

当手里有“非容器管理对象”时，`AutowireCapableBeanFactory` 提供三段能力：

1. `autowireBean`：只做依赖注入
2. `initializeBean`：触发初始化回调/BPP
3. `destroyBean`：触发销毁回调

**结论**：这三段能力彼此独立，调用顺序决定能拿到什么语义。

## 使用方式：在真实项目里会如何接触 BeanFactory？

### 4.1 作为框架/中间件作者（更常见）

可能会：

- 写一个 `BeanFactoryPostProcessor` / `BeanPostProcessor`
- 在其中获取到 `ConfigurableListableBeanFactory`
- 读取 BeanDefinition / 注册额外定义 / 修改属性 / 注册 value resolver 等

### 4.2 作为业务开发者（更少见，但排障常见）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- `DefaultListableBeanFactory#doResolveDependency`（注入失败/候选收敛）
- `AbstractBeanFactory#doGetBean`（循环依赖、FactoryBean、提前暴露）

以及在排障时用到：

- `getBean("&x")`（区分 FactoryBean 本体与 product）
- `getBeansOfType`（枚举候选）

---

### 5.1 BeanFactory 主线入口

观察点：

- `beanName`
- `mbd`（merged definition）
- `singletonObjects`（是否命中单例缓存）

### 5.2 “注解生效”的关键入口（BPP 视角）

最关键的观察点（watch）：

- `beanFactory.getBeanPostProcessors()`（或等价字段）：plain vs 手动 bootstrap 的差异
- 当前 bean 的注入元数据（是否解析到了 @Autowired 字段/方法）

### 5.3 “为什么 ApplicationContext 开箱即用”

若要把根因讲得更完整，可以对照阅读：

- [容器启动与基础设施处理器：为什么注解能工作？](internals-container-bootstrap-and-infrastructure.md)
- [容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](ioc-post-processors.md)

---

---

## 最小可运行实验（Lab）

本章引用的实验入口：
- Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

## 复现入口（可运行）

本章新增 Lab（先运行通过，再设置断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`

运行命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBeanFactoryApiLabTest test
```

强相关对照 Lab（已存在）：

- `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`（BeanFactory vs ApplicationContext 对比）
- `SpringCoreBeansBootstrapInternalsLabTest`（为什么注解能工作：基础设施处理器 bootstrap）

- `BeanFactory`：最小 getBean/类型判断 API
- `HierarchicalBeanFactory`：父子工厂可见性（与 Context hierarchy 相关）
- `ListableBeanFactory`：枚举能力（`getBeansOfType` / `getBeanNamesForType`）
- `AutowireCapableBeanFactory`：对“容器外对象”做注入/初始化（见 Part05 的对应章节/Lab）
- `ConfigurableBeanFactory`：更底层的配置能力（scope/aliases/value resolvers 等）
- `ConfigurableListableBeanFactory`：综合性最强（可枚举 + 可配置 + 可用于内部框架扩展）
- `BeanDefinitionRegistry`：定义层的注册/移除（BeanDefinition 的“仓库”）

> `DefaultListableBeanFactory` 基本上是这些接口的“集大成者”，所以在断点里大概率会看到它。

本章 Lab 就是用 “同一个 bean” 做对照：

可以在日志/断点里看到：

## Debug / 断点入口与观察点（把“注解为什么不生效”变成可证明结论）

断点入口（按“先装规则、再创建对象”的顺序运行一次）：

1. `AnnotationConfigUtils#registerAnnotationConfigProcessors`
   - 观察：它只是把处理器注册成 BeanDefinition（registry 里有了），并不会自动让注解生效
2. `DefaultListableBeanFactory#addBeanPostProcessor`
   - 观察：BPP 真正进入 `beanFactory.getBeanPostProcessors()` 的时机（这一步才是“注解能力被激活”的关键）
3. `AutowiredAnnotationBeanPostProcessor#postProcessProperties`
   - 观察：字段/参数注入是否发生（plain BeanFactory 未安装 BPP 时不会命中）
4. `CommonAnnotationBeanPostProcessor#postProcessProperties` / `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`
   - 观察：`@Resource/@PostConstruct` 等行为是否触发
5. `AbstractAutowireCapableBeanFactory#populateBean` / `#initializeBean`
   - 观察：创建链路是否走到注入/初始化阶段，以及 BPP 链路是否生效

## 边界分流：BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界

### 关键边界：plain BeanFactory 不会“自动让注解生效”

很多人第一次直接 new 一个 `DefaultListableBeanFactory` 会易错点：

### 误判点：不要把外层现象当成根因

1. **误区：BeanFactory = “更轻量，所以应该优先用”**
  - 轻量不等于稳妥。除非读者明确自己要控制哪些 post-processors，否则默认用 ApplicationContext。
2. **误区：注册了 `ConfigurationClassPostProcessor` 这个 bean，就等于注解能工作**
   - 不够：读者还需要“执行/注册”整套基础设施链路（ApplicationContext refresh 会做，plain BeanFactory 不会自动做）。
3. **误区：只要加了 BPP，就能让以前创建过的 bean 也被处理**
   - BPP 通常不 retroactive。顺序与时机是排障关键点。

## 面试常问（BeanFactory vs ApplicationContext：差异与边界）

### Q1：`BeanFactory` 和 `ApplicationContext` 的关键差异是什么？为什么 plain BeanFactory 下“注解不生效”？

- 标准答案（可复述）：
  - `ApplicationContext` 会在 refresh 主线里自动完成 BFPP/BDRPP/BPP 的 bootstrap（包括注册并激活注解处理器）；plain `DefaultListableBeanFactory` 只是内核，不会自动装这些基础设施，因此很多注解行为没有触发者。
- 证据链（方法级）：
  - 注册处理器（定义层）：`AnnotationConfigUtils#registerAnnotationConfigProcessors`
  - 激活处理器（实例层）：`DefaultListableBeanFactory#addBeanPostProcessor`
  - 注入触发点：`AutowiredAnnotationBeanPostProcessor#postProcessProperties`
- 最小复现：
  - `SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`

### Q2：为什么“已注册处理器 BeanDefinition”，注解仍不生效？

- 标准答案（可复述）：
  - 因为“注册定义”不等于“处理器已加入 BPP 执行链”。只有处理器实例进入 `beanFactory.getBeanPostProcessors()`，创建链路才会在对应钩子点回调它们。
- 证据链（方法级）：
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（ApplicationContext 场景）
  - `DefaultListableBeanFactory#addBeanPostProcessor`（plain BeanFactory 手动激活）

## 验证标准：BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界
- 需要解释清楚：为什么 `AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory)` “表面上装了处理器”，但注解仍然不生效吗？
- 需要指出：在 plain BeanFactory 场景里，“让注解生效”的最小动作是什么？（提示：不是 refresh，而是把处理器实例加进 BPP 列表）
- 需要用断点证明：`@Autowired` 的发生点在 `populateBean` 的哪个钩子里吗？（提示：`AutowiredAnnotationBeanPostProcessor#postProcessProperties`）

## 收束：BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界

- `AbstractBeanFactory#doGetBean`（拿 bean 的主入口）
- `AbstractAutowireCapableBeanFactory#doCreateBean`（实例化/填充/初始化）
- `DefaultListableBeanFactory#doResolveDependency`（依赖解析/候选收敛）

- `AbstractAutowireCapableBeanFactory#populateBean`（准备进入属性填充）
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（@Autowired/@Value 等注解注入）
- `CommonAnnotationBeanPostProcessor#postProcessProperties`（@Resource/@PostConstruct 等）

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanFactoryApiLabTest.java`

<!-- BOOKIFY:END -->

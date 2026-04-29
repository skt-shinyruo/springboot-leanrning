# dependsOn：强制初始化顺序（即使没有显式依赖）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：dependsOn：强制初始化顺序（即使没有显式依赖）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansDependsOnLabTest`。需要下探源码时，可以从 `AbstractBeanFactory#doGetBean` / `AbstractApplicationContext#refresh` / `DefaultListableBeanFactory#preInstantiateSingletons` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：`dependsOn` 管顺序，不管注入

这章解决两个高频误判。第一，把 `dependsOn` 当成注入依赖；实际它只管初始化和销毁顺序。第二，把循环 depends-on 当成三级缓存循环依赖；实际这是定义层拓扑环，遇环会直接失败。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansDependsOnLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`

## 机制主线：它解决的是“顺序”，不是“注入”

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

可以把 `dependsOn` 当成一句话：

> **创建 A 之前，先确保 B 已经创建；销毁时反过来，先销毁 A 再销毁 B。**

它经常被用来表达“**启动顺序**/ **资源初始化顺序**”：

- 例如：一个 bean 负责初始化线程池/metrics exporter；另一个 bean 虽然不注入它，但必须在它之后启动。
- 例如：某个 bean 的 `init` 逻辑依赖“某个外部资源已就绪”，而资源初始化不一定通过 DI 表达（此时显式 DI 更合适，但有时历史包袱会逼读者用 dependsOn）。

> ⚠️ 处理原则：把 `dependsOn` 视为“最后手段”。能用显式依赖（构造注入/方法参数注入）就不要用它，因为**显式依赖更可维护、也更能被 IDE/测试/重构工具捕获**。

### 机制边界：条件、分支与结果

**条件**：`mbd.getDependsOn()` 是否为空
**分支**：`AbstractBeanFactory#doGetBean` 先 `getBean(dep)` 再创建自身
**结果**：
- 只影响创建/销毁顺序
- 不改变候选选择与注入规则
**断点入口**：`AbstractBeanFactory#doGetBean`

## 方法级入口：dependsOn 在哪一步生效？

`dependsOn` 的读取点固定：**bean 创建入口 `doGetBean`**。
它既可能发生在容器启动预实例化（pre-instantiation），也可能发生在运行期第一次 `getBean(...)`。

### 1.1 启动期（单例预实例化）主链路

最常见链路（忽略细枝末节，只保留需要断点的主干）：

1. `AbstractApplicationContext#refresh`
2. `finishBeanFactoryInitialization`
3. `DefaultListableBeanFactory#preInstantiateSingletons`
4. `getBean(beanName)`
5. `AbstractBeanFactory#doGetBean`
6. **处理 `mbd.getDependsOn()`**
7. `createBean` / `doCreateBean`（进入实例化/填充/初始化/后置处理器）

### 1.2 运行期（按需创建）主链路

如果 bean A 本身是 lazy-init 或者根本不是单例预实例化覆盖范围（例如 prototype），那么直到读者第一次触发：

- `BeanFactory#getBean("A")` 或者某个组件首次注入/访问到它

仍然会进入 `AbstractBeanFactory#doGetBean`，因此 **dependsOn 在“启动期预实例化”和“运行期按需创建”两种模式下生效逻辑完全一致**。

## 写法入口：`@DependsOn` / `BeanDefinition#setDependsOn(...)` / XML `depends-on`

`dependsOn` 只有一个“输入形态”：**一组 beanName**（注意不是 type）。

常见入口：

- `@DependsOn({"beanB", "beanC"})`
- `BeanDefinition#setDependsOn("beanB", "beanC")`（编程式注册定义时）
- XML：`<bean id="a" depends-on="beanB,beanC" .../>`

### 2.1 一个重要细节：它匹配的是“名字”，不是“类型”

因此可以遇到两个典型现象：

- 写错名字：创建 A 时才会抛 `NoSuchBeanDefinitionException`（因为直到要创建 A 才触发 dependsOn）
- 容易误以为是按类型：但 `dependsOn="dataSource"` 只会命中 beanName 为 `dataSource` 的那个定义，跟 `DataSource.class` 没半毛钱关系

## 容器内部结构：两张依赖图怎么读？

Spring 会把 `dependsOn` 这条关系写进 `DefaultSingletonBeanRegistry` 的两张表：

- `dependentBeanMap`：**dependency → dependentBeans**
  可以把它理解为“dependency 被哪些 bean 依赖”，因此它是关闭容器时计算销毁顺序的重要输入。
- `dependenciesForBeanMap`：**bean → dependencies**
  可以把它理解为“bean 依赖哪些 dependency”，其定位更接近是“正向邻接表”，排障时也常用。

常用观察入口（直接在 debugger 里看）：

- `DefaultSingletonBeanRegistry#getDependentBeans(beanName)`
- `DefaultSingletonBeanRegistry#getDependenciesForBean(beanName)`

> 这两张表不只记录 `dependsOn`，也记录容器运行中形成的“依赖关系”（例如注入触发的依赖记录）。
> 因此在排障时要先问：**这条边是“定义层写死”的 dependsOn，还是“实例层注入/创建”过程中记录的依赖？**

## 销毁顺序：为什么关闭时顺序“反过来”？

规则一句话：

> **销毁时先销毁 dependent，再销毁 dependency。**

预期解释：如果 A 依赖 B，那么在 A 的销毁回调（`DisposableBean#destroy` / `@PreDestroy`）里可能仍然会用到 B；
因此必须先销毁 A，再销毁 B。

方法级落点（若希望断点就盯这些）：

- `DefaultSingletonBeanRegistry#destroySingletons`（遍历单例并触发销毁）
- `DefaultSingletonBeanRegistry#destroyBean`（递归销毁 dependent）
- `DisposableBeanAdapter#destroy`（真正执行销毁回调：`@PreDestroy` / `DisposableBean` / destroy-method）

## dependsOn vs SmartLifecycle phase：什么时候用哪一个？

- **dependsOn**：表达“初始化/销毁顺序”，适合基础设施/资源就绪顺序
- **SmartLifecycle phase**：表达“启动/停止阶段顺序”，适合需要 start/stop 语义的组件
如果需要严格的 start/stop 控制，优先用 **SmartLifecycle**；只有在“必须强制初始化顺序但没有显式依赖”时考虑 dependsOn。

## 父子容器边界（层级 context 下的依赖解析）

- dependsOn 只在 **当前 BeanFactory** 范围内生效
- 子容器可见父容器 bean，但父容器不可见子容器
- 跨 context 的 dependsOn 容易出现“名字存在但不可见”的误判

## 交互：dependsOn 会强行拉起 lazy-init 吗？

结论很“硬”：

- **会。**因为 `dependsOn` 的实现方式就是“在创建 A 前先 `getBean(dep)`”，这等价于人为触发一次依赖的创建。

这里经常混淆两种 lazy：

| 场景 | 编写的是什么 | 延迟的对象是谁 | dependsOn 是否会拉起？ |
| --- | --- | --- | --- |
| `lazyInit=true` / `@Lazy` 在 bean 定义上 | `@Lazy` class / `@Bean` | **bean 自己**是否预实例化 | ✅ 会（被 `getBean(dep)` 拉起） |
| `@Lazy` 在注入点上 | `@Autowired @Lazy` | 注入的是**代理**，目标 bean 可能延后创建 | ✅ 仍会（dependsOn 不看注入点） |

> 实务取舍：若想要“注入不拉起、首次使用才拉起”，用 **注入点 `@Lazy` / `ObjectProvider`**。
> 若想要“即使没显式依赖也必须先初始化”，才考虑 `dependsOn`。

## 机制边界：dependsOn 解决不了哪些问题？

请把下面这句背下来（面试/排障都能用）：

> **dependsOn 管顺序，不管注入；管 beanName，不管 type；管创建/销毁，不管运行时调用。**

因此它解决不了：

- **候选选择**问题：不会影响 `@Primary` / `@Qualifier` / `@Order` 的规则（见 [候选选择与优先级](wiring-autowire-candidate-selection-primary-priority-order.md)）
- **循环依赖**问题：`dependsOn` 的环不是三级缓存能救的（它会 fail-fast）
- **AOP 自调用**问题：代理是否生效与 dependsOn 无关（见 [代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）](wiring-proxying-phase-bpp-wraps-bean.md)）
- **BeanPostProcessor 顺序**问题：处理器顺序由 ordering 体系决定，而不是 dependsOn（见 [顺序（Ordering）：PriorityOrdered / Ordered / 无序](internals-post-processor-ordering.md)）

## 可复现闭环（基于 `SpringCoreBeansDependsOnLabTest`）

运行完成该 Lab，至少需要复述 3 条结论：

1. **dependsOn 只影响顺序**
   - 断点：`doGetBean` → `mbd.getDependsOn()`
   - 断言：依赖先创建，注入规则不变
2. **dependsOn 会拉起 lazy-init**
   - 断点：`preInstantiateSingletons`
   - 断言：lazy bean 被强制创建
3. **依赖图可复盘**
   - 断点：`registerDependentBean`
   - 断言：依赖边记录在 `dependentBeanMap`

## 排障决策表（初始化/关闭/异常消息 → 证据链）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象/异常 | 最可能原因 | 证据链（方法级） | 修复策略 |
| --- | --- | --- | --- |
| B 明明 lazy-init，但启动时就被创建了 | 有人对 B 写了 dependsOn（直接或间接） | `AbstractBeanFactory#doGetBean` → 读取 `mbd.getDependsOn()` → `getBean(B)` | 去掉 `dependsOn`；或者把“顺序依赖”改成显式 DI；或者用注入点 `@Lazy` / `ObjectProvider` |
| 创建 A 时抛 `NoSuchBeanDefinitionException: No bean named 'xxx'` | `dependsOn` 写错了 beanName（或 alias 未生效） | `doGetBean(A)` → 遍历 dependsOn → `getBean("xxx")` 抛错 | 修正 beanName/alias；避免把 type 当成名字写进去 |
| 异常包含 `Circular depends-on relationship` | 人为写了 `dependsOn A -> B -> A` 的拓扑环 | `doGetBean` → `isDependent` 检测环 → fail-fast | 打断环；不要误判为“循环依赖/三级缓存” |
| 关闭容器时销毁顺序“反预期” | 读者真正写的是“依赖关系”，销毁必须逆序 | `destroySingletons` → `destroyBean` 递归销毁 dependent | 把资源释放逻辑放到正确的 bean；必要时重新设计生命周期（`SmartLifecycle`/phase） |

## 断点闭环（照着走一次）

### 8.1 断点入口（按收益排序）

1. `AbstractBeanFactory#doGetBean`：定位 `mbd.getDependsOn()` 的处理分支（这是本章“证据链”的核心）
2. `DefaultSingletonBeanRegistry#registerDependentBean`：确认依赖边是如何写入两张表的
3. `DefaultSingletonBeanRegistry#isDependent`：定位 “Circular depends-on relationship” 的 fail-fast 点
4. `DefaultSingletonBeanRegistry#destroySingletons`：观察关闭阶段销毁的触发顺序

### 8.2 固定观察点（观察清单）

- `beanName` / `dependentBeanName`
- `mbd.getDependsOn()`
- `dependentBeanMap` / `dependenciesForBeanMap`
- `getDependentBeans(beanName)` / `getDependenciesForBean(beanName)`

## 面试常问（标准答案 + 方法级证据链）

### Q1：`@DependsOn` 到底解决什么问题？它和 `@Autowired` 有什么区别？

- 标准答案：`@DependsOn` 只解决 **初始化/销毁顺序**，不会参与注入；`@Autowired` 解决的是 **依赖解析与注入**。
- 方法级证据链：`@DependsOn` 在 `AbstractBeanFactory#doGetBean` 读取 `mbd.getDependsOn()` 并先 `getBean(dep)`；注入则主要经由 `AutowiredAnnotationBeanPostProcessor#postProcessProperties` → `DefaultListableBeanFactory#doResolveDependency`（见第 14/33 章）。

### Q2：为什么 dependsOn 会让 lazy-init 失效？

- 标准答案：因为 dependsOn 的实现方式就是“创建 A 之前显式 `getBean(dep)`”，调用方主动触发了依赖的创建。
- 方法级证据链：`doGetBean(A)` → 遍历 dependsOn → `getBean(dep)`。

### Q3：`Circular depends-on relationship` 算不算“循环依赖”？三级缓存能不能救？

- 标准答案：它是 **定义层的拓扑环**，不是“实例层早期引用”问题；三级缓存救不了，Spring 会 fail-fast。
- 方法级证据链：`doGetBean` → `isDependent` 检测到图里已有反向边 → 直接抛异常。

## 验收口径：dependsOn：强制初始化顺序（即使没有显式依赖）
`dependsOn` = **BeanDefinition 里的 beanName 列表**；生效点在 `doGetBean`；影响创建/销毁顺序与依赖图记录，不影响候选选择与注入。

## 小结：dependsOn：强制初始化顺序（即使没有显式依赖）

- 本章完成后：请把 `dependsOn` 和 “注入依赖/循环依赖/后处理器顺序”明确分家；排障时优先用方法级证据链判定问题属于**定义层**还是**实例层**。
- 下一章将讲 “能注入但不是 Bean”：`registerResolvableDependency`，它经常与 `*Aware` 混淆，但两者的生效点完全不同。


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansDependsOnLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`

<!-- BOOKIFY:END -->

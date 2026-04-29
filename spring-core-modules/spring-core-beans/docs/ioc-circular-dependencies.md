# 循环依赖：现象、原因与规避（constructor vs setter）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：循环依赖：现象、原因与规避（constructor vs setter）。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansContainerLabTest`。需要下探源码时，可以从 `ConstructorResolver#autowireConstructor` / `AbstractAutowireCapableBeanFactory#populateBean` / `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：同样是相互依赖，为什么有的直接失败

循环依赖不能先问“Spring 能不能救”，而要先分型：依赖发生在构造器阶段，还是发生在属性填充阶段。constructor 循环通常没有提前暴露窗口；setter/field 循环只有在 singleton 创建窗口期才可能闭合。

先运行“constructor fail-fast vs setter 可能成功”的最小实验，再带着断点把“为什么能救/为什么救不了”的证据链走通。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
    - 测试文件：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

## 机制主线：为什么 constructor 死、setter 有时能活？

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

把循环依赖先分成两类（这是排障时最省脑的第一步）：

1. **构造器循环依赖（constructor cycle）**：依赖发生在“实例化之前” → 容器没有 early exposure 的窗口 → 典型 fail-fast
2. **属性/Setter 循环依赖（setter/field cycle）**：依赖发生在“实例已创建、但尚未完成初始化”的窗口期 → 容器可能提前暴露一个引用 → 有机会使依赖环得以闭合

这也是为什么同样是“相互依赖”，表现会完全不同。

### 机制边界：constructor vs setter 的条件、分支与结果

- **条件**：依赖发生在“实例化前”还是“实例化后但初始化前”
- **分支**：
  - constructor 依赖 → `ConstructorResolver#autowireConstructor` 触发
  - setter/field 依赖 → `populateBean` 触发
- **结果**：
  - constructor cycle：没有 early exposure 窗口 → fail-fast
  - setter cycle：可能命中 early reference → 有时可救
- **断点入口**：`ConstructorResolver#autowireConstructor` / `AbstractAutowireCapableBeanFactory#populateBean`

---

## 将现象固化为断言（避免主观推断）

先运行完下面两类现象，保证能“用测试复现”，再进入源码断点：

- constructor cycle（fail-fast）：`SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`
- setter cycle（可能成功）：`SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`

若希望将“打断 constructor 环”的工程手段一并验证（而非仅停留在概念记忆），可再运行：

- `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaLazyInjectionPointProxy`
- `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaObjectProvider`

> 这里的重点是：constructor 环“不是靠三级缓存救”，而是靠“延迟获取依赖”改变时机；这属于工程层面的折中，代价要读者自己承担。

### 1.1 循环依赖类型速查（含 fail-fast 点）

| 类型 | 是否可能被“救活” | fail-fast 点 | 备注 |
| --- | --- | --- | --- |
| constructor ↔ constructor | 几乎不行 | `autowireConstructor` | 没有 early exposure 窗口 |
| setter/field ↔ setter/field（singleton） | 可能 | `getSingleton(..., allowEarlyReference=true)` | 依赖 early reference |
| prototype ↔ prototype | 不行 | `isPrototypeCurrentlyInCreation` | prototype 不进入单例缓存 |
| dependsOn 形成的环 | 不行 | `AbstractBeanFactory#checkDependencies` | 强制初始化顺序，遇环直接失败 |

---

## 1.2 为什么读者看完仍不懂“为什么要三级缓存”？（桥接：2-level vs 3-level）

> 若读者当前的核心困惑为“为什么不是二级缓存就够”，可先参阅：
> - [`00. Why Index（基础问题索引）`](guide-why-index.md)（结论 + 10 分钟证据链）
> - AOP 前置理解：[01. AOP：代理（Proxy）+ 入口（Call Path）](../../spring-core-aop/docs/proxy-fundamentals-aop-proxy-mental-model.md)（为什么要跳：本章后面会用 raw vs proxy / early vs final 来解释“到底救没救”；验证什么：在 AOP 章先跑通一个最小 proxy 用例，并在“proxy 创建点 + 调用入口”各停一次，确认观察到的是“代理对象 + 调用路径”而不是“原始实例”）

读者之所以会在“三级缓存”这里卡住，通常是因为把它误当成“多一个 Map 的实现细节”，而忽略了它在设计上解决的是两个更本质的问题：

1. **只在真的需要 early reference 时才创建它（按需/延迟）**
2. **让 early reference 的形态（raw vs proxy）可被 BPP/AOP 决策，并尽量做到 early == final（一致性）**

把它压缩成一句话：

> 二级缓存只能缓存“对象”；三级缓存额外缓存了“按需生成 early reference 的能力（ObjectFactory）”，从而把“创建时机 + 形态决策”固定在可控窗口里。

### 二级 vs 三级：差别不在“多一层”，而在“什么时候做决定”

| 方案 | 可存储内容 | 会遇到的典型问题 | 为何常在 AOP/代理处受阻 |
| --- | --- | --- | --- |
| 2-level（final + early） | 只能存对象（raw 或 proxy） | 要么“所有 bean 都提前生成 early 引用/early proxy”（不必要成本），要么“先放 raw，后面再换成 proxy”（raw 注入绕过代理/一致性失败） | proxy/wrapper 的生成点本来就在 BPP 链上；如果 early 阶段交出去的是 raw，而 final 阶段变成 proxy，就出现 early ≠ final |
| 3-level（final + early + factory） | 既能存对象，也能存“生成对象的工厂（ObjectFactory）” | 只有真正出现循环注入、确实需要 early 引用时才创建；并且创建时会走 `getEarlyBeanReference`，尽量让 early 与 final 对齐 | factory 把“是否需要 early / early 形态是什么”延迟到需求出现的那一刻，让 BPP/AOP 在正确窗口介入 |

如需将“二级 vs 三级”的论证推进到方法级证据链，下一章可参阅：

- [`16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？`](internals-early-reference-and-circular.md)（把 raw vs wrapped 与一致性保护系统阐述）
- [`31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy`](wiring-proxying-phase-bpp-wraps-bean.md)（把“最终暴露对象可能变化”的容器视角系统阐述）

## 三层缓存的真实语义：final / early / factory

循环依赖相关的缓存都在 `DefaultSingletonBeanRegistry` 里。无需背字段名，但必须能把三类语义对上：

- `singletonObjects`：一级缓存，**final**（完全初始化完成后对外暴露的单例）
- `earlySingletonObjects`：二级缓存，**early**（循环依赖窗口期暴露的引用，可能是 raw，也可能是 proxy）
- `singletonFactories`：三级缓存，**factory**（一个 `ObjectFactory`，用于“按需生成 early reference”）

这套设计解决的是一个具体的问题：

> 当 A 在创建中（还没初始化完）但 B 需要注入 A 时，容器能不能给 B 一个“暂时可用”的 A 引用？

它的答案是：**可以，但只能在 singleton 的特定窗口期，并且通过 factory 延迟决定 early reference 的形态**。

### 2.1 early reference 的生成链路（SmartInstantiationAwareBPP 介入）

- 入口：`AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
- 扩展点：`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
- 关键影响：代理通常在这里介入，决定 early 引用与最终暴露对象是否一致

### 2.2 两个关键开关：`allowCircularReferences` vs `allowRawInjectionDespiteWrapping`

把“循环依赖能不能救”说清楚，需要把 **机制（三级缓存）** 与 **策略开关（容器态度）** 分开。

1. `DefaultListableBeanFactory#setAllowCircularReferences(boolean)`（简称 `allowCircularReferences`）
   - 作用：是否允许 singleton 在创建窗口期做 early exposure（注册 `singletonFactory` 到三级缓存）。
   - 影响结果：
     - `true`：setter/field cycle **可能**被救活（取决于是否真的触发 `getSingleton(..., allowEarlyReference=true)`）
     - `false`：即便是 setter/field cycle，也会更倾向 **fail-fast**（因为“救援窗口”被直接关掉）

2. `AbstractAutowireCapableBeanFactory#setAllowRawInjectionDespiteWrapping(boolean)`（简称 `allowRawInjectionDespiteWrapping`）
   - 作用：当容器发现 **依赖方已经注入了 raw early reference**，但当前 bean 在初始化阶段又被 after-init BPP 包装成 proxy/wrapper 时，是否允许继续启动。
   - 影响结果：
     - `false`：**一致性保护（工程默认）**。容器会尽量保证 early == final：优先让 early reference 与最终暴露对象保持一致（例如让 early 也返回 proxy）；若无法做到一致，则可能 **fail-fast（信息包含 *raw version*）**。
     - `true`：允许继续启动，但代价是：**一部分依赖方获取到的对象形态与容器最终暴露形态不一致**（风险较高，属于“能够启动但不可靠”）。

> 这两个开关解决的问题不同：
> - `allowCircularReferences` 决定“救不救”（有没有 early exposure 窗口）
> - `allowRawInjectionDespiteWrapping` 决定“救活后是否可控”（是否允许 raw 注入绕过最终包装）

---

## 关键窗口期：early exposure 发生在 `doCreateBean` 的哪一步？

把 `doCreateBean` 只看成 4 句话（足够读者对照断点理解）：

1. **实例化**：先创建 bean 实例（此时尚无属性注入，也无初始化回调）
2. **（可选）提前暴露**：如果允许循环依赖，注册一个 `singletonFactory` 到三级缓存（还没产生 early object）
3. **属性填充**：开始解析依赖并注入（setter/field 注入在这里发生）
4. **初始化**：执行 Aware/@PostConstruct/afterPropertiesSet/initMethod，以及 after-init BPP 可能返回 proxy，然后把 final 对象放入一级缓存

setter cycle 之所以“可能成功”，就在于第 2 步留下的窗口：**B 在创建时请求 A，`getSingleton(..., allowEarlyReference=true)` 可以从 factory 里获取到一个 early reference**。

constructor cycle 之所以“基本无解”，就在于构造器依赖发生在第 1 步之前：**连实例都还没 new 出来，谈不上提前暴露**。

---

## 断点闭环：从 `getSingleton` 看清“到底救没救”

至少运行一次“观察缓存变化”的断点闭环（完成一次验证后，可避免再次被“三级缓存神话”误导）。

### 4.1 断点入口（按收益排序）

1. `DefaultSingletonBeanRegistry#getSingleton`：看三层命中分支（final/early/factory）
2. `DefaultSingletonBeanRegistry#addSingletonFactory`：看“何时把 factory 放进三级缓存”（early exposure 的起点）
3. `AbstractAutowireCapableBeanFactory#doCreateBean`：看 earlySingletonExposure 的判定与创建窗口
4. `AbstractAutowireCapableBeanFactory#populateBean`：看 setter/field 注入发生的时机

### 4.2 固定观察点（观察清单）

在 `getSingleton(beanName, allowEarlyReference)` 里只盯这些即可：

- `isSingletonCurrentlyInCreation(beanName)`：是否处于创建中（决定 early 分支是否可能发生）
- `singletonObjects.containsKey(beanName)`：final 是否已产生
- `earlySingletonObjects.containsKey(beanName)`：是否已有 early 引用
- `singletonFactories.containsKey(beanName)`：是否存在 early 工厂（允许“按需创建”）

在 `doCreateBean(beanName, ...)` 里盯：

- `earlySingletonExposure`：是否允许 early exposure（关键开关）
- `exposedObject`：最终对外暴露对象（可能被 after-init BPP 替换成 proxy）
- `earlySingletonReference`：early 引用（出现循环依赖时常能看到它）

### 4.3 需要复述的“证据链”

当读者运行 setter cycle 并在断点里看到下面这条链，可认为已形成可验证的理解闭环：

1. 创建 A：`doCreateBean("a")` → `addSingletonFactory("a", ...)`（三级缓存出现工厂）
2. 创建 B：注入时需要 A → `getSingleton("a", allowEarlyReference=true)`
3. `getSingleton` 发现 A “in creation” 且存在 factory → 调用 factory 生成 early reference
4. B 获取到 early reference 完成创建 → 回到 A 的 populate/initialize → 最终对象进入 `singletonObjects`

> 提醒：这一章到这里为止即可。若希望进一步厘清“early reference 应该是 raw 还是 proxy”“raw vs wrapped 不一致为何会 fail-fast”，请去看 [early reference 与循环依赖](internals-early-reference-and-circular.md)（那一章专门讲这个误区）。

### 4.4 异常 → 断点入口速查（把“看异常”变成“可定位”）

循环依赖相关异常，读者最容易掉进“看到一串 BeanCreationException 就开始猜”的陷阱。用下面的“异常 → 入口方法”来做第一跳定位：

1. `BeanCurrentlyInCreationException`（典型信息包含 *currently in creation*）
   - 含义：某个 bean 处于创建中，又被再次请求（环路信号，或 early reference 一致性校验失败）。
   - 第一断点：`DefaultSingletonBeanRegistry#beforeSingletonCreation` / `getSingleton`
   - 第二断点：`AbstractAutowireCapableBeanFactory#doCreateBean`（看 `earlySingletonExposure` / `exposedObject`）

2. `BeanCurrentlyInCreationException` 且信息包含 *raw version*（这是判断 raw/proxy 形态不一致的第一线索）
   - 含义：依赖方获取到了 raw early reference，但最终对象被 BPP 包装（proxy/wrapper），触发了 raw vs wrapped 不一致保护。
   - 第一断点：`AbstractAutowireCapableBeanFactory#doCreateBean`（尾部一致性检查区域）
   - 重点检查：`allowRawInjectionDespiteWrapping` 开关、`earlySingletonReference` 与 `exposedObject` 是否不同
  - 备注：若未观察到 *raw version* 异常信息，也不代表“没有风险”；也可能是容器通过 early proxy 等方式将 early 与 final 对齐。仍要运行本章 Lab 以确认实际注入形态

3. `BeanCreationException` / `UnsatisfiedDependencyException`（外层包装）
   - 含义：真正的环路通常藏在 root cause（`getRootCause()`）里。
  - 先关注 root cause 的异常类型与信息，再回到上面两类路径定位。

---

## 排障配方：如何定位“环路边”并选择打断手段
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


1. **优先定位异常根因（root cause）**：`BeanCurrentlyInCreationException` 往往是内因
2. **锁定环路边**：
   - 断点：`DefaultSingletonBeanRegistry#beforeSingletonCreation`
   - 观察：`dependentBeanMap` / `dependenciesForBeanMap`（谁依赖谁）
3. **判断类型**：constructor / setter / prototype / dependsOn
4. **选择手段**：
   - `@Lazy`：引入代理，延后依赖获取
   - `ObjectProvider`：按需获取（更清晰、可测试）
   - **重构**：拆依赖/引入中介（长期最优）

## Framework vs Boot：策略差异（避免以“能够启动”作为正确性的唯一判据）

在纯 Spring Framework 容器里，循环依赖策略通常更“宽松”；在 Spring Boot 里，启动过程往往会更倾向 fail-fast，并提供配置开关（例如 `spring.main.allow-circular-references`）。

一个足够实用的记忆法是：

- Spring Framework（纯容器）更偏“机制默认可用”（通常默认允许循环依赖救援）
- Spring Boot（工程默认）更偏“安全默认”（Boot 2.6+ 默认倾向禁用，需要显式开启）

把它理解成“一条策略映射链”，避免只记一个配置项：

- Boot 配置（`spring.main.allow-circular-references`）
  → `SpringApplication` 上的 allow-circular-references 策略
  → `DefaultListableBeanFactory#setAllowCircularReferences(...)`

学习阶段可以在不同容器之间切换来观察差异，但工程上请牢记：

- “容器能救活”只是机制的副作用，不是读者设计循环依赖的理由
- 依赖图是系统复杂度的真实来源：越早消环越省成本

---

## 工程处理策略（按优先级）

### 6.1 首选：重构消环（把依赖图改对）

常见做法：

- 把双向依赖拆成单向：抽出第三方组件/接口（例如把“协调逻辑”下沉到 `Orchestrator`）
- 引入事件/回调解耦（见 `spring-core-events` 模块）
- 把“需要对方”变成“需要对方的能力”（接口化 + 依赖倒置）

### 6.2 次选：延迟依赖打断环（了解代价后再用）

- `ObjectProvider<T>`：把“构造时必须获取到”改成“用到时再拿”（依赖关系变成运行时分支）
- `@Lazy`（注入点代理）：把依赖解析延迟到第一次使用（多一层代理/调试复杂度上升）

### 6.3 不作为默认选择：为了启动而把所有依赖改 setter

setter 注入能够“使依赖环得以闭合”的前提是：需要接受半初始化窗口以及更隐蔽的运行时问题。学习阶段可用于理解机制；工程实践中通常不作为默认选择。

## 可复现闭环（基于 `SpringCoreBeansCircularDependencyBoundaryLabTest`）

运行完成这些用例，需要明确 3 个结论：

1. **constructor cycle 直接 fail-fast**
   - 断点：`ConstructorResolver#autowireConstructor`
   - 断言：启动失败 + `BeanCurrentlyInCreationException`
2. **`@Lazy`/`ObjectProvider` 可以打断 constructor 环**
   - 断点：`getObject()` / `ObjectFactory#getObject()`
   - 断言：依赖被延迟获取后启动成功
3. **setter cycle 的“可救”来自 early exposure**
   - 断点：`addSingletonFactory` → `getSingleton(..., allowEarlyReference=true)`
   - 断言：early 引用命中，环被临时打通

4. **禁用 `allowCircularReferences` 后，setter/field cycle 也应 fail-fast**
   - 断点：`doCreateBean`（观察 `earlySingletonExposure` 分支不再成立）
   - 断言：fail-fast + root cause 为 `BeanCurrentlyInCreationException`

5. **“能救 ≠ 安全”：allowRawInjectionDespiteWrapping 的两种结果**
   - 当 `allowRawInjectionDespiteWrapping=false`：容器会保护一致性（优先 early==final；如果做不到一致则 fail-fast）
  - 当 `allowRawInjectionDespiteWrapping=true`：可能启动成功，但应能观察到“依赖方持有 raw、容器对外暴露 proxy”的不一致（属于风险演示，不是工程默认做法）

---

## 边界：能启动不等于依赖图健康

1. **误区：Spring 解决了循环依赖**
  - 更准确的说法：Spring 只在特定条件下“救活某些环”（singleton + early exposure + 合适的增强/代理策略）。
2. **误区：constructor 环就一定无解**
   - “能启动”并不等于“无代价”：`@Lazy/ObjectProvider` 的本质是改变时机，并引入代理/分支复杂度。
3. **误区：只看启动成功，不看对象形态一致性**
   - 一旦 AOP/代理介入，early 与 final 形态不一致会让问题更隐蔽；这部分请看 [16](internals-early-reference-and-circular.md)。

---

## 面试常问（循环依赖）

1. **constructor cycle 为什么基本 fail-fast？setter cycle 为什么有时能救？**
   - 要点：constructor 依赖发生在实例化之前，没有 early exposure 窗口；setter/field 依赖发生在实例已创建但未初始化完的窗口期，singleton 可以提前暴露引用，使依赖环得以闭合。
   - 证据链：`doCreateBean` 的 early exposure（`addSingletonFactory`）+ `getSingleton(beanName, allowEarlyReference)` 三层命中分支。

2. **三级缓存到底解决了什么问题？它没解决什么？**
   - 要点：它解决的是“singleton 在创建窗口期的提前引用”，不是“任意依赖图都能救”；prototype、constructor cycle 等场景仍然是边界。
   - 证据链：`DefaultSingletonBeanRegistry#getSingleton`（final/early/factory 三层）+ `earlySingletonObjects` 命中情况。

3. **`getEarlyBeanReference` 的意义是什么？为什么会牵扯 raw vs wrapped 一致性？**
   - 要点：early 引用是否等于最终暴露形态（proxy/wrapper）很关键；不一致会导致 raw 注入绕过代理，或触发一致性保护 fail-fast。
   - 证据链：`AbstractAutowireCapableBeanFactory#getEarlyBeanReference` + `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference` + `doCreateBean` 尾部一致性检查。

复习入口：`appendix-interview-playbook.md`（Q6/Q7）。

## 源码调用链（方法级）：三层缓存 + early reference 在哪发生

当在面试/排障里讲循环依赖，最关键的是把“结论”落到方法级调用链：

1. `AbstractAutowireCapableBeanFactory#doCreateBean`（单 bean 创建主线）
2. `DefaultSingletonBeanRegistry#addSingletonFactory`（early exposure：注册 early factory）
3. `DefaultSingletonBeanRegistry#getSingleton`（三层缓存命中分支：final/early/factory）
4. `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`（决定 early 形态：raw vs proxy）

无需背实现细节，但必须能解释“为什么在这个窗口期能救 setter 循环、救不了 constructor 循环”。

## 验收口径：三句话讲清 early exposure 窗口
读完后应能用 3 句完整回答：

1. constructor cycle 为什么 fail-fast？（依赖发生在实例化之前，没有 early exposure 窗口）
2. setter cycle 为什么可能成功？（singleton 创建窗口期 + early exposure + `getSingleton(..., allowEarlyReference=true)`）
3. 工程上如何处理？（重构消环优先；延迟依赖是折中；setter 不是默认解法）


## 小结：循环依赖的答案在创建窗口期

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 测试文件：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

<!-- BOOKIFY:END -->

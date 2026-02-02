# 15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` / `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation` / `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`
    - 推荐 Lab：`SpringCoreBeansPreInstantiationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](14-post-processor-ordering.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[16. early reference 与循环依赖：getEarlyBeanReference](16-early-reference-and-circular.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行**
- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先运行一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansPreInstantiationLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansPreInstantiationLabTest`，再用 `SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章讲一个“非常像隐式行为”的容器机制：

- `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`

它允许在 bean 实例化之前返回一个对象，从而 **短路默认的创建路径**。

### 处理器时机与排序（为什么要先注册再创建）

- IABPP 属于 **实例层 BPP**，必须在 `registerBeanPostProcessors` 完成后才能生效  
- 排序规则仍遵循 `PriorityOrdered → Ordered → 无序`  
- 早于 BPP 注册的创建，将无法触发短路

## 1. 现象：构造器抛异常会让 refresh 直接失败

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
  - `withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`（证据：构造器被调用一次，context refresh 失败）

可以观察到：

- `FailingService` 构造器被调用
- 构造器抛异常导致容器 refresh 失败

这说明：**默认情况下，单例会在 refresh 阶段被创建**（非 lazy）。

### 1.1 机制系统阐述：条件 → 分支 → 结果

**条件**：是否有 IABPP 在 before-instantiation 阶段返回替身  
**分支**：`resolveBeforeInstantiation` 返回非 null → 直接暴露  
**结果**：  
- 无短路：构造器执行，异常导致 refresh 失败  
- 有短路：构造器不执行，容器获取到 proxy/替身

## 2. 现象：短路后，构造器不再执行

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`
  - `postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()`（证据：构造器调用次数为 0，获取到的是 proxy）

示例中注册了一个 `InstantiationAwareBeanPostProcessor`：

- 当容器准备创建 `FailingService` 时
- `postProcessBeforeInstantiation` 直接返回一个 JDK proxy（实现了 `GreetingService`）
- 容器就把这个 proxy 当作最终 bean

因此：

- 构造器不会执行
- refresh 不会失败

## 3. 这个机制有什么现实意义？

理解它的价值在于：

- 应能够理解“容器为什么能把某个 bean 变成代理/替身对象”
- 应能够理解“实例层增强”的入口不仅仅是 AOP（很多能力都是类似机制）

入口：

最小复现入口（方法级）：

- `SpringCoreBeansPreInstantiationLabTest.withoutBeforeInstantiationShortCircuit_refreshFailsAndConstructorWasCalled()`
- `SpringCoreBeansPreInstantiationLabTest.postProcessBeforeInstantiation_canShortCircuitDefaultInstantiationPath()`

推荐断点（闭环版）：

1) `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`：短路入口（是否走到这里决定“构造器会不会执行”）
2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`：观察哪个 `InstantiationAwareBeanPostProcessor` 返回了替身
3) 在 Lab 里实现的 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`：观察返回对象（surrogate/proxy）
4) `AbstractAutowireCapableBeanFactory#doCreateBean`：对照两条路径（短路成功时目标 bean 不会走完整创建主线）

## 可复现闭环（基于 `SpringCoreBeansPreInstantiationLabTest`）

至少应能够用 3 条断言讲清楚本章主线：

1) **没有短路时，构造器必然执行**  
   - 断点：`doCreateBean`  
   - 断言：构造器调用次数为 1
2) **短路时，构造器不执行**  
   - 断点：`resolveBeforeInstantiation`  
   - 断言：构造器调用次数为 0
3) **短路对象必须满足类型兼容**  
   - 断点：`applyBeanPostProcessorsBeforeInstantiation`  
   - 断言：JDK proxy 只能满足接口注入

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- “已编写 before-instantiation 的 BPP，但构造器仍然执行” → **实例层（时机/注册方式）**：BPP 是否在 refresh 前注册？是否真的被当作 BPP 注册进 BeanFactory？（对照 [25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)）
- “短路后出现 `BeanNotOfRequiredTypeException`” → **实例层（暴露类型）**：返回对象的类型是否与容器期望类型兼容？（JDK proxy 只实现接口）
- “短路后生命周期回调/注入行为变得反直觉” → **实例层（绕过默认流程）**：读者返回对象意味着读者可能绕过 `doCreateBean` 的部分阶段（可对照 [17](17-lifecycle-callback-order.md)、[30](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)）
- “误认为这是 AOP/事务专属机制” → **实例层通用机制**：代理/替身的出现不止发生在 AOP（见 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）

## 4. 源码调用链（方法级）：短路发生在哪个分支？

无需背完整 `createBean` 流程，但要记住一个“关键分叉”：

- **默认路径**：`createBean` → `doCreateBean`（构造器/工厂方法 → `populateBean` → `initializeBean`）
- **短路路径**：`createBean` → `resolveBeforeInstantiation`（有返回值则直接作为最终暴露对象）

把分支落到方法级，一条够用的最短链路是：

1) `AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`
2) `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(beanName, mbd)`
3) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation(targetType, beanName)`
4) `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation(beanClass, beanName)`
5) 如果返回非 null：
   - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization(result, beanName)`（必要时再做 after-init 包装）
   - **跳过** `doCreateBean`（因此构造器/注入/初始化回调的直觉会被打破）

> Debug 建议：在 `resolveBeforeInstantiation` 加条件断点（beanName），可以立刻知道“这次创建到底有没有走短路分支”。

## 5. 排障决策表（实例化前短路：从“构造器没执行”到“证据链”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| 构造器还是执行了（短路没生效） | BPP 注册太晚 / 根本没进 BPP 链 | `beanFactory.getBeanPostProcessors()` 是否包含相应的 IABPP；`resolveBeforeInstantiation` 是否命中；`applyBeanPostProcessorsBeforeInstantiation` 是否返回 null | 确保在 refresh 前注册；不要在 BFPP/BDRPP 阶段过早创建目标 bean；必要时先用最小容器入口复现 | `SpringCoreBeansPreInstantiationLabTest` |
| 构造器不执行但 `@Autowired/@PostConstruct` 也未触发 | 扩展点直接返回“最终对象”，从而绕过默认注入/初始化路径 | 对照：短路分支返回非 null；`doCreateBean/populateBean/initializeBean` 未命中目标 beanName | 这属于机制边界：若需要注入/生命周期语义，不宜使用 short-circuit；可将依赖迁移至代理内部或外部工厂 | `SpringCoreBeansPreInstantiationLabTest`（对照两条用例） |
| `BeanNotOfRequiredTypeException` | 返回对象类型不兼容（JDK proxy 只实现接口） | 观察返回对象类型；按实现类 `getBean(Impl.class)` 失败 | 让注入点按接口；或使用 class-based proxy（但注意 final 限制）；或改成 after-init 代理（更稳定） | `SpringCoreBeansPreInstantiationLabTest` |
| 行为像 AOP，但找不到切面/事务配置 | 不是 AOP，而是 IABPP 的 before-instantiation short-circuit | `resolveBeforeInstantiation` 返回非 null；定位到具体哪个 IABPP 返回了替身 | 先在“创建链路”定位代理产生点，再回到“是谁注册了该 IABPP” | `SpringCoreBeansPreInstantiationLabTest` |

## 6. 面试常问（实例化前短路：能力与风险）

### Q1：`postProcessBeforeInstantiation` 能做什么？为什么它看起来像“隐式行为”？

- 标准答案（可复述）：
  - 它允许在目标 bean 实例化之前直接返回一个替身对象（常见是 proxy），从而短路默认的构造器/注入/初始化流程，最终暴露对象不一定来自原始类的实例化。
- 证据链（方法级）：
  - `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`
  - `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
- 最小复现：
  - `SpringCoreBeansPreInstantiationLabTest`

### Q2：它和“初始化后代理（AOP 常见形态）”有什么本质差别？

- 标准答案（可复述）：
  - before-instantiation short-circuit 发生在默认实例化之前；AOP 常见代理发生在 `postProcessAfterInitialization`（初始化后替换最终暴露对象）。两者影响的阶段不同，导致“构造器是否执行/生命周期是否按直觉发生”的可观察结果也不同。
- 证据链（方法级）：
  - short-circuit：`resolveBeforeInstantiation`
  - after-init：`applyBeanPostProcessorsAfterInitialization`（或具体 APC）

### Q3：如何在排障里快速证明“某个 bean 命中了短路分支”？

- 标准答案（可复述）：
  - 对 `resolveBeforeInstantiation(beanName)` 下条件断点；看 `applyBeanPostProcessorsBeforeInstantiation` 是否返回非 null；再反查是哪一个 `InstantiationAwareBeanPostProcessor` 返回的替身对象。

## 7. 自检要点

应能够回答：

1) 短路分支发生在 `createBean` 的哪个阶段？（提示：`resolveBeforeInstantiation`）
2) 为什么短路属于“高风险扩展点”？（要点：该分支会绕过默认注入/初始化路径）
3) 可以用哪两个断点证明“短路真的发生了”？（提示：`resolveBeforeInstantiation` + 相应的 IABPP）

## 小结与下一章

- `DefaultListableBeanFactory#preInstantiateSingletons`：非 lazy 单例通常在 refresh 期间从这里开始批量创建（本章现象的触发点）
- `AbstractAutowireCapableBeanFactory#createBean`：创建入口（会先尝试“实例化前短路”）
- `AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`：调用 `postProcessBeforeInstantiation` 的关键钩子
- `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`：短路扩展点（在“还没走默认实例化”前直接返回对象）
- `AbstractAutowireCapableBeanFactory#doCreateBean`：默认创建主流程（短路成功时通常不会走到这里）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansPreInstantiationLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`

上一章：[14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](14-post-processor-ordering.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[16. early reference 与循环依赖：getEarlyBeanReference](16-early-reference-and-circular.md)

<!-- BOOKIFY:END -->

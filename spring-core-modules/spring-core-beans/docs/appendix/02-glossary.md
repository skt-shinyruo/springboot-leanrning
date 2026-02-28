# 02. 术语表（Glossary）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：建议先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    本章围绕91. 术语表（Glossary）展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansContainerLabTest`。需要下探源码时，可以从 `@Value("#{...}")` / `DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#getSingleton` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见误区清单（建议反复对照）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab](03-knowledge-map.md)
<!-- GLOBAL-BOOK-NAV:END -->


## 导读

- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansContainerLabTest`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansContainerLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：术语表减少抽象解释，补“落到代码里是什么”：每个术语给出关键类/方法/数据结构，并回链到首次出现的章节。
    - 下一跳：若是从现象进入，优先回到 [知识地图](03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这份术语表的目标不是“背概念”，而是解决两个学习痛点：

### 使用方式（把术语落成可排障结论）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


遇到术语时，按下面 4 步快速落地：

1) **现象**：它通常对应哪类问题/异常？
2) **证据链**：它在源码主线上出现在哪个方法？
3) **修复**：应能够做的最小动作是什么？
4) **验证**：哪个 Lab/Test 能复现并验证？

---

## 容器与定义

- **Bean**：被 Spring 容器管理的对象（最终 `getBean()` 获取到的“对外暴露对象”，可能是原始对象也可能是代理）。
  章节：[`01`](../part-01-ioc-container/09-bean-mental-model.md)
- **BeanDefinition**：定义层元数据，描述“如何创建一个 bean”（class/factory method/scope/lazy/dependsOn/回调等）。
  章节：[`01`](../part-01-ioc-container/09-bean-mental-model.md)、[`35`](../part-04-wiring-and-boundaries/18-merged-bean-definition.md)
- **RootBeanDefinition / MergedBeanDefinition**：创建时实际参与计算的“合并后的最终配方”。
  章节：[`35`](../part-04-wiring-and-boundaries/18-merged-bean-definition.md)
- **BeanFactory**：最核心的 IoC 容器能力（创建/注入/scope/生命周期骨架）。
  章节：[`01`](../part-01-ioc-container/09-bean-mental-model.md)
- **ApplicationContext**：在 BeanFactory 上叠加事件、资源、环境等“应用级能力”，并提供 `refresh()` 主线。
  章节：[`01`](../part-01-ioc-container/09-bean-mental-model.md)、[`12`](../part-03-container-internals/01-container-bootstrap-and-infrastructure.md)
- **refresh**：容器启动的主线流程（定义注册→执行 BFPP/BDRPP→注册 BPP→创建单例→收尾事件）。
  章节：[`00`](../part-00-guide/03-deep-dive-guide.md)、[`12`](../part-03-container-internals/01-container-bootstrap-and-infrastructure.md)

---

## 注入与候选

- **DependencyDescriptor**：注入点的“描述符”（需要什么类型/是否 required/是否带泛型/有哪些注解/名称等）。
  章节：[`03`](../part-01-ioc-container/02-dependency-injection-resolution.md)
- **候选（candidates）**：按类型匹配得到的候选集合；单依赖需要进一步收敛为唯一胜者，否则失败（NoUnique）。
  章节：[`03`](../part-01-ioc-container/02-dependency-injection-resolution.md)、[`33`](../part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)
- **`@Qualifier` / AutowireCandidateResolver**：缩小候选集合（精确选择）；包含 meta-annotation 的 Qualifier 也在此阶段参与过滤。
  章节：[`03`](../part-01-ioc-container/02-dependency-injection-resolution.md)、[`45`](../part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md)
- **`@Primary` / `@Priority`**：候选收敛的默认胜者/优先级线索（注意：并不等价于集合排序）。
  章节：[`33`](../part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)
- **`@Order` / Ordered**：主要影响集合注入/链路顺序，不等价于“单依赖选谁”。
  章节：[`33`](../part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)、[`14`](../part-03-container-internals/03-post-processor-ordering.md)
- **ObjectProvider**：把“获取依赖”延迟到使用时（常用于 prototype 注入 singleton、可选依赖等）。
  章节：[`04`](../part-01-ioc-container/03-scope-and-prototype.md)

---

## 生命周期与扩展点

- **生命周期（lifecycle callbacks）**：实例化→属性填充→初始化回调（Aware/@PostConstruct 等）→对外暴露→销毁回调。
  章节：[`05`](../part-01-ioc-container/04-lifecycle-and-callbacks.md)、[`17`](../part-03-container-internals/06-lifecycle-callback-order.md)
- **Aware**：让 bean “感知容器能力”的回调族（BeanName/BeanFactory/ApplicationContext 等）。
  章节：[`12`](../part-03-container-internals/01-container-bootstrap-and-infrastructure.md)
- **BFPP（BeanFactoryPostProcessor）**：作用于定义层（BeanDefinition），发生在实例化之前。
  章节：[`06`](../part-01-ioc-container/05-post-processors.md)、[`14`](../part-03-container-internals/03-post-processor-ordering.md)
- **BDRPP（BeanDefinitionRegistryPostProcessor）**：BFPP 的增强版，可以再注册更多 BeanDefinition。
  章节：[`13`](../part-03-container-internals/02-bdrpp-definition-registration.md)
- **BPP（BeanPostProcessor）**：作用于实例层（创建过程中/初始化前后），可以包装/替换最终暴露对象（proxy）。
  章节：[`06`](../part-01-ioc-container/05-post-processors.md)、[`31`](../part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md)
- **PriorityOrdered / Ordered**：处理器排序的两层契约（分段执行 + 组内排序）。
  章节：[`14`](../part-03-container-internals/03-post-processor-ordering.md)

---

## 代理、FactoryBean、循环依赖

- **Proxy（代理）**：容器最终暴露对象可能是代理而非原始实例，常见由 BPP 在 after-init 返回。
  章节：[`31`](../part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md)
- **FactoryBean**：注册在容器里的是工厂，`getBean("name")` 默认获取到 product，`getBean("&name")` 才获取到工厂本身。
  章节：[`08`](../part-01-ioc-container/07-factorybean.md)、[`23`](../part-04-wiring-and-boundaries/06-factorybean-deep-dive.md)、[`29`](../part-04-wiring-and-boundaries/12-factorybean-edge-cases.md)
- **early reference（提前暴露引用）**：为缓解部分单例循环依赖，在“还没初始化完”时暴露早期引用（可能与代理交互）。
  章节：[`09`](../part-01-ioc-container/08-circular-dependencies.md)、[`16`](../part-03-container-internals/05-early-reference-and-circular.md)

---

## 值解析与类型转换

- **embedded value resolver**：`@Value` 字符串解析的核心机制（non-strict vs strict）。
  章节：[`34`](../part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md)
- **BeanWrapper**：属性读写与类型转换触发器（写入属性时触发 convertIfNecessary）。
  章节：[`36`](../part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md)
- **ConversionService**：现代转换体系（建议优先理解与使用）。
  章节：[`36`](../part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md)

---

## AOT 与真实世界补齐

- **AOT（Ahead-of-Time）**：把原本运行期才能完成的工作（分析/生成/裁剪元信息）前移到构建期执行，以换取更快启动与更强可预知性。
  章节：[`40`](../part-05-aot-and-real-world/01-aot-and-native-overview.md)
- **RuntimeHints / RuntimeHintsRegistrar**：AOT/Native 下的“构建期契约”数据结构与注册入口，用于声明反射/代理/资源等运行期需求。
  章节：[`41`](../part-05-aot-and-real-world/02-runtimehints-basics.md)
- **XmlBeanDefinitionReader / BeanDefinitionReader**：把输入源（XML/properties/groovy 等）解析为 BeanDefinition 并注册到 BeanFactory 的 reader 家族（定义层输入）。
  章节：[`42`](../part-05-aot-and-real-world/03-xml-bean-definition-reader.md)、[`47`](../part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md)
- **AutowireCapableBeanFactory**：对容器外对象提供“注入/初始化/销毁”的能力入口（把部分容器管道应用到非托管对象上）。
  章节：[`43`](../part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md)
- **SpEL（Spring Expression Language）**：用于 `@Value("#{...}")` 等场景的表达式语言（表达式求值后仍会进入类型转换）。
  章节：[`44`](../part-05-aot-and-real-world/05-spel-and-value-expression.md)

---

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansContainerLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

> 验证入口（可运行）：`SpringCoreBeansContainerLabTest`

## 0. 复现入口（可运行）

- 本章为索引/术语类内容，不直接提供单一 Lab 入口。
- 建议做法：从本页跳转到对应章节后，按章节中的“复现入口（可运行）”运行对应 Test。

1) 读文档/看断点时遇到名词能快速定位“它到底是什么、在哪个阶段出现、影响什么”
2) 把同一类名词放在一起对比，避免“记得名字但不知道边界”

> 使用建议：遇到不熟悉的术语，可先在此处快速检索，再回到对应章节运行 Lab。
> Part 05（AOT/XML/SpEL/容器外对象）相关术语请优先参阅上方“**AOT 与真实世界补齐**”小节。

## 常见误区与边界

- **DependencyDescriptor**：注入点的“描述符”（需要什么类型/是否 required/是否带泛型/有哪些注解/名称等）。
  章节：[`03`](../part-01-ioc-container/02-dependency-injection-resolution.md)
- **候选（candidates）**：按类型匹配得到的候选集合；单依赖需要进一步收敛为唯一胜者，否则失败。
  章节：[`03`](../part-01-ioc-container/02-dependency-injection-resolution.md)、[`33`](../part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)
- **`@Qualifier`**：缩小候选集合（精确选择）。
  章节：[`03`](../part-01-ioc-container/02-dependency-injection-resolution.md)
- **`@Primary`**：默认胜者（没有更强限定条件时）。
  章节：[`33`](../part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)
- **`@Order` / Ordered**：主要影响集合注入/链路执行顺序，不等价于“单依赖选谁”。
  章节：[`33`](../part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)、[`14`](../part-03-container-internals/03-post-processor-ordering.md)
- **ResolvableDependency**：可注入但不是 bean 的特殊依赖（例如 `ApplicationContext`、`Environment`）。
  章节：[`20`](../part-04-wiring-and-boundaries/03-resolvable-dependency.md)
- **ResolvableType**：Spring 用来描述/匹配泛型的类型系统。
  章节：[`37`](../part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md)

## 排障使用方式（术语 → 断点入口）

若在真实项目里看到异常/现象，先不必急于“猜机制”，先把术语落到阶段与断点：

1) 看到 **BeanDefinition / registry / reader**：优先认为是“定义层”，先去断点 `DefaultListableBeanFactory#registerBeanDefinition` / 对应 Reader 的 `loadBeanDefinitions`。
2) 看到 **populate / convert / @Value**：优先认为是“注入/值解析/类型转换”阶段，去断点 `populateBean` / `resolveEmbeddedValue` / `convertIfNecessary`。
3) 看到 **post-processor / proxy / advisor**：优先认为是“BPP 链导致的包装/替换”，去断点 `registerBeanPostProcessors` / `applyBeanPostProcessorsAfterInitialization`。
4) 看到 **early reference / in creation**：优先认为是“循环依赖窗口期”，去断点 `DefaultSingletonBeanRegistry#getSingleton` / `addSingletonFactory`。

更系统的分流表：`appendix/05-production-troubleshooting-checklist.md`

## 面试使用方式（术语 → 结论 → 证据链）

术语表不是用来背的；面试/述职时需要做到的是“把术语放回调用链与时机”：

- 读者提到 BFPP/BDRPP/BPP：要能补一句“发生在 refresh 哪一段”，并说出关键方法：`invokeBeanFactoryPostProcessors` / `registerBeanPostProcessors`。
- 读者提到 FactoryBean：要能补一句“名字 `&` 前缀的差异”，并能指到 `getObjectForBeanInstance`。
- 读者提到循环依赖：要能补一句“三层缓存解决的是 setter 窗口期，不是构造器循环”，并能指到 `getSingleton`。

答题模板入口：`appendix/04-interview-playbook.md`

## 自检要点
- 应能够把下面 5 个名词分别放到 refresh 主线的哪个阶段吗：`BeanDefinition` / BFPP/BDRPP / BPP / `doGetBean` / `doCreateBean`？
- 应能够解释清楚：为什么同一个名词（例如 “processor”）在定义阶段与创建阶段的职责完全不同吗？
- 是否能够用术语表把“看到名词 → 关联章节 → 运行 Lab → 设置断点验证”的链路完成验证？

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest`

上一章：[90. 常见误区清单（建议反复对照）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[92. 知识点地图（Concept → Chapter → Lab）](03-knowledge-map.md)

<!-- BOOKIFY:END -->

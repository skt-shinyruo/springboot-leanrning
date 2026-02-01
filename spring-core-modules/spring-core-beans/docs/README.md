# spring-core-beans 文档导航（Docs TOC）

## 导读

- 本文件作为 `spring-core-beans` 模块的学习入口与目录页，用于将“阅读内容 / 运行方式 / 断点入口”组织为一条连续主线。
- 本模块目标：把 IoC 容器从“会用 API”提升到“能解释机制、能设置断点、能排障定位”。
- 推荐学习方式：**先运行对应 Lab 固化现象，再回到章节阅读机制主线，最后通过断点完成证据链验证**。
- 本模块每章开头均提供“章节学习卡片（五问闭环）”：以最少信息回答“本章主题/使用方式/断点入口/对应 Lab”，并与上一章/下一章导航保持一致，便于连续阅读与复盘。

## 四条阅读路线（按读者分层：源码进阶 + 面试）

- A（能用为主）：按目录顺读每章的“本章要点 + 最小实验”，遇到问题回看“常见误区”。
- B（能断点为主）：每章至少运行一次对应 Lab，并按章节给出的 breakpoints/watch list 在调试器中观察关键数据结构变化。
- C（能排障/能解释为主）：把每章的“自检要点”当成面试/复盘模板；遇到真实问题时按章节的“排障分流表”定位到最短调用链。
- D（面试冲刺为主）：先阅读 `appendix/93-interview-playbook.md` 的题库，再回到对应章节，以“证据链（方法级）+ 可运行 Lab”完成论证（避免仅记忆结论）。

## 章节契约（教程化验收口径：10/30/3）

可以把每一章都当成一个“可验收交付物”，按 10/30/3 三段闭环学习：

1) **10 分钟最小闭环**：运行本章 Lab/Test，观察预期现象（或断言）。
2) **30 分钟深入闭环**：命中关键断点（3–5 个稳定锚点）并通过 watch list 观察决定性变量。
3) **3 分钟复述闭环**：用“结论 → 证据链（关键方法）→ 反例/误区”复述本章核心机制（对标 `appendix/93` 的标准结构）。

## 如何运行（最小闭环）

- 运行本模块全部测试：
  - `mvn -pl :spring-core-beans test`
- 仅运行某个章节对应 Lab：
  - `mvn -pl :spring-core-beans -Dtest=<TestClassName> test`
- Explore/Debug（可选启用，不影响默认回归）：
  - `mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`

## 可从此处开始

- 基础问题索引（Why Index）：三级缓存 / three level cache / early reference / proxy 替换 → [00. Why Index（基础问题索引）](part-00-guide/009-00-why-index.md)
- 30 分钟快启：`part-00-guide/012-01-quickstart-30min.md`
- 深入导读（症状驱动导航）：`part-00-guide/011-00-deep-dive-guide.md`
- 全章“内容级再加深”策略（逐章可执行路线，按章节给出“入口/断点/自证/排错”建议）：`deepening-strategies/README.md`
- 核心七件套（检查表 + 对应章节/Lab）：`appendix/92-knowledge-map.md`（第 0 节）
- Debugger Pack（断点包总入口）：`appendix/98-debugger-pack.md`
- 团队内训讲义（可直接用于授课的课时脚本）：`appendix/99-team-training-kit.md`

## 继续深化从哪里开始（Round 2）

若已经“跑得动 Lab / 打得进断点”，但希望更快进入下一轮（更短证据链、更短排错路径），建议从下面三条入口之一开始：

- **现象驱动（从异常/现象进入）**：先到 [知识地图](appendix/92-knowledge-map.md) 用「现象 → 章节 → 断点组 → 推荐 Lab」把入口压到最短：
  - 推荐先跑：`SpringCoreBeansBreakpointPackLabTest`（断点包总入口，用一组实验把现象固定下来）
  - 再按表格给出的断点组（例如 C2/C6/C5/C7）命中关键分支，最后回到对应章节把机制主线与边界对照补齐
- **断点驱动（先证明 refresh 处于哪一段）**：先到 [断点地图](part-00-guide/013-02-breakpoint-map.md) 选 C1–C7 中一组断点把阶段定位清楚：
  - 推荐先跑：`SpringCoreBeansBootstrapInternalsLabTest`（refresh 主线对照）或 `SpringCoreBeansLabTest`
  - 定位阶段后，再回到章节正文把“观察到的变量变化”收敛为结论/反例/排错路径
- **排障驱动（把经验固化为 SOP）**：先到 [生产排障清单](appendix/94-production-troubleshooting-checklist.md) 走 3–5 步最短诊断路径，把问题分型为“定义层/注入解析/代理替换/值解析”：
  - 推荐先从表格里的“第一断点 + 推荐 Lab”动手（例如注入解析走 C6，循环依赖走 C5）
  - 收敛后再回到章节/Lab，形成可回归的证据链

> 说明：目录页只负责“把读者送到下一步可验证动作”，不在这里重复机制细节（细节留在正文）。

## 症状驱动导航（快速定位）

> 更系统的“症状 → 章节 → 断点 → Lab”导航见：`part-00-guide/011-00-deep-dive-guide.md`。定位到章节后，下一步建议直接用 [知识地图](appendix/92-knowledge-map.md) 选“断点组 + 推荐 Lab”，或用 [断点地图](part-00-guide/013-02-breakpoint-map.md) 直接命中 C 组（避免把 README 扩写成另一份知识地图）。

| 现象/异常（读者视角） | 直达章节（最短路径） | 备注（先分层再追栈） |
| --- | --- | --- |
| `NoSuchBeanDefinitionException` / “@Bean/@Component 似乎未生效” | [02. Bean 注册入口](part-01-ioc-container/02-bean-registration.md)、[12. 注解为何生效（bootstrap）](part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)、[10. Boot 自动装配影响链路](part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md) | 优先判定“定义层有没有注册 BeanDefinition” |
| `NoUniqueBeanDefinitionException` / 多实现注入歧义 | [03. 依赖注入解析](part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33. 候选选择 vs 顺序](part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) | 收敛：`@Primary/@Qualifier/@Priority` |
| “循环依赖”异常 / `BeanCurrentlyInCreationException` | [09. 循环依赖（现象与规避）](part-01-ioc-container/09-circular-dependencies.md)、[16. early reference 与循环依赖](part-03-container-internals/16-early-reference-and-circular.md) | 先区分 constructor vs setter；再看 early reference 参与者 |
| “为什么 Spring 要用三级缓存？” / `three level cache` / `earlySingletonObjects` / `singletonFactories` | [00. Why Index（基础问题索引）](part-00-guide/009-00-why-index.md)、[09. 循环依赖](part-01-ioc-container/09-circular-dependencies.md)、[16. early reference](part-03-container-internals/16-early-reference-and-circular.md) | 优先把握“final/early/factory 三类语义”与“early 形态一致性（raw vs proxy）” |
| lazy bean 启动期被拉起 / “明明 @Lazy 还被提前创建” | [19. dependsOn](part-04-wiring-and-boundaries/19-depends-on.md)、[18. Lazy 语义](part-04-wiring-and-boundaries/023-18-lazy-semantics.md) | `dependsOn` 会显式 `getBean(dep)`，可强制拉起 lazy-init |
| “获取到 proxy” / AOP 行为异常 / self-invocation | [31. 代理产生阶段（BPP 替换）](part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)、[15. 实例化前短路（pre）](part-03-container-internals/15-pre-instantiation-short-circuit.md) | 先定位是 pre/early/after-init 哪个窗口替换对象 |
| `@Value("${...}")` 解析失败 / 值不符合预期 | [34. 占位符解析（strict vs non-strict）](part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)、[38. Environment/PropertySource](part-04-wiring-and-boundaries/38-environment-and-propertysource.md) | 关注 PropertySource precedence 与 placeholder resolver |
| `@Resource` 注入错对象 / “为什么像按名称找？” | [32. @Resource name-first](part-04-wiring-and-boundaries/32-resource-injection-name-first.md)、[22. beanName/alias](part-04-wiring-and-boundaries/22-bean-names-and-aliases.md) | name-first + alias 会共同影响最终命中 |
| FactoryBean 混淆 `&` / “按类型发现/注入失效” | [08. FactoryBean（基础）](part-01-ioc-container/08-factorybean.md)、[23. FactoryBean 深潜](part-04-wiring-and-boundaries/23-factorybean-deep-dive.md)、[29. FactoryBean 边界](part-04-wiring-and-boundaries/29-factorybean-edge-cases.md) | 关键点：`getObjectType/isSingleton` 对 type matching 的影响 |
| 后处理器顺序导致“偶发不生效”/手工注册 BPP 陷阱 | [14. Ordering](part-03-container-internals/14-post-processor-ordering.md)、[25. 手工添加 BPP](part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md) | 优先核对 `PriorityOrdered/Ordered` 的分组与排序；再确认是否绕过默认注册流程 |
| AOT/Native 运行期缺失反射/代理/资源 | [40. AOT 总览](part-05-aot-and-real-world/024-40-aot-and-native-overview.md)、[41. RuntimeHints](part-05-aot-and-real-world/41-runtimehints-basics.md) | 用 registrar + 单测把“构建期契约”钉死 |

## 目录

### Part 00：Guide（怎么学 / 从哪里设置断点）

- [第 10 章：主线时间线：Spring Core Beans（IoC 容器）](part-00-guide/010-03-mainline-timeline.md)
- [第 11 章：00. 深入指南：将“Bean 三层模型”落实到源码与断点](part-00-guide/011-00-deep-dive-guide.md)
- [第 11 章：04：关键分支矩阵（Beans Branch Decision Matrix）](part-00-guide/011-04-branch-decision-matrix.md)
- [第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](part-00-guide/012-01-quickstart-30min.md)
- [第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）](part-00-guide/013-01-applicationcontext-refresh-call-chain.md)
- [第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](part-00-guide/013-02-breakpoint-map.md)

### Part 01：IoC Container（注册 / 注入 / 生命周期 / 扩展点）

- [第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary](part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](part-01-ioc-container/015-04-scope-and-prototype.md)
- [第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
- [第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](part-01-ioc-container/017-06-post-processors.md)
- [第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](part-01-ioc-container/018-07-configuration-enhancement.md)
- [02. Bean 注册入口：扫描、`@Bean`、`@Import`、Registrar](part-01-ioc-container/02-bean-registration.md)
- [第 20 章：01. Bean 运行机制：从 BeanDefinition 到最终暴露对象](part-01-ioc-container/020-01-bean-mental-model.md)
- [08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](part-01-ioc-container/08-factorybean.md)
- [09. 循环依赖：现象、原因与规避（constructor vs setter）](part-01-ioc-container/09-circular-dependencies.md)

### Part 02：Boot Auto-Config（Boot 叠加后容器如何变复杂）

- [第 19 章：11. 调试与自检：如何观察容器正在执行的工作](part-02-boot-autoconfig/019-11-debugging-and-observability.md)
- [020-09 Auto-Config Ordering（自动配置顺序）](part-02-boot-autoconfig/020-09-auto-config-ordering.md)
- [第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)

### Part 03：Internals（refresh 主线 / 处理器算法 / 缓存边界）

- [第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？](part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- [13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义](part-03-container-internals/13-bdrpp-definition-registration.md)
- [14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](part-03-container-internals/14-post-processor-ordering.md)
- [15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](part-03-container-internals/15-pre-instantiation-short-circuit.md)
- [16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](part-03-container-internals/16-early-reference-and-circular.md)
- [17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）](part-03-container-internals/17-lifecycle-callback-order.md)
- [从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](part-03-container-internals/18-refresh-to-bean-creation-mainline.md)

### Part 04：Wiring & Boundaries（候选选择 / 代理 / 占位符 / 转换等）

- [第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](part-04-wiring-and-boundaries/023-18-lazy-semantics.md)
- [19. dependsOn：强制初始化顺序（即使没有显式依赖）](part-04-wiring-and-boundaries/19-depends-on.md)
- [20. registerResolvableDependency：能注入，但它不是 Bean](part-04-wiring-and-boundaries/20-resolvable-dependency.md)
- [21. 父子 ApplicationContext：可见性与覆盖边界](part-04-wiring-and-boundaries/21-context-hierarchy.md)
- [22. Bean 名称与 alias：同一个实例，多一个名字](part-04-wiring-and-boundaries/22-bean-names-and-aliases.md)
- [23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](part-04-wiring-and-boundaries/23-factorybean-deep-dive.md)
- [24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？](part-04-wiring-and-boundaries/24-bean-definition-overriding.md)
- [25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)
- [26. SmartInitializingSingleton：所有单例都创建完之后再做事](part-04-wiring-and-boundaries/26-smart-initializing-singleton.md)
- [27. SmartLifecycle：start/stop 时机与 phase 顺序](part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md)
- [28. 自定义 Scope + scoped proxy：thread scope 的真实语义](part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md)
- [29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](part-04-wiring-and-boundaries/29-factorybean-edge-cases.md)
- [30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
- [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- [32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](part-04-wiring-and-boundaries/32-resource-injection-name-first.md)
- [33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` 到底各管什么？](part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
- [34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
- [35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？](part-04-wiring-and-boundaries/35-merged-bean-definition.md)
- [36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)
- [37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失](part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md)
- [38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](part-04-wiring-and-boundaries/38-environment-and-propertysource.md)
- [39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界](part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md)

### Part 05：AOT & Real World（XML/Reader/AOT/外部对象/SpEL/自定义 qualifier）

- [第 24 章：40. AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”](part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
- [41. RuntimeHints 入门：完成构建期契约验证](part-05-aot-and-real-world/41-runtimehints-basics.md)
- [42. XML → BeanDefinitionReader：定义层解析与错误分型](part-05-aot-and-real-world/42-xml-bean-definition-reader.md)
- [43. 容器外对象注入：AutowireCapableBeanFactory](part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md)
- [44. SpEL 与 `@Value("#{...}")`：表达式解析链路](part-05-aot-and-real-world/44-spel-and-value-expression.md)
- [45. 自定义 Qualifier：meta-annotation 与候选收敛](part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md)
- [46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](part-05-aot-and-real-world/46-xml-namespace-extension.md)
- [47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md)
- [48. 方法注入（Method Injection）：replaced-method / MethodReplacer](part-05-aot-and-real-world/48-method-injection-replaced-method.md)
- [49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md)
- [50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](part-05-aot-and-real-world/50-property-editor-and-value-resolution.md)

### Appendix（术语表 / 速查 / 排障清单 / 索引）

- [第 25 章：90. 常见误区清单（可反复对照）](appendix/025-90-common-pitfalls.md)
- [第 26 章：99. 自测题：是否能够真的理解了？](appendix/026-99-self-check.md)
- [91. 术语表（Glossary）](appendix/91-glossary.md)
- [92. 知识地图（Concept → Chapter → Lab）](appendix/92-knowledge-map.md)
- [93. 面试复述模板：用“可证明的主线”回答 Spring Beans](appendix/93-interview-playbook.md)
- [94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）](appendix/94-production-troubleshooting-checklist.md)
- [95. spring-beans Public API 索引（Spring Framework 6.2.15）](appendix/95-spring-beans-public-api-index.md)
- [96. spring-beans Public API 覆盖差距（Gap）清单（Spring Framework 6.2.15）](appendix/96-spring-beans-public-api-gap.md)
- [97. Explore/Debug 用例（可选启用，不影响默认回归）](appendix/97-explore-debug-tests.md)
- [Debugger Pack（断点包总入口）](appendix/98-debugger-pack.md)
- [99. 团队内训讲义（Training Kit）：可直接用于授课的课时脚本](appendix/99-team-training-kit.md)
- [内容级再加深策略（逐章可执行路线）](deepening-strategies/README.md)

## 自检要点
- 是否能够按“主线 → 分支 → 证据链”的方式学习：先运行 Lab，再结合断点阅读章节？
- 是否能够能把一个现象先分层：定义阶段（BeanDefinition/processor） vs 创建阶段（getBean/doCreateBean/BPP）？
- 是否能够在 1 分钟内从目录定位到：对应章节 + 对应 LabTest + 断点入口？
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先回到本章/本 Part 已给出的 Lab/Test，把现象跑出来；再按正文的调用链/断点去验证结论。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：将目录页的价值从“列链接”提升为“给路径”：为关键节点补一句“为什么现在读它”，并在 proxy/事务/自调用等处给出 Beans→AOP 的最短跳转与目的说明。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[00. 模块导读与路径说明（Start Here）](part-00-guide/012-01-quickstart-30min.md) ｜ 目录：[Docs TOC](README.md) ｜ 下一章：[01. 深入指南（症状驱动导航）](part-00-guide/011-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->

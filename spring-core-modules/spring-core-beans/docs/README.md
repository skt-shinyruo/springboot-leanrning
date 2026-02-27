# spring-core-beans：IoC 容器与 Bean（模块导论）

本模块聚焦 Spring Framework 的 IoC 容器与 Bean 机制。它不以“会用某个注解”为终点，而把能力落在三个可验证的层面：

1. **能解释**：把“注册/注入/生命周期/后处理器/代理/循环依赖”等机制放回 `refresh()` 主线，解释清楚它发生在什么时候、为什么会这样。
2. **能调试**：知道关键断点落在“定义层还是创建层”，并能在调试器里观察到决定性变量变化。
3. **能排障**：面对异常与现象，能先分层（定义/创建/最终暴露对象），再用最短证据链收敛到原因。

官方参考（适用 Spring Framework 6.2.x；本仓库基线 6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

---

## 10 分钟入口：先跑通一个容器闭环

如果只选择一个入口作为起跑线，可以先运行：

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`

读者在这一轮不需要把机制讲全，但应能在断言与调试过程中回答三个事实问题：

- 哪些 BeanDefinition 被注册进容器（定义层发生了什么）？
- bean 实例是在 `refresh()` 的哪一段被创建出来的（创建层发生了什么）？
- 最终暴露对象是否发生过替换（例如被 BPP 换成 proxy）？

---

## 如何运行（保持入口可回归）

- 运行本模块全部测试：`mvn -pl :spring-core-beans test`
- 仅运行某个章节对应 Lab：`mvn -pl :spring-core-beans -Dtest=<TestClassName> test`
- Explore/Debug（可选开关，不影响默认回归）：
  `mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`

---

## 阅读路线（主线 / 断点 / 排障）

本模块的内容跨度较大，最稳定的读法是把“阅读”绑定到可运行入口：

- **主线阅读（建立心智模型）**：先用 Guide 确认 `refresh()` 主线位置，再顺读 Part 01（IoC Container），最后进入 Internals 与 Wiring & Boundaries。
  - Guide 入口：`part-00-guide/03-deep-dive-guide.md` / `part-00-guide/05-quickstart-30min.md`
- **断点阅读（以可观察为中心）**：每章至少跑一次 Lab，按章节提供的 breakpoints/watch list 观察关键数据结构。
  - 断点地图：`part-00-guide/07-breakpoint-map.md` / Debugger Pack：`appendix/09-debugger-pack.md`
- **排障阅读（从现象回到最短证据链）**：先用下文的“症状驱动导航”定位章节，再回到对应 Lab 固化现象与边界。
  - 知识地图：`appendix/03-knowledge-map.md` / 生产排障清单：`appendix/05-production-troubleshooting-checklist.md`

---

## 从哪里开始（把入口压到最短）

如果目标是尽快把高频“为什么”变成可验证结论，可以从以下入口切入：

- Why Index：把“三级缓存/early reference/proxy 替换”这类问题做成实验闭环
  - [01. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](part-00-guide/01-why-index.md)
- 深入导读：用“症状 → 章节 → 断点 → Lab”确定最短路径
  - `part-00-guide/03-deep-dive-guide.md`

当已经能够“跑得动 Lab / 打得进断点”，想进入更短证据链、更短排错路径时，可以按三种入口继续推进：

- **现象驱动**：先用知识地图把入口压到最短，再跑断点组收敛关键分支
- **断点驱动**：先定位 `refresh()` 处于哪一段，再回到章节把观察到的变量变化收敛为结论
- **排障驱动**：按生产排障清单把问题分型为“定义层/注入解析/代理替换/值解析”，再回到对应章节与 Lab 固化证据链

> 目录页的职责是“给路线与入口”，机制细节在正文中展开。

## 症状驱动导航（快速定位）

> 更系统的“症状 → 章节 → 断点 → Lab”导航见：`part-00-guide/03-deep-dive-guide.md`。定位到章节后，下一步建议直接用 [知识地图](appendix/03-knowledge-map.md) 选“断点组 + 推荐 Lab”，或用 [断点地图](part-00-guide/07-breakpoint-map.md) 直接命中 C 组（避免把 README 扩写成另一份知识地图）。
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html
> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

| 现象/异常（读者视角） | 直达章节（最短路径） | 备注（先分层再追栈） |
| --- | --- | --- |
| `NoSuchBeanDefinitionException` / “@Bean/@Component 似乎未生效” | [01. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）](part-01-ioc-container/01-bean-registration.md)、[01. 容器启动与基础设施处理器：为什么注解能工作？](part-03-container-internals/01-container-bootstrap-and-infrastructure.md)、[03. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](part-02-boot-autoconfig/03-spring-boot-auto-configuration.md) | 优先判定“定义层有没有注册 BeanDefinition” |
| `NoUniqueBeanDefinitionException` / 多实现注入歧义 | [02. 依赖注入解析：类型/名称/@Qualifier/@Primary](part-01-ioc-container/02-dependency-injection-resolution.md)、[16. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界](part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md) | 收敛：`@Primary/@Qualifier/@Priority` |
| “循环依赖”异常 / `BeanCurrentlyInCreationException` | [08. 循环依赖：现象、原因与规避（constructor vs setter）](part-01-ioc-container/08-circular-dependencies.md)、[05. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](part-03-container-internals/05-early-reference-and-circular.md) | 先区分 constructor vs setter；再看 early reference 参与者 |
| “为什么 Spring 要用三级缓存？” / `three level cache` / `earlySingletonObjects` / `singletonFactories` | [01. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](part-00-guide/01-why-index.md)、[08. 循环依赖：现象、原因与规避（constructor vs setter）](part-01-ioc-container/08-circular-dependencies.md)、[05. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](part-03-container-internals/05-early-reference-and-circular.md) | 优先把握“final/early/factory 三类语义”与“early 形态一致性（raw vs proxy）” |
| lazy bean 启动期被拉起 / “明明 @Lazy 还被提前创建” | [02. dependsOn：强制初始化顺序（即使没有显式依赖）](part-04-wiring-and-boundaries/02-depends-on.md)、[01. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](part-04-wiring-and-boundaries/01-lazy-semantics.md) | `dependsOn` 会显式 `getBean(dep)`，可强制拉起 lazy-init |
| “获取到 proxy” / AOP 行为异常 / self-invocation | [14. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）](part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md)、[04. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](part-03-container-internals/04-pre-instantiation-short-circuit.md) | 先定位是 pre/early/after-init 哪个窗口替换对象 |
| `@Value("${...}")` 解析失败 / 值不符合预期 | [17. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md)、[21. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](part-04-wiring-and-boundaries/21-environment-and-propertysource.md) | 关注 PropertySource precedence 与 placeholder resolver |
| `@Resource` 注入错对象 / “为什么像按名称找？” | [15. `@Resource` 注入：为什么其定位更接近“按名称找 Bean”？](part-04-wiring-and-boundaries/15-resource-injection-name-first.md)、[05. Bean 名称与 alias：同一个实例，多一个名字](part-04-wiring-and-boundaries/05-bean-names-and-aliases.md) | name-first + alias 会共同影响最终命中 |
| FactoryBean 混淆 `&` / “按类型发现/注入失效” | [07. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](part-01-ioc-container/07-factorybean.md)、[06. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](part-04-wiring-and-boundaries/06-factorybean-deep-dive.md)、[12. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](part-04-wiring-and-boundaries/12-factorybean-edge-cases.md) | 关键点：`getObjectType/isSingleton` 对 type matching 的影响 |
| 后处理器顺序导致“偶发不生效”/手工注册 BPP 陷阱 | [03. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](part-03-container-internals/03-post-processor-ordering.md)、[08. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md) | 优先核对 `PriorityOrdered/Ordered` 的分组与排序；再确认是否绕过默认注册流程 |
| AOT/Native 运行期缺失反射/代理/资源 | [01. AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”](part-05-aot-and-real-world/01-aot-and-native-overview.md)、[02. RuntimeHints 入门：把构建期契约完成验证](part-05-aot-and-real-world/02-runtimehints-basics.md) | 用 registrar + 单测把“构建期契约”固定 |

## 目录
- 完整目录（按阅读顺序）：[SUMMARY.md](SUMMARY.md)
- Part 00：Guide → `part-00-guide/`
- Part 01：IoC Container → `part-01-ioc-container/`
- Part 02：Boot Auto-Config → `part-02-boot-autoconfig/`
- Part 03：Internals → `part-03-container-internals/`
- Part 04：Wiring & Boundaries → `part-04-wiring-and-boundaries/`
- Part 05：AOT & Real World → `part-05-aot-and-real-world/`
- Appendix → `appendix/`
- 深化策略（可选）→ `deepening-strategies/`
## 自检要点
- 是否能够按“主线 → 分支 → 证据链”的方式学习：先运行 Lab，再结合断点阅读章节？
- 是否能够能把一个现象先分层：定义阶段（BeanDefinition/processor） vs 创建阶段（getBean/doCreateBean/BPP）？
- 是否能够在 1 分钟内从目录定位到：对应章节 + 对应 LabTest + 断点入口？
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先回到本章/本 Part 已给出的 Lab/Test，把现象跑出来；再按正文的调用链/断点去验证结论。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：将目录页的价值从“列链接”提升为“给路径”：为关键节点补一句“为什么现在读它”，并在 proxy/事务/自调用等处给出 Beans→AOP 的最短跳转与目的说明。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix/03-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](part-00-guide/07-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

上一章：[05. 30 分钟快速闭环：先快后深（3 个最小实验入口）](part-00-guide/05-quickstart-30min.md) ｜ 目录：[Docs TOC](README.md) ｜ 下一章：[03. 深入分析指南：将“Bean 三层模型”落实到源码与断点](part-00-guide/03-deep-dive-guide.md)

<!-- BOOKIFY:END -->

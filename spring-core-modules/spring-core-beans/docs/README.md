# spring-core-beans 文档导航（Docs TOC）

## 导读

- 本文件作为 `spring-core-beans` 模块的学习入口与目录页，用于将“阅读内容 / 运行方式 / 断点入口”组织为一条连续主线。
- 本模块目标：把 IoC 容器从“会用 API”提升到“能解释机制、能设置断点、能排障定位”。
- 推荐学习方式：**先运行对应 Lab 固化现象，再回到章节阅读机制主线，最后通过断点完成证据链验证**。
- 本模块每章开头均提供“章节学习卡片（五问闭环）”：以最少信息回答“本章主题/使用方式/断点入口/对应 Lab”，并与上一章/下一章导航保持一致，便于连续阅读与复盘。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


## 四条阅读路线（按读者分层：源码进阶 + 面试）

- A（能用为主）：按目录顺读每章的“本章要点 + 最小实验”，遇到问题回看“常见误区”。
- B（能断点为主）：每章至少运行一次对应 Lab，并按章节给出的 breakpoints/watch list 在调试器中观察关键数据结构变化。
- C（能排障/能解释为主）：把每章的“自检要点”当成面试/复盘模板；遇到真实问题时按章节的“排障分流表”定位到最短调用链。
- D（面试冲刺为主）：先阅读 `appendix/04-interview-playbook.md` 的题库，再回到对应章节，以“证据链（方法级）+ 可运行 Lab”完成论证（避免仅记忆结论）。

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

- 基础问题索引（Why Index）：三级缓存 / three level cache / early reference / proxy 替换 → [01. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](part-00-guide/01-why-index.md)
- 30 分钟快启：`part-00-guide/05-quickstart-30min.md`
- 深入导读（症状驱动导航）：`part-00-guide/03-deep-dive-guide.md`
- 全章“内容级再加深”策略（逐章可执行路线，按章节给出“入口/断点/自证/排错”建议）：`deepening-strategies/README.md`
- 核心七件套（检查表 + 对应章节/Lab）：`appendix/03-knowledge-map.md`（第 0 节）
- Debugger Pack（断点包总入口）：`appendix/09-debugger-pack.md`
- 团队内训讲义（可直接用于授课的课时脚本）：`appendix/10-team-training-kit.md`

## 继续深化从哪里开始（Round 2）

若已经“跑得动 Lab / 打得进断点”，但希望更快进入下一轮（更短证据链、更短排错路径），建议从下面三条入口之一开始：

- **现象驱动（从异常/现象进入）**：先到 [知识地图](appendix/03-knowledge-map.md) 用「现象 → 章节 → 断点组 → 推荐 Lab」把入口压到最短：
  - 推荐先跑：`SpringCoreBeansBreakpointPackLabTest`（断点包总入口，用一组实验把现象固定下来）
  - 再按表格给出的断点组（例如 C2/C6/C5/C7）命中关键分支，最后回到对应章节把机制主线与边界对照补齐
- **断点驱动（先证明 refresh 处于哪一段）**：先到 [断点地图](part-00-guide/07-breakpoint-map.md) 选 C1–C7 中一组断点把阶段定位清楚：
  - 推荐先跑：`SpringCoreBeansBootstrapInternalsLabTest`（refresh 主线对照）或 `SpringCoreBeansLabTest`
  - 定位阶段后，再回到章节正文把“观察到的变量变化”收敛为结论/反例/排错路径
- **排障驱动（把经验固化为 SOP）**：先到 [生产排障清单](appendix/05-production-troubleshooting-checklist.md) 走 3–5 步最短诊断路径，把问题分型为“定义层/注入解析/代理替换/值解析”：
  - 推荐先从表格里的“第一断点 + 推荐 Lab”动手（例如注入解析走 C6，循环依赖走 C5）
  - 收敛后再回到章节/Lab，形成可回归的证据链

> 说明：目录页只负责“把读者送到下一步可验证动作”，不在这里重复机制细节（细节留在正文）。

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

# Task List: spring-core-beans 教程体系重构（源码进阶 / 面试 / 团队内训）

Directory: `helloagents/plan/202601271554_spring_core_beans_docs_tutorial_upgrade/`

---

## 1. 课程入口与体验（Entry / Route / Debug）

- [√] 1.1 重写模块入口 README：聚焦三类读者、快启、验收标准，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/README.md`
- [√] 1.2 重写 docs TOC：三条阅读路线 + 运行/调试/断点策略收敛，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/README.md`
- [-] 1.3 强化 30 分钟快启：把“跑什么/看什么/学到什么”写成步骤化闭环，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 1.4 强化深挖指南：按“现象/问题→章节→断点→Lab”提供跳读索引，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [√] 1.5 补齐知识地图的“主题→章节→Lab→断点”索引段落，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
- [-] 1.6 强化主线时间线：把 refresh 主线节点与对应章节/Lab 形成可跳读路径，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 1.7 强化 refresh 调用链章节：为每步补齐“关键方法/关键分支/观察点/对应 Lab”索引，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 1.8 强化断点地图：按主题收敛可复用断点与 watch list（注册/注入/生命周期/处理器/循环依赖/FactoryBean/代理/值解析/转换/Scope），verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 1.9 强化关键分支矩阵：把常见现象收敛为“现象→阶段→入口→关键变量→修复策略”，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [√] 1.10 强化 Debugger Pack：把断点包与章节/Lab/排障分流整合成一键入口，verify why.md#requirement-r1-entry-learning-path in `spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`

## 2. BeanDefinition 与注册入口（Definition / Registration）

- [-] 2.1 深化 Bean 心智模型：定义层 vs 实例层的可观察对象与断点入口，verify why.md#requirement-r2-beandefinition-and-registration in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 2.2 深化注册入口章节：scan/@Bean/@Import/registrar/programmatic 的时机与边界，verify why.md#requirement-r2-beandefinition-and-registration in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 2.3 深化 MergedBeanDefinition：RootBeanDefinition 从哪里来、何时合并、对注入/回调的影响，verify why.md#requirement-r2-beandefinition-and-registration in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 2.4 深化 Bean 名称与 alias：命名/别名/覆盖与排障线索，verify why.md#requirement-r2-beandefinition-and-registration in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 3. 依赖解析与候选选择（DI / Qualifier / Primary）

- [-] 3.1 深化依赖注入解析：按“决策顺序 + 断点 + 关键变量”重写，verify why.md#requirement-r3-di-resolution-and-candidate-selection in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 3.2 深化候选选择与优先级：澄清 @Primary/@Priority/@Order 的职责边界，verify why.md#requirement-r3-di-resolution-and-candidate-selection in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 3.3 深化泛型匹配坑点：明确 ResolvableType 与代理导致的信息丢失，verify why.md#requirement-r3-di-resolution-and-candidate-selection in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 3.4 深化注入发生点：把 @Autowired/@Inject/@Value/@Resource 放回 populateBean 的阶段与钩子，verify why.md#requirement-r3-di-resolution-and-candidate-selection in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 3.5 深化 @Resource 注入：name-first 规则、回退逻辑与排障入口，verify why.md#requirement-r3-di-resolution-and-candidate-selection in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 3.6 深化 ResolvableDependency：能注入但不是 Bean 的机制与边界，verify why.md#requirement-r3-di-resolution-and-candidate-selection in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 4. 生命周期（Lifecycle / Callbacks）

- [-] 4.1 深化生命周期章节：把回调顺序与边界写成可复述与可验证内容，verify why.md#requirement-r4-lifecycle-and-callbacks in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 4.2 深化生命周期回调顺序章节：补齐关键断点/观察点/反例，verify why.md#requirement-r4-lifecycle-and-callbacks in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 4.3 深化创建主线：把 doCreateBean → populateBean → applyPropertyValues/BeanWrapper 属性填充写成可断点证据链，verify why.md#requirement-r4-lifecycle-and-callbacks in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 5. 后处理器与扩展点（BFPP / BPP / BDRPP）

- [-] 5.1 深化扩展点章节：用“定义层改写 vs 实例层改写”组织内容，verify why.md#requirement-r5-bfpp-bpp-and-extension-points in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 5.2 深化处理器顺序章节：给出可执行的排序/分段规则与观察点，verify why.md#requirement-r5-bfpp-bpp-and-extension-points in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 5.3 深化容器 bootstrap 章节：把“注解为何生效”拆成处理器速查表与时间线，verify why.md#requirement-r5-bfpp-bpp-and-extension-points in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 5.4 深化 BDRPP：把“注册阶段动态加定义”写成可复现/可排障版本，verify why.md#requirement-r5-bfpp-bpp-and-extension-points in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 6. 循环依赖边界（Circular / Early Reference）

- [-] 6.1 深化循环依赖章节：分型（constructor/setter/proxy）+ 最短排障链路，verify why.md#requirement-r6-circular-dependency-boundary in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 6.2 深化 early reference 章节：聚焦 getEarlyBeanReference 的作用与限制，verify why.md#requirement-r6-circular-dependency-boundary in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 7. FactoryBean（Product vs Factory）

- [-] 7.1 深化 FactoryBean 基础章节：`&` 前缀、product/factory、缓存语义，verify why.md#requirement-r7-factorybean-and-product-vs-factory in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 7.2 深化 FactoryBean 深潜章节：类型匹配、边界 case、排障套路，verify why.md#requirement-r7-factorybean-and-product-vs-factory in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 7.3 深化 FactoryBean 边界章节：getObjectType=null 等导致的“发现失败”，verify why.md#requirement-r7-factorybean-and-product-vs-factory in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 8. 配置类增强与代理（@Configuration / Proxying Phase）

- [-] 8.1 深化配置类增强章节：proxyBeanMethods/调用语义/反例，verify why.md#requirement-r8-configuration-enhancement-and-proxying in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 8.2 深化代理替换阶段章节：BPP 如何换掉最终暴露对象，verify why.md#requirement-r8-configuration-enhancement-and-proxying in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 8.3 深化“实例化前短路”章节：postProcessBeforeInstantiation 的使用与风险，verify why.md#requirement-r8-configuration-enhancement-and-proxying in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 9. Scope 与 Scoped Proxy（Scope / Provider / Lookup）

- [-] 9.1 深化 scope 与 prototype 注入陷阱：对比修复手段与边界，verify why.md#requirement-r9-scope-and-scoped-proxy in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 9.2 深化自定义 scope 与 scoped proxy：thread scope 语义与注入陷阱，verify why.md#requirement-r9-scope-and-scoped-proxy in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 10. 值解析 / SpEL / 类型转换（Value / SpEL / Conversion）

- [-] 10.1 深化占位符解析：strict vs non-strict、排障入口，verify why.md#requirement-r10-value-spel-type-conversion in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 10.2 深化类型转换：BeanWrapper/ConversionService/PropertyEditor 边界，verify why.md#requirement-r10-value-spel-type-conversion in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 10.3 深化 SpEL：`@Value("#{...}")` 的解析链路与常见误区，verify why.md#requirement-r10-value-spel-type-conversion in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 10.4 深化值解析收口：PropertyEditor 与 BeanDefinition 值如何落到对象，verify why.md#requirement-r10-value-spel-type-conversion in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 10.5 深化 Environment 与 PropertySource：把占位符/值解析放回 property sources 优先级与排障主线，verify why.md#requirement-r10-value-spel-type-conversion in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。

## 11. 面试与内训交付（Interview / Training）

- [-] 11.1 重写面试复述模板：按主题输出“主线+边界+证据链”，verify why.md#requirement-r11-interview-and-training-kit in `spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 11.2 增强生产排障清单：异常分型→入口→观察点→修复策略，verify why.md#requirement-r11-interview-and-training-kit in `spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 11.3 增强自测题：按主题覆盖 + 标准答案骨架提示，verify why.md#requirement-r11-interview-and-training-kit in `spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 11.4 增强常见坑清单：按主题补齐“现象→根因→断点→修复”，verify why.md#requirement-r11-interview-and-training-kit in `spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [-] 11.5 增强术语表：统一术语/同义词/易混淆点与对应章节入口，verify why.md#requirement-r11-interview-and-training-kit in `spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`
  > Note: 本次方案包聚焦“团队内训讲义 + 入口接入”，该任务延后到后续方案包按反馈继续深化。
- [√] 11.6 新增内训讲义（课时脚本）：按 60/90/120 分钟拆分教学单元 + 配套 Labs/练习题，verify why.md#requirement-r11-interview-and-training-kit in `spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`

## 12. Knowledge Base 同步（SSOT）

- [√] 12.1 更新知识库模块文档：同步入口/路线/关键链接与本次改造进度，verify why.md#requirement-r1-entry-learning-path in `helloagents/wiki/modules/spring-core-beans.md`
- [√] 12.2 更新知识库变更日志：记录本次教程体系改造与影响范围，in `helloagents/CHANGELOG.md`

## 13. Security Check

- [√] 13.1 执行安全自检（G9）：不引入密钥/生产地址；新增测试不访问外部网络；避免高风险命令与不可逆操作

## 14. Testing

- [√] 14.1 跑模块回归：`mvn -pl :spring-core-beans test`（阶段性闭环后执行）

# Task List: spring-core-beans 深挖升级（结构重构 + 知识点补齐 + 可复现实验）

> Note(2026-01-14): 本方案包剩余未执行任务已在后续 Beans 系列方案包中拆分/覆盖，统一标记为 [-]；具体落地请以后续 history 方案包与当前仓库代码为准（见 helloagents/history/index.md）。

Directory: `helloagents/plan/202601051050_spring_core_beans_deepen/`

---

## 0. 执行约定（让 task 变得“可落地、可验收”）
- [-] 0.1 统一章节契约：所有章节至少补齐 A–G（定位/结论/主线/源码锚点/最小实验/坑点/小结预告），并用同一套标题结构；验证 why.md#requirement-r1-docs-navigation
  - A（定位）：这章解决什么问题？读完能回答哪 3 个问题？
  - B（结论）：先给“可复述的结论”（2–5 条），避免只讲过程不落结论
  - C（主线）：把机制放到 refresh 时间线/创建流程中（发生在何时、为什么在那里）
  - D（源码锚点）：给出关键类/关键方法（至少 3 个），并说明“看它能看到什么”
  - E（最小实验）：给出可运行入口（Test 类/方法）+ 断点 + 观察点（watch list）
  - F（坑点）：列出 2–5 个常见坑（症状→根因→修复）
  - G（小结预告）：本章总结 + 下一章承接（告诉读者“下一章看什么、为什么要看”）
- [-] 0.2 统一实验闭环：每个关键机制至少绑定 1 个可运行的 `@Test` 方法（“文档→Lab→断点→观察点→结论复述”闭环）；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 实验绑定要求：
    - 文档必须明确指出“入口测试”与“预期现象/输出/断言”
    - Lab 里必须能让读者复述结论（不是只看到 println）
- [-] 0.3 统一断点降噪：所有“断点闭环”小节必须给出（1）推荐断点（2）条件断点模板（3）watch list（4）最小运行命令；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 推荐输出结构（章节内固定顺序）：
    - Breakpoints：列出类名#方法名（建议 3–8 个）
    - Conditional Breakpoints：给出可复制模板（常用变量：beanName、mbd.beanClassName、beanDefinitionName）
    - Watches：列出 5–10 个重点 watch（例如：mbd、beanWrapper、singletonObjects、earlySingletonObjects）
    - Run：可复制运行命令（类级/方法级）
- [-] 0.4 统一验收命令模板：文档内统一使用 `mvn -pl spring-core-beans -Dtest=<TestClass>#<testMethod> test`（或只到类级）作为可复制命令；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 命令规范：
    - 默认使用方法级命令（更快、更可复现）
    - 若方法级不稳定（受顺序/环境影响），退回类级并说明原因
- [-] 0.5 统一覆盖矩阵维护：`11.*` 覆盖矩阵必须映射到明确的 Doc/Lab；若缺失则必须在 5/6/7 中新增 Doc/Lab 任务补齐；验证 why.md#requirement-r2-bean-knowledge-map
  - 维护规则：
    - 每次新增 Doc/Lab，都要在矩阵中补 1–N 个映射点（避免“写了文章但不知道覆盖了什么”）
    - 每次新增坑点/排障套路，也要反向补回矩阵（对应到章节/实验入口）

## 1. Docs 基础结构与导航（R1）
- [√] 1.1.1 修复 `docs/beans/spring-core-beans/README.md` 现存 TOC/排版异常（重复路径拼接、表格断裂、锚点失效），保证目录可顺读；验证 why.md#requirement-r1-docs-navigation
  - 交付物（本次已完成）：
    - 修复 TOC 中的重复路径拼接/异常排版，使目录可顺读
    - 补齐/修正新增章节与 appendix 的入口（36/37、91/92 等）
  - 验收方式：
    - 从 `docs/beans/spring-core-beans/README.md` 顺读点击：目录锚点可跳转、无断链
- [√] 1.1.2 为 `docs/beans/spring-core-beans/README.md` 增加“主线阅读路线（必读/进阶/查漏补缺）”与“快速定位（按问题找章节）”；验证 why.md#requirement-r1-docs-navigation
  - 交付物（本次已完成）：
    - 新增“快速定位（按问题找章节）”索引，按问题→章节/实验定位
  - 验收方式：
    - 任意选择 2 个“症状型问题”（例如：注入歧义/泛型匹配/类型转换），应能从该索引直达对应章节
- [√] 1.1.3 在 `docs/beans/spring-core-beans/README.md` 增加“章节 ↔ Lab/Test 对照表”（至少覆盖 Part01/03/04 的核心章）；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 交付物（本次已完成）：
    - 新增“章节 ↔ Lab/Test 对照表（精选）”，把关键机制与可运行入口对齐
  - 验收方式：
    - 按表选择 1 个章节，能复制命令精确运行对应 Test（类级或方法级）
- [√] 1.1.4 在 `docs/beans/spring-core-beans/README.md` 增加“索引入口”：术语表、知识点地图、排障速查、自测题；验证 why.md#requirement-r1-docs-navigation
  - 交付物（本次已完成）：
    - 增加 glossary/knowledge map/坑点/自测题的索引入口（便于跳读与查漏）
  - 验收方式：
    - 入口页（glossary/knowledge map/自测）之间互链可用，且均能回到目录页
- [-] 1.2.1 深化 `docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md` 的“First Pass 10 实验”部分：为每个实验补齐（目标/入口测试/推荐断点/观察点/可复述结论 2 句）；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 1.2.2 在 `docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md` 增加“断点降噪模板合集”（按 `beanName`/排除 `org.springframework.*`/仅看用户 bean 等）；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 1.3.1 为 `docs/beans/spring-core-beans/**.md` 统一补齐“上一章｜目录｜下一章”导航（优先使用同一模板，避免手工错链）；验证 why.md#requirement-r1-docs-navigation
- [-] 1.3.2 为所有新增章节（36+）建立一致的“编号/文件名/目录映射规则”，并纳入 `docs/beans/spring-core-beans/README.md`；验证 why.md#requirement-r1-docs-navigation

## 2. Part 01：IoC Container 主线深挖（R2/R3）
- [-] 2.1.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/01-bean-mental-model.md`：补齐“refresh 主线 → 三层模型”的一张总图（阶段/入口类/关键方法/观察点）；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.1.2 为 `01-bean-mental-model.md` 增强“最终暴露对象 3 类替换点（pre/early/after-init）”对照表，并与 docs/15/16/31 互链；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.1.3 为 `01-bean-mental-model.md` 补齐断点闭环（入口 Lab：`spring-core-beans/src/test/java/.../SpringCoreBeansContainerLabTest.java`、`.../SpringCoreBeansProxyingPhaseLabTest.java`；必须含 watch list）；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 2.2.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`：把“注册入口”拆成 4 类并给出落地链路（scan/@Bean/@Import/编程式注册）；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.2.2 为 `01-bean-registration.md` 增强 `@Import` 专区（ImportSelector/DeferredImportSelector/Registrar 的差异、时机、常见坑）并绑定 Labs；验证 why.md#requirement-r2-bean-knowledge-map
- [√] 2.2.3 新增最小实验：component-scan（include/exclude filter、默认命名、重复扫描），并在 `01-bean-registration.md` 给出断点入口；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 交付物（本次已完成）：
    - 新增 Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java`
    - 新增测试用扫描组件（test-only）：`.../componentscan/*`
    - 文档补充 `@ComponentScan` include/exclude filters 说明（与 Lab 对齐）
  - 验收方式：
    - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansComponentScanLabTest test` 通过
- [-] 2.3.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`：补齐 `DependencyDescriptor`/`AutowireCandidateResolver` 在“候选收敛”中的角色（从断点可观察）；验证 why.md#requirement-r2-bean-knowledge-map
- [√] 2.3.2 为 `03-dependency-injection-resolution.md` 补齐“可选依赖三件套对照表”（`@Autowired(required=false)`/`Optional<T>`/`ObjectProvider<T>`），并新增/增强对应 Lab；验证 why.md#requirement-r4-missing-topics-coverage
  - 交付物（本次已完成）：
    - 文档补齐可选依赖的表达方式与行为差异：`@Autowired(required=false)` / `Optional<T>` / `ObjectProvider<T>`
    - 新增 Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansOptionalInjectionLabTest.java`
  - 验收方式：
    - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansOptionalInjectionLabTest test` 通过
- [-] 2.4.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/04-scope-and-prototype.md`：把 prototype 注入陷阱拆成 3 种修复（ObjectProvider/@Lookup/scoped proxy）并绑定可跑实验；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.4.2 为 `04-scope-and-prototype.md` 补齐 `@Lookup` 的源码锚点与断点闭环（增强已存在示例，避免“只讲概念”）；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 2.5.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/05-lifecycle-and-callbacks.md`：补齐“初始化/销毁顺序总表”（Aware/BPP before/@PostConstruct/afterPropertiesSet/initMethod/BPP after/销毁回调）；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.5.2 为 `05-lifecycle-and-callbacks.md` 补齐“prototype 不自动销毁”的风险说明与可复现实验（必要时新增 Lab）；验证 why.md#requirement-r4-missing-topics-coverage
- [-] 2.6.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/06-post-processors.md`：明确 BFPP/BDRPP/BPP 的“改什么/何时改/影响谁”，并配 1 张对照表；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.6.2 为 `06-post-processors.md` 增强“排序与分段”说明（与 docs/14 互链），并绑定 Ordering Lab；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 2.7.1 校准并增强 `docs/beans/spring-core-beans/part-01-ioc-container/07-configuration-enhancement.md`：补齐 Full/Lite/`proxyBeanMethods=false` 的“语义变化/推荐写法/排障套路”；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.7.2 为 `07-configuration-enhancement.md` 补齐断点闭环（ConfigurationClassPostProcessor/Enhancer/BeanMethodInterceptor）与最小运行命令；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 2.8.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/07-factorybean.md`：与 docs/23/29 建立“从入门到深挖”的承接结构，并补齐 `&beanName`/`getObjectType`/缓存语义；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.9.1 深化 `docs/beans/spring-core-beans/part-01-ioc-container/08-circular-dependencies.md`：补齐“能救/不能救”的分类表（singleton setter/constructor/prototype），并与 docs/16 对齐；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 2.9.2 为 `08-circular-dependencies.md` 补齐开关与风险说明（`allowCircularReferences`、raw injection 风险），并必要时新增最小实验；验证 why.md#requirement-r4-missing-topics-coverage

## 3. Part 02：Boot 自动装配与可观察性增强（R5）
- [-] 3.1.1 深化 `docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`：补齐“AutoConfiguration.imports → 注册定义 → 条件化判定 → backoff/override”的主线图；验证 why.md#requirement-r5-troubleshooting-playbook
- [-] 3.1.2 为 `10-spring-boot-auto-configuration.md` 增强“覆盖矩阵”：用户自定义 bean / auto-config bean / 条件化退避的组合表，并绑定 labs（OverrideMatrix/BackoffTiming）；验证 why.md#requirement-r5-troubleshooting-playbook
- [-] 3.2.1 深化 `docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`：补齐“从异常到断点”的定位套路（NoSuch/NoUnique/UnsatisfiedDependency/条件不命中）；验证 why.md#requirement-r5-troubleshooting-playbook
- [-] 3.2.2 为 `11-debugging-and-observability.md` 增强“可观察性工具箱”：ConditionEvaluationReport、BeanDefinition origin、BeanGraph dump、关键 logger 列表；验证 why.md#requirement-r5-troubleshooting-playbook

## 4. Part 03：容器内部机制（源码级）补强（R2/R3）
- [-] 4.1.1 深化 `docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`：列出“哪些基础设施处理器让注解生效”（Autowired、PostConstruct、Resource、Configuration 解析等）并绑定 Lab；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 4.2.1 深化 `docs/beans/spring-core-beans/part-03-container-internals/02-bdrpp-definition-registration.md`：补齐“BDRPP 能再注册定义”的典型用法与风险（循环发现、多轮扫描）；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 4.3.1 深化 `docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md`：补齐“分段执行 + 组内排序 + internal BPP 末尾挪动”的三层模型，并给出反例；验证 why.md#requirement-r3-labsexercises-close-the-loop
- [-] 4.4.1 深化 `docs/beans/spring-core-beans/part-03-container-internals/04-pre-instantiation-short-circuit.md`：把“实例化前短路”与 AOP/代理替换对齐成一张时序图；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 4.5.1 深化 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`：补齐三级缓存/early reference 与 AOP 交互的边界条件，并绑定可观察点；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 4.6.1 深化 `docs/beans/spring-core-beans/part-03-container-internals/06-lifecycle-callback-order.md`：补齐“回调顺序稳定表 + 验证断言点”，并与 docs/05 互链；验证 why.md#requirement-r3-labsexercises-close-the-loop

## 5. Part 04：装配语义与边界案例补齐（R2/R4）
- [-] 5.1.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-lazy-semantics.md`：补齐“bean 懒加载 vs 注入点懒代理”对照表与断点闭环；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.2.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/02-depends-on.md`：补齐依赖边记录/销毁顺序影响，并与 BeanGraph dump 互链；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.3.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/03-resolvable-dependency.md`：补齐“能注入但不是 bean”的判定与常见对象清单；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.4.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/04-context-hierarchy.md`：补齐跨层级查找、覆盖、事件传播的排障套路；验证 why.md#requirement-r5-troubleshooting-playbook
- [-] 5.5.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`：补齐 alias/`&beanName`/按名回退的关联关系；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.6.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`：补齐 `getObjectType/isSingleton` 与注入匹配/缓存的关系，并绑定 edge cases；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.7.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`：补齐覆盖开关/来源定位/典型冲突矩阵，并绑定 Boot OverrideMatrix；验证 why.md#requirement-r5-troubleshooting-playbook
- [-] 5.8.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`：补齐“编程式注册 BPP/注册 BeanDefinition”的边界与排障；验证 why.md#requirement-r4-missing-topics-coverage
- [-] 5.9.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`：补齐与 `finishBeanFactoryInitialization` 的时间线关系；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.10.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`：补齐 phase 控制、stop 顺序与依赖边关系；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.11.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`：补齐 scoped proxy 注入长生命周期 bean 的典型模式与观察点；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.12.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`：补齐早期初始化、类型预测失败、与 BPP 交互的边界；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.13.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`：补齐“构造器选择算法/注入时机/循环依赖边界”的对照；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.14.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`：补齐“代理产生 3 时机 + self-invocation 失效原因 + early proxy 风险”；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.15.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`：补齐 `@Resource` 与 `@Autowired` 的决策差异与排障；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.16.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`：补齐“单依赖选择 vs 集合排序”对照表与断点闭环；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.17.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`：补齐 PropertySource/PlaceholderConfigurer/SpEL 的边界与排障；验证 why.md#requirement-r2-bean-knowledge-map
- [-] 5.18.1 深化 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-merged-bean-definition.md`：补齐“registry 看到的定义 vs 创建时 merged 定义”对照与断点观察；验证 why.md#requirement-r2-bean-knowledge-map
- [√] 5.19.1 新增章节 36：类型转换链路（`BeanWrapper`/`PropertyEditor`/`ConversionService`/`TypeDescriptor`）+ 最小实验；验证 why.md#requirement-r4-missing-topics-coverage
  - 交付物（本次已完成）：
    - 新增文档：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`
    - 新增 Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansTypeConversionLabTest.java`
  - 验收方式：
    - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansTypeConversionLabTest test` 通过
- [√] 5.20.1 新增章节 37：泛型匹配与注入坑（`ResolvableType`/擦除/bridge method）+ 绑定现有坑题 Lab；验证 why.md#requirement-r4-missing-topics-coverage
  - 交付物（本次已完成）：
    - 新增文档：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md`
    - 与既有坑题 Lab 对齐：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
  - 验收方式：
    - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test` 通过

## 6. Labs/Exercises 对齐与增强（R3/R5）
- [√] 6.1.1 新增 Lab：component-scan（过滤器/命名/冲突/扫描边界），用于补齐 11.A10–11.A12；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 交付物（本次已完成）：
    - `SpringCoreBeansComponentScanLabTest` + componentscan 测试组件集
  - 验收方式：
    - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansComponentScanLabTest test` 通过
- [√] 6.1.2 新增 Lab：Profile/Environment（`@Profile` 生效时机与注册边界），用于补齐 11.A22；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 交付物（本次已完成）：
    - 新增 Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansProfileRegistrationLabTest.java`
  - 验收方式：
    - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansProfileRegistrationLabTest test` 通过
- [√] 6.1.3 新增/增强 Lab：可选依赖（`@Autowired(required=false)`/`Optional<T>`/`@Nullable`），用于补齐 11.B6–11.B7；验证 why.md#requirement-r3-labsexercises-close-the-loop
  - 交付物（本次已完成）：
    - 新增 Lab：`SpringCoreBeansOptionalInjectionLabTest`（覆盖 required=false/Optional/ObjectProvider）
  - Note: `@Nullable` 的运行态效果与 required=false/Optional 高度接近，本次先通过文档说明 + Lab 覆盖主要行为；如需“强制纳入代码示例”，可在后续迭代补 1 个 @Nullable 注入点对照。
- [-] 6.1.4 新增 Lab：JSR-330 `@Inject`/`Provider<T>`（与 `@Autowired`/`ObjectProvider` 对照），用于补齐 11.B8/11.B13；验证 why.md#requirement-r4-missing-topics-coverage
- [√] 6.1.5 新增 Lab：类型转换（ConversionService/BeanWrapper），用于补齐 11.B24–11.B26 与 11.D29；验证 why.md#requirement-r4-missing-topics-coverage
  - 交付物（本次已完成）：
    - 新增 Lab：`SpringCoreBeansTypeConversionLabTest`（覆盖 populateBean 阶段的属性值转换 + 自定义 ConversionService）
- [-] 6.2.1 增强 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanGraphDumper.java`：补齐候选集合/依赖边/销毁顺序相关 dump；验证 why.md#requirement-r5-troubleshooting-playbook
- [-] 6.2.2 增强 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanDefinitionOriginDumper.java`：补齐 BeanDefinition 来源追踪与输出格式（便于对照文档）；验证 why.md#requirement-r5-troubleshooting-playbook

## 7. Appendix 与索引能力建设（R1/R5）
- [√] 7.1.1 新增术语表（glossary）：把核心名词（BeanDefinition/Scope/BPP/BFPP/BDRPP/early reference/FactoryBean 等）统一定义，并链接到对应章节；验证 why.md#requirement-r1-docs-navigation
  - 交付物（本次已完成）：
    - 新增：`docs/beans/spring-core-beans/appendix/02-glossary.md`
- [√] 7.1.2 新增“知识点地图”：把 `11.*` 覆盖矩阵整理成“知识点 → 章节 → Lab/Test”的索引页（可供跳读定位）；验证 why.md#requirement-r1-docs-navigation
  - 交付物（本次已完成）：
    - 新增：`docs/beans/spring-core-beans/appendix/03-knowledge-map.md`
- [-] 7.2.1 重写 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md`：按“症状→根因→断点入口→修复策略→相关章节/实验”结构输出；验证 why.md#requirement-r5-troubleshooting-playbook
  - 交付物（本次已完成）：
    - 新增坑点条目：泛型匹配不可靠、类型转换不只发生在 `@Value`
    - 修复 appendix/90 指向各 Part 的相对链接（避免断链）
  - Note: 本次以“补齐关键坑点 + 修复链接”为主；后续如要按 Playbook 模板全面重写，可再拆分为“按症状分组 + 每条补齐断点入口/修复策略”的子任务执行。
- [√] 7.3.1 更新 `docs/beans/spring-core-beans/appendix/99-self-check.md`：为新增章节与关键机制补齐自测题与答案要点；验证 why.md#requirement-r1-docs-navigation
  - 交付物（本次已完成）：
    - 新增自测：类型转换（34/36）、泛型匹配（37/29）

## 8. Security Check
- [√] 8.1 执行安全自检（G9）：确保无敏感信息、无高风险命令、无生产环境操作暗示
  - 交付物（本次已完成）：
    - 已对新增/修改文件做敏感信息与高风险关键字扫描（未发现密钥/私钥/生产环境操作暗示）

## 9. Documentation Update（Knowledge Base 同步）
- [√] 9.1 更新 `helloagents/wiki/**`：补齐 `spring-core-beans` 模块索引、学习路径与变更记录入口
  - 交付物（本次已完成）：
    - 更新：`helloagents/wiki/modules/spring-core-beans.md`（Highlights/Last Updated/Change History）
- [√] 9.2 更新 `helloagents/CHANGELOG.md`：记录本次文档与实验体系增强
  - 交付物（本次已完成）：
    - 更新：`helloagents/CHANGELOG.md`（补充本次 docs+Labs 增强记录）

## 10. Testing
- [√] 10.1 运行 `mvn -pl spring-core-beans test` 并修复因本次变更引入的失败（仅修复本次相关）
  - 交付物（本次已完成）：
    - 已执行并通过：`mvn -pl spring-core-beans test`
- [-] 10.2 抽查关键 Labs：精确到方法运行，确保文档所指向的实验可复现（至少覆盖：DI 候选选择/ordering/early reference/proxying/value placeholder）
- [√] 10.3 抽查文档链接：从 `docs/beans/spring-core-beans/README.md` 顺读，确保新增章节与 appendix 索引无断链
  - 交付物（本次已完成）：
    - 已批量修复 docs 内跨 Part 的相对链接，并对 docs 全量执行“相对路径存在性检查”（结果：0 断链）

---

## 11. Spring Bean 全量知识点覆盖矩阵（A/B/C/D 全量，逐条映射到 Doc/Lab）

> 说明：本矩阵用于确保“覆盖面不缺项”。每一项都必须能指向：
> - Doc：主要承载章节（不存在则在 5/7 中新增）
> - Lab：可运行实验（不存在则在 6 中新增）
> 执行时允许把多个知识点合并落到同一章/同一组实验里，但必须在该章节明确覆盖并可复现。

### 11.A 定义与注册（Definition / Registration / Configuration Parsing）
- [-] 11.A1 Bean 三层模型：`BeanDefinition` ≠ bean instance ≠ proxy（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-mental-model.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`）
- [-] 11.A2 “容器管理”的含义：IoC、DI、依赖图、候选、注入点、scope、lifecycle（Doc：术语表/知识点地图；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`）
- [-] 11.A3 `BeanFactory` vs `ApplicationContext`：能力边界、典型场景选择、性能/功能取舍（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-mental-model.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java`）
- [-] 11.A4 `ApplicationContext#refresh()` 时间线：关键阶段与“定义层/实例层/代理层”发生点（Doc：`docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`）
- [-] 11.A5 `BeanDefinition` 类型谱系：`BeanDefinition` / `RootBeanDefinition` / `GenericBeanDefinition` / `AnnotatedBeanDefinition` / `BeanDefinitionHolder`（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-merged-bean-definition.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`）
- [-] 11.A6 `RootBeanDefinition` 关键字段语义：`scope`、`lazyInit`、`dependsOn`、`primary`、`autowireCandidate`、`role`、`synthetic`、`factoryMethod*`、`init/destroyMethod`（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-merged-bean-definition.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`）
- [-] 11.A7 Bean 元信息来源：注解元数据（`AnnotationMetadata`）如何变成 `BeanDefinition`（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`）
- [-] 11.A8 Bean 注册入口总览：component-scan / `@Bean` / `@Import` / XML/编程式注册（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java`）
- [-] 11.A9 `BeanDefinitionRegistry` 与 `DefaultListableBeanFactory`：注册表数据结构与查找路径（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`）
- [-] 11.A10 `ClassPathScanningCandidateComponentProvider`：扫描候选判定、元注解识别、过滤器机制（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：新增 `SpringCoreBeansComponentScanLabTest`）
- [-] 11.A11 `@ComponentScan`：basePackages、过滤器、扫描边界、重复扫描与性能（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：新增 `SpringCoreBeansComponentScanLabTest`）
- [-] 11.A12 stereotype 注解语义：`@Component/@Service/@Repository/@Controller` 的差异与边界（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：新增 `SpringCoreBeansComponentScanLabTest`）
- [-] 11.A13 `@Bean` 方法注册语义：beanName 默认规则、`name/value`、返回类型与可见性、方法参数注入（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`）
- [-] 11.A14 `@Configuration` Full vs Lite：配置类解析、CGLIB 增强存在的原因（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/07-configuration-enhancement.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`）
- [-] 11.A15 `proxyBeanMethods=false`：性能收益、语义变化、适用/禁用条件（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/07-configuration-enhancement.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`）
- [-] 11.A16 `@Import` 三种形态：导入普通类/配置类/Selector/Registrar（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java`）
- [-] 11.A17 `ImportSelector`：选择器返回类名列表的时机与可观测点（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java`）
- [-] 11.A18 `DeferredImportSelector`：延迟导入的目的、排序、与自动装配的关系（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java`）
- [-] 11.A19 `ImportBeanDefinitionRegistrar`：编程式注册定义、常见用法与坑（Doc：`docs/beans/spring-core-beans/part-03-container-internals/02-bdrpp-definition-registration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`）
- [-] 11.A20 `@Enable*` 设计模式：本质=“导入 + 注册基础设施 Bean”（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationLabTest.java`）
- [-] 11.A21 条件化注册：`@Conditional` 基础模型、Condition 执行时机（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java`）
- [-] 11.A22 `@Profile`：定义是否进入容器、与配置文件/环境变量的关系（Doc：补充到 `docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`；Lab：新增 `SpringCoreBeansProfileRegistrationLabTest`）
- [-] 11.A23 BeanDefinition 的“装饰/增强”：`@ConfigurationClassPostProcessor` 把注解变定义的关键路径（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`）
- [-] 11.A24 BeanDefinition 合并（Merged）：`getMergedLocalBeanDefinition` 为什么存在、何时发生、对观察结果影响（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-merged-bean-definition.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`）
- [-] 11.A25 BeanDefinition 覆盖（overriding）：同名 bean 覆盖策略、Boot 开关与排查手法（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`）
- [-] 11.A26 Bean 名称与别名：命名规则、alias、`&beanName`（FactoryBean 前缀）对查找的影响（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`）
- [-] 11.A27 `@DependsOn` 定义层语义：强制初始化顺序、为何容易被滥用（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/02-depends-on.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`）
- [-] 11.A28 `@Lazy` 定义层语义：bean 懒加载 vs 注入点懒加载（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-lazy-semantics.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`）
- [-] 11.A29 `@Role/@Description`：区分应用/基础设施 Bean、排障价值（Doc：补充到 `docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：新增最小断言（可并入 BootstrapInternals Lab））
- [-] 11.A30 `@PropertySource`：属性源引入与 bean 注册/解析顺序的关系（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`）
- [-] 11.A31 父子容器（context hierarchy）：查找优先级、同名覆盖、事件传播与资源加载差异（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/04-context-hierarchy.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`）
- [-] 11.A32 `BeanFactoryUtils`：跨层级查找的工具方法与使用边界（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/04-context-hierarchy.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`）
- [-] 11.A33 编程式注册：`registerBeanDefinition` / `registerSingleton` / `registerBean`（Boot 3）差异（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`；Lab：新增 `SpringCoreBeansProgrammaticRegistrationLabTest`）
- [-] 11.A34 “基础设施 Bean”清单：内置处理器/解析器为何是 Bean、何时被注册（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`）
- [-] 11.A35 自动装配导入来源：`AutoConfiguration.imports` 的装配入口与“定义何时注册”（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationLabTest.java`）

### 11.B 依赖解析与注入（Autowiring / Resolution / Type Conversion / @Value）
- [-] 11.B1 注入方式对比：构造器/Setter/字段/方法参数注入的差异与推荐（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`）
- [-] 11.B2 注入发生位置：`populateBean` / `postProcessProperties` / `doResolveDependency` 链路（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`）
- [-] 11.B3 `DependencyDescriptor`：注入点描述符包含信息（required、泛型、注解、参数名等）（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`（断点观察））
- [-] 11.B4 `AutowireCandidateResolver`：候选判定的真正入口（Qualifier/Value/Lazy 等）（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`（断点观察））
- [-] 11.B5 候选选择总流程：按类型找候选 → 过滤候选 → 决策唯一候选 → 失败异常（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`）
- [-] 11.B6 `@Autowired(required=...)`：必需/可选依赖行为差异（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：新增可选依赖 Lab）
- [-] 11.B7 `@Nullable` / `Optional<T>`：可选注入语义与边界（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：新增可选依赖 Lab）
- [-] 11.B8 `ObjectProvider<T>` / `ObjectFactory<T>` / `Provider<T>`：延迟获取、规避循环依赖、按需查找（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/04-scope-and-prototype.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`）
- [-] 11.B9 `@Qualifier`：匹配规则、自定义 Qualifier 注解、组合限定策略（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionAmbiguityLabTest.java`）
- [-] 11.B10 `@Primary`：默认候选与冲突行为（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`）
- [-] 11.B11 以名称回退：参数名/字段名参与匹配的条件与差异（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`（对照））
- [-] 11.B12 `@Resource`（JSR-250）：name-first 的解析顺序与和 `@Autowired` 的差异（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`）
- [-] 11.B13 `@Inject`（JSR-330）：与 `@Autowired` 的差异点（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：新增 JSR-330 Lab）
- [-] 11.B14 集合注入：`List<T>`/`Set<T>`/`Map<String,T>` 的收集规则、空集合 vs 缺失异常（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`（集合场景））
- [-] 11.B15 集合排序：`@Order` / `Ordered` / `@Priority` 的作用域（排序 vs 选择）（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`）
- [-] 11.B16 泛型匹配：`ResolvableType` 如何影响候选筛选（Doc：新增章节 37；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`）
- [-] 11.B17 泛型坑专题：通配符、继承层级、bridge method、擦除导致的“看起来能注入/实际不能”（Doc：新增章节 37；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`）
- [-] 11.B18 `FactoryBean<T>` 注入时的类型推断：`getObjectType()`、预测类型、泛型对齐（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`）
- [-] 11.B19 `@Lazy` 注入点语义：懒代理注入、何时触发真实 bean 创建（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-lazy-semantics.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`）
- [-] 11.B20 `@Value` 占位符：`${}` 解析、缺失 key 的 strict vs non-strict（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`）
- [-] 11.B21 `@Value` + SpEL：`#{}` 表达式上下文、可访问对象、常见坑（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`）
- [-] 11.B22 `PropertySourcesPlaceholderConfigurer`：为何属于 BFPP、为何顺序重要（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`）
- [-] 11.B23 EmbeddedValueResolver：容器如何提供字符串解析能力给注入体系（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`）
- [-] 11.B24 类型转换总览：`BeanWrapper`、`PropertyEditor`、`ConversionService`、`TypeDescriptor` 的关系（Doc：新增章节 36；Lab：新增类型转换 Lab）
- [-] 11.B25 `BeanWrapperImpl`：属性填充时的 setter 解析与转换链路（Doc：新增章节 36；Lab：新增类型转换 Lab）
- [-] 11.B26 `ConversionService`：默认实现、注册 Converter、与 Spring MVC/DataBinder 的边界（Doc：新增章节 36；Lab：新增类型转换 Lab）
- [-] 11.B27 “属性绑定 ≠ 依赖注入”：`@ConfigurationProperties` 与 `@Value` 的适用边界（Doc：补充到新增章节 36 或 docs/34；Lab：视需要新增最小例子）
- [-] 11.B28 构造器选择算法：多构造器、`@Autowired`、参数可解析性、歧义（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`）
- [-] 11.B29 循环依赖与注入方式：构造器循环为何更难救（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/08-circular-dependencies.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`）
- [-] 11.B30 `@DependsOn` 对注入的间接影响：先实例化谁，为什么会改变观察结果（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/02-depends-on.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`）
- [-] 11.B31 `MethodParameter` 与参数名发现：-parameters/debug 信息对“按名回退”的影响（Doc：补充到 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`；Lab：可通过断点观察）
- [-] 11.B32 `ResolvableDependency` 注入：为何 `ApplicationContext/Environment/ResourceLoader` 能注入但不是 bean（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/03-resolvable-dependency.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java`）
- [-] 11.B33 `AutowiredAnnotationBeanPostProcessor`：核心职责与介入点（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`）
- [-] 11.B34 `CommonAnnotationBeanPostProcessor`：`@Resource/@PostConstruct` 等为何能生效（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`）
- [-] 11.B35 注入失败异常族谱：`UnsatisfiedDependencyException`/`NoSuchBeanDefinitionException`/`NoUniqueBeanDefinitionException` 定位套路（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java`）

### 11.C 生命周期与扩展点（Lifecycle / Post-Processors / refresh 主线）
- [-] 11.C1 refresh 六步/十二步主线图：每一步“改定义还是改实例”（Doc：`docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`）
- [-] 11.C2 `invokeBeanFactoryPostProcessors`：BFPP/BDRPP 执行顺序与时机（Doc：`docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`）
- [-] 11.C3 `registerBeanPostProcessors`：为何 BPP 必须先注册，否则哪些能力会失效（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`）
- [-] 11.C4 `finishBeanFactoryInitialization`：为何会触发单例预实例化（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`）
- [-] 11.C5 `preInstantiateSingletons`：哪些 bean 会提前创建、哪些不会（lazy/FactoryBean/Smart* 等）（Doc：`docs/beans/spring-core-beans/part-03-container-internals/04-pre-instantiation-short-circuit.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`）
- [-] 11.C6 Bean 创建主流程：`createBean` → `doCreateBean` → instantiate/populate/initialize（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/01-bean-mental-model.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`）
- [-] 11.C7 “实例化前短路”：`postProcessBeforeInstantiation` 返回代理/替身对象（Doc：`docs/beans/spring-core-beans/part-03-container-internals/04-pre-instantiation-short-circuit.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`）
- [-] 11.C8 属性填充阶段：`postProcessProperties` 与 `populateBean` 的协作关系（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`）
- [-] 11.C9 初始化阶段：Aware 回调族（BeanName/BeanFactory/ApplicationContext/Environment/ResourceLoader 等）（Doc：`docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`）
- [-] 11.C10 初始化回调顺序：`@PostConstruct` / `InitializingBean` / `initMethod` / BPP before/after（Doc：`docs/beans/spring-core-beans/part-03-container-internals/06-lifecycle-callback-order.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`）
- [-] 11.C11 销毁回调顺序：`@PreDestroy` / `DisposableBean` / `destroyMethod`（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/05-lifecycle-and-callbacks.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java`）
- [-] 11.C12 scope 与销毁语义：singleton vs prototype（prototype 默认不销毁）与资源泄露风险（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/04-scope-and-prototype.md`；Lab：补充/新增 prototype 销毁最小实验）
- [-] 11.C13 `BeanPostProcessor` 体系总览：注入、生命周期注解、AOP 各自依赖哪些 BPP（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/06-post-processors.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`）
- [-] 11.C14 `InstantiationAwareBeanPostProcessor`：介入点与常见用途（构造器决策/提前代理/属性填充）（Doc：`docs/beans/spring-core-beans/part-03-container-internals/04-pre-instantiation-short-circuit.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java`）
- [-] 11.C15 `SmartInstantiationAwareBeanPostProcessor`：预测类型、early reference、循环依赖 + AOP 的关键（Doc：`docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`）
- [-] 11.C16 `MergedBeanDefinitionPostProcessor`：为何 merged definition 重要（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-merged-bean-definition.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`）
- [-] 11.C17 BFPP vs BPP：改定义 vs 改实例（需能举例）（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/06-post-processors.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`）
- [-] 11.C18 `PriorityOrdered/Ordered`：处理器排序规则与常见误区（Doc：`docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`）
- [-] 11.C19 `SmartInitializingSingleton`：所有单例创建完之后的一次性回调（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java`）
- [-] 11.C20 `Lifecycle` / `SmartLifecycle`：start/stop、phase、与容器关闭顺序（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`）
- [-] 11.C21 依赖关系记录：`dependentBeanMap` 如何影响销毁顺序与排障（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/02-depends-on.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`）
- [-] 11.C22 单例缓存结构：`singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的语义（Doc：`docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`）
- [-] 11.C23 “创建中”标记：`singletonsCurrentlyInCreation` 与异常定位（Doc：`docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`）
- [-] 11.C24 `FactoryBean` 的生命周期位置：FactoryBean 本体与产物的创建/缓存差异（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`）
- [-] 11.C25 事件与生命周期：`ContextRefreshedEvent/ContextClosedEvent` 与初始化/销毁配合套路（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`；Lab：视需要新增最小事件示例或跨模块引用 `spring-core-events`）

### 11.D 高级机制 / 边界条件 / 排障（AOP / FactoryBean / Scope / Circular / Boot Observability）
- [-] 11.D1 AOP 代理产生时机：before-instantiation / after-initialization / early-reference 三条路径（Doc：`docs/beans/spring-core-beans/part-03-container-internals/04-pre-instantiation-short-circuit.md` + `.../05-early-reference-and-circular.md` + `.../14-proxying-phase-bpp-wraps-bean.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`）
- [-] 11.D2 `AbstractAutoProxyCreator`：自动代理创建器的核心决策点（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`）
- [-] 11.D3 `getEarlyBeanReference`：为何循环依赖 + AOP 会扯到 early proxy（Doc：`docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`）
- [-] 11.D4 self-invocation：为何注解事务/缓存/异步会失效、典型修复方式（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`）
- [-] 11.D5 JDK 代理 vs CGLIB：接口/类代理差异、final 限制、equals/hashCode/toString 行为（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`）
- [-] 11.D6 `proxyTargetClass` 与选择策略：为何有时必须 CGLIB（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`；Lab：视需要新增最小代理策略实验或跨模块引用 `spring-core-aop`）
- [-] 11.D7 `exposeProxy` 与 AopContext：如何解决 self-invocation（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`；Lab：视需要新增最小实验或跨模块引用）
- [-] 11.D8 `FactoryBean` 基本语义：`getBean("x")` 是产物；`&x` 才是工厂（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/07-factorybean.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`）
- [-] 11.D9 `FactoryBean#getObjectType()`：类型预测、注入匹配、与泛型的坑（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`）
- [-] 11.D10 `FactoryBean#isSingleton()`：产物缓存语义与创建次数（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`）
- [-] 11.D11 FactoryBean 边界案例：早期初始化、`SmartFactoryBean`、与 BPP 交互（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`）
- [-] 11.D12 自定义 Scope：实现要点、生命周期、线程隔离（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`）
- [-] 11.D13 Scoped Proxy：短生命周期 bean 注入 singleton 的代理机制（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`）
- [-] 11.D14 `request/session` scope：Web 场景下代理与上下文获取（可选增强）（Doc：在 docs/28 增加“Web scope 提示与边界”，或跨模块引用 `springboot-web-mvc`；Lab：可选新增）
- [-] 11.D15 循环依赖分类：singleton setter 可救、constructor 通常无解、prototype 基本无解（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/08-circular-dependencies.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`）
- [-] 11.D16 `allowCircularReferences`：开关语义与风险（Doc：补充到 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`（可扩展））
- [-] 11.D17 `allowRawInjectionDespiteWrapping`：raw bean 注入导致“代理前后不一致”的坑（Doc：补充到 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`；Lab：视需要新增最小复现实验）
- [-] 11.D18 `@Lazy` 作为解环手段：为何能推迟取 bean 从而缓解环（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-lazy-semantics.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`）
- [-] 11.D19 `@DependsOn` 引入的“伪循环/死锁式问题”与排查（Doc：补充到 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/02-depends-on.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`）
- [-] 11.D20 BeanDefinition 覆盖排障：同名 bean 到底来自哪里、谁覆盖了谁（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java`）
- [-] 11.D21 Boot 条件化退避：`@ConditionalOnMissingBean` 等为何让你的 bean 不生效（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java`）
- [-] 11.D22 ConditionEvaluationReport：如何读 report、如何定位“没生效原因”（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java`）
- [-] 11.D23 BeanDefinition origin tracing：定位“这个 bean 从哪里注册来的”（Doc：`docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java`）
- [-] 11.D24 Debug 日志策略：开启哪些 logger 能最快定位“为何有/为何无”（Doc：补充到 `docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`；Lab：无（以操作清单为主））
- [-] 11.D25 Actuator `/beans` `/conditions`：排障入口（可选增强）（Doc：在 docs/11 增加“可选：Actuator”小节，并链接到 `springboot-actuator` 模块；Lab：可选）
- [-] 11.D26 PostProcessor 排障：为何顺序不同会导致行为完全不同（Doc：`docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`）
- [-] 11.D27 `@Configuration(proxyBeanMethods=false)` 排障：为何 bean 不是同一个实例（Doc：`docs/beans/spring-core-beans/part-01-ioc-container/07-configuration-enhancement.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`）
- [-] 11.D28 泛型匹配排障：看起来类型对，为什么注入失败（Doc：新增章节 37；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`）
- [-] 11.D29 类型转换排障：为何属性填充失败、转换器去哪儿注册（Doc：新增章节 36；Lab：新增类型转换 Lab）
- [-] 11.D30 代理排障：如何判断注入到的是代理还是原对象、断点该打在哪（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`）
- [-] 11.D31 MergedBeanDefinition 排障：为何 registry 里的定义与创建时看到的不一样（Doc：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-merged-bean-definition.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`）
- [-] 11.D32 编程式注册排障：动态注册导致的启动顺序/覆盖/条件失效（Doc：`docs/beans/spring-core-beans/part-03-container-internals/02-bdrpp-definition-registration.md` + `.../08-programmatic-bpp-registration.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`）
- [-] 11.D33 性能与启动优化：哪些 bean 最耗时、如何用最小实验定位（Doc：补充到 `docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`；Lab：以操作手册为主）
- [-] 11.D34 可测试性：如何用 JUnit 最小启动器复现某个容器机制（Doc：`docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md`；Lab：全模块 Labs）
- [-] 11.D35 “症状→断点入口”套路表：注入失败/代理异常/循环依赖/条件不命中各自优先断点（Doc：`docs/beans/spring-core-beans/appendix/90-common-pitfalls.md`；Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansExceptionNavigationLabTest.java`）

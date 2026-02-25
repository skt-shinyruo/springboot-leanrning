# Task List: deepen_beans_aop_tx_web

Directory: `helloagents/plan/202601091043_deepen_beans_aop_tx_web/`

---

## 0. 全局约定与学习路径统一（cross-cutting）

> 目标：把四个模块的“学习入口/文档结构/测试入口/排障路线”做成一致的体验。  
> 验证：`why.md#requirement-r0-learning-path-unification` + `why.md#scenario-s0-docs-lab-binding`

### 0.1 统一学习路径的“固定结构”与命名约定
- [ ] 0.1.1 在 `helloagents/project.md` 补充统一约定：Lab/Exercise/Solution 的语义、命名规则、断言粒度原则，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.1.2 补充统一“文档 ↔ 测试”映射规范：章节末尾必须列出“推荐测试入口 + 推荐断点 + 推荐观察点”，落到 `helloagents/project.md`，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.1.3 明确“排障条目模板”：现象/根因/如何验证（指向测试）/如何断点/如何修复（教学层面），落到 `helloagents/project.md`，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`

### 0.2 对齐四模块 Deep Dive Guide（统一入口与阅读顺序）
- [ ] 0.2.1 对齐 Beans Deep Dive Guide（`docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md`）：加入“推荐阅读顺序 + 推荐测试入口索引 + 推荐断点索引”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.2.2 对齐 AOP Deep Dive Guide（`docs/aop/spring-core-aop/part-00-guide/00-deep-dive-guide.md`）：加入“推荐阅读顺序 + 推荐测试入口索引 + 推荐断点索引”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.2.3 对齐 Tx Deep Dive Guide（`docs/tx/spring-core-tx/part-00-guide/00-deep-dive-guide.md`）：加入“推荐阅读顺序 + 推荐测试入口索引 + 推荐断点索引”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.2.4 对齐 Web MVC Deep Dive Guide（`docs/web-mvc/springboot-web-mvc/part-00-guide/00-deep-dive-guide.md`）：加入“推荐阅读顺序 + 推荐测试入口索引 + 推荐断点索引”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`

### 0.3 对齐四模块 module README（把“怎么学/怎么跑”放在最显眼位置）
- [ ] 0.3.1 更新 Beans README：在 `spring-core-beans/README.md` 增加“学习路径（docs）→ 推荐 tests → 常见坑（appendix）→ 自检（99）”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.3.2 更新 AOP README：在 `spring-core-aop/README.md` 增加“学习路径（docs）→ 推荐 tests → 常见坑（appendix）→ 自检（99）”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.3.3 更新 Tx README：在 `spring-core-tx/README.md` 增加“学习路径（docs）→ 推荐 tests → 常见坑（appendix）→ 自检（99）”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.3.4 更新 Web MVC README：在 `springboot-web-mvc/README.md` 增加“学习路径（docs）→ 推荐 tests → 常见坑（appendix）→ 自检（99）”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`

### 0.4 对齐四模块 docs/README（让 docs/ 目录本身可导航）
- [ ] 0.4.1 更新 Beans docs/README：在 `docs/beans/spring-core-beans/README.md` 增加“Part 导航 + 关键章节（BPP/循环依赖/候选选择）→ 对应测试入口”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.4.2 更新 AOP docs/README：在 `docs/aop/spring-core-aop/README.md` 增加“Part 导航 + 关键章节（自调用/AutoProxyCreator/JDK vs CGLIB）→ 对应测试入口”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.4.3 更新 Tx docs/README：在 `docs/tx/spring-core-tx/README.md` 增加“Part 导航 + 关键章节（边界/传播/回滚）→ 对应测试入口”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.4.4 更新 Web MVC docs/README：在 `docs/web-mvc/springboot-web-mvc/README.md` 增加“Part 导航 + 关键章节（绑定/异常链路/Security 交互）→ 对应测试入口”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`

### 0.5 对齐 Troubleshooting / Pitfalls / Self-check（体验一致）
- [ ] 0.5.1 统一 Beans pitfalls：重构 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md` 为统一模板（现象→根因→验证→断点→修复建议），验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.2 统一 AOP pitfalls：重构 `docs/aop/spring-core-aop/appendix/90-common-pitfalls.md` 为统一模板（现象→根因→验证→断点→修复建议），验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.3 统一 Tx pitfalls：重构 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 为统一模板（现象→根因→验证→断点→修复建议），验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.4 统一 Web MVC pitfalls：重构 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md` 为统一模板（现象→根因→验证→断点→修复建议），验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.5 对齐 Beans 自检：更新 `docs/beans/spring-core-beans/appendix/99-self-check.md`，确保每条自检都能指向“具体 doc + 具体 test”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.6 对齐 AOP 自检：更新 `docs/aop/spring-core-aop/appendix/99-self-check.md`，确保每条自检都能指向“具体 doc + 具体 test”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.7 对齐 Tx 自检：更新 `docs/tx/spring-core-tx/appendix/99-self-check.md`，确保每条自检都能指向“具体 doc + 具体 test”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.5.8 对齐 Web MVC 自检：更新 `docs/web-mvc/springboot-web-mvc/appendix/99-self-check.md`，确保每条自检都能指向“具体 doc + 具体 test”，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`

### 0.6 更新知识库导航（helloagents/wiki）
- [ ] 0.6.1 更新 Beans 模块导航（`helloagents/wiki/modules/spring-core-beans.md`）：补齐 Docs Index / Labs Index / Troubleshooting 入口，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.6.2 更新 AOP 模块导航（`helloagents/wiki/modules/spring-core-aop.md`）：补齐 Docs Index / Labs Index / Troubleshooting 入口，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.6.3 更新 Tx 模块导航（`helloagents/wiki/modules/spring-core-tx.md`）：补齐 Docs Index / Labs Index / Troubleshooting 入口，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`
- [ ] 0.6.4 更新 Web MVC 模块导航（`helloagents/wiki/modules/springboot-web-mvc.md`）：补齐 Docs Index / Labs Index / Troubleshooting 入口，验证 `why.md#requirement-r0-learning-path-unification` `why.md#scenario-s0-docs-lab-binding`

---

## 1. spring-core-beans（容器/扩展点）

> 目标：围绕容器启动主线与扩展点（BFPP/BPP/生命周期）形成“主线 + 关键边界 + 排障”闭环。  
> 验证：`why.md#requirement-r1-beans-container-mainline` + 对应 Scenario anchors

### 1.0 导航对齐与现状梳理（先把“入口”拉直）
- [ ] 1.0.1 更新 Beans Deep Dive Guide：把“容器 refresh 时间线”与对应 Labs 绑定（`docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.0.2 更新 Beans docs/README：补齐“容器主线章节索引 → 测试入口索引”（`docs/beans/spring-core-beans/README.md`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.0.3 更新知识库 Beans 模块页：强调 BPP 顺序/循环依赖/候选选择三条主线与入口（`helloagents/wiki/modules/spring-core-beans.md`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`

### 1.1 BPP/BFPP 顺序主线：文档补齐（refresh 时间线 + 断点）
- [ ] 1.1.1 为 `docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md` 增加“refresh 时间线”章节：哪些阶段注册/排序/调用 BFPP/BPP，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.1.2 为 `docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md` 增加“关键类/关键方法清单（推荐断点）”章节，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.1.3 为 `docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md` 增加“常见误区与排障路线（为什么顺序不如预期）”章节，并指向对应 Lab，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.1.4 在 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md` 增补“BPP 顺序相关坑”条目（现象→根因→验证→断点），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`

### 1.2 BPP/BFPP 顺序主线：Lab 强化（把结论固化为断言）
- [ ] 1.2.1 拆分并强化 `SpringCoreBeansPostProcessorOrderingLabTest`：增加“BFPP vs BPP 生效时机”对照用例（`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.2.2 拆分并强化 `SpringCoreBeansPostProcessorOrderingLabTest`：增加 `PriorityOrdered` vs `Ordered` vs 无序 的 ordering 对照用例（同文件），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.2.3 拆分并强化 `SpringCoreBeansPostProcessorOrderingLabTest`：增加“@Order/Ordered 接口在不同类型 PostProcessor 上的差异”对照用例（同文件），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.2.4 为上述 Lab 增加“定位型断言”：断言顺序与“证据链”（例如记录调用轨迹/beanName 列表），避免仅靠日志肉眼观察（同文件），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`
- [ ] 1.2.5 在 `docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md` 补充“推荐测试入口：SpringCoreBeansPostProcessorOrderingLabTest 的用例清单”并标注对应小节，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s1-bpp-ordering`

### 1.3 循环依赖边界：文档补齐（能救/不能救矩阵）
- [ ] 1.3.1 为 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md` 增加“循环依赖可解矩阵”：构造器/字段/Setter、singleton/prototype、@Lazy、Provider 等，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.3.2 为 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md` 增加“early reference 与代理介入”的边界解释：raw injection despite wrapping 等典型现象，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.3.3 在循环依赖章节中补齐“推荐断点清单”与“如何从异常反推到 refresh 时间线位置”，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.3.4 在 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md` 增补“循环依赖相关坑”的排障条目（至少覆盖 2 个可救 + 2 个不可救），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`

### 1.4 循环依赖边界：新增对照 Lab（让矩阵可被断言）
- [ ] 1.4.1 新增 `SpringCoreBeansCircularDependencyBoundaryLabTest`：覆盖“构造器循环依赖不可解”的最小复现与断言（`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.4.2 新增 `SpringCoreBeansCircularDependencyBoundaryLabTest`：覆盖“Setter/字段注入可解（early singleton exposure）”的最小复现与断言（同文件），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.4.3 新增 `SpringCoreBeansCircularDependencyBoundaryLabTest`：覆盖“@Lazy 或 Provider 打破循环”的对照用例（同文件），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.4.4 新增 `SpringCoreBeansCircularDependencyBoundaryLabTest`：覆盖“代理介入导致的边界现象（raw injection / early reference）”的最小复现与断言（同文件或必要时新增支撑类），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`
- [ ] 1.4.5 为新 Lab 补齐文档入口：在 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md` 添加“推荐测试入口/推荐断点”链接，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s2-circular-dependency-boundary`

### 1.5 注入候选选择：文档补齐（从报错反推规则）
- [ ] 1.5.1 深化候选选择主线：为 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md` 增加“决策树：候选收集 → 排序 → 决胜规则 → 报错分支”，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.5.2 深化候选选择主线：在同文档补齐“异常 → 反推到哪个规则分支”的排障小节（NoSuchBean / NoUniqueBean 等），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.5.3 深化泛型匹配坑：为 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md` 增加“典型误判案例 + 推荐验证测试入口”，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.5.4 在 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md` 增补“注入/候选选择排障 checklist”：从现象（报错/注入错 Bean）到定位（规则分支/断点/验证测试），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`

### 1.6 注入候选选择：强化现有 Labs + 新增定位型 Lab
- [ ] 1.6.1 强化 `SpringCoreBeansAutowireCandidateSelectionLabTest`：补齐 @Primary/@Qualifier/名称匹配 的最小对照用例，并把结论写成断言（`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.6.2 强化 `SpringCoreBeansInjectionAmbiguityLabTest`：补齐“多候选报错”的可解释断言与定位信息（`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionAmbiguityLabTest.java`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.6.3 强化 `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`：补齐“泛型匹配成功/失败”的最小对照断言（`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.6.4 新增定位型 Lab：`SpringCoreBeansAutowireCandidateTroubleshootingLabTest`，把“报错 → 排障路线”固化为 3–5 个用例（`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateTroubleshootingLabTest.java`），验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`
- [ ] 1.6.5 在 `docs/beans/spring-core-beans/appendix/99-self-check.md` 增补自检题：给出 5 个常见报错/现象，要求能指向“具体 doc + 具体 test”，验证 `why.md#requirement-r1-beans-container-mainline` `why.md#scenario-s3-injection-candidate-selection`

### 1.7 Beans 阶段性验证（避免做大后爆炸）
- [ ] 1.7.1 运行 Beans 模块测试：`bash scripts/test-module.sh spring-core-beans`，确保新增/强化用例稳定

---

## 2. spring-core-aop（代理/自调用/装配）

> 目标：围绕代理产生时机、Advisor 匹配证据链、自调用边界、JDK vs CGLIB 类型差异，形成“可解释 + 可断点 + 可排障”。  
> 验证：`why.md#requirement-r2-aop-proxy-and-self-invocation` + 对应 Scenario anchors

### 2.0 导航对齐与现状梳理
- [ ] 2.0.1 更新 AOP Deep Dive Guide：把“自调用/AutoProxyCreator/JDK vs CGLIB”三条主线与 tests 入口绑定（`docs/aop/spring-core-aop/part-00-guide/00-deep-dive-guide.md`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.0.2 更新 AOP docs/README：补齐 Part 导航 + 推荐测试入口索引（`docs/aop/spring-core-aop/README.md`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.0.3 更新知识库 AOP 模块页：补齐 Docs Index / Labs Index / Troubleshooting（`helloagents/wiki/modules/spring-core-aop.md`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`

### 2.1 自调用边界：文档深化（为什么不生效 + 怎么验证）
- [ ] 2.1.1 深化 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md`：补齐“为什么 this 调用绕过代理”的机制解释与最小示意，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.1.2 深化同文档：补齐“如何验证 Advice 是否命中”的推荐方式（优先测试断言/调用轨迹，不以日志为主），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.1.3 深化同文档：补齐“可控替代方案与边界”章节（拆分 Bean、注入代理、exposeProxy/AopContext 教学用途与风险），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.1.4 在 `docs/aop/spring-core-aop/appendix/90-common-pitfalls.md` 增补“自调用导致不生效”的排障条目（现象→根因→验证→断点→修复），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`

### 2.2 自调用边界：新增对照 Lab（把差异固化为断言）
- [ ] 2.2.1 新增 `SpringCoreAopSelfInvocationBoundaryLabTest`：用最小用例断言“外部调用命中 Advice、内部自调用不命中”（`spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopSelfInvocationBoundaryLabTest.java`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.2.2 如需支撑类，调整/补齐 `SelfInvocationExampleService` 的最小示例方法（`spring-core-aop/src/main/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SelfInvocationExampleService.java`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.2.3 在 Lab 中增加“可控替代方案”对照用例（例如通过注入代理调用），并写成断言（同 LabTest 或必要时新增支撑），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.2.4 在 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md` 增加“推荐测试入口：SpringCoreAopSelfInvocationBoundaryLabTest”与用例映射，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`

### 2.3 AutoProxyCreator 主线：文档深化（证据链 + 推荐断点）
- [ ] 2.3.1 深化 `docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/07-autoproxy-creator-mainline.md`：补齐“AutoProxyCreator 在 BPP after-init 的主线位置”与关键调用链路，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`
- [ ] 2.3.2 深化同文档：补齐“为什么某个 Bean 会/不会被代理”的证据链（Advisor 匹配过程、跳过条件），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`
- [ ] 2.3.3 深化同文档：补齐“推荐断点与观察点清单”，并把断点与测试入口一一对应，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`

### 2.4 AutoProxyCreator 主线：强化现有 Lab（让证据链可断言）
- [ ] 2.4.1 强化 `SpringCoreAopAutoProxyCreatorInternalsLabTest`：新增断言验证“哪些 Bean 被代理/哪些不被代理”（`spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyCreatorInternalsLabTest.java`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`
- [ ] 2.4.2 强化同 Lab：新增“Advisor 匹配结果可解释”的断言（例如按 beanName/annotation 维度输出可断言证据），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`
- [ ] 2.4.3 强化 `SpringCoreAopPointcutExpressionsLabTest`：补齐“表达式命中/不命中”的对照用例，并把结论写成断言（`spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopPointcutExpressionsLabTest.java`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`

### 2.5 JDK vs CGLIB：文档 + 对照 Lab（类型差异与限制）
- [ ] 2.5.1 深化 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/02-jdk-vs-cglib.md`：补齐“类型差异/可注入类型/方法可拦截边界”的表格化总结，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s3-jdk-vs-cglib-type-diff`
- [ ] 2.5.2 深化 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/04-final-and-proxy-limits.md`：补齐 final 限制的可验证结论与推荐断点，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s3-jdk-vs-cglib-type-diff`
- [ ] 2.5.3 新增 `SpringCoreAopProxyTypeMatrixLabTest`：用最小对照断言代理类型差异（JDK/CGLIB）及其注入边界（`spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyTypeMatrixLabTest.java`），验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s3-jdk-vs-cglib-type-diff`
- [ ] 2.5.4 在 `docs/aop/spring-core-aop/appendix/99-self-check.md` 增补自检题：给出 5 个“为何 Advice 不生效/代理类型不如预期”的场景题，并要求能指向测试入口，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s3-jdk-vs-cglib-type-diff`

### 2.6 AOP 排障清单（从现象到断点）
- [ ] 2.6.1 重构 `docs/aop/spring-core-aop/appendix/90-common-pitfalls.md`：按统一模板补齐“自调用/代理类型/pointcut 不命中/多重代理顺序”等条目，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s1-self-invocation`
- [ ] 2.6.2 将 pitfalls 条目与 tests 入口绑定：为每条 pitfalls 指向至少 1 个 LabTest，并在文档中写清“如何运行/观察什么”，验证 `why.md#requirement-r2-aop-proxy-and-self-invocation` `why.md#scenario-s2-auto-proxy-creator-mainline`

### 2.7 AOP 阶段性验证
- [ ] 2.7.1 运行 AOP 模块测试：`bash scripts/test-module.sh spring-core-aop`

---

## 3. spring-core-tx（事务边界/传播/回滚）

> 目标：把“事务依赖代理/传播矩阵/回滚规则”做成可验证、可断点、可排障的闭环。  
> 验证：`why.md#requirement-r3-tx-propagation-and-rollback` + 对应 Scenario anchors

### 3.0 导航对齐与现状梳理
- [ ] 3.0.1 更新 Tx Deep Dive Guide：绑定“边界/传播/回滚”三条主线与测试入口（`docs/tx/spring-core-tx/part-00-guide/00-deep-dive-guide.md`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`
- [ ] 3.0.2 更新 Tx docs/README：补齐 Part 导航 + 推荐测试入口索引（`docs/tx/spring-core-tx/README.md`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`
- [ ] 3.0.3 更新知识库 Tx 模块页：补齐 Docs Index / Labs Index / Troubleshooting（`helloagents/wiki/modules/spring-core-tx.md`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`

### 3.1 事务边界与代理：文档深化（把 TransactionInterceptor 放进主线）
- [ ] 3.1.1 深化 `docs/tx/spring-core-tx/part-01-transaction-basics/01-transaction-boundary.md`：补齐“事务边界是什么/为什么必须靠代理”的可验证结论，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`
- [ ] 3.1.2 深化 `docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md`：补齐“@Transactional → Advisor/Interceptor → PlatformTransactionManager”的调用链路与推荐断点，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`
- [ ] 3.1.3 在 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 增补“事务不生效（自调用/非 public/未被代理）”的排障条目，并指向已有测试入口，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`

### 3.2 传播行为矩阵：新增 Lab（把差异固化为断言）
- [ ] 3.2.1 新增 `SpringCoreTxPropagationMatrixLabTest`：覆盖 REQUIRED vs REQUIRES_NEW vs NESTED 的最小对照（`spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxPropagationMatrixLabTest.java`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s2-propagation-matrix`
- [ ] 3.2.2 如需支撑代码，扩展 `AccountService`：新增用于传播行为对照的内部调用链（`spring-core-tx/src/main/java/com/learning/springboot/springcoretx/part01_transaction_basics/AccountService.java`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s2-propagation-matrix`
- [ ] 3.2.3 为传播矩阵 Lab 增加“可观察断言”：断言事务是否新开/挂起/嵌套（可通过记录事务标识/连接/保存点等可观察信号），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s2-propagation-matrix`
- [ ] 3.2.4 在 `docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md` 增加“传播矩阵表 + 对应 Lab 用例索引 + 推荐断点”，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s2-propagation-matrix`

### 3.3 回滚规则：新增 Lab + 文档深化（checked vs runtime）
- [ ] 3.3.1 新增 `SpringCoreTxRollbackRulesLabTest`：断言 RuntimeException 默认回滚、CheckedException 默认不回滚（`spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxRollbackRulesLabTest.java`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s3-rollback-rules`
- [ ] 3.3.2 扩展 `AccountService`：新增用于回滚规则对照的最小方法（`spring-core-tx/src/main/java/com/learning/springboot/springcoretx/part01_transaction_basics/AccountService.java`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s3-rollback-rules`
- [ ] 3.3.3 在 `docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md` 增加“规则总结表 + 用例索引 + 常见误区”，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s3-rollback-rules`
- [ ] 3.3.4 在 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 增补“回滚不如预期”的排障条目（异常类型/捕获吞掉/传播边界），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s3-rollback-rules`

### 3.4 事务与自调用（补齐对照与排障）
- [ ] 3.4.1 强化 `SpringCoreTxSelfInvocationPitfallLabTest`：补齐“事务不生效”的对照断言与定位信息（`spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxSelfInvocationPitfallLabTest.java`），验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`
- [ ] 3.4.2 在 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 将“自调用导致事务失效”的排障路线写成 checklist，并指向上述 Lab，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s1-tx-boundary-and-proxy`

### 3.5 Tx 自检与排障（可复述 + 可验证）
- [ ] 3.5.1 更新 `docs/tx/spring-core-tx/appendix/99-self-check.md`：新增 10 条自检（传播/回滚/边界/自调用），每条指向 doc + test，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s2-propagation-matrix`
- [ ] 3.5.2 重构 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md`：按统一模板覆盖至少 12 个常见坑，并绑定测试入口，验证 `why.md#requirement-r3-tx-propagation-and-rollback` `why.md#scenario-s3-rollback-rules`

### 3.6 Tx 阶段性验证
- [ ] 3.6.1 运行 Tx 模块测试：`bash scripts/test-module.sh spring-core-tx`

---

## 4. springboot-web-mvc（绑定/异常链路/与 Security 交互）

> 目标：把 MVC 的“请求处理链路/参数绑定/异常解析/安全过滤链”做成可验证、可断点、可排障的闭环。  
> 验证：`why.md#requirement-r4-webmvc-binding-exception-security` + 对应 Scenario anchors

### 4.0 导航对齐与现状梳理
- [ ] 4.0.1 更新 Web MVC Deep Dive Guide：把“绑定/异常链路/Security 交互”三条主线与 tests 入口绑定（`docs/web-mvc/springboot-web-mvc/part-00-guide/00-deep-dive-guide.md`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.0.2 更新 Web MVC docs/README：补齐 Part 导航 + 推荐测试入口索引（`docs/web-mvc/springboot-web-mvc/README.md`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.0.3 更新知识库 Web MVC 模块页：补齐 Docs Index / Labs Index / Troubleshooting（`helloagents/wiki/modules/springboot-web-mvc.md`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`

### 4.1 参数绑定/转换/校验：文档深化（主线 + 关键分支）
- [ ] 4.1.1 深化 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/03-binding-and-converters.md`：补齐“参数解析 → WebDataBinder → ConversionService/Converter → 绑定结果”的主线与推荐断点，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.1.2 深化 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/01-validation-and-error-shaping.md`：补齐“校验失败如何塑形错误响应（400/ProblemDetail/自定义错误结构）”的分支解释与推荐测试入口，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.1.3 深化 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/02-argument-resolver-and-binder.md`：补齐 ArgumentResolver / Binder 的关键扩展点与断点清单，并链接回 part-01，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.1.4 在 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md` 增补“400/类型转换失败/绑定失败/校验失败”的排障条目（现象→根因→验证→断点），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`

### 4.2 参数绑定/转换/校验：强化现有 Labs（把结论固化为断言）
- [ ] 4.2.1 强化 `BootWebMvcBindingDeepDiveLabTest`：补齐“绑定成功/失败、转换成功/失败、校验成功/失败”的对照断言（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.2.2 强化 `BootWebMvcLabTest` 或 `BootWebMvcSpringBootLabTest`：补齐“默认配置下 binder/validator 是否存在”的可观察断言（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` 或相邻文件），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`
- [ ] 4.2.3 为绑定相关用例补齐“定位信息”：失败时输出可读上下文（参数名/错误码/解析器），避免纯 400 无解释（同测试文件），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`

### 4.3 异常处理链路：文档深化（Resolver 顺序 + 匹配规则）
- [ ] 4.3.1 深化 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/02-exception-handling.md`：补齐“异常从 Controller 抛出后如何被处理”的主线（高层叙事），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.3.2 深化 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md`：补齐 HandlerExceptionResolver 链路、顺序与关键分支（深层机制），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.3.3 深化 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/05-controlleradvice-matching-and-ordering.md`：补齐 ControllerAdvice 的匹配与排序规则，并绑定现有 advice tests，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.3.4 在 `docs/web-mvc/springboot-web-mvc/part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md` 增补“异常链路排查路线”：从响应 → 定位到 resolver/advice/默认错误处理，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`

### 4.4 异常处理链路：新增/强化 Labs（让顺序与匹配可断言）
- [ ] 4.4.1 新增 `BootWebMvcExceptionResolverChainLabTest`：断言“不同异常类型落到不同 resolver/handler”的对照（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcExceptionResolverChainLabTest.java`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.4.2 扩展 `BootWebMvcAdviceOrderLabTest`：补齐“多个 @ControllerAdvice 的 ordering 影响”的更细粒度断言（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part09_advice_order/BootWebMvcAdviceOrderLabTest.java`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.4.3 扩展 `BootWebMvcAdviceMatchingLabTest`：补齐“advice 匹配（包/注解/assignable types）”的对照断言（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part10_advice_matching/BootWebMvcAdviceMatchingLabTest.java`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.4.4 在文档中绑定测试入口：把 `BootWebMvcExceptionResolverChainLabTest` 与 advice tests 写入对应章节末尾（修改 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/02-exception-handling.md` 或 internals 章节），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`

### 4.5 与 Security 交互：文档深化（FilterChain 在 MVC 之前）
- [ ] 4.5.1 深化 `docs/web-mvc/springboot-web-mvc/part-08-security-observability/01-security-filterchain-and-mvc.md`：补齐“401/403/CSRF/Anonymous”在 MVC 之前发生的主线解释，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`
- [ ] 4.5.2 在同文档补齐“从现象到断点”的排障路线：优先从 FilterChainProxy/ExceptionTranslationFilter 入手（教学用断点清单），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`
- [ ] 4.5.3 在 `docs/web-mvc/springboot-web-mvc/part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md` 增补“401/403 排查路线”并指向安全相关 Lab，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`

### 4.6 与 Security 交互：强化现有 Labs（让 401/403 分支可断言）
- [ ] 4.6.1 强化 `BootWebMvcSecurityLabTest`：补齐“未认证 401 vs 已认证但无权限 403”的对照断言（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcSecurityLabTest.java`），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`
- [ ] 4.6.2 强化 `BootWebMvcSecurityLabTest`：补齐“CSRF 导致 403”的对照用例（教学分支可保留），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`
- [ ] 4.6.3 强化 `BootWebMvcSecurityLabTest`：补齐“FilterChain 与 Controller 根本不进入”的定位型断言（例如断言 handler 未被调用、或断言某个过滤器触发），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`
- [ ] 4.6.4 在安全文档中绑定测试入口：在 `01-security-filterchain-and-mvc.md` 添加“推荐测试入口：BootWebMvcSecurityLabTest 用例索引”，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`

### 4.7 Web MVC Troubleshooting Playbook（把常见状态码全部纳入路线图）
- [ ] 4.7.1 重构 `docs/web-mvc/springboot-web-mvc/part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md`：按统一模板拆分 400/401/403/404/405/406/415/422 的排查路线（每条指向“doc + test + 断点”），验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s3-security-filterchain-interaction`
- [ ] 4.7.2 在 `docs/web-mvc/springboot-web-mvc/appendix/99-self-check.md` 增补自检题：给出 10 个常见状态码场景，要求能指向排障路线与测试入口，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s2-exception-resolver-chain`
- [ ] 4.7.3 在 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md` 增补“新手最常见 10 个坑”并绑定测试入口，验证 `why.md#requirement-r4-webmvc-binding-exception-security` `why.md#scenario-s1-argument-binding-and-validation`

### 4.8 Web MVC 阶段性验证
- [ ] 4.8.1 运行 Web MVC 模块测试：`bash scripts/test-module.sh springboot-web-mvc`

---

## 5. 收尾：端到端 Debug 主线（可选，但建议作为毕业作业）

> 目标：从一次请求出发，串起 MVC →（可选）AOP →（可选）Tx，形成跨模块定位路线图。  
> 验证：`why.md#requirement-r5-e2e-storyline` `why.md#scenario-s1-end-to-end-debug-route`

### 5.1 方案确认（先把“在哪里做”定下来）
- [ ] 5.1.1 评估端到端主线落点：在 `springboot-web-mvc` 直接实现 vs 复用现有业务模块（仅做教学，不引入生产依赖），并把决策补充到 `helloagents/plan/202601091043_deepen_beans_aop_tx_web/how.md`（ADR 追加），验证 `why.md#requirement-r5-e2e-storyline` `why.md#scenario-s1-end-to-end-debug-route`

### 5.2 最小可控实现（以可断言/可断点为第一目标）
- [ ] 5.2.1 新增 `BootWebMvcEndToEndDebugRouteLabTest`：定义端到端用例（请求 → 业务方法 → 事务边界），并把关键观察点写成断言（`springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcEndToEndDebugRouteLabTest.java`），验证 `why.md#requirement-r5-e2e-storyline` `why.md#scenario-s1-end-to-end-debug-route`
- [ ] 5.2.2 为端到端用例补齐最小 Controller/Service（如需要）：仅为教学服务，保持可控分支与可复现（文件路径按现有包结构放置），验证 `why.md#requirement-r5-e2e-storyline` `why.md#scenario-s1-end-to-end-debug-route`
- [ ] 5.2.3 为端到端主线补齐文档章节：把“从现象到断点”的跨模块路线写入 Web MVC troubleshooting 或新增小节，并绑定测试入口，验证 `why.md#requirement-r5-e2e-storyline` `why.md#scenario-s1-end-to-end-debug-route`

---

## 6. Security Check（非功能性约束，必须每轮执行）
- [ ] 6.1 检查新增/修改代码：不引入明文敏感信息、不接入生产服务、不写危险权限提升示例（按 G9）
- [ ] 6.2 检查新增测试：不依赖外部网络；必要时使用 mock/stub；避免不稳定的时间/随机性依赖

---

## 7. Testing（全局回归）
- [ ] 7.1 跑四模块单测回归：`bash scripts/test-module.sh spring-core-beans`
- [ ] 7.2 跑四模块单测回归：`bash scripts/test-module.sh spring-core-aop`
- [ ] 7.3 跑四模块单测回归：`bash scripts/test-module.sh spring-core-tx`
- [ ] 7.4 跑四模块单测回归：`bash scripts/test-module.sh springboot-web-mvc`
- [ ] 7.5 跑全量测试并记录结论：`bash scripts/test-all.sh`

---

## 8. 知识库同步 & 变更归档（执行阶段必须做）
- [ ] 8.1 同步知识库模块页：更新四模块的“Docs/Labs/排障入口/关键断点”（`helloagents/wiki/modules/spring-core-beans.md` 等），保证与实际文件一致
- [ ] 8.2 更新 `helloagents/CHANGELOG.md`：记录本次“文档深化 + 新增 Labs”变更点
- [ ] 8.3 进行一致性审计：docs 引用的测试类/路径必须存在；测试注释引用的 doc 路径必须存在
- [ ] 8.4 将本方案包迁移到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`（按 G11 生命周期）

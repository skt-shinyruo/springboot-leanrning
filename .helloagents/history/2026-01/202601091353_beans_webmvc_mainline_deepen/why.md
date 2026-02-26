# Change Proposal: Beans + Web MVC 主线深化（候选选择 / 异常链路 / Security 边界）

## Requirement Background

本仓库的目标不是“跑起来就行”，而是把 Spring 学习过程中最容易反复踩坑的机制，做成：

1. **可复现**：每个关键分支都有稳定的最小实验（`*LabTest`）
2. **可验证**：关键结论以断言为准（tests as SSOT），而不是依赖日志“看起来像”
3. **可练习**：同一知识点提供练习题（`*ExerciseTest`，默认 `@Disabled`）
4. **可排障**：现象 → 根因 → 如何验证 → 推荐断点/观察点 → 修复建议

你已明确本轮聚焦主线：
- **Beans / 容器（重点：依赖注入候选选择与边界）**
- **Web MVC（重点：异常解析链路与与 Security FilterChain 的交互边界）**

目前仓库已有一定积累（例如 `@Primary/@Priority/@Order`、resolver 链路、Security 401/403/CSRF），但仍需要进一步把“规则”升级为“可被一眼排障的决策树 + 可断点可证明的证据链”。

## Change Content

1. Beans：把“候选选择”做成可排障流程
   - 补齐 `@Autowired` 单依赖的候选决胜顺序：`@Qualifier` / `@Primary` / **by-name fallback** / `@Priority` / 失败
   - 补齐“集合注入 vs 单依赖注入”边界：排序（`@Order`）≠ 决胜
   - 补齐泛型参与候选收敛的行为与陷阱，并绑定现有 Lab（ResolvableType/代理丢失信息）
   - 补齐 `ObjectProvider` 在“多候选/可选依赖/延迟解析”场景的语义

2. Web MVC：把“401/403 不归 @ControllerAdvice 管”做成可证明事实
   - 新增专用 Lab：用 `MockMvc` 的 `handler/resolvedException` 作为证据链锚点，区分：
     - FilterChain 阶段：401/403（Security/CSRF）
     - DispatcherServlet 阶段：400（binder/validation/converter）与 resolver 翻译
   - 文档补齐：用“如何证明没进 DispatcherServlet”作为排障主线，而不是停留在概念描述

3. 学习路径统一：把新增内容纳入 docs 与排障清单（两模块）
   - 每个新增结论都绑定到：doc 章节 + LabTest +（可选）ExerciseTest + troubleshooting 条目

## Impact Scope

- **Modules:**
  - `spring-core-beans`
  - `springboot-web-mvc`

- **Files (expected):**
  - Beans：`docs/part-04-wiring-and-boundaries/33-...md`、`docs/appendix/90-...md`、`docs/appendix/99-...md`、相关 `*LabTest/*ExerciseTest`
  - Web MVC：`docs/part-08-security-observability/01-...md`、`docs/appendix/90-...md`、相关 `*LabTest/*ExerciseTest`
  - SSOT：`helloagents/wiki/modules/*.md`、`helloagents/CHANGELOG.md`、方案包迁移到 `helloagents/history/`

- **APIs/Data:** 仅教学内容与测试入口调整，不引入对外 API 兼容性变更；不触达生产数据。

## Core Scenarios

### Requirement: R1-beans-autowire-candidate-selection-mainline
**Module:** spring-core-beans

把“注入失败/注错”的排障过程变成稳定套路：先确定注入点类型（单依赖 vs 集合），再按候选决胜顺序逐步收敛，最后用断点证明走到哪个分支。

#### Scenario: S1-by-name-fallback
当存在多个同类型候选且无 `@Primary/@Qualifier` 时，`@Autowired` 在某些场景可用“依赖名匹配 beanName”收敛到唯一候选；需要用 Lab 固化。

#### Scenario: S2-qualifier-vs-primary
Qualifier 是“更强约束”，在限定命中的前提下可以绕过 primary 的默认胜者语义；需要用对照实验固化。

#### Scenario: S3-objectprovider-semantics
把 `ObjectProvider` 在多候选/可选依赖/延迟解析上的语义写清楚，并提供可断言入口。

### Requirement: R2-webmvc-security-vs-resolver-boundary
**Module:** springboot-web-mvc

把 “401/403 为什么 @ControllerAdvice 捕不到” 变成可证据化结论：
- 发生在 FilterChain：通常 `handler/resolvedException` 为 `null`
- 发生在 MVC：`resolvedException` 有值且类型可定位到 binder/validation/converter/resolver 分支

## Risk Assessment

- **Risk:** 规则解释写成“背诵题”，缺少可验证证据链  
  **Mitigation:** 每条结论必须绑定到具体 Lab 断言；文档优先引用测试入口与建议断点。

- **Risk:** 新增测试太“实现细节化”导致脆弱  
  **Mitigation:** 优先断言稳定可观察行为（状态码、异常类型、是否进入 handler）；内部调用链仅作为断点建议。


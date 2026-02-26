# Task List: beans_webmvc_mainline_deepen（超细化任务清单）

Directory: `helloagents/plan/202601091353_beans_webmvc_mainline_deepen/`

> 本任务包聚焦两条主线：**Beans（候选选择）** 与 **Web MVC（异常链路 + Security 边界）**。  

---

## 0. 本轮 DoD（验收标准）

- [√] 0.0.1 Beans：候选选择能用“决策树”复述，并能用 LabTest 稳定复现至少 6 个关键分支（含 by-name fallback）
- [√] 0.0.2 Web MVC：能用 LabTest 明确区分“401/403 在 FilterChain” vs “400 在 MVC resolver”
- [√] 0.0.3 两模块都补齐 Troubleshooting 条目（至少各 8 条），每条绑定具体测试入口
- [√] 0.0.4 两模块各补齐至少 1 个 ExerciseTest（默认 `@Disabled`），并给出明确提示与验证方式
- [√] 0.0.6 知识库同步：更新 `helloagents/wiki/modules/spring-core-beans.md`、`helloagents/wiki/modules/springboot-web-mvc.md`、`helloagents/CHANGELOG.md`
- [√] 0.0.7 方案包迁移：`helloagents/plan/...` → `helloagents/history/2026-01/...` 并更新 `helloagents/history/index.md`

---

### 1.1 基线回归（开工前）
- [√] 1.1.2 运行 Beans 模块：`bash scripts/test-module.sh spring-core-beans`
- [√] 1.1.3 运行 Web MVC 模块：`bash scripts/test-module.sh springboot-web-mvc`
- [-] 1.1.4 若有失败：记录失败用例与最小复现命令到本 task.md 的“备注区”（不要求写入 docs）

### 1.2 现状映射（把“已有资产”列成索引，避免重复造轮子）
- [√] 1.2.1 Beans：盘点候选选择相关现有 Lab（列出文件名即可）
  - `SpringCoreBeansInjectionAmbiguityLabTest`
  - `SpringCoreBeansAutowireCandidateSelectionLabTest`
  - `SpringCoreBeansJsr330InjectionLabTest`
  - `SpringCoreBeansCustomQualifierLabTest`
  - `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
  - `SpringCoreBeansResourceInjectionLabTest`
- [√] 1.2.2 Web MVC：盘点异常链路与安全相关现有 Lab（列出文件名即可）
  - `BootWebMvcExceptionResolverChainLabTest`
  - `BootWebMvcTestingDebuggingLabTest`
  - `BootWebMvcSecurityLabTest`
  - `BootWebMvcAdviceOrderLabTest`
  - `BootWebMvcAdviceMatchingLabTest`

---

## 2. Beans：候选选择主线深化（决策树 + 证据链）

> 目标：把 `NoUniqueBeanDefinitionException` / `NoSuchBeanDefinitionException` 等注入问题，落到“候选收敛顺序 + 可断言用例 + 推荐断点”。

### 2.1 文档主线：补齐“候选收敛决策树”（不新增章节编号，优先扩充现有章）

#### 2.1.1 扩充候选收敛顺序（加入 by-name fallback）
- [√] 2.1.1.1 更新 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`：明确单依赖决胜顺序包含“依赖名匹配 beanName”的分支
- [√] 2.1.1.2 在同一章补齐对照表：`@Qualifier` vs `@Primary` vs name fallback vs `@Priority`（每个条目：解决什么/何时生效/典型坑）
- [√] 2.1.1.3 在同一章补齐源码锚点：`DefaultListableBeanFactory#determineAutowireCandidate`（建议断点）

#### 2.1.2 把“相关 Lab”从散点变成主线导航
- [√] 2.1.2.1 在 33 章末尾补充“关联 Lab 索引”：InjectionAmbiguity/JSR330/CustomQualifier/ResourceInjection/GenericPitfalls
- [√] 2.1.2.2 在 `docs/beans/spring-core-beans/README.md` 的“注入失败”快速定位条目中补齐：by-name fallback 与对应 Lab（新增后需回填）

### 2.2 Labs：新增/强化 by-name fallback 与决胜优先级对照

> 说明：本阶段尽量 **扩充** 现有 LabTest（减少文件扩散），但每个用例的“证据链”必须清晰（断言 + OBSERVE 输出）。

#### 2.2.1 增强 `SpringCoreBeansAutowireCandidateSelectionLabTest`（单文件迭代）
- [√] 2.2.1.1 在 `spring-core-beans/src/test/java/.../SpringCoreBeansAutowireCandidateSelectionLabTest.java` 新增用例：by-name fallback 能消除单依赖歧义（建议用 field 注入避免 -parameters 依赖）
- [√] 2.2.1.2 在同一文件新增用例：`@Primary` 存在时应压过 by-name fallback（验证决胜优先级）
- [√] 2.2.1.3 在同一文件新增用例：`@Qualifier` 能强制选中非 primary（验证 qualifier 的“更强约束”）
- [√] 2.2.1.4 在同一文件新增用例：泛型收敛（`Handler<String>` vs `Handler<Long>`）可把多候选收敛到唯一候选（只断言结果，不绑定内部实现）
- [√] 2.2.1.5 在同一文件新增用例：`ObjectProvider#getIfUnique()` 在多候选时返回 `null`（用于可选依赖排障）
- [√] 2.2.1.6 在同一文件新增用例：`ObjectProvider#stream()` / `orderedStream()` 与 `@Order/@Priority` 的关系（强调“集合排序≠单依赖决胜”）

#### 2.2.2 必要时拆分新 Lab（仅当 2.2.1 文件过大/可读性下降）
- [-] 2.2.2.1 若单文件过长：新建 `SpringCoreBeansAutowireCandidateByNameFallbackLabTest`，只放 by-name fallback 对照用例
- [-] 2.2.2.2 若新增：同步更新 33 章的 Lab 引用与 `docs/README.md` 快速定位

### 2.3 Exercises：把“修注入失败”做成手练题（默认不影响回归）

#### 2.3.1 新增 ExerciseTest（默认 `@Disabled`）
- [√] 2.3.1.1 新增 `spring-core-beans/src/test/java/.../SpringCoreBeansAutowireCandidateSelectionExerciseTest.java`
- [√] 2.3.1.2 Exercise 题 1：给出“多候选导致 fail-fast”的场景，让学员分别用：
  - `@Qualifier`
  - `@Primary`
  - 重构注入点（集合注入或拆分接口）
  三种方式修复（每种方式写成一个子任务提示）
- [√] 2.3.1.3 Exercise 题 2：给出“泛型类型失配导致无候选”的场景，让学员修复 bean 定义或注入点类型
- [√] 2.3.1.4 Exercise 文件必须包含：提示（指向 docs 章节）+ 验证方式（跑哪一个 Lab/期望断言）

#### 2.3.2 可选：提供 ExerciseSolutionTest（作为答案对照）
- [-] 2.3.2.1 新增 `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest.java`（保持默认可跑）
- [-] 2.3.2.2 Solution 必须用“最少魔法”的方式修复（优先 qualifier/primary，避免反射）

### 2.4 Troubleshooting & Self-check：把坑与自测题补齐并绑定入口

#### 2.4.1 Pitfalls 清单（至少 8 条）
- [√] 2.4.1.1 更新 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md`：新增条目“`@Order` 不能解决单注入歧义”
- [√] 2.4.1.2 同文件新增条目“by-name fallback 为什么有时会‘注错’”（强调：依赖名变化/重构风险）
- [√] 2.4.1.3 同文件新增条目“Qualifier 与 Primary 冲突时怎么判断谁生效”
- [√] 2.4.1.4 同文件新增条目“ObjectProvider#getIfAvailable vs getIfUnique 的区别（以及排障建议）”
- [√] 2.4.1.5 同文件新增条目“泛型匹配：为什么按 `Handler<String>` 找不到候选（代理丢失信息）”
- [√] 2.4.1.6 每条 pitfall 必须包含：现象 → 根因 → 如何验证（指向具体 Lab）→ 推荐断点
- [√] 2.4.1.7 自检：确认 pitfall 里的 test 文件路径与类名真实存在

#### 2.4.2 自测题（至少新增 10 题）
- [√] 2.4.2.1 更新 `docs/beans/spring-core-beans/appendix/99-self-check.md`：新增“候选收敛决策树”相关题目（每题绑定 doc + test）
- [√] 2.4.2.2 新增题目示例方向：
  - 何时会走 by-name fallback？
  - `@Primary` 与 `@Priority` 的优先级与边界
  - 为什么 `@Qualifier` 能绕过 primary？
  - 为什么集合注入有序但单注入仍可能失败？

### 2.5 Beans 阶段回归
- [√] 2.5.1 运行 Beans 模块：`bash scripts/test-module.sh spring-core-beans`

---

## 3. Web MVC：异常链路 + Security 边界深化（证据链：handler/resolvedException）

> 目标：把 “401/403 为什么 @ControllerAdvice 捕不到” 变成一个可运行、可断言、可排障的事实链路。

### 3.1 Labs：新增“边界对照”专用 LabTest

#### 3.1.1 新增 LabTest（推荐放在 part08_security_observability）
- [√] 3.1.1.1 新增 `springboot-web-mvc/src/test/java/.../BootWebMvcSecurityVsMvcExceptionBoundaryLabTest.java`
- [√] 3.1.1.2 用例 A：未认证访问 secure endpoint → 401，并断言：
  - status=401
  - `MvcResult#getHandler()` 为 `null`（或可证明“未进入 handler”）
  - `MvcResult#getResolvedException()` 为 `null`（或可证明“不是 MVC resolver 翻译”）
- [√] 3.1.1.3 用例 B：认证但无权限访问 admin endpoint → 403，并断言同上（handler/resolvedException）
- [√] 3.1.1.4 用例 C：认证后 POST 不带 CSRF → 403，并断言同上
- [√] 3.1.1.5 用例 D：走 MVC binder/validation 分支（例如 `/api/advanced/exceptions/validate`）→ 400，并断言：
  - status=400
  - `resolvedException` 是具体异常类型（BindException / MethodArgumentNotValidException / HttpMessageNotReadableException）
- [√] 3.1.1.6 该 Lab 必须输出 OBSERVE：把“在哪发生”用一句话写清楚（FilterChain vs DispatcherServlet）

#### 3.1.2 可选增强：用自定义 handler 证明“要改响应体应该改哪里”
- [-] 3.1.2.1 在测试配置中临时注入 `AuthenticationEntryPoint` / `AccessDeniedHandler`（仅测试内）并断言响应体变化
- [-] 3.1.2.2 在 docs 中明确：ControllerAdvice 改不了 401/403 的根因与正确入口

### 3.2 文档：把概念升级为“可执行排障步骤”

#### 3.2.1 扩充 Security 与 MVC 相对位置文档
- [√] 3.2.1.1 更新 `docs/web-mvc/springboot-web-mvc/part-08-security-observability/01-security-filterchain-and-mvc.md`：新增小节“如何证明没进入 DispatcherServlet”
- [√] 3.2.1.2 在该小节加入明确步骤：
  - 用 MockMvc 获取 `handler/resolvedException`
  - 若 handler=null：优先怀疑 FilterChain（Security/Filter）
  - 若 resolvedException!=null：进入 resolver 链路排障
- [√] 3.2.1.3 同文件补齐 Lab 引用：新增边界 Lab + 既有 `BootWebMvcSecurityLabTest`

#### 3.2.2 把边界写入 resolver 主线章节（交叉引用即可）
- [√] 3.2.2.1 更新 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md`：补充一条“401/403 往往发生在 DispatcherServlet 之前”，并链接到 part-08 文档与边界 Lab

#### 3.2.3 把排障套路写入 testing/debugging 章节
- [√] 3.2.3.1 更新 `docs/web-mvc/springboot-web-mvc/part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md`：新增排障 checklist 条目“401/403：先看 handler/resolvedException 再看 controller”

### 3.3 Troubleshooting & Self-check

#### 3.3.1 Pitfalls（至少新增 8 条中的 4 条与 Security 相关）
- [√] 3.3.1.1 更新 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md`：新增条目“@ControllerAdvice 捕不到 401/403”
- [√] 3.3.1.2 同文件新增条目“POST 变 403：CSRF 误伤如何验证/如何修”
- [√] 3.3.1.3 同文件新增条目“@WebMvcTest 为什么突然 401/403（slice 默认也会过 filters）”
- [√] 3.3.1.4 每条必须绑定：doc + LabTest + 推荐断点（DelegatingFilterProxy/FilterChainProxy/CsrfFilter）

#### 3.3.2 自测题（可先留到下一轮，但需要占位）
- [√] 3.3.2.1 更新 `docs/web-mvc/springboot-web-mvc/appendix/99-self-check.md`：新增至少 6 题（如文件已存在）

### 3.4 Web MVC 阶段回归
- [√] 3.4.1 运行 Web MVC 模块：`bash scripts/test-module.sh springboot-web-mvc`

---

## 4. SSOT 同步与收尾（执行阶段必须做）

> 本段在开发实现后执行：同步知识库、更新 changelog、迁移方案包。

### 4.1 知识库同步（helloagents/wiki）
- [√] 4.1.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补齐本轮新增 docs/labs/exercises/排障索引
- [√] 4.1.2 更新 `helloagents/wiki/modules/springboot-web-mvc.md`：补齐本轮新增 docs/labs/exercises/排障索引
- [√] 4.1.3 更新 `helloagents/CHANGELOG.md`：记录 Added/Changed（两模块）

### 4.2 一致性自检（docs ↔ tests ↔ wiki）
- [√] 4.2.2 全量回归（阶段收尾）：`bash scripts/test-all.sh`

### 4.3 方案包迁移与索引更新（强制）
- [√] 4.3.1 更新本 task.md 状态：逐条把已完成任务标记为 `[√]`，跳过标记为 `[-]`，失败标记为 `[X]` 并写明原因
- [√] 4.3.2 迁移 `helloagents/plan/202601091353_beans_webmvc_mainline_deepen/` → `helloagents/history/2026-01/202601091353_beans_webmvc_mainline_deepen/`
- [√] 4.3.3 更新 `helloagents/history/index.md`：新增索引记录（✅Completed）

---

## 备注区（执行中记录）

- 失败/阻塞项：
  - （待填）

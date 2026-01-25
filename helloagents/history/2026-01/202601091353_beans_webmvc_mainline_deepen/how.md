# Technical Design: Beans + Web MVC 主线深化（候选选择 / Security ↔ MVC 边界）

## Technical Solution

本轮采用“增量深化 + tests as SSOT”的策略：尽量复用既有结构与命名体系，通过新增/强化 Lab/Exercise 与 docs 绑定，把关键机制补齐为可回归的知识点。

### Core Technologies

- Java 17
- Spring Boot 3.x（Maven 多模块聚合）
- JUnit 5 + AssertJ
- Spring Test / MockMvc
- Spring Security + Spring Security Test（用于 401/403/CSRF 分支的稳定复现）

### Implementation Key Points

1. **规则写成“决策树 + 证据链”**
   - 决策树：先分“单依赖 vs 集合注入”，再按“收敛顺序”逐步排除
   - 证据链：优先使用稳定可观察事实（状态码、异常类型、是否进入 handler）

2. **Beans：以 `DefaultListableBeanFactory#doResolveDependency` 为主线锚点**
   - 文档用“候选收集 → 候选过滤 → 决胜规则 → 注入/失败”组织
   - 测试用例只断言：结果（选中了谁/是否失败）与关键差异点（Qualifier/Primary/Name/Priority）

3. **Web MVC：以 `resolvedException` / `handler` 固化“发生在哪”**
   - FilterChain 失败（401/403/CSRF）通常不会走到 `HandlerMethod`
   - MVC 失败（400/415/406/500 等）可通过 `resolvedException` 锁定异常来源分支

4. **Exercises 默认不影响回归**
   - `*ExerciseTest` 默认 `@Disabled`，内容是练习题与提示
   - 如提供 `*ExerciseSolutionTest`，保持默认启用且可作为对照答案（可选）

## Architecture Decision ADR

### ADR-1: Web 排障以 `resolvedException/handler` 为证据链锚点

**Context:** 仅靠 status code 或日志很难区分 “FilterChain 拦截” 与 “MVC resolver 翻译”。  
**Decision:** 新增专用 Lab 固化 `handler/resolvedException` 的判定分支，并把该套路写入 docs 与排障清单。  
**Rationale:** 证据链稳定、可回归；能快速把排障从“猜”变成“证据”。  
**Alternatives:** 仅依赖日志观察 → 不稳定且不利于回归。  
**Impact:** 文档需要持续与测试同步，但可显著降低后续排障成本。

## Security and Performance

- **Security**
  - 不引入任何明文密钥/Token；不接入生产环境服务
  - Security 场景限定为教学端点 `/api/advanced/secure/**`（已隔离），避免影响其他 Labs

- **Performance**
  - Web 场景：能用 `@WebMvcTest` 的用例保持 slice；需要验证 FilterChain 时再用 `@SpringBootTest + MockMvc`
  - Beans 场景：优先使用 `AnnotationConfigApplicationContext` 的最小容器进行对照实验

## Testing and Verification

- 单模块回归：
  - `bash scripts/test-module.sh spring-core-beans`
  - `bash scripts/test-module.sh springboot-web-mvc`
- 全量回归（阶段收尾）：
  - `bash scripts/test-all.sh`


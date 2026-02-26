# Change Proposal: spring-core-aop-weaving（AspectJ LTW/CTW）

## Requirement Background

当前仓库的 `spring-core-aop` 已经覆盖了 **Spring AOP（proxy-based AOP）** 的核心机制：代理类型（JDK/CGLIB）、自调用陷阱、pointcut 表达式系统、AutoProxyCreator 主线、多代理叠加与真实世界排障闭环。

但在真实工程与源码学习中，仍经常会遇到“代理 AOP 无法覆盖”的边界问题，例如：

- 目标对象不在 Spring 容器内（`new` 出来的对象、第三方库类、静态方法调用链等）
- 需要拦截更丰富的 join point（`call/constructor/get/set/handler/...`）
- 需要理解 AspectJ 的 `call vs execution`、`withincode`、`cflow` 等更高级的表达式语义

因此需要新增一个独立模块 `spring-core-aop-weaving`：专注 **AspectJ weaving**（LTW + CTW），并用可验证的 Labs/Exercises 把差异与边界讲清楚，同时避免把复杂度混入 `spring-core-aop` 的“代理主线”。

## Change Content

1. 新增模块 `spring-core-aop-weaving`：可运行入口 + 可验证的 Labs/Exercises。
2. 覆盖两条 weaving 路径：
   - **LTW（Load-Time Weaving）**：通过 `-javaagent` 在类加载时织入
   - **CTW（Compile-Time Weaving）**：通过 Maven 插件在编译期织入
3. 覆盖更多 join point 与高级表达式：`call/execution`、constructor、`get/set`、`withincode/cflow` 等，并通过测试断言固化结论。
4. 同步更新学习导航：根 `README.md` 与 `helloagents/wiki/*` 模块索引。

## Impact Scope

- **Modules:**
  - 新增：`spring-core-aop-weaving`
  - 更新：父工程聚合 `pom.xml`、根 `README.md`、知识库 `helloagents/wiki/*`
- **Files:**（以最终实现为准）
  - `pom.xml`
  - `spring-core-aop-weaving/**`
  - `README.md`
  - `helloagents/wiki/overview.md`
  - `helloagents/wiki/modules/spring-core-aop.md`
  - `helloagents/wiki/modules/spring-core-aop-weaving.md`
  - `helloagents/CHANGELOG.md`

## Core Scenarios

### Requirement: Module Bootstrap
**Module:** spring-core-aop-weaving
新增模块可被聚合构建，并具备最小可运行入口与可验证测试。

#### Scenario: Build & Test Green
当执行模块测试时：
- `mvn -pl spring-core-aop-weaving test` 全绿
- 模块 `README.md` 与 `docs/README.md` 可作为学习入口与索引页

### Requirement: LTW Minimal Loop
**Module:** spring-core-aop-weaving
提供 LTW（Load-Time Weaving）最小闭环，让学习者验证：织入不是代理，拦截不依赖“走代理对象的 call path”。

#### Scenario: LTW Weaves Beyond Spring Proxy Boundaries
- 即使目标对象不是 Spring Bean（直接 `new`），仍能被织入并命中 join point
- 自调用（`this.inner()`）不再绕过拦截（与 `spring-core-aop` 的代理对比）

### Requirement: CTW Minimal Loop
**Module:** spring-core-aop-weaving
提供 CTW（Compile-Time Weaving）最小闭环，让学习者验证：无需 `-javaagent` 也能命中 join point（因为字节码已在编译期织入）。

#### Scenario: CTW Works Without Java Agent
- 在无 `-javaagent` 的测试执行中，仍可稳定断言拦截发生
- 能解释“CTW 与 LTW 的主要差异：织入时机与部署复杂度”

### Requirement: Join Points and Advanced Pointcuts
**Module:** spring-core-aop-weaving
补齐 proxy AOP 无法覆盖的 join point 与表达式能力边界，并提供可复现断言。

#### Scenario: Cover Non-Proxy Join Points
至少覆盖并可断言：
- `call` vs `execution` 的差异（调用点 vs 被调用体）
- constructor join point（构造器执行/调用）
- field `get/set`（字段读写）
- 至少一个高级表达式：`withincode` 或 `cflow`（用于展示“更强但更难维护”的边界）

### Requirement: Learning Navigation
**Module:** spring-core-aop-weaving
同步学习导航与知识库，使学习路径可发现、可串联。

#### Scenario: Discoverable in Root README and Wiki
- 根 `README.md` 的学习路线/模块目录出现 `spring-core-aop-weaving`
- `helloagents/wiki/overview.md` 与模块文档增加对应条目与学习建议

## Risk Assessment

- **Risk:** 引入 `-javaagent` 与 CTW 编译插件可能增加构建复杂度，影响排障成本。
  - **Mitigation:** 限制影响范围：插件仅配置在 `spring-core-aop-weaving` 模块；提供清晰的 docs 与断点/验证路径；Labs 数量控制在最小可解释集合。
- **Risk:** LTW/CTW 同时存在时，可能出现“重复织入/理解混淆”。
  - **Mitigation:** 用明确的包结构与测试分组隔离；在 docs 里给出对照表与推荐阅读/运行顺序。

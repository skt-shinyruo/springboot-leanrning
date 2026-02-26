# Technical Design: spring-core-aop-weaving（AspectJ LTW/CTW）

## Technical Solution

### Core Technologies

- Java 17（运行环境要求；父工程 enforcer 约束）
- Java 16（本模块编译目标：为兼容 `aspectj-maven-plugin` 内置 ajc 的 source level 上限；不影响 weaving 关键结论）
- Maven（多模块聚合）
- Spring Boot（沿用仓库学习风格：可运行入口 + 可观察输出；本模块不依赖 Boot 自动装配能力）
- AspectJ：
  - `aspectjrt`：运行期 AspectJ 注解/切面模型支持
  - `aspectjweaver`：LTW 所需的 weaver + `-javaagent`
- Maven 插件：
  - `maven-surefire-plugin`：将 LTW/CTW 的测试执行隔离（是否携带 `-javaagent`）
  - `maven-dependency-plugin`：把 `aspectjweaver.jar` 固化复制到 `target/`，避免依赖本地仓库路径
  - `aspectj-maven-plugin`：CTW（编译期织入，ajc）

### Implementation Key Points

1. **心智模型拆分**
   - `spring-core-aop`：proxy-based AOP（call path + proxy 才生效）
   - `spring-core-aop-weaving`：weaving-based AOP（在字节码层改调用点/被调用体，可覆盖更多 join point）

2. **隔离 LTW 与 CTW 的可验证闭环**
   - LTW：测试 JVM 以 `-javaagent` 启动，针对“非 Spring Bean 也能拦截 / 自调用也能拦截 / 更多 join point”给出断言
   - CTW：通过 `aspectj-maven-plugin` 在编译期织入，并在**无 agent** 的测试执行里断言拦截仍发生

3. **统一“可断言观察点”**
   - 复用 `InvocationLog` 思路：不依赖日志文本，而是用结构化记录点（方法签名、join point 类型、顺序）做断言

4. **范围控制**
   - CTW 仅对指定包/指定类织入（避免 LTW 叠加造成重复触发）
   - Labs 覆盖最小集合，但确保能解释“为什么 proxy AOP 做不到”

## Architecture Decision ADR

### ADR-001: 将 weaving 独立为 `spring-core-aop-weaving` 模块

**Context:** `spring-core-aop` 已聚焦 Spring AOP 代理主线；weaving 引入 `-javaagent` 与编译插件，复杂度与运行方式不同。  
**Decision:** 新增独立模块承载 weaving。  
**Rationale:** 隔离构建复杂度与学习路径；避免把“代理主线”污染为“所有 AOP 形态的混杂集合”。  
**Alternatives:**  
- 方案 A：直接扩写 `spring-core-aop` → Rejection reason: 构建/运行方式差异太大，学习者容易混淆“代理 vs 织入”。  
- 方案 B：只写文档不加实验 → Rejection reason: 无法形成可验证闭环，学习价值不足。  
**Impact:** 增加一个模块与少量导航维护成本，但整体可控。

### ADR-002: 默认构建同时跑 LTW + CTW 的 Labs

**Context:** 本仓库约定 Labs 默认启用，`mvn test` 应可直接验证关键结论。  
**Decision:** 在 `spring-core-aop-weaving` 模块内配置测试分组，使 `mvn -pl spring-core-aop-weaving test` 即可跑完 LTW+CTW 两套 Labs。  
**Rationale:** 降低学习者“需要记很多命令/参数”的门槛，保证可验证闭环。  
**Alternatives:**  
- 方案 A：用 Maven profile 手动切换 → Rejection reason: 易忘、难复现；与仓库 Labs 约定不一致。  
**Impact:** 测试执行次数可能略增，但 Labs 数量很少，可接受。

### ADR-003: CTW 采用 `aspectj-maven-plugin` 并限制织入范围

**Context:** CTW 需要在编译期织入字节码；同时模块也需要 LTW（agent）。若不限制范围，可能出现“重复织入/重复触发”的混淆。  
**Decision:** 使用 `aspectj-maven-plugin`（ajc）做 CTW，并通过包结构/织入配置限制 CTW 的织入目标。  
**Rationale:** 保证 LTW 与 CTW 的对照清晰、可复现、可断言。  
**Alternatives:**  
- 方案 A：CTW 织入全量类 → Rejection reason: 与 LTW 混叠，难解释/难排障。  
**Impact:** 需要更明确的包结构与 Maven 配置，但总体复杂度可控。

## Security and Performance

- **Security:** 本模块仅用于学习，不应将 `-javaagent` 运行方式直接迁移到生产环境；文档中会明确风险与边界。
- **Performance:** weaving 会引入类加载/编译期开销；Labs/示例以最小集合为主，避免引入不必要的性能噪音。

## Testing and Deployment

- 模块测试（默认包含 LTW+CTW Labs）：`mvn -pl spring-core-aop-weaving test`
- 全仓库测试（可选）：`mvn -q test`

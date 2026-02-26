# 技术设计：完善 Spring Core 基础模块学习体验（Beans / AOP / Tx / Events）

## 技术方案

### 核心技术
- Java 17
- Spring Boot（父工程统一管理版本）
- Spring Framework（随 Spring Boot 依赖）
- 测试：JUnit 5 + AssertJ（`spring-boot-starter-test`）

### 实现要点
1. **README / docs 一致化**
   - 每个模块 README 固化统一导航结构（学习目标 → 推荐阅读顺序 → Labs/Exercises → 运行态观察点）
   - docs 与测试类之间保持互链：从“概念”可以定位到“可断言实验”，从“实验”可以回到“机制解释”

2. **以可断言的观察点为核心**
   - 新增/调整 Labs：优先增加“可断言对象”（例如 InMemoryLog/InvocationLog）而不是只靠控制台输出
   - 异步/并发场景：使用 `CountDownLatch` + 超时等待，避免 `Thread.sleep` 带来的不稳定

3. **运行态观察点（spring-boot:run）增强但不引入端点**
   - 通过现有 `*DemoRunner` 输出结构化信息（线程名、代理类型、事务活跃状态、事件执行链路摘要等）
   - 只作为“建立直觉”的辅助；最终以测试断言作为结论来源

## 模块级落地设计：spring-core-beans（深入）

> 目标：在本项目现有基础上“补强 + 串联”，让初学者能沿着一条主线把容器机制学透，并把结论固化为断言。

### 1) README 设计（学习入口不迷路）
- 增加 “Start Here（5 分钟闭环）”：只给 2–3 条命令与 1 个核心结论（避免信息过载）
- 增加 “学习路线（入门→进阶→深挖）”：把 30+ Labs 收敛为 3 条明确路径，并为每条路径给出退出条件
- 增加 “容器主线（refresh call chain）一页纸”：以 `AbstractApplicationContext#refresh` 为主线，把 BFPP/BPP/注入/初始化/销毁映射到本模块 docs 与 Labs

### 2) docs 设计（从现象到断点）
- `docs/00-deep-dive-guide.md`：补齐“断点地图（按阶段）”与“从一个入口测试走完整链路”的操作步骤
- `docs/11-debugging-and-observability.md`：补齐 DI 解析与生命周期排查 Playbook（重点是：如何判断是定义层问题、还是实例层问题）

### 3) DemoRunner 输出设计（运行态可观察，但不替代断言）
- 统一输出为稳定 key（便于初学者对照 README）：
  - 容器信息：active profiles、beanDefinitionCount
  - DI 观察：`TextFormatter` beans 列表、FormattingService 实际注入实现
  - scope 观察：prototype direct/provider 的差异
  - 生命周期观察：`@PostConstruct` 标记是否已触发
- 约束：不引入 Web/端点，不依赖时序，不引入额外依赖

### 4) Labs 设计（补强“容器主线时间线”）
- 优先复用并扩展现有 Labs：
  - `SpringCoreBeansBeanCreationTraceLabTest`（实例化→注入→初始化→BPP 包装/替换）
  - `SpringCoreBeansContainerLabTest`（BeanDefinition vs instance、BFPP/BPP 的最小闭环）
  - `SpringCoreBeansLifecycleCallbackOrderLabTest`（生命周期回调顺序）
- 当现有 Labs 无法清晰表达“完整时间线”时，再新增一个专门的 Lab，要求：单主题、断言稳定、避免 println 依赖

### 5) 面试点落地规范（插入正文对应小节）

> 目标：把“面试常问的提问方式”直接融入机制解释正文，帮助学习者做到“能讲清 + 能追问 + 能复现”。

**插入格式（统一模板，直接写在正文对应段落附近）：**

- **题目：**（一句话，面试官问法）
- **追问：**（2–3 个，按深入方向展开）
- **本仓库复现入口：**（至少 1 个 Test/Lab；必要时补充最小 Lab）

**插入位置原则：**
- 必须放在“概念解释刚讲完、还没跳到下一章/下一节”的位置（避免变成章末附录）
- 只覆盖当前小节主题，不在一个块里混多个主题

**本次新增面试点（按主题映射到章节）：**
- BeanFactory vs ApplicationContext → `docs/01` 对应小节
- 依赖解析决策树（候选收集/收敛/泛型/集合注入）→ `docs/03` / `docs/33` / `docs/30`
- Aware 回调的时机与触发者（谁调用、发生在注入链路哪里）→ `docs/05` / `docs/17`
- 三级缓存/early reference 与代理交互 → `docs/16`（必要时补充最小 Lab）
- proxy 形态与类型系统陷阱（JDK vs CGLIB、按实现类获取失败、谁替换了对象）→ `docs/31`（必要时补充最小 Lab）

## 架构决策 ADR

### ADR-1：Spring Core 模块保持非 Web，不引入 Actuator 端点
**Context：**
本次目标是增强基础机制学习体验，并要求提供运行态观察点；但引入 Web/Actuator 端点会扩大依赖与运行面，且需要端口规划与暴露边界控制。

**Decision：**
本次改造不为 `spring-core-*` 模块新增 Web/Actuator 端点；运行态观察通过日志/控制台输出提供。端点/Actuator 的学习与验证仍以 `springboot-actuator` 模块为主。

**Rationale：**
- 维持模块“机制学习”定位：最小依赖、最小运行面、可在纯测试环境复现实验
- 避免端口冲突与暴露配置风险
- 符合“断言优先于日志”的测试约定

**Alternatives：**
- 方案 A：为每个模块引入 `spring-boot-starter-web` + `spring-boot-starter-actuator`
  - 拒绝原因：范围扩大、端口与暴露边界复杂、学习成本上升
- 方案 B：仅在 `web` profile 下启用端点
  - 拒绝原因：仍需维护两套运行形态；本次选择更小范围落地（后续可演进）

**Impact：**
- 运行态观察以控制台/日志为主
- 不新增网络暴露面，降低安全风险

## 安全与性能
- **安全：**
  - 不新增对外端点，不引入额外暴露面
  - 避免在示例中引入敏感信息、硬编码密钥
  - 测试与示例避免“生产环境操作”类高风险行为
- **性能：**
  - 测试优先小上下文、稳定断言，控制执行时间
  - 异步测试使用超时等待而非睡眠，减少偶发失败

## 测试与验证
- 单模块验证：
  - `mvn -pl spring-core-beans test`
  - `mvn -pl spring-core-aop test`
  - `mvn -pl spring-core-events test`
  - `mvn -pl spring-core-tx test`
- 全仓库回归：`mvn -q test`
- 运行态观察（可选）：
  - `mvn -pl spring-core-beans spring-boot:run`
  - `mvn -pl spring-core-aop spring-boot:run`
  - `mvn -pl spring-core-events spring-boot:run`
  - `mvn -pl spring-core-tx spring-boot:run`

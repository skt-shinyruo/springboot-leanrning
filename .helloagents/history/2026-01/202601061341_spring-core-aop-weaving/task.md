# Task List: spring-core-aop-weaving（AspectJ LTW/CTW）

Directory: `helloagents/plan/202601061341_spring-core-aop-weaving/`

---

## 0. Definition of Done（完成标准 / 验收口径）
- [√] 0.1 模块可发现：根 `pom.xml` 已聚合，根 `README.md` 与 `helloagents/wiki/*` 可导航到本模块
- [√] 0.2 双闭环可验证：LTW（带 `-javaagent`）与 CTW（无 `-javaagent`）都能通过断言证明“确实发生 weaving”
- [√] 0.3 覆盖非代理能力边界：至少覆盖 `call/execution`、constructor、field `get/set`，并通过事件 kind 做断言（不是只靠 print）
- [√] 0.4 覆盖高级表达式：至少覆盖 `withincode` 与 `cflow`（或等价控制流/语境限定表达式）并可断言
- [√] 0.5 Docs 体系完整：模块 `README.md` + `docs/README.md` + Part 00/01/02/03/04 + Appendix（坑清单/自测题）形成学习闭环
- [√] 0.6 Exercises 可扩展：提供默认 `@Disabled` 的练习题入口（要求写断言 + 保持 Labs 不回归）
- [√] 0.7 构建范围受控：AspectJ 依赖/插件只出现在新模块中，不影响其它模块构建

## 1. spring-core-aop-weaving（模块骨架与聚合构建）
- [√] 1.1 在父工程 `pom.xml` 注册新模块 `spring-core-aop-weaving`，并创建模块目录骨架（`src/main`/`src/test`/`docs`），验证 `why.md#requirement-module-bootstrap`
- [√] 1.2 创建模块 `spring-core-aop-weaving/pom.xml`：引入 `aspectjrt`/`aspectjweaver` 与测试依赖（JUnit5/Boot Test），并定义 `aspectj.version` 属性，验证 `why.md#requirement-module-bootstrap`
- [√] 1.3 配置 `maven-dependency-plugin`：在测试前把 `aspectjweaver.jar` copy 到 `spring-core-aop-weaving/target/`（稳定的 `-javaagent` 路径），验证 `why.md#requirement-ltw-minimal-loop`
- [√] 1.4 配置 `maven-surefire-plugin`：隔离两套测试执行
  - LTW：携带 `-javaagent:${project.build.directory}/aspectjweaver.jar`
  - CTW：不携带 `-javaagent`
  验证 `why.md#requirement-ltw-minimal-loop` 与 `why.md#requirement-ctw-minimal-loop`
- [√] 1.5 新增最小入口类（仅用于演示/手动运行）：`spring-core-aop-weaving/src/main/java/com/learning/springboot/springcoreaopweaving/SpringCoreAopWeavingApplication.java`，验证 `why.md#requirement-module-bootstrap`
- [√] 1.6 新增 `spring-core-aop-weaving/src/main/resources/application.properties`（`spring.application.name=spring-core-aop-weaving`），验证 `why.md#requirement-module-bootstrap`

## 2. spring-core-aop-weaving（统一可断言观察点：InvocationLog v2）
- [√] 2.1 新增可断言事件结构（建议：`JoinPointEvent`/`JoinPointKind`/`SourceLocation`），用于记录 kind（call/execution/get/set/...）、签名、顺序与线程信息，验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [√] 2.2 新增 `InvocationLog`（线程安全或测试内可控），提供 `record(...)`/`reset()`/`events()` 等最小 API，验证 `why.md#requirement-module-bootstrap`
- [√] 2.3 新增测试断言工具（建议：`InvocationLogAssertions`）：支持“包含/顺序/次数”断言，减少 Labs 重复代码，验证 `why.md#requirement-module-bootstrap`

## 3. spring-core-aop-weaving（Docs：目录结构与学习路线）
- [√] 3.1 新增模块级入口文档 `spring-core-aop-weaving/README.md`：目标、前置、命令、Labs/Exercises 索引、与 `spring-core-aop` 的对照入口，验证 `why.md#requirement-learning-navigation`
- [√] 3.2 新增 docs 目录页 `docs/aop/spring-core-aop-weaving/README.md`：按 Part 列出章节与推荐顺序，验证 `why.md#requirement-learning-navigation`
- [√] 3.3 新增 Part 00（阅读指南）：解释“为什么单独开模块 + 如何跑 LTW/CTW + 如何排障”，验证 `why.md#requirement-learning-navigation`
- [√] 3.4 新增 Part 01（心智模型）：proxy vs weaving、join point 概览、`call vs execution`、`this/target` 的语义差异，验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [√] 3.5 新增 Part 02（LTW）：`-javaagent`、`META-INF/aop.xml`、include/exclude、classloader 相关坑，验证 `why.md#requirement-ltw-minimal-loop`
- [√] 3.6 新增 Part 03（CTW）：`aspectj-maven-plugin`、织入范围控制、无 agent 的验证思路，验证 `why.md#requirement-ctw-minimal-loop`
- [√] 3.7 新增 Appendix：常见坑清单 + 自测题（至少覆盖：重复织入、未命中 aop.xml、IDEA/命令行差异、`call` 误用），验证 `why.md#requirement-join-points-and-advanced-pointcuts`

## 4. spring-core-aop-weaving（LTW：基础设施与最小闭环）
- [√] 4.1 新增 `spring-core-aop-weaving/src/test/resources/META-INF/aop.xml`：声明 LTW 要织入的 aspects 与织入范围（避免全量织入），验证 `why.md#requirement-ltw-minimal-loop`
- [√] 4.2 新增 LTW aspect（建议包名 `part02_ltw_fundamentals`）：至少实现 `execution` 与 `call` 两类 advice，并把关键字段记录到 `InvocationLog`，验证 `why.md#requirement-ltw-minimal-loop`
- [√] 4.3 新增 LTW 目标对象（Plain Java，不依赖 Spring）：`SelfInvocationWeavingTarget`（outer→this.inner）、`ConstructorWeavingTarget`、`FieldAccessWeavingTarget` 等，验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [√] 4.4 新增 LTW “agent 生效” Lab：用最小断言证明 weaving 已发生（不要只靠打印），验证 `why.md#requirement-ltw-minimal-loop`

## 5. spring-core-aop-weaving（LTW Labs：join point 与表达式覆盖）
- [√] 5.1 新增 Lab：非 Spring Bean（直接 `new`）也能被拦截（execution），验证 `why.md#requirement-ltw-minimal-loop`
- [√] 5.2 新增 Lab：自调用不再绕过拦截（断言 outer 与 inner 都被拦截），验证 `why.md#requirement-ltw-minimal-loop`
- [√] 5.3 新增 Lab：`call` vs `execution` 对照（同一业务操作下两者触发次数/位置不同），验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [√] 5.4 新增 Lab：constructor join point（`call(new(..))`/`execution(new(..))`）可被织入并断言触发，验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [√] 5.5 新增 Lab：field `get/set` join point 可被织入并断言触发（强调与 getter/setter 的区别），验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [-] 5.6（可选强化）新增 Lab：`handler(ExceptionType)` 或 `initialization/preinitialization/staticinitialization` 至少其一，用于展示“代理 AOP 完全覆盖不了的 join point”，验证 `why.md#requirement-join-points-and-advanced-pointcuts`

## 6. spring-core-aop-weaving（LTW Labs：高级表达式）
- [√] 6.1 新增 Lab：`withincode(...)` 限定命中范围（同一个被调用方法，在不同 caller 中命中不同），验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [√] 6.2 新增 Lab：`cflow(...)`（或 `cflowbelow(...)`）限定控制流范围（仅在特定入口调用链中生效），验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [-] 6.3 新增 Lab：展示 `this/target` 在 weaving 场景下的语义（并给出与 `spring-core-aop` 的对照提示），验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [-] 6.4（可选强化）新增 Lab：参数与注解相关表达式（任选其一或组合）
  - `args(...)` / `@args(...)`
  - `@annotation(...)` / `@within(...)` / `@withincode(...)`
  用于补齐“语义强但误判多”的典型表达式边界，验证 `why.md#requirement-join-points-and-advanced-pointcuts`

## 7. spring-core-aop-weaving（CTW：编译期织入构建与范围控制）
- [√] 7.1 设计 CTW 专用包结构（建议 `part03_ctw_fundamentals` 与 `ctwtargets`）：确保与 LTW targets 分离，验证 `why.md#requirement-ctw-minimal-loop`
- [√] 7.2 配置 `aspectj-maven-plugin`（ajc）进行 CTW：指定织入目标包、编译级别（16，受 ajc source level 限制）、lint 策略，验证 `why.md#requirement-ctw-minimal-loop`
- [√] 7.3 明确并防止“重复织入”风险：CTW 仅织入 `ctwtargets`；LTW 的 `aop.xml` 仅包含 LTW aspects/targets，验证 `why.md#requirement-ctw-minimal-loop`
- [√] 7.4 在 surefire 的 CTW 执行中显式断言 JVM 未携带 `-javaagent`（例如读取 inputArguments），验证 `why.md#requirement-ctw-minimal-loop`

## 8. spring-core-aop-weaving（CTW Labs：无 agent 的可验证闭环）
- [√] 8.1 新增 CTW Lab：无 `-javaagent` 仍可拦截（execution），验证 `why.md#requirement-ctw-minimal-loop`
- [√] 8.2 新增 CTW Lab：自调用同样可被拦截（outer→inner），验证 `why.md#requirement-ctw-minimal-loop`
- [√] 8.3 新增 CTW Lab：覆盖至少 2 类“非代理 join point”（建议 constructor + field get/set），验证 `why.md#requirement-join-points-and-advanced-pointcuts`

## 9. spring-core-aop-weaving（Exercises：默认禁用的改造题/补全题）
- [√] 9.1 新增 `*ExerciseTest.java`（`@Disabled`）：引导学习者扩展 join point 或高级表达式，并通过结构化断言验证（保持所有 Labs 仍然全绿），验证 `why.md#requirement-join-points-and-advanced-pointcuts`
- [-] 9.2（增强题）拆分多个 exercises（依难度分层）：
  - Exercise A：补齐一个高级表达式（`cflowbelow` / `withincode` 组合 / `within` 等）
  - Exercise B：补齐一个“代理 AOP 覆盖不了”的 join point（`handler` / `initialization` / `preinitialization` 等）
  - Exercise C：补齐一个“语义强但易误判”的表达式（`args/@annotation/@within` 等）
  验证口径：每个 Exercise 必须能写出“命中 + 次数 + 不误伤”的断言
- [√] 9.3 在 docs 中标注 Exercises 开启方式与预期学习产出（与仓库统一规范一致），验证 `why.md#requirement-learning-navigation`

## 10. Security Check
- [√] 10.1 执行安全检查（按 G9）：不引入明文敏感信息；插件配置限定在新模块；文档明确 `-javaagent` 的边界与风险（学习用途），验证 `why.md#requirement-learning-navigation`

## 11. Documentation Update（学习导航与知识库同步）
- [√] 11.1 更新根 `README.md`：学习路线/模块目录/索引，加入 `spring-core-aop-weaving`，并给出与 `spring-core-aop` 的对照入口，验证 `why.md#requirement-learning-navigation`
- [√] 11.2 新增 `helloagents/wiki/modules/spring-core-aop-weaving.md`：模块规格、目录索引、Labs/Exercises 入口与完成标准，验证 `why.md#requirement-learning-navigation`
- [√] 11.3 更新 `helloagents/wiki/overview.md`：模块索引新增 `spring-core-aop-weaving`，验证 `why.md#requirement-learning-navigation`
- [√] 11.4 更新 `helloagents/wiki/modules/spring-core-aop.md`：补充“代理 AOP vs weaving 的边界链接”，验证 `why.md#requirement-learning-navigation`
- [√] 11.5 更新 `helloagents/CHANGELOG.md`：记录新增模块与关键能力（LTW/CTW + join point 覆盖），验证 `why.md#requirement-learning-navigation`

## 12. Quality Verification
- [√] 12.1 运行并通过：`python3 scripts/check-md-relative-links.py docs/aop/spring-core-aop-weaving`
- [√] 12.2 运行并通过：`mvn -pl spring-core-aop-weaving test`
- [-] 12.3（可选）运行全仓库：`mvn -q test`（如耗时过长，可先跳过但需在结果中标注）

## 13. Advanced Topics Backlog（可选：更全面更深入的延伸任务）
- [-] 13.1 扩写 Join Point Cookbook：补齐 `handler/initialization/preinitialization/staticinitialization/adviceexecution` 的最小示例与“何时用/何时别用”
- [-] 13.2 扩写 Pointcut Cookbook：补齐 `within/args/@annotation/@within/@withincode/this/target` 的语义对照与易错点（至少包含 1 个反例）
- [-] 13.3 增加一个“误判演示” Lab：展示 `call` 误用、过宽 `within(..)`、或 `cflow` 滥用导致的大面积命中（并给出限界写法）
- [-] 13.4 增加一个“可维护性提醒”章节：pointcut 稳定性、重构脆弱点、与测试回归策略
- [-] 13.5 增加一个 Spring 集成向 Labs（可选）：`LoadTimeWeaver` / `@EnableLoadTimeWeaving`（仅做机制展示，不建议生产直接照搬）
- [-] 13.6 增加一个 CTW 产物排障向章节：如何确认“运行的是织入后的 class”（例如 `-showWeaveInfo`/构建产物检查）

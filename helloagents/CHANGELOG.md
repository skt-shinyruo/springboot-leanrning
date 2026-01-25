# Changelog

本文件记录本仓库的重要变更，格式参考 Keep a Changelog，版本号遵循语义化版本（SemVer）。

## [Unreleased]
### Added
- `spring-core-beans` 补齐缺失章节：`020-09-auto-config-ordering.md`

### Added
- spring-core-beans：新增 Debugger Pack 文档与主线/断点/排障/性能入口测试套件

### Removed
- 移除 docs 相关的检查脚本与 docs-site 发布 workflow，并清理文档/知识库中的引用

### Changed
- spring-core-beans：docs 目录入口补充 Debugger Pack 链接，同步知识库入口索引

### Changed
- `docs/book/debugger-pack.md`：补充“模块深挖最小闭环（Doc/Test/Perf）”验收口径（用于全模块 A–E 统一标准化）。
- `docs/**`：为 `springboot-autoconfiguration` / `springboot-logging` / `springboot-observability` / `spring-core-events` 目录页补齐并发/性能 Lab 可跑入口命令。
- `spring-boot-*/README.md`：修正部分模块 README 标题与并发/性能 Lab 索引，避免与 `spring-boot-*` artifactId 命名脱节。
- `docs/book/labs-index.md`：新增/刷新 4 个并发/性能 Lab 的索引入口。
- `docs/SUMMARY.md`：补齐全量模块的“调用链”导航项，并将新增主题（AutoConfiguration/Logging/Observability/SpEL）接入站点目录（SSOT）。
- `docs/topics/index.md`、`docs/README.md`：更新主题索引，纳入新增主题模块。
- `docs`：将所有模块文档从 `<module>/docs/` 迁移到仓库根 `docs/`，并按主题分组为 `docs/<topic>/<module>/...`；Book 内容从 `docs-site/content/book/` 迁移到 `docs/book/`。
- `docs-site`：MkDocs 站点改为直接读取 `../docs`，并通过 `mkdocs-literate-nav` 以 `docs/SUMMARY.md` 作为站点导航 SSOT（文档即目录）。
- `docs`：站点导航 SSOT 由 YAML/脚本注入切换为 `docs/SUMMARY.md`（Markdown 目录文件，顺序/显示名/层级都以此为准）。
- `docs-site`：侧边栏目录收敛为“索引级入口”（README + Guide + Pitfalls/Self-check），避免展开全部章节导致导航爆炸。
- `docs`：清理文档中的 `/book/` 绝对链接，统一改为相对链接，兼容 GitHub Pages 子路径部署。
- 调整 Maven 工程结构：按 tutorials 风格分组聚合到 `spring-boot-modules/` 与 `spring-core-modules/`。
- 修复 Book/模块文档跨目录引用的相对链接（避免 `docs/docs/...` 断链）。
- `docs/async-scheduling` / `docs/events` / `docs/spel`：在模块目录页增加“性能与并发（可复现实验范式）”入口与可跑样板命令（线程池饱和 / 异步分发线程边界 / SpEL 并发求值）。
- `docs`：补齐剩余模块目录页的 Solutions 入口与并发/性能 Lab 入口，并互链到 Book 工具页（Exercises & Solutions / 性能与并发）。

### Removed
- `docs`：删除旧的导航/主题 SSOT：`docs/topics/topics.yml`（避免与 `docs/SUMMARY.md` 双轨维护）。
- `scripts`：删除旧的导航同步/注入脚本：`scripts/docs-topics-sync.py`、`scripts/docs-site-sync.py`。

### Added
- `scripts/audit-module-deep-dive.sh`：从方案包 `task.md` 解析 22 个模块的 docs/tests/perf 入口清单，并输出缺失项（用于全模块深挖标准化的“可审计基线”）。
- `spring-boot-autoconfiguration` / `spring-boot-logging` / `spring-boot-observability` / `spring-core-events`：新增并发/性能可复现实验 `*ConcurrencyLabTest`（无耗时阈值断言），并接入模块目录页与知识库模块页入口。
- `docs/web-mvc/springboot-web-mvc`：新增“请求调用链速览”章节（FilterChain → DispatcherServlet#doDispatch），用于断点定位与证据链入口。
- 新增主题模块：`springboot-autoconfiguration` / `springboot-logging` / `springboot-observability` / `spring-core-spel`（含代码 + Labs + docs 骨架），并纳入 docs-site/labs 索引。
- `docs`：新增站点目录文件 `docs/SUMMARY.md`（Markdown 目录文件，作为站点导航 SSOT）。
- `docs-site`：新增 `mkdocs-literate-nav` 依赖（固定版本），用于从 `docs/SUMMARY.md` 解析生成导航。
- 全模块补齐 Book Matrix（每章最小可运行测试入口），并在 Book 主线章节与知识库模块页统一入口。
- `docs-site`：新增 MkDocs 文档站点骨架（`docs-site/mkdocs.yml` + serve/build 脚本），将仓库根 `docs/` 构建为可搜索、可侧边栏导航的静态站点；build 输出已加入 `.gitignore`。
- `docs-site`：新增“写作指南”页面 `docs/book-style.md`，用于统一书籍化重排原则（主线时间线先行、提示框作为插入段、redirect 保留旧入口）。
- `docs-site`：在写作指南中新增“章节学习卡片（五问闭环）”规范（知识点 / 怎么使用 / 原理 / 源码入口 / 推荐 Lab），用于把每章的学习目标、可运行入口与源码证据链收敛到第一屏。
- `docs-site`：新增“主线之书（Book-only）”目录骨架（`docs/book/`），覆盖 18 模块的跨模块时间线章节树，并新增工具页（Labs 索引 / Debugger Pack / Exercises & Solutions / 迁移规则）。
- `docs/book/performance-and-concurrency.md`：新增“性能与并发（可复现实验范式）”工具页（可复现套路 + 样板索引）。
- `springboot-autoconfiguration` / `springboot-logging` / `springboot-observability` / `spring-core-spel` / `spring-core-events` / `springboot-async-scheduling`：新增 Exercises 对应 Solutions（默认参与回归）。
- `springboot-async-scheduling`：新增并发/性能可复现实验（线程池饱和/拒绝策略）与测试辅助 `Waiter`。
- `spring-core-spel`：新增并发求值 Lab（复用 parsed expression + per-thread EvaluationContext）。
- `springboot-basics` / `springboot-web-mvc` / `springboot-data-jpa` / `springboot-actuator` / `springboot-testing` / `springboot-business-case` / `springboot-security` / `springboot-web-client` / `springboot-cache` / `spring-core-aop` / `spring-core-aop-weaving` / `spring-core-tx` / `spring-core-validation` / `spring-core-resources` / `spring-core-profiles` / `spring-core-beans`：补齐/接入并发与性能可复现实验（每模块至少 1 个 `part02_perf_concurrency/*LabTest`）并补齐缺失 Exercises 对应 Solutions（Tx/Validation/Resources/Profiles + Beans Part04）。
- GitHub Pages：不再提供自动构建与发布 workflow（如需发布请自行配置）。
- `helloagents`：新增学习路线图 `helloagents/wiki/learning-path.md`，并在 `helloagents/wiki/overview.md` 与四模块页（Beans/AOP/Tx/Web MVC）增加 Start Here/路线图入口，收敛新读者的“先跑什么/再读什么”路径。
- `spring-core-beans`：新增 30 分钟快启章节（Start Here），并系统补齐/强化 docs（容器主线、BPP 顺序、FactoryBean、循环依赖、AOT/真实世界等）与可运行证据链；同时更新 `scripts/generate-spring-beans-public-api-index.py` 并重新生成 Appendix 95/96（补齐“坑点与排障”）。
- `spring-core-beans`：新增“主线叙事”章节：从 `AbstractApplicationContext#refresh` 走到 `AbstractAutowireCapableBeanFactory#doCreateBean`，聚焦关键方法与关键分支，并在导读/目录页增加入口：`docs/beans/spring-core-beans/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`。
- `spring-core-beans`：在主线叙事章补充“分支决策表”（现象 → 阶段 → 关键方法 → 必看变量 → LabTest），把主线叙事进一步压缩成可复用排障套路。
- `spring-core-beans`：继续深化主线叙事与导读：补齐 `preInstantiateSingletons` 的关键分支伪代码（FactoryBean/SmartFactoryBean/SmartInitializingSingleton/background init），补齐 `doGetBean` 的关键分支（dependsOn/parent fallback/prototype guard）；同时在 bootstrap 章新增“处理器速查表 + 时机时间线 + 过早 getBean 反例”，在深挖指南新增“症状驱动导航（现象→章节→断点→Lab）”速查表。
- `springboot-web-mvc`：深化“DispatcherServlet 主链路”章节：补齐 `doDispatch` → `processDispatchResult` 的关键方法/关键分支（multipart/async/exception/afterCompletion），并补齐“FilterChain → ExceptionResolvers → Spring Boot `/error`”完整叙事（含 async 两次 dispatch 时间线 + 证据链）；进一步补齐 ERROR vs ASYNC dispatch 对照（DispatcherType=ERROR/ASYNC）与分支决策表，降低排障误判成本。
- `spring-core-events`：新增异步 multicaster 默认 Lab `SpringCoreEventsAsyncMulticasterLabTest`，用于可断言验证自定义 `ApplicationEventMulticaster` + `TaskExecutor` 的异步分发主线。
- `springboot-data-jpa`：新增用于 N+1/EntityGraph 验证的示例实体与仓库（`LibraryAuthor/LibraryBook`），并增强 `BootDataJpaLabTest` 覆盖 `getReferenceById` 懒代理与 N+1/EntityGraph 边界的可断言证据链。
- `scripts`：新增 docs 书本化批处理脚本 `scripts/bookify-docs.py`：以 `docs/README.md` 为 SSOT，对每章 upsert 统一尾部区块（`### 对应 Lab/Test` + `上一章｜目录｜下一章`），确保可重复执行。
- `spring-core-events`：补齐事务事件最小闭环（`@TransactionalEventListener` AFTER_COMMIT/rollback）Lab，并在 docs/07 增加对应入口块；测试依赖补齐 `spring-tx`。
- `springboot-web-mvc`：补齐传统 MVC（HTML）页面渲染主线（Thymeleaf/表单提交/校验回显/PRG/错误页/Accept 内容协商），新增对应 docs 与 MockMvc + 端到端 Labs，并补齐 API 侧 malformed JSON/type mismatch 的统一错误体。
- `springboot-web-mvc`：安全基线补充：模板输出默认使用转义（`th:text`），错误页仅展示必要信息，不输出堆栈与敏感细节（为后续接入安全模块预留空间）。
- `springboot-web-mvc`：新增 advanced deep dive：补齐 MVC 机制内核（DispatcherServlet/ArgumentResolver/MessageConverter）、REST 契约与 Jackson（406/415/strict media type）、真实 HTTP 场景（CORS/上传下载/静态资源）、Async/SSE 与排障工具箱，并新增对应 Labs/章节入口。
- `springboot-web-mvc`：进一步深化：新增知识地图、绑定/校验分支补齐（@ModelAttribute/BindException）、ProblemDetail 对照、ETag/304、以及 Part 08（Security/Observability：401/403/CSRF + metrics）与对应 Labs/排障升级。
- `springboot-web-mvc`：再深化一层：新增 ExceptionResolvers 主线章节、Filter/Interceptor（sync+async lifecycle）可断言 Lab、条件请求对照（静态资源 If-Modified-Since/304 + ShallowEtagHeaderFilter）、以及 DeferredResult（timeout/fallback）测试闭环；同步补齐“坑点清单 → 可复现测试入口”绑定。
- `springboot-web-mvc`：继续深化：补齐导读/自测/Part01+Part02 的“坑点待补齐”占位内容，并新增两组工程化边界 Labs：`@InitBinder#setAllowedFields` 防 mass assignment、`@ControllerAdvice` 的 `@Order` 优先级可复现验证。
- `springboot-web-mvc`：深化 v3：新增 `@ControllerAdvice` 匹配规则可复现 Labs（`basePackages/annotations/assignableTypes` + selector 并集 OR 语义 + `@Order` 叠加），新增 binder `BindingResult#getSuppressedFields()` 证据链，以及 HttpMessageConverter 选择可观测（`ResponseBodyAdvice` 写入 `selectedConverterType/selectedContentType` 到响应头）与对应 docs 章节/排障清单升级。
- 全模块：新增“主线时间线”章节 `<module>/docs/part-00-guide/03-mainline-timeline.md`（18 模块），并在该章节内提供至少 1 个可跑入口（`*LabTest`），用于把“顺读主线”变成可验证证据链。
- `spring-core-beans`：新增循环依赖边界 Lab `SpringCoreBeansCircularDependencyBoundaryLabTest`（constructor cycle fail-fast vs `@Lazy`/`ObjectProvider` 打断），并同步补齐循环依赖章节与 early reference 交叉链接入口。
- `spring-core-aop`：新增 exposeProxy 可验证 Lab `SpringCoreAopExposeProxyLabTest` + 示例 `ExposeProxyExampleService`，并补齐 exposeProxy 文档与“自调用绕过代理”排障条目。
- `spring-core-tx`：新增传播行为进阶 Lab `SpringCoreTxPropagationMatrixLabTest`（MANDATORY/NEVER/NESTED 对照）与回滚规则 Lab `SpringCoreTxRollbackRulesLabTest`（Runtime vs Checked + rollbackFor/noRollbackFor），并补齐 propagation/rollback 文档与 appendix（pitfalls/self-check）入口。
- `springboot-web-mvc`：新增异常解析链路 Lab `BootWebMvcExceptionResolverChainLabTest` + `ExceptionResolverChainController`，用 resolvedException 固化 BindException/MethodArgumentNotValidException/HttpMessageNotReadableException 三类 400 根因差异，并更新对应章节入口块。
- `helloagents`：补齐 `project.md` 的测试类型约定与 Troubleshooting 条目模板（文档↔测试入口↔排障清单对齐）。
- `spring-core-beans`：增强依赖注入候选选择对照：补齐 by-name fallback、`@Qualifier` vs `@Primary`、泛型收敛与 `ObjectProvider#getIfUnique()/orderedStream()` 语义，并同步更新 docs/33 与 appendix（pitfalls/self-check）。
- `springboot-web-mvc`：新增 Security FilterChain vs MVC 异常链路边界 Lab `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`（`handler/resolvedException` 证据链），并同步更新 part-08/part-03/part-07 与 appendix（pitfalls/self-check）入口。
- `springboot-security`：新增多条 `SecurityFilterChain` 分流/顺序可断言 Lab `BootSecurityMultiFilterChainOrderLabTest`，用于验证 matcher 命中链路与 cross-cutting filter 的链内可见性。
- `springboot-data-jpa`：新增 merge/detach 语义默认 Lab `BootDataJpaMergeAndDetachLabTest`，固化 detached 修改不落库与 `merge()` 返回 managed copy 的关键边界。
- `spring-core-events`：新增 listener filtering 默认 Lab `SpringCoreEventsListenerFilteringLabTest`，用于可断言解释“监听器没触发”常见根因（参数类型过滤）。
- `springboot-web-client`：新增 WebClient filter 顺序默认 Lab `BootWebClientWebClientFilterOrderLabTest`，用 `ExchangeFunction` stub 在无真实网络下可断言验证 request 顺序与 response 顺序反转。
- 新增 Web MVC “错误分支矩阵”可运行 Lab：`BootWebMvcErrorBranchMatrixLabTest`（覆盖 400/406/415/406 等关键分支）。
- 全模块：新增“关键分支矩阵（Branch Matrix）”体系：为 18 个模块补齐/新增 `*BranchMatrixLabTest`（JUnit Platform Suite 聚合关键分支），并在模块 README、Book 主线（18 章）、`helloagents/wiki/modules/*.md` 统一入口；同时在各模块 appendix（90/99）加入统一排障模板（Symptoms → Repro → Evidence → Decision → Fix → Verify）与“从 Book/Branch Matrix 进入”的自检路径。
- `spring-core-aop-weaving`：补齐 LTW/CTW 双入口分支矩阵（`AspectjLtwBranchMatrixLabTest`/`AspectjCtwBranchMatrixLabTest`），并增加“运行环境假设/跳过”保护，避免在 Surefire 多 execution 或 `-Dtest=...` 场景下产生误失败。
- 全模块：新增“关键分支矩阵”体系（Branch Matrix / Breakpoint Map / Branch Decision Matrix / Playbook / Self-check）并推广到 18 个模块；同时在 Book 主线 18 章与各模块 README、知识库模块页统一入口与可跑命令（含 `spring-core-aop-weaving` LTW/CTW 环境分流说明）。
- 新增并迁移方案包：`helloagents/history/2026-01/202601201248_tutorials_style_reorg/`（记录 tutorials 风格重排与首批示范落地）。

### Removed
### Changed
- 根 `README.md`：前置“主线之书（Book）”入口（Book TOC + 两条阅读路径），并按推荐学习顺序串起模块与主线节点，降低新读者找路成本。
- `docs/book/`：为主线节点章节补齐“本章可跑入口（最小闭环）”（Lab 命令 + Exercise 路径），让章节具备“读 → 跑 → 改”的独立闭环。
- `docs/book/094/116/138`：改写正文为更“像书”的叙事结构（开场问题 → 心智模型 → 主线时间线 → 读者检查点 → 证据链观察点），减少“像说明书”的条目感。
- `docs-site`：站点导航新增“写作指南”入口（`docs-site/mkdocs.yml`），并在模块侧边栏目录中自动聚合新增的“主线时间线”章节。
- `docs-site`：站点导航切换为 Book-only（侧边栏仅展示“主线之书”章节树）；`scripts/docs-site-sync.py` 改为注入书目录，模块 docs 作为素材库/搜索命中入口保留。
- `docs-site`：Book-only 的“附录”导航补齐知识库入口（overview/learning-path/history）与模块快速入口（模块 README 列表），提升在“书目录”下的跳转效率。
- `docs-site`：Book-only 目录进一步优化：主线章节按 Part 分卷展示、目录标题短化，并改为自动扫描 `docs/book/` 生成 nav（新增章节无需手改脚本中的章节列表）。
- `scripts/check-md-relative-links.py`：支持校验站点绝对链接 `/book/...`（映射到 `docs/book/`），便于模块 docs 使用“redirect 到书章节”的稳定链接形式。
- `springboot-basics`：试点将模块主线时间线章节迁移到书第 1 章，并保留旧入口作为 redirect（避免断链）。
- 全模块 docs：停止使用 A–G（A.本章定位…G.小结）“契约式”章节骨架；统一去除字母前缀，并将“核心结论”转换为 summary 提示框，同时将 BOOKIFY 的实验入口提炼为章首提示框（更接近书籍阅读体验）；新增批处理脚本 `scripts/rewrite-docs-book-style.py`。
- `docs/book-style.md`：补齐“正文二次书籍化（第二层）”规范：统一叙事节奏（导读→主线推进→关键分支/坑点→证据链→小结与下一章承接），并给出最小模板片段与常见反例修复方式。
- 全模块 docs：重排 `<module>/docs/README.md` 为书籍化目录页（第一屏给出主线时间线/导读入口；README 的 Markdown 链接清单作为 teaching coverage 的章节 SSOT）。
- `spring-core-beans`：试点合并 Part 01 的前两章（原 02 章合并进 01 章），保留 `02-bean-registration.md` 作为 redirect 入口。
- `springboot-web-mvc`：试点合并 Internals 章节（原 02 章合并进 01 章），保留 `02-argument-resolver-and-binder.md` 作为 redirect 入口。
- `springboot-*` 与 `spring-core-*`（除 `spring-core-beans`/`springboot-web-mvc`）：深挖对齐（对标 `spring-core-beans`），补齐各模块 Guide 机制主线（导航图）、章节可断言坑点/边界与断点入口，并同步更新 `helloagents/wiki/modules/*.md`。
- 根 `README.md`：跨模块学习路线入口统一指向 `<module>/docs/README.md`（Docs TOC）。
- `spring-core-*`：对齐教学化文档规范：清理 docs 正文中的 `docs/NN` 缩写引用，统一替换为可解析的 Markdown 相对链接；统一章节末尾 `### 对应 Lab/Test` 入口块；并通过断链检查与教学覆盖检查。
- `scripts/check-md-relative-links.py`：支持传入 docs 目录或单个 .md 文件进行模块级自检；默认扫描所有 `spring-core-*/docs` 与 `springboot-*/docs`。
- `spring-core-beans`：将源码分组目录命名语义化（`part00/part01/...` → `part00_guide/part01_ioc_container/...`），提升与 docs Part（具名章节域）的对齐程度。
- `spring-core-beans`：源码与测试按 docs Part 结构分组：将 `src/main/java` 与 `src/test/java` 从平铺改为 `part00_guide/part01_ioc_container/part02_boot_autoconfig/part03_container_internals/part04_wiring_and_boundaries/appendix` 分包，并新增跨 Part 复用的 `testsupport`；同时全量修复 docs/README/知识库中的源码路径引用；保留 `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 包名不变。
- `spring-core-beans`：docs 书本化（Bookify）：引入 `docs/README.md` 目录页与 Part 结构，对 docs 章节进行移动/重命名/重新分组；整理章节结构为 A–G（定位/结论/主线/源码/实验/坑点/小结预告）并补齐“上一章｜目录｜下一章”导航；同时全局修复 docs 内链与模块 README 的入口链接。
- `spring-core-beans`：补齐模块 README 学习路线（Start Here/入门→进阶→深挖/refresh 主线一页纸/运行态观察点），新增“注入歧义最小复现”Lab + 对应 Exercise，并增强 `BeansDemoRunner` 的 `BEANS:` 结构化输出；同时同步根 README 与 progress 打卡入口。
- `spring-core-beans`：把“新增面试点”按主题嵌入到 docs/01、03、05、16、31、33 的正文对应小节，并补齐可断言复现入口（新增 BeanFactory vs ApplicationContext / Aware 基础设施 / 泛型匹配坑 Labs；扩展 proxy 章节增加 CGLIB vs JDK 对照与 BPP 定位闭环）。
- `spring-core-beans`：在 docs/01、02、03、05、09 增补 Spring 源码解析（refresh 主线/ConfigurationClassPostProcessor 注册入口/`doResolveDependency` 候选收敛/`doCreateBean` 生命周期/三级缓存循环依赖），并补充最小仓库 src 代码片段辅助理解。
- `spring-core-beans`：继续深化 docs/06 与 docs/12（PostProcessors/容器启动基础设施）：补齐 `PostProcessorRegistrationDelegate` 两段算法的源码级解释，并新增“static @Bean BFPP”最小可运行 Lab 用于解释早实例化与顺序陷阱。
- `spring-core-beans`：把 docs/14（Ordering）与 docs/25（programmatic BPP 注册）补成“算法级 + 可复现”版本：补齐分段执行/排序器规则/internal BPP 重新注册，以及 `addBeanPostProcessor` 的 list 语义与“BPP 不会 retroactive”的时机陷阱；同时增强 ordering Lab 覆盖 order 数值与 @Order 反例。
- `spring-core-beans`：移除误添加的 `docs/00-first-pass-learning-loop.md` 并清理引用；同时把可执行的“First Pass（10 个最小实验入口）”融入 `docs/00-deep-dive-guide.md` 与 `docs/99-self-check.md`，避免额外文件带来的重复编号与噪声。
- `spring-core-beans`：深化 Boot 自动装配章节（docs/10）：补齐 `matchIfMissing` 三态语义与自动配置顺序依赖（after/before）最小复现 Lab，并在 docs/11 与 README 的索引表同步入口。
- `spring-core-beans`：深化 Boot 自动装配 Exercises：新增 matchIfMissing 三态与 `@ConditionalOnBean` 顺序/时机差异的练习题，并提供“条件报告可查询”的 helper 练习，确保所有面试点落位到 docs/10 对应正文小节。
- `spring-core-beans`：补齐 auto-config back-off/覆盖排障闭环：新增 early/late registrar 对照 Lab，解释“为什么写了覆盖 Bean 但没退让”的真实原因，并把入口落位到 docs/10 的“覆盖”正文小节。
- `spring-core-beans`：补齐 Boot 自动装配主线与排障“可观察性”：新增 import/排序主线 Lab、BeanDefinition 来源追踪 Dumper，以及覆盖/back-off 场景矩阵 Lab；同步 docs/10 与模块 README 入口。
- `spring-core-beans`：提升 docs/01-03 连贯性：为连续教程主线补齐“本章定位/主线 vs 深挖/下一章预告”桥接段，统一阅读结构但不删减知识点。
- 深化 `spring-core-beans` 核心章节：扩写 docs/03、05、06、07、08、09、16、17、23、29、31，并扩展 docs/90（常见坑）与 docs/99（自测题），补齐源码级决策树/断点闭环/排障速查与 Lab 映射。
- `spring-core-beans`：新增 docs/36（类型转换链路：BeanWrapper/ConversionService）与 docs/37（泛型匹配注入坑：ResolvableType/代理/实例注册），新增 appendix/91（术语表）与 appendix/92（知识点跳读地图），并新增 component-scan/profile/optional injection/type conversion Labs 形成文档→实验闭环；同时修复 docs/README.md 排版并补齐快速定位与章节↔Lab 对照表。
- `spring-core-beans`：统一 docs 全章“上一章｜目录｜下一章”导航与“复现入口（可运行）”块，新增 JSR-330 `@Inject`/`Provider<T>` 对照 Lab（补齐 `jakarta.inject-api` 测试依赖），并增强 testsupport dumper 输出（候选集合/依赖边/来源定位）以提升排障可观察性。
- `spring-core-beans`：补齐 3 类易翻车边界机制：编程式注册差异（定义层 vs 实例层）、raw injection despite wrapping（`allowRawInjectionDespiteWrapping` 一致性保护）、prototype 销毁语义（默认不销毁 + `destroyBean` 手动销毁）；并在 docs/04、docs/05、docs/16、docs/25 增补复现入口与断点锚点。
- `spring-core-beans`：新增 Part 05（AOT/RuntimeHints/XML/容器外对象/SpEL/自定义 Qualifier）与对应 Labs，并新增 Appendix（面试复述模板 / 生产排障清单），同步更新 docs/README、模块 README、知识点地图与术语表索引。
- `spring-core-beans`：为 Exercises 补齐对应 Solution（默认参与回归），并在 docs/README 增加“章节↔Lab↔Exercise↔Solution”对照表与运行建议；同时补强 docs/02（ImportSelector 新手闭环/源码主线/断点观察点）、docs/04（练习↔答案指引）与 Part05 的 42–45（补齐“源码/断点建议”与观察点）。
- `spring-core-beans`：补齐 Spring Framework `spring-beans` 包 5 组机制闭环：XML namespace 扩展（`spring.handlers/schemas` + `NamespaceHandler/Parser`）、Properties/Groovy `BeanDefinitionReader`、`replaced-method` 方法注入、内置 FactoryBean（`MethodInvokingFactoryBean`/`ServiceLocatorFactoryBean`/`&beanName`）、以及 PropertyEditor 与 `BeanDefinitionValueResolver` 值解析主线；新增 docs 46–50 与对应 Labs（默认参与回归），并引入 test scope `org.apache.groovy:groovy:4.0.21` 以开箱运行 Groovy Reader Lab。
- `spring-core-beans`：补齐 Spring Framework `BeanFactory API` 与 `Environment Abstraction` 的系统化深挖闭环：新增 docs 38–39（主线/边界/误区 + 断点入口/观察点）与对应 Labs（默认参与回归），并同步更新 docs/README 与知识点地图索引入口。
- `spring-core-beans`：面向 Spring Framework `spring-beans` 模块的 Public API 全覆盖：新增 Appendix（`95` Public API 索引 + `96` Gap 清单）用于“按类型检索/可审计”，并补齐 `META-INF/spring/aot.factories`/`AotServices` 与 `ServiceLoader*FactoryBean` 机制的 docs+Labs；新增 `97` Explore/Debug 用例与显式开关（不影响默认回归）。
- `spring-core-beans`：补齐 `org.springframework.beans.support` support 工具类闭环（`ArgumentConvertingMethodInvoker`/`ResourceEditorRegistrar`/`PropertyComparator`/`PagedListHolder`/`SortDefinition`）：扩写 docs/36 并新增可运行 Lab；同步更新 Appendix 95/96 生成规则并重新生成，使 Gap 清单归零（0 partial）。
- `scripts`：新增 Markdown 相对链接存在性检查脚本（用于文档 0 断链自检）。
- 深化 `spring-core-aop` 核心章节：新增 docs/00（深挖指南）与 docs/99（自测题），扩写 docs/01-06、docs/90（常见坑），补齐源码断点入口/观察点与排障闭环。
- 二次深化 `spring-core-aop`：新增 docs/07-09（AutoProxyCreator 主线 / pointcut 表达式系统 / 多代理叠加与顺序），并新增 4 组 Labs 覆盖 BPP 主线、proceed 嵌套、this vs target、以及多 advisor vs 套娃 proxy 的可断言闭环；同时在 `spring-core-beans` 的 BPP/代理/顺序章节补齐 AutoProxyCreator 承接与跨模块链接。
- 三次深化 `spring-core-aop`：新增 docs/10（真实叠加 Debug Playbook）与集成 Lab（Tx/Cache/Method Security），把“多代理叠加”落到真实基础设施断点与可断言语义，并更新 README/深挖指南/多代理章节导航。
- 新增 `spring-core-aop-weaving`：AspectJ weaving 深挖模块（LTW/CTW），覆盖 `call`/`execution`、constructor、field `get/set`、`withincode`/`cflow` 等 join point 与表达式，并提供 docs + Labs/Exercises 可验证闭环。
- `spring-core-aop`/`spring-core-events`/`spring-core-tx`：补齐 DemoRunner 结构化输出（`AOP:`/`EVENTS:`/`TX:` 前缀），在 events 增加可控的异常传播演示（特定输入触发 throwing listener）；`spring-core-tx` 新增“自调用绕过事务”Lab，并同步 README/根 README/progress 的入口索引。
- `spring-core-aop`/`spring-core-events`/`spring-core-profiles`/`spring-core-resources`/`spring-core-tx`/`spring-core-validation`：对齐 docs Part 目录结构与 src/main+src/test 分包结构（语义化 Part 命名），并同步修复 README/文档中的源码路径与跨模块引用；保持各模块 `*Application` 入口包名不变。
- `springboot-*`：将 Part 结构（docs + src/test + src/main 最小分组）推广到全部 `springboot-*` 模块：新增 `docs/README.md` 与 `part-00-guide/`、迁移 docs 章节到 `part-01-*/` 与 `appendix/`；tests 按 `part00_guide`（Exercises）/ `part01_*`（Labs）分包；在不修改各模块 `*Application` 入口包名的前提下，将示例代码迁移到 `part01_*`（`springboot-business-case` 例外：为保留领域分层，仅对 tests 与 docs 对齐）；同时修复各模块 README 与 docs 内的源码路径引用。
- `springboot-*`：补齐教学化“可跑入口闭环”：将 `docs/README.md` 的章节清单统一为 Markdown 链接（SSOT），并补齐 `part-00-guide/00-deep-dive-guide.md` 与 `appendix/90/99` 的“对应 Lab/Exercise（可运行）”入口块；对缺口模块补齐 `min-labs=2`（新增 5 个 `*LabTest.java`）。

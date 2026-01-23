# Spring Boot Learning Workspace（知识库概览）

> 本目录是 HelloAGENTS 的 SSOT（知识层面的单一事实来源）。代码是运行时行为的事实来源；当文档与代码冲突时，以代码为准并同步修订知识库。

## 1. 项目概览

### 目标

- 用一组可运行的最小示例与可验证的测试实验，系统学习 Spring Boot / Spring Core 的关键概念。

### 范围

- **包含：** 多模块学习工程（每个 module 对应一个主题），配套 docs 与 Labs/Exercises（测试）。
- **不包含：** 生产级业务系统、统一对外 API 规范、统一数据模型规范。

## 2. 模块索引

| 模块 | 主题 | 状态 | 文档 |
| --- | --- | --- | --- |
| `springboot-basics` | 基础入门与工程习惯 | 🚧 | [modules/springboot-basics.md](modules/springboot-basics.md) |
| `springboot-web-mvc` | Web MVC 与请求处理 | 🚧 | [modules/springboot-web-mvc.md](modules/springboot-web-mvc.md) |
| `springboot-data-jpa` | JPA 与数据访问 | 🚧 | [modules/springboot-data-jpa.md](modules/springboot-data-jpa.md) |
| `springboot-actuator` | Actuator 与可观测性 | 🚧 | [modules/springboot-actuator.md](modules/springboot-actuator.md) |
| `springboot-testing` | 测试策略与工具 | 🚧 | [modules/springboot-testing.md](modules/springboot-testing.md) |
| `springboot-business-case` | 业务案例串联 | 🚧 | [modules/springboot-business-case.md](modules/springboot-business-case.md) |
| `springboot-security` | Spring Security | 🚧 | [modules/springboot-security.md](modules/springboot-security.md) |
| `springboot-web-client` | WebClient/HTTP 客户端 | 🚧 | [modules/springboot-web-client.md](modules/springboot-web-client.md) |
| `springboot-async-scheduling` | 异步与调度 | 🚧 | [modules/springboot-async-scheduling.md](modules/springboot-async-scheduling.md) |
| `springboot-cache` | 缓存 | 🚧 | [modules/springboot-cache.md](modules/springboot-cache.md) |
| `springboot-autoconfiguration` | Auto-Configuration（imports/条件/backoff） | 🚧 | [modules/springboot-autoconfiguration.md](modules/springboot-autoconfiguration.md) |
| `springboot-logging` | LoggingSystem/日志级别/输出捕获 | 🚧 | [modules/springboot-logging.md](modules/springboot-logging.md) |
| `springboot-observability` | Metrics/Observability（HTTP 指标） | 🚧 | [modules/springboot-observability.md](modules/springboot-observability.md) |
| `spring-core-beans` | IoC 容器与 Bean | 🚧 | [modules/spring-core-beans.md](modules/spring-core-beans.md) |
| `spring-core-aop` | AOP 与代理 | 🚧 | [modules/spring-core-aop.md](modules/spring-core-aop.md) |
| `spring-core-aop-weaving` | AspectJ weaving（LTW/CTW） | 🚧 | [modules/spring-core-aop-weaving.md](modules/spring-core-aop-weaving.md) |
| `spring-core-events` | 应用事件 | 🚧 | [modules/spring-core-events.md](modules/spring-core-events.md) |
| `spring-core-validation` | 校验（Validation） | 🚧 | [modules/spring-core-validation.md](modules/spring-core-validation.md) |
| `spring-core-resources` | 资源抽象与加载 | 🚧 | [modules/spring-core-resources.md](modules/spring-core-resources.md) |
| `spring-core-tx` | 事务 | 🚧 | [modules/spring-core-tx.md](modules/spring-core-tx.md) |
| `spring-core-profiles` | Profiles/Environment | 🚧 | [modules/spring-core-profiles.md](modules/spring-core-profiles.md) |
| `spring-core-spel` | SpEL（表达式解析/执行/边界） | 🚧 | [modules/spring-core-spel.md](modules/spring-core-spel.md) |

## 3. 快速链接

- [学习路线图（主线 / 机制线）](learning-path.md)
- [性能与并发（可复现实验范式）](../../docs/book/performance-and-concurrency.md)
- [项目技术约定](../project.md)
- [架构说明](arch.md)
- [API 说明](api.md)
- [数据模型说明](data.md)
- [变更历史索引](../history/index.md)

## 4. 文档站点（MkDocs）

> 目标：基于仓库根 `docs/` 构建可搜索的静态站点；并遵循“文档即目录”，用一份 Markdown 目录文件统一维护站点导航。

- 站点目录（SSOT）：`docs/SUMMARY.md`（目录本身就是文档，按其顺序/层级展示）
- 主题索引页（可发现性）：`docs/topics/index.md`
- Book（主线之书）：`docs/book/index.md`（目录与阅读说明）
- 站点配置：`docs-site/mkdocs.yml`（`docs_dir: ../docs`）
- 本地预览：`bash scripts/docs-site-serve.sh`
- 构建：`bash scripts/docs-site-build.sh`
- 文档门禁（自检）：`bash scripts/check-docs.sh`
- GitHub Pages 发布：workflow `.github/workflows/docs-site-pages.yml`（push 到 main/master 或 `workflow_dispatch`）

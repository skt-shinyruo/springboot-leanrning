# 文档中心（统一入口）

本仓库的所有文档已统一迁移到仓库根目录的 `docs/` 目录下，并按主题分组（每个主题下仍保留原模块的章节结构，便于溯源与维护）。

## 主题索引

<!-- BEGIN AUTO TOPICS INDEX -->
### Spring Boot（应用层）

- 基础入门：[`docs/basics/`](basics/)
- Web MVC：[`docs/web-mvc/`](web-mvc/)
- Security：[`docs/security/`](security/)
- Data JPA：[`docs/data-jpa/`](data-jpa/)
- Cache：[`docs/cache/`](cache/)
- Async & Scheduling：[`docs/async-scheduling/`](async-scheduling/)
- Actuator：[`docs/actuator/`](actuator/)
- Web Client：[`docs/web-client/`](web-client/)
- Testing：[`docs/testing/`](testing/)
- Business Case：[`docs/business-case/`](business-case/)

### Spring Core（底层机制）

- IoC / Bean：[`docs/beans/`](beans/)
- AOP（含 weaving）：[`docs/aop/`](aop/)
- 事务（Tx）：[`docs/tx/`](tx/)
- Events：[`docs/events/`](events/)
- Resources：[`docs/resources/`](resources/)
- Profiles：[`docs/profiles/`](profiles/)
- Validation：[`docs/validation/`](validation/)

### Book（主线之书）

- Book：[`docs/book/`](book/)

<!-- END AUTO TOPICS INDEX -->

## 说明

- **单一源文档（SSOT）**：模块目录下不再保留 `docs/`，避免双写与不一致。
- `docs-site/` 仅作为站点构建/发布的载体（如保留），文档源以 `docs/` 为准。

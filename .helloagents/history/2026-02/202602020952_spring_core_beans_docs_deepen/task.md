# Task List: spring-core-beans 文档逐章深度完善（机制 / 源码 / 排障）

Directory: `helloagents/plan/202602020952_spring_core_beans_docs_deepen/`

---

## 1. 草案确认（你审核后再落盘）
- [√] 1.1 已确认执行方式：先给逐章评审笔记（交付方式 B），verify why.md#requirement-逐章深度完善机制--源码--排障

## 2. 导航与策略（先升级“送读者到下一步动作”的层）
- [√] 2.1 深化模块入口 README：补版本标注与官方 reference 入口（保持索引定位），edit `spring-core-modules/spring-core-beans/README.md`, verify why.md#requirement-官方文档对齐版本标注
- [√] 2.2 深化 Docs TOC：补官方链接入口（版本语境对齐），edit `spring-core-modules/spring-core-beans/docs/README.md`, verify why.md#requirement-结构优化与跨章跳转允许重排

## 3. deepening-strategies（作为继续加深 SSOT，对齐 AE-DEEPENING）
- [√] 3.1 升级策略索引与使用方式：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`, verify why.md#requirement-结构优化与跨章跳转允许重排
- [√] 3.2 升级 module-readme 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/module-readme.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.3 升级 docs-root 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/docs-root.md`, verify why.md#requirement-结构优化与跨章跳转允许重排
- [√] 3.4 升级 Part 00 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-00-guide.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.5 升级 Part 01 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-01-ioc-container.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.6 升级 Part 02 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-02-boot-autoconfig.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.7 升级 Part 03 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-03-container-internals.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.8 升级 Part 04 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-04-wiring-and-boundaries.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.9 升级 Part 05 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-05-aot-and-real-world.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 3.10 升级 Appendix 策略：增加官方文档对照入口（版本语境），edit `spring-core-modules/spring-core-beans/docs/deepening-strategies/appendix.md`, verify why.md#requirement-结构优化与跨章跳转允许重排

## 4. 正文落盘（按章差异化补强）

> 说明：本步骤以“逐章阅读 → 就地补强”的方式执行；允许重排小节，但避免引入固定模板。
> 为控制风险，按 Part 分批推进，每批完成后做一次 mkdocs build 校验。

### Part 00（Guide）
- [√] 4.1 为 Why Index 增加官方文档对照（版本语境），edit `spring-core-modules/spring-core-beans/docs/part-00-guide/01-why-index.md`，verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 4.2 为 Part-00 全章补齐官方文档对照（版本语境），edit `spring-core-modules/spring-core-beans/docs/part-00-guide/*.md`, verify why.md#requirement-逐章深度完善机制--源码--排障

### Part 01（IoC Container）
- [√] 4.3 为 Part-01 全章补齐官方文档对照（版本语境），edit `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/*.md`, verify why.md#requirement-逐章深度完善机制--源码--排障
- [√] 4.4 为 Part-01 重点章补齐更精准的官方文档入口（注解/JavaConfig/scopes/extension），edit `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/*.md`, verify why.md#requirement-逐章深度完善机制--源码--排障

## 5. Security Check
- [√] 5.1 文档安全性检查：本次仅新增官方文档链接与版本语境说明，未引入危险命令/不安全建议，verify why.md#requirement-逐章深度完善机制--源码--排障

## 6. Testing / Build
- [X] 6.1 构建文档站点（需要 Python 依赖）：环境缺少 `python3-venv/ensurepip`，无法创建 venv 安装 mkdocs；如需继续请补齐系统包后重试
- [√] 6.2 运行模块测试：`mvn -q -pl :spring-core-beans test`（已通过）

## 7. Knowledge Base Sync
- [√] 7.1 同步更新模块知识库：`helloagents/wiki/modules/spring-core-beans.md`（状态/入口/变更记录），verify why.md#requirement-结构优化与跨章跳转允许重排
- [√] 7.2 更新 `helloagents/CHANGELOG.md` / `helloagents/history/index.md`，并迁移 solution package 至 `helloagents/history/2026-02/202602020952_spring_core_beans_docs_deepen/`

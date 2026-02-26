# Task List：全站书籍化 Stage 3（Book-only + 教学体验增强）

Directory: `helloagents/plan/202601152248_docs-book-stage3-all-modules/`

---

## 1. Book-only 站点导航与书目录骨架（SSOT 切换）
- [√] 1.1 新增 Book 目录骨架：`docs/book/index.md`（主线之书首页与阅读说明），verify why.md#核心场景
- [√] 1.2 MkDocs 导航切换为 Book-only：更新 `docs-site/mkdocs.yml`（侧边栏仅展示 book），verify why.md#全站以书为唯一目录book-only
- [√] 1.3 调整同步脚本：更新 `scripts/docs-site-sync.py`（生成 Book nav 注入，模块 nav 不再作为主入口），verify why.md#全站以书为唯一目录book-only

## 2. 主线之书：跨模块时间线章节树（覆盖 18 模块）
- [√] 2.1 书的 Start Here：`docs/book/00-start-here.md`（如何运行 Labs / 如何读主线 / 如何用搜索），verify why.md#教学体验增强abcd
- [√] 2.2 Boot 启动与配置主线：`docs/book/01-boot-basics-mainline.md`（承接 springboot-basics），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.3 IoC 容器主线：`docs/book/02-ioc-container-mainline.md`（承接 spring-core-beans），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.4 AOP/代理主线：`docs/book/03-aop-proxy-mainline.md`（承接 spring-core-aop），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.5 织入主线（LTW/CTW）：`docs/book/04-aop-weaving-mainline.md`（承接 spring-core-aop-weaving），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.6 事务主线：`docs/book/05-tx-mainline.md`（承接 spring-core-tx），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.7 Web MVC 请求主线：`docs/book/06-webmvc-mainline.md`（承接 springboot-web-mvc），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.8 Security 主线：`docs/book/07-security-mainline.md`（承接 springboot-security），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.9 Data JPA 主线：`docs/book/08-data-jpa-mainline.md`（承接 springboot-data-jpa），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.10 Cache 主线：`docs/book/09-cache-mainline.md`（承接 springboot-cache），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.11 Async/Scheduling 主线：`docs/book/10-async-scheduling-mainline.md`（承接 springboot-async-scheduling），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.12 Events 主线：`docs/book/11-events-mainline.md`（承接 spring-core-events），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.13 Resources 主线：`docs/book/12-resources-mainline.md`（承接 spring-core-resources），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.14 Profiles 主线：`docs/book/13-profiles-mainline.md`（承接 spring-core-profiles），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.15 Validation 主线：`docs/book/14-validation-mainline.md`（承接 spring-core-validation），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.16 Actuator/Observability 主线：`docs/book/15-actuator-observability-mainline.md`（承接 springboot-actuator），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.17 Web Client 主线：`docs/book/16-web-client-mainline.md`（承接 springboot-web-client），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.18 Testing 主线：`docs/book/17-testing-mainline.md`（承接 springboot-testing），verify why.md#覆盖-18-模块并形成主线时间线
- [√] 2.19 Business Case 收束：`docs/book/18-business-case.md`（承接 springboot-business-case），verify why.md#覆盖-18-模块并形成主线时间线

## 3. 教学体验增强（A/B/C/D）
- [√] 3.1 Labs 索引页（A）：新增 `docs/book/labs-index.md`（按模块/主题/命令索引），verify why.md#教学体验增强abcd
- [√] 3.3 Debugger Pack 索引页（B）：新增 `docs/book/debugger-pack.md`（入口断点/观察点/关键分支索引），verify why.md#教学体验增强abcd
- [√] 3.4 Exercises/Solutions 指南（C）：新增 `docs/book/exercises-and-solutions.md`（启用方式/运行方式/不破坏 CI），verify why.md#教学体验增强abcd
- [√] 3.5 站点阅读体验（D）：优化 `docs/index.md`（首页更像“书的封面/目录入口”），verify why.md#全站以书为唯一目录book-only

## 4. 迁移与重排（允许改名/移动，分阶段推进）
- [√] 4.1 定义迁移规则页：新增 `docs/book/migration-rules.md`（何时移动/何时 redirect/何时允许断链），verify why.md#允许合并拆章与改名
- [√] 4.2 试点：选择 1 个模块做“物理迁移”样板（例如 Beans 或 Web MVC），把 1–3 章迁移到 book 并更新引用，verify why.md#允许合并拆章与改名

## 5. Security Check
- [√] 5.1 安全自检：确认不引入 secrets/token；确认脚本不包含高风险命令（rm -rf / prod 连接等）

## 7. Knowledge Base Update
- [√] 7.1 更新知识库：`helloagents/wiki/overview.md`（补充“主线之书”的阅读方式与入口）
- [√] 7.2 更新变更记录：`helloagents/CHANGELOG.md`

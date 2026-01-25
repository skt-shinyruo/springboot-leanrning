# Task List：全量文档书籍化改写（章节学习卡片：五问闭环）

Directory: `helloagents/plan/202601161747_docs_chapter_cards_rollout/`

---

## 0. 范围与口径（先定“什么算一章”）

- [√] 0.1 定义“章节集合 SSOT”
  - 模块章节：以 18 个模块 `docs/README.md` 内的 Markdown 链接清单为 SSOT（共 174 章）
  - Book-only：以 `docs/book/` 为 SSOT（编号章 0–18 + 工具页/附录页）
- [√] 0.2 约定“工具页/索引页”也强制五问（允许 `源码入口：N/A（本页为索引/工具页）` / `推荐 Lab：N/A`）

---

## 1. 模板与写作 SSOT（把规则写进书的写作指南）

- [√] 1.1 更新 `docs/book-style.md`：新增“章节学习卡片（五问）”规范
- [√] 1.2 定义学习卡片字段最小标准（每章必须包含）
  - 知识点（1 句话边界）
  - 怎么用（最常见用法 + 场景）
  - 原理（主线 + 关键分支）
  - 源码入口（关键类/关键方法/建议断点）
  - 推荐 Lab（至少 1 个）

---

- [√] 2.1 新增“章节清单生成”脚本（从 docs/README SSOT 生成 checklist）
  - 输出：方案包内的 `chapters.md` 或 `chapters.csv`（用于进度追踪）
- [√] 2.2 新增“学习卡片完整性检查”脚本
  - 规则：SSOT 章节集合内，每章都必须包含五问字段（可允许 N/A）
- [√] 2.4 新增“学习卡片 upsert”脚本：为全量章节批量 upsert 章节学习卡片（可重复执行）

---

## 3. 书（Book-only）优先：先把读者主入口打磨好

- [√] 3.1 Book-only：为 0–18 编号章补齐“学习卡片（五问）”
- [√] 3.2 Book-only：为工具页/附录页补齐“本页用途/怎么用/原理/源码/Lab（可 N/A）”
  - 例：Labs 索引 / Debugger Pack / Exercises & Solutions / 迁移规则 / 目录页

---

## 4. 模块 docs 全量推进（按批次）

> 章节数统计（docs/README.md 链接数）：共 174 章  

### Batch A：主线地基（配置 → 容器 → AOP → 事务）

- [√] 4.1 `springboot-basics`（6 章）：为每章补齐五问学习卡片 + 推荐 Lab
- [√] 4.2 `spring-core-beans`（17 章）：为每章补齐五问学习卡片 + 源码入口（主线类/关键算法入口）
- [√] 4.3 `spring-core-aop`（14 章）：为每章补齐五问学习卡片 + 代理链/AutoProxy 源码入口
- [√] 4.4 `spring-core-tx`（10 章）：为每章补齐五问学习卡片 + 事务拦截器/传播/回滚源码入口

### Batch B：请求入口（Web MVC）与安全边界

- [√] 4.5 `springboot-web-mvc`（21 章）：为每章补齐五问学习卡片 + DispatcherServlet/Resolver 链源码入口
- [√] 4.6 `springboot-security`（9 章）：为每章补齐五问学习卡片 + FilterChain/认证授权源码入口

### Batch C：数据与性能（JPA/Cache）+ 并发与解耦（Async/Events）

- [√] 4.7 `springboot-data-jpa`（11 章）：为每章补齐五问学习卡片 + PC/flush/fetching 源码入口
- [√] 4.8 `springboot-cache`（9 章）：为每章补齐五问学习卡片 + cache interceptor/key 源码入口
- [√] 4.9 `springboot-async-scheduling`（9 章）：为每章补齐五问学习卡片 + executor/scheduler 源码入口
- [√] 4.10 `spring-core-events`（11 章）：为每章补齐五问学习卡片 + multicaster/事务事件源码入口

### Batch D：基础设施边界（Resources/Profiles/Validation）

- [√] 4.11 `spring-core-resources`（10 章）：为每章补齐五问学习卡片 + jar/classpath 边界源码入口
- [√] 4.12 `spring-core-profiles`（5 章）：为每章补齐五问学习卡片 + profile/conditional 源码入口
- [√] 4.13 `spring-core-validation`（10 章）：为每章补齐五问学习卡片 + 方法校验代理源码入口

### Batch E：可观测与对外调用（Actuator/Web Client）+ 测试方法论

- [√] 4.14 `springboot-actuator`（5 章）：为每章补齐五问学习卡片 + endpoint/exposure 源码入口
- [√] 4.15 `springboot-web-client`（9 章）：为每章补齐五问学习卡片 + filter/timeout/retry 源码入口
- [√] 4.16 `springboot-testing`（5 章）：为每章补齐五问学习卡片 + slice/full context 源码入口

### Batch F：能力上限与收束（Weaving/Business Case）

- [√] 4.17 `spring-core-aop-weaving`（8 章）：为每章补齐五问学习卡片 + LTW/CTW/join point 源码入口
- [√] 4.18 `springboot-business-case`（5 章）：为每章补齐五问学习卡片 + 端到端链路“定位点”与推荐 Lab

---

## 5. 验证与发布

---

## 6. 知识库同步与归档

- [√] 6.1 更新 `helloagents/CHANGELOG.md`（记录本次全量文档改写方案与规则变更）
- [√] 6.2 执行完成后：迁移方案包到 `helloagents/history/YYYY-MM/`
- [√] 6.3 更新 `helloagents/history/index.md`（登记与可追溯）

# Task List: 深化主线叙事的“可复用排障套路”（Beans 决策表 + Web MVC error/async 证据链）

Directory: `helloagents/plan/202601182033_beans_branch_decision_table_webmvc_error_async_deepen/`

---

## 1. spring-core-beans（主线章：分支决策表）
- [√] 1.1 在 `docs/beans/spring-core-modules/spring-core-beans/part-03-container-internals/07-refresh-to-bean-creation-mainline.md` 新增“分支决策表”小节：现象 → 阶段 → 关键方法 → 必看变量 → LabTest（至少覆盖：BDRPP/BFPP、BPP 顺序、FactoryBean、预实例化、early reference/循环依赖、raw injection despite wrapping、生命周期回调顺序）

## 2. springboot-web-mvc（主链路章：异常→Boot error、async 时间线）
- [√] 2.1 在 `docs/web-mvc/spring-boot-modules/springboot-web-mvc/part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md` 补齐“FilterChain → DispatcherServlet → ExceptionResolvers → Spring Boot error”的连续叙事，重点解释“异常未被 resolver 处理时”如何进入 Boot error（/error、ErrorAttributes、error view/json）
- [√] 2.2 在同一章补齐 async “两次 dispatch”时间线（REQUEST vs ASYNC），并给出可断言证据链（优先引用 `BootWebMvcTraceLabTest`，必要时补充跨章引用）

## 3. Security Check
- [√] 3.1 安全自检（G9）：确认无敏感信息写入、无危险脚本/命令、无生产环境操作

## 4. Documentation / Knowledge Base Sync
- [√] 4.1 同步更新知识库模块页：
  - `helloagents/wiki/modules/spring-core-beans.md`：补充分支决策表入口与“排障速查”定位
  - `helloagents/wiki/modules/springboot-web-mvc.md`：补充 error/async 主线补齐入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md`（Unreleased：补充本次文档深化点）

## 5. Testing
- [√] 5.1 运行 `mvn -q -pl spring-core-beans test`
- [√] 5.2 运行 `mvn -q -pl springboot-web-mvc test`

## 6. Migration
- [√] 6.1 更新 task 状态并迁移方案包到 `helloagents/history/2026-01/202601182033_beans_branch_decision_table_webmvc_error_async_deepen/`
- [√] 6.2 更新 `helloagents/history/index.md`

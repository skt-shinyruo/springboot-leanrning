# Task List: 全模块文档书本化升级（统一章节契约与导航）

Directory: `helloagents/plan/202601062218_all_modules_docs_bookify/`

---

## 0. 基线确认（只读）
- [√] 0.1 统计所有模块章节数、现有导航/入口块覆盖率（以 `docs/README.md` 为 SSOT），verify why.md#requirement-all-modules-docs-bookify

## 1. 书本化脚本（批处理）
- [√] 1.1 新增/实现 `scripts/bookify-docs.py`：解析模块 `docs/README.md`，对章节进行“入口块 + 导航”upsert，verify why.md#requirement-all-modules-docs-bookify
- [√] 1.2 运行脚本全量执行（覆盖 18 个模块的全部章节），verify why.md#requirement-all-modules-docs-bookify

## 2. 全模块章节契约落地（按模块验收）

> 说明：以下每个模块都执行“bookify + 模块级自检”，确保章节可连续阅读、入口可定位。

### 2.1 spring-core-beans
- [√] 2.1.1 bookify：统一章节尾部入口块与导航行（以 docs/README 为顺序），verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.1.2 模块自检：`python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-beans`，verify why.md#scenario-chapter-contract-and-navigation

### 2.2 spring-core-aop
- [√] 2.2.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.2.2 模块自检：`python3 scripts/check-md-relative-links.py docs/aop/spring-core-aop` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-aop`，verify why.md#scenario-chapter-contract-and-navigation

### 2.3 spring-core-aop-weaving
- [√] 2.3.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.3.2 模块自检：`python3 scripts/check-md-relative-links.py docs/aop/spring-core-aop-weaving` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-aop-weaving`，verify why.md#scenario-chapter-contract-and-navigation

### 2.4 spring-core-events
- [√] 2.4.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.4.2 模块自检：`python3 scripts/check-md-relative-links.py docs/events/spring-core-events` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-events`，verify why.md#scenario-chapter-contract-and-navigation

### 2.5 spring-core-validation
- [√] 2.5.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.5.2 模块自检：`python3 scripts/check-md-relative-links.py docs/validation/spring-core-validation` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-validation`，verify why.md#scenario-chapter-contract-and-navigation

### 2.6 spring-core-resources
- [√] 2.6.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.6.2 模块自检：`python3 scripts/check-md-relative-links.py docs/resources/spring-core-resources` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-resources`，verify why.md#scenario-chapter-contract-and-navigation

### 2.7 spring-core-tx
- [√] 2.7.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.7.2 模块自检：`python3 scripts/check-md-relative-links.py docs/tx/spring-core-tx` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-tx`，verify why.md#scenario-chapter-contract-and-navigation

### 2.8 spring-core-profiles
- [√] 2.8.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.8.2 模块自检：`python3 scripts/check-md-relative-links.py docs/profiles/spring-core-profiles` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-profiles`，verify why.md#scenario-chapter-contract-and-navigation

### 2.9 springboot-basics
- [√] 2.9.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.9.2 模块自检：`python3 scripts/check-md-relative-links.py docs/basics/springboot-basics` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-basics`，verify why.md#scenario-chapter-contract-and-navigation

### 2.10 springboot-web-mvc
- [√] 2.10.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.10.2 模块自检：`python3 scripts/check-md-relative-links.py docs/web-mvc/springboot-web-mvc` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-web-mvc`，verify why.md#scenario-chapter-contract-and-navigation

### 2.11 springboot-data-jpa
- [√] 2.11.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.11.2 模块自检：`python3 scripts/check-md-relative-links.py docs/data-jpa/springboot-data-jpa` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-data-jpa`，verify why.md#scenario-chapter-contract-and-navigation

### 2.12 springboot-actuator
- [√] 2.12.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.12.2 模块自检：`python3 scripts/check-md-relative-links.py docs/actuator/springboot-actuator` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-actuator`，verify why.md#scenario-chapter-contract-and-navigation

### 2.13 springboot-testing
- [√] 2.13.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.13.2 模块自检：`python3 scripts/check-md-relative-links.py docs/testing/springboot-testing` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-testing`，verify why.md#scenario-chapter-contract-and-navigation

### 2.14 springboot-business-case
- [√] 2.14.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.14.2 模块自检：`python3 scripts/check-md-relative-links.py docs/business-case/springboot-business-case` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-business-case`，verify why.md#scenario-chapter-contract-and-navigation

### 2.15 springboot-security
- [√] 2.15.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.15.2 模块自检：`python3 scripts/check-md-relative-links.py docs/security/springboot-security` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-security`，verify why.md#scenario-chapter-contract-and-navigation

### 2.16 springboot-web-client
- [√] 2.16.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.16.2 模块自检：`python3 scripts/check-md-relative-links.py docs/web-client/springboot-web-client` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-web-client`，verify why.md#scenario-chapter-contract-and-navigation

### 2.17 springboot-async-scheduling
- [√] 2.17.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.17.2 模块自检：`python3 scripts/check-md-relative-links.py docs/async-scheduling/springboot-async-scheduling` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-async-scheduling`，verify why.md#scenario-chapter-contract-and-navigation

### 2.18 springboot-cache
- [√] 2.18.1 bookify：统一章节尾部入口块与导航行，verify why.md#scenario-chapter-contract-and-navigation
- [√] 2.18.2 模块自检：`python3 scripts/check-md-relative-links.py docs/cache/springboot-cache` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module springboot-cache`，verify why.md#scenario-chapter-contract-and-navigation

## 3. Security Check
- [√] 3.1 执行安全自检（G9）：无生产环境操作、无密钥明文、无破坏性命令；批处理脚本不引入第三方依赖，verify why.md#requirement-all-modules-docs-bookify

## 4. Knowledge Base Update（helloagents）
- [√] 4.1 更新 `helloagents/wiki/modules/*.md`：记录本次“全模块书本化”变更，verify why.md#requirement-all-modules-docs-bookify
- [√] 4.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`，verify why.md#requirement-all-modules-docs-bookify

## 5. Testing（最终验收）
- [√] 5.2 抽样回归：选择 3 个模块跑 `mvn -pl <module> test`（如 `spring-core-beans`/`springboot-web-mvc`/`springboot-security`），verify why.md#scenario-chapter-contract-and-navigation

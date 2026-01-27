# Task List: 进一步深化 Bean 注册入口（02-bean-registration）

Directory: `helloagents/plan/202601271944_spring_core_beans_bean_registration_callchain_and_playbook/`

---

## 1. Documentation

- [√] 1.1 增补“源码调用链到方法级”章节（scan/@Bean/@Import/programmatic），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`, verify `why.md#requirement-r1-bean-registration-callchain-method-level`
- [√] 1.2 增补“排障决策表（注册相关）”，in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`, verify `why.md#requirement-r2-bean-registration-troubleshooting-decision-table`
- [√] 1.3 增补“面试标准答案（可复述）”，in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`, verify `why.md#requirement-r3-bean-registration-interview-standard-answers`

## 2. Knowledge Base Sync（SSOT）

- [√] 2.1 同步知识库模块页：追加本次变更记录与入口链接，in `helloagents/wiki/modules/spring-core-beans.md`
- [√] 2.2 更新知识库变更日志：记录本次文档深化，in `helloagents/CHANGELOG.md`
- [√] 2.3 归档方案包与索引更新：迁移到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`

## 3. Security Check

- [√] 3.1 安全自检（G9）：不引入密钥/生产地址；不引入外部网络依赖

## 4. Testing

- [√] 4.1 跑最小回归：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansImportLabTest test`

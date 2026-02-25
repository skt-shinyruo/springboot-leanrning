# Task List: 深化 Bean 注册入口教程（02-bean-registration）

Directory: `helloagents/plan/202601271739_spring_core_beans_bean_registration_deepen/`

---

## 1. Documentation

- [√] 1.1 深化注册入口章节：补齐入口对照表/最短调用链/证据链/面试复述模板，in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/01-bean-registration.md`, verify why.md#requirement-r1-bean-registration-docs-deepen

## 2. Knowledge Base Sync（SSOT）

- [√] 2.1 同步知识库模块页：新增本次变更记录与入口链接，in `helloagents/wiki/modules/spring-core-beans.md`
- [√] 2.2 更新知识库变更日志：记录本次文档深化，in `helloagents/CHANGELOG.md`

## 3. Security Check

- [√] 3.1 安全自检（G9）：不引入密钥/生产地址；不引入外部网络依赖

## 4. Testing

- [√] 4.1 跑模块回归：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansComponentScanLabTest test`

# Task List: solutions_all_remaining_modules（剩余模块 Solutions 全量补齐）

Directory: `helloagents/plan/202601222155_solutions_all_remaining_modules/`

---

## 1. Spring Boot Modules（Solutions 全量补齐）

- [√] 1.1 `springboot-basics`：确认已存在 `BootBasicsExerciseSolutionTest`（对齐 Exercises，独立上下文），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-basics-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.2 `springboot-web-mvc`：确认已存在 `BootWebMvcExerciseSolutionTest`（对齐 Exercises，MockMvc/随机端口二选一，独立上下文），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-web-mvc-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.3 `springboot-data-jpa`：确认已存在 `BootDataJpaExerciseSolutionTest`（对齐 Exercises，数据层用内存库/切片测试），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-data-jpa-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.4 `springboot-actuator`：确认已存在 `BootActuatorExerciseSolutionTest`（对齐 Exercises，endpoint/metrics 断言），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-actuator-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.5 `springboot-testing`：确认已存在 `BootTestingExerciseSolutionTest`（对齐 Exercises，MockBean/切片/上下文加载边界），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-testing-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.6 `springboot-business-case`：确认已存在 `BootBusinessCaseExerciseSolutionTest`（对齐 Exercises，业务串联用本地/内存依赖），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-business-case-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.7 `springboot-security`：确认已存在 `BootSecurityExerciseSolutionTest`（对齐 Exercises，MockMvc + security test 断言 401/403/鉴权路径），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-security-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.8 `springboot-web-client`：确认已存在 `BootWebClientExerciseSolutionTest`（对齐 Exercises，使用 mock server / stub ExchangeFunction，避免外网），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-web-client-exercise-对应-solution-可运行且不影响主线-labs
- [√] 1.9 `springboot-cache`：确认已存在 `BootCacheExerciseSolutionTest`（对齐 Exercises，缓存命中/失效/代理边界可断言），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-springboot-cache-exercise-对应-solution-可运行且不影响主线-labs

## 2. Spring Core Modules（Solutions 全量补齐）

- [√] 2.1 `spring-core-aop`：确认已存在 `SpringCoreAopExerciseSolutionTest`（对齐 Exercises，代理/自调用/拦截链断言），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-aop-exercise-对应-solution-可运行且不影响主线-labs
- [√] 2.2 `spring-core-aop-weaving`：确认已存在 `SpringCoreAopWeavingExerciseSolutionTest`（对齐 Exercises，LTW/CTW 场景按仓库现有约束实现），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-aop-weaving-exercise-对应-solution-可运行且不影响主线-labs
- [√] 2.3 `spring-core-tx`：新增 `SpringCoreTxExerciseSolutionTest`（对齐 Exercises，事务边界/传播/回滚规则证据链），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-tx-exercise-对应-solution-可运行且不影响主线-labs
- [√] 2.4 `spring-core-validation`：新增 `SpringCoreValidationExerciseSolutionTest`（对齐 Exercises，Bean Validation/方法校验边界），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-validation-exercise-对应-solution-可运行且不影响主线-labs
- [√] 2.5 `spring-core-resources`：新增 `SpringCoreResourcesExerciseSolutionTest`（对齐 Exercises，Resource 加载/解析/边界），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-resources-exercise-对应-solution-可运行且不影响主线-labs
- [√] 2.6 `spring-core-profiles`：新增 `SpringCoreProfilesExerciseSolutionTest`（对齐 Exercises，profile 激活/属性覆盖），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-profiles-exercise-对应-solution-可运行且不影响主线-labs

## 3. spring-core-beans（补齐 ExerciseSolution 缺口）

- [√] 3.1 `spring-core-beans`：新增 `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest`（对齐 `SpringCoreBeansAutowireCandidateSelectionExerciseTest`），verify why.md#requirement-剩余模块具备-solutions-闭环-scenario-spring-core-beans-part04-exercisesolution-缺口补齐并保持默认回归

## 4. Docs（Solutions 可发现性）

- [√] 4.1 更新 Book 工具页：补齐 Solutions 索引与约定（`docs/book/exercises-and-solutions.md`），verify why.md#requirement-solutions-入口在文档中可发现-scenario-book-工具页提供-solutions-索引与约定
- [√] 4.2 Boot 模块目录页：补齐 Solution 可跑入口（basics/web-mvc/data-jpa/actuator/testing/business-case/security/web-client/cache），verify why.md#requirement-solutions-入口在文档中可发现-scenario-各模块目录页提供-solution-可跑入口
- [√] 4.3 Core 模块目录页：补齐 Solution 可跑入口（aop/aop-weaving/tx/validation/resources/profiles/beans），verify why.md#requirement-solutions-入口在文档中可发现-scenario-各模块目录页提供-solution-可跑入口
- [√] 4.4 更新并发与性能专题页索引：补齐本批次新增 Labs 入口（`docs/book/performance-and-concurrency.md`），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页

## 5. 并发与性能 Labs（全量推广：每模块至少 1 个）

- [√] 5.1 `springboot-basics`：新增并发/性能 Lab（并发读取 Environment / property resolution，断言一致性与无异常），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.2 `springboot-web-mvc`：新增并发/性能 Lab（并发请求 + latch 编排，断言线程边界/请求隔离），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.3 `springboot-data-jpa`：新增并发/性能 Lab（并发事务边界/隔离证据链：每线程独立事务/EntityManager），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.4 `springboot-actuator`：新增并发/性能 Lab（并发请求驱动 metrics 增量，断言计数变化），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.5 `springboot-testing`：新增并发/性能 Lab（TestContext 缓存/复用的可断言证据链，不用耗时阈值），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.6 `springboot-business-case`：新增并发/性能 Lab（并发请求/服务调用的边界：幂等/并发可见性/线程隔离之一，选择最可断言路径），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.7 `springboot-security`：新增并发/性能 Lab（并发请求下 SecurityContext 隔离，断言 principal 不串线），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.8 `springboot-web-client`：新增并发/性能 Lab（WebClient/RestClient 可复用：并发调用 stub ExchangeFunction，断言线程安全/请求隔离），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.9 `springboot-cache`：新增并发/性能 Lab（缓存 stampede/同步加载：并发下底层方法调用次数可断言），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.10 `spring-core-aop`：新增并发/性能 Lab（同一 proxy 并发调用：advice 线程隔离/无共享可变状态），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.11 `spring-core-aop-weaving`：新增并发/性能 Lab（weaving 场景并发调用：ThreadLocal/切面状态隔离的可断言证据链），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.12 `spring-core-tx`：新增并发/性能 Lab（事务上下文 ThreadLocal：不跨线程传播的可复现证据链），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.13 `spring-core-validation`：新增并发/性能 Lab（Validator 并发使用：线程安全与一致性断言），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.14 `spring-core-resources`：新增并发/性能 Lab（并发资源解析：PatternResolver 结果一致性/无异常），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.15 `spring-core-profiles`：新增并发/性能 Lab（并发 profile/property resolution：一致性与边界断言），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页
- [√] 5.16 `spring-core-beans`：新增并发/性能 Lab（并发依赖解析/候选选择的一致性证据链：避免耗时阈值），verify why.md#requirement-并发与性能可复现实验覆盖所有剩余模块-scenario-每个模块新增至少-1-个不-flaky-的并发与性能-lab-并接入-book-专题页

## 6. Security Check（强制）

- [√] 6.1 安全自检（G9）：无生产环境操作、无明文密钥/Token、无破坏性脚本命令；WebClient/HTTP 不访问外网

## 7. Verification（逐模块 checkpoint + 闸门回归）

- [√] 7.1 单模块回归：`mvn -q -pl :springboot-basics test`
- [√] 7.2 单模块回归：`mvn -q -pl :springboot-web-mvc test`
- [√] 7.3 单模块回归：`mvn -q -pl :springboot-data-jpa test`
- [√] 7.4 单模块回归：`mvn -q -pl :springboot-actuator test`
- [√] 7.5 单模块回归：`mvn -q -pl :springboot-testing test`
- [√] 7.6 单模块回归：`mvn -q -pl :springboot-business-case test`
- [√] 7.7 单模块回归：`mvn -q -pl :springboot-security test`
- [√] 7.8 单模块回归：`mvn -q -pl :springboot-web-client test`
- [√] 7.9 单模块回归：`mvn -q -pl :springboot-cache test`
- [√] 7.10 单模块回归：`mvn -q -pl :spring-core-aop test`
- [√] 7.11 单模块回归：`mvn -q -pl :spring-core-aop-weaving test`
- [√] 7.12 单模块回归：`mvn -q -pl :spring-core-tx test`
- [√] 7.13 单模块回归：`mvn -q -pl :spring-core-validation test`
- [√] 7.14 单模块回归：`mvn -q -pl :spring-core-resources test`
- [√] 7.15 单模块回归：`mvn -q -pl :spring-core-profiles test`
- [√] 7.16 单模块回归：`mvn -q -pl :spring-core-beans test`
- [√] 7.17 全仓回归：运行 `mvn -q test`
- [√] 7.18 更新 Labs 索引：运行 `python3 scripts/generate-book-labs-index.py`
- [√] 7.19 文档门禁：运行 `bash scripts/check-docs.sh`
- [√] 7.20 站点构建：运行 `bash scripts/docs-site-build.sh`

## 8. Knowledge Base Sync + Archive（强制）

- [√] 8.1 同步知识库（Boot）：更新相关 `helloagents/wiki/modules/springboot-*.md`（增加 Solution + 并发/性能 Lab 入口，更新 Last Updated）
- [√] 8.2 同步知识库（Core）：更新相关 `helloagents/wiki/modules/spring-core-*.md`（增加 Solution + 并发/性能 Lab 入口，更新 Last Updated）
- [√] 8.3 同步学习路线：更新 `helloagents/wiki/learning-path.md`（加入“Solutions 全量覆盖”的使用建议与入口）
- [√] 8.4 变更记录：更新 `helloagents/CHANGELOG.md`（记录本批次新增 Solutions/并发与性能 Labs/文档入口）
- [√] 8.5 迁移方案包：`helloagents/plan/202601222155_solutions_all_remaining_modules/` → `helloagents/history/2026-01/202601222155_solutions_all_remaining_modules/`
- [√] 8.6 更新 `helloagents/history/index.md` 索引记录（✅Completed）

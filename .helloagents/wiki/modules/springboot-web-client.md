# springboot-web-client

## Purpose

学习 HTTP Client：WebClient、重试、超时、错误处理与可测试性。

## Module Overview

- **Responsibility:** 用最小示例与测试覆盖 HTTP 调用的关键问题与最佳实践。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Specifications

### Requirement: WebClient 学习闭环
**Module:** springboot-web-client
覆盖请求构建、响应处理、错误与超时策略。

#### Scenario: 错误处理与超时策略可验证
- 通过测试稳定复现并断言行为

### Requirement: 深挖对齐（对标 spring-core-beans）
**Module:** springboot-web-client
把“请求构建/错误映射/超时与重试/可测试性”写成可排障的机制主线，并绑定默认 Lab 入口。

#### Scenario: Guide 主线可作为导航图
- Guide 已补齐：RestClient vs WebClient、错误处理、超时/重试边界与调试入口

#### Scenario: 章节坑点可回归
- 每章至少 1 个可断言坑点，并绑定默认 `*LabTest#method` 作为证据链

## Dependencies

- 与 Web MVC/基础模块弱耦合

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-web-client/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-web-client/docs/part-00-guide/02-deep-dive-guide.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-web-client/docs/part-00-guide/04-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-web-client/docs/part-00-guide/05-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-web-client/docs/appendix/01-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-web-client/docs/appendix/02-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBranchMatrixLabTest.java`
- **Labs:**
  - `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientRestClientLabTest.java`
  - `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java`
  - `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientFilterOrderLabTest.java`
- **Exercise:** `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseSolutionTest.java`
- **Lab（并发/性能：RestClient 并发请求隔离）：** `spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part02_perf_concurrency/BootWebClientRestClientConcurrencyLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；client/model/support 集中在 `com.learning.springboot.bootwebclient.part01_web_client`
- `src/test/java`：`part00_guide`（Exercises/Solutions）/ `part01_web_client`（Labs）/ `part02_perf_concurrency`（并发/性能 Labs）

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（RestClient 并发请求隔离）
- [202601091802_modules_depth_align_to_beans](../../history/2026-01/202601091802_modules_depth_align_to_beans/) - ✅ 已执行：对标 spring-core-beans 深挖升级（Guide 机制主线 + 每章可断言坑点 + 默认 Lab 关键分支覆盖校验）
- [202601092110_depth_align_v2_batch01_sec_jpa_events_client](../../history/2026-01/202601092110_depth_align_v2_batch01_sec_jpa_events_client/) - ✅ 已执行：batch01 深挖对齐 v2（新增 WebClient filter 顺序默认 Lab + mockwebserver 章节坑点补齐 + 自测入口补齐）
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用

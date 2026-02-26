# Task List: springboot-part-structure-sync

Directory: `helloagents/plan/202601041358_springboot-part-structure-sync/`

---

## 1. springboot-basics
- [√] 1.1 docs Part 化：新增 `docs/README.md`、建立 `part-00-guide/`、迁移 `docs/*.md` 到 `part-01-boot-basics/` 与 `appendix/`，并修复内部链接（verify why.md#requirement-docs-part-结构推广-scenario-阅读路径一致）
- [√] 1.2 tests 分包：将 `src/test/java/com/learning/springboot/bootbasics/*Test.java` 按 Part/topic 改包到 `part00_guide` / `part01_boot_basics` / `appendix`（verify why.md#requirement-源码与-tests-分组对齐-docs-scenario-复现入口可定位）
- [√] 1.3 src/main 最小分组：除 `BootBasicsApplication` 外，将用于演示/讲解的基础设施类迁移到 `part01_boot_basics`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 1.4 运行验证：`mvn -pl springboot-basics test`

## 2. springboot-web-mvc
- [√] 2.1 docs Part 化：新增 `docs/README.md`、迁移 `docs/*.md` 到 `part-01-web-mvc/` 与 `appendix/`，并修复源码路径引用（verify why.md#requirement-全量修复引用-scenario-链接不-404）
- [√] 2.2 src/test 分包：将 `BootWebMvc*Test` 按 Part/topic 改包（verify why.md#requirement-源码与-tests-分组对齐-docs-scenario-复现入口可定位）
- [√] 2.3 src/main 最小分组：除 `BootWebMvcApplication` 外，将用于 docs 讲解的 demo/support 类迁移到 `part01_web_mvc`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 2.4 运行验证：`mvn -pl springboot-web-mvc test`

## 3. springboot-data-jpa
- [√] 3.1 docs Part 化：新增 `docs/README.md`、迁移 `docs/*.md` 到 `part-01-data-jpa/` 与 `appendix/`
- [√] 3.2 tests 分包：将 `BootDataJpa*Test` 按 Part/topic 改包到 `part00_guide`/`part01_data_jpa`/`appendix`
- [√] 3.3 src/main 最小分组：除 `BootDataJpaApplication` 外，将 entity/repository/runner 迁移到 `part01_data_jpa`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 3.4 运行验证：`mvn -pl springboot-data-jpa test`

## 4. springboot-actuator
- [√] 4.1 新增 docs 最小骨架：创建 `docs/README.md`、`part-00-guide/00-deep-dive-guide.md`、`part-01-actuator/01-actuator-basics.md`、`appendix/90-common-pitfalls.md`（骨架）
- [√] 4.2 tests 分包：将 `BootActuator*Test` 按 Part/topic 改包
- [√] 4.3 src/main 最小分组：除 `BootActuatorApplication` 外，将 actuator demo/indicator 迁移到 `part01_actuator`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 4.4 运行验证：`mvn -pl springboot-actuator test`

## 5. springboot-testing
- [√] 5.1 新增 docs 最小骨架：创建 `docs/README.md`、`part-00-guide/00-deep-dive-guide.md`、`part-01-testing/01-slice-and-mocking.md`、`appendix/90-common-pitfalls.md`（骨架）
- [√] 5.2 tests 分包：将 `BootTesting*Test` 与 `GreetingController*Test` 按 Part/topic 改包
- [√] 5.3 src/main 最小分组：除 `BootTestingApplication` 外，将 controller/service 迁移到 `part01_testing`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 5.4 运行验证：`mvn -pl springboot-testing test`

## 6. springboot-business-case（保留领域分层）
- [√] 6.1 新增 docs 最小骨架：创建 `docs/README.md`、`part-00-guide/00-deep-dive-guide.md`、`part-01-business-case/01-architecture-and-flow.md`、`appendix/90-common-pitfalls.md`（骨架）
- [√] 6.2 tests Part 化：仅迁移 tests 到 `part00_guide`/`part01_business_case`/`appendix`，不移动 `src/main/java/.../app|api|domain|events|tracing`（verify why.md#requirement-business-case-保持领域分层-scenario-领域结构不被破坏）
- [√] 6.3 运行验证：`mvn -pl springboot-business-case test`

## 7. springboot-security
- [√] 7.1 docs Part 化：新增 `docs/README.md`、迁移 `docs/*.md` 到 `part-01-security/` 与 `appendix/`，并修复源码路径引用
- [√] 7.2 tests 分包：将 `BootSecurity*Test` 按 Part/topic 改包
- [√] 7.3 src/main 最小分组：除 `BootSecurityApplication` 外，将用于 docs 的 demo/support 类迁移到 `part01_security`
- [√] 7.4 运行验证：`mvn -pl springboot-security test`

## 8. springboot-web-client
- [√] 8.1 docs Part 化：新增 `docs/README.md`、迁移 `docs/*.md` 到 `part-01-web-client/` 与 `appendix/`
- [√] 8.2 tests 分包：将 `BootWebClient*Test` 按 Part/topic 改包
- [√] 8.3 src/main 最小分组：除 `BootWebClientApplication` 外，将 client/model/support 迁移到 `part01_web_client`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 8.4 运行验证：`mvn -pl springboot-web-client test`

## 9. springboot-async-scheduling
- [√] 9.1 docs Part 化：新增 `docs/README.md`、迁移 `docs/*.md` 到 `part-01-async-scheduling/` 与 `appendix/`
- [√] 9.2 tests 分包：将 `BootAsyncScheduling*Test` 按 Part/topic 改包
- [√] 9.3 src/main 最小分组：除 `BootAsyncSchedulingApplication` 外，将 async/scheduling demo/service 迁移到 `part01_async_scheduling`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 9.4 运行验证：`mvn -pl springboot-async-scheduling test`

## 10. springboot-cache
- [√] 10.1 docs Part 化：新增 `docs/README.md`、迁移 `docs/*.md` 到 `part-01-cache/` 与 `appendix/`
- [√] 10.2 tests 分包：将 `BootCache*Test` 按 Part/topic 改包
- [√] 10.3 src/main 最小分组：除 `BootCacheApplication` 外，将 cache config/service 迁移到 `part01_cache`（verify why.md#requirement-保持入口类包名不变-scenario-spring-boot-默认扫描不受影响）
- [√] 10.4 运行验证：`mvn -pl springboot-cache test`

## 11. Cross-module 引用修复与知识库同步
- [√] 11.1 全局引用审计与修复：根 `README.md`、各模块 `README.md`、docs 内 `src/(main|test)/java` 路径引用同步更新（verify why.md#requirement-全量修复引用-scenario-链接不-404）
- [√] 11.2 同步知识库：更新 `helloagents/wiki/modules/springboot-*.md`，追加本次变更记录到 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`

## 12. Security Check
- [√] 12.1 执行安全检查（G9）：确认无敏感信息、无生产环境操作、无权限/支付相关逻辑改动

## 13. Testing（聚合回归）
- [√] 13.1 `mvn -pl springboot-basics,springboot-web-mvc,springboot-data-jpa,springboot-actuator,springboot-testing,springboot-business-case,springboot-security,springboot-web-client,springboot-async-scheduling,springboot-cache test`

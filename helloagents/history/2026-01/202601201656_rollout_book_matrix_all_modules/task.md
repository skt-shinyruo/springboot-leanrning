# Task List: 推广 Book Matrix（关键分支矩阵入口）到全模块

Directory: `helloagents/plan/202601201656_rollout_book_matrix_all_modules/`

---

## 1. 测试聚合能力（JUnit Platform Suite）
- [√] 1.1 在根 `pom.xml` 增加 test scope 依赖：`org.junit.platform:junit-platform-suite-api` 与 `org.junit.platform:junit-platform-suite-engine`，用于 `@Suite` 发现与执行

## 2. spring-boot-modules：为每个模块新增 Book Matrix 入口
- [√] 2.1 新增 `BootBasicsBookMatrixLabTest`（聚合 `BootBasicsDefaultLabTest`/`BootBasicsDevLabTest`/`BootBasicsOverrideLabTest`）
- [√] 2.2 新增 `BootWebMvcBookMatrixLabTest`（聚合 `BootWebMvcInternalsLabTest`/`BootWebMvcErrorBranchMatrixLabTest`）
- [√] 2.3 新增 `BootDataJpaBookMatrixLabTest`（聚合 `BootDataJpaLabTest`/`BootDataJpaMergeAndDetachLabTest`/`BootDataJpaDebugSqlLabTest`）
- [√] 2.4 新增 `BootActuatorBookMatrixLabTest`（聚合 `BootActuatorLabTest`/`BootActuatorExposureOverrideLabTest`）
- [√] 2.5 新增 `BootTestingBookMatrixLabTest`（聚合 `GreetingControllerWebMvcLabTest`/`GreetingControllerSpringBootLabTest`/`BootTestingMockBeanLabTest`）
- [√] 2.6 新增 `BootBusinessCaseBookMatrixLabTest`（聚合 `BootBusinessCaseLabTest`/`BootBusinessCaseServiceLabTest`）
- [√] 2.7 新增 `BootSecurityBookMatrixLabTest`（聚合 `BootSecurityLabTest`/`BootSecurityMultiFilterChainOrderLabTest`/`BootSecurityDevProfileLabTest`）
- [√] 2.8 新增 `BootWebClientBookMatrixLabTest`（聚合 `BootWebClientRestClientLabTest`/`BootWebClientWebClientLabTest`/`BootWebClientWebClientFilterOrderLabTest`）
- [√] 2.9 新增 `BootAsyncSchedulingBookMatrixLabTest`（聚合 `BootAsyncSchedulingLabTest`/`BootAsyncSchedulingSchedulingLabTest`）
- [√] 2.10 新增 `BootCacheBookMatrixLabTest`（聚合 `BootCacheLabTest`/`BootCacheSpelKeyLabTest`）

## 3. spring-core-modules：为每个模块新增 Book Matrix 入口
- [√] 3.1 新增 `SpringCoreBeansBookMatrixLabTest`（聚合 `SpringCoreBeansLabTest`/`SpringCoreBeansContainerLabTest`/`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`）
- [√] 3.2 新增 `SpringCoreAopBookMatrixLabTest`（聚合 `SpringCoreAopLabTest`/`SpringCoreAopProxyMechanicsLabTest`/`SpringCoreAopMultiProxyStackingLabTest`）
- [√] 3.3 新增 `AspectjWeavingBookMatrixLabTest`（聚合 `AspectjLtwLabTest`/`AspectjCtwLabTest`）
- [√] 3.4 新增 `SpringCoreEventsBookMatrixLabTest`（聚合 `SpringCoreEventsLabTest`/`SpringCoreEventsAsyncMulticasterLabTest`/`SpringCoreEventsTransactionalEventLabTest`）
- [√] 3.5 新增 `SpringCoreProfilesBookMatrixLabTest`（聚合 `SpringCoreProfilesLabTest`/`SpringCoreProfilesProfilePrecedenceLabTest`）
- [√] 3.6 新增 `SpringCoreResourcesBookMatrixLabTest`（聚合 `SpringCoreResourcesLabTest`/`SpringCoreResourcesMechanicsLabTest`）
- [√] 3.7 新增 `SpringCoreTxBookMatrixLabTest`（聚合 `SpringCoreTxLabTest`/`SpringCoreTxPropagationMatrixLabTest`/`SpringCoreTxRollbackRulesLabTest`/`SpringCoreTxSelfInvocationPitfallLabTest`）
- [√] 3.8 新增 `SpringCoreValidationBookMatrixLabTest`（聚合 `SpringCoreValidationLabTest`/`SpringCoreValidationMechanicsLabTest`）

## 4. Book 入口绑定（推广到所有模块章节）
- [√] 4.1 在 `docs/book/*-mainline.md` 为每章新增“进阶：Book Matrix”命令（`mvn -q -pl :<artifactId> -Dtest=<BookMatrix> test`）

## 5. 知识库同步（统一入口）
- [√] 5.1 更新 `helloagents/wiki/modules/*.md`：补充对应模块的 `*BookMatrixLabTest` 作为统一进阶入口

## 6. Security Check
- [√] 6.1 安全自检（G9）：确认无敏感信息写入、无危险脚本/命令、无生产环境操作

## 7. Documentation / Index Regeneration
- [√] 7.1 重新生成 `docs/book/labs-index.md`：`python3 scripts/generate-book-labs-index.py`
- [√] 7.2 运行 docs gate：`bash scripts/check-docs.sh`
- [√] 7.3 运行 docs-site 构建：`bash scripts/docs-site-build.sh`

## 8. Testing
- [√] 8.1 全仓回归：`mvn -q test`

## 9. Knowledge Base & Changelog
- [√] 9.1 更新 `helloagents/CHANGELOG.md`（Unreleased）：记录“全模块 Book Matrix 入口推广”

## 10. Migration
- [√] 10.1 更新 task 状态并迁移方案包到 `helloagents/history/2026-01/202601201656_rollout_book_matrix_all_modules/`
- [√] 10.2 更新 `helloagents/history/index.md`


# Task List: Spring Core 系列模块教学化规范全量推广（对齐 spring-core-beans）

Directory: `helloagents/plan/202601061556_spring_core_modules_teaching_rollout/`

---

## 0. 执行前基线（先跑脚本，再改文档/代码）
- [√] 0.1 全量链接自检：`python3 scripts/check-md-relative-links.py`（当前基线：`[OK] Checked 129 markdown files, missing targets: 0`），verify why.md#requirement-docs-link-integrity
- [√] 0.2 全量教学覆盖自检：`python3 scripts/check-teaching-coverage.py --min-labs 2`（当前基线：仅 `docs/events/spring-core-events/part-02-async-and-transactional/07-transactional-event-listener.md` 缺可跑入口），verify why.md#requirement-chapter-lab-closure
  > Note: 执行前该缺口会导致全量 check fail；已在任务 5.2 修复，当前全量已通过。

## 1. 全局规范（书本化最小统一口径）
- [√] 1.1 目录页（SSOT）统一：每个模块 `spring-core-*/docs/README.md` 必须满足，verify why.md#requirement-docs-index-and-numbering
  - [√] 1.1.1 以 Part 分组列出所有章节（链接必须可解析）
  - [√] 1.1.2 章节编号与文件名一致（`NN-*.md`）且排序稳定
  - [√] 1.1.3 提供最小运行说明：如何跑本模块 Labs（例如 `mvn -pl <module> test` / `-Dtest=*LabTest`）
- [√] 1.2 章节末尾“可跑入口”统一：每个章节至少包含 1 个可跑入口引用（真实路径或 `*LabTest` 类名引用均可；推荐统一成 `### 对应 Lab/Test` 区块），verify why.md#requirement-chapter-lab-closure
- [√] 1.3 “docs/NN” 缩写统一策略：在 `spring-core-*/docs/**/*.md` 正文中不出现裸 `docs/NN`（一律替换成真实 Markdown 链接），verify why.md#requirement-docs-no-shorthand-references
- [√] 1.4 改动闭环：每次修改 docs 或 tests 后，必须复跑 `check-md-relative-links` 与 `check-teaching-coverage`，verify why.md#requirement-docs-link-integrity

## 2. spring-core-beans（基准模块：清理正文中的 `docs/NN` 缩写）
- [√] 2.1 缩写检索：`rg -n "\\bdocs/\\d{2}\\b" docs/beans/spring-core-beans -S`，收敛为 0 个命中，verify why.md#requirement-docs-no-shorthand-references
- [√] 2.2 按文件逐一替换（每处替换都要用“章节名 + 真实相对路径”的 Markdown 链接），verify why.md#requirement-docs-no-shorthand-references
  - [√] 2.2.1 `docs/beans/spring-core-beans/part-01-ioc-container/01-bean-mental-model.md`（涉及：`docs/03`、`docs/05`、`docs/09`、`docs/15`、`docs/16`、`docs/31`、`docs/35`）
  - [√] 2.2.2 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`（涉及：`docs/23`、`docs/29`、`docs/31`、`docs/32`）
  - [√] 2.2.3 `docs/beans/spring-core-beans/part-01-ioc-container/05-lifecycle-and-callbacks.md`（涉及：`docs/03`、`docs/06`、`docs/12`、`docs/17`、`docs/31`、`docs/34`）
  - [√] 2.2.4 `docs/beans/spring-core-beans/part-01-ioc-container/06-post-processors.md`（涉及：`docs/06`）
  - [√] 2.2.5 `docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`（涉及：`docs/06`）
  - [√] 2.2.6 `docs/beans/spring-core-beans/part-03-container-internals/16-early-reference-and-circular.md`（涉及：`docs/09`、`docs/31`）
  - [√] 2.2.7 `docs/beans/spring-core-beans/part-03-container-internals/17-lifecycle-callback-order.md`（涉及：`docs/14`、`docs/25`、`docs/31`）
  - [√] 2.2.8 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`（涉及：`docs/29`）
  - [√] 2.2.9 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`（涉及：`docs/05`）
  - [√] 2.2.10 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`（涉及：`docs/15`、`docs/16`）
  - [√] 2.2.11 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`（涉及：`docs/23`、`docs/29`）
  - [√] 2.2.12 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md`（涉及：`docs/12`、`docs/16`）
- [√] 2.3 模块验证：`python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans` 通过，verify why.md#requirement-docs-link-integrity
- [√] 2.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-beans` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 2.5 模块回归：`mvn -pl spring-core-beans test`

## 3. spring-core-aop（目录页核对 + 章节↔实验闭环“逐章确认”）
- [√] 3.1 目录页核对：`docs/aop/spring-core-aop/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 3.2 逐章确认/补齐“对应 Lab/Test（真实路径）”区块（若已存在则只校验路径真实），verify why.md#requirement-chapter-lab-closure
  - [√] 3.2.1 `docs/aop/spring-core-aop/part-00-guide/00-deep-dive-guide.md` → 入口建议：
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java`
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyCreatorInternalsLabTest.java`
  - [√] 3.2.2 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java`
  - [√] 3.2.3 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/02-jdk-vs-cglib.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyMechanicsLabTest.java`
  - [√] 3.2.4 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java`
  - [√] 3.2.5 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/04-final-and-proxy-limits.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyMechanicsLabTest.java`
  - [√] 3.2.6 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/05-expose-proxy.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java`
  - [√] 3.2.7 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/06-debugging.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopProceedNestingLabTest.java`
  - [√] 3.2.8 `docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/07-autoproxy-creator-mainline.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyCreatorInternalsLabTest.java`
  - [√] 3.2.9 `docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/08-pointcut-expression-system.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopPointcutExpressionsLabTest.java`
  - [√] 3.2.10 `docs/aop/spring-core-aop/part-03-proxy-stacking/09-multi-proxy-stacking.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopMultiProxyStackingLabTest.java`
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopProceedNestingLabTest.java`
  - [√] 3.2.11 `docs/aop/spring-core-aop/part-03-proxy-stacking/10-real-world-stacking-playbook.md` →
    - `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopRealWorldStackingLabTest.java`
  - [√] 3.2.12 `docs/aop/spring-core-aop/appendix/90-common-pitfalls.md` → 至少 1 个 `*LabTest.java`
  - [√] 3.2.13 `docs/aop/spring-core-aop/appendix/99-self-check.md` → 至少 1 个 `*LabTest.java`
- [√] 3.3 模块验证：`python3 scripts/check-md-relative-links.py docs/aop/spring-core-aop` 通过，verify why.md#requirement-docs-link-integrity
- [√] 3.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-aop` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 3.5 模块回归：`mvn -pl spring-core-aop test`

## 4. spring-core-aop-weaving（目录页核对 + 章节↔实验闭环“逐章确认”）
- [√] 4.1 目录页核对：`docs/aop/spring-core-aop-weaving/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 4.2 逐章确认/补齐“对应 Lab/Test（真实路径）”，verify why.md#requirement-chapter-lab-closure
  - [√] 4.2.1 `docs/aop/spring-core-aop-weaving/part-00-guide/00-deep-dive-guide.md` →
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java`
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java`
  - [√] 4.2.2 `docs/aop/spring-core-aop-weaving/part-01-mental-model/01-proxy-vs-weaving.md` →
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java`
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java`
  - [√] 4.2.3 `docs/aop/spring-core-aop-weaving/part-02-ltw/02-ltw-basics.md` →
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java`
  - [√] 4.2.4 `docs/aop/spring-core-aop-weaving/part-03-ctw/03-ctw-basics.md` →
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java`
  - [√] 4.2.5 `docs/aop/spring-core-aop-weaving/part-04-join-points/04-join-point-cookbook.md` →
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java`
    - `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java`
  - [√] 4.2.6 `docs/aop/spring-core-aop-weaving/appendix/90-common-pitfalls.md` → 至少 1 个 Lab 入口
  - [√] 4.2.7 `docs/aop/spring-core-aop-weaving/appendix/99-self-check.md` → 至少 1 个 Lab 入口
- [√] 4.3 模块验证：`python3 scripts/check-md-relative-links.py docs/aop/spring-core-aop-weaving` 通过，verify why.md#requirement-docs-link-integrity
- [√] 4.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-aop-weaving` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 4.5 模块回归：`mvn -pl spring-core-aop-weaving test`

## 5. spring-core-events（补齐 docs/07 的可跑入口 + 逐章确认）
- [√] 5.1 目录页核对：`docs/events/spring-core-events/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 5.2 修复缺口：`docs/events/spring-core-events/part-02-async-and-transactional/07-transactional-event-listener.md` 增加可跑入口（真实路径），verify why.md#requirement-chapter-lab-closure
  - [√] 5.2.1 新增/扩展 Lab：增加事务事件最小闭环（创建目录 `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/` 并新增 `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`）
  - [√] 5.2.2 覆盖至少 2 个断言场景：AFTER_COMMIT 触发、rollback 不触发（或 AFTER_ROLLBACK 触发）
  - [√] 5.2.3 在 docs/07 章末补齐 `### 对应 Lab/Test` 并引用新/扩展的 LabTest 文件路径
- [√] 5.3 逐章确认/补齐“对应 Lab/Test（真实路径）”，verify why.md#requirement-chapter-lab-closure
  > Note: 除 docs/07 外其余章节已满足“可跑入口可解析”要求（由脚本验证），无需额外改动。
  - [√] 5.3.1 `docs/events/spring-core-events/part-00-guide/00-deep-dive-guide.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`
  - [√] 5.3.2 `docs/events/spring-core-events/part-01-event-basics/01-event-mental-model.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`
  - [√] 5.3.3 `docs/events/spring-core-events/part-01-event-basics/02-multiple-listeners-and-order.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`
  - [√] 5.3.4 `docs/events/spring-core-events/part-01-event-basics/03-condition-and-payload.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`
  - [√] 5.3.5 `docs/events/spring-core-events/part-01-event-basics/04-sync-and-exceptions.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsMechanicsLabTest.java`
  - [√] 5.3.6 `docs/events/spring-core-events/part-02-async-and-transactional/05-async-listener.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsMechanicsLabTest.java`
  - [√] 5.3.7 `docs/events/spring-core-events/part-02-async-and-transactional/06-async-multicaster.md` →
    - `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsMechanicsLabTest.java`
  - [√] 5.3.8 `docs/events/spring-core-events/part-02-async-and-transactional/07-transactional-event-listener.md` → `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`（5.2 产出）
  - [√] 5.3.9 `docs/events/spring-core-events/appendix/90-common-pitfalls.md` → 至少 1 个 Lab 入口
  - [√] 5.3.10 `docs/events/spring-core-events/appendix/99-self-check.md` → 至少 1 个 Lab 入口
- [√] 5.4 模块验证：`python3 scripts/check-md-relative-links.py docs/events/spring-core-events` 通过，verify why.md#requirement-docs-link-integrity
- [√] 5.5 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-events` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 5.6 模块回归：`mvn -pl spring-core-events test`

## 6. spring-core-profiles（逐章确认：入口路径真实 + min-labs=2 已满足）
- [√] 6.1 目录页核对：`docs/profiles/spring-core-profiles/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 6.2 逐章确认/补齐“对应 Lab/Test（真实路径）”，verify why.md#requirement-chapter-lab-closure
  - [√] 6.2.1 `docs/profiles/spring-core-profiles/part-00-guide/00-deep-dive-guide.md` →
    - `spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesLabTest.java`
  - [√] 6.2.2 `docs/profiles/spring-core-profiles/part-01-profiles/01-profile-activation-and-bean-selection.md` →
    - `spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesLabTest.java`
    - `spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesProfilePrecedenceLabTest.java`
  - [√] 6.2.3 `docs/profiles/spring-core-profiles/appendix/90-common-pitfalls.md` → 至少 1 个 Lab 入口
  - [√] 6.2.4 `docs/profiles/spring-core-profiles/appendix/99-self-check.md` → 至少 1 个 Lab 入口
- [√] 6.3 模块验证：`python3 scripts/check-md-relative-links.py docs/profiles/spring-core-profiles` 通过，verify why.md#requirement-docs-link-integrity
- [√] 6.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-profiles` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 6.5 模块回归：`mvn -pl spring-core-profiles test`

## 7. spring-core-resources（逐章确认：入口路径真实 + min-labs=2 已满足）
- [√] 7.1 目录页核对：`docs/resources/spring-core-resources/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 7.2 逐章确认/补齐“对应 Lab/Test（真实路径）”，verify why.md#requirement-chapter-lab-closure
  - [√] 7.2.1 `docs/resources/spring-core-resources/part-00-guide/00-deep-dive-guide.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java`
  - [√] 7.2.2 `docs/resources/spring-core-resources/part-01-resource-abstraction/01-resource-abstraction.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java`
  - [√] 7.2.3 `docs/resources/spring-core-resources/part-01-resource-abstraction/02-classpath-locations.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java`
  - [√] 7.2.4 `docs/resources/spring-core-resources/part-01-resource-abstraction/03-classpath-star-and-pattern.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java`
  - [√] 7.2.5 `docs/resources/spring-core-resources/part-01-resource-abstraction/04-exists-and-handles.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java`
  - [√] 7.2.6 `docs/resources/spring-core-resources/part-01-resource-abstraction/05-reading-and-encoding.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java`
  - [√] 7.2.7 `docs/resources/spring-core-resources/part-01-resource-abstraction/06-jar-vs-filesystem.md` →
    - `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java`
  - [√] 7.2.8 `docs/resources/spring-core-resources/appendix/90-common-pitfalls.md` → 至少 1 个 Lab 入口
  - [√] 7.2.9 `docs/resources/spring-core-resources/appendix/99-self-check.md` → 至少 1 个 Lab 入口
- [√] 7.3 模块验证：`python3 scripts/check-md-relative-links.py docs/resources/spring-core-resources` 通过，verify why.md#requirement-docs-link-integrity
- [√] 7.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-resources` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 7.5 模块回归：`mvn -pl spring-core-resources test`

## 8. spring-core-tx（逐章确认：入口路径真实 + min-labs=2 已满足）
- [√] 8.1 目录页核对：`docs/tx/spring-core-tx/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 8.2 逐章确认/补齐“对应 Lab/Test（真实路径）”，verify why.md#requirement-chapter-lab-closure
  - [√] 8.2.1 `docs/tx/spring-core-tx/part-00-guide/00-deep-dive-guide.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.2 `docs/tx/spring-core-tx/part-01-transaction-basics/01-transaction-boundary.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.3 `docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.4 `docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.5 `docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.6 `docs/tx/spring-core-tx/part-02-template-and-debugging/05-transaction-template.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.7 `docs/tx/spring-core-tx/part-02-template-and-debugging/06-debugging.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
  - [√] 8.2.8 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` →
    - `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxSelfInvocationPitfallLabTest.java`
  - [√] 8.2.9 `docs/tx/spring-core-tx/appendix/99-self-check.md` → 至少 1 个 Lab 入口
- [√] 8.3 模块验证：`python3 scripts/check-md-relative-links.py docs/tx/spring-core-tx` 通过，verify why.md#requirement-docs-link-integrity
- [√] 8.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-tx` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 8.5 模块回归：`mvn -pl spring-core-tx test`

## 9. spring-core-validation（逐章确认：入口路径真实 + min-labs=2 已满足）
- [√] 9.1 目录页核对：`docs/validation/spring-core-validation/README.md` 覆盖 docs 目录下全部章节文件（除 README 本身），verify why.md#requirement-docs-index-and-numbering
- [√] 9.2 逐章确认/补齐“对应 Lab/Test（真实路径）”，verify why.md#requirement-chapter-lab-closure
  - [√] 9.2.1 `docs/validation/spring-core-validation/part-00-guide/00-deep-dive-guide.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java`
  - [√] 9.2.2 `docs/validation/spring-core-validation/part-01-validation-core/01-constraint-mental-model.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java`
  - [√] 9.2.3 `docs/validation/spring-core-validation/part-01-validation-core/02-programmatic-validator.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java`
  - [√] 9.2.4 `docs/validation/spring-core-validation/part-01-validation-core/03-method-validation-proxy.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java`
  - [√] 9.2.5 `docs/validation/spring-core-validation/part-01-validation-core/04-groups.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java`
  - [√] 9.2.6 `docs/validation/spring-core-validation/part-01-validation-core/05-custom-constraint.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationLabTest.java`
  - [√] 9.2.7 `docs/validation/spring-core-validation/part-01-validation-core/06-debugging.md` →
    - `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java`
  - [√] 9.2.8 `docs/validation/spring-core-validation/appendix/90-common-pitfalls.md` → 至少 1 个 Lab 入口
  - [√] 9.2.9 `docs/validation/spring-core-validation/appendix/99-self-check.md` → 至少 1 个 Lab 入口
- [√] 9.3 模块验证：`python3 scripts/check-md-relative-links.py docs/validation/spring-core-validation` 通过，verify why.md#requirement-docs-link-integrity
- [√] 9.4 模块验证：`python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-validation` 通过，verify why.md#requirement-chapter-lab-closure
- [√] 9.5 模块回归：`mvn -pl spring-core-validation test`

## 10. 知识库同步（helloagents）
- [√] 10.1 更新 `helloagents/wiki/modules/spring-core-*.md` 的 Change History：记录本次推广变更（docs 规范化 / Labs 补齐 / 自检结果），verify why.md#requirement-knowledge-base-sync
- [√] 10.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`，verify why.md#requirement-knowledge-base-sync
- [√] 10.3 执行完成后：将方案包迁移到 `helloagents/history/YYYY-MM/`（并在 `history/index.md` 登记），verify why.md#requirement-knowledge-base-sync

## 11. Security Check
- [√] 11.1 执行安全自检（G9）：无生产环境操作、无密钥明文、无破坏性命令；跨模块引用不越界；新增测试不依赖外部服务

## 12. Testing（最终验收）
- [√] 12.1 全量：`python3 scripts/check-md-relative-links.py`
- [√] 12.2 全量：`python3 scripts/check-teaching-coverage.py --min-labs 2`
- [√] 12.3 分模块回归：`mvn -pl spring-core-aop test` … `spring-core-validation`

# 99 自检：Spring Boot Data JPA
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootDataJpaBookMatrixLabTest`
    - 分支入口：`BootDataJpaBranchMatrixLabTest`
    - 推荐先跑：`BootDataJpaLabTest` / `BootDataJpaMergeAndDetachLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（建议反复对照）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. `@DataJpaTest` 默认是否运行在事务里？如何把“事务存在”固定成断言？
   - 证据入口：`BootDataJpaLabTest#dataJpaTestRunsInsideATransaction`
2. Repository 的最小闭环是什么？（save → id → query）如何把它写成一条回归用例？
   - 证据入口：`BootDataJpaLabTest#savesAndFindsByTitle`
3. persistence context（一级缓存）意味着什么？`save` 之后实体是否一定是 managed？
   - 证据入口：`BootDataJpaLabTest#entityIsManagedAfterSaveInSamePersistenceContext`
4. `EntityManager#clear` 会带来什么效果？如何证明“同一个对象不再受管理”？
   - 证据入口：`BootDataJpaLabTest#entityManagerClearDetachesEntities`
5. dirty checking 是如何把“改对象”变成“发 SQL”的？如何把它固定成可回归事实？
   - 证据入口：`BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`
6. flush 与 commit 的关系是什么？如何证明“flush 后 JDBC 能看见插入行”（即使事务还没结束）？
   - 证据入口：`BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`
7. `getReferenceById` 返回的是什么？它什么时候触发真正的 SQL？
   - 证据入口：`BootDataJpaLabTest#getReferenceByIdReturnsALazyProxy_andInitializesOnPropertyAccess`
8. N+1 的根因是什么？如何用统计/断言证明它真的发生了，而不是“感觉很慢”？
   - 证据入口：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections`
9. 如何用 EntityGraph（或等价手段）避免 N+1，并证明“访问集合不会额外发 SQL”？
   - 证据入口：`BootDataJpaLabTest#entityGraphCanAvoidNPlusOne_whenFetchingCollections`
10. detach 与 merge 的边界是什么？如何用对照用例证明“detached 改动不会落库，但 merge 会把改动带回 managed copy”？
    - 证据入口：`BootDataJpaMergeAndDetachLabTest#detached_changesWithoutMerge_shouldNotBePersisted` + `BootDataJpaMergeAndDetachLabTest#merge_shouldPersistDetachedChangesIntoManagedCopy`

## 退出条件（完成标准）

- 能区分三条线并提供证据入口：persistence context（managed/detached）→ flush/commit → fetching（lazy/N+1）。
- 能把“感觉/猜测”替换成“统计/断言”：SQL 次数、是否 initialized、是否落库。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaDebugSqlLabTest` / `BootDataJpaLabTest` / `BootDataJpaMergeAndDetachLabTest`
- Exercise：`BootDataJpaExerciseTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->

# spring-boot-data-jpa

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”学习 Spring Data JPA 的入门要点。

这份 `README.md` 主要做索引与导航；更深入的解释请按章节阅读：见 [`docs/README.md`](docs/README.md)。

## Start Here（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 你将学到什么

- Entity / Repository 的最小闭环（保存、查询、删除）
- persistence context（managed / detached）与实体状态机
- flush 与 JDBC 可见性
- dirty checking（脏检查）
- fetching 与 N+1（通过 Exercises 引导你亲手复现）

## 前置知识

- 建议先完成 `spring-boot-basics`（至少能跑通项目、理解配置）
- 具备基本 SQL/事务概念（commit/rollback 的直觉即可）
- （可选）了解 `@Transactional` 的基本含义（后续可用 `spring-core-tx` 深入）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-data-jpa spring-boot:run
```

启动后会在控制台打印一段简单的“写入/查询”示例日志（示例数据写入 H2 内存库）。

### 测试

```bash
mvn -pl :spring-boot-data-jpa test
```

## 推荐 docs 阅读顺序（从现象到机制）

（docs 目录页：[`docs/README.md`](docs/README.md)）

1. [Entity 状态机：transient / managed / detached / removed](docs/part-01-data-jpa/01-entity-states.md)
2. [Persistence Context：一级缓存与事务绑定](docs/part-01-data-jpa/02-persistence-context.md)
3. [flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？](docs/part-01-data-jpa/03-flush-and-visibility.md)
4. [Dirty Checking：为什么改字段不用 save 也能落库？](docs/part-01-data-jpa/04-dirty-checking.md)
5. [Fetching 与 N+1：为什么查一次会变成查很多次？](docs/part-01-data-jpa/05-fetching-and-n-plus-one.md)
6. [`@DataJpaTest`：为什么它适合学 JPA（切片测试）](docs/part-01-data-jpa/06-datajpatest-slice.md)
7. [Debug/观察：怎么把 Hibernate 的 SQL 看清楚？](docs/part-01-data-jpa/07-debug-sql.md)
8. [常见坑清单（建议反复对照）](docs/appendix/01-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`，建议逐个开启。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java` | save/find 最小闭环 + persistence context + flush + dirty checking | ⭐⭐ | `docs/part-01/01` → `docs/part-01/04`（先现象后机制） |
| Exercise | `src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java` | fetching/N+1、关系映射、lazy reference、回滚行为等 | ⭐⭐–⭐⭐⭐ | `docs/part-01/05`、`docs/part-01/06`、`docs/appendix/90` |

## 概念 → 在本模块哪里能“看见”

| 你要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 你应该能解释清楚 |
| --- | --- | --- | --- |
| save/find 的最小闭环 | [docs/part-01/01](docs/part-01-data-jpa/01-entity-states.md) | `BootDataJpaLabTest#savesAndFindsByTitle` + `BookRepository` | Repository 方法如何映射成查询 |
| managed / detached 的差异 | [docs/part-01/02](docs/part-01-data-jpa/02-persistence-context.md) | `BootDataJpaLabTest#entityIsManagedAfterSaveInSamePersistenceContext` | `entityManager.contains(...)` 的语义 |
| flush 与 JDBC 可见性 | [docs/part-01/03](docs/part-01-data-jpa/03-flush-and-visibility.md) | `BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction` | flush 让 SQL 真正执行但不等于 commit |
| dirty checking 的真实行为 | [docs/part-01/04](docs/part-01-data-jpa/04-dirty-checking.md) | `BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush` | 为什么“改字段”能落库 |
| slice test 的价值 | [docs/part-01/06](docs/part-01-data-jpa/06-datajpatest-slice.md) | `BootDataJpaLabTest#dataJpaTestRunsInsideATransaction` | 为什么 `@DataJpaTest` 特别适合学 JPA |

## 常见 Debug 路径

- 学机制优先用 `@DataJpaTest`：上下文更小、反馈更快
- 避免“一级缓存假象”：必要时 `flush()` + `clear()` 再查
- 需要看 SQL 时：先用断言确定结论，再用 SQL 日志解释原因（见 [docs/part-01/07](docs/part-01-data-jpa/07-debug-sql.md)）

## 常见坑

- 把 persistence context 当成数据库状态（导致误判）
- flush ≠ commit（同一事务内可见，不代表对外可见）
- 懒加载与 N+1：访问方式决定 SQL 数量

## 参考

- Spring Data JPA
- Hibernate / JPA

# spring-boot-data-jpa

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”学习 Spring Data JPA 的入门要点。

这份 `README.md` 主要做索引与导航；更深入的解释请按章节阅读：见本 README 的「目录（唯一顺序来源）」。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

## 本模块的学习产出

- Entity / Repository 的最小闭环（保存、查询、删除）
- persistence context（managed / detached）与实体状态机
- flush 与 JDBC 可见性
- dirty checking（脏检查）
- fetching 与 N+1（通过 Exercises 完成可复现验证）

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

（目录：见本 README 的「目录（唯一顺序来源）」）

1. [Entity 状态机：transient / managed / detached / removed](docs/data-jpa-entity-states.md)
2. [Persistence Context：一级缓存与事务绑定](docs/data-jpa-persistence-context.md)
3. [flush：SQL 什么时候发出去？为什么 flush 后 JDBC 能查到？](docs/data-jpa-flush-and-visibility.md)
4. [Dirty Checking：为什么改字段不用 save 也能落库？](docs/data-jpa-dirty-checking.md)
5. [Fetching 与 N+1：为什么查一次会变成查很多次？](docs/data-jpa-fetching-and-n-plus-one.md)
6. [`@DataJpaTest`：为什么它适合学 JPA（切片测试）](docs/data-jpa-datajpatest-slice.md)
7. [Debug/观察：怎么把 Hibernate 的 SQL 看清楚？](docs/data-jpa-debug-sql.md)
8. [常见坑清单（建议反复对照）](docs/appendix-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`，建议逐个开启。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java` | save/find 最小闭环 + persistence context + flush + dirty checking | ⭐⭐ | `docs/part-01/01` → `docs/part-01/04`（先现象后机制） |
| Exercise | `src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java` | fetching/N+1、关系映射、lazy reference、回滚行为等 | ⭐⭐–⭐⭐⭐ | `docs/part-01/05`、`docs/part-01/06`、`docs/appendix/90` |

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| save/find 的最小闭环 | [docs/part-01/01](docs/data-jpa-entity-states.md) | `BootDataJpaLabTest#savesAndFindsByTitle` + `BookRepository` | Repository 方法如何映射成查询 |
| managed / detached 的差异 | [docs/part-01/02](docs/data-jpa-persistence-context.md) | `BootDataJpaLabTest#entityIsManagedAfterSaveInSamePersistenceContext` | `entityManager.contains(...)` 的语义 |
| flush 与 JDBC 可见性 | [docs/part-01/03](docs/data-jpa-flush-and-visibility.md) | `BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction` | flush 让 SQL 真正执行但不等于 commit |
| dirty checking 的真实行为 | [docs/part-01/04](docs/data-jpa-dirty-checking.md) | `BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush` | 为什么“改字段”能落库 |
| slice test 的价值 | [docs/part-01/06](docs/data-jpa-datajpatest-slice.md) | `BootDataJpaLabTest#dataJpaTestRunsInsideATransaction` | 为什么 `@DataJpaTest` 特别适合学 JPA |

## 常见 Debug 路径

- 学机制优先用 `@DataJpaTest`：上下文更小、反馈更快
- 避免“一级缓存假象”：必要时 `flush()` + `clear()` 再查
- 需要看 SQL 时：先用断言确定结论，再用 SQL 日志解释原因（见 [docs/part-01/07](docs/data-jpa-debug-sql.md)）

## 常见坑

- 把 persistence context 当成数据库状态（导致误判）
- flush ≠ commit（同一事务内可见，不代表对外可见）
- 懒加载与 N+1：访问方式决定 SQL 数量

## 参考

- Spring Data JPA
- Hibernate / JPA

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Data JPA：Persistence Context、flush 与查询边界

本模块以 JPA 的核心事实为中心组织内容：实体状态如何变化、持久化上下文如何影响可见性、`flush` 与脏检查在哪些时机发生，以及 fetching 策略如何演变成 N+1。很多行为只有在事务边界内才有意义，因此本模块与事务模块（`spring-core-tx`）是天然的串联关系。

---

### 10 分钟入口：固定持久化上下文的“可见性事实”
- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`

运行后应能回答：在同一事务/同一持久化上下文中，查询结果为何会“看起来被缓存”；何时 `flush` 会触发写入与可见性变化；哪些现象属于 ORM 的正常语义而不是“数据库不一致”。

### 从这里开始（先建立坐标）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线（按事实递进）
- [实体状态](docs/data-jpa-entity-states.md)
- [持久化上下文](docs/data-jpa-persistence-context.md)
- [flush 与可见性](docs/data-jpa-flush-and-visibility.md)
- [脏检查](docs/data-jpa-dirty-checking.md)
- [fetching 与 N+1](docs/data-jpa-fetching-and-n-plus-one.md)
- [@DataJpaTest](docs/data-jpa-datajpatest-slice.md)
- [SQL 调试](docs/data-jpa-debug-sql.md)

### 关联模块（按需串联）
- 事务边界与一致性：`spring-core-tx`

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-data-jpa -Dtest=*ExerciseSolutionTest test`
- 并发/性能（EntityManager/事务边界隔离）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaEntityManagerConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)

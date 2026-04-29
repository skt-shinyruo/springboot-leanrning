# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：Persistence Context 是一致性视图；flush/clear/merge 会改变“视图与 DB 的关系”。

    把 JPA 的关键边界（managed/detached、flush 可见性）收敛成“可复现 + 可观察”的矩阵表。

    对照入口：`BootDataJpaBranchMatrixLabTest`。需要下探源码时，可以从 `EntityManager` / `Session` / `SimpleJpaRepository` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Data JPA）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. Entity 状态机：transient / managed / detached / removed](data-jpa-entity-states.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

JPA 的大多数“怪现象”都能用两条线解释：

- **对象现在是什么状态（managed/detached）**
- **flush 什么时候发生（以及 flush 后谁能看到）**

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| managed 脏检查 | 事务内修改 managed entity | 提交/flush 后变化落库 | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaLabTest` | `entityManager.contains` / flush 时机 / SQL |
| clear → detach 边界 | 调用 `EntityManager#clear` | 后续修改不再自动落库 | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaLabTest` | `contains(entity)==false` |
| merge 语义 | detached entity 调用 `merge` | detached 变化被合并到 managed copy | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaMergeAndDetachLabTest` | merge 返回值引用是否变化 |
| SQL 证据链 | 打开 show-sql/format_sql | 可以直接看见“什么时候发 SQL” | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaDebugSqlLabTest` | 控制台 SQL 输出与断点对应 |

## 运行命令

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

## 调试路线

- 第 1 站：`EntityManager#clear` / `#merge` / `#flush`（状态机与 flush）
- 第 2 站：`SimpleJpaRepository#save`（Repository 到 EM 的桥）
- 第 3 站：回到断言处，确认“以为发生的分支”与“真实发生的分支”一致

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

Persistence Context 是一致性视图；flush/clear/merge 会改变“视图与 DB 的关系”。

下一章见：[01. Entity 状态机：transient / managed / detached / removed](data-jpa-entity-states.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootDataJpaBranchMatrixLabTest`
- Lab：`BootDataJpaMergeAndDetachLabTest` / `BootDataJpaDebugSqlLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[data-jpa-entity-states.md](data-jpa-entity-states.md)

<!-- BOOKIFY:END -->


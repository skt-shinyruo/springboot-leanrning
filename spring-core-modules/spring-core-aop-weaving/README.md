# spring-core-aop-weaving

本模块专注 **AspectJ Weaving（织入）**，用于补齐 `spring-core-aop`（Spring AOP / 代理）无法覆盖的边界：

- LTW（Load-Time Weaving）：通过 `-javaagent` 在类加载时织入
- CTW（Compile-Time Weaving）：通过 Maven 插件在编译期织入（离线织入）
- 更丰富的 join point：`call/execution/constructor/get/set/...`
- 更高级的 pointcut：`withincode/cflow/...`

> 验证路径：先完成 `spring-core-aop` 再来本模块。
> 代理 AOP 的第一性原理是“改调用链（必须走 proxy）”；weaving 的第一性原理是“改字节码（不依赖 proxy）”。

> ⚠️ 注意：由于 `aspectj-maven-plugin`（ajc）对 Java 版本的限制，本模块的编译目标设置为 `--release 16`；但运行时仍要求 JDK 17+（与父工程 enforcer 一致）。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- 能解释清楚：**Spring AOP（代理） vs AspectJ（织入）** 的能力边界与代价
- 能通过测试验证：LTW/CTW 都能命中“非代理 join point”
- 能在真实排障中判断：问题是“没走代理”还是“没织入/织入范围不对”

## 关键命令

### 测试（入口）

```bash
mvn -pl :spring-core-aop-weaving test
```

本模块的测试分成两套：

- `*Ltw*Test`：JVM 会以 `-javaagent:.../aspectjweaver.jar` 启动（由 surefire 自动配置）
- `*Ctw*Test`：JVM **不带** `-javaagent`（用于证明 CTW 不依赖 agent）

### 运行（可选）

```bash
mvn -pl :spring-core-aop-weaving spring-boot:run
```

> 注意：运行应用本身不会自动附带 `-javaagent`。本模块的核心结论以 Labs 为准。

## docs 阅读顺序

0. [深挖指南：如何跑通 LTW/CTW + 常见排障路径](docs/guide-deep-dive-guide.md)
1. [心智模型：Proxy vs Weaving（为什么 weaving 不依赖 call path）](docs/mental-model-proxy-vs-weaving.md)
2. [LTW：`-javaagent` + `META-INF/aop.xml`（最小闭环与边界）](docs/ltw-basics.md)
3. [CTW：编译期织入（无 agent 也能拦截）](docs/ctw-basics.md)
4. [Join Point & 表达式速查：call/execution/get/set/withincode/cflow](docs/join-points-join-point-cookbook.md)
5. [常见坑清单（排查时对照）](docs/appendix-common-pitfalls.md)
6. [自测题：是否真正理解了 weaving？](docs/appendix-self-check.md)

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 延伸阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java` | LTW：agent + aop.xml；call/execution；self-invocation；get/set；withincode/cflow | ⭐⭐⭐ | docs/00、02、04 |
| Lab | `src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java` | CTW：无 agent；同样覆盖 call/execution/get/set/withincode/cflow | ⭐⭐⭐ | docs/03、04 |
| Exercise | `src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java` | 改造：扩展一个 join point / 表达式并保持断言全绿 | ⭐⭐ | docs/04 |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：AOP Weaving（织入：LTW/CTW）：代理之外的另一条路

织入解决的是“代理做不到或不适合做”的那部分 AOP 需求：切点落在构造器、字段、final 方法等代理天然受限的位置；或希望以字节码层面的方式改变行为边界。本模块先把“代理 vs 织入”的边界跑清楚，再分别讨论 LTW（load-time weaving）与 CTW（compile-time weaving），最后用 join point 维度把落点与风险控制住。

---

### 10 分钟入口：先确认织入是否生效
- `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`

运行后应能回答：织入在何处介入；哪些 join point 能命中、哪些不能；与代理方案相比，行为边界与可观测性有什么变化。

### 从这里开始（顺读路径）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [代理 vs 织入](docs/mental-model-proxy-vs-weaving.md)
- [LTW 基础](docs/ltw-basics.md)
- [CTW 基础](docs/ctw-basics.md)
- [Join Point 菜谱](docs/join-points-join-point-cookbook.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- Branch Matrix（LTW/CTW）：直接运行模块测试（让 Surefire 自动区分 execution）：
  `mvn -q -pl :spring-core-aop-weaving test`
  或分别运行：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-core-aop-weaving -Dtest=*ExerciseSolutionTest test`
- 并发/性能（LTW 并发织入边界）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwConcurrencyLabTest test`

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)

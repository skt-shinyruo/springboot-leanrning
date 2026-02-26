# spring-core-aop-weaving

本模块专注 **AspectJ Weaving（织入）**，用于补齐 `spring-core-aop`（Spring AOP / 代理）无法覆盖的边界：

- LTW（Load-Time Weaving）：通过 `-javaagent` 在类加载时织入
- CTW（Compile-Time Weaving）：通过 Maven 插件在编译期织入（离线织入）
- 更丰富的 join point：`call/execution/constructor/get/set/...`
- 更高级的 pointcut：`withincode/cflow/...`

> 学习建议：先完成 `spring-core-aop` 再来本模块。  
> 代理 AOP 的第一性原理是“改调用链（必须走 proxy）”；weaving 的第一性原理是“改字节码（不依赖 proxy）”。

> ⚠️ 注意：由于 `aspectj-maven-plugin`（ajc）对 Java 版本的限制，本模块的编译目标设置为 `--release 16`；但运行时仍要求 JDK 17+（与父工程 enforcer 一致）。

## Start Here（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 你将学到什么

- 你能解释清楚：**Spring AOP（代理） vs AspectJ（织入）** 的能力边界与代价
- 你能通过测试验证：LTW/CTW 都能命中“非代理 join point”
- 你能在真实排障中判断：问题是“没走代理”还是“没织入/织入范围不对”

## 关键命令

### 测试（推荐入口）

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

## 推荐 docs 阅读顺序

0. [深挖指南：如何跑通 LTW/CTW + 常见排障路径](docs/part-00-guide/02-deep-dive-guide.md)
1. [心智模型：Proxy vs Weaving（为什么 weaving 不依赖 call path）](docs/part-01-mental-model/01-proxy-vs-weaving.md)
2. [LTW：`-javaagent` + `META-INF/aop.xml`（最小闭环与边界）](docs/part-02-ltw/01-ltw-basics.md)
3. [CTW：编译期织入（无 agent 也能拦截）](docs/part-03-ctw/01-ctw-basics.md)
4. [Join Point & 表达式速查：call/execution/get/set/withincode/cflow](docs/part-04-join-points/01-join-point-cookbook.md)
5. [常见坑清单（建议反复对照）](docs/appendix/01-common-pitfalls.md)
6. [自测题：你是否真的理解了 weaving？](docs/appendix/02-self-check.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java` | LTW：agent + aop.xml；call/execution；self-invocation；get/set；withincode/cflow | ⭐⭐⭐ | docs/00、02、04 |
| Lab | `src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwLabTest.java` | CTW：无 agent；同样覆盖 call/execution/get/set/withincode/cflow | ⭐⭐⭐ | docs/03、04 |
| Exercise | `src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java` | 改造：扩展一个 join point / 表达式并保持断言全绿 | ⭐⭐ | docs/04 |

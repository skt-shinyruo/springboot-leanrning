# spring-core-spel

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Expression Language（SpEL）**：

- 表达式解析与求值（parser / evaluation context）
- 变量、根对象、属性访问（`#root`、`#var`、property access）
- 为什么 SpEL 既强大也需要安全边界（尤其是在可控输入场景）

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

完成标准（应能解释清楚）：

- 一段表达式是如何被解析成 AST 并求值的（至少能定位到关键类/入口）
- 为什么 SpEL 常被用在 Cache key / Validation / Security 表达式里
- 如何把“表达式行为”固化成可回归断言（避免手动试错）

## 关键命令

### 测试

```bash
mvn -pl :spring-core-spel test
```

### 运行

```bash
mvn -pl :spring-core-spel spring-boot:run
```

## Labs / Exercises 索引

> Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 |
| --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelLabTest.java` | parser + evaluation context 的最小闭环 | ⭐⭐ |
| Exercise | `src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseTest.java` | 增加变量/函数并固化断言 | ⭐⭐–⭐⭐⭐ |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Core SpEL：解析、求值与边界

本模块以一条最短求值链路为主线，把 SpEL 的三个关键对象跑成事实：parser 如何把表达式解析成 AST，evaluation context 如何提供 root/variables/property access，最终 `getValue()` 在何处完成求值与类型转换。函数扩展与安全边界属于更复杂的分支，本模块先把基础链路固定下来，再进入扩展点。

---

### 10 分钟入口：先跑通 parse → evaluate
- `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`

运行后应能回答：表达式在何处被解析；属性访问与类型转换在何处发生；不同 evaluation context 下为何会产生不同的求值结果。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)
3. [SpEL 调用链（parse → AST → evaluate）](docs/guide-spel-call-chain.md)
4. [断点地图（排障优先）](docs/guide-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](docs/guide-branch-decision-matrix.md)

### 顺读主线
- [SpEL 入门：root/variables/property access](docs/spel-basics.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- 并发求值：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)

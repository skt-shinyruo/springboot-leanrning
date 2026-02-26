# spring-core-spel

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Expression Language（SpEL）**：

- 表达式解析与求值（parser / evaluation context）
- 变量、根对象、属性访问（`#root`、`#var`、property access）
- 为什么 SpEL 既强大也需要安全边界（尤其是在可控输入场景）

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](docs/README.md)。

## Start Here（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

完成标准（你应该能解释清楚）：

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

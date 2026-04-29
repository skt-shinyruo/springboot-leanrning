# spring-core-resources

本模块用“可运行的最小示例 + 可验证的测试实验（实验/练习）”学习 Spring 的 **`Resource` 抽象**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- 统一抽象：classpath / file / URL 资源读取
- classpath 位置写法（是否需要 `/`）
- pattern 扫描：`classpath*:` + 通配符
- `Resource` 的“句柄语义”：`getResource(...)` 返回 handle，`exists()` 才判断存在
- jar vs filesystem 的差异（通过 练习 引导观察）

## 前置知识

- 了解 classpath 的基本概念（资源跟随 jar/目录打包）
- 了解 Java IO 的最小概念（InputStream/Reader/编码）

## 关键命令

### 运行

```bash
mvn -pl :spring-core-resources spring-boot:run
```

运行后观察控制台输出：

- `classpath:data/hello.txt` 的内容
- 通过 `classpath*:data/*.txt` 找到的资源列表

### 测试

```bash
mvn -pl :spring-core-resources test
```

## docs 阅读顺序（从现象到机制）

1. [`Resource` 抽象：为什么 Spring 不直接使用 `File`？](docs/resource-abstraction.md)
2. [classpath 路径：`classpath:data/x` vs `classpath:/data/x`](docs/resource-abstraction-classpath-locations.md)
3. [`classpath*:` 与 pattern：为什么能扫到多个资源？](docs/resource-abstraction-classpath-star-and-pattern.md)
4. [`getResource(...)` 的返回值：为什么它会返回不存在资源句柄？](docs/resource-abstraction-exists-and-handles.md)
5. [读取资源：InputStream、编码与可观察性](docs/resource-abstraction-reading-and-encoding.md)
6. [jar vs filesystem：为什么 IDE OK，打包后不行？](docs/resource-abstraction-jar-vs-filesystem.md)
7. [常见坑清单（排查时对照）](docs/appendix-common-pitfalls.md)

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 延伸阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java` | classpath/file 读取、pattern 扫描、缺失资源错误 | ⭐⭐ | `docs/01` → `docs/03` |
| Lab | `src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java` | handle/exists、description、`classpath*:` 细节 | ⭐⭐ | `docs/04`、`docs/05` |
| Exercise | `src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java` | 新增资源/metadata/jar 差异/排序稳定等练习 | ⭐⭐–⭐⭐⭐ | `docs/06`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| classpath 资源读取 | [docs/02](docs/resource-abstraction-classpath-locations.md) | `SpringCoreResourcesLabTest#readsClasspathResourceContent` | `Resource` 如何读取 classpath 内容 |
| `classpath:` 是否需要 `/` | [docs/02](docs/resource-abstraction-classpath-locations.md) | `SpringCoreResourcesLabTest#supportsLeadingSlashInClasspathLocation` | 两种写法为什么都能工作 |
| `classpath*:` pattern 扫描 | [docs/03](docs/resource-abstraction-classpath-star-and-pattern.md) | `SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath` | 为什么能返回多个 Resource |
| handle 与 exists | [docs/04](docs/resource-abstraction-exists-and-handles.md) | `SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist` | 为什么 getResource 不返回 null |
| `getDescription()` 的 debug 价值 | [docs/05](docs/resource-abstraction-reading-and-encoding.md) | `SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging` | 为什么 description 比 path 更可靠 |

## 常见 Debug 路径

- 优先用 `Resource#getDescription()` 做 debug（比猜测 path 更可靠）
- pattern 扫描结果排序后断言，避免“顺序不稳定”学歪
- 遇到问题先问：是“资源不存在”（`exists=false`）还是“存在但读不了”（IO 错误）

## 常见坑

- 把 classpath 资源当 File：IDE 里 OK，打包后崩
- 以为 `getResource(...)` 不存在会返回 null（本质上返回 handle）
- 忽略编码导致读取内容乱码

## 参考

- Spring Framework Reference：Resources

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Resources：定位、读取与 classpath 边界

资源问题的高频根因在于“同一段路径在不同运行形态下不是同一件事”：classpath、jar 内资源与 filesystem 文件在定位、读取、pattern 扫描、以及 `exists` 语义上都有细微但决定性的差异。本模块按资源抽象逐步展开，目标是把这些边界跑成可验证的事实。

---

### 10 分钟入口：先跑通一次 classpath 定位与读取
- `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`

运行后应能回答：Resource 抽象背后到底是哪一种实现（classpath/jar/file）；`classpath*:` 与 pattern 扫描在何处展开；为何在 IDE 与打包后运行时表现不同。

### 从这里开始（顺读路径）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [Resource 抽象](docs/resource-abstraction.md)
- [classpath 定位](docs/resource-abstraction-classpath-locations.md)
- [classpath* 与 pattern](docs/resource-abstraction-classpath-star-and-pattern.md)
- [exists 与 handles](docs/resource-abstraction-exists-and-handles.md)
- [读取与编码](docs/resource-abstraction-reading-and-encoding.md)
- [jar vs filesystem](docs/resource-abstraction-jar-vs-filesystem.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-core-resources -Dtest=*ExerciseSolutionTest test`
- 并发/性能（PathMatchingResourcePatternResolver 并发解析）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)

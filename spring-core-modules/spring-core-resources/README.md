# spring-core-resources

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”学习 Spring 的 **`Resource` 抽象**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](../../docs/resources/spring-core-resources/)。

## 你将学到什么

- 统一抽象：classpath / file / URL 资源读取
- classpath 位置写法（是否需要 `/`）
- pattern 扫描：`classpath*:` + 通配符
- `Resource` 的“句柄语义”：`getResource(...)` 返回 handle，`exists()` 才判断存在
- jar vs filesystem 的差异（通过 Exercises 引导观察）

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

## 推荐 docs 阅读顺序（从现象到机制）

1. [`Resource` 抽象：为什么 Spring 不让你直接用 `File`？](../../docs/resources/spring-core-resources/part-01-resource-abstraction/01-resource-abstraction.md)
2. [classpath 路径：`classpath:data/x` vs `classpath:/data/x`](../../docs/resources/spring-core-resources/part-01-resource-abstraction/02-classpath-locations.md)
3. [`classpath*:` 与 pattern：为什么能扫到多个资源？](../../docs/resources/spring-core-resources/part-01-resource-abstraction/03-classpath-star-and-pattern.md)
4. [`getResource(...)` 的返回值：为什么它会返回不存在资源句柄？](../../docs/resources/spring-core-resources/part-01-resource-abstraction/04-exists-and-handles.md)
5. [读取资源：InputStream、编码与可观察性](../../docs/resources/spring-core-resources/part-01-resource-abstraction/05-reading-and-encoding.md)
6. [jar vs filesystem：为什么 IDE OK，打包后不行？](../../docs/resources/spring-core-resources/part-01-resource-abstraction/06-jar-vs-filesystem.md)
7. [常见坑清单（建议反复对照）](../../docs/resources/spring-core-resources/appendix/90-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java` | classpath/file 读取、pattern 扫描、缺失资源错误 | ⭐⭐ | `docs/01` → `docs/03` |
| Lab | `src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java` | handle/exists、description、`classpath*:` 细节 | ⭐⭐ | `docs/04`、`docs/05` |
| Exercise | `src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java` | 新增资源/metadata/jar 差异/排序稳定等练习 | ⭐⭐–⭐⭐⭐ | `docs/06`、`docs/90` |

## 概念 → 在本模块哪里能“看见”

| 你要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 你应该能解释清楚 |
| --- | --- | --- | --- |
| classpath 资源读取 | [docs/02](../../docs/resources/spring-core-resources/part-01-resource-abstraction/02-classpath-locations.md) | `SpringCoreResourcesLabTest#readsClasspathResourceContent` | `Resource` 如何读取 classpath 内容 |
| `classpath:` 是否需要 `/` | [docs/02](../../docs/resources/spring-core-resources/part-01-resource-abstraction/02-classpath-locations.md) | `SpringCoreResourcesLabTest#supportsLeadingSlashInClasspathLocation` | 两种写法为什么都能工作 |
| `classpath*:` pattern 扫描 | [docs/03](../../docs/resources/spring-core-resources/part-01-resource-abstraction/03-classpath-star-and-pattern.md) | `SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath` | 为什么能返回多个 Resource |
| handle 与 exists | [docs/04](../../docs/resources/spring-core-resources/part-01-resource-abstraction/04-exists-and-handles.md) | `SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist` | 为什么 getResource 不返回 null |
| `getDescription()` 的 debug 价值 | [docs/05](../../docs/resources/spring-core-resources/part-01-resource-abstraction/05-reading-and-encoding.md) | `SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging` | 为什么 description 比 path 更可靠 |

## 常见 Debug 路径

- 优先用 `Resource#getDescription()` 做 debug（比你猜 path 更可靠）
- pattern 扫描结果建议排序后断言，避免“顺序不稳定”学歪
- 遇到问题先问：是“资源不存在”（`exists=false`）还是“存在但读不了”（IO 错误）

## 常见坑

- 把 classpath 资源当 File：IDE 里 OK，打包后崩
- 以为 `getResource(...)` 不存在会返回 null（其实返回 handle）
- 忽略编码导致读取内容乱码

## 参考

- Spring Framework Reference：Resources

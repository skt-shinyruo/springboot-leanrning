# spring-boot-autoconfiguration

本模块用“可运行的最小示例 + 可验证的测试实验（实验/练习）”讲透 **Spring Boot 自动配置（Auto-Configuration）**的核心机制：

- 条件装配（`@ConditionalOnProperty/@ConditionalOnClass/@ConditionalOnMissingBean`）
- backoff（用户自定义 bean 覆盖默认配置）
- 顺序与叠加（多个 auto-config 如何组合）

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix-self-check.md`](docs/appendix-self-check.md)

完成标准（应能解释清楚）：

- 为什么某个 bean “有时存在、有时不存在”（条件装配）
- 为什么用户自己定义 bean 后，auto-config 会 backoff（不再创建默认 bean）
- 为什么“有代理/有拦截器/有基础设施”这类能力，经常都用 auto-config 统一装配

## 关键命令

### 测试

```bash
mvn -pl :spring-boot-autoconfiguration test
```

### 运行

```bash
mvn -pl :spring-boot-autoconfiguration spring-boot:run
```

## 实验/练习索引

> 练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 |
| --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationLabTest.java` | 条件装配 + backoff + 顺序叠加（最小闭环） | ⭐⭐ |
| Lab（Perf/Concurrency） | `src/test/java/com/learning/springboot/bootautoconfiguration/part02_perf_concurrency/BootAutoConfigurationConcurrencyLabTest.java` | 并发读取容器产物一致性（Primary/Backoff 的结果不可漂移） | ⭐⭐ |
| Exercise | `src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseTest.java` | 增加 1 个条件分支并固化为断言 | ⭐⭐–⭐⭐⭐ |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Auto-Configuration：导入、条件决策与 back-off

自动装配的难点往往不在注解本身，而在“为什么有时生效、有时不生效”。同一份依赖在不同工程里出现差异，通常来自两个事实：自动配置类是否被导入，以及条件是否满足（并在何处发生 back-off）。本模块把这两个事实拆成可运行实验，并把关键分支固定到断点与断言上。

---

### 10 分钟入口：确认导入与条件决策
- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`

运行后应能在调试器中回答：自动配置是通过何种 imports 机制被导入的；条件评估发生在哪个阶段；最终有哪些 BeanDefinition 被注册、哪些发生 back-off。

---

### 阅读路线（调用链 → 分支 → 正文）
1. 先建立调用链坐标（把入口压到最短）
   - [主线时间线](docs/guide-mainline-timeline.md)
   - [深挖导读](docs/guide-deep-dive-guide.md)
   - [AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](docs/guide-autoconfiguration-import-call-chain.md)
2. 再用断点与分支矩阵收敛关键 if/then
   - [断点地图](docs/guide-breakpoint-map.md)
   - [关键分支矩阵](docs/guide-branch-decision-matrix.md)
3. 最后进入正文（把条件与 back-off 跑成事实）
   - [条件装配与 backoff：为什么它“有时生效、有时不生效”](docs/autoconfig-basics-conditional-and-backoff.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)

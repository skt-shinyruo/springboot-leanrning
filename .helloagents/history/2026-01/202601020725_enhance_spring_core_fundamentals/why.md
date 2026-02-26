# 变更提案：完善 Spring Core 基础模块学习体验（Beans / AOP / Tx / Events）

## 需求背景

本仓库以“一个模块对应一个主题”的方式帮助初学者学习 Spring Boot / Spring Framework 的常用机制。当前 `spring-core-*` 系列模块已经具备较完整的示例与部分 Labs/Exercises，但在“面向初学者的学习路径一致性”“可断言的实验覆盖度”“运行态可观察点（日志/运行输出）”方面仍有提升空间。

本次改造聚焦于 **已有模块的学习体验增强**（不新增新模块、不引入 Web/Actuator 端点），让初学者能做到：
- 能按 README → docs → Labs/Exercises 的路径循序渐进
- 以 `mvn test` 的断言为主验证机制结论
- 通过 `spring-boot:run` 运行时输出（日志/控制台）辅助建立直觉

## 产品分析

### 目标用户与使用场景
- **用户群体：** Spring/Spring Boot 初学者；具备 Java 基础，但对 IoC/DI/代理/事务/事件缺少系统理解
- **使用场景：**
  - 自学：按模块完成 Labs/Exercises 并打卡进度（`docs/progress.md`）
  - 复盘：用最小可运行示例 + 断言，复现 “为什么会这样”
  - Debug：通过断点与可观察点定位 “代理是否生效”“事务边界在哪里”“事件是否同步/线程是谁”

### 价值主张与成功指标
- **价值主张：** 让“能跑”变成“能断言、能解释、能调试”
- **成功指标（可验收）：**
  - 每个目标模块 README 明确给出：学习目标、推荐阅读顺序、Labs/Exercises 索引、运行态观察点
  - `spring-core-events` 与 `spring-core-tx` 补齐核心机制的 Labs/Exercises 覆盖（以断言为主，避免不稳定测试）
  - 根 `README.md` 与 `docs/progress.md` 的索引/学习路线与新增内容保持一致

### 人文关怀
- 以“可验证的最小实验”降低学习挫败感，避免只靠日志猜测
- 明确指出常见坑与排查路径，减少初学者在代理/事务/异步上的误判

## 变更内容
1. 统一并强化四个 Spring Core 模块的学习导航：README/docs/测试入口一致且易发现
2. 增强 `spring-core-events` 与 `spring-core-tx` 的 Labs/Exercises：覆盖同步/异常传播/异步、回滚规则/传播/自调用陷阱等关键点
3. 增强 `spring-boot:run` 的运行态观察：通过结构化输出展示关键结论（线程、代理、事务活跃状态等）
4. 同步更新全局索引：根 `README.md` 与 `docs/progress.md`

## 影响范围
- **Modules:**
  - `spring-core-beans`
  - `spring-core-aop`
  - `spring-core-events`
  - `spring-core-tx`
- **Files（预期）：**
  - 各模块 `README.md`、部分 `docs/*.md`
  - 各模块 `src/test/java/**/*LabTest.java`、`src/test/java/**/*ExerciseTest.java`
  - 各模块 `src/main/java/**/*DemoRunner.java`（或等效 Runner）
  - 根 `README.md`
  - `docs/progress.md`

## 核心场景

### Requirement: 初学者能在每个模块内快速找到“学什么、怎么学、如何验证”
**Module:** spring-core-beans / spring-core-aop / spring-core-events / spring-core-tx
通过 README 提供统一结构：学习目标 → 推荐阅读顺序 → Labs/Exercises → 运行态观察点 → 常见 Debug 路径。

#### Scenario: 只看 README 就能跑通并验证一个关键结论
在模块 README 中给出最小命令（`mvn -pl <module> test` / `spring-boot:run`）与观察点说明。
- 期望结果：学习者能在 5 分钟内完成一次“运行 + 断言 + 解释”的闭环

### Requirement: 建立 Spring 容器的“三层心智模型”（定义 / 实例 / 代理替身）
**Module:** spring-core-beans
初学者需要先把“容器里到底是什么”说清楚：`BeanDefinition` 是元数据定义；Bean instance 是创建出来的对象；而容器最终暴露的对象可能是 **被 BPP 包装/替换后的 proxy**。如果不建立这三层心智模型，后续 AOP/事务/校验等模块会持续误判。

#### Scenario: 学习者能用断言证明“BeanDefinition ≠ Bean instance ≠ 最终暴露对象”
- 期望结果：在单个 Lab 中通过断言稳定验证三者差异，并能解释 BFPP/BPP 分别作用在“定义层/实例层”

### Requirement: 能解释 DI 解析的“选择规则”并避免注入歧义
**Module:** spring-core-beans
以 `@Qualifier` / `@Primary` / `@Order` / 候选过滤为主线，让学习者能回答：当多个候选 Bean 同时存在时，容器到底如何选择最终注入目标？

#### Scenario: 学习者能复现并修复“多个候选导致注入失败”
- 期望结果：通过一个可断言的失败用例复现歧义，再用 `@Qualifier` 或 `@Primary` 修复，并能解释差异

### Requirement: 能用时间线解释 Bean 生命周期回调与注入阶段
**Module:** spring-core-beans
明确“构造 → aware → 属性填充 → init 回调链（@PostConstruct/InitializingBean/initMethod）→ BPP after → 销毁回调链”的顺序，并区分 singleton/prototype 在销毁阶段的行为差异。

#### Scenario: 学习者能用断言写出生命周期回调顺序（并解释 prototype 为何默认不销毁）
- 期望结果：通过事件序列断言固化顺序，避免仅靠日志学习到不稳定结论

### Requirement: 面试导向知识点沉淀到正文（不新增独立“面试题汇总文档”）
**Module:** spring-core-beans
把 Spring 原理/框架岗常问的知识点，按主题“落到对应章节正文的小节中”，让学习者在读机制解释时就能同步掌握面试问法与追问方向，而不是依赖额外的面试题汇总文档。

#### Scenario: 每个新增面试点都嵌入到对应小节，并给出可复现入口
- 期望结果：
  - 在对应章节正文中插入 “题目 + 追问 + 本仓库对应 Lab/Test 入口”
  - 若仓库内缺少可复现入口，则补充一个最小 Lab（稳定断言、单一主题）

### Requirement: 事件机制的核心现象可被稳定断言
**Module:** spring-core-events
补齐 Labs/Exercises，覆盖同步/顺序/异常传播/@Async（启用与未启用对比）等关键机制，避免依赖不稳定的时序假设。

#### Scenario: 学习者能证明“事件默认同步且异常会传播”
- 期望结果：测试断言事件执行线程与异常传播行为一致、可重复

### Requirement: 事务机制的核心现象可被稳定断言
**Module:** spring-core-tx
补齐 Labs/Exercises，覆盖提交/回滚、checked exception 回滚规则、传播（REQUIRED/REQUIRES_NEW）、自调用绕过代理等关键点。

#### Scenario: 学习者能证明“自调用会绕过事务代理”
- 期望结果：测试能复现“不生效”的最小场景，并给出修复方式的对比断言

### Requirement: 全局学习索引与打卡清单保持一致
**Module:** repo root
根 `README.md` 与 `docs/progress.md` 同步更新，确保新增/调整的 Labs/Exercises 与学习路线可被发现。

#### Scenario: 学习者按索引可定位到具体测试/文档入口
- 期望结果：索引包含准确路径，且与模块 README 的入口一致

## 风险评估
- **风险：** 异步相关测试容易出现不稳定（flaky）
  - **缓解：** 使用 `CountDownLatch`/超时等待等确定性手段；避免 `Thread.sleep`
- **风险：** 为了运行态观察引入 Web/Actuator 端点导致范围扩大与安全边界复杂化
  - **缓解：** 本次方案明确不引入端点，仅通过日志/控制台输出提供观察点（端点学习交由 `springboot-actuator` 模块）
- **风险：** README/索引更新导致路径不一致或过期
  - **缓解：** 将 README 的索引与测试类/文档路径绑定更新，并在 CI/本地测试中验证链接可用性（人工检查 + 跑测试）

# 技术方案：spring-core-beans 学习路线与最小闭环（README + Labs/Exercises）

## 技术方案

### 核心技术
- Java 17 / Spring Boot（项目既有版本与依赖）
- JUnit 5 + AssertJ（以断言为主）
- `spring-boot-maven-plugin`（`spring-boot:run` 运行态观察）

### 实现要点

1) README 结构化设计（优先信息架构，不堆内容）
- Start Here：只给 1～3 个强推荐入口（命令 + 观察点 + “你应该能解释什么”）
- 学习路线：入门→进阶→深挖，每层给出入口与目标
- refresh 主线一页纸：阶段 → 关键类/扩展点 → 本模块 docs → 对应 Lab/Test
- 运行态观察点：把 `BeansDemoRunner` 输出字段与 docs/Labs 建立映射

2) docs 增强遵循“调试优先”
- `docs/00-deep-dive-guide.md`：按阶段给断点地图（BFPP/BPP/注入/生命周期/代理/循环依赖）
- `docs/11-debugging-and-observability.md`：用“最小断点闭环”组织（每个闭环绑定一个测试入口）

3) Labs/Exercises 增量遵循“单主题 + 稳定断言”
- 新增 `SpringCoreBeansInjectionAmbiguityLabTest`
  - 场景 1：多候选导致 refresh 失败（断言异常类型）
  - 场景 2：用 `@Primary` 修复（断言注入目标）
  - 场景 3：用 `@Qualifier` 修复（断言注入目标）
- 在 `SpringCoreBeansExerciseTest` 添加对应练习题（`@Disabled`）

4) 全局索引同步策略（避免漂移）
- 先落地模块 README/入口测试与 docs 映射
- 再更新根 `README.md` 与 `docs/progress.md` 中 spring-core-beans 的入口链接

## 安全与范围控制
- 不新增 Web/Actuator 端点（避免扩大范围与安全边界）
- 不引入外部依赖（仅使用 Spring 已内置能力）
- 关键结论以 `mvn test` 的断言为准，运行态输出仅作为辅助观察点

## 测试与验证
- 目标模块回归：`mvn -pl spring-core-beans test`
- 文档一致性检查：README 中列出的入口测试类/方法必须真实存在
- 运行态验证（可选）：`mvn -pl spring-core-beans spring-boot:run`，观察 `BeansDemoRunner` 输出是否与 README 的“观察点”一致


# 变更提案：spring-core-beans 学习路线与最小闭环（README + Labs/Exercises + 运行态观察）

## 需求背景

当前 `spring-core-beans` 已经有较丰富的 docs 与 Labs，但对初学者来说常见痛点是：

1. 入口太多：不知道从哪里开始，容易在 30+ 个 Lab/Test 里迷路
2. 概念与复现入口没有强绑定：看完解释后不知道去哪跑一个断言把结论“钉住”
3. refresh 主线、BFPP/BPP、注入解析、生命周期这些核心机制缺少“统一导航”和“一页纸地图”
4. 运行态观察点（`spring-boot:run`）虽然有价值，但如果没有“应该观察什么/对应哪个 Lab”的映射，学习效果会大打折扣

因此需要在 **不改变现有代码结构与风格** 的前提下，把 `spring-core-beans` 打磨成“5 分钟闭环可跑、入门→进阶→深挖可循、每章都有可断言复现入口”的学习模块。

## 变更内容

1. 强化 `spring-core-beans/README.md` 作为“唯一入口导航”：
   - Start Here（5 分钟闭环）
   - 学习路线（入门→进阶→深挖）
   - refresh 主线一页纸（阶段 → docs → Lab 对应表）
   - 运行态观察点（`spring-boot:run`）与“可断言入口”的映射
2. 强化深挖指南与调试手册：
   - `docs/00-deep-dive-guide.md`：按阶段的断点地图 + 从一个入口测试走完全链路
   - `docs/11-debugging-and-observability.md`：IoC/DI 与生命周期 Debug Playbook（最小断点闭环 + 推荐入口测试）
3. 补齐/增强最小可断言实验：
   - 新增“注入歧义最小复现” Lab（失败→修复对照）
   - 在 `SpringCoreBeansExerciseTest` 增加对应练习题（默认 `@Disabled`）
4. 同步更新全局索引：
   - 根 `README.md` 的学习路线索引（至少更新 spring-core-beans 入口）
   - `docs/progress.md` 的打卡入口（至少更新 spring-core-beans 最小闭环）

## 影响范围

- **Modules:**
  - `spring-core-beans`
- **Files（预期）：**
  - `spring-core-beans/README.md`
  - `docs/beans/spring-core-beans/00-deep-dive-guide.md`
  - `docs/beans/spring-core-beans/11-debugging-and-observability.md`
  - `spring-core-beans/src/main/java/**/BeansDemoRunner.java`
  - `spring-core-beans/src/test/java/**/SpringCoreBeansInjectionAmbiguityLabTest.java`
  - `spring-core-beans/src/test/java/**/SpringCoreBeansExerciseTest.java`
  - 根 `README.md`
  - `docs/progress.md`

## 核心场景

### Requirement: 5 分钟完成一次“运行 + 断言 + 解释”的闭环
**Module:** spring-core-beans
仅通过模块 README，学习者能定位并跑通一个关键结论：容器的“三层心智模型”（定义/实例/最终暴露对象），并知道下一步怎么深入。

#### Scenario: Start Here 提供最小命令与入口测试
- README 给出 `mvn -pl spring-core-beans test` 的推荐测试方法入口
- 能解释清楚：BeanDefinition vs instance vs proxy 的差异

### Requirement: 学习路线清晰（入门→进阶→深挖），且每一层都有“你应该能解释什么”
**Module:** spring-core-beans
把入口固定为少量“强推荐”Lab，避免新手在大量测试里迷失。

#### Scenario: 每层路线都有明确的目标与入口
- 入门：3 个入口（`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `docs/00-deep-dive-guide.md`）
- 进阶：refresh 主线与关键扩展点（BFPP/BPP/生命周期/DI 决策树）
- 深挖：断点地图 + 反例（循环依赖/代理类型陷阱/候选选择）

### Requirement: DI 注入歧义“可复现、可修复、可对照”
**Module:** spring-core-beans
把“候选收集→候选收敛→最终注入”的概念落到最小失败用例与修复对照。

#### Scenario: 新增 Lab 与 Exercise 驱动学习
- Lab：复现 `NoUniqueBeanDefinitionException`，并用 `@Qualifier/@Primary` 修复
- Exercise：默认 disabled，引导学习者独立完成修复并写断言

### Requirement: refresh 主线一页纸 + 断点地图（按阶段）
**Module:** spring-core-beans
让学习者能把 docs 与 Labs 放回 `AbstractApplicationContext#refresh` 的阶段里理解。

#### Scenario: 从一个入口测试走完整链路
- `docs/00` 给出按阶段的断点地图
- `docs/11` 给出 debug playbook（最小断点闭环）

## 风险评估

- **风险：** README 入口过多导致信息噪声
  - **缓解：** 强制“Start Here ≤ 3 个入口”，其余内容放入“深入/参考”区
- **风险：** 运行态观察点依赖日志/时序不稳定
  - **缓解：** 运行态仅作为辅助观察；关键结论以 `mvn test` 断言为主


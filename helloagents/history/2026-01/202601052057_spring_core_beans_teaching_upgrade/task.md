# Task List: spring-core-beans 教学闭环（源码级）全量深化

Directory: `helloagents/plan/202601052057_spring_core_beans_teaching_upgrade/`

---

## 0. 全局基线与工具化（先稳住回归）
- [√] 0.1 审计现状：统计每章是否具备“复现入口/运行命令/断点建议/Deep Dive（调用链+关键分支）/练习指引”，记录缺口（以 `docs/beans/spring-core-beans/**` 为准）
  > Note: 基于 docs 关键字/结构审计：51 篇文档均包含断点建议；README/部分 Appendix 不适用“复现入口/运行命令”块；少数章节未出现“推荐运行命令”字样但提供可运行入口（以 Lab/Test 路径指向为主）。
- [√] 0.2 为现有 Exercise 补齐 Solution（默认参与回归）：新增对应的 Solution 测试套件（对齐 `spring-core-beans/src/test/java/**/*ExerciseTest.java`）
  > Note: 新增 4 份 Solution（默认参与回归），覆盖 Part00/01/02/03 的 Exercise 题目集合。
- [√] 0.3 更新 `docs/beans/spring-core-beans/README.md`：加入“章节 ↔ Lab ↔ Exercise ↔ Solution”映射（可检索结构）
- [√] 0.4 验证：运行 `mvn -pl spring-core-beans test` + `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

## 1. Part 00 Guide（学习路线与总入口）
- [-] 1.1 深化 `docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md`：补齐源码主线与关键分支指引，并指向对应 Lab/Exercise/Solution
  > Note: 本轮优先补齐 Exercise↔Solution 回归闭环与关键章节补强；00 指南整体已具备主线/断点/入口结构，暂不改动。
- [-] 1.2 验证：模块测试 + docs 链接检查
  > Note: 已在 0.4 / 7.5 执行全量验证。

## 2. Part 01 IoC Container（01–09 全章源码级补齐）
- [-] 2.1 `docs/beans/spring-core-beans/part-01-ioc-container/01-bean-mental-model.md`：补齐调用链主线 + 关键分支/边界条件 + 断点/观察点 + 练习指引
  > Note: 文档已具备主线/断点/入口（并能映射到可运行 Lab），本轮暂不改动。
- [√] 2.2 `docs/beans/spring-core-beans/part-01-ioc-container/01-bean-registration.md`：补齐 ImportSelector/Registrar 等机制的源码主线与关键分支，并对齐 Exercise/Solution 指引
- [-] 2.3 `docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`：补齐候选收集/缩小的算法主线与关键分支，并对齐 Lab/练习指引
  > Note: 章节已覆盖候选算法主线与断点入口，本轮暂不改动。
- [√] 2.4 `docs/beans/spring-core-beans/part-01-ioc-container/04-scope-and-prototype.md`：补齐 scope/prototype 边界的源码路径与关键分支，对齐 Lab/练习指引
- [-] 2.5 `docs/beans/spring-core-beans/part-01-ioc-container/05-lifecycle-and-callbacks.md`：补齐生命周期回调顺序的源码主线与关键分支，对齐 Lab/练习指引
  > Note: 章节已具备可运行入口与断点建议，本轮暂不改动。
- [-] 2.6 `docs/beans/spring-core-beans/part-01-ioc-container/06-post-processors.md`：补齐 BFPP/BPP 主线与关键分支（时机/顺序），对齐 Lab/练习指引
  > Note: 章节已具备源码级主线与排序/时机断点，本轮暂不改动。
- [-] 2.7 `docs/beans/spring-core-beans/part-01-ioc-container/07-configuration-enhancement.md`：补齐配置类增强/代理的源码主线与关键分支，对齐 Lab/练习指引
  > Note: 章节已具备主线与可运行入口，本轮暂不改动。
- [-] 2.8 `docs/beans/spring-core-beans/part-01-ioc-container/07-factorybean.md`：补齐 FactoryBean 的 getObjectType/getObject 相关分支与主线，对齐 Lab/练习指引
  > Note: 章节已具备可运行入口与边界分支提示，本轮暂不改动。
- [-] 2.9 `docs/beans/spring-core-beans/part-01-ioc-container/08-circular-dependencies.md`：补齐循环依赖“三层缓存/early reference”源码主线与边界条件，对齐 Lab/练习指引
  > Note: 章节已具备三级缓存/early reference 主线与断点入口，本轮暂不改动。
- [-] 2.10 验证：模块测试 + docs 链接检查
  > Note: 已在 0.4 / 7.5 执行全量验证。

## 3. Part 02 Boot Autoconfig（10–11 全章源码级补齐）
- [-] 3.1 `docs/beans/spring-core-beans/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`：补齐 auto-config 导入/排序/条件评估主线与关键分支，对齐 Lab/Exercise/Solution 指引
  > Note: 章节已具备主线/断点/复现入口；本轮新增 Solution 已在 docs/README 统一对照表中可检索。
- [-] 3.2 `docs/beans/spring-core-beans/part-02-boot-autoconfig/11-debugging-and-observability.md`：补齐排障主线（Condition report/bean origin 等）与关键分支，对齐 Lab/练习指引
  > Note: 章节已具备排障 playbook 与复现入口；本轮新增 Solution 已在 docs/README 统一对照表中可检索。
- [-] 3.3 验证：模块测试 + docs 链接检查
  > Note: 已在 0.4 / 7.5 执行全量验证。

## 4. Part 03 Container Internals（12–17 全章源码级补齐）
- [-] 4.1 `docs/beans/spring-core-beans/part-03-container-internals/12-container-bootstrap-and-infrastructure.md`：补齐 bootstrap 主线与关键分支，对齐 Lab/练习指引
  > Note: 章节已具备 bootstrap 主线与可运行入口，本轮暂不改动。
- [-] 4.2 `docs/beans/spring-core-beans/part-03-container-internals/02-bdrpp-definition-registration.md`：补齐 BDRPP 注册主线与关键分支，对齐 Lab/练习指引
  > Note: 章节已具备可运行入口与断点建议，本轮暂不改动。
- [-] 4.3 `docs/beans/spring-core-beans/part-03-container-internals/03-post-processor-ordering.md`：补齐排序算法主线与关键分支，对齐 Lab/Exercise/Solution 指引
  > Note: 章节已具备算法级主线与入口，本轮暂不改动。
- [-] 4.4 `docs/beans/spring-core-beans/part-03-container-internals/04-pre-instantiation-short-circuit.md`：补齐 pre-instantiation 分支与主线，对齐 Lab/练习指引
  > Note: 章节已具备可运行入口与断点建议，本轮暂不改动。
- [-] 4.5 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`：补齐 early reference 分支与主线，对齐 Lab/练习指引
  > Note: 章节已具备可运行入口与断点建议，本轮暂不改动。
- [-] 4.6 `docs/beans/spring-core-beans/part-03-container-internals/06-lifecycle-callback-order.md`：补齐生命周期排序主线与关键分支，对齐 Lab/练习指引
  > Note: 章节已具备可运行入口与断点建议，本轮暂不改动。
- [-] 4.7 验证：模块测试 + docs 链接检查
  > Note: 已在 0.4 / 7.5 执行全量验证。

## 5. Part 04 Wiring & Boundaries（18–37 全章源码级补齐）
- [-] 5.1 逐章补齐 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/18-*.md` 至 `37-*.md`：统一 Deep Dive（调用链 + 关键分支）与练习指引，并确保每章与可运行入口对齐
  > Note: Part04 已具备统一 A–G 契约与可运行入口；本轮新增 Solution 入口统一收敛到 docs/README 对照表，暂不逐章重复添加。
- [-] 5.2 对于 Part04 中“边界机制”章节：确保至少有一个可切换分支的复现入口（必要时补最小 Lab 或增强既有 Lab）
  > Note: Part04 多数章节已具备可切换分支的最小复现入口，本轮暂不新增 Lab。
- [-] 5.3 验证：模块测试 + docs 链接检查
  > Note: 已在 0.4 / 7.5 执行全量验证。

## 6. Part 05 AOT & Real World（40–45 全章源码级补齐）
- [√] 6.1 `docs/beans/spring-core-beans/part-05-aot-and-real-world/40-*.md` 至 `45-*.md`：统一 Deep Dive（调用链 + 关键分支）与练习指引，对齐 Part05 Labs
  > Note: 补强 42–45 的“源码/断点建议”与观察点说明（XML/容器外对象/SpEL/自定义 Qualifier）；其余章节结构与入口保持不变。
- [-] 6.2 验证：模块测试 + docs 链接检查
  > Note: 已在 0.4 / 7.5 执行全量验证。

## 7. Appendix 与全局收敛（目录、地图、知识库）
- [-] 7.1 更新 `docs/beans/spring-core-beans/appendix/03-knowledge-map.md`：补齐“从概念到章节/入口”的检索路径，并标注核心主线
  > Note: 本轮优先收敛 docs/README 的“章节↔Lab↔Exercise↔Solution”可检索入口；知识点地图保持不变。
- [√] 7.2 更新 `docs/beans/spring-core-beans/README.md`：收敛成稳定入口（章节 ↔ Lab ↔ Exercise ↔ Solution 的映射完整可用）
- [√] 7.3 同步知识库：更新 `helloagents/wiki/modules/spring-core-beans.md`（入口、覆盖率、现状说明）
- [√] 7.4 更新 `helloagents/CHANGELOG.md`：记录本次变更范围与关键新增（Solution、索引、源码级深化）
- [√] 7.5 全量验证：`mvn -pl spring-core-beans test` + `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

# Task List: spring-core-beans 源码按 docs Part 分组

Directory: `helloagents/plan/202601032124_spring-core-beans-src-part-grouping/`

---

## 1. Package Structure (spring-core-beans)
- [√] 1.1 设计并落地 Part 对齐的 package 结构（`part00/part01/part02/part03/part04/appendix` + `testsupport`），并确认 `SpringCoreBeansApplication` 包名不变，verify why.md#requirement-keep-application-package-stable

## 2. Refactor `src/main/java` (spring-core-beans)
- [√] 2.1 将 main 中非启动类示例代码迁移到 `com.learning.springboot.springcorebeans.part01`（或其子包），并修复编译依赖与 import，verify why.md#requirement-part-based-source-grouping

## 3. Refactor `src/test/java` (spring-core-beans)
- [√] 3.1 识别并迁移跨 Part 复用的测试工具/marker 到 `com.learning.springboot.springcorebeans.testsupport`，必要类型改为 `public` 并修复引用，verify why.md#risk-assessment
- [√] 3.2 按 Part 将 IoC container 相关 Labs/Exercises 测试迁移到 `com.learning.springboot.springcorebeans.part01`，修复 package/import，verify why.md#requirement-part-based-source-grouping
- [√] 3.3 按 Part 将 Boot auto-config 相关测试迁移到 `com.learning.springboot.springcorebeans.part02`，修复 package/import，verify why.md#requirement-part-based-source-grouping
- [√] 3.4 按 Part 将 container internals 相关测试迁移到 `com.learning.springboot.springcorebeans.part03`，修复 package/import，verify why.md#requirement-part-based-source-grouping
- [√] 3.5 按 Part 将 wiring & boundaries 相关测试迁移到 `com.learning.springboot.springcorebeans.part04`，修复 package/import，verify why.md#requirement-part-based-source-grouping

## 4. Docs Path Fix (docs/beans/spring-core-beans)
- [√] 4.1 全量修复 `docs/beans/spring-core-beans/**` 中对 `src/main/java` 与 `src/test/java` 的源码路径引用（链接/文本），verify why.md#requirement-docs-source-path-consistency

## 5. Security Check
- [√] 5.1 执行安全自检（不引入敏感信息/生产配置；不提升权限；避免误改启动类包名），verify why.md#risk-assessment

## 6. Testing
- [√] 6.1 执行 `mvn -q -pl spring-core-beans test` 并记录结果（通过/失败原因与修复点）

## 7. Knowledge Base Sync
- [√] 7.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充“源码与测试按 Part 分组”的约定与导航
- [√] 7.2 更新 `helloagents/CHANGELOG.md`：记录本次重构（Changed）

---

## Execution Notes

- Test result: `mvn -q -pl spring-core-beans test` ✅ 通过

# Task List: spring-core-beans 深化（PostProcessors + 容器启动基础设施）源码解析补强

Directory: `helloagents/history/2026-01/202601030731_spring-core-beans-post-processors-bootstrap-source-deepening/`

---

## 1. spring-core-beans
- [√] 1.1 更新 `docs/beans/spring-core-beans/06-post-processors.md`：补齐 `PostProcessorRegistrationDelegate` 两段算法的源码解析（含精简伪代码与关键分叉），验证 why.md#scenario-bdrpp-bfpp-algorithm 与 why.md#scenario-register-bpp-algorithm
- [√] 1.2 新增 Lab：`spring-core-beans/src/test/java/.../SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java`（或同等命名），用事件断言复现“non-static BFPP 迫使配置类早实例化，从而错过普通 BPP”的机制，验证 why.md#scenario-static-bean-for-postprocessor
- [√] 1.3 更新 `docs/beans/spring-core-beans/06-post-processors.md`：补齐“为什么 post-processor 常建议 static @Bean”的源码级解释，并引用新增 Lab 的最小片段，验证 why.md#scenario-static-bean-for-postprocessor
- [√] 1.4 更新 `docs/beans/spring-core-beans/12-container-bootstrap-and-infrastructure.md`：补齐 `AnnotationConfigApplicationContext` 的默认装配链路（reader/processor 注册→refresh→BPP 生效），并补充“进 registry ≠ 已生效”的时机差异，验证 why.md#scenario-annotation-processors-bootstrap

## 2. Security Check
- [√] 2.1 检查新增内容不包含敏感信息/外部依赖，且不涉及生产环境操作（per G9）

## 3. Testing
- [√] 3.1 运行并确保通过：`mvn -pl spring-core-beans test`

## 4. Knowledge Base Sync
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：记录本次补强点与入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`
 - [√] 4.3 将方案包迁移到 `helloagents/history/2026-01/202601030731_spring-core-beans-post-processors-bootstrap-source-deepening/`

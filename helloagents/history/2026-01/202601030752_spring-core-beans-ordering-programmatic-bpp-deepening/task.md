# Task List: spring-core-beans Ordering & Programmatic BPP Deepening

Directory: `helloagents/plan/202601030752_spring-core-beans-ordering-programmatic-bpp-deepening/`

---

## 1. spring-core-beans 文档（算法级）
- [√] 1.1 补强 `docs/beans/spring-core-beans/03-post-processor-ordering.md`：加入 BFPP/BDRPP 与 BPP 的排序/注册算法级解析与伪代码
- [√] 1.2 补强 `docs/beans/spring-core-beans/08-programmatic-bpp-registration.md`：加入 `addBeanPostProcessor` 的 list 语义、绕过排序原因与“时机陷阱”解析

## 2. spring-core-beans Labs（可复现闭环）
- [√] 2.1 增强 `SpringCoreBeansPostProcessorOrderingLabTest`：补充 order 数值与 `@Order` 的可复现实验（只断言相对顺序）

## 3. Security Check
- [√] 3.1 安全检查：无敏感信息、无 EHRB 操作、无明文凭证写入

## 4. Knowledge Base Sync
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次新增的文档要点与实验入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`

## 5. Testing
- [√] 5.1 运行 `mvn -q -pl spring-core-beans test`

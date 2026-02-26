# Task List: spring-core-aop 深化（源码级文档 + 可验证闭环）

Directory: `helloagents/plan/202601010804_spring-core-aop-deep-dive/`

---

## 1. spring-core-aop（文档）

- [√] 1.1 新增 AOP 深挖指南（断点入口/观察点/最短 call chain），并更新 README 阅读顺序
- [√] 1.2 深化 docs/01（心智模型）：明确 call path、代理产生时机与 BPP 关联，并指向对应 Lab
- [√] 1.3 深化 docs/02（JDK vs CGLIB）：补齐类型/注入边界、proxyTargetClass 配置入口与断点建议
- [√] 1.4 深化 docs/03（自调用）：补齐工程解法对比（重构/自注入/exposeProxy），并指向 Exercise
- [√] 1.5 深化 docs/04（代理限制）：补齐 final/private/static/构造期调用等边界与误区
- [√] 1.6 深化 docs/05（exposeProxy）：补齐启用方式、thread-local 语义与风险提示
- [√] 1.7 深化 docs/06（debugging）：补齐 proxy/advisor/拦截链观察方法与排查顺序
- [√] 1.8 扩写 docs/90（常见坑）：每条坑都映射到“章节 + Lab/Exercise + 推荐断点”
- [√] 1.9 新增 docs/99（自测题）：把关键结论固化成可回答清单，并指向 Exercises

## 2. Security Check

- [√] 2.1 安全检查：不引入敏感信息、不做生产环境操作、不引入危险命令/配置

## 3. Knowledge Base Update

- [√] 3.1 更新 `helloagents/wiki/modules/spring-core-aop.md`（规格与变更记录）
- [√] 3.2 更新 `helloagents/CHANGELOG.md`（记录本次变更）

## 4. Testing

- [√] 4.1 运行并通过：`mvn -pl spring-core-aop test`

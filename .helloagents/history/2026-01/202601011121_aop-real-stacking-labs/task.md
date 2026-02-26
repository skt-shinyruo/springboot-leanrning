# Task List: spring-core-aop 真实多代理叠加（Tx/Cache/Security）+ AOP 内核深化 + 学习路线

Directory: `helloagents/history/2026-01/202601011121_aop-real-stacking-labs/`

---

## 1. spring-core-aop（依赖与最小基础设施）
- [√] 1.1 为 `spring-core-aop` 增加学习用依赖（Tx + Method Security），并记录变更到文档/知识库：`spring-core-aop/pom.xml`

## 2. spring-core-aop（真实叠加 Labs：Tx/Cache/Security/AOP）
- [√] 2.1 新增集成 Lab：同一方法叠加 `@Traced/@Transactional/@Cacheable/@PreAuthorize`，覆盖“鉴权阻断/缓存短路/事务激活”三类断言：`spring-core-aop/src/test/java/.../SpringCoreAopRealWorldStackingLabTest.java`
- [√] 2.2 增强可观察性：提供 advisor/chain 观察工具或断言入口（复用到集成 Lab）：`spring-core-aop/src/test/java/...`

## 3. spring-core-aop（文档：学习路线 + 断点清单 + 真实叠加排障）
- [√] 3.1 新增“真实叠加 Debug Playbook”章节（路线 + 断点清单 + checklist）：`docs/aop/spring-core-aop/part-03-proxy-stacking/10-real-world-stacking-playbook.md`
- [√] 3.2 更新现有章节引用与导航（README/深挖指南/多代理章节）：`spring-core-aop/README.md`、`docs/aop/spring-core-aop/part-00-guide/00-deep-dive-guide.md`、`docs/aop/spring-core-aop/part-03-proxy-stacking/09-multi-proxy-stacking.md`

## 4. Security Check
- [√] 4.1 安全自检（G9）：不引入明文密钥/外部服务；依赖变更最小化；不引入高风险命令

## 5. Knowledge Base Sync（SSOT）
- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-aop.md`
- [√] 5.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`

## 6. Testing
- [√] 6.1 运行并通过：`mvn -pl spring-core-aop test`

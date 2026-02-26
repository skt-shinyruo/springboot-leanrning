# Task List: spring-core-* Part 化结构同步（对齐 spring-core-beans）

Directory: `helloagents/plan/202601041046_spring-core-part-structure-sync/`

---

## 1. spring-core-aop
- [√] 1.1 调整 `docs/aop/spring-core-aop/`：建立 `part-00-guide/`、`part-01-*/`、`appendix/` 并迁移现有章节文件，修复文档内部链接
- [√] 1.2 迁移 `spring-core-aop/src/main/java` 非 Application 代码到 `com.learning.springboot.springcoreaop.part01_*` 等语义化 package，保持 `SpringCoreAopApplication` 不变
- [√] 1.3 迁移 `spring-core-aop/src/test/java` 到与 docs Part 对齐的 package（`part01_*`/`part02_*`/`appendix`/`testsupport`），修复 import
- [√] 1.4 同步修复 `docs/aop/spring-core-aop/**` 中所有源码路径与包名引用
- [√] 1.5 验证：执行 `mvn -pl spring-core-aop test`

## 2. spring-core-events
- [√] 2.1 调整 `docs/events/spring-core-events/`：补齐 `part-00-guide/00-deep-dive-guide.md`、`appendix/99-self-check.md`，并将现有章节移动到 `part-01-*`，修复链接
- [√] 2.2 迁移 `spring-core-events/src/main/java` 非 Application 代码到 `com.learning.springboot.springcoreevents.part01_*` 等，保持 `SpringCoreEventsApplication` 不变
- [√] 2.3 迁移 `spring-core-events/src/test/java` 到 Part package，修复 import
- [√] 2.4 同步修复 `docs/events/spring-core-events/**` 中所有源码路径与包名引用
- [√] 2.5 验证：执行 `mvn -pl spring-core-events test`

## 3. spring-core-profiles
- [√] 3.1 新增 `docs/profiles/spring-core-profiles/` 书本骨架：`docs/README.md`、`docs/part-00-guide/00-deep-dive-guide.md`、`docs/part-01-*/`、`docs/appendix/90-common-pitfalls.md`、`docs/appendix/99-self-check.md`
- [√] 3.2 将现有 Labs/Exercises 对应的知识点链接进 docs（以“索引/入口 + 最小章节占位”为主，后续可再深挖）
- [√] 3.3 迁移 `spring-core-profiles/src/main/java` 非 Application 代码到 `com.learning.springboot.springcoreprofiles.part01_*` 等，保持 `SpringCoreProfilesApplication` 不变
- [√] 3.4 迁移 `spring-core-profiles/src/test/java` 到 Part package，修复 import
- [√] 3.5 验证：执行 `mvn -pl spring-core-profiles test`

## 4. spring-core-resources
- [√] 4.1 调整 `docs/resources/spring-core-resources/`：补齐 `part-00-guide/00-deep-dive-guide.md`、`appendix/99-self-check.md`，并将章节移动到 `part-01-*`
- [√] 4.2 迁移 `spring-core-resources/src/main/java` 非 Application 代码到 `com.learning.springboot.springcoreresources.part01_*` 等，保持 `SpringCoreResourcesApplication` 不变
- [√] 4.3 迁移 `spring-core-resources/src/test/java` 到 Part package，修复 import
- [√] 4.4 同步修复 `docs/resources/spring-core-resources/**` 中所有源码路径与包名引用
- [√] 4.5 验证：执行 `mvn -pl spring-core-resources test`

## 5. spring-core-tx
- [√] 5.1 调整 `docs/tx/spring-core-tx/`：补齐 `part-00-guide/00-deep-dive-guide.md`、`appendix/99-self-check.md`，并将章节移动到 `part-01-*`
- [√] 5.2 迁移 `spring-core-tx/src/main/java` 非 Application 代码到 `com.learning.springboot.springcoretx.part01_*` 等，保持 `SpringCoreTxApplication` 不变
- [√] 5.3 迁移 `spring-core-tx/src/test/java` 到 Part package，修复 import
- [√] 5.4 同步修复 `docs/tx/spring-core-tx/**` 中所有源码路径与包名引用
- [√] 5.5 验证：执行 `mvn -pl spring-core-tx test`

## 6. spring-core-validation
- [√] 6.1 调整 `docs/validation/spring-core-validation/`：补齐 `part-00-guide/00-deep-dive-guide.md`、`appendix/99-self-check.md`，并将章节移动到 `part-01-*`
- [√] 6.2 迁移 `spring-core-validation/src/main/java` 非 Application 代码到 `com.learning.springboot.springcorevalidation.part01_*` 等，保持 `SpringCoreValidationApplication` 不变
- [√] 6.3 迁移 `spring-core-validation/src/test/java` 到 Part package，修复 import
- [√] 6.4 同步修复 `docs/validation/spring-core-validation/**` 中所有源码路径与包名引用
- [√] 6.5 验证：执行 `mvn -pl spring-core-validation test`

## 7. Security Check
- [√] 7.1 安全自检：确认仅做结构性重构，不引入外部连接/敏感信息；避免在 docs 中出现错误路径导致误导

## 8. Knowledge Base Update（helloagents）
- [√] 8.1 更新 `helloagents/wiki/modules/spring-core-aop.md` / `spring-core-events.md` / `spring-core-profiles.md` / `spring-core-resources.md` / `spring-core-tx.md` / `spring-core-validation.md` 的“Source Layout / Docs Index / Change History”
- [√] 8.2 更新 `helloagents/CHANGELOG.md` 记录本次跨模块结构同步

## 9. Repository Consistency Audit
- [√] 9.1 全局检索残留旧路径引用（尤其是 docs 中的 `src/...` 路径、package 名称），确保仓库引用一致

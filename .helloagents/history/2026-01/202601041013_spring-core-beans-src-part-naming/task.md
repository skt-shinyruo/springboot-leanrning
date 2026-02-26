# Task List: spring-core-beans 源码分组命名语义化（part01_ioc_container）

Directory: `helloagents/plan/202601041013_spring-core-beans-src-part-naming/`

---

## 1. Refactor Packages and Directories
- [√] 1.1 将 `src/main/java` 的 `part01` 重命名为 `part01_ioc_container` 并同步修复 package/import，verify why.md#requirement-目录命名语义化与-docs-part-一致
- [√] 1.2 将 `src/test/java` 的 `part00/part01/part02/part03/part04` 分别重命名为 `part00_guide/part01_ioc_container/part02_boot_autoconfig/part03_container_internals/part04_wiring_and_boundaries` 并同步修复 package/import，verify why.md#requirement-目录命名语义化与-docs-part-一致
- [√] 1.3 确认 `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 包名不变，verify why.md#requirement-keep-application-package-stable

## 2. Docs / README / Knowledge Base Sync
- [√] 2.1 全量修复 `docs/beans/spring-core-beans/**` 中的源码路径引用（旧 partXX 路径 → 新 partXX_*** 路径），verify why.md#requirement-docs-路径引用一致性
- [√] 2.2 修复 `spring-core-beans/README.md` 中的源码路径引用与链接（保持中文规则），verify why.md#requirement-docs-路径引用一致性
- [√] 2.3 更新 `helloagents/wiki/modules/spring-core-beans.md` 的 Source Layout（与新命名一致），verify why.md#requirement-docs-路径引用一致性
- [√] 2.4 更新 `helloagents/CHANGELOG.md`（Changed）记录本次命名语义化调整

## 3. Security Check
- [√] 3.1 安全自检：不引入敏感信息/生产配置，不误改启动类包名，verify why.md#risk-assessment

## 4. Testing
- [√] 4.1 执行 `mvn -q -pl spring-core-beans test` 并记录结果

---

## Execution Notes

- Test result: `mvn -q -pl spring-core-beans test` ✅ 通过

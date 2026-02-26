# Technical Design: spring-core-* Part 化结构同步（对齐 spring-core-beans）

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.5.9（父 POM）
- Maven（多模块工程）
- JUnit（模块内现有测试框架）

### Implementation Key Points
1. **统一 docs 目录规范（对齐 spring-core-beans）**
   - 标准骨架：
     - `docs/README.md`：本模块文档索引与阅读入口
     - `docs/part-00-guide/00-deep-dive-guide.md`：阅读指南/全局路线图
     - `docs/part-01-*/`：主线章节（可按内容拆多个 Part）
     - `docs/appendix/`：常见坑/自检/扩展阅读
   - 对于已有 docs 的模块：以“移动/重命名”为主，不改动知识点本身
   - 对于缺失 docs 的模块（如 `spring-core-profiles`）：补齐最小“书本骨架”，并提供与现有 Labs/Exercises 相匹配的章节占位/索引

2. **统一 src(main/test) 的 Part 分组与命名**
   - 约束：`*Application.java` 保持原包名与路径不变（例如 `com.learning.springboot.springcoreaop.SpringCoreAopApplication`）
   - 新增/迁移 package 规则：
     - `com.learning.springboot.<module>.part01_<topic>`
     - `com.learning.springboot.<module>.part02_<topic>`（如需要）
     - `com.learning.springboot.<module>.appendix`
     - `com.learning.springboot.<module>.testsupport`（跨 Part 复用工具）
   - 命名策略：`<topic>` 对齐 docs 的 Part 目录名（`part-01-xxx` → `part01_xxx`）

3. **文档源码路径引用同步修复**
   - 目标：docs 中出现的“文件路径/包名/类名”必须与真实源码一致（硬性要求）
   - 处理方式：
     - 迁移后对每个模块执行 `rg` 检索旧路径（如 `src/test/java/com/learning/.../<module>/` 或旧 package）
     - 修复 docs 的引用路径与包名
     - 校验 docs 内部相对链接（Part 目录移动后容易断链）

4. **验证策略（逐模块回归）**
   - 每完成一个模块的迁移与引用修复，执行：
     - `mvn -pl <module> test`
   - 全部模块完成后，可选执行：
     - `mvn -pl spring-core-aop,spring-core-events,spring-core-profiles,spring-core-resources,spring-core-tx,spring-core-validation test`

## Security and Performance
- **Security:** 本次为结构重构，不引入外部服务、不处理敏感信息；重点防止路径引用错误导致“文档指向错误源码”的可信度问题
- **Performance:** 不涉及运行期性能调整；主要风险为重构引入的编译/测试失败，靠逐模块回归控制

## Testing and Deployment
- **Testing:** 以模块级 `mvn -pl <module> test` 为准，确保 Labs/Exercises 的“可复现性”不因移动而丢失
- **Deployment:** 无部署变更


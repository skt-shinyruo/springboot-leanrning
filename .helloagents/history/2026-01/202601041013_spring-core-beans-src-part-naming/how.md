# Technical Design: spring-core-beans 源码分组命名语义化（part01_ioc_container）

## Technical Solution

### Core Technologies
- Java + Maven
- Spring Framework / Spring Boot（测试启动与容器机制）
- JUnit（测试框架）

### Implementation Key Points
1. **目录与 package 同步重命名**：使用 `partXX_<topic>` 形式，保证顺序稳定且具语义。
2. **保留启动类包名**：`SpringCoreBeansApplication` 不移动、不改包名。
3. **全量引用修复**：覆盖以下三类引用：
   - Java import / package
   - docs 中的源码路径（`src/main/java...` / `src/test/java...`）
   - 模块 README 与知识库中的路径索引
4. **回归测试**：执行 `mvn -q -pl spring-core-beans test`。

### Target Package Layout
- `com.learning.springboot.springcorebeans`（保持）
  - `SpringCoreBeansApplication`（保持不动）
- `com.learning.springboot.springcorebeans.part01_ioc_container`（原 `part01`）
- `com.learning.springboot.springcorebeans.part02_boot_autoconfig`（原 `part02`，仅 test 为主）
- `com.learning.springboot.springcorebeans.part03_container_internals`（原 `part03`，仅 test 为主）
- `com.learning.springboot.springcorebeans.part04_wiring_and_boundaries`（原 `part04`，仅 test 为主）
- `com.learning.springboot.springcorebeans.part00_guide`（原 `part00`，仅 test 为主）
- `com.learning.springboot.springcorebeans.appendix`（保持）
- `com.learning.springboot.springcorebeans.testsupport`（保持）

### Mapping to docs
- `docs/part-00-guide/**` ⇔ `part00_guide/**`
- `docs/part-01-ioc-container/**` ⇔ `part01_ioc_container/**`
- `docs/part-02-boot-autoconfig/**` ⇔ `part02_boot_autoconfig/**`
- `docs/part-03-container-internals/**` ⇔ `part03_container_internals/**`
- `docs/part-04-wiring-and-boundaries/**` ⇔ `part04_wiring_and_boundaries/**`

## Security and Performance
- **Security:** 仅工程结构调整；不引入敏感信息与生产环境操作。
- **Performance:** 运行时无影响；主要是编译期与工程可维护性提升。

## Testing and Deployment
- **Testing:** `mvn -q -pl spring-core-beans test`
- **Deployment:** 无

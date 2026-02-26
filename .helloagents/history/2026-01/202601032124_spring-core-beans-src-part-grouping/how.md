# Technical Design: spring-core-beans 源码按 docs Part 分组

## Technical Solution

### Core Technologies
- Java + Maven（模块构建）
- Spring Framework / Spring Boot（容器与测试）
- JUnit（测试框架）

### Implementation Key Points
1. **包结构对齐 docs Part：** 在 `com.learning.springboot.springcorebeans` 根包下新增 Part 子包，使源码与文档 Part 形成稳定映射。
2. **保留应用启动类包名不变：** `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 必须保持原包名与路径不变。
3. **测试支撑抽取：** 将跨 Part 复用的测试工具类统一迁移到 `com.learning.springboot.springcorebeans.testsupport`，避免 package-private 在分包后不可见的问题。
4. **全量修复 docs 引用：** 通过全局搜索（旧路径前缀）与“类名 → 新路径”核对，修复 `docs/beans/spring-core-beans/**` 中所有源码路径引用。

### Target Package Layout (Proposed)
> 说明：以下是“按 Part 分组”的最小稳定结构；Part 内可继续按主题细分子包，但优先保持与 docs Part 的一一对应。

- `com.learning.springboot.springcorebeans`（保持）
  - `SpringCoreBeansApplication`（保持不动）
- `com.learning.springboot.springcorebeans.part00`（guide / 约定、导航性示例，必要时使用）
- `com.learning.springboot.springcorebeans.part01`（IoC container：bean/DI/scope/lifecycle/BPP 等）
- `com.learning.springboot.springcorebeans.part02`（Spring Boot auto-configuration）
- `com.learning.springboot.springcorebeans.part03`（container internals：启动、BDRPP/BPP 顺序等）
- `com.learning.springboot.springcorebeans.part04`（wiring & boundaries：lazy/dependsOn/hierarchy 等）
- `com.learning.springboot.springcorebeans.appendix`（附录类示例，必要时使用）
- `com.learning.springboot.springcorebeans.testsupport`（仅 test scope 复用：dumpers/markers/helpers）

### Mapping Rules
- docs 路径 `docs/beans/spring-core-beans/part-01-ioc-container/**` 对应到：
  - `src/main/java/.../part01/**`（如存在 demo）
  - `src/test/java/.../part01/**`（Labs/Exercises 主入口）
- 以此类推：`part-02` → `part02`，`part-03` → `part03`，`part-04` → `part04`，`appendix` → `appendix`

## Security and Performance
- **Security:** 仅重组目录/包结构；不引入生产环境配置、不处理敏感信息；避免在 main 代码中引入测试专用依赖。
- **Performance:** 对运行时性能无影响（主要是编译期与工程结构变更）；测试支撑类属于 test scope。

## Testing and Deployment
- **Testing:** 执行 `mvn -q -pl spring-core-beans test`；若有需要，增加对关键 Part 的冒烟验证（例如自动配置相关测试类）。
- **Deployment:** 无发布流程变更；仅代码组织与文档引用调整。

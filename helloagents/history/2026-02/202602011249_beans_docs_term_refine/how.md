# Technical Design: spring-core-beans 文档术语降噪（抽象标签 → 更直白表述）

## Technical Solution

### Strategy

以“按上下文选择更直白表述”为原则，避免一刀切替换：

- 描述“读者需要先抓住的核心结论/框架”时：优先用 **理解框架** / **一句话结论**。
- 描述“系统如何运作”时：优先用 **运行机制** / **主线机制**。
- 描述“入口怎么走”时：优先用 **入口理解**（避免“入口理解框架”这类口号化短语）。

### Scope Control

- 不修改文件路径与目录结构（避免断链）。
- 不引入新的固定模板；只对现有内容做“术语降噪 + 描述一致性回收”。

### Verification

1. **术语扫描**：确认 `spring-core-modules/spring-core-beans/docs/**` 不再出现口号化抽象标签。
2. **相对链接目标存在性**：对 beans docs 做相对链接目标存在性检查（仅检查文件是否存在）。
3. **Lab/Test 引用存在性**：抽取 `SpringCoreBeans*LabTest/Exercise*` 引用并校验对应 `.java` 文件存在。
4. **回归测试**：执行 `mvn -pl spring-core-modules/spring-core-beans test`，确保文档改动不影响回归入口（主要验证仓库处于健康状态）。

## Security and Compliance

- 文档变更不新增任何密钥/token/内网地址；外链保持为官方/源码仓库优先。

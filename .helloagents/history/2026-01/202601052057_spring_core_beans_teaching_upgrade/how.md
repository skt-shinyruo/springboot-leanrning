# Technical Design: spring-core-beans 教学闭环（源码级）全量深化

## Technical Solution

### Core Technologies
- Spring Framework / Spring Boot（本仓库既有）
- JUnit5（本仓库既有）
- docs 相对链接校验脚本：`scripts/check-md-relative-links.py`（本仓库既有）

### Implementation Key Points

1. **以 Lab 为锚点反推源码主线**
   - 每章先确认现象入口（已有 Lab 优先复用）
   - 在文档 Deep Dive 中补齐：
     - 源码主线调用链草图（入口 → 决策点 → 产物/副作用）
     - 关键分支/边界条件（触发条件 + 走向差异 + 观察点）
     - 断点入口与观察点（至少 2 个入口断点、3 个观察点）

2. **Exercise/Solution 设计策略（默认参与回归）**
   - Exercise 保持默认禁用（不破坏 CI）
   - 新增 Solution 测试套件：
     - 每个 Exercise 文件对应一个 Solution 文件（或按主题拆分多个 Solution）
     - Solution 默认参与回归，确保答案持续可验证
   - 参考答案尽量“最小改动、最小依赖”，避免引入外部环境敏感点

3. **索引与互链增强**
   - 在 `docs/beans/spring-core-beans/README.md` 增加“章节 ↔ Lab ↔ Exercise ↔ Solution”映射（以表格/列表形式）
   - 在每章结尾统一补“练习题指引”：指向相关 Exercise/ Solution（如果该章没有专属练习，则指向 Part 级练习集合）
   - 保持章节“上一章｜目录｜下一章”导航一致性，并修复相对链接

4. **分批交付与持续验证**
   - 按 Part 分批推进：Part01 → Part02 → Part03 → Part04 → Part05 → Appendix/全局索引收敛
   - 每批次必跑：
     - `mvn -pl spring-core-beans test`
     - `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

## Architecture Design

无架构级改动（仅教学文档与测试入口增强）。

## Security and Performance

- **Security:** 不接入外部服务；不引入明文密钥；不进行生产环境操作。
- **Performance:** Solution 默认参与回归会增加测试时间；通过复用现有 Lab/最小实现控制增量，并避免引入慢的全量启动型测试。

## Testing and Deployment

### Testing
- 模块回归：`mvn -pl spring-core-beans test`
- 单点验证：按章节对应 Lab/Solution 运行单测（在文档中给出命令）
- docs 链接检查：`python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

### Deployment
无部署步骤（仓库内容更新 + CI 回归）。

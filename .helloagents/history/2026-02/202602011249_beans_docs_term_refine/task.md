# Task List: spring-core-beans 文档术语降噪（抽象标签 → 更直白表述）

Directory: `helloagents/history/2026-02/202602011249_beans_docs_term_refine/`

---

## 1. Docs Update（spring-core-beans）

- [√] 1.1 扫描并替换 `spring-core-modules/spring-core-beans/docs/**` 中的抽象标签式表述（按上下文改为理解框架/运行机制/一句话结论/入口理解等）
- [√] 1.2 同步更新 beans docs 的导航/索引中对相关章节的命名描述（README/知识地图/上下章跳转），保持一致性
- [√] 1.3 调整 beans docs 内对 AOP 章节的引用文本（保留链接目标，降低口号化抽象措辞）

## 2. Quality Verification

- [√] 2.1 术语残留自检：`spring-core-modules/spring-core-beans/docs/**` 不再出现口号化抽象标签
- [√] 2.2 相对链接目标存在性自检（beans docs）
- [√] 2.3 Lab/Test 引用存在性自检（beans docs）

## 3. Security Check

- [√] 3.1 安全自检：确认新增/修改内容不包含密钥/token/个人信息

## 4. Verification（回归）

- [√] 4.1 运行 `mvn -pl spring-core-modules/spring-core-beans test`

## 5. Knowledge Base Sync

- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-beans.md`（记录本次“术语降噪”变更）
- [√] 5.2 更新 `helloagents/CHANGELOG.md`
- [√] 5.3 更新 `helloagents/history/index.md`

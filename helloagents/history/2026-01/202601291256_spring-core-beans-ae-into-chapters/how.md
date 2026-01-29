# Technical Design: spring-core-beans 将 A–E 策略写入各章节正文

## Technical Solution

### Core Technologies
- Markdown（模块文档）
- Maven + Surefire（回归验证：`mvn -pl :spring-core-beans test`）

### Implementation Key Points

#### 1) 插入位置与形态（最小侵入）

- 每章在 `## 机制主线` 之前插入一个“内容级再加深（A–E 维度）”提示块：
  - 读者在进入主线前就能获得“下一步怎么深挖”的方向
  - 不打断章节已有叙事；避免把策略散落到多处
- 使用显式标记保证幂等：
  - `<!-- AE-DEEPENING:START -->`
  - `<!-- AE-DEEPENING:END -->`

#### 2) 内容来源与差异化（不做统一模板填空）

- 以 `spring-core-beans/docs/deepening-strategies/*.md` 为“策略输入”，将每个章节对应的 A–E 加深点映射到该章节正文提示块中。
- 对已具备 A–E 的章节：提示块只做“收敛与强化”（证据链入口/反例/断点组/追问），不重复改写正文段落。
- 对相对薄弱的章节：提示块提供更具体的“证据链入口方法 + 反例 + 排障第一断点”指引，后续可再做二次落地补写。

#### 3) 质量与一致性

- 不改变现有章节结构（导读/实验入口/机制主线/排障/面试/自检/BOOKIFY）。
- 新增内容统一使用中文描述；路径与代码标识保持原样。

## Security and Performance

- **Security:** 文档增量不引入可执行代码；对涉及 SpEL/反射/AOT 的章节保持“最小暴露面”提醒，不鼓励不受控表达式求值与过度反射放开。
- **Performance:** 文档改动不影响运行时；测试仅做回归验证。

## Testing and Deployment

- **Testing:** `mvn -pl :spring-core-beans test`
- **Deployment:** 无（文档/知识库变更）


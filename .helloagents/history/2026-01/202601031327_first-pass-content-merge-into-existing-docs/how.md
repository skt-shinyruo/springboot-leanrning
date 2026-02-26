# Technical Design: Merge First Pass Content into Existing Docs (00/99)

## Technical Solution

### Implementation Key Points
1. **只融入，不新增文件**
   - 不再创建独立的 “First Pass” 文档文件，避免出现重复编号与导航噪声。
2. **最小信息量原则**
   - `00`：补一个可选的 10 步清单，强调“入口 + 你要写的结论”，不做重复讲解。
   - `99`：补一个同样的入口索引，用于自测闭环。
3. **一致性**
   - 所有 test 名称保持与仓库实际存在的 Lab/Test 一致。

## Testing
- `mvn -q -pl spring-core-beans test`


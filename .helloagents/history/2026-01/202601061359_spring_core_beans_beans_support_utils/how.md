# Technical Design: spring-core-beans `org.springframework.beans.support` 补齐（docs + Lab + 索引可审计）

## Technical Solution

### Core Technologies

- Spring Framework `spring-beans`（已有）
- 测试：JUnit 5 + AssertJ（已有）
- 最小容器：`AnnotationConfigApplicationContext` / `RootBeanDefinition`（用于复现 property population 与内置 editor 注册）

### Implementation Key Points

1. **文档落位（避免新增编号噪声）**
   - 在 `docs/36`（类型转换链路）新增 `org.springframework.beans.support` 小节
   - 以“support 工具类如何复用 BeanWrapper/TypeConverter/PropertyEditor”作为统一主线
2. **最小可运行 Lab（可断言 + 可断点）**
   - `ArgumentConvertingMethodInvoker`：验证 String 参数可转换后匹配方法并成功调用；额外覆盖 custom PropertyEditor 注册路径
   - `ResourceEditorRegistrar`：验证在 ApplicationContext refresh 后，String → `Resource` 能在属性填充阶段转换成功
   - `PropertyComparator`：验证嵌套属性路径排序、ignoreCase、以及“读不到属性→按 null 处理”的边界
   - `PagedListHolder` + `MutableSortDefinition`：验证 `resort()` 的生效条件、分页边界、以及 toggle 升降序语义
3. **索引可审计（Appendix 95/96）**
   - 调整 `scripts/generate-spring-beans-public-api-index.py`：将 `org.springframework.beans.support` 标记为 `core`，并把主 Lab 指向新增的 support utilities Lab
   - 运行脚本重新生成 Appendix 95/96，确保 Gap 为 0 且 chapter/lab 路径存在性校验通过

## Architecture Decision ADR

### ADR-001: 不新增独立章节编号，改为在 docs/36 补齐 support 小节

**Context:** support 工具类与类型转换/BeanWrapper 机制紧耦合，但新增独立章节会引入编号与导航噪声。  
**Decision:** 在 `docs/36` 内增补 `org.springframework.beans.support` 小节，并以新增 Lab 作为复现入口。  
**Rationale:** 学习主线更连贯；索引规则仍可指向同一章节；变更更小且易维护。  
**Impact:** 章节内容更完整；Appendix 95/96 归零后可审计完成状态。  

## Testing and Verification

- 必跑：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansBeansSupportUtilitiesLabTest test`
- 回归建议：`mvn -pl spring-core-beans test`
- 索引生成：`python3 scripts/generate-spring-beans-public-api-index.py`（生成文件禁止手工编辑）


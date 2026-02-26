# Change Proposal: 移除 docs 强制检查与相关引用

## Requirement Background
当前仓库包含一组用于文档/站点构建检查的脚本与 GitHub Actions 流程，并且在 README、docs、知识库与历史记录中存在对应说明与调用方式。需求要求彻底移除这些检查脚本、工作流以及所有引用说明。

## Change Content
1. 移除 `scripts/` 下与文档检查、站点构建/预览相关的脚本。
2. 移除 `.github/workflows/` 下与 docs-site 发布/构建相关的流程文件或步骤。
3. 清理 `README.md`、`docs/**`、`helloagents/wiki/**`、`helloagents/history/**` 中相关说明与引用。

## Impact Scope
- **Modules:** scripts / docs / docs-site / .github/workflows / helloagents
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 脚本与流程移除
**Module:** scripts / .github/workflows
删除相关脚本与工作流文件，仓库不再提供对应入口。

#### Scenario: 无引用残留
仓库文本中不再出现已删除脚本/流程的调用指引。

### Requirement: 文档与知识库清理
**Module:** docs / helloagents
清理所有相关说明与引用，避免读者按旧指引执行。

#### Scenario: 文本清理完成
README、docs、知识库与历史记录中均无相关描述。

## Risk Assessment
- **Risk:** 移除检查后，文档一致性问题更依赖人工发现
- **Mitigation:** 需求已明确，后续如需新的质量策略再单独定义

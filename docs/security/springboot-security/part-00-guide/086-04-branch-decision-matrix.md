# 第 86 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把 Security 的关键分支（多 filter chain、profile 差异）写成矩阵表；每行都能被测试复现并用断点定位。
    - 原理：分支本质是“请求匹配 → chain 选择 → filter 顺序 → 鉴权决策”。
    - 源码入口：`FilterChainProxy` / `SecurityFilterChain`
    - 推荐 Lab：`BootSecurityBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 86 章：02：断点地图（Security Debugger Pack）](086-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 87 章：01：Basic Auth 与授权（最小可跑主线）](../part-01-security/087-01-basic-auth-and-authorization.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 多 chain 选择 | 多个 `SecurityFilterChain` 同时存在 | 命中顺序正确（更具体的先匹配） | `BootSecurityMultiFilterChainOrderLabTest` | `FilterChainProxy#getFilters` |
| Profile 差异 | `dev` profile 激活 | dev 配置生效（允许/放宽策略） | `BootSecurityDevProfileLabTest` | activeProfiles / bean 注册 |
| 默认主线 | 默认 profile | 默认授权策略生效 | `BootSecurityLabTest` | authentication / status |

## 推荐运行命令

- `mvn -q -pl :springboot-security -Dtest=BootSecurityBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/092-90-common-pitfalls.md`](../appendix/092-90-common-pitfalls.md)
- 自检：[`../appendix/093-99-self-check.md`](../appendix/093-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootSecurityBranchMatrixLabTest`
- Lab：`BootSecurityMultiFilterChainOrderLabTest` / `BootSecurityDevProfileLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](086-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-security/01-basic-auth-and-authorization.md](../part-01-security/087-01-basic-auth-and-authorization.md)

<!-- BOOKIFY:END -->


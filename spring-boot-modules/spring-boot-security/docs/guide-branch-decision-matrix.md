# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：分支本质是“请求匹配 → chain 选择 → filter 顺序 → 鉴权决策”。

    把 Security 的关键分支（多 filter chain、profile 差异）写成矩阵表；每行都能被测试复现并用断点定位。

    对照入口：`BootSecurityBranchMatrixLabTest`。需要下探源码时，可以从 `FilterChainProxy` / `SecurityFilterChain` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Security）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 401 vs 403：Basic Auth 与授权规则](security-basic-auth-and-authorization.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootSecurityBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：分支本质是“请求匹配 → chain 选择 → filter 顺序 → 鉴权决策”。需要下探源码时，可以从 `FilterChainProxy` / `SecurityFilterChain` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 多 chain 选择 | 多个 `SecurityFilterChain` 同时存在 | 命中顺序正确（更具体的先匹配） | `BootSecurityMultiFilterChainOrderLabTest` | `FilterChainProxy#getFilters` |
| Profile 差异 | `dev` profile 激活 | dev 配置生效（允许/放宽策略） | `BootSecurityDevProfileLabTest` | activeProfiles / bean 注册 |
| 默认主线 | 默认 profile | 默认授权策略生效 | `BootSecurityLabTest` | authentication / status |

## 运行命令

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

分支本质是“请求匹配 → chain 选择 → filter 顺序 → 鉴权决策”。

下一章见：[01：Basic Auth 与授权（最小可跑主线）](security-basic-auth-and-authorization.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootSecurityBranchMatrixLabTest`
- Lab：`BootSecurityMultiFilterChainOrderLabTest` / `BootSecurityDevProfileLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[security-basic-auth-and-authorization.md](security-basic-auth-and-authorization.md)

<!-- BOOKIFY:END -->


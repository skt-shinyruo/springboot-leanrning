# 03. Security 调用链（FilterChainProxy → Authentication → Authorization）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Security 调用链（FilterChainProxy → Authentication → Authorization）展开，主线可以概括为：Spring Security 的核心在 FilterChain：请求先过 `FilterChainProxy`，匹配具体 SecurityFilterChain，再做认证与鉴权决策。

    先跑 `BootSecurityLabTest`，把“未认证/已认证/拒绝访问”等分支固化成断言，再按本章把 FilterChain 到最终决策串起来。

    需要下探源码时，可以从 `FilterChainProxy` / `SecurityFilterChain` / `AuthenticationManager` / `AuthorizationManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Security](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Security）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootSecurityLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：Spring Security 的核心在 FilterChain：请求先过 `FilterChainProxy`，匹配具体 SecurityFilterChain，再做认证与鉴权决策。需要下探源码时，可以从 `FilterChainProxy` / `SecurityFilterChain` / `AuthenticationManager` / `AuthorizationManager` 这些入口切入。


## 最短调用链

1. 请求进入 `FilterChainProxy`
2. 匹配到某个 `SecurityFilterChain`
3. 认证（Authentication）：把请求变成 principal（或失败）
4. 鉴权（Authorization）：判断是否允许访问资源
5. 继续进入 MVC（或直接返回 401/403）

证据链入口：

- `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`

## 小结与下一章

Spring Security 的核心在 FilterChain：请求先过 `FilterChainProxy`，匹配具体 SecurityFilterChain，再做认证与鉴权决策。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootSecurityLabTest`
- Lab：`BootSecurityMultiFilterChainOrderLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->

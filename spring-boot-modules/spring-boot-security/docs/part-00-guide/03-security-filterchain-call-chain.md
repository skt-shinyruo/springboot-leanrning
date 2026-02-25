# 03. Security 调用链（FilterChainProxy → Authentication → Authorization）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Security 调用链（FilterChainProxy → Authentication → Authorization）
    - 怎么使用：先跑 `BootSecurityLabTest`，把“未认证/已认证/拒绝访问”等分支固化成断言，再按本文把 FilterChain 到最终决策串起来。
    - 原理：Spring Security 的核心在 FilterChain：请求先过 `FilterChainProxy`，匹配具体 SecurityFilterChain，再做认证与鉴权决策。
    - 源码入口：`FilterChainProxy` / `SecurityFilterChain` / `AuthenticationManager` / `AuthorizationManager`
    - 推荐 Lab：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-security）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Security Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Security 调用链（FilterChainProxy → Authentication → Authorization）**
- 建议入口：优先运行 `BootSecurityLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：Spring Security 的核心在 FilterChain：请求先过 `FilterChainProxy`，匹配具体 SecurityFilterChain，再做认证与鉴权决策。
- 源码入口：`FilterChainProxy` / `SecurityFilterChain` / `AuthenticationManager` / `AuthorizationManager`



## 最短调用链

1. 请求进入 `FilterChainProxy`
2. 匹配到某个 `SecurityFilterChain`
3. 认证（Authentication）：把请求变成 principal（或失败）
4. 鉴权（Authorization）：判断是否允许访问资源
5. 继续进入 MVC（或直接返回 401/403）

证据链入口：

- `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`

## 小结与下一章

- 小结：Spring Security 的核心在 FilterChain：请求先过 `FilterChainProxy`，匹配具体 SecurityFilterChain，再做认证与鉴权决策。
- 下一章：[第 86 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityLabTest`
- Lab：`BootSecurityMultiFilterChainOrderLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->

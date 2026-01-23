# 第 174 章：01：Web Client 调用链（RestClient/WebClient：过滤器/拦截器在哪里生效）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Web Client 调用链（RestClient/WebClient：过滤器/拦截器在哪里生效）
    - 怎么使用：先跑 `BootWebClientWebClientLabTest` 与 `BootWebClientRestClientLabTest`，把“请求经过 filter/interceptor”固化成断言，再按本文串起调用链。
    - 原理：客户端也有链路：builder 组装 filter/interceptor → exchange/execute → 底层 connector/HttpClient；排障关键是定位“链上哪个环节改写了请求/响应”。
    - 源码入口：（WebClient）`ExchangeFilterFunction` / `ExchangeFunction` /（RestClient）interceptors /（底层）connector
    - 推荐 Lab：`BootWebClientWebClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 174 章：00. 深挖导读](174-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 174 章：02：断点地图](174-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链

### 1) WebClient（响应式）

1. builder 组装 `ExchangeFilterFunction`
2. 触发 request → `ExchangeFunction#exchange`
3. filters 按顺序包裹执行（像 AOP 的 around）
4. connector 发起真实请求

### 2) RestClient（同步）

1. builder 组装 interceptors
2. execute 时依次执行 interceptors
3. request factory 发起真实请求

证据链入口：

- `BootWebClientWebClientLabTest` / `BootWebClientRestClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientWebClientLabTest`
- Lab：`BootWebClientRestClientLabTest`
- Lab：`BootWebClientWebClientFilterOrderLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](174-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](174-02-breakpoint-map.md)

<!-- BOOKIFY:END -->

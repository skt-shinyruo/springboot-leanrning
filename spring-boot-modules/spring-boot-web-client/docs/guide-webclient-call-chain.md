# 03. Web Client 调用链（RestClient/WebClient：过滤器/拦截器在哪里生效）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Web Client 调用链（RestClient/WebClient：过滤器/拦截器在哪里生效）展开，主线可以概括为：客户端也有链路：builder 组装 filter/interceptor → exchange/execute → 底层 connector/HttpClient；排障关键是定位“链上哪个环节改写了请求/响应”。

    先跑 `BootWebClientWebClientLabTest` 与 `BootWebClientRestClientLabTest`，把“请求经过 filter/interceptor”固化成断言，再按本章串起调用链。

    需要下探源码时，可以从 （WebClient）`ExchangeFilterFunction` / `ExchangeFunction` /（RestClient）interceptors /（底层）connector 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Web Client](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Web Client）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootWebClientWebClientLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：客户端也有链路：builder 组装 filter/interceptor → exchange/execute → 底层 connector/HttpClient；排障关键是定位“链上哪个环节改写了请求/响应”。需要下探源码时，可以从 （WebClient）`ExchangeFilterFunction` / `ExchangeFunction` /（RestClient）interceptors /（底层）connector 这些入口切入。


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

## 小结与下一章

客户端也有链路：builder 组装 filter/interceptor → exchange/execute → 底层 connector/HttpClient；排障关键是定位“链上哪个环节改写了请求/响应”。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebClientWebClientLabTest`
- Lab：`BootWebClientRestClientLabTest`
- Lab：`BootWebClientWebClientFilterOrderLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->

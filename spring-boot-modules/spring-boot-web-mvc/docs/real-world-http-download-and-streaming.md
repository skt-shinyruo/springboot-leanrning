# 03. 下载与 Header（Content-Disposition / Content-Type / bytes）

## 导读

本章围绕「03：下载与 Header（Content-Disposition / Content-Type / bytes）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `BootWebMvcRealWorldHttpLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线

- 本章在 `BootWebMvcRealWorldHttpLabTest` 中固定：上传 `hello.txt` → 下载 → 断言 header 与 bytes 一致。

## 源码与断点

断点入口：
- `org.springframework.http.converter.ByteArrayHttpMessageConverter#writeInternal`
- `org.springframework.http.HttpHeaders#setContentDisposition`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`

## 常见坑与边界

- 不要把下载写成“返回 base64 字符串”：那是另一种契约，且更难复用浏览器行为。

## 小结与下一章

- 下一章进入静态资源：Spring Boot 如何从 `static/` 提供资源，以及如何验证它真的可用。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[02. Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）](real-world-http-multipart-upload.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 静态资源与缓存（Static Resources / Cache-Control）](real-world-http-static-resources-and-cache.md)
<!-- BOOKIFY:END -->

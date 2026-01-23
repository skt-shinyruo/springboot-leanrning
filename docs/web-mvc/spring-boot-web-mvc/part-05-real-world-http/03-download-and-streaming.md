# 03：下载与 Header（Content-Disposition / Content-Type / bytes）

## 导读

- 本章主题：**03：下载与 Header（Content-Disposition / Content-Type / bytes）**
- 目标：把“文件下载为什么在浏览器里表现不一致”的问题，收敛为几个可测试的 header。

!!! summary "本章要点"

    - 下载是否触发“另存为”，核心看 `Content-Disposition: attachment; filename="..."`。
    - `Content-Type` 决定浏览器如何解释 bytes；缺失时通常退化为 `application/octet-stream`。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线

- 本章在 `BootWebMvcRealWorldHttpLabTest` 中固定：上传 `hello.txt` → 下载 → 断言 header 与 bytes 一致。

## 源码与断点

建议断点：
- `org.springframework.http.converter.ByteArrayHttpMessageConverter#writeInternal`
- `org.springframework.http.HttpHeaders#setContentDisposition`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`

## 常见坑与边界

- 不要把下载写成“返回 base64 字符串”：那是另一种契约，且更难复用浏览器行为。

## 小结与下一章

- 下一章进入静态资源：Spring Boot 如何从 `static/` 提供资源，以及如何验证它真的可用。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[part-05-real-world-http/02-multipart-upload.md](02-multipart-upload.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-05-real-world-http/04-static-resources-and-cache.md](04-static-resources-and-cache.md)

<!-- BOOKIFY:END -->

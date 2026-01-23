package com.learning.springboot.bootwebmvc.part05_real_world;

// 本类用于演示 ETag/条件请求：If-None-Match 命中时返回 304（不返回响应体）。

import java.nio.charset.StandardCharsets;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/cache")
public class ApiEtagDemoController {

    @GetMapping(value = "/etag", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> etagDemo(
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch
    ) {
        String body = "hello-etag";
        String etag = "\"" + DigestUtils.md5DigestAsHex(body.getBytes(StandardCharsets.UTF_8)) + "\"";

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .eTag(etag)
                .body(body);
    }

    /**
     * 对照：不在 controller 中显式设置 ETag，而是让 ShallowEtagHeaderFilter 计算并处理 304。
     */
    @GetMapping(value = "/filter-etag", produces = MediaType.TEXT_PLAIN_VALUE)
    public String filterEtag() {
        return "hello-filter-etag";
    }
}

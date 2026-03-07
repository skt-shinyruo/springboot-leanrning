package com.learning.springboot.bootwebmvc.part03_internals;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/internals")
public class WebMvcInternalsController {

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI(
            @ClientIp String clientIp,
            @RequestHeader("User-Agent") String userAgent
    ) {
        return Map.of(
                "clientIp", clientIp,
                "userAgent", userAgent
        );
    }

    @GetMapping("/request-info")
    public Map<String, Object> requestInfo(HttpServletRequest request) {
        return Map.of(
                "scheme", request.getScheme(),
                "secure", request.isSecure(),
                "serverName", request.getServerName(),
                "serverPort", request.getServerPort(),
                "contextPath", request.getContextPath(),
                "requestUri", request.getRequestURI(),
                "requestUrl", request.getRequestURL().toString(),
                "remoteAddr", request.getRemoteAddr()
        );
    }
}

package com.learning.springboot.bootwebmvc.part03_internals;

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
}


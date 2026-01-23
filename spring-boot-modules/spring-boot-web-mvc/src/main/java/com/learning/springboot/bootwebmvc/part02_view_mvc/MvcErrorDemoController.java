package com.learning.springboot.bootwebmvc.part02_view_mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcErrorDemoController {

    @GetMapping("/pages/error-demo")
    public String errorDemo() {
        throw new IllegalStateException("demo_error");
    }
}

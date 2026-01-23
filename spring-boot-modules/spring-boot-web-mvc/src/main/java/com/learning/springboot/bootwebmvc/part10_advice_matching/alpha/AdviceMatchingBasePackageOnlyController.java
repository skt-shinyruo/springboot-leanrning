package com.learning.springboot.bootwebmvc.part10_advice_matching.alpha;

import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingDemoException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/advice-matching")
public class AdviceMatchingBasePackageOnlyController {

    @GetMapping("/base-package-only")
    public void basePackageOnly() {
        throw new AdviceMatchingDemoException("boom");
    }
}

package com.learning.springboot.bootwebmvc.part10_advice_matching.alpha;

import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingDemoException;
import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingTagged;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AdviceMatchingTagged
@RequestMapping("/api/advanced/advice-matching")
public class AdviceMatchingCompositeController {

    @GetMapping("/composite")
    public void composite() {
        throw new AdviceMatchingDemoException("boom");
    }
}


package com.learning.springboot.bootwebmvc.part10_advice_matching.controllers;

import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingDemoException;
import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingTagged;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AdviceMatchingTagged
@RequestMapping("/api/advanced/advice-matching")
public class AdviceMatchingAnnotationsOnlyController {

    @GetMapping("/annotations-only")
    public void annotationsOnly() {
        throw new AdviceMatchingDemoException("boom");
    }
}


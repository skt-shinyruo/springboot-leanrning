package com.learning.springboot.bootwebmvc.part10_advice_matching.controllers;

import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingDemoException;
import com.learning.springboot.bootwebmvc.part10_advice_matching.AdviceMatchingMarker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/advice-matching")
public class AdviceMatchingAssignableOnlyController implements AdviceMatchingMarker {

    @GetMapping("/assignable-only")
    public void assignableOnly() {
        throw new AdviceMatchingDemoException("boom");
    }
}


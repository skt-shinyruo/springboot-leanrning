package com.learning.springboot.springcoreaopweaving.testsupport;

import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import com.learning.springboot.springcoreaopweaving.support.JoinPointEvent;
import java.util.List;

public final class InvocationLogAssertions {

    private InvocationLogAssertions() {
    }

    public static List<JoinPointEvent> byMode(InvocationLog log, String mode) {
        return log.events().stream()
                .filter(event -> event.mode().equals(mode))
                .toList();
    }

    public static List<JoinPointEvent> byAdvice(InvocationLog log, String advice) {
        return log.events().stream()
                .filter(event -> event.advice().equals(advice))
                .toList();
    }

    public static List<JoinPointEvent> byAdvicePrefix(InvocationLog log, String advicePrefix) {
        return log.events().stream()
                .filter(event -> event.advice().startsWith(advicePrefix))
                .toList();
    }
}

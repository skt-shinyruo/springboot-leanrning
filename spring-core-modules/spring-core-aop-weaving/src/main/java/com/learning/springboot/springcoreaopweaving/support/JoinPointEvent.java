package com.learning.springboot.springcoreaopweaving.support;

import java.util.Objects;
import org.aspectj.lang.JoinPoint;

public record JoinPointEvent(
        String mode,
        String advice,
        String kind,
        String signature,
        String location,
        String threadName
) {

    public static JoinPointEvent from(String mode, String advice, JoinPoint joinPoint) {
        Objects.requireNonNull(mode, "mode must not be null");
        Objects.requireNonNull(advice, "advice must not be null");
        Objects.requireNonNull(joinPoint, "joinPoint must not be null");

        String kind = joinPoint.getStaticPart().getKind();
        String signature = joinPoint.getSignature().toShortString();
        String location = joinPoint.getSourceLocation() == null ? "" : joinPoint.getSourceLocation().toString();
        String threadName = Thread.currentThread().getName();
        return new JoinPointEvent(mode, advice, kind, signature, location, threadName);
    }
}

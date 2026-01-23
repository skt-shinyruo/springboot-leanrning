package com.learning.springboot.bootbusinesscase.tracing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private final InvocationLog invocationLog;

    public TracingAspect(InvocationLog invocationLog) {
        this.invocationLog = invocationLog;
    }

    @Around("@annotation(com.learning.springboot.bootbusinesscase.tracing.TracedOperation)")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long startNs = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
            invocationLog.record(method);
            System.out.println("traced: " + method + " took " + elapsedMs + "ms");
        }
    }
}


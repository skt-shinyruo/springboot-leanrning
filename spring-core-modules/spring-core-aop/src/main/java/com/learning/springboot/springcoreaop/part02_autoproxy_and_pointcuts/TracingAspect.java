package com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts;

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

    @Around("@annotation(com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced)")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long startNs = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;
            invocationLog.record(method);
            System.out.println("AOP traced: " + method + " took " + elapsedMs + "ms");
        }
    }
}


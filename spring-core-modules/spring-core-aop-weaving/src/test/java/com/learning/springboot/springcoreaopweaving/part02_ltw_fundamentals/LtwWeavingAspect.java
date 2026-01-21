package com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals;

import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import com.learning.springboot.springcoreaopweaving.support.JoinPointEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LtwWeavingAspect {

    private static final String MODE = "LTW";

    private final InvocationLog invocationLog = InvocationLog.getInstance();

    @Before("execution(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwPlainCalculator.add(..))")
    public void onPlainExecution(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "method-execution:plain-add", joinPoint));
    }

    @Before("execution(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwSelfInvocationTarget.*(..))")
    public void onSelfInvocationExecution(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "method-execution:self-invocation", joinPoint));
    }

    @Before("call(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwCallVsExecutionTarget.callee(..))")
    public void onCallCallee(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "method-call:callee", joinPoint));
    }

    @Before("execution(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwCallVsExecutionTarget.callee(..))")
    public void onExecutionCallee(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "method-execution:callee", joinPoint));
    }

    @Before("call(com.learning.springboot.springcoreaopweaving.ltwtargets.LtwConstructorTarget.new(..))")
    public void onConstructorCall(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "constructor-call", joinPoint));
    }

    @Before("execution(com.learning.springboot.springcoreaopweaving.ltwtargets.LtwConstructorTarget.new(..))")
    public void onConstructorExecution(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "constructor-execution", joinPoint));
    }

    @Before("set(int com.learning.springboot.springcoreaopweaving.ltwtargets.LtwFieldAccessTarget.value)")
    public void onFieldSet(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "field-set", joinPoint));
    }

    @Before("get(int com.learning.springboot.springcoreaopweaving.ltwtargets.LtwFieldAccessTarget.value)")
    public void onFieldGet(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "field-get", joinPoint));
    }

    @Before("call(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwWithincodeTarget.callee(..))"
            + " && withincode(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwWithincodeTarget.callerA(..))")
    public void onWithincodeCall(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "withincode:call-callee-from-callerA", joinPoint));
    }

    @Before("execution(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwCflowTarget.deep(..))"
            + " && cflow(execution(* com.learning.springboot.springcoreaopweaving.ltwtargets.LtwCflowTarget.entry(..)))")
    public void onCflowExecution(JoinPoint joinPoint) {
        invocationLog.record(JoinPointEvent.from(MODE, "cflow:execution-deep-under-entry", joinPoint));
    }
}

package com.learning.springboot.springcoreaopweaving.part03_ctw_fundamentals;

import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import com.learning.springboot.springcoreaopweaving.support.JoinPointEvent;

public aspect CtwWeavingAspect {

    private static final String MODE = "CTW";

    before(): call(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCallVsExecutionTarget.callee(..)) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "method-call:callee", thisJoinPoint));
    }

    before(): execution(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCallVsExecutionTarget.callee(..)) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "method-execution:callee", thisJoinPoint));
    }

    before(): execution(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwSelfInvocationTarget.*(..)) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "method-execution:self-invocation", thisJoinPoint));
    }

    before(): call(com.learning.springboot.springcoreaopweaving.ctwtargets.CtwConstructorTarget.new(..)) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "constructor-call", thisJoinPoint));
    }

    before(): execution(com.learning.springboot.springcoreaopweaving.ctwtargets.CtwConstructorTarget.new(..)) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "constructor-execution", thisJoinPoint));
    }

    before(): set(int com.learning.springboot.springcoreaopweaving.ctwtargets.CtwFieldAccessTarget.value) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "field-set", thisJoinPoint));
    }

    before(): get(int com.learning.springboot.springcoreaopweaving.ctwtargets.CtwFieldAccessTarget.value) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "field-get", thisJoinPoint));
    }

    before(): call(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwWithincodeTarget.callee(..))
            && withincode(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwWithincodeTarget.callerA(..)) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "withincode:call-callee-from-callerA", thisJoinPoint));
    }

    before(): execution(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCflowTarget.deep(..))
            && cflow(execution(* com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCflowTarget.entry(..))) {
        InvocationLog.getInstance().record(JoinPointEvent.from(MODE, "cflow:execution-deep-under-entry", thisJoinPoint));
    }
}

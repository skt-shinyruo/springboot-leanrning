package com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals;

import static com.learning.springboot.springcoreaopweaving.testsupport.InvocationLogAssertions.byAdvice;
import static com.learning.springboot.springcoreaopweaving.testsupport.InvocationLogAssertions.byAdvicePrefix;
import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwCallVsExecutionTarget;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwCflowTarget;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwConstructorTarget;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwFieldAccessTarget;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwPlainCalculator;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwSelfInvocationTarget;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwWithincodeTarget;
import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import java.lang.management.ManagementFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AspectjLtwLabTest {
    @BeforeAll
    static void requireJavaAgent() {
        boolean hasJavaAgent = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("-javaagent:"));

        Assumptions.assumeTrue(
                hasJavaAgent,
                "LTW 测试需要 -javaagent（AspectJ Weaver）。请通过 Maven Surefire 的 ltw-tests-with-javaagent 执行，或直接运行 mvn test。"
        );
    }

    private final InvocationLog invocationLog = InvocationLog.getInstance();

    @BeforeEach
    void setUp() {
        invocationLog.reset();
    }

    @Test
    void ltw_testJvmIsStartedWithJavaAgent() {
        String inputArgs = String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments());
        assertThat(inputArgs).contains("-javaagent:");
        assertThat(inputArgs).contains("aspectjweaver.jar");
    }

    @Test
    void ltw_canWeaveExecutionForNonSpringObjects() {
        int result = new LtwPlainCalculator().add(1, 2);

        assertThat(result).isEqualTo(3);
        assertThat(byAdvice(invocationLog, "method-execution:plain-add")).hasSize(1);
        assertThat(byAdvice(invocationLog, "method-execution:plain-add").get(0).kind()).isEqualTo("method-execution");
    }

    @Test
    void ltw_selfInvocationDoesNotBypassWeaving() {
        String result = new LtwSelfInvocationTarget().outer("Bob");

        assertThat(result).contains("outer->inner:Bob");
        assertThat(byAdvice(invocationLog, "method-execution:self-invocation")).hasSize(2);
        assertThat(byAdvice(invocationLog, "method-execution:self-invocation").get(0).signature()).contains("outer");
        assertThat(byAdvice(invocationLog, "method-execution:self-invocation").get(1).signature()).contains("inner");
    }

    @Test
    void ltw_callVsExecution_areDifferentJoinPointKinds() {
        int result = new LtwCallVsExecutionTarget().caller(1);

        assertThat(result).isEqualTo(12);

        assertThat(byAdvice(invocationLog, "method-call:callee")).hasSize(1);
        assertThat(byAdvice(invocationLog, "method-execution:callee")).hasSize(1);

        assertThat(byAdvice(invocationLog, "method-call:callee").get(0).kind()).isEqualTo("method-call");
        assertThat(byAdvice(invocationLog, "method-execution:callee").get(0).kind()).isEqualTo("method-execution");
    }

    @Test
    void ltw_constructorCallAndExecution_canBeIntercepted() {
        LtwConstructorTarget target = new LtwConstructorTarget("x");

        assertThat(target.name()).isEqualTo("x");
        assertThat(byAdvice(invocationLog, "constructor-call")).hasSize(1);
        assertThat(byAdvice(invocationLog, "constructor-execution")).hasSize(1);
    }

    @Test
    void ltw_fieldGetAndSet_canBeIntercepted() {
        LtwFieldAccessTarget target = new LtwFieldAccessTarget();

        target.write(42);
        int read = target.read();

        assertThat(read).isEqualTo(42);
        assertThat(byAdvice(invocationLog, "field-set")).isNotEmpty();
        assertThat(byAdvice(invocationLog, "field-get")).isNotEmpty();
    }

    @Test
    void ltw_withincode_limitsJoinPointByCallerMethodBody() {
        LtwWithincodeTarget target = new LtwWithincodeTarget();

        invocationLog.reset();
        target.callerA();
        int callerA = byAdvice(invocationLog, "withincode:call-callee-from-callerA").size();

        invocationLog.reset();
        target.callerB();
        int callerB = byAdvice(invocationLog, "withincode:call-callee-from-callerA").size();

        assertThat(callerA).isEqualTo(1);
        assertThat(callerB).isEqualTo(0);
    }

    @Test
    void ltw_cflow_limitsJoinPointByControlFlow() {
        LtwCflowTarget target = new LtwCflowTarget();

        invocationLog.reset();
        target.entry();
        int underEntryCount = byAdvicePrefix(invocationLog, "cflow:").size();

        invocationLog.reset();
        target.otherEntry();
        int underOtherEntryCount = byAdvicePrefix(invocationLog, "cflow:").size();

        assertThat(underEntryCount).isEqualTo(1);
        assertThat(underOtherEntryCount).isEqualTo(0);
    }
}

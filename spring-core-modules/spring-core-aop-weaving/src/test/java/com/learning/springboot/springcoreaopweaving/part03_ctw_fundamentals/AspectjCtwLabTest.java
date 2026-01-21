package com.learning.springboot.springcoreaopweaving.part03_ctw_fundamentals;

import static com.learning.springboot.springcoreaopweaving.testsupport.InvocationLogAssertions.byAdvice;
import static com.learning.springboot.springcoreaopweaving.testsupport.InvocationLogAssertions.byAdvicePrefix;
import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCallVsExecutionTarget;
import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCflowTarget;
import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwConstructorTarget;
import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwFieldAccessTarget;
import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwSelfInvocationTarget;
import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwWithincodeTarget;
import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import java.lang.management.ManagementFactory;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AspectjCtwLabTest {

    private final InvocationLog invocationLog = InvocationLog.getInstance();

    @BeforeAll
    static void requireNoAspectjWeaverJavaAgent() {
        boolean hasAspectjWeaverAgent = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("aspectjweaver.jar"));

        Assumptions.assumeTrue(
                !hasAspectjWeaverAgent,
                "CTW 测试要求 JVM 不携带 AspectJ -javaagent（aspectjweaver.jar）。请通过 Maven Surefire 的 ctw-tests-without-javaagent 执行，或直接运行 mvn test。"
        );
    }

    @BeforeEach
    void setUp() {
        invocationLog.reset();
    }

    @Test
    void ctw_testJvmIsNotStartedWithAspectjJavaAgent() {
        String inputArgs = String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments());
        assertThat(inputArgs).doesNotContain("aspectjweaver.jar");
    }

    @Test
    void ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall() {
        int result = new CtwCallVsExecutionTarget().caller(1);

        assertThat(result).isEqualTo(12);

        assertThat(byAdvice(invocationLog, "method-call:callee")).hasSize(1);
        assertThat(byAdvice(invocationLog, "method-execution:callee")).hasSize(1);
    }

    @Test
    void ctw_selfInvocationIsStillIntercepted() {
        String result = new CtwSelfInvocationTarget().outer("Bob");

        assertThat(result).contains("outer->inner:Bob");
        assertThat(byAdvice(invocationLog, "method-execution:self-invocation")).hasSize(2);
    }

    @Test
    void ctw_constructorAndFieldJoinPoints_areSupported() {
        CtwConstructorTarget constructorTarget = CtwConstructorTarget.create("x");
        CtwFieldAccessTarget fieldTarget = new CtwFieldAccessTarget();

        fieldTarget.write(7);
        fieldTarget.read();

        assertThat(constructorTarget.name()).isEqualTo("x");
        assertThat(byAdvice(invocationLog, "constructor-call")).hasSize(1);
        assertThat(byAdvice(invocationLog, "constructor-execution")).hasSize(1);
        assertThat(byAdvice(invocationLog, "field-set")).isNotEmpty();
        assertThat(byAdvice(invocationLog, "field-get")).isNotEmpty();
    }

    @Test
    void ctw_withincodeAndCflow_workAsAdvancedPointcuts() {
        CtwWithincodeTarget withincodeTarget = new CtwWithincodeTarget();

        invocationLog.reset();
        withincodeTarget.callerA();
        int callerA = byAdvice(invocationLog, "withincode:call-callee-from-callerA").size();

        invocationLog.reset();
        withincodeTarget.callerB();
        int callerB = byAdvice(invocationLog, "withincode:call-callee-from-callerA").size();

        assertThat(callerA).isEqualTo(1);
        assertThat(callerB).isEqualTo(0);

        CtwCflowTarget cflowTarget = new CtwCflowTarget();

        invocationLog.reset();
        cflowTarget.entry();
        int underEntry = byAdvicePrefix(invocationLog, "cflow:").size();

        invocationLog.reset();
        cflowTarget.otherEntry();
        int underOther = byAdvicePrefix(invocationLog, "cflow:").size();

        assertThat(underEntry).isEqualTo(1);
        assertThat(underOther).isEqualTo(0);
    }
}

package com.learning.springboot.springcoreaopweaving.part00_guide;

import static com.learning.springboot.springcoreaopweaving.testsupport.InvocationLogAssertions.byAdvice;
import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcoreaopweaving.ctwtargets.CtwCallVsExecutionTarget;
import com.learning.springboot.springcoreaopweaving.ltwtargets.LtwPlainCalculator;
import com.learning.springboot.springcoreaopweaving.support.InvocationLog;
import java.lang.management.ManagementFactory;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 参考实现：对齐 SpringCoreAopWeavingExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 *
 * 说明：本模块存在两条回归路径：
 * - LTW：需要 -javaagent:aspectjweaver.jar（测试会 assumeTrue(hasJavaAgent)）
 * - CTW：要求 JVM 不携带 aspectjweaver.jar（测试会 assumeTrue(!hasWeaverAgent)）
 */
class SpringCoreAopWeavingExerciseSolutionTest {

    private final InvocationLog invocationLog = InvocationLog.getInstance();

    @BeforeEach
    void setUp() {
        invocationLog.reset();
    }

    @Test
    void solution_ltw_weavingRecordsJoinPointEvents_whenJavaAgentPresent() {
        boolean hasJavaAgent = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("aspectjweaver.jar"));

        Assumptions.assumeTrue(hasJavaAgent, "LTW solution: require -javaagent:aspectjweaver.jar");

        int result = new LtwPlainCalculator().add(1, 2);

        assertThat(result).isEqualTo(3);
        assertThat(byAdvice(invocationLog, "method-execution:plain-add")).hasSize(1);
    }

    @Test
    void solution_ctw_weavingRecordsJoinPointEvents_whenNoWeaverAgentPresent() {
        boolean hasWeaverAgent = ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .anyMatch(arg -> arg.contains("aspectjweaver.jar"));

        Assumptions.assumeTrue(!hasWeaverAgent, "CTW solution: require JVM without aspectjweaver.jar");

        int result = new CtwCallVsExecutionTarget().caller(1);

        assertThat(result).isEqualTo(12);
        assertThat(byAdvice(invocationLog, "method-call:callee")).hasSize(1);
        assertThat(byAdvice(invocationLog, "method-execution:callee")).hasSize(1);
    }
}


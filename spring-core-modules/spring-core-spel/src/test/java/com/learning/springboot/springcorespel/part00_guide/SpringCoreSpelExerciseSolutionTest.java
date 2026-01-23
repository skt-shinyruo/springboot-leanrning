package com.learning.springboot.springcorespel.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 参考实现：对齐 SpringCoreSpelExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
class SpringCoreSpelExerciseSolutionTest {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Test
    void solution_addVariablesAndFunctions() throws Exception {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariables(Map.of("env", "prod"));

        Method toUpper = SpringCoreSpelExerciseSolutionTest.class.getDeclaredMethod("toUpper", String.class);
        ctx.registerFunction("toUpper", toUpper);

        String value = parser.parseExpression("#toUpper(#env)").getValue(ctx, String.class);
        assertThat(value).isEqualTo("PROD");
    }

    static String toUpper(String input) {
        return input == null ? null : input.toUpperCase();
    }
}


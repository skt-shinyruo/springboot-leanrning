package com.learning.springboot.springcorespel.part01_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

class SpringCoreSpelConcurrencyLabTest {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Test
    void parsedExpressionCanBeEvaluatedConcurrently_whenEvaluationContextIsPerThread() throws Exception {
        Expression expr = parser.parseExpression("value + 1");

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < 32; i += 1) {
                int input = i;
                tasks.add(() -> {
                    StandardEvaluationContext ctx = new StandardEvaluationContext(new Input(input));
                    return expr.getValue(ctx, Integer.class);
                });
            }

            List<Future<Integer>> futures = pool.invokeAll(tasks);
            assertThat(futures).hasSize(32);

            for (int i = 0; i < futures.size(); i += 1) {
                assertThat(futures.get(i).get(1, TimeUnit.SECONDS)).isEqualTo(i + 1);
            }
        } finally {
            pool.shutdownNow();
        }
    }

    record Input(int value) {}
}


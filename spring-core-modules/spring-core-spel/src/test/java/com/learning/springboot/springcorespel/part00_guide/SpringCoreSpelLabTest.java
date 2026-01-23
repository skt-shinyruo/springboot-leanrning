package com.learning.springboot.springcorespel.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

class SpringCoreSpelLabTest {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Test
    void parsesAndEvaluatesSimpleExpression() {
        Integer value = parser.parseExpression("1 + 2").getValue(Integer.class);
        assertThat(value).isEqualTo(3);
    }

    @Test
    void evaluatesAgainstRootObjectAndVariables() {
        User root = new User("Alice", 18);
        StandardEvaluationContext ctx = new StandardEvaluationContext(root);
        ctx.setVariables(Map.of("minAge", 18));

        Boolean ok = parser.parseExpression("name == 'Alice' and age >= #minAge").getValue(ctx, Boolean.class);
        assertThat(ok).isTrue();
    }

    record User(String name, int age) {}
}

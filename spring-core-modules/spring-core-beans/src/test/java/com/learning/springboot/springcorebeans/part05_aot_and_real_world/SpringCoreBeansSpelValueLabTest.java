package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验演示 @Value("#{...}")（SpEL）链路的两个关键点：
 * 1) SpEL 可以引用容器中的 bean（@beanName）
 * 2) SpEL 解析结果仍会进入类型转换，最终注入到注入点类型
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

class SpringCoreBeansSpelValueLabTest {

    @Test
    void valueWithSpel_canReferenceBeanAndResultIsConvertedToTargetType() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

            context.registerBean("baseNumberProvider", BaseNumberProvider.class, BaseNumberProvider::new);
            context.registerBean(SpelTarget.class);
            context.refresh();

            SpelTarget target = context.getBean(SpelTarget.class);

            assertThat(target.answer()).isEqualTo(42);
            assertThat(target.upper()).isEqualTo("HELLO");
            assertThat(target.stringNumber()).isEqualTo(42);

            System.out.println("OBSERVE: SpEL can reference beans (@baseNumberProvider) and still participates in type conversion");
        }
    }

    @Test
    void spelCanComposeWithPlaceholderResolution_placeholdersResolveFirst_thenExpressionIsEvaluated() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

            ConfigurableEnvironment environment = context.getEnvironment();
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("lab", java.util.Map.of("demo.base", "40")));

            context.registerBean(SpelPlaceholderTarget.class);
            context.refresh();

            SpelPlaceholderTarget target = context.getBean(SpelPlaceholderTarget.class);

            System.out.println("OBSERVE: ${...} is resolved first, then the remaining '#{...}' expression is evaluated");
            assertThat(target.answer()).isEqualTo(42);
        }
    }

    @Test
    void spelEvaluationMaySucceedButTypeConversionMayFail_whenInjectingIntoPrimitiveType() {
        GenericApplicationContext context = new GenericApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.registerBean(BadNumberTarget.class);

        assertThatThrownBy(context::refresh)
                .as("SpEL 可以返回任意对象；注入到目标类型时仍会发生类型转换，可能在此处失败")
                .hasRootCauseInstanceOf(NumberFormatException.class);

        context.close();

        System.out.println("OBSERVE: failing here is a type conversion issue, not necessarily a SpEL parse issue");
    }

    static class BaseNumberProvider {
        public int base() {
            return 40;
        }
    }

    static class SpelTarget {
        @Value("#{ @baseNumberProvider.base() + 2 }")
        private int answer;

        @Value("#{ 'hello'.toUpperCase() }")
        private String upper;

        @Value("#{ '42' }")
        private int stringNumber;

        int answer() {
            return answer;
        }

        String upper() {
            return upper;
        }

        int stringNumber() {
            return stringNumber;
        }
    }

    static class SpelPlaceholderTarget {
        @Value("#{ ${demo.base:40} + 2 }")
        private int answer;

        int answer() {
            return answer;
        }
    }

    static class BadNumberTarget {
        @Value("#{ 'not-a-number' }")
        private int number;

        int number() {
            return number;
        }
    }
}

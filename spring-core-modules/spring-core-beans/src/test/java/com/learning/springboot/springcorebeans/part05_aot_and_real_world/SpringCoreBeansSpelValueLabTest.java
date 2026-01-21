package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验演示 @Value("#{...}")（SpEL）链路的两个关键点：
 * 1) SpEL 可以引用容器中的 bean（@beanName）
 * 2) SpEL 解析结果仍会进入类型转换，最终注入到注入点类型
 */

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

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
}

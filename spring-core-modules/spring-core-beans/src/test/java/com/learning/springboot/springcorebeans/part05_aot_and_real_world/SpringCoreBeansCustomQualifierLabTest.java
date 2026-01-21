package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验演示“自定义 Qualifier（meta-annotation）”如何参与候选收敛：
 * 1) 候选有多个实现时，单依赖注入必须收敛到唯一候选
 * 2) 自定义注解 + @Qualifier 的 meta-annotation 可以把收敛规则变成有业务语义的限定符
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansCustomQualifierLabTest {

    @Test
    void customQualifierMetaAnnotation_canNarrowDownCandidates_forSingleInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(CnGreeting.class, EnGreeting.class, GreetingService.class);
            context.refresh();

            GreetingService service = context.getBean(GreetingService.class);

            assertThat(service.greeting().language()).isEqualTo("cn");
            assertThat(service.greeting().message()).isEqualTo("你好");

            System.out.println("OBSERVE: Custom qualifier narrows multiple candidates to a single injection target");
        }
    }

    interface Greeting {
        String language();

        String message();
    }

    @Cn
    static class CnGreeting implements Greeting {
        @Override
        public String language() {
            return "cn";
        }

        @Override
        public String message() {
            return "你好";
        }
    }

    static class EnGreeting implements Greeting {
        @Override
        public String language() {
            return "en";
        }

        @Override
        public String message() {
            return "hello";
        }
    }

    static class GreetingService {
        private final Greeting greeting;

        GreetingService(@Cn Greeting greeting) {
            this.greeting = greeting;
        }

        Greeting greeting() {
            return greeting;
        }
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface Cn {
    }
}


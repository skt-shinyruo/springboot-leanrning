package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansBeanNameAliasLabTest {

    @Test
    void aliasResolvesToSameSingletonInstanceAsCanonicalName() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("primaryName", Marker.class, () -> new Marker("primary"));

            context.getBeanFactory().registerAlias("primaryName", "aliasName");
            context.refresh();

            Marker byPrimary = context.getBean("primaryName", Marker.class);
            Marker byAlias = context.getBean("aliasName", Marker.class);

            System.out.println("OBSERVE: alias is just another name pointing to the same bean instance");
            assertThat(byAlias).isSameAs(byPrimary);
            assertThat(context.getBeanFactory().getAliases("primaryName")).contains("aliasName");
        }
    }

    record Marker(String origin) {
    }
}

package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验用 JVM 单测验证 AOT/Native 的“构建期契约”概念：
 * 1) RuntimeHints 默认为空（没有注册就不会命中）
 * 2) 通过 RuntimeHintsRegistrar 注册后，hints 可以被断言验证
 *
 * 注意：这里不构建 native image，仅验证“hints 是否被声明”这一核心心智模型。
 */

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class SpringCoreBeansAotRuntimeHintsLabTest {

    @Test
    void runtimeHints_areEmptyByDefault_untilExplicitlyRegistered() {
        RuntimeHints hints = new RuntimeHints();

        boolean matches = RuntimeHintsPredicates.reflection()
                .onType(Target.class)
                .test(hints);

        System.out.println("OBSERVE: Without registration, RuntimeHintsPredicates.reflection().onType(Target) does NOT match");
        assertThat(matches).isFalse();
    }

    @Test
    void runtimeHintsRegistrar_canRegisterReflectionHints_forAType() {
        RuntimeHints hints = new RuntimeHints();
        RuntimeHintsRegistrar registrar = new TargetHints();

        registrar.registerHints(hints, getClass().getClassLoader());

        boolean matches = RuntimeHintsPredicates.reflection()
                .onType(Target.class)
                .test(hints);

        System.out.println("OBSERVE: After RuntimeHintsRegistrar registers hints, reflection predicate matches");
        assertThat(matches).isTrue();
    }

    static class Target {
        private final String value;

        Target() {
            this("default");
        }

        Target(String value) {
            this.value = value;
        }

        String value() {
            return value;
        }
    }

    static class TargetHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(TypeReference.of(Target.class), MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        }
    }
}


package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SpringCoreBeansImportExerciseTest {

    @Test
    @Disabled("练习：扩展 @Import 实验：导入两个配置类，并解释每个 Bean 来自哪个配置")
    void exercise_importMultipleConfigurations() {
        assertThat(true)
                .as("""
                        练习：扩展 @Import 实验：导入两个配置类，并解释每个 Bean 来自哪个配置。

                        下一步：
                        1) 在 Import Lab 的基础上导入第二个配置类。
                        2) 用断言验证：哪些 Bean 存在、它们分别来自哪里。
                        3) 写下你的观察（哪个配置提供了哪个 Bean）。

                        参考：
                        - `spring-core-modules/spring-core-beans/src/test/java/.../SpringCoreBeansImportLabTest.java`
                        - `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：让 ImportSelector 同时依赖两个属性（例如 mode + enabled），并增加断言")
    void exercise_importSelectorWithMultipleConditions() {
        assertThat(true)
                .as("""
                        练习：让 ImportSelector 同时依赖两个属性（例如 mode + enabled），并增加断言。

                        下一步：
                        1) 扩展 selector：读取两个 property 并决定导入列表。
                        2) 用测试覆盖多种组合（mode=A/B、enabled=true/false）。
                        3) 断言最终导入的配置/Bean 符合预期。

                        建议阅读：
                        - `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增强 registrar：读取多个注解属性并注册多个 BeanDefinition（不同名称），并写断言")
    void exercise_registrarRegistersMultipleBeans() {
        assertThat(true)
                .as("""
                        练习：增强 registrar：读取多个注解属性并注册多个 BeanDefinition（不同名称），并写断言。

                        下一步：
                        1) 扩展注解：提供多个属性（例如 names/types）。
                        2) 在 registrar 里读取并批量注册多个 BeanDefinition。
                        3) 用断言验证：bean 数量、bean 名称、bean 类型都符合预期。

                        常见坑：
                        - 注册的 beanName 冲突会导致覆盖或启动失败；请确保名称唯一
                        """)
                .isFalse();
    }
}

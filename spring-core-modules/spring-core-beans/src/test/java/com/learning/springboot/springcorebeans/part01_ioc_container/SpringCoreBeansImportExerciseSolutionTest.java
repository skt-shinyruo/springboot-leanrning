package com.learning.springboot.springcorebeans.part01_ioc_container;

// 参考实现：对齐 SpringCoreBeansImportExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

class SpringCoreBeansImportExerciseSolutionTest {

    @Test
    void solution_importMultipleConfigurations() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportingMultipleConfiguration.class)) {
            ImportedMarker marker = context.getBean(ImportedMarker.class);
            ExtraImportedMarker extra = context.getBean(ExtraImportedMarker.class);

            assertThat(marker.origin()).isEqualTo("from-imported-configuration");
            assertThat(extra.origin()).isEqualTo("from-extra-imported-configuration");

            BeanDefinition markerDefinition = context.getBeanFactory().getBeanDefinition("importedMarker");
            BeanDefinition extraDefinition = context.getBeanFactory().getBeanDefinition("extraImportedMarker");

            assertThat(markerDefinition.getFactoryMethodName()).isEqualTo("importedMarker");
            assertThat(extraDefinition.getFactoryMethodName()).isEqualTo("extraImportedMarker");
        }
    }

    @Test
    void solution_importSelectorWithMultipleConditions() {
        try (AnnotationConfigApplicationContext enabledLower = new AnnotationConfigApplicationContext()) {
            TestPropertyValues.of(
                    "exercise.selector.enabled=true",
                    "exercise.selector.mode=lower"
            ).applyTo(enabledLower);
            enabledLower.register(SelectorEnabledConfiguration.class);
            enabledLower.refresh();

            SelectedImport selected = enabledLower.getBean(SelectedImport.class);
            assertThat(selected.id()).isEqualTo("lower");
        }

        try (AnnotationConfigApplicationContext disabled = new AnnotationConfigApplicationContext()) {
            TestPropertyValues.of(
                    "exercise.selector.enabled=false",
                    "exercise.selector.mode=lower"
            ).applyTo(disabled);
            disabled.register(SelectorEnabledConfiguration.class);
            disabled.refresh();

            assertThat(disabled.containsBean("selectedImport")).isFalse();
        }

        try (AnnotationConfigApplicationContext enabledDefault = new AnnotationConfigApplicationContext()) {
            TestPropertyValues.of("exercise.selector.enabled=true").applyTo(enabledDefault);
            enabledDefault.register(SelectorEnabledConfiguration.class);
            enabledDefault.refresh();

            SelectedImport selected = enabledDefault.getBean(SelectedImport.class);
            assertThat(selected.id()).isEqualTo("upper");
        }
    }

    @Test
    void solution_registrarRegistersMultipleBeans() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MultiRegistrarEnabledConfiguration.class)) {
            RegisteredMessage a = context.getBean("messageA", RegisteredMessage.class);
            RegisteredMessage b = context.getBean("messageB", RegisteredMessage.class);
            RegisteredMessage c = context.getBean("messageC", RegisteredMessage.class);

            assertThat(a.value()).isEqualTo("A");
            assertThat(b.value()).isEqualTo("B");
            assertThat(c.value()).isEqualTo("C");

            assertThat(Arrays.asList(context.getBeanNamesForType(RegisteredMessage.class)))
                    .contains("messageA", "messageB", "messageC");
        }
    }

    record ImportedMarker(String origin) {
    }

    record ExtraImportedMarker(String origin) {
    }

    @Configuration
    @Import({ImportedConfiguration.class, ExtraImportedConfiguration.class})
    static class ImportingMultipleConfiguration {
    }

    @Configuration
    static class ImportedConfiguration {
        @Bean
        ImportedMarker importedMarker() {
            return new ImportedMarker("from-imported-configuration");
        }
    }

    @Configuration
    static class ExtraImportedConfiguration {
        @Bean
        ExtraImportedMarker extraImportedMarker() {
            return new ExtraImportedMarker("from-extra-imported-configuration");
        }
    }

    record SelectedImport(String id) {
    }

    @Configuration
    @EnableSelectedImportV2(
            modeProperty = "exercise.selector.mode",
            enabledProperty = "exercise.selector.enabled",
            defaultMode = "upper"
    )
    static class SelectorEnabledConfiguration {
    }

    @Configuration
    static class UpperSelectedImportConfiguration {
        @Bean
        SelectedImport selectedImport() {
            return new SelectedImport("upper");
        }
    }

    @Configuration
    static class LowerSelectedImportConfiguration {
        @Bean
        SelectedImport selectedImport() {
            return new SelectedImport("lower");
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(SelectedImportImportSelectorV2.class)
    @interface EnableSelectedImportV2 {
        String modeProperty();

        String enabledProperty();

        String defaultMode();
    }

    static class SelectedImportImportSelectorV2 implements ImportSelector, org.springframework.context.EnvironmentAware {

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableSelectedImportV2.class.getName());
            String modeProperty = (String) attributes.get("modeProperty");
            String enabledProperty = (String) attributes.get("enabledProperty");
            String defaultMode = (String) attributes.get("defaultMode");

            boolean enabled = Boolean.parseBoolean(environment.getProperty(enabledProperty, "true"));
            if (!enabled) {
                return new String[0];
            }

            String mode = environment.getProperty(modeProperty, defaultMode);
            return switch (mode) {
                case "lower" -> new String[]{LowerSelectedImportConfiguration.class.getName()};
                case "upper" -> new String[]{UpperSelectedImportConfiguration.class.getName()};
                default -> new String[]{UpperSelectedImportConfiguration.class.getName()};
            };
        }
    }

    record RegisteredMessage(String value) {
    }

    @Configuration
    @EnableRegisteredMessages({"messageA", "messageB", "messageC"})
    static class MultiRegistrarEnabledConfiguration {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(RegisteredMessagesRegistrar.class)
    @interface EnableRegisteredMessages {
        String[] value();
    }

    static class RegisteredMessagesRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableRegisteredMessages.class.getName());
            String[] names = (String[]) attributes.get("value");

            for (String name : names) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RegisteredMessage.class);
                builder.addConstructorArgValue(name.replace("message", ""));

                registry.registerBeanDefinition(name, builder.getBeanDefinition());
            }
        }
    }
}


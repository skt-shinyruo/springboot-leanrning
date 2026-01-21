package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

class SpringCoreBeansImportLabTest {

    @Test
    void importAnnotationBringsInAdditionalConfiguration() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportingConfiguration.class)) {
            ImportedMarker marker = context.getBean(ImportedMarker.class);

            System.out.println("OBSERVE: @Import importedMarker.origin=" + marker.origin());
            assertThat(marker.origin()).isEqualTo("imported-by-@Import");
        }
    }

    @Test
    void importSelectorChoosesConfigurationBasedOnEnvironmentProperty() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            TestPropertyValues.of("demo.selector.mode=lower").applyTo(context);

            context.register(SelectorEnabledConfiguration.class);
            context.refresh();

            SelectedImport selected = context.getBean(SelectedImport.class);

            System.out.println("OBSERVE: ImportSelector demo.selector.mode=" + context.getEnvironment().getProperty("demo.selector.mode"));
            System.out.println("OBSERVE: ImportSelector selected.id=" + selected.id());
            assertThat(selected.id()).isEqualTo("lower");
        }
    }

    @Test
    void importBeanDefinitionRegistrarCanRegisterBeanDefinitionProgrammatically() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RegistrarEnabledConfiguration.class)) {
            RegisteredMessage message = context.getBean(RegisteredMessage.class);

            System.out.println("OBSERVE: Registrar registeredMessage.value=" + message.value());
            assertThat(message.value()).isEqualTo("from-registrar");

            BeanDefinition definition = context.getBeanFactory().getBeanDefinition("registeredMessage");
            Object constructorArg0 = definition.getConstructorArgumentValues()
                    .getArgumentValue(0, String.class)
                    .getValue();

            assertThat(constructorArg0).isEqualTo("from-registrar");
        }
    }

    record ImportedMarker(String origin) {
    }

    record SelectedImport(String id) {
    }

    record RegisteredMessage(String value) {
    }

    @Configuration
    @Import(ImportedConfiguration.class)
    static class ImportingConfiguration {
    }

    @Configuration
    static class ImportedConfiguration {
        @Bean
        ImportedMarker importedMarker() {
            return new ImportedMarker("imported-by-@Import");
        }
    }

    @Configuration
    @EnableSelectedImport(modeProperty = "demo.selector.mode", defaultMode = "upper")
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
    @Import(SelectedImportImportSelector.class)
    @interface EnableSelectedImport {
        String modeProperty();

        String defaultMode();
    }

    static class SelectedImportImportSelector implements ImportSelector, org.springframework.context.EnvironmentAware {

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableSelectedImport.class.getName());
            String modeProperty = (String) attributes.get("modeProperty");
            String defaultMode = (String) attributes.get("defaultMode");

            String mode = environment.getProperty(modeProperty, defaultMode);
            return switch (mode) {
                case "lower" -> new String[]{LowerSelectedImportConfiguration.class.getName()};
                case "upper" -> new String[]{UpperSelectedImportConfiguration.class.getName()};
                default -> new String[]{UpperSelectedImportConfiguration.class.getName()};
            };
        }
    }

    @Configuration
    @EnableRegisteredMessage("from-registrar")
    static class RegistrarEnabledConfiguration {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(RegisteredMessageRegistrar.class)
    @interface EnableRegisteredMessage {
        String value();
    }

    static class RegisteredMessageRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableRegisteredMessage.class.getName());
            String value = (String) attributes.get("value");

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RegisteredMessage.class);
            builder.addConstructorArgValue(value);
            BeanDefinition definition = builder.getBeanDefinition();

            registry.registerBeanDefinition("registeredMessage", definition);
        }
    }
}

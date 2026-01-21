package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;

class SpringCoreBeansAotFactoriesLabTest {

    @Test
    void aotFactories_canLoadBeanFactoryInitializationAotProcessors_viaAotServices() {
        AotServices<BeanFactoryInitializationAotProcessor> processors =
                AotServices.factories().load(BeanFactoryInitializationAotProcessor.class);

        List<String> processorTypes = new ArrayList<>();
        for (BeanFactoryInitializationAotProcessor processor : processors) {
            processorTypes.add(processor.getClass().getName());
        }

        System.out.println("OBSERVE: spring-beans ships META-INF/spring/aot.factories for AOT services discovery");
        System.out.println("OBSERVE: AotServices.factories().load(BeanFactoryInitializationAotProcessor.class) discovers processors");

        assertThat(processorTypes)
                .contains("org.springframework.beans.factory.aot.BeanRegistrationsAotProcessor");
    }

    @Test
    void aotFactories_canLoadRuntimeHintsRegistrars_viaAotServices() {
        AotServices<RuntimeHintsRegistrar> registrars = AotServices.factories().load(RuntimeHintsRegistrar.class);

        List<String> registrarTypes = new ArrayList<>();
        for (RuntimeHintsRegistrar registrar : registrars) {
            registrarTypes.add(registrar.getClass().getName());
        }

        System.out.println("OBSERVE: aot.factories also registers RuntimeHintsRegistrar implementations");

        assertThat(registrarTypes)
                .contains(
                        "org.springframework.beans.factory.annotation.JakartaAnnotationsRuntimeHints",
                        "org.springframework.beans.BeanUtilsRuntimeHints");
    }
}


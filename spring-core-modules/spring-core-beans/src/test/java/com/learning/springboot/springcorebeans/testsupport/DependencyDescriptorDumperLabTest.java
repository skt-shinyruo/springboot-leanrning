package com.learning.springboot.springcorebeans.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;

public class DependencyDescriptorDumperLabTest {

    @Test
    void dump_includesFieldAndMethodParameterMetadata_andQualifierValue() throws Exception {
        Field field = SampleFieldInjection.class.getDeclaredField("secondaryWorker");
        DependencyDescriptor fieldDescriptor = new DependencyDescriptor(field, true);
        String fieldDump = DependencyDescriptorDumper.dump(fieldDescriptor);

        Constructor<SampleConstructorInjection> constructor =
                SampleConstructorInjection.class.getDeclaredConstructor(Worker.class);
        MethodParameter parameter = new MethodParameter(constructor, 0);
        DependencyDescriptor parameterDescriptor = new DependencyDescriptor(parameter, true);
        String parameterDump = DependencyDescriptorDumper.dump(parameterDescriptor);

        assertThat(fieldDump).contains("DEPENDENCY_DESCRIPTOR");
        assertThat(fieldDump).contains("- kind: Field");
        assertThat(fieldDump).contains("- dependencyName: secondaryWorker");
        assertThat(fieldDump).contains("org.springframework.beans.factory.annotation.Qualifier{value=secondaryWorker}");

        assertThat(parameterDump).contains("DEPENDENCY_DESCRIPTOR");
        assertThat(parameterDump).contains("- kind: MethodParameter");
        assertThat(parameterDump).contains("- parameterIndex: 0");
        assertThat(parameterDump).contains("org.springframework.beans.factory.annotation.Qualifier{value=secondaryWorker}");

        InjectionPoint fieldInjectionPoint = new InjectionPoint(field);
        String injectionPointDump = DependencyDescriptorDumper.dump(fieldInjectionPoint);
        assertThat(injectionPointDump).contains("INJECTION_POINT");
        assertThat(injectionPointDump).contains("- kind: Field");
        assertThat(injectionPointDump).contains("- dependencyName: secondaryWorker");
    }

    interface Worker {
        String id();
    }

    static class SampleFieldInjection {
        @Autowired
        @Qualifier("secondaryWorker")
        private Worker secondaryWorker;
    }

    static class SampleConstructorInjection {
        SampleConstructorInjection(@Qualifier("secondaryWorker") Worker secondaryWorker) {
        }
    }
}

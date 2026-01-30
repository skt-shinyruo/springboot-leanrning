package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcorebeans.testsupport.DependencyDescriptorDumper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;

class SpringCoreBeansDependencyDescriptorMetadataLabTest {

    @Test
    void injectionPointMetadata_differs_betweenFieldAndConstructorParameter() throws Exception {
        Field field = FieldInjectionPointSample.class.getDeclaredField("secondaryWorker");
        DependencyDescriptor fieldDescriptor = new DependencyDescriptor(field, true);
        String fieldDump = DependencyDescriptorDumper.dump(fieldDescriptor);

        Constructor<ConstructorInjectionPointSample> constructor =
                ConstructorInjectionPointSample.class.getDeclaredConstructor(Worker.class);
        MethodParameter parameter = new MethodParameter(constructor, 0);
        parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        System.out.println("OBSERVE: constructor parameterName discovery => " + parameter.getParameterName());
        DependencyDescriptor parameterDescriptor = new DependencyDescriptor(parameter, true);
        String parameterDump = DependencyDescriptorDumper.dump(parameterDescriptor);

        System.out.println("OBSERVE: Field injection point metadata\n" + fieldDump);
        System.out.println("OBSERVE: Constructor parameter injection point metadata\n" + parameterDump);

        assertThat(fieldDump).contains("- kind: Field");
        assertThat(fieldDump).contains("- dependencyName: secondaryWorker");
        assertThat(fieldDump).contains("org.springframework.beans.factory.annotation.Qualifier{value=secondaryWorker}");

        assertThat(parameterDump).contains("- kind: MethodParameter");
        assertThat(parameterDump).contains("org.springframework.beans.factory.annotation.Qualifier{value=secondaryWorker}");
    }

    @Test
    void resolvableType_preservesGenericSignature_forFieldAndConstructorParameter() throws Exception {
        Field field = GenericFieldInjectionPointSample.class.getDeclaredField("stringHandler");
        DependencyDescriptor fieldDescriptor = new DependencyDescriptor(field, true);
        String fieldDump = DependencyDescriptorDumper.dump(fieldDescriptor);

        Constructor<GenericConstructorInjectionPointSample> constructor =
                GenericConstructorInjectionPointSample.class.getDeclaredConstructor(Handler.class);
        MethodParameter parameter = new MethodParameter(constructor, 0);
        parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        System.out.println("OBSERVE: constructor parameterName discovery => " + parameter.getParameterName());
        DependencyDescriptor parameterDescriptor = new DependencyDescriptor(parameter, true);
        String parameterDump = DependencyDescriptorDumper.dump(parameterDescriptor);

        System.out.println("OBSERVE: Field generic injection point metadata\n" + fieldDump);
        System.out.println("OBSERVE: Constructor parameter generic injection point metadata\n" + parameterDump);

        assertThat(fieldDump).contains("dependencyType: " + Handler.class.getName());
        assertThat(fieldDump).contains("resolvableType: " + Handler.class.getName() + "<java.lang.String>");

        assertThat(parameterDump).contains("dependencyType: " + Handler.class.getName());
        assertThat(parameterDump).contains("resolvableType: " + Handler.class.getName() + "<java.lang.String>");
    }

    interface Worker {
        String id();
    }

    interface Handler<T> {
        String id();
    }

    static class FieldInjectionPointSample {
        @Autowired
        @Qualifier("secondaryWorker")
        private Worker secondaryWorker;
    }

    static class ConstructorInjectionPointSample {
        ConstructorInjectionPointSample(@Qualifier("secondaryWorker") Worker secondaryWorker) {
        }
    }

    static class GenericFieldInjectionPointSample {
        @Autowired
        private Handler<String> stringHandler;
    }

    static class GenericConstructorInjectionPointSample {
        GenericConstructorInjectionPointSample(Handler<String> stringHandler) {
        }
    }
}

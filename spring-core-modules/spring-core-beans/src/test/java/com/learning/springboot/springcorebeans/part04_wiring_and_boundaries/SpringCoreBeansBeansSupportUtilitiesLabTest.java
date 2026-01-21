package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.beans.support.SortDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

class SpringCoreBeansBeansSupportUtilitiesLabTest {

    @Test
    void argumentConvertingMethodInvoker_canConvertStringArguments_forMethodInvocation() throws Exception {
        PortTarget target = new PortTarget();

        ArgumentConvertingMethodInvoker invoker = new ArgumentConvertingMethodInvoker();
        invoker.setTargetObject(target);
        invoker.setTargetMethod("setPort");
        invoker.setArguments("8080");

        invoker.prepare();
        invoker.invoke();

        assertThat(target.port()).isEqualTo(8080);
    }

    @Test
    void argumentConvertingMethodInvoker_canUseCustomPropertyEditor_forNonStandardType() throws Exception {
        EndpointTarget target = new EndpointTarget();

        ArgumentConvertingMethodInvoker invoker = new ArgumentConvertingMethodInvoker();
        invoker.setTargetObject(target);
        invoker.setTargetMethod("setEndpoint");
        invoker.registerCustomEditor(Endpoint.class, new HostAndPortEditor());
        invoker.setArguments("localhost:8080");

        invoker.prepare();
        invoker.invoke();

        assertThat(target.endpoint()).isEqualTo(new HostAndPortEndpoint("localhost", 8080));
    }

    @Test
    void resourceEditorRegistrar_allowsStringToResourceConversion_duringPopulateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            RootBeanDefinition holder = new RootBeanDefinition(ResourceHolder.class);
            holder.getPropertyValues().add("resource", "classpath:application.properties");
            context.registerBeanDefinition("holder", holder);

            context.refresh();

            ResourceHolder bean = context.getBean(ResourceHolder.class);
            assertThat(bean.resource()).isNotNull();
            assertThat(bean.resource().exists()).isTrue();
        }
    }

    @Test
    void propertyComparator_sortsByNestedProperty_andPutsNullsLast() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Bob", new Address("boston")));
        people.add(new Person("alice", new Address("Austin")));
        people.add(new Person("Charlie", null));

        SortDefinition sort = new MutableSortDefinition("address.city", true, true);
        PropertyComparator.sort(people, sort);

        assertThat(people).extracting(Person::getName).containsExactly("alice", "Bob", "Charlie");
    }

    @Test
    void pagedListHolder_resortAndPagination_work_withToggleAscending() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("c", new Address("c")));
        people.add(new Person("a", new Address("a")));
        people.add(new Person("b", new Address("b")));

        PagedListHolder<Person> holder = new PagedListHolder<>(people);
        holder.setPageSize(2);

        MutableSortDefinition sort = (MutableSortDefinition) holder.getSort();
        sort.setProperty("name");
        holder.resort();

        assertThat(holder.getPageCount()).isEqualTo(2);
        assertThat(holder.getPageList()).extracting(Person::getName).containsExactly("a", "b");

        holder.setPage(1);
        assertThat(holder.getPageList()).extracting(Person::getName).containsExactly("c");

        sort.setProperty("name");
        holder.resort();

        assertThat(holder.getPageList()).extracting(Person::getName).containsExactly("c", "b");
    }

    static class PortTarget {
        private int port;

        public void setPort(int port) {
            this.port = port;
        }

        int port() {
            return port;
        }
    }

    interface Endpoint {
        String host();

        int port();
    }

    record HostAndPortEndpoint(String host, int port) implements Endpoint {
    }

    static class EndpointTarget {
        private Endpoint endpoint;

        public void setEndpoint(Endpoint endpoint) {
            this.endpoint = endpoint;
        }

        Endpoint endpoint() {
            return endpoint;
        }
    }

    static class HostAndPortEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            String[] parts = text.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid host:port: " + text);
            }
            setValue(new HostAndPortEndpoint(parts[0], Integer.parseInt(parts[1])));
        }
    }

    static class ResourceHolder {
        private Resource resource;

        public void setResource(Resource resource) {
            this.resource = resource;
        }

        Resource resource() {
            return resource;
        }
    }

    static class Person {
        private final String name;
        private final Address address;

        Person(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public Address getAddress() {
            return address;
        }
    }

    static class Address {
        private final String city;

        Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }
}


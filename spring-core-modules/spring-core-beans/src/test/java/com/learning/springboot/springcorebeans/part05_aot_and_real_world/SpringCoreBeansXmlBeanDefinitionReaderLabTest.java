package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验用于把 XML 输入拉回“定义层（BeanDefinition）”视角：
 * 1) XmlBeanDefinitionReader#loadBeanDefinitions 会把 XML 解析为 BeanDefinition 并注册到 BeanFactory
 * 2) 定义层失败（例如 invalid XML）通常表现为 BeanDefinitionStoreException
 */

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;

class SpringCoreBeansXmlBeanDefinitionReaderLabTest {

    @Test
    void xmlBeanDefinitionReader_loadsBeanDefinitions_andBeanDefinitionContainsConstructorArgValues() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);

            String xml = """
                    <beans xmlns="http://www.springframework.org/schema/beans"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd">
                      <bean id="message" class="%s">
                        <constructor-arg value="from-xml"/>
                      </bean>
                    </beans>
                    """.formatted(Message.class.getName());

            reader.loadBeanDefinitions(new ByteArrayResource(xml.getBytes(UTF_8)));
            context.refresh();

            Message message = context.getBean(Message.class);
            assertThat(message.value()).isEqualTo("from-xml");

            BeanDefinition definition = context.getBeanFactory().getBeanDefinition("message");
            Object constructorArg0 = definition.getConstructorArgumentValues()
                    .getGenericArgumentValues()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .getValue();

            System.out.println("OBSERVE: XML is just one input form; it is normalized into BeanDefinition");
            System.out.println("OBSERVE: beanDefinition.beanClassName=" + definition.getBeanClassName());
            System.out.println("OBSERVE: beanDefinition.resourceDescription=" + definition.getResourceDescription());
            System.out.println("OBSERVE: beanDefinition.constructorArg0=" + constructorArg0);

            assertThat(definition.getBeanClassName()).isEqualTo(Message.class.getName());
            assertThat(constructorArg0).isInstanceOf(TypedStringValue.class);
            assertThat(((TypedStringValue) constructorArg0).getValue()).isEqualTo("from-xml");
        }
    }

    @Test
    void invalidXml_throwsBeanDefinitionStoreException_asDefinitionPhaseErrorSignal() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);

            String invalidXml = """
                    <beans>
                      <bean id="bad" class="com.example.DoesNotMatter">
                    </beans>
                    """;

            assertThatThrownBy(() -> reader.loadBeanDefinitions(new ByteArrayResource(invalidXml.getBytes(UTF_8))))
                    .isInstanceOf(BeanDefinitionStoreException.class);

            System.out.println("OBSERVE: BeanDefinitionStoreException indicates a definition reading/parsing problem (XML is one cause)");
        }
    }

    record Message(String value) {
    }
}

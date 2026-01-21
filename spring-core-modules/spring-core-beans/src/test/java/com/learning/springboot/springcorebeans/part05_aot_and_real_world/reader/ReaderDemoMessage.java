package com.learning.springboot.springcorebeans.part05_aot_and_real_world.reader;

// 用于 BeanDefinitionReader 演示的简单 JavaBean（便于做属性填充与断点观察）。

public class ReaderDemoMessage {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


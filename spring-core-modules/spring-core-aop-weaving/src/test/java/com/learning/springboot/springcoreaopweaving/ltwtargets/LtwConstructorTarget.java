package com.learning.springboot.springcoreaopweaving.ltwtargets;

public class LtwConstructorTarget {

    private final String name;

    public LtwConstructorTarget(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}

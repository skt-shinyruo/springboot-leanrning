package com.learning.springboot.springcoreaopweaving.ctwtargets;

public class CtwConstructorTarget {

    private final String name;

    public CtwConstructorTarget(String name) {
        this.name = name;
    }

    public static CtwConstructorTarget create(String name) {
        return new CtwConstructorTarget(name);
    }

    public String name() {
        return name;
    }
}

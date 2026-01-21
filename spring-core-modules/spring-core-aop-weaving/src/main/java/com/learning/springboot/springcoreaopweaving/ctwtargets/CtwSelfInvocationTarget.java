package com.learning.springboot.springcoreaopweaving.ctwtargets;

public class CtwSelfInvocationTarget {

    public String outer(String name) {
        return "outer->" + inner(name);
    }

    public String inner(String name) {
        return "inner:" + name;
    }
}

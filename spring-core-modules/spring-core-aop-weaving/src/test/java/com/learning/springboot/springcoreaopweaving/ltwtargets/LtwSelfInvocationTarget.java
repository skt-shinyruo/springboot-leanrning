package com.learning.springboot.springcoreaopweaving.ltwtargets;

public class LtwSelfInvocationTarget {

    public String outer(String name) {
        return "outer->" + inner(name);
    }

    public String inner(String name) {
        return "inner:" + name;
    }
}

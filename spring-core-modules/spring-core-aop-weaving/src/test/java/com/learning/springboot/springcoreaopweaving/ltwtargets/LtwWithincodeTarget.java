package com.learning.springboot.springcoreaopweaving.ltwtargets;

public class LtwWithincodeTarget {

    public String callerA() {
        return callee("A");
    }

    public String callerB() {
        return callee("B");
    }

    public String callee(String from) {
        return "callee:" + from;
    }
}

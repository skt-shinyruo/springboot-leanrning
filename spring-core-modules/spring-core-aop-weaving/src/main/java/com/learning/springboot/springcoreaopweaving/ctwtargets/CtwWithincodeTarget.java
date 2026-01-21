package com.learning.springboot.springcoreaopweaving.ctwtargets;

public class CtwWithincodeTarget {

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

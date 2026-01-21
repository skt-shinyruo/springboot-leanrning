package com.learning.springboot.springcoreaopweaving.ctwtargets;

public class CtwCallVsExecutionTarget {

    public int caller(int x) {
        return callee(x) + 1;
    }

    public int callee(int x) {
        return x + 10;
    }
}

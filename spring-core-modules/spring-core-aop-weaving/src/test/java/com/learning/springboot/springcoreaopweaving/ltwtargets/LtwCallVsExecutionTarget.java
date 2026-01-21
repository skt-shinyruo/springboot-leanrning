package com.learning.springboot.springcoreaopweaving.ltwtargets;

public class LtwCallVsExecutionTarget {

    public int caller(int x) {
        return callee(x) + 1;
    }

    public int callee(int x) {
        return x + 10;
    }
}

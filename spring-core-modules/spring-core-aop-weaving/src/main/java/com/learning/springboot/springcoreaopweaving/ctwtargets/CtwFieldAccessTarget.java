package com.learning.springboot.springcoreaopweaving.ctwtargets;

public class CtwFieldAccessTarget {

    int value;

    public void write(int newValue) {
        this.value = newValue;
    }

    public int read() {
        return this.value;
    }
}

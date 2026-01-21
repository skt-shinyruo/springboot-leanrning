package com.learning.springboot.springcoreaopweaving.ltwtargets;

public class LtwFieldAccessTarget {

    int value;

    public void write(int newValue) {
        this.value = newValue;
    }

    public int read() {
        return this.value;
    }
}

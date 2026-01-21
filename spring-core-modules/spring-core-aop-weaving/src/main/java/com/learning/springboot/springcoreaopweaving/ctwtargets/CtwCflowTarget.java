package com.learning.springboot.springcoreaopweaving.ctwtargets;

public class CtwCflowTarget {

    public void entry() {
        middle();
    }

    public void otherEntry() {
        deep();
    }

    private void middle() {
        deep();
    }

    public void deep() {
        // for weaving
    }
}

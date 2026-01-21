package com.learning.springboot.springcoreaopweaving.ltwtargets;

public class LtwCflowTarget {

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

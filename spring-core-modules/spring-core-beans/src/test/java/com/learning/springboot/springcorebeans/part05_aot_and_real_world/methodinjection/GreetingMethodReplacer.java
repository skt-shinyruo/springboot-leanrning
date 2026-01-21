package com.learning.springboot.springcorebeans.part05_aot_and_real_world.methodinjection;

import java.lang.reflect.Method;

import org.springframework.beans.factory.support.MethodReplacer;

public class GreetingMethodReplacer implements MethodReplacer {

    @Override
    public Object reimplement(Object obj, Method method, Object[] args) {
        String name = args == null || args.length == 0 ? "" : String.valueOf(args[0]);
        return "replaced:" + name;
    }
}


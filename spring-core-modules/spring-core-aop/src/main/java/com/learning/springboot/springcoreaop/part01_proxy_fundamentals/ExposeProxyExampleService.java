package com.learning.springboot.springcoreaop.part01_proxy_fundamentals;

// 本示例用于演示 exposeProxy + AopContext.currentProxy：让同类内部调用也能走代理链（教学用途，不建议作为默认工程写法）。

import com.learning.springboot.springcoreaop.part02_autoproxy_and_pointcuts.Traced;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

@Service
public class ExposeProxyExampleService {

    @Traced
    public String outer(String name) {
        ExposeProxyExampleService proxy = (ExposeProxyExampleService) AopContext.currentProxy();
        return "outer->" + proxy.inner(name);
    }

    @Traced
    public String inner(String name) {
        return "inner->" + name;
    }
}


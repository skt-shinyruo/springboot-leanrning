package com.learning.springboot.bootwebmvc.part01_web_mvc;

// 本类用于演示 binder 的“字段白名单”边界：避免危险字段被批量绑定（mass assignment）。

import jakarta.validation.constraints.NotBlank;

public class MassAssignmentForm {

    @NotBlank
    private String name;

    /**
     * 教学用“危险字段”：真实工程里这类字段不应该来自表单/请求参数直接绑定。
     */
    private boolean admin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}


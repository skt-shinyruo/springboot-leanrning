package com.learning.springboot.bootwebmvc.part01_web_mvc;

// 本类用于演示 @ModelAttribute（默认）路径：DataBinder + Validation 的绑定/校验行为。

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class BindingForm {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


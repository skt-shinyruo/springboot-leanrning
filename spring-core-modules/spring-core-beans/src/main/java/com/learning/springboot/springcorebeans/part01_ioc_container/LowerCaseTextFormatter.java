package com.learning.springboot.springcorebeans.part01_ioc_container;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component("lowerFormatter")
public class LowerCaseTextFormatter implements TextFormatter {

    @Override
    public String format(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase(Locale.ROOT);
    }
}

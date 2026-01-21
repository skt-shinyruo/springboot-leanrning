package com.learning.springboot.springcorebeans.part01_ioc_container;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component("upperFormatter")
public class UpperCaseTextFormatter implements TextFormatter {

    @Override
    public String format(String input) {
        if (input == null) {
            return null;
        }
        return input.toUpperCase(Locale.ROOT);
    }
}

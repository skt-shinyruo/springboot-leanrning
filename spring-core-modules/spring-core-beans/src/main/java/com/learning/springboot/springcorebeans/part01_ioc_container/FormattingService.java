package com.learning.springboot.springcorebeans.part01_ioc_container;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FormattingService {

    private final TextFormatter textFormatter;

    public FormattingService(@Qualifier("upperFormatter") TextFormatter textFormatter) {
        this.textFormatter = textFormatter;
    }

    public String format(String input) {
        return textFormatter.format(input);
    }

    public String formatterImplementation() {
        return textFormatter.getClass().getSimpleName();
    }
}

package com.learning.springboot.springcorebeans.part01_ioc_container;

import java.util.UUID;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeIdGenerator {

    private final UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }
}

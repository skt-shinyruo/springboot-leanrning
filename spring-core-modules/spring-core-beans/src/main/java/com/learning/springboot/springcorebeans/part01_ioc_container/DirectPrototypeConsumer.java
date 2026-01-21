package com.learning.springboot.springcorebeans.part01_ioc_container;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class DirectPrototypeConsumer {

    private final PrototypeIdGenerator idGenerator;

    public DirectPrototypeConsumer(PrototypeIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public UUID currentId() {
        return idGenerator.getId();
    }
}

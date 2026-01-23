package com.learning.springboot.bootactuator.part01_actuator;

import java.util.Map;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "learning")
public class LearningEndpoint {

    @ReadOperation
    public Map<String, Object> learning() {
        return Map.of(
                "module", "springboot-actuator",
                "status", "ok"
        );
    }
}


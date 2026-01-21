package com.learning.springboot.bootactuator.part01_actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class LearningHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("module", "springboot-actuator")
                .withDetail("hint", "change this indicator to learn Actuator")
                .build();
    }
}

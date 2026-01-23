package com.learning.springboot.bootactuator.part01_actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LearningHealthIndicator implements HealthIndicator {

    private final boolean enabled;

    public LearningHealthIndicator(@Value("${learning.health.enabled:true}") boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Health health() {
        if (!enabled) {
            return Health.down()
                    .withDetail("module", "springboot-actuator")
                    .withDetail("hint", "enable learning.health.enabled=true to see UP")
                    .build();
        }

        return Health.up()
                .withDetail("module", "springboot-actuator")
                .withDetail("hint", "change this indicator to learn Actuator")
                .build();
    }
}

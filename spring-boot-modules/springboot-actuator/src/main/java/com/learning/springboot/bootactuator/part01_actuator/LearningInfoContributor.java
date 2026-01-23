package com.learning.springboot.bootactuator.part01_actuator;

import java.util.Map;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class LearningInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("learning", Map.of(
                "module", "springboot-actuator",
                "hint", "InfoContributor can add custom diagnostic fields"
        ));
    }
}


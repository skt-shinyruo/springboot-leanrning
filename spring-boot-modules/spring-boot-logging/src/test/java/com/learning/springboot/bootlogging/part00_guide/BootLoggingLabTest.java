package com.learning.springboot.bootlogging.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootlogging.service.LoggingDemoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(properties = "logging.level.com.learning.springboot.bootlogging=DEBUG")
class BootLoggingLabTest {

    @Autowired
    private LoggingDemoService demoService;

    @Test
    void debugLogIsPrintedWhenLevelIsDebug(CapturedOutput output) {
        demoService.logOnce("lab");

        assertThat(output).contains("LOGGING:info");
        assertThat(output).contains("LOGGING:debug");
    }
}

package com.learning.springboot.bootlogging.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.learning.springboot.bootlogging.service.LoggingDemoService;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 参考实现：对齐 BootLoggingExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest(properties = "logging.level.com.learning.springboot.bootlogging=DEBUG")
class BootLoggingExerciseSolutionTest {

    @Autowired
    private LoggingDemoService demoService;

    @Test
    void solution_addMdcAndAssertItAppearsInLogs() {
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingDemoService.class);

        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            MDC.put("requestId", "r1");
            demoService.logOnce("lab");
        } finally {
            MDC.remove("requestId");
            logger.detachAppender(appender);
            appender.stop();
        }

        assertThat(appender.list).isNotEmpty();
        assertThat(appender.list)
                .anySatisfy(event -> assertThat(event.getMDCPropertyMap()).containsEntry("requestId", "r1"));
    }
}


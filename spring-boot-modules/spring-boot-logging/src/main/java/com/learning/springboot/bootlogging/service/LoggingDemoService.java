package com.learning.springboot.bootlogging.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingDemoService {

    private static final Logger log = LoggerFactory.getLogger(LoggingDemoService.class);

    public void logOnce(String tag) {
        log.info("LOGGING:info tag={}", tag);
        log.debug("LOGGING:debug tag={}", tag);
    }
}

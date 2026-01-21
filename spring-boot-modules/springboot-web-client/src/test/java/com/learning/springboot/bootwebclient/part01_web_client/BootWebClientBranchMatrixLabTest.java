package com.learning.springboot.bootwebclient.part01_web_client;

/**
 * 关键分支矩阵入口：聚合 Web Client 关键分支（RestClient vs WebClient、filter order），用于回归与调试。
 */
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BootWebClientRestClientLabTest.class,
        BootWebClientWebClientLabTest.class,
        BootWebClientWebClientFilterOrderLabTest.class
})
class BootWebClientBranchMatrixLabTest {}


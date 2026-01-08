package com.num.management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic integration test for the Spring Boot application context.
 * <p>
 * This test ensures that the application context loads successfully without any
 * errors.
 * It is a standard sanity check for Spring Boot applications.
 * </p>
 */
@SpringBootTest
class NumManagementApplicationTests {

    /**
     * Test to verify that the application context loads.
     * If this test fails, it means there is a configuration issue preventing the
     * app from starting.
     */
    @Test
    void contextLoads() {
        // No assertions needed; checking if context loads throws no exception.
    }

}

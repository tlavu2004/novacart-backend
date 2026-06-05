package com.tlavu.novacart.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpringEnvironmentTest {

    @Autowired
    private Environment environment;

    @Test
    @Disabled("Environment debugging only - not part of CI")
    void shouldMatchSystemEnvWhenAvailable() {
        String springValue = environment.getProperty("APP_DB_USERNAME");
        String systemValue = System.getenv("APP_DB_USERNAME");

        assertNotNull(springValue, "Spring Environment should have APP_DB_USERNAME");
        assertFalse(springValue.isBlank(), "Spring Environment APP_DB_USERNAME should not be blank");

        if (systemValue != null && !systemValue.isBlank()) {
            assertEquals(systemValue, springValue, "Spring Environment should match System Environment for APP_DB_USERNAME");
        }
    }
}

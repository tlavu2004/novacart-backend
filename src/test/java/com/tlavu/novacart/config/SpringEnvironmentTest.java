package com.tlavu.novacart.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
public class SpringEnvironmentTest {

    @Autowired
    private Environment environment;

    @Test
    void shouldReadEnvVariable() {
        System.out.println("APP_DB_USERNAME = " + environment.getProperty("APP_DB_USERNAME"));
    }
}

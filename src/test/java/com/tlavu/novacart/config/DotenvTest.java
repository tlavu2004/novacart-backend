package com.tlavu.novacart.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DotenvTest {

    @Test
    void shouldReadEnvFile() {
        Dotenv dotenv = Dotenv.load();

        String username = dotenv.get("APP_DB_USERNAME");

        assertNotNull(username, "APP_DB_USERNAME should not be null");
        assertFalse(username.isEmpty(), "APP_DB_USERNAME should not be empty");
    }
}

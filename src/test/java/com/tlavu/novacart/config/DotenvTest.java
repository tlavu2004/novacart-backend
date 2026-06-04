package com.tlavu.novacart.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

class DotenvTest {

    @Test
    void shouldReadEnvFile() {
        Dotenv dotenv = Dotenv.load();

        System.out.println("APP_DB_USERNAME = " + dotenv.get("APP_DB_USERNAME"));
    }
}

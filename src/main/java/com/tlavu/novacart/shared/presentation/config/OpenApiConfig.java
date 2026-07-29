package com.tlavu.novacart.shared.presentation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI novaCartOpenApi() {

        return new OpenAPI().info(new Info()
                .title("NovaCart API")
                .version("v1")
                .description("REST API contract for NovaCart modules."));
    }
}

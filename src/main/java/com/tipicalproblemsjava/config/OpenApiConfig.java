package com.tipicalproblemsjava.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI phoneServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CRUD-сервис телефонов")
                        .description("""
                                API для домашнего задания по troubleshooting.
                                Сервис позволяет создавать, получать, обновлять и удалять телефоны,
                                а также запускать endpoint для искусственной CPU-нагрузки.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Студент")
                                .email("student@example.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Локальный запуск приложения"));
    }
}

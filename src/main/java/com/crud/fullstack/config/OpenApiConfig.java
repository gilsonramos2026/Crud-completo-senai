package com.crud.fullstack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Define as informações gerais do seu sistema
                .info(new Info()
                        .title("Fullstack CRUD API")
                        .version("1.0.0")
                        .description("Documentação profissional da API REST para gerenciamento de usuários e produtos com autenticação JWT.")
                        .contact(new Contact()
                                .name("Suporte Técnico")
                                .email("suporte@crudapp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))

                // Adiciona a exigência global de segurança para poder testar endpoints com o Token
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // Configura o formato do botão "Authorize" para JWT do tipo Bearer
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira apenas o token JWT gerado pelo endpoint de login para se autenticar.")));
    }
}

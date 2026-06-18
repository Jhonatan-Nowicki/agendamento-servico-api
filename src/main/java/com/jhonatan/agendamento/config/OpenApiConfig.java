package com.jhonatan.agendamento.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Agendamento de Serviços API",
                version = "1.0",
                description = "API para gerenciamento de clientes, profissionais, especialidades e agendamentos",
                contact = @Contact(
                        name = "Jhonatan Nowicki"
                )
        )
)
public class OpenApiConfig {
}
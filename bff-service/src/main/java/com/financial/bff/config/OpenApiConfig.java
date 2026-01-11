package com.financial.bff.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("https://api.financiero.com")
                                .description("Servidor de Producción")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenido del endpoint /oauth/token")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Sistema Financiero - BFF API")
                .description("""
                API REST para consulta de información de clientes y sus productos financieros.
                
                ## Flujo de Autenticación
                
                1. Obtener token OAuth2 desde `/oauth/token` con client credentials
                2. Usar el token en el header `Authorization: Bearer {token}`
                3. Encriptar el código único del cliente en Base64
                4. Realizar la consulta a `/api/v1/clientes/info/{codigoEncriptado}`
                
                ## Encriptación
                
                El código único del cliente debe ser encriptado en Base64:
                - CLI001 → Q0xJMDAx
                - CLI002 → Q0xJMDAy
                - CLI003 → Q0xJMDAz
                
                ## Tracking Distribuido
                
                Todas las peticiones soportan el header `X-Trace-Id` para seguimiento distribuido.
                Si no se proporciona, se genera automáticamente.
                
                ## Códigos de Respuesta
                
                - **200 OK**: Solicitud exitosa
                - **400 Bad Request**: Código inválido o mal formado
                - **401 Unauthorized**: Token ausente o inválido
                - **404 Not Found**: Cliente no encontrado
                - **500 Internal Server Error**: Error del servidor
                - **503 Service Unavailable**: Servicio downstream no disponible
                """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipo de Desarrollo")
                        .email("desarrollo@financiero.com")
                        .url("https://www.financiero.com"))
                .license(new License()
                        .name("Uso Educativo")
                        .url("https://opensource.org/licenses/MIT"));
    }
}